import {Component, OnInit} from '@angular/core';
import {User, UserService} from "../services/user.service";
import {Constant} from "../ constant";
import {EventListener} from "../services/EventMgr";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationComponent} from "./confirmation/confirmation.component";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  userName;
  email;
  password;
  newPassword;
  confirmPassword;
  mainForm: FormGroup;

  updatePassword = false;

  constructor(private us: UserService, private formBuilder: FormBuilder, private dialog: MatDialog) {
    this.mainForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }

  ngOnInit(): void {
    let that = this;
    Constant.EVENT_MGR.subscribe("login", <EventListener>{
      fireEvent: (obj) => {
        that.us.getUser().subscribe((data) => {
          that.userName = data.userName;
          that.email = data.email;
        });
      }
    })
    that.us.getUser().subscribe((data) => {
      that.userName = data.userName;
      that.email = data.email;
    });
  }

  save(): void {
    if (!this.mainForm.valid || (this.updatePassword && this.newPassword != this.confirmPassword)) {
      return;
    }
    let user = new User();
    user.email = this.email;
    user.userName = this.userName;
    user.password = this.password;
    user.newPassword = this.newPassword;
    this.us.updateUser(user).subscribe((data) => {
      this.userName = data.userName;
      this.email = data.email;
    });
  }

  delete(): void {
    const dialogRef = this.dialog.open(ConfirmationComponent, {
      width: '15%',
      data: "Do you want to delete your account?"
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.us.deleteUser()
      }
    });
  }

}
