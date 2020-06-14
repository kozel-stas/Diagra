import {Component, OnInit} from '@angular/core';
import {DocumentsComponent} from "../documents.component";
import {Document} from "../../services/document.service";

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit {

  document: Document;
  selected = false;
  parent: DocumentsComponent = null;

  constructor() {
  }

  ngOnInit(): void {
  }

  select(): void {
    this.selected = this.parent.select(this);
  }

  unselect() {
    this.selected = false;
  }

}
