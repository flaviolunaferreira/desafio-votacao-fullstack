import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SessaoVotacaoService } from '../../../services/sessao-votacao.service';
import { PautaService } from '../../../services/pauta.service';
import { SessaoVotacaoResponseDTO } from '../../../models/sessao-votacao.model';
import { PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-sessao-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sessao-list.component.html',
  styleUrls: ['./sessao-list.component.scss']
})
export class SessaoListComponent implements OnInit {
  sessoes: SessaoVotacaoResponseDTO[] = [];
  pautas: PautaResponseDTO[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(
    private sessaoService: SessaoVotacaoService,
    private pautaService: PautaService
  ) {}

  ngOnInit(): void {
    this.loadPautas();
    this.loadSessoes();
  }

  loadPautas(): void {
    this.pautaService.getAll().subscribe({
      next: (pautas: PautaResponseDTO[]) => {
        this.pautas = pautas;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar pautas';
      }
    });
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

  getPautaTitulo(pautaId: number): string {
    const pauta = this.pautas.find(p => p.id === pautaId);
    return pauta ? pauta.titulo : 'Pauta não encontrada';
  }

  isSessaoAberta(sessao: SessaoVotacaoResponseDTO): boolean {
    return new Date(sessao.dataFechamento) > new Date();
  }

  deleteSessao(id: number): void {
    if (confirm('Deseja excluir esta sessão?')) {
      this.isLoading = true;
      this.sessaoService.delete(id).subscribe({
        next: () => this.loadSessoes(),
        error: (err: ApiError) => {
          this.error = err.message || 'Erro ao deletar sessão';
          this.isLoading = false;
        }
      });
    }
  }
}
