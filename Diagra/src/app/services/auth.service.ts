import {Constant} from "../ constant";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {of} from "rxjs";
import {map} from "rxjs/operators";

export class AuthData {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  date: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private static AUTH_URL: string = "/oauth/token";

  private authorized: boolean = false;
  private authData: AuthData = null;

  constructor(private httpClient: HttpClient) {
    this.ngOnInit();
  }

  private ngOnInit(): void {
    let token = localStorage.getItem(Constant.REFRESH_TOKEN);
    if (token && token !== "null") {
      this.authData = new AuthData();
      this.authData.refresh_token = token;
      this.authorizeByRefreshToken()
    } else {
      this.logout();
    }
  }

  private authorizeByRefreshToken() {
    if (this.authData && this.authData.refresh_token) {
      let body = new URLSearchParams();
      body.set('grant_type', "refresh_token");
      body.set('refresh_token', this.authData.refresh_token);
      this.httpClient.post(
        Constant.API_URL + AuthService.AUTH_URL,
        body.toString(),
        {
          headers: new HttpHeaders({
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": Constant.AUTH_HEADER
          })
        }
      )
        .toPromise()
        .then((resp) => resp as AuthData)
        .then((authData) => {
          this.authData = authData;
          localStorage.setItem(Constant.REFRESH_TOKEN, authData.refresh_token);
          authData.date = new Date().getTime();
          this.authorized = true;
        }).catch(e => {
        this.logout()
      });
    } else {
      this.logout()
    }
  }

  public headers(): Observable<HttpHeaders> {
    if (new Date().getTime() - 5000 > this.authData.date + this.authData.expires_in * 1000) {
      if (this.authData && this.authData.refresh_token) {
        let body = new URLSearchParams();
        body.set('grant_type', "refresh_token");
        body.set('refresh_token', this.authData.refresh_token);
        let observable = this.httpClient.post(
          Constant.API_URL + AuthService.AUTH_URL,
          body.toString(),
          {
            headers: new HttpHeaders({
              "Content-Type": "application/x-www-form-urlencoded",
              "Authorization": Constant.AUTH_HEADER
            })
          }
        )
        observable.subscribe((data) => {

        });
        return observable.pipe(
          map(data => data as AuthData),
          map(auth => {
            this.authData = auth;
            localStorage.setItem(Constant.REFRESH_TOKEN, auth.refresh_token);
            auth.date = new Date().getTime();
            return auth;
          }),
          map(auth => new HttpHeaders({
            "Authorization": "Bearer " + auth.access_token
          }))
        );
      } else {
        this.logout()
      }
    } else {
      return of(new HttpHeaders({
        "Authorization": "Bearer " + this.authData.access_token
      }));
    }
  }

  private authorize() {
    if (this.authData && this.authData.access_token && this.authData.date && this.authData.expires_in) {
      if (new Date().getTime() - 5000 > this.authData.date + this.authData.expires_in * 1000) {
        this.authorizeByRefreshToken();
      } else {
        this.login()
      }
    } else {
      this.authorizeByRefreshToken();
    }
  }

  public loginUser(username, password) {
    let body = new URLSearchParams();
    body.set('password', password);
    body.set('username', username);
    body.set('grant_type', "password");
    this.httpClient.post(
      Constant.API_URL + AuthService.AUTH_URL,
      body.toString(),
      {
        headers: new HttpHeaders({
          "Content-Type": "application/x-www-form-urlencoded",
          "Authorization": Constant.AUTH_HEADER
        })
      }
    )
      .toPromise()
      .then((resp) => resp as AuthData)
      .then((authData) => {
        this.authData = authData;
        localStorage.setItem(Constant.REFRESH_TOKEN, authData.refresh_token);
        authData.date = new Date().getTime();
        this.login();
      }).catch(e => {
      console.error(e);
      this.logout()
    });
  }

  public logout() {
    this.authorized = false;
    localStorage.setItem(Constant.REFRESH_TOKEN, null);
    Constant.EVENT_MGR.fireEvent("logout", {})
  }

  public login() {
    this.authorized = true;
    Constant.EVENT_MGR.fireEvent("login", {})
  }

  public isAuthorized() {
    return this.authorized;
  }

}
