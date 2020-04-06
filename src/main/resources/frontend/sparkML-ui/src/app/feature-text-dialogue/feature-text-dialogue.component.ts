import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormGroup, FormBuilder, Validators, FormControl, Form} from '@angular/forms';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-feature-text-dialogue',
  templateUrl: './feature-text-dialogue.component.html',
  styleUrls: ['./feature-text-dialogue.component.css']
})
export class FeatureTextDialogueComponent implements OnInit {

  sourceData: FormGroup;
  aliasFileDetail: FormGroup;
  sourceFileDetail: FormGroup;
  constructor(public dialogRef: MatDialogRef<FeatureTextDialogueComponent>,
              private formBuilder: FormBuilder,
              private httpClient:HttpClient,
              @Inject(MAT_DIALOG_DATA) public data: any) { }


  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.sourceData = this.data;
  }



  get f() { return this.sourceData.controls; }




}
