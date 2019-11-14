import {Component, OnInit} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {CdkDragDrop, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {FeatureDialogueComponent} from "../feature-dialogue/feature-dialogue.component";
import {FeatureTextDialogueComponent} from "../feature-text-dialogue/feature-text-dialogue.component";
import {Form, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SaveModelDialogueComponent} from "../save-model-dialogue/save-model-dialogue.component";
import {DecisionTreeComponent} from "../decision-tree/decision-tree.component";
import {CodeGenerationService} from "../services/code-generation.service";
import {first} from "rxjs/operators";
import {StartJobComponent} from "../start-job/start-job.component";
import {MatSnackBar} from '@angular/material/snack-bar';
import {KMeansClusteringComponent} from "../kmeans-clustering/kmeans-clustering.component";


@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  constructor(private dialog: MatDialog,
              private services: CodeGenerationService,
              private formBuilder: FormBuilder,
              private snackBar: MatSnackBar) {
  }

  isNotValid: Boolean = false;

  sourceFileDetail: FormGroup;
  saveModelDetail: FormGroup;
  sourceFileTextDetail: FormGroup;
  jobName: FormControl;
  trainModel: FormGroup;
  paramData: FormGroup;
  kMeansForm: FormGroup;
  jsonData: any = {};

  functions = [
    'Start',
    'FeatureExtraction',
    'FeatureExtractionFromTextFile',
    'Decision Tree',
    'KMEANS',
    "Collaborative Filtering",
    "Save Model"
  ];

  done = [];

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex);
    }
    this.isNotValid = false;

  }

  openFeatureExtraction(): void {
    const dialogRef = this.dialog.open(FeatureDialogueComponent, {
      width: '400px',
      data: this.sourceFileDetail
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);

      this.sourceFileDetail = res;
      let str:String = this.sourceFileDetail.controls['colWithStrings'].value;
      let arr =str.split(",");
      this.jsonData["featureExtraction"] = {
        filePath: this.sourceFileDetail.controls['filePath'].value,
        labelCol: this.sourceFileDetail.controls['labelCol'].value,
        colWithStrings: arr
      };
      console.log(JSON.stringify(this.jsonData));


    });
  }

  openFeatureExtractionText() {

    const dialogRef = this.dialog.open(FeatureTextDialogueComponent, {
      width: '400px',
      data: {sourceFileTextDetail: this.sourceFileTextDetail}
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
    });

  }

  openSaveModel() {
    const dialogRef = this.dialog.open(SaveModelDialogueComponent, {
      width: '400px',
      data: this.saveModelDetail
    });

    dialogRef.afterClosed().subscribe(res => {
      this.saveModelDetail = res;
      // this.jsonData['saveModel'] = {
      //   filePath: this.saveModelDetail.controls['filePath'].value,
      //   modelName: this.saveModelDetail.controls['modelName'].value
      // };
      // console.log(JSON.stringify(this.jsonData));
    });
  }

  openDecisionTree() {
    const dialogRef = this.dialog.open(DecisionTreeComponent, {
      width: '400px',
      data: {saveModelDetail: this.saveModelDetail}
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
      this.trainModel = res;

      this.jsonData['trainModel'] = {
        impurity: this.trainModel.controls['impurity'].value,
        depth: this.trainModel.controls['depth'].value,
        maxBins: this.trainModel.controls['maxBins'].value,
        training_size: this.trainModel.controls['training_size'].value,
        test_size: this.trainModel.controls['test_size'].value
      };
      console.log(JSON.stringify(this.jsonData));
    });
  }
  openKMeans() {
    const dialogRef = this.dialog.open(KMeansClusteringComponent, {
      width: '400px',
      data: this.kMeansForm
    });

    dialogRef.afterClosed().subscribe(res => {
      this.kMeansForm = res;
      this.trainModel = this.kMeansForm;
      this.jsonData['trainModel'] = this.kMeansForm.value;
      console.log(JSON.stringify(this.jsonData));
    });
  }

  openStartJob() {
    const dialogRef = this.dialog.open(StartJobComponent, {
      width: '400px',
      data: {jobName: this.jobName}
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
      this.jobName = res;
    })
  }


  GenerateJarFile() {
    if (this.done.length > 4 ||
      (this.done.indexOf("FeatureExtraction") != -1 && this.done.indexOf("FeatureExtractionFromTextFile") != -1) ||
      (this.done.indexOf("Collaborative Filterin") != - 1 && this.done.indexOf("FeatureExtraction") != -1) ||
      (this.done.indexOf("Decision Tree") != -1 && this.done.indexOf("FeatureExtractionFromTextFile") != -1) ||
      (this.done.indexOf("KMEANS") != -1 && this.done.indexOf("FeatureExtractionFromTextFile") != -1)
    ) {
      this.isNotValid = true;
    } else {
      this.isNotValid = false;

      this.paramData = this.formBuilder.group({
        jobName: this.jobName,
        featureExtraction: this.sourceFileDetail,
        trainModel: this.trainModel,
        saveModel: this.saveModelDetail
      });
      if (this.done.indexOf("Decision Tree") != -1) {
        this.services.generateDecisionTree(JSON.stringify(this.paramData.value))
          .pipe(first())
          .subscribe(
            data => {
              this.snackBar.open(data["message"], "success", {
                duration: 10000
              })
            },
            error => {
              console.log(error);
              this.snackBar.open(error["message"], "warning", {
                duration: 10000
              })
            }
          );
      }
      if (this.done.indexOf("KMEANS") != -1) {
        this.services.generateKMeans(JSON.stringify(this.paramData.value))
          .pipe(first())
          .subscribe(
            data => {
              this.snackBar.open(data["message"], "success", {
                duration: 10000
              })
            },
            error => {
              console.log(error);
              this.snackBar.open(error["message"], "warning", {
                duration: 10000
              })
            }
          );
      }
      if (this.done.indexOf("Collaborative Filtering") != -1) {
        this.services.generateCollaborativeFiltering(JSON.stringify(this.paramData.value))
          .pipe(first())
          .subscribe(
            data => {
              this.snackBar.open(data["message"], "success", {
                duration: 10000
              })
            },
            error => {
              console.log(error);
              this.snackBar.open(error["message"], "warning", {
                duration: 10000
              })
            }
          );
      }
    }
  }

  ngOnInit() {
    this.jobName = new FormControl('_newJob', Validators.required);
    this.sourceFileDetail = this.formBuilder.group({
      filePath: ['', Validators.required],
      labelCol: ['', Validators.required],
      colWithStrings: ['', Validators.required]
    });

    this.kMeansForm = this.formBuilder.group({
      lowK: new FormControl( Validators.required),
      highK: new FormControl(),
      maxIter: new FormControl(),
      steps: new FormControl(),
      initMode: new FormControl('random', Validators.required),
      scaleFeature: new FormControl(false),
      withStd: new FormControl(true),
      distanceThreshold: new FormControl(0.000001, Validators.required)
    });

    this.saveModelDetail = this.formBuilder.group({
      filePath: ['', Validators.required],
      modelName: ['', Validators.required],
    });
  }

}
