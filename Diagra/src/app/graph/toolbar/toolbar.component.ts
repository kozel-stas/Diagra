import {Component, OnInit} from '@angular/core';
import {DocumentService} from "../../services/document.service";
import {IeService} from "../../services/ie.service";
import {MxGraphServiceFactory} from "../MxGraphServiceFactory";
import {mxgraph} from "ts-mxgraph";
import {Constant} from "../../ constant";
import {AuthService} from "../../services/auth.service";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent implements OnInit {

  private static mxCodec: typeof mxgraph.mxCodec = MxGraphServiceFactory.getMxGraphProperty("mxCodec");

  importBar = false;
  exportBar = false;

  constructor(private ds: DocumentService, private ie: IeService, private auth: AuthService, private http: HttpClient) {
  }

  ngOnInit(): void {
  }

  public save() {
    if (this.ds.resolveTransfer()) {
      if (this.ds.resolveTransfer().id) {
        var graph = MxGraphServiceFactory.getGraph();
        var encoder = new ToolbarComponent.mxCodec();
        var result = encoder.encode(graph.getModel());
        var xml = MxGraphServiceFactory.getMxGraphProperty('mxUtils').getXml(result);
        this.ie.generateLink(new File([xml], "code.xml", {type: 'text/plain'})).subscribe(link => {
          this.ds.resolveTransfer().type = "XML";
          this.ds.resolveTransfer().transitionLink = link.link;
          this.ds.updateDocument(this.ds.resolveTransfer()).subscribe(doc => {
            this.ds.transferDoc(doc);
          })
        });
      } else {
        var graph = MxGraphServiceFactory.getGraph();
        var encoder = new ToolbarComponent.mxCodec();
        var result = encoder.encode(graph.getModel());
        var xml = MxGraphServiceFactory.getMxGraphProperty('mxUtils').getXml(result);
        this.ie.generateLink(new File([xml], "code.xml", {type: 'text/plain'})).subscribe(link => {
          this.ds.resolveTransfer().type = "XML";
          this.ds.resolveTransfer().transitionLink = link.link;
          this.ds.createDocument(this.ds.resolveTransfer()).subscribe(doc => {
            this.ds.transferDoc(doc);
          })
        });
      }
    }
  }

  public toPng(): void {
    var mySVG = document.getElementById("graphContainer").querySelector("svg");
    var svgAsXML = new XMLSerializer().serializeToString(mySVG);
    console.log(MxGraphServiceFactory.getGraph().getGraphBounds().x);
    console.log(MxGraphServiceFactory.getGraph().getGraphBounds().y);
    this.auth.headers().pipe().subscribe(headers => {
      this.http.post(Constant.API_URL + "/api/export", JSON.stringify({
        "data": svgAsXML,
        "width": Math.round(MxGraphServiceFactory.getGraph().getGraphBounds().width),
        "height": Math.round(MxGraphServiceFactory.getGraph().getGraphBounds().height),
        "x": Math.round(MxGraphServiceFactory.getGraph().getGraphBounds().x),
        "y": Math.round(MxGraphServiceFactory.getGraph().getGraphBounds().y)
      }), {headers: headers.set("Content-Type", "application/json"), responseType: "arraybuffer"}).subscribe(data => {
        var blob = new Blob([data], {type: 'image/png'});
        var url = window.URL.createObjectURL(blob);
        let a: any = document.createElement('a');
        a.href = url;
        a.download = this.ds.resolveTransfer() ? this.ds.resolveTransfer().name : "graph" + ".png";
        document.body.appendChild(a);
        a.style = 'display: none';
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
      })
    })
  }

}
