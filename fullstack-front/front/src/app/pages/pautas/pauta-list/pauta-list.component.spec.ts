import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PautaListComponent } from './pauta-list.component';

describe('PautaListComponent', () => {
  let component: PautaListComponent;
  let fixture: ComponentFixture<PautaListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PautaListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PautaListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
