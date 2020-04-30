import {Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {UserService} from "../services/user.service";
import {Constant} from "../ constant";
import {EventListener} from "../services/EventMgr";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SignUpComponent implements OnInit {

  form: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  username: string;
  password: string;
  email: string;

  submit() {
    if (this.form.valid) {
      this.userService.createUser(this.username, this.email, this.password);
    }
  }

  @Input() error: string | null;

  @Output() submitEM = new EventEmitter();

  ngOnInit(): void {
  }

  constructor(public dialogRef: MatDialogRef<SignUpComponent>, private userService: UserService) {
    Constant.EVENT_MGR.subscribe("login", <EventListener>{
      fireEvent: (obj) => {
        this.onClose();
      }
    })
  }

  onClose() {
    this.dialogRef.close();
  }
}
