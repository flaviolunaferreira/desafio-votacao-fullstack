import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VotoListComponent } from './voto-list.component';

describe('VotoListComponent', () => {
  let component: VotoListComponent;
  let fixture: ComponentFixture<VotoListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VotoListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VotoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
