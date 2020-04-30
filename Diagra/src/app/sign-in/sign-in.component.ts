import {Component, EventEmitter, OnInit, Output, ViewEncapsulation} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {Constant} from "../ constant";
import {EventListener} from "../services/EventMgr";
import {UserService} from "../services/user.service";

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SignInComponent implements OnInit {

  form: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  username: String;
  password: String;

  submit() {
    if (this.form.valid) {
      this.authService.loginUser(this.username, this.password);
    }
  }

  @Output() submitEM = new EventEmitter();

  ngOnInit(): void {
  }

  constructor(public dialogRef: MatDialogRef<SignInComponent>, private authService: AuthService, private userService: UserService) {
    Constant.EVENT_MGR.subscribe("login", <EventListener>{
      fireEvent: (obj) => {
        userService.getUser().subscribe((data) => console.log(data));
        this.onClose();
      }
    })
  }

  onClose() {
    this.dialogRef.close();
  }

}
