import {Injectable, OnInit} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnInit {


  constructor() {
  }

  ngOnInit(): void {
  }

  authorized() {
    return false;
  }

}
