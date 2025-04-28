import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VotoFormComponent } from './voto-form.component';

describe('VotoFormComponent', () => {
  let component: VotoFormComponent;
  let fixture: ComponentFixture<VotoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VotoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VotoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
