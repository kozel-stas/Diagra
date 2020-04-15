import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {SignInComponent} from "../sign-in/sign-in.component";
import {SignUpComponent} from "../sign-up/sign-up.component";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-common-ui',
  templateUrl: './common-ui.component.html',
  styleUrls: ['./common-ui.component.scss'],
})
export class CommonUiComponent implements OnInit {

  authorized = false;

  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private router: Router,
    public auth: AuthService
  ) {
  }

  ngOnInit(): void {
  }

  onSignInBtnClick() {
    const dialogRef = this.dialog.open(SignInComponent);

    setTimeout(() => {
      dialogRef.afterClosed().subscribe(result => {
        this.router.navigate(['../']);
      })
    });
  }

  onSignUpBtnClick() {
    const dialogRef = this.dialog.open(SignUpComponent);

    setTimeout(() => {
      dialogRef.afterClosed().subscribe(result => {
        this.router.navigate(['../']);
      })
    });
  }

}
