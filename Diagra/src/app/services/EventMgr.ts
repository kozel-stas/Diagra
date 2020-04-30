export interface EventListener {
  fireEvent(obj): void;
}

export class EventMgr {

  private listeners: Map<string, EventListener[]> = new Map();

  public fireEvent(event: string, obj: object) {
    let o = this.listeners.get(event);
    if (o) {
      for (let i = 0; i < o.length; i++) {
        o[i].fireEvent(obj);
      }
    }
  }

  public subscribe(event: string, obj: EventListener) {
    let o = this.listeners.get(event);
    if (!o) {
      o = [];
      this.listeners.set(event, o);
    }
    o.push(obj);
  }

}
