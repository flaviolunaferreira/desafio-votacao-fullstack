import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { VotoService } from '../../../services/voto.service';
import { EleitorService } from '../../../services/eleitor.service';
import { SessaoAbertaDTO, VotoRequestDTO } from '../../../models/voto.model';
import { TimerComponent } from '../../../shared/components/timer/timer.component';
import { Subscription } from 'rxjs';
import { ApiError } from '../../../models/api-error.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-voto-form',
  standalone: true,
  imports: [CommonModule, FormsModule, TimerComponent],
  templateUrl: './voto-form.component.html',
  styleUrls: ['./voto-form.component.scss'],
  providers: [NgxMaskDirective]
})
export class VotoFormComponent implements OnInit, OnDestroy {
  cpf = '';
  cpfMask = '000.000.000-00'; // Máscara para CPF
  showCpfModal = true;
  cpfError = '';
  sessoes: SessaoAbertaDTO[] = [];
  selectedSessao: SessaoAbertaDTO | null = null;
  isLoading = false;
  error: string | null = null;
  private subscription: Subscription = new Subscription();

  constructor(
    private votoService: VotoService,
    private eleitorService: EleitorService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  validarCpf(): void {
    const cleanCpf = this.cpf.replace(/\D/g, '');
    if (!cleanCpf || cleanCpf.length !== 11) {
      this.cpfError = 'CPF deve ter 11 dígitos numéricos';
      return;
    }
    if (!/^\d{11}$/.test(cleanCpf)) {
      this.cpfError = 'CPF deve conter apenas números';
      return;
    }

    this.isLoading = true;
    this.cpfError = '';
    this.eleitorService.validarCpf(cleanCpf).subscribe({
      next: (response) => {
        if (response.status === 'ABLE_TO_VOTE') {
          this.carregarSessoes();
          this.showCpfModal = false;
        } else {
          this.cpfError = 'CPF não autorizado para votar';
          this.isLoading = false;
        }
      },
      error: (err: ApiError) => {
        this.cpfError = err.status === 404 ? 'CPF não registrado' : 'Erro ao validar CPF. Tente novamente.';
        this.isLoading = false;
      }
    });
  }

  carregarSessoes(): void {
    this.isLoading = true;
    const cleanCpf = this.cpf.replace(/\D/g, '');
    this.votoService.getSessoesAbertasNaoVotadas(cleanCpf).subscribe({
      next: (sessoes: SessaoAbertaDTO[]) => {
        this.sessoes = sessoes;
        if (sessoes.length === 0) {
          this.error = 'Nenhuma sessão aberta disponível para votação';
        } else {
          this.selectedSessao = sessoes[0];
        }
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.status === 404 ? 'CPF não registrado' : 'Erro ao carregar sessões. Tente novamente.';
        this.isLoading = false;
      }
    });
  }

  selectSessao(sessao: SessaoAbertaDTO): void {
    this.selectedSessao = sessao;
  }

  votar(voto: 'SIM' | 'NAO'): void {
    if (!this.selectedSessao) {
        console.error('Sessão selecionada é nula');
        return;
    }
    this.isLoading = true;
    const cleanCpf = this.cpf.replace(/\D/g, '');
    const votoRequest: VotoRequestDTO = {
        sessaoId: this.selectedSessao.id,
        cpf: cleanCpf,
        voto: voto === 'SIM'
    };
    console.log('Enviando voto:', JSON.stringify(votoRequest, null, 2));
    this.votoService.votar(votoRequest).subscribe({
        next: () => {
            this.sessoes = this.sessoes.filter(s => s.id !== this.selectedSessao!.id);
            this.selectedSessao = this.sessoes.length > 0 ? this.sessoes[0] : null;
            this.isLoading = false;
            alert('Voto registrado com sucesso!');
        },
        error: (err: ApiError) => {
            this.error = err.message || 'Erro ao registrar voto';
            this.isLoading = false;
            console.error('Erro ao votar:', err);
        }
    });
}

  cancel(): void {
    this.router.navigate(['/dashboard']);
  }
}
