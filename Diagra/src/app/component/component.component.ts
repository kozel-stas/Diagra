import {Component, OnInit} from '@angular/core';
import {mxgraph, mxgraphFactory} from "ts-mxgraph";

@Component({
  selector: 'app-component',
  templateUrl: './component.component.html',
  styleUrls: ['./component.component.scss']
})
export class ComponentComponent implements OnInit {

  constructor() {
  }


  ngOnInit() {
    const {mxGraph, mxGraphModel} = mxgraphFactory({
      mxLoadResources: false,
      mxLoadStylesheets: false,
    });
    // Note - All mxGraph methods accessible using mx.xyz
    // Eg. mx.mxGraph, mx.mxClient, mx.mxKeyHandler, mx.mxUtils and so on.

    // Create graph

    var container = document.getElementById('graphContainer');
    if (container) {
      const model: mxgraph.mxGraphModel = new mxGraphModel();
      const graph: mxgraph.mxGraph = new mxGraph(container, model);

      // Gets the default parent for inserting new cells. This
      // is normally the first child of the root (ie. layer 0).
      var parent = graph.getDefaultParent();

      // Adds cells to the model in a single step
      graph.getModel().beginUpdate();
      try {
        var v1 = graph.insertVertex(parent, null,
          'Hello,', 20, 20, 80, 30);
        var v2 = graph.insertVertex(parent, null,
          'World!', 200, 150, 80, 30);
        var e1 = graph.insertEdge(parent, null, '', v1, v2);
      } finally {
        // Updates the display
        graph.getModel().endUpdate();
      }
    }

    // You can try demo code given in official doc with above changes.
  }

}
