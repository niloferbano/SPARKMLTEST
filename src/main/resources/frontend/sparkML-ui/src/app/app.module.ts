import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {DragDropModule} from '@angular/cdk/drag-drop';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FeatureDialogueComponent } from './feature-dialogue/feature-dialogue.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {MatDialogModule, MatInputModule, MatButtonModule} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FeatureTextDialogueComponent } from './feature-text-dialogue/feature-text-dialogue.component';
import {MatDividerModule} from "@angular/material/divider";
import {MatTooltipModule} from "@angular/material/tooltip";
import { SaveModelDialogueComponent } from './save-model-dialogue/save-model-dialogue.component';
import { KMeansClusteringComponent } from './kmeans-clustering/kmeans-clustering.component';
import { DecisionTreeComponent } from './decision-tree/decision-tree.component';
import { CollaborativeFilteringComponent } from './collaborative-filtering/collaborative-filtering.component';
import { HomePageComponent } from './home-page/home-page.component';
import { HttpClientModule} from "@angular/common/http";
import {CodeGenerationService} from "./services/code-generation.service";
import { StartJobComponent } from './start-job/start-job.component';
import { MatSnackBarModule } from "@angular/material/snack-bar";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MAT_RADIO_DEFAULT_OPTIONS, MatRadioModule} from "@angular/material/radio";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatSelectModule} from "@angular/material/select";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";


@NgModule({
  declarations: [
    AppComponent,
    FeatureDialogueComponent,
    FeatureTextDialogueComponent,
    SaveModelDialogueComponent,
    KMeansClusteringComponent,
    DecisionTreeComponent,
    CollaborativeFilteringComponent,
    HomePageComponent,
    StartJobComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    DragDropModule,
    MatFormFieldModule,
    FormsModule,
    MatDialogModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatDividerModule,
    MatTooltipModule,
    HttpClientModule,
    MatSnackBarModule,
    MatCheckboxModule,
    MatRadioModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  providers: [CodeGenerationService,
    {
      provide: MAT_RADIO_DEFAULT_OPTIONS,
      useValue: { color: 'accent' },
    }],
  bootstrap: [AppComponent],
  entryComponents: [
    StartJobComponent,
    FeatureDialogueComponent,
    FeatureTextDialogueComponent,
    SaveModelDialogueComponent,
    KMeansClusteringComponent,
    DecisionTreeComponent,
    CollaborativeFilteringComponent]
})
export class AppModule { }
