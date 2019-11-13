import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureTextDialogueComponent } from './feature-text-dialogue.component';

describe('FeatureTextDialogueComponent', () => {
  let component: FeatureTextDialogueComponent;
  let fixture: ComponentFixture<FeatureTextDialogueComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FeatureTextDialogueComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureTextDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
