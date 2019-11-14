import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StartJobComponent } from './start-job.component';

describe('StartJobComponent', () => {
  let component: StartJobComponent;
  let fixture: ComponentFixture<StartJobComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StartJobComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StartJobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
