import {Injectable} from '@angular/core';
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";
import {map, switchMap} from "rxjs/operators";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Constant} from "../ constant";

export class Link {

  constructor(link) {
    this.link = link;
  }

  link: string
}

export class IERequest {

  constructor(from, to, link) {
    this.from = from;
    this.transitionLink = link;
    this.to = to;
  }

  from: string;
  to: string;
  transitionLink: string;

}

export class IEResponse {

  format: string;
  transitionLink: string;

}

@Injectable({
  providedIn: 'root'
})
export class IeService {

  private static TRANSITION_API_URL = Constant.API_URL + "/api/transition";
  private static IE_API_URL = Constant.API_URL + "/api/ie";

  constructor(private auth: AuthService, private httpClient: HttpClient) {
  }

  public generateLink(file: File): Observable<Link> {
    let form = new FormData();
    form.set("file", file);
    return this.auth.headers().pipe(
      switchMap(http => this.httpClient.post(IeService.TRANSITION_API_URL, form, {headers: http})),
      map(link => link as Link)
    );
  }

  public resolve(link: Link): Observable<File> {
    return this.auth.headers().pipe(
      switchMap(http => this.httpClient.get(Constant.API_URL + link.link, {headers: http, responseType: "blob"})),
      map(data => new File([data], link.link, {type: data.type, lastModified: new Date().getTime()}))
    );
  }

  public transform(req: IERequest): Observable<IEResponse> {
    return this.auth.headers().pipe(
      switchMap(http => {
        http = http.set("Content-Type", "application/json");
        return this.httpClient.post(IeService.IE_API_URL, JSON.stringify(req).toString(), {headers: http})
      }),
      map(link => link as IEResponse)
    );
  }


}
