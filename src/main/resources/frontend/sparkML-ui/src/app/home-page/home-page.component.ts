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
import {CollaborativeFilteringComponent} from "../collaborative-filtering/collaborative-filtering.component";
import {MapType} from "@angular/compiler";
import {DecimalPipe} from "@angular/common";


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
  featureExtraction: FormGroup;
  jobName: FormControl;
  trainModel: FormGroup;
  paramData: FormGroup;
  kMeansForm: FormGroup;
  cfForm: FormGroup;
  decisionTreeForm: FormGroup;
  jsonData: any = {};
  isloading: Boolean = false;

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
    if(this.sourceFileDetail === undefined) {
      this.sourceFileDetail = this.formBuilder.group({
        filePath: ['', Validators.required],
        labelCol: ['', Validators.required],
        colWithStrings: ['', Validators.required]
      });
    }
    const dialogRef = this.dialog.open(FeatureDialogueComponent, {
      width: '400px',
      data: this.sourceFileDetail
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
      this.sourceFileDetail = res;
      let str:String = this.sourceFileDetail.controls['colWithStrings'].value;
      let arr =str.split(",");
      this.sourceFileDetail.controls['colWithStrings'].setValue(arr);
      this.featureExtraction = this.sourceFileDetail;
    });
  }

  openFeatureExtractionText() {
    if(this.sourceFileTextDetail === undefined) {
      this.sourceFileTextDetail = this.formBuilder.group({
        sourceFilePath: this.formBuilder.group({
          filePath: new FormControl(),
          separator: new FormControl(),
          sourceDetail: this.formBuilder.group({
            orderOfSourceColumns: new FormControl(),
            itemColName: new FormControl(),
            ratingColName: new FormControl(),
            userIdColName: new FormControl(),
            ratingColType: new FormControl()
          })
        }),

        aliasFilePath: this.formBuilder.group({
          filePath: new FormControl(),
          separator: new FormControl(),
          orderOfSourceColumns: new FormControl(),
        })


      });
    }
    const dialogRef = this.dialog.open(FeatureTextDialogueComponent, {
      width: '400px',
      data: this.sourceFileTextDetail
    });

    dialogRef.afterClosed().subscribe(res => {
      this.sourceFileTextDetail = res;
      this.featureExtraction = res;
      let str:string = this.featureExtraction.controls['sourceFilePath'].value['sourceDetail']['ratingColType'];
      let arr = str.split(':');
      let json = {};
      str = arr[0];
      if(arr.length == 2) {
        json[arr[0]] = arr[1];
        let ratingColType: FormGroup = this.formBuilder.group( {
        });
        ratingColType.addControl(arr[0], new FormControl(arr[1]));
        this.featureExtraction.controls['sourceFilePath'].value['sourceDetail']['ratingColType'] = ratingColType;
        let temp: FormGroup = <FormGroup>this.featureExtraction.controls['sourceFilePath'];
        let temp1: FormGroup = <FormGroup>temp.controls['sourceDetail'];
        temp1.addControl(arr[0], new FormControl(arr[1]));
        temp1.removeControl('ratingColType');
        this.featureExtraction.controls['sourceFilePath'] = temp;
        this.sourceFileTextDetail = this.featureExtraction;

      }
    });

  }

  openSaveModel() {
    if(this.saveModelDetail === undefined) {
      this.saveModelDetail = this.formBuilder.group({
        filePath: ['', Validators.required],
        modelName: ['', Validators.required],
      });
    }
    const dialogRef = this.dialog.open(SaveModelDialogueComponent, {
      width: '400px',
      data: this.saveModelDetail
    });

    dialogRef.afterClosed().subscribe(res => {
      this.saveModelDetail = res;
    });
  }

  openDecisionTree() {
    if( this.decisionTreeForm === undefined) {
      this.decisionTreeForm = this.formBuilder.group({
        impurity: new FormControl('entropy', Validators.required),
        depth: new FormControl(Validators.required),
        maxBins: new FormControl(Validators.required),
        training_size: new FormControl( 0.8, Validators.required),
        test_size: new FormControl(0.2, Validators.required)

      });
    }
    const dialogRef = this.dialog.open(DecisionTreeComponent, {
      width: '400px',
      data: this.decisionTreeForm
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
      this.trainModel = res;
      this.decisionTreeForm = res;
    });
  }
  openCollaborativeFiltering() {
    if(this.cfForm === undefined) {
      this.cfForm = this.formBuilder.group({
        ranks: new FormControl([],Validators.required),
        alphas: new FormControl([],Validators.required),
        regParams: new FormControl([],Validators.required),
        maxIter: new FormControl(5,Validators.required),
        implicitPref: new FormControl(true,Validators.required),
        numOfBlocks: new FormControl(10,Validators.required),
        evaluationMetric: new FormControl('rmse',Validators.required),
        trainingsize: new FormControl(0.8,Validators.required),
        testingsize: new FormControl(0.2, Validators.required)
      });
    }
    const dialogRef = this.dialog.open(CollaborativeFilteringComponent, {
      width: '400px',
      data: this.cfForm
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
      this.trainModel = res;
      this.cfForm = Object.assign({}, res);

      let str:string = this.trainModel.controls['ranks'].value;
      let arr =str.split(",").map(Number);
      this.trainModel.controls['ranks'].setValue(arr);

      let strFloat = [];


      str = this.trainModel.controls['alphas'].value;
      str.split(',').forEach(function (item) {
        strFloat.push(parseFloat(item))
      });
      //this.trainModel.controls['alphas'].setValue(str.split(",").map(Number));
      this.trainModel.controls['alphas'].setValue(strFloat);
      str = this.trainModel.controls['regParams'].value;
      this.trainModel.controls['regParams'].setValue(str.split(",").map(Number));
    });

  }
  openKMeans() {
    if(this.kMeansForm === undefined) {
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
    }
    const dialogRef = this.dialog.open(KMeansClusteringComponent, {
      width: '400px',
      height: '800px',
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
    if(this.jobName === undefined) {
      this.jobName = new FormControl('_newJob', Validators.required);
    }
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
    this.isloading = true;
    if (this.done.length != 4 ||
      (this.done.indexOf("FeatureExtraction") != -1 && this.done.indexOf("FeatureExtractionFromTextFile") != -1) ||
      (this.done.indexOf("Collaborative Filtering") != -1 && this.done.indexOf("FeatureExtraction") != -1) ||
      (this.done.indexOf("Decision Tree") != -1 && this.done.indexOf("FeatureExtractionFromTextFile") != -1) ||
      (this.done.indexOf("KMEANS") != -1 && this.done.indexOf("FeatureExtractionFromTextFile") != -1)
    ) {
      this.isNotValid = true;
    } else {
      this.isNotValid = false;

      this.paramData = this.formBuilder.group({
        jobName: this.jobName,
        featureExtraction: this.featureExtraction,
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
              this.isloading = false;
            },
            error => {
              console.log(error);
              this.snackBar.open(error["error"]["text"], "success", {
                duration: 10000
              })
              this.isloading = false;
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

  }

}
