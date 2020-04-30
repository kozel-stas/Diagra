import {EventMgr} from "./services/EventMgr";

export class Constant {

  public static EVENT_MGR: EventMgr = new EventMgr();

  public static API_URL = 'http://localhost:8080';
  public static REFRESH_TOKEN = 'refresh_token';
  public static AUTH_HEADER = 'Basic Y2xpZW50X2lkOmNsaWVudF9zZWNyZXQ=';

}
