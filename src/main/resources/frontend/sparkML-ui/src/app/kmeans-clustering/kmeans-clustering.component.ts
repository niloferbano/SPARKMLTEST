import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ValidateFn} from "codelyzer/walkerFactory/walkerFn";

@Component({
  selector: 'app-kmeans-clustering',
  templateUrl: './kmeans-clustering.component.html',
  styleUrls: ['./kmeans-clustering.component.css']
})
export class KMeansClusteringComponent implements OnInit {

  kmeansFormGroup: FormGroup;
  seasons: string[] = ['withStd', 'withMean'];
  constructor(public dialogRef: MatDialogRef<KMeansClusteringComponent>,
              private formBuilder: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.kmeansFormGroup = this.data;
    // this.kmeansFormGroup = new FormGroup({
    //
    //   lowK: new FormControl( Validators.required),
    //   highK: new FormControl(),
    //   maxIter: new FormControl(),
    //   steps: new FormControl(),
    //   initMode: new FormControl('random', Validators.required),
    //   scaleFeature: new FormControl(false),
    //   withStd: new FormControl(true),
    //   distanceThreshold: new FormControl(0.000001, Validators.required)
    // })
  }

  get f() { return this.kmeansFormGroup.controls; }
  onNoClick(): void {
    this.dialogRef.close();
  }

}
