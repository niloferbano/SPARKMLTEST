import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { map } from 'rxjs/operators';
@Injectable({
  providedIn: 'root'
})
export class CodeGenerationService {

  httpOptions={ headers: new HttpHeaders({ 'Content-Type': 'application/json'})};
  constructor(private http: HttpClient) { }

  generateDecisionTree(data: any) {
    return this.http.post("http://localhost:9090/runModel", JSON.parse(data),this.httpOptions ).pipe(
      map(data => {
        console.log(data);
        return data;
      })
    );
  }
  generateCollaborativeFiltering(data: any) {
    return this.http.post("http://localhost:9090/recommend", JSON.parse(data),this.httpOptions );
  }
  generateKMeans(data: any) {
    return this.http.post("http://localhost:9090/runKmeans", JSON.parse(data),this.httpOptions );
  }
}
