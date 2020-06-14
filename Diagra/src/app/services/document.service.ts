import {Injectable} from '@angular/core';
import {AuthService} from "./auth.service";
import {IeService} from "./ie.service";
import {Observable} from "rxjs";
import {map, switchMap} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";
import {Constant} from "../ constant";

export class Document {
  name: string;
  description: string;
  transitionLink: string
  type: string;
  id: string;
}


@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private static DOCUMENT_API_URL = Constant.API_URL + "/api/document";

  constructor(private auth: AuthService, private httpClient: HttpClient) {

  }

  private doc: Document;

  public transferDoc(doc: Document): void {
    this.doc = doc;
  }

  public resolveTransfer(): Document {
    return this.doc;
  }

  public getDocuments(): Observable<Document[]> {
    return this.auth.headers().pipe(
      switchMap(http => this.httpClient.get(DocumentService.DOCUMENT_API_URL, {headers: http})),
      map(data => data as Document[])
    );
  }

  public deleteDocument(id: string): void {
    this.auth.headers().subscribe(header => {
      this.httpClient.delete(DocumentService.DOCUMENT_API_URL + "/" + id, {headers: header})
        .subscribe(log => console.log(log));
    });
  }

  public createDocument(doc: Document): Observable<Document> {
    doc.id = null;
    return this.auth.headers().pipe(
      switchMap(http => this.httpClient.post(DocumentService.DOCUMENT_API_URL, JSON.stringify(doc), {headers: http.set("Content-Type", "application/json")})),
      map(data => data as Document)
    );
  }

  public updateDocument(doc: Document): Observable<Document> {
    return this.auth.headers().pipe(
      switchMap(header => this.httpClient.delete(DocumentService.DOCUMENT_API_URL + "/" + doc.id, {headers: header})),
      switchMap(data => this.createDocument(doc))
    );
  }

}
