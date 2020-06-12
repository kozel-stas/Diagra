import {Component, ComponentFactoryResolver, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {DocumentComponent} from "./document/document.component";

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

  constructor(private CFR: ComponentFactoryResolver) {
  }

  ngOnInit(): void {
    var a = () => {
      let componentFactory = this.CFR.resolveComponentFactory(DocumentComponent);
      let childComponentRef = this.VCR.createComponent(componentFactory);

      let childComponent = childComponentRef.instance;

      childComponent.id = "1";
      childComponent.description = "AAA";
      childComponent.name = "LOX";
      childComponent.parent = this;

      this.list.push(childComponent);
      console.log(childComponent);
      console.log(this.VCR.length)
      setTimeout(a, 3000);
    };
    setTimeout(a, 3000);

    // add reference for newly created component
    // this.componentsReferences.push(childComponentRef);
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

}
