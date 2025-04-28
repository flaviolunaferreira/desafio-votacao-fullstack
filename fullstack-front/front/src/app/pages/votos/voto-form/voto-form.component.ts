import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VotoService } from '../../../services/voto.service';
import { SessaoAberta } from '../../../models/sessao-votacao.model';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-voto-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './voto-form.component.html',
  styleUrls: ['./voto-form.component.scss']
})
export class VotoFormComponent implements OnInit, OnDestroy {
  cpf: string = '';
  showCpfModal: boolean = true;
  cpfError: string = '';
  sessoes: SessaoAberta[] = [];
  selectedSessao: SessaoAberta | null = null;
  timers: { [key: number]: string } = {};
  private timerSubscription: Subscription | null = null;

  constructor(private votoService: VotoService) {}

  ngOnInit(): void {}

  ngOnDestroy(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  validarCpf(): void {
    this.votoService.validarCpf(this.cpf).subscribe({
      next: (isValid: any) => {
        if (isValid) {
          this.carregarSessoes();
          this.showCpfModal = false;
        } else {
          this.cpfError = 'CPF inválido';
        }
      },
      error: (err: { error: { message: string; }; }) => {
        this.cpfError = err.error?.message || 'Erro ao validar CPF';
      }
    });
  }

  carregarSessoes(): void {
    this.votoService.listarSessoesAbertasSemVoto(this.cpf).subscribe({
      next: (sessoes: SessaoAberta[]) => {
        this.sessoes = sessoes;
        if (sessoes.length > 0) {
          this.selectedSessao = sessoes[0];
        }
        this.startTimer();
      },
      error: (err: { error: { message: string; }; }) => {
        this.cpfError = err.error?.message || 'Erro ao carregar sessões';
      }
    });
  }

  startTimer(): void {
    this.timerSubscription = interval(1000).subscribe(() => {
      const now = new Date();
      this.sessoes.forEach((sessao) => {
        const dataFim = new Date(sessao.dataFim);
        if (isNaN(dataFim.getTime())) {
          this.timers[sessao.id] = 'Data inválida';
          return;
        }
        const diffMs = dataFim.getTime() - now.getTime();
        if (diffMs <= 0) {
          this.timers[sessao.id] = 'Encerrada';
        } else {
          const hours = Math.floor(diffMs / (1000 * 60 * 60));
          const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
          const seconds = Math.floor((diffMs % (1000 * 60)) / 1000);
          this.timers[sessao.id] = `${hours}h ${minutes}m ${seconds}s`;
        }
      });
    });
  }

  getTempoRestanteColor(tempo: string, dataFim: string): string {
    if (tempo === 'Encerrada' || tempo === 'Data inválida') return '#D32F2F';
    const diffMs = new Date(dataFim).getTime() - new Date().getTime();
    if (diffMs <= 60 * 1000) return '#D32F2F'; // < 1 min
    if (diffMs <= 30 * 60 * 1000) return '#FF7043'; // ≤ 30 min
    if (diffMs <= 60 * 60 * 1000) return '#FFCA28'; // ≤ 1 hora
    return '#66BB6A'; // > 1 hora
  }

  votar(voto: string): void {
    if (!this.selectedSessao) return;
    const votoRequest = {
      pautaId: this.selectedSessao.pautaId,
      cpf: this.cpf,
      voto: voto
    };
    this.votoService.votar(votoRequest).subscribe({
      next: () => {
        this.sessoes = this.sessoes.filter(s => s.id !== this.selectedSessao!.id);
        this.selectedSessao = this.sessoes.length > 0 ? this.sessoes[0] : null;
      },
      error: (err: { error: { message: string; }; }) => {
        this.cpfError = err.error?.message || 'Erro ao registrar voto';
      }
    });
  }
}
