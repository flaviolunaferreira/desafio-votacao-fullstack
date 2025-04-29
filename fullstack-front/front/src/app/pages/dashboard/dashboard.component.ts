import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardResumoDTO, ParticipacaoSessaoDTO, TendenciaVotosDTO } from '../../models/dashboard.model';
import { ApiError } from '../../models/api-error.model';
import { ChartConfiguration, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NgChartsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  resumo: DashboardResumoDTO = {
    totalPautas: 0,
    totalSessoesAbertas: 0,
    totalSessoesEncerradas: 0,
    totalVotos: 0,
    percentualVotosSim: 0,
    percentualVotosNao: 0,
    pautasRecentes: [],
    sessoesAtivas: []
  };
  participacaoSessoes: ParticipacaoSessaoDTO[] = [];
  tendenciaVotos: TendenciaVotosDTO[] = [];
  isLoading = false;
  error: string | null = null;
  private pendingRequests = 0;

  // Filtro de período
  filtroPeriodo: string = 'SEMANA'; // Padrão: Semana

  // Configuração do gráfico de tendência
  public lineChartData: ChartConfiguration['data'] = {
    datasets: [
      {
        data: [],
        label: 'Votos Sim',
        borderColor: '#007acc',
        backgroundColor: 'rgba(0, 122, 204, 0.2)',
        fill: true
      },
      {
        data: [],
        label: 'Votos Não',
        borderColor: '#ff4444',
        backgroundColor: 'rgba(255, 68, 68, 0.2)',
        fill: true
      }
    ],
    labels: []
  };

  public lineChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: true },
      title: { display: true, text: 'Tendência de Votos' }
    },
    scales: {
      y: { beginAtZero: true }
    }
  };

  public lineChartType: 'line' = 'line';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private trackLoadingStart(): void {
    this.pendingRequests++;
    this.isLoading = true;
  }

  private trackLoadingComplete(): void {
    this.pendingRequests--;
    if (this.pendingRequests === 0) {
      this.isLoading = false;
    }
  }

  loadDashboardData(): void {
    this.error = null;

    this.trackLoadingStart();
    this.dashboardService.getResumo().subscribe({
      next: (resumo: DashboardResumoDTO) => { this.resumo = resumo; },
      error: (err: ApiError) => { this.error = err.message || 'Erro ao carregar resumo'; },
      complete: () => { this.trackLoadingComplete(); }
    });

    this.trackLoadingStart();
    this.dashboardService.getParticipacaoSessoes().subscribe({
      next: (participacao: ParticipacaoSessaoDTO[]) => { this.participacaoSessoes = participacao; },
      error: (err: ApiError) => { this.error = err.message || 'Erro ao carregar participação em sessões'; },
      complete: () => { this.trackLoadingComplete(); }
    });

    this.loadTendenciaVotos();
  }

  loadTendenciaVotos(): void {
    const { inicio, fim } = this.calcularDatas();
    const granularidade = this.filtroPeriodo;

    this.trackLoadingStart();
    this.dashboardService.getTendenciaVotos(inicio, fim, granularidade).subscribe({
      next: (tendencia: TendenciaVotosDTO[]) => {
        this.tendenciaVotos = tendencia;
        this.updateChartData();
      },
      error: (err: ApiError) => { this.error = err.message || 'Erro ao carregar tendência de votos'; },
      complete: () => { this.trackLoadingComplete(); }
    });
  }

  calcularDatas(): { inicio: string; fim: string } {
    const hoje = new Date();
    const fim = this.formatarData(hoje);
    let inicio: string;

    switch (this.filtroPeriodo) {
      case 'SEMANA':
        const umaSemanaAtras = new Date(hoje);
        umaSemanaAtras.setDate(hoje.getDate() - 7);
        inicio = this.formatarData(umaSemanaAtras);
        break;
      case 'MES':
        const umMesAtras = new Date(hoje);
        umMesAtras.setDate(hoje.getDate() - 30);
        inicio = this.formatarData(umMesAtras);
        break;
      default:
        inicio = fim;
    }

    return { inicio, fim };
  }

  formatarData(data: Date): string {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  updateChartData(): void {
    this.lineChartData = {
      datasets: [
        {
          data: this.tendenciaVotos.map(t => t.votosSim),
          label: 'Votos Sim',
          borderColor: '#007acc',
          backgroundColor: 'rgba(0, 122, 204, 0.2)',
          fill: true
        },
        {
          data: this.tendenciaVotos.map(t => t.votosNao),
          label: 'Votos Não',
          borderColor: '#ff4444',
          backgroundColor: 'rgba(255, 68, 68, 0.2)',
          fill: true
        }
      ],
      labels: this.tendenciaVotos.map(t => t.periodo)
    };
  }

  filtrarTendencia(): void {
    this.loadTendenciaVotos();
  }
}
