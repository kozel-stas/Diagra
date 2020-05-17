import {mxgraph} from "ts-mxgraph";
import {MxGraphServiceFactory} from "./MxGraphServiceFactory";

const mxHierarchicalLayout: typeof mxgraph.mxHierarchicalLayout = MxGraphServiceFactory.getMxGraphProperty("mxHierarchicalLayout");
const mxCompactTreeLayout: typeof mxgraph.mxCompactTreeLayout = MxGraphServiceFactory.getMxGraphProperty("mxCompactTreeLayout");
const mxCompositeLayout: typeof mxgraph.mxCompositeLayout = MxGraphServiceFactory.getMxGraphProperty("mxCompositeLayout");

export class Layout extends mxCompositeLayout {

  constructor(graph: any) {
    let first = new mxCompactTreeLayout(graph, false);
    let second = new mxHierarchicalLayout(graph);
    second.disableEdgeStyle = false;
    super(graph, [first, second], first);
  }

  execute(parent: any): void {
    super.execute(parent);
  }

}
