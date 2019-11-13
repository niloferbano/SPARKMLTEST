import { Injectable } from '@angular/core';
import { HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CodeGenerationService {

  constructor(private http: HttpClient) { }

  generateDecisionTree(data: any) {
    return this.http.post("http://localhost:9090/runModel", JSON.parse(data));
  }
}
