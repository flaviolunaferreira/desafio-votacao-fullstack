import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { SessaoVotacaoService } from '../../../services/sessao-votacao.service';
import { PautaService } from '../../../services/pauta.service';
import { SessaoVotacaoRequestDTO, SessaoVotacaoResponseDTO } from '../../../models/sessao-votacao.model';
import { PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-sessao-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './sessao-form.component.html',
  styleUrls: ['./sessao-form.component.scss']
})
export class SessaoFormComponent implements OnInit {
  sessaoForm: FormGroup;
  pautas: PautaResponseDTO[] = [];
  isLoading = false;
  error: string | null = null;
  isEditMode = false;
  sessaoId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private sessaoService: SessaoVotacaoService,
    private pautaService: PautaService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.sessaoForm = this.fb.group({
      pautaId: ['', Validators.required],
      dataAbertura: ['', Validators.required], // Mantido para UX, mas não enviado ao backend
      duracao: ['', [Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.sessaoId = parseInt(id, 10);
      if (this.sessaoId) {
        this.loadSessao(this.sessaoId);
      }
    }
    this.loadPautas();
  }

  loadPautas(): void {
    this.isLoading = true;
    this.pautaService.getAll().subscribe({
      next: (pautas: PautaResponseDTO[]) => {
        this.pautas = pautas;
        this.isLoading = false;
        if (pautas.length === 0) {
          this.error = 'Nenhuma pauta disponível para criar uma sessão';
        }
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar pautas';
        this.isLoading = false;
      }
    });
  }

  loadSessao(id: number): void {
    this.isLoading = true;
    this.sessaoService.getById(id).subscribe({
      next: (sessao: SessaoVotacaoResponseDTO) => {
        let duracao = '';
        if (sessao.dataAbertura && sessao.dataFechamento) {
          const abertura = new Date(sessao.dataAbertura).getTime();
          const fechamento = new Date(sessao.dataFechamento).getTime();
          const diffMinutos = (fechamento - abertura) / (1000 * 60);
          if (diffMinutos > 0) {
            duracao = Math.round(diffMinutos).toString();
          }
        }
        this.sessaoForm.patchValue({
          pautaId: sessao.pautaId.toString(),
          dataAbertura: new Date(sessao.dataAbertura).toISOString().slice(0, 16),
          duracao
        });
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar sessão';
        this.isLoading = false;
      }
    });
  }

  saveSessao(): void {
    if (this.sessaoForm.invalid) {
      this.sessaoForm.markAllAsTouched();
      return;
    }
    this.isLoading = true;

    const formValue = this.sessaoForm.value;
    if (!formValue.pautaId || isNaN(parseInt(formValue.pautaId, 10))) {
      this.error = 'Selecione uma pauta válida';
      this.isLoading = false;
      return;
    }

    const duracaoMinutos = formValue.duracao ? parseInt(formValue.duracao, 10) : 1;
    if (formValue.duracao && (isNaN(duracaoMinutos) || duracaoMinutos <= 0)) {
      this.error = 'A duração deve ser um número positivo';
      this.isLoading = false;
      return;
    }

    const sessaoData: SessaoVotacaoRequestDTO = {
      pautaId: parseInt(formValue.pautaId, 10),
      duracao: duracaoMinutos
    };

    console.log('Enviando sessaoData:', sessaoData);

    const action = this.isEditMode && this.sessaoId
      ? this.sessaoService.update(this.sessaoId, sessaoData)
      : this.sessaoService.create(sessaoData);

    action.subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/sessoes']);
      },
      error: (err: ApiError) => {
        console.error('Erro na requisição:', err);
        this.error = err.message || 'Erro ao salvar sessão. Verifique os dados e tente novamente.';
        this.isLoading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/sessoes']);
  }
}
