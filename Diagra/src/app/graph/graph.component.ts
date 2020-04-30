import {Component, Injectable, OnInit} from '@angular/core';
import {MxGraphServiceFactory} from "./MxGraphServiceFactory";
import {mxgraph} from "ts-mxgraph";
import {Constant} from "../ constant";
import {EventListener} from "../services/EventMgr";

@Component({
  selector: 'app-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.scss']
})
@Injectable({
  providedIn: 'root'
})
export class GraphComponent implements OnInit {

  private static mxGraph: typeof mxgraph.mxGraph = MxGraphServiceFactory.getMxGraphProperty('mxGraph');
  private static mxImage: typeof mxgraph.mxImage = MxGraphServiceFactory.getMxGraphProperty('mxImage');
  private static mxKeyHandler: typeof mxgraph.mxKeyHandler = MxGraphServiceFactory.getMxGraphProperty("mxKeyHandler");
  private static mxToolbar: typeof mxgraph.mxToolbar = MxGraphServiceFactory.getMxGraphProperty("mxToolbar");
  private static mxRubberband: typeof mxgraph.mxRubberband = MxGraphServiceFactory.getMxGraphProperty("mxRubberband");
  private static mxGeometry: typeof mxgraph.mxGeometry = MxGraphServiceFactory.getMxGraphProperty("mxGeometry");
  private static mxCell: typeof mxgraph.mxCell = MxGraphServiceFactory.getMxGraphProperty("mxCell");
  private static mxUndoManager: typeof mxgraph.mxUndoManager = MxGraphServiceFactory.getMxGraphProperty("mxUndoManager");
  private static mxCodec: typeof mxgraph.mxCodec = MxGraphServiceFactory.getMxGraphProperty("mxCodec");
  private static mxHierarchicalLayout: typeof mxgraph.mxHierarchicalLayout = MxGraphServiceFactory.getMxGraphProperty("mxHierarchicalLayout");

  graph: mxgraph.mxGraph;

  constructor() {

  }

  ngOnInit(): void {
    let container = document.getElementById("graphContainer");
    let toolbarContainer = document.getElementById("toolbarContainer");
    if (container && toolbarContainer) {
      this.graph = new GraphComponent.mxGraph(container);
      this.initEdit(this.graph);
      this.initStyles();
      new GraphComponent.mxRubberband(this.graph);
      this.initToolBar(new GraphComponent.mxToolbar(toolbarContainer));
      this.initUndoManager();
      Constant.EVENT_MGR.subscribe("graph_update", <EventListener>{
        fireEvent: (obj) => {
          this.fromXml(obj.xml);
        }
      });
    }
  }

  private initEdit(graph) {
    MxGraphServiceFactory.getMxGraphProperty("mxConnectionHandler").prototype.connectImage = new GraphComponent.mxImage('/assets/connector.gif', 16, 16)
    graph.dropEnabled = true;
    graph.setConnectable(true);
    graph.setMultigraph(false);
    graph.setAllowDanglingEdges(false);
    MxGraphServiceFactory.getMxGraphProperty("mxDragSource").prototype.getDropTarget = function (graph, x, y) {
      let cell = graph.getCellAt(x, y);
      if (!graph.isValidDropTarget(cell)) {
        return null;
      }
      return cell;
    };
    // Delete binder
    let keyHandler = new GraphComponent.mxKeyHandler(graph);
    keyHandler.bindKey(46, function (evt) {
      if (graph.isEnabled()) {
        graph.removeCells();
      }
    });
    // COPY key CTRL+C - only selected elements
    keyHandler.bindControlKey(67, function (evt) {
      if (graph.isEnabled()) {
        MxGraphServiceFactory.getMxGraphProperty("mxClipboard").copy(graph);
      }
    });
    // PASTE key CTRL+V
    keyHandler.bindControlKey(86, function (evt) {
      if (graph.isEnabled()) {
        MxGraphServiceFactory.getMxGraphProperty("mxClipboard").paste(graph);
      }
    });
  }

