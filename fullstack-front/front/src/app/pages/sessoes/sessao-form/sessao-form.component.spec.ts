import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessaoFormComponent } from './sessao-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PautaService } from '../../../services/pauta.service';
import { SessaoVotacaoService } from '../../../services/sessao-votacao.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('SessaoFormComponent', () => {
  let component: SessaoFormComponent;
  let fixture: ComponentFixture<SessaoFormComponent>;
  let pautaService: jasmine.SpyObj<PautaService>;
  let sessaoService: jasmine.SpyObj<SessaoVotacaoService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const pautaServiceSpy = jasmine.createSpyObj('PautaService', ['getAll']);
    const sessaoServiceSpy = jasmine.createSpyObj('SessaoVotacaoService', ['getById', 'create', 'update']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, ReactiveFormsModule, SessaoFormComponent],
      providers: [
        { provide: PautaService, useValue: pautaServiceSpy },
        { provide: SessaoVotacaoService, useValue: sessaoServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => null } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SessaoFormComponent);
    component = fixture.componentInstance;
    pautaService = TestBed.inject(PautaService) as jasmine.SpyObj<PautaService>;
    sessaoService = TestBed.inject(SessaoVotacaoService) as jasmine.SpyObj<SessaoVotacaoService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form', () => {
    expect(component.sessaoForm).toBeDefined();
    expect(component.sessaoForm.get('pautaId')).toBeDefined();
    expect(component.sessaoForm.get('dataAbertura')).toBeDefined();
    expect(component.sessaoForm.get('duracao')).toBeDefined();
  });

  it('should load pautas', () => {
    const mockPautas = [{ id: 1, titulo: 'Pauta 1', descricao: 'Descrição' }];
    pautaService.getAll.and.returnValue(of(mockPautas));
    component.ngOnInit();
    expect(component.pautas).toEqual(mockPautas);
    expect(component.error).toBeNull();
  });

  it('should handle empty pautas', () => {
    pautaService.getAll.and.returnValue(of([]));
    component.ngOnInit();
    expect(component.pautas).toEqual([]);
    expect(component.error).toBe('Nenhuma pauta disponível para criar uma sessão');
  });

  it('should load sessao in edit mode', () => {
    const mockSessao = {
      id: 1,
      pautaId: 1,
      dataAbertura: '2025-04-28T10:00:00Z',
      dataFechamento: '2025-04-28T10:05:00Z'
    };
    sessaoService.getById.and.returnValue(of(mockSessao));
    component.sessaoId = 1;
    component.isEditMode = true;
    component.loadSessao(1);
    expect(component.sessaoForm.get('pautaId')?.value).toBe('1');
    expect(component.sessaoForm.get('duracao')?.value).toBe('5');
  });

  it('should handle invalid pautaId', () => {
    component.sessaoForm.patchValue({ pautaId: '' });
    component.saveSessao();
    expect(component.error).toBe('Selecione uma pauta válida');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should handle invalid duracao', () => {
    component.sessaoForm.patchValue({ pautaId: '1', duracao: '-1' });
    component.saveSessao();
    expect(component.error).toBe('A duração deve ser um número positivo');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should create sessao with valid data', () => {
    component.sessaoForm.patchValue({ pautaId: '1', duracao: '5' });
    sessaoService.create.and.returnValue(of({ id: 1, pautaId: 1, dataAbertura: '2025-04-28T10:00:00Z', dataFechamento: '2025-04-28T10:05:00Z' }));
    component.saveSessao();
    expect(sessaoService.create).toHaveBeenCalledWith({ pautaId: 1, duracao: 5 });
    expect(router.navigate).toHaveBeenCalledWith(['/sessoes']);
  });
});
