import {mxgraphFactory} from 'ts-mxgraph';

type MxGraphProperty =
  | 'mxCellRenderer'
  | 'mxClient'
  | 'mxConstants'
  | 'mxCylinder'
  | 'mxConnectionConstraint'
  | 'mxGeometry'
  | 'mxGraph'
  | 'mxCell'
  | 'mxGraphModel'
  | 'mxPerimeter'
  | 'mxPoint'
  | 'mxShape'
  | 'mxSvgCanvas2D'
  | 'mxUtils'
  | 'mxCodec'
  | 'mxImage'
  | 'mxKeyHandler'
  | 'mxDragSource'
  | 'mxOutline'
  | 'mxHierarchicalLayout'
  | 'mxClipboard'
  | 'mxUndoManager'
  | 'mxStackLayout'
  | 'mxRubberband'
  | 'mxEvent'
  | 'mxToolbar'
  | 'mxHierarchicalEdgeStyle'
  | 'mxGraphHandler'
  | 'mxCompositeLayout'
  | 'mxCompactTreeLayout'
  | 'mxConnectionHandler';

export class MxGraphServiceFactory {
  private static instance: MxGraphServiceFactory = null;

  private constructor(private readonly mxGraphLib: any) {
  }

  private static getInstance(): MxGraphServiceFactory {
    if (MxGraphServiceFactory.instance === null) {
      const mxGraphLib = mxgraphFactory({
        mxLoadResources: false,
        mxLoadStylesheets: false,
      });
      MxGraphServiceFactory.instance = new MxGraphServiceFactory(mxGraphLib);
    }
    return MxGraphServiceFactory.instance;
  }

  public static getMxGraphProperty(propertyName: MxGraphProperty): any {
    return MxGraphServiceFactory.getInstance().mxGraphLib[propertyName];
  }

}