  private initToolBar(toolbar) {
    GraphComponent.toolbarTemplate(
      this.graph,
      toolbar,
      GraphComponent.createVertexTemplate(100, 40, ''),
      "/assets/rectangle.gif"
    );
    GraphComponent.toolbarTemplate(
      this.graph,
      toolbar,
      GraphComponent.createVertexTemplate(100, 40, 'text;html=1;strokeColor=none;fillColor=none;align=center;verticalAlign=middle;whiteSpace=wrap;rounded=0;', (vertex) => {
        vertex.setConnectable(false);
      }),
      null
    );
    GraphComponent.toolbarTemplate(
      this.graph,
      toolbar,
      GraphComponent.createVertexTemplate(40, 40, 'shape=ellipse'),
      "/assets/ellipse.gif"
    );
    GraphComponent.toolbarTemplate(
      this.graph,
      toolbar,
      GraphComponent.createVertexTemplate(40, 40, 'shape=rhombus'),
      "/assets/rhombus.gif",
      "Decision"
    );
  }

  private initUndoManager() {
    let undoManager = new GraphComponent.mxUndoManager(1000);
    let undoListener = function (sender, evt) {
      undoManager.undoableEditHappened(evt.getProperty('edit'));
    };
    // Installs the command history
    let listener = MxGraphServiceFactory.getMxGraphProperty("mxUtils").bind(this, function (sender, evt) {
      undoListener.apply(this, arguments);
    });
    this.graph.getModel().addListener(MxGraphServiceFactory.getMxGraphProperty("mxEvent").UNDO, listener);
    this.graph.getView().addListener(MxGraphServiceFactory.getMxGraphProperty("mxEvent").UNDO, listener);

    let graph = this.graph;
    let undoHandler = function (sender, evt) {
      let cand = graph.getSelectionCellsForChanges(evt.getProperty('edit').changes);
      if (cand.length > 0) {
        let cells = [];
        for (let i = 0; i < cand.length; i++) {
          if (graph.view.getState(cand[i]) != null) {
            cells.push(cand[i]);
          }
        }
        graph.setSelectionCells(cells);
      }
    };
    undoManager.addListener(MxGraphServiceFactory.getMxGraphProperty("mxEvent").UNDO, undoHandler);
    undoManager.addListener(MxGraphServiceFactory.getMxGraphProperty("mxEvent").REDO, undoHandler);

    document.addEventListener('keydown', function (event) {
      if (event.ctrlKey && event.key === 'z') {
        undoManager.undo();
      }
    });
  }

  private initStyles() {
    MxGraphServiceFactory.getMxGraphProperty('mxConstants').HANDLE_FILLCOLOR = '#99ccff';
    MxGraphServiceFactory.getMxGraphProperty('mxConstants').HANDLE_STROKECOLOR = '#0088cf';
    MxGraphServiceFactory.getMxGraphProperty('mxConstants').VERTEX_SELECTION_COLOR = '#00a8ff';
  }

  private static createVertexTemplate(w, h, style, fabric?: any) {
    let vertex = new GraphComponent.mxCell(null, new GraphComponent.mxGeometry(0, 0, w, h), style);
    vertex.setVertex(true);
    if (fabric) {
      fabric(vertex);
    }
    return vertex;
  }

  private static toolbarTemplate(graph, toolbar, prototype, image, title?: string) {
    let drug = function (graph, evt, cell) {
      graph.stopEditing(false);
      let pt = graph.getPointForEvent(evt);
      let vertex = graph.getModel().cloneCell(prototype);
      vertex.geometry.x = pt.x;
      vertex.geometry.y = pt.y;
      graph.setSelectionCells(graph.importCells([vertex], 0, 0, cell));
    };
    let img = toolbar.addMode(title, image, drug);
    MxGraphServiceFactory.getMxGraphProperty('mxUtils').makeDraggable(img, graph, drug);
  }

  private fromXml(xml: string): void {
    console.log(this);
    console.log(this.graph)
    this.graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()))
    let xmlDom = MxGraphServiceFactory.getMxGraphProperty("mxUtils").parseXml(xml);
    let codec = new GraphComponent.mxCodec(xmlDom);
    codec.decode(xmlDom.documentElement, this.graph.getModel());
    let layout = new GraphComponent.mxHierarchicalLayout(this.graph);
    layout.execute(this.graph.getDefaultParent());
  }

}
