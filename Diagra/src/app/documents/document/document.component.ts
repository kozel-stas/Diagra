import {Component, OnInit} from '@angular/core';
import {DocumentsComponent} from "../documents.component";

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit {

  id = null;
  name = null;
  description = null;
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
