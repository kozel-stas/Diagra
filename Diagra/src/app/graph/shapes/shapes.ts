import {mxgraph} from "mxgraph";
import {MxGraphServiceFactory} from "../MxGraphServiceFactory";

const mxCylinder: typeof mxgraph.mxCylinder = MxGraphServiceFactory.getMxGraphProperty('mxCylinder');

export class PredefinedProcess extends mxCylinder {

  protected constructor(bounds: mxgraph.mxRectangle, fill: string, stroke: string, strokewidth: number) {
    super(bounds, fill, stroke, strokewidth);
  }

  public paintVertexShape(c: mxgraph.mxSvgCanvas2D, x: number, y: number, w: number, h: number): void {
    c.begin();
    c.moveTo(x, y);
    c.lineTo(x, y + h);
    c.lineTo(x + w, y + h);
    c.lineTo(x + w, y);
    c.lineTo(x, y);
    let sizeW = w / 6;
    c.moveTo(x + sizeW, y);
    c.lineTo(x + sizeW, y + h);
    c.moveTo(x + (w - sizeW), y);
    c.lineTo(x + (w - sizeW), y + h);
    c.fillAndStroke();
    c.close(undefined, undefined, undefined, undefined, undefined, undefined);
  }


}

export class Data extends mxCylinder {

  protected constructor(bounds: mxgraph.mxRectangle, fill: string, stroke: string, strokewidth: number) {
    super(bounds, fill, stroke, strokewidth);
  }

  public paintVertexShape(c: mxgraph.mxSvgCanvas2D, x: number, y: number, w: number, h: number): void {
    c.begin();
    let size = h / Math.tan((70 * Math.PI / 180));
    c.moveTo(x + size, y);
    c.lineTo(x + w, y);
    c.lineTo(x + w - size, y + h);
    c.lineTo(x, y + h);
    c.lineTo(x + size, y);
    c.fillAndStroke();
    c.close(undefined, undefined, undefined, undefined, undefined, undefined);
  }

}

export class CycleStart extends mxCylinder {

  protected constructor(bounds: mxgraph.mxRectangle, fill: string, stroke: string, strokewidth: number) {
    super(bounds, fill, stroke, strokewidth);
  }

  public paintVertexShape(c: mxgraph.mxSvgCanvas2D, x: number, y: number, w: number, h: number): void {
    c.begin();
    let sizeW = w / 6;
    c.moveTo(x + sizeW, y);
    c.lineTo(x + w - sizeW, y);
    c.lineTo(x + w, y + sizeW);
    c.lineTo(x + w, y + h);
    c.lineTo(x, y + h);
    c.lineTo(x, y + sizeW);
    c.lineTo(x + sizeW, y);
    c.fillAndStroke();
    c.close(undefined, undefined, undefined, undefined, undefined, undefined);
  }

}

export class CycleEnd extends mxCylinder {

  protected constructor(bounds: mxgraph.mxRectangle, fill: string, stroke: string, strokewidth: number) {
    super(bounds, fill, stroke, strokewidth);
  }

  public paintVertexShape(c: mxgraph.mxSvgCanvas2D, x: number, y: number, w: number, h: number): void {
    c.begin();
    let sizeW = w / 6;
    c.moveTo(x, y);
    c.lineTo(x + w, y);
    c.lineTo(x + w, y + h - sizeW);
    c.lineTo(x + w - sizeW, y + h);
    c.lineTo(x + sizeW, y + h);
    c.lineTo(x, y + h - sizeW);
    c.lineTo(x, y);
    c.fillAndStroke();
    c.close(undefined, undefined, undefined, undefined, undefined, undefined);
  }

}

export class Comment extends mxCylinder {

  protected constructor(bounds: mxgraph.mxRectangle, fill: string, stroke: string, strokewidth: number) {
    super(bounds, fill, stroke, strokewidth);
  }

  public paintVertexShape(c: mxgraph.mxSvgCanvas2D, x: number, y: number, w: number, h: number): void {
    c.begin();
    let sizeW = w / 10;
    c.moveTo(x, y);
    c.lineTo(x, y + h);
    c.lineTo(x + sizeW, y + h);
    c.moveTo(x, y);
    c.lineTo(x + sizeW, y);
    c.stroke();
    c.close(undefined, undefined, undefined, undefined, undefined, undefined);
  }

}

export class Terminator extends mxCylinder {

  protected constructor(bounds: mxgraph.mxRectangle, fill: string, stroke: string, strokewidth: number) {
    super(bounds, fill, stroke, strokewidth);
  }

  public paintVertexShape(c: mxgraph.mxSvgCanvas2D, x: number, y: number, w: number, h: number): void {
    c.begin();
    c.moveTo(x + w / 6, y);
    c.lineTo(x + w / 6 * 5, y);
    c.quadTo(x + w, y + h / 2, x + w / 6 * 5, y + h);
    c.lineTo(x  + w / 6, y + h);
    c.quadTo(x, y + h / 2, x + w / 6, y);
    c.fillAndStroke();
    c.close(undefined, undefined, undefined, undefined, undefined, undefined);
  }

}
