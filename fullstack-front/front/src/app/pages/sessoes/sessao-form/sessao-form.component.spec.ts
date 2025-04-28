import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessaoFormComponent } from './sessao-form.component';

describe('SessaoFormComponent', () => {
  let component: SessaoFormComponent;
  let fixture: ComponentFixture<SessaoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessaoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessaoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
