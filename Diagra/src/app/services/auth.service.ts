import {Constant} from "../ constant";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Injectable} from "@angular/core";

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
    if (token) {
      this.authData = new AuthData();
      this.authData.refresh_token = token;
      this.authorizeByRefreshToken()
    } else {
      this.logout();
    }
  }

  private async authorizeByRefreshToken() {
    if (this.authData && this.authData.refresh_token) {
      let body = new URLSearchParams();
      body.set('grant_type', "refresh_token");
      body.set('refresh_token', this.authData.refresh_token);
      await this.httpClient.post(
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
          console.error(e);
          this.logout()
        });
    } else {
      this.logout()
    }
  }

  private async authorize() {
    if (this.authData && this.authData.access_token && this.authData.date && this.authData.expires_in) {
      if (new Date().getTime() - 5000 > this.authData.date + this.authData.expires_in * 1000) {
        this.authorizeByRefreshToken();
      } else {
        this.authorized = true;
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
        this.authorized = true;
      }).catch(e => {
      console.error(e);
      this.logout()
    });
  }

  public logout() {
    this.authorized = false;
    localStorage.setItem(Constant.REFRESH_TOKEN, null);
  }

  public isAuthorized() {
    return this.authorized;
  }

}
