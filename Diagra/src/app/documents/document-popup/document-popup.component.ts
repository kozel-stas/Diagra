import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-document-popup',
  templateUrl: './document-popup.component.html',
  styleUrls: ['./document-popup.component.scss']
})
export class DocumentPopupComponent {

  name: string;
  description: string;
  mainForm: FormGroup;


  constructor(private dialogRef: MatDialogRef<DocumentPopupComponent>) {
    this.mainForm = new FormGroup({
      name: new FormControl('', [Validators.required]),
      description: new FormControl('', [Validators.required]),
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    if (this.mainForm.valid){
      this.dialogRef.close(true);
    }
  }

}
