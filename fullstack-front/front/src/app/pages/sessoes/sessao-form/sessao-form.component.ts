import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { DynamicCrudComponent, DynamicTableConfig } from '../../../shared/dynamic-crud/dynamic-crud.component';
import { SessaoVotacaoService } from '../../../services/sessao-votacao.service';
import { SessaoVotacaoRequestDTO, SessaoVotacaoResponseDTO } from '../../../models/sessao-votacao.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-sessao-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    DynamicCrudComponent
  ],
  templateUrl: './sessao-form.component.html',
  styleUrls: ['./sessao-form.component.scss']
})
export class SessaoFormComponent implements OnInit {
  sessao: SessaoVotacaoRequestDTO | SessaoVotacaoResponseDTO = { pautaId: 0 };
  isLoading = false;
  error: string | null = null;
  isEditMode = false;

  config: DynamicTableConfig = {
    title: 'Gerenciar Sessão de Votação',
    fields: [
      {
        name: 'pautaId',
        label: 'ID da Pauta',
        type: 'input',
        dataType: 'number',
        required: true,
        minValue: 1,
        showInTable: false,
        filterable: false,
        disabled: false
      },
      {
        name: 'dataAbertura',
        label: 'Data e Hora de Abertura',
        type: 'datetime-local',
        dataType: 'date',
        required: true,
        showInTable: false,
        filterable: false,
        disabled: false
      },
      {
        name: 'dataFechamento',
        label: 'Data e Hora de Fechamento',
        type: 'input',
        dataType: 'date',
        required: false,
        showInTable: false,
        filterable: false,
        disabled: false
      }
    ],
    actions: {
      view: false,
      edit: false,
      delete: false
    }
  };

  constructor(
    private sessaoService: SessaoVotacaoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadSessao(+id);
    }
  }

  loadSessao(id: number): void {
    this.isLoading = true;
    this.sessaoService.getById(id).subscribe({
      next: (sessao: SessaoVotacaoResponseDTO) => {
        this.sessao = sessao;
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar sessão';
        this.isLoading = false;
      }
    });
  }

  saveSessao(data: SessaoVotacaoRequestDTO & { dataAbertura?: string, dataFechamento?: string }): void {
    this.isLoading = true;

    // Validar que dataFechamento é posterior a dataAbertura, se fornecida
    if (data.dataFechamento && data.dataAbertura) {
      const abertura = new Date(data.dataAbertura);
      const fechamento = new Date(data.dataFechamento);
      if (fechamento <= abertura) {
        this.error = 'Data de fechamento deve ser posterior à data de abertura';
        this.isLoading = false;
        return;
      }
    }

    const action = this.isEditMode && (this.sessao as SessaoVotacaoResponseDTO).id
      ? this.sessaoService.update((this.sessao as SessaoVotacaoResponseDTO).id, data)
      : this.sessaoService.create(data);

    action.subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/sessoes']);
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao salvar sessão';
        this.isLoading = false;
      }
    });
  }
}
