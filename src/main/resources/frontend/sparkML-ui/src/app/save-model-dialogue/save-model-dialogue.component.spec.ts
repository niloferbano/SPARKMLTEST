import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SaveModelDialogueComponent } from './save-model-dialogue.component';

describe('SaveModelDialogueComponent', () => {
  let component: SaveModelDialogueComponent;
  let fixture: ComponentFixture<SaveModelDialogueComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SaveModelDialogueComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SaveModelDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
