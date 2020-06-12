import {Component, OnInit} from '@angular/core';
import {GraphComponent} from "../graph/graph.component";
import {Code, CodeComponent} from "../code/code.component";
import {Constant} from "../ constant";
import {EventListener} from "../services/EventMgr";
import {IERequest, IeService, Link} from "../services/ie.service";
import {throwError} from "rxjs";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-union',
  templateUrl: './union.component.html',
  styleUrls: ['./union.component.scss']
})
export class UnionComponent implements OnInit {

  private id;

  constructor(private ie: IeService) {
  }

  ngOnInit(): void {
    Constant.EVENT_MGR.subscribe("code_ready", <EventListener>{
      fireEvent: (obj) => {
        this.ie.generateLink(new File([obj.code], "code.txt", {type: 'text/plain'})).subscribe(link => {
          if (link.link) {
            this.ie.transform(new IERequest("JAVA_CODE", "XML", link.link)).subscribe(ieR => {
              if (ieR.format === "XML" && ieR.transitionLink) {
                this.ie.resolve(new Link(ieR.transitionLink)).subscribe(link => {
                  let reader = new FileReader();
                  reader.onload = function () {
                    Constant.EVENT_MGR.fireEvent("graph_update", {xml: reader.result.toString()})
                  };
                  reader.readAsText(link);
                });
              } else {
                console.error(ieR);
              }
            });
          } else {
            console.error(link);
          }
        })
      }
    })
  }

}
