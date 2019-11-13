import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormGroup, FormBuilder, Validators, FormControl} from '@angular/forms';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-feature-text-dialogue',
  templateUrl: './feature-text-dialogue.component.html',
  styleUrls: ['./feature-text-dialogue.component.css']
})
export class FeatureTextDialogueComponent implements OnInit {

  sourceFilDetail: FormGroup;
  aliasFileDetail: FormGroup;
  selectedFile:File = null;


  sourceData: FormGroup;
  constructor(public dialogRef: MatDialogRef<FeatureTextDialogueComponent>,
              private formBuilder: FormBuilder,
              private httpClient:HttpClient,
              @Inject(MAT_DIALOG_DATA) public data: any) { }


  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.sourceFilDetail = this.formBuilder.group({
      filePath: new FormControl(),
      separator: new FormControl(),
      sourceDetail: this.formBuilder.group({
        orderOfSourceColumns: new FormControl(),
        itemColName: new FormControl(),
        ratingColName: new FormControl(),
        userIdColName: new FormControl(),
        ratingColType: new FormControl()

      })
    });

    this.aliasFileDetail = this.formBuilder.group({
      filePath: new FormControl(),
      separator: new FormControl(),
      orderOfSourceColumns: new FormControl(),
    });

    this.sourceData = this.formBuilder.group({
      sourceFile: this.sourceFilDetail,
      aliasFile: this.aliasFileDetail
    })
  }

  onFileSelected(event) {
    this.selectedFile = <File>event.target.files[0];
    const fd = new FormData();
    fd.append('file',this.selectedFile, this.selectedFile.name);
    this.httpClient.post<any>(
      'https://console.firebase.google.com/project/sparkml-697ad/storage/sparkml-697ad.appspot.com/files',
      fd, {
        reportProgress: true,
        observe: "events"
      })
      .subscribe(event => {
        console.log(event);
      },
        error => {
        console.log(error);
        }
      );
    this.sourceFilDetail.controls['filePath'].setValue(this.selectedFile);
    console.log(event)
    //this.http.post()

  }

  get f() { return this.sourceFilDetail.controls; }




}
