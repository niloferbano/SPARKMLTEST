import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-save-model-dialogue',
  templateUrl: './save-model-dialogue.component.html',
  styleUrls: ['./save-model-dialogue.component.css']
})
export class SaveModelDialogueComponent implements OnInit {

  saveModelDetail: FormGroup;

  constructor( public dialogRef: MatDialogRef<SaveModelDialogueComponent>,
               private formBuilder: FormBuilder,
               @Inject(MAT_DIALOG_DATA) public data: any) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnInit() {
    this.saveModelDetail = this.data;
    // this.saveModelDetail = this.formBuilder.group({
    //   filePath: ['', Validators.required],
    //   modelName: ['', Validators.required],
    // });
  }

  get f() { return this.saveModelDetail.controls; }
}
