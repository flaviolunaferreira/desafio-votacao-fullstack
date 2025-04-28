import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DynamicCrudComponent, DynamicTableConfig } from '../../../shared/dynamic-crud/dynamic-crud.component';
import { SessaoVotacaoService } from '../../../services/sessao-votacao.service';
import { SessaoVotacaoRequestDTO, SessaoVotacaoResponseDTO } from '../../../models/sessao-votacao.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-sessao-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    DynamicCrudComponent
  ],
  templateUrl: './sessao-list.component.html',
  styleUrls: ['./sessao-list.component.scss']
})
export class SessaoListComponent implements OnInit {
  sessoes: SessaoVotacaoResponseDTO[] = [];
  isLoading = false;
  error: string | null = null;

  config: DynamicTableConfig = {
    title: 'Lista de Sessões de Votação',
    fields: [
      {
        name: 'pautaId',
        label: 'ID da Pauta',
        type: 'input',
        dataType: 'number',
        required: true,
        minValue: 1,
        showInTable: true,
        filterable: true,
        disabled: false
      },
      {
        name: 'dataAbertura',
        label: 'Data de Abertura',
        type: 'date',
        dataType: 'date',
        required: true,
        showInTable: true,
        filterable: true,
        disabled: false
      },
      {
        name: 'dataFechamento',
        label: 'Data de Fechamento',
        type: 'date',
        dataType: 'date',
        required: true,
        showInTable: true,
        filterable: true,
        disabled: false
      }
    ],
    actions: {
      view: true,
      edit: true,
      delete: true
    }
  };

  constructor(private sessaoService: SessaoVotacaoService, private router: Router) {}

  ngOnInit(): void {
    this.loadSessoes();
  }

  loadSessoes(): void {
    this.isLoading = true;
    this.sessaoService.getAll().subscribe({
      next: (sessoes: SessaoVotacaoResponseDTO[]) => {
        this.sessoes = sessoes;
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar sessões';
        this.isLoading = false;
      }
    });
  }

  createSessao(data: SessaoVotacaoRequestDTO): void {
    this.isLoading = true;
    this.sessaoService.create(data).subscribe({
      next: () => this.loadSessoes(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao criar sessão';
        this.isLoading = false;
      }
    });
  }

  updateSessao(event: { id: number, data: SessaoVotacaoRequestDTO }): void {
    this.isLoading = true;
    this.sessaoService.update(event.id, event.data).subscribe({
      next: () => this.loadSessoes(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao atualizar sessão';
        this.isLoading = false;
      }
    });
  }

  deleteSessao(id: number): void {
    this.isLoading = true;
    this.sessaoService.delete(id).subscribe({
      next: () => this.loadSessoes(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao deletar sessão';
        this.isLoading = false;
      }
    });
  }

  viewSessao(item: SessaoVotacaoResponseDTO): void {
    this.router.navigate([`/sessoes/${item.id}`]);
  }
}
