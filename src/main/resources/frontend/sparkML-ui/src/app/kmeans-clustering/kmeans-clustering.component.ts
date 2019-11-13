import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-kmeans-clustering',
  templateUrl: './kmeans-clustering.component.html',
  styleUrls: ['./kmeans-clustering.component.css']
})
export class KMeansClusteringComponent implements OnInit {

  kmeansFormGroup: FormGroup;
  constructor() { }

  ngOnInit() {
    this.kmeansFormGroup = new FormGroup({
      impurity: new FormControl(),
      depth: new FormControl(),
      maxBins: new FormControl(),
      training_size: new FormControl(),
      test_size: new FormControl(),
      lowK: new FormControl(),
      highK: new FormControl(),
      maxIter: new FormControl(),
      steps: new FormControl(),
      initMode: new FormControl(),
      scaleFeature: new FormControl(),
      withStd: new FormControl()
    })
  }

}
