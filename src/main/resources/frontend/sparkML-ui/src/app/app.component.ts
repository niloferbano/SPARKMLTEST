import {Component} from '@angular/core';
import {CdkDragDrop, moveItemInArray, transferArrayItem} from '@angular/cdk/drag-drop';
import {FeatureDialogueComponent} from "./feature-dialogue/feature-dialogue.component";
import {MatDialog} from '@angular/material/dialog';
import {FeatureTextDialogueComponent} from "./feature-text-dialogue/feature-text-dialogue.component";
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})


export class AppComponent {

  filePath: string;
  labelCol: string;

  sourceFileDetail: FormGroup;

  constructor(private dialog: MatDialog) {}
  functions = [
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
      data: {sourceFileDetail: this.sourceFileDetail, color: this.labelCol}
    });

    dialogRef.afterClosed().subscribe(res => {
      this.labelCol = res;
    });
  }

  openFeatureExtractionText() {

    const dialogRef = this.dialog.open(FeatureTextDialogueComponent, {
      width: '400px',
      data: {filePath: this.filePath, color: this.labelCol}
    });

    dialogRef.afterClosed().subscribe(res => {
      this.labelCol = res;
    });

  }
}
