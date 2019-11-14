import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-start-job',
  templateUrl: './start-job.component.html',
  styleUrls: ['./start-job.component.css']
})
export class StartJobComponent implements OnInit {


  job: FormGroup;
  constructor(public dialogRef: MatDialogRef<StartJobComponent>,
              private formBuilder: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.job = this.data;
    // this.job = new FormGroup({
    //   jobName: new FormControl("_newJob")
    // });
  }
  onNoClick(): void {
    this.dialogRef.close();
  }

}
