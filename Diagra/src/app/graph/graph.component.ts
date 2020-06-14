import {Component, Injectable, OnInit} from '@angular/core';
import {MxGraphServiceFactory} from "./MxGraphServiceFactory";
import {mxgraph} from "ts-mxgraph";
import {Constant} from "../ constant";
import {EventListener} from "../services/EventMgr";
import {Comment, CycleEnd, CycleStart, Data, PredefinedProcess, Terminator} from "./shapes/shapes";
import {Layout} from "./layout";
import {Document, DocumentService} from "../services/document.service";
import {IeService, Link} from "../services/ie.service";

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
  private static mxConnectionConstraint: typeof mxgraph.mxConnectionConstraint = MxGraphServiceFactory.getMxGraphProperty("mxConnectionConstraint");
  private static mxPoint: typeof mxgraph.mxPoint = MxGraphServiceFactory.getMxGraphProperty("mxPoint");
  private static mxOutline: typeof mxgraph.mxOutline = MxGraphServiceFactory.getMxGraphProperty("mxOutline");

  graph: mxgraph.mxGraph;
  private doc: Document;

  constructor(private ds: DocumentService, private ie: IeService) {
  }

  ngOnInit(): void {
    let container = document.getElementById("graphContainer");
    let outline = document.getElementById("outLineContainer");
    let toolbarContainer = document.getElementById("toolbarContainer");
    if (container && toolbarContainer && outline) {
      this.graph = new GraphComponent.mxGraph(container);
      this.initEdit(this.graph);
      this.initStyles();
      new GraphComponent.mxRubberband(this.graph);
      this.initToolBar(new GraphComponent.mxToolbar(toolbarContainer));
      this.initUndoManager();
      new GraphComponent.mxOutline(this.graph, outline);
      GraphComponent.initCustomShapes();
      Constant.EVENT_MGR.subscribe("graph_update", <EventListener>{
        fireEvent: (obj) => {
          this.fromXml(obj.xml, obj.unsort);
        }
      });
      this.doc = this.ds.resolveTransfer();
      if (this.doc && this.doc.transitionLink && this.doc.id) {
        this.ie.resolve(new Link(this.doc.transitionLink)).subscribe(file => {
          let reader = new FileReader();
          reader.onload = function () {
            Constant.EVENT_MGR.fireEvent("graph_update", {xml: reader.result.toString(), unsort: false})
          };
          reader.readAsText(file);
        });
      }
    }
    MxGraphServiceFactory.setGraph(this.graph);
  }

  private initEdit(graph: mxgraph.mxGraph) {
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
    this.graph.getAllConnectionConstraints = function (terminal, source) {
      let points = [
        new GraphComponent.mxConnectionConstraint(new GraphComponent.mxPoint(0.5, 0), true),
        new GraphComponent.mxConnectionConstraint(new GraphComponent.mxPoint(0, 0.5), true),
        new GraphComponent.mxConnectionConstraint(new GraphComponent.mxPoint(1, 0.5), true),
        new GraphComponent.mxConnectionConstraint(new GraphComponent.mxPoint(0.5, 1), true)
      ]
      if (terminal != null && terminal.shape != null) {
        return points;
      }
      return null;
    };
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
    let graph = this.graph;
    toolbar.addItem('Zoom In', '/assets/zoom_in32.png', function (evt) {
      graph.zoomIn();
    });

    toolbar.addItem('Zoom Out', '/assets/zoom_out32.png', function (evt) {
      graph.zoomOut();
    });

    toolbar.addItem('Actual Size', '/assets/view_1_132.png', function (evt) {
      graph.zoomActual();
    });
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

  private static initCustomShapes() {
    MxGraphServiceFactory.getMxGraphProperty('mxCellRenderer').registerShape('predefined_process', PredefinedProcess);
    MxGraphServiceFactory.getMxGraphProperty('mxCellRenderer').registerShape('data', Data);
    MxGraphServiceFactory.getMxGraphProperty('mxCellRenderer').registerShape('cycle_start', CycleStart);
    MxGraphServiceFactory.getMxGraphProperty('mxCellRenderer').registerShape('cycle_end', CycleEnd);
    MxGraphServiceFactory.getMxGraphProperty('mxCellRenderer').registerShape('comment', Comment);
    MxGraphServiceFactory.getMxGraphProperty('mxCellRenderer').registerShape('terminator', Terminator);
  }

  private fromXml(xml: string, unsort: boolean): void {
    this.graph.getModel().beginUpdate();
    try {
      this.graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()))
    } finally {
      this.graph.getModel().endUpdate();
    }
    let xmlDom = MxGraphServiceFactory.getMxGraphProperty("mxUtils").parseXml(xml);
    let codec = new GraphComponent.mxCodec(xmlDom);
    codec.decode(xmlDom.documentElement, this.graph.getModel());
    if (!unsort) {
      this.sort();
    }
  }

  private sort() {
    let cells = [];
    for (let i = 0; i < Object.getOwnPropertyNames(this.graph.getModel().cells).length; i++) {
      let cell = this.graph.getModel().cells[i];
      if (cell.getStyle() && cell.getStyle().includes("shape=comment")) {
        cells.push(cell);
        this.graph.updateCellSize(cell, true);
      }
    }
    let edges = [];
    this.graph.getModel().beginUpdate();
    try {
      for (let cell of this.graph.getAllEdges(cells)) {
        if (cells.includes(cell.source) || cells.includes(cell.target)) {
          edges.push(cell)
          this.graph.getModel().remove(cell);
        }
      }
    } finally {
      this.graph.getModel().endUpdate();
    }
    new Layout(this.graph).execute(this.graph.getDefaultParent());
    this.graph.getModel().beginUpdate();
    try {
      for (let edge of edges) {
        if (cells.includes(edge.source) || cells.includes(edge.target)) {
          let comment = cells.includes(edge.source) ? edge.source : edge.target;
          let source = cells.includes(edge.source) ? edge.target : edge.source;
          this.graph.moveCells(
            [comment],
            (source.getGeometry().getCenterX() + (source.getGeometry().width / 2)),
            source.getGeometry().getCenterY() - comment.getGeometry().height / 2,
            false
          );
          this.graph.addEdge(edge, this.graph.getDefaultParent());
        }
      }
    } finally {
      this.graph.getModel().endUpdate();
    }

    this.graph.getModel().beginUpdate();
    try {
      for (let i = 0; i < Object.getOwnPropertyNames(this.graph.getModel().cells).length; i++) {
        let cell = this.graph.getModel().cells[i];
        if (cell.isEdge() && !(cell.target.getStyle() && cell.target.getStyle().includes("shape=comment")) && cell.source.geometry.getCenterX() != cell.target.geometry.getCenterX()) {
          this.graph.getModel().remove(cell);
          let edge = this.graph.insertEdge(cell.getParent(), null, cell.getValue(), cell.source, cell.target, null);
          edge.geometry.points = [
            new GraphComponent.mxPoint(
              cell.source.geometry.getCenterX(),
              cell.target.geometry.getCenterY() - cell.target.geometry.height / 2 - this.getSourceOffset(edge)
            ),
            new GraphComponent.mxPoint(
              cell.target.geometry.getCenterX(),
              cell.target.geometry.getCenterY() - cell.target.geometry.height / 2 - this.getSourceOffset(edge)
            )
          ];
          this.fixPoints(edge, cell);
        } else if (cell.isEdge()) {
          console.log(cell)
          this.fixPoints(cell, cell);
        }
      }
    } finally {
      this.graph.getModel().endUpdate();
    }
  }

  private fixPoints(edge: mxgraph.mxCell, cell: mxgraph.mxCell) {
    let offset = 40;
    let intersect = this.isIntersect(edge);
    while (intersect) {
      edge.geometry.points = [
        new GraphComponent.mxPoint(
          cell.source.geometry.getCenterX(),
          cell.source.geometry.getCenterY() + cell.source.geometry.height / 2 + 40
        ),
        new GraphComponent.mxPoint(
          cell.source.geometry.getCenterX() + offset,
          cell.source.geometry.getCenterY() + cell.source.geometry.height / 2 + 40
        ),
        new GraphComponent.mxPoint(
          cell.source.geometry.getCenterX() + offset,
          cell.target.geometry.getCenterY() - cell.target.geometry.height / 2 - this.getOffset(edge)
        ),
        new GraphComponent.mxPoint(
          cell.target.geometry.getCenterX(),
          cell.target.geometry.getCenterY() - cell.target.geometry.height / 2 - this.getOffset(edge)
        )
      ];
      offset += 40;
      intersect = this.isIntersect(edge);
    }
  }

  private getOffset(cell: mxgraph.mxCell): number {
    if (cell.target) {
      let offset = cell.target.edges.filter(data => data.target == cell.target).indexOf(cell);
      if (40 - 5 * offset < 5) {
        return 40;
      } else {
        return 40 - 5 * offset;
      }
    }
    return 40;
  }

  private getSourceOffset(cell: mxgraph.mxCell): number {
    if (cell.source) {
      let offset = cell.source.edges.filter(data => data.source == cell.source).indexOf(cell);
      if (40 - 5 * offset < 5) {
        return 40;
      } else {
        return 40 - 5 * offset;
      }
    }
    return 40;
  }

  private isIntersect(edge: mxgraph.mxCell): mxgraph.mxCell {
    for (let i = 0; i < Object.getOwnPropertyNames(this.graph.getModel().cells).length; i++) {
      let cell = this.graph.getModel().cells[i];
      if (cell && cell.isVertex()) {
        let points = edge.geometry.points ? [edge.source.geometry, ...edge.geometry.points, edge.target.geometry] : [edge.source.geometry, edge.target.geometry];
        let previous = null;
        for (let point of points) {
          if ((point.x < cell.geometry.x + cell.geometry.width && point.x > cell.geometry.x)
            && previous && previous.y < cell.geometry.y && point.y >= cell.geometry.y) {
            return cell;
          }
          previous = point;
        }
      }
    }
    return null;
  }

}
