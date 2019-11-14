import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

@Component({
  selector: 'app-feature-dialogue',
  templateUrl: './feature-dialogue.component.html',
  styleUrls: ['./feature-dialogue.component.css']
})
export class FeatureDialogueComponent implements OnInit {


  featureDetails: FormGroup;
  constructor(    public dialogRef: MatDialogRef<FeatureDialogueComponent>,
                  private formBuilder: FormBuilder,
                  @Inject(MAT_DIALOG_DATA) public data: any)  {

  }

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnInit() {
    this.featureDetails = this.data;
    // this.featureDetails = this.formBuilder.group({
    //   filePath: ['', Validators.required],
    //   labelCol: ['', Validators.required],
    //   colWithStrings: ['', Validators.required],
    // });
  }

  get f() { return this.featureDetails.controls; }


}
