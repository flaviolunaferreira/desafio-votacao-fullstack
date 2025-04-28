import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessaoListComponent } from './sessao-list.component';

describe('SessaoListComponent', () => {
  let component: SessaoListComponent;
  let fixture: ComponentFixture<SessaoListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessaoListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessaoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
