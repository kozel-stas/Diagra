import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonUiComponent } from './common-ui.component';

describe('CommonUiComponent', () => {
  let component: CommonUiComponent;
  let fixture: ComponentFixture<CommonUiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommonUiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommonUiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
