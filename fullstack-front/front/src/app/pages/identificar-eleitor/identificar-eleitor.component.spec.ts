import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IdentificarEleitorComponent } from './identificar-eleitor.component';

describe('IdentificarEleitorComponent', () => {
  let component: IdentificarEleitorComponent;
  let fixture: ComponentFixture<IdentificarEleitorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IdentificarEleitorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IdentificarEleitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
