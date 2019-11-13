import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { KMeansClusteringComponent } from './kmeans-clustering.component';

describe('KMeansClusteringComponent', () => {
  let component: KMeansClusteringComponent;
  let fixture: ComponentFixture<KMeansClusteringComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KMeansClusteringComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KMeansClusteringComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
