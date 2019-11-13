import { Component, OnInit } from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {CdkDragDrop, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {FeatureDialogueComponent} from "../feature-dialogue/feature-dialogue.component";
import {FeatureTextDialogueComponent} from "../feature-text-dialogue/feature-text-dialogue.component";
import {Form, FormBuilder, FormGroup} from "@angular/forms";
import {SaveModelDialogueComponent} from "../save-model-dialogue/save-model-dialogue.component";
import {DecisionTreeComponent} from "../decision-tree/decision-tree.component";
import {CodeGenerationService} from "../services/code-generation.service";
import {first} from "rxjs/operators";


@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  constructor(private dialog: MatDialog,
              private services: CodeGenerationService,
              private formBuilder: FormBuilder) { }
  sourceFileDetail: FormGroup;
  saveModelDetail: FormGroup;
  sourceFileTextDetail: FormGroup;
  trainModel: FormGroup;
  paramData: FormGroup;

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

  done = [

  ];
  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex);
    }
  }
  openFeatureExtraction(): void {
    const dialogRef = this.dialog.open(FeatureDialogueComponent, {
      width: '400px',
      data: {sourceFileDetail: this.sourceFileDetail}
    });

    dialogRef.afterClosed().subscribe(res => {
      console.log(res);
      this.sourceFileDetail = res;
      this.jsonData["featureExtraction"]= {filePath: this.sourceFileDetail.controls['filePath'].value,
                                          labledCol: this.sourceFileDetail.controls['labelCol'].value};
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
      data: {saveModelDetail: this.saveModelDetail}
    });

    dialogRef.afterClosed().subscribe(res => {
      this.saveModelDetail = res;
      this.jsonData['saveModel']= {filePath: this.saveModelDetail.controls['filePath'].value,
      modelName: this.saveModelDetail.controls['modelName'].value};
      console.log(JSON.stringify(this.jsonData));
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

      this.jsonData['trainModel']= {impurity: this.trainModel.controls['impurity'].value,
        depth: this.trainModel.controls['depth'].value,
        maxBins: this.trainModel.controls['maxBins'].value,
        training_size: this.trainModel.controls['training_size'].value,
        test_size: this.trainModel.controls['test_size'].value};
      console.log(JSON.stringify(this.jsonData));
    });
  }


  GenerateJarFile() {
    this.paramData = this.formBuilder.group({
      featureExtraction: this.sourceFileDetail,
      trainModel: this.trainModel,
      saveModel: this.saveModelDetail
    })
      this.services.generateDecisionTree(JSON.stringify(this.paramData.value))
        .pipe(first())
        .subscribe(
          data => {
            console.log(data);
          },
          error => {
            console.log(error);
          }
        );
  }

  ngOnInit() {
  }

}
