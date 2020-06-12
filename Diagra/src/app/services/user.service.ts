import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Constant} from "../ constant";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {flatMap, map, switchMap} from "rxjs/operators";

export class User {
  userName: string;
  email: string;
  password: string
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private static USER_API_URL = Constant.API_URL + "/api/user";

  constructor(private httpClient: HttpClient, private authService: AuthService) {
  }

  public createUser(userName: string, email: string, password: string) {
    Promise.resolve(this.httpClient.post(
      UserService.USER_API_URL,
      JSON.stringify({
        userName: userName,
        email: email,
        password: password
      }).toString(),
      {
        headers: new HttpHeaders({
          "Content-Type": "application/json"
        })
      }
    ).toPromise()).then(data => {
      this.authService.loginUser(userName, password);
    });
  }

  public getUser(): Observable<User> {
    return this.authService.headers().pipe(
      switchMap(http => this.httpClient.get(UserService.USER_API_URL, {headers: http})),
      map(user => user as User)
    );
  }

  public deleteUser(): void {
    this.authService.headers().pipe(
      switchMap(http => this.httpClient.delete(UserService.USER_API_URL, {headers: http})),
    ).subscribe((data) => this.authService.logout());
  }

  public updateUser(user: User): Observable<User> {
    return this.authService.headers().pipe(
      switchMap(http => this.httpClient.put(UserService.USER_API_URL, JSON.stringify(user).toString(), {headers: http.set("Content-Type", "application/json")})),
      map(user => user as User),
      map(data => {
        this.authService.loginUser(user.userName, user.password);
        return data;
      })
    );
  }

}
