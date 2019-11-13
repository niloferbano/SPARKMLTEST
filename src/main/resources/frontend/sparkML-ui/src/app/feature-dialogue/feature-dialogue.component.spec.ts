import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureDialogueComponent } from './feature-dialogue.component';

describe('FeatureDialogueComponent', () => {
  let component: FeatureDialogueComponent;
  let fixture: ComponentFixture<FeatureDialogueComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FeatureDialogueComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
