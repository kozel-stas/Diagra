import {Component, ComponentFactoryResolver, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {DocumentComponent} from "./document/document.component";
import {ConfirmationComponent} from "../account/confirmation/confirmation.component";
import {MatDialog} from "@angular/material/dialog";
import {DocumentPopupComponent} from "./document-popup/document-popup.component";
import {Document, DocumentService} from "../services/document.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent implements OnInit {
  @ViewChild('viewContainerRef', {read: ViewContainerRef}) VCR: ViewContainerRef;

  editGroup = false;
  createGroup = false;

  private list: DocumentComponent[] = [];
  private selected: DocumentComponent = null;

  constructor(private CFR: ComponentFactoryResolver, private dialog: MatDialog, private ds: DocumentService,  private router: Router) {
  }

  ngOnInit(): void {
    this.ds.getDocuments().subscribe(documents => {
      if (documents && documents.length > 0) {
        for (let document of documents) {
          let componentFactory = this.CFR.resolveComponentFactory(DocumentComponent);
          let childComponentRef = this.VCR.createComponent(componentFactory);
          let childComponent = childComponentRef.instance;
          childComponent.document = document;
          childComponent.parent = this;
          this.list.push(childComponent);
        }
      }
    });
  }

  public select(doc: DocumentComponent): boolean {
    if (doc && this.list.indexOf(doc) >= 0) {
      if (this.selected) {
        this.selected.unselect();
        if (this.selected == doc) {
          this.selected = null;
          return false;
        }
      }
      this.selected = doc;
      return true;
    }
    return false;
  }

  public remove(): void {
    if (this.selected && this.selected.selected && this.list.includes(this.selected)) {
      this.VCR.remove(this.list.indexOf(this.selected));
      this.list.splice(this.list.indexOf(this.selected), 1);
      this.ds.deleteDocument(this.selected.document.id);
      this.selected = null;
    }
  }



  public new(type: string): void {
    const dialogRef = this.dialog.open(DocumentPopupComponent, {
      width: '25%',
      height: '25%',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        var doc = new Document();
        doc.name = dialogRef.componentInstance.name;
        doc.description = dialogRef.componentInstance.description;
        this.ds.transferDoc(doc);
        console.log(doc);
        switch (type) {
          case "code":
            this.router.navigate(['../union'])
            break;
          default:
            this.router.navigate(['../graph'])
            break;
        }
      }
    });
  }

  public edit(): void {
    if (this.selected){
      this.ds.transferDoc(this.selected.document);
      this.router.navigate(['../graph'])
    }
  }

}
