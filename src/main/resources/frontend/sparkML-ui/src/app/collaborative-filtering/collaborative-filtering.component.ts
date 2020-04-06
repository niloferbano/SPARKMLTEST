import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-collaborative-filtering',
  templateUrl: './collaborative-filtering.component.html',
  styleUrls: ['./collaborative-filtering.component.css']
})
export class CollaborativeFilteringComponent implements OnInit {

  //cfFormGroup: FormGroup;

  constructor(public dialogRef: MatDialogRef<CollaborativeFilteringComponent>,
              private formBuilder: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    //this.cfFormGroup = this.data;
  }

  updatetest(event) {
    this.data.controls['testinsize'].setValue(
      (1 - this.data.controls['trainingsize'].value))
  }


  onNoClick(): void {
    this.dialogRef.close();
  }

}
