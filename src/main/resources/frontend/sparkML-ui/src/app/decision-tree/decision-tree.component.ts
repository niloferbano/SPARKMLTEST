import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-decision-tree',
  templateUrl: './decision-tree.component.html',
  styleUrls: ['./decision-tree.component.css']
})
export class DecisionTreeComponent implements OnInit {

  decisionTreeParamForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<DecisionTreeComponent>,
              private formBuilder: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any) { }


  ngOnInit() {
    this.decisionTreeParamForm = this.data;
    // this.decisionTreeParamForm = this.formBuilder.group({
    //   impurity: new FormControl('entropy', Validators.required),
    //   depth: new FormControl(Validators.required),
    //   maxBins: new FormControl(Validators.required),
    //   training_size: new FormControl( 0.8, Validators.required),
    //   test_size: new FormControl(0.2, Validators.required),
    // })

  }

  updatetest(event) {
    this.decisionTreeParamForm.controls['test_size'].setValue(
      (1 - this.decisionTreeParamForm.controls['training_size'].value))
  }


  onNoClick(): void {
    this.dialogRef.close();
  }

}
