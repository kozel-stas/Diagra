import {Component, OnInit} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {SignUpComponent} from "../sign-up/sign-up.component";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(
    private dialog: MatDialog,
    private auth: AuthService
  ) {
  }

  ngOnInit(): void {
  }

  onSignUpBtnClick() {
    if (!this.auth.isAuthorized()) {
      const dialogRef = this.dialog.open(SignUpComponent);
    }
  }

}
