import {Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SignUpComponent implements OnInit {

  form: FormGroup = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
    email: new FormControl(''),
  });

  submit() {
    if (this.form.valid) {
      this.submitEM.emit(this.form.value);
    }
  }

  @Input() error: string | null;

  @Output() submitEM = new EventEmitter();

  ngOnInit(): void {
  }

  constructor(public dialogRef: MatDialogRef<SignUpComponent>) {
  }

  onClose() {
    this.dialogRef.close();
  }
}
