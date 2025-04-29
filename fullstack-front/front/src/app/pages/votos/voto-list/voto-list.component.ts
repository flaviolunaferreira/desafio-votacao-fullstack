import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VotoService } from '../../../services/voto.service';
import { PautaService } from '../../../services/pauta.service';
import { VotoResponseDTO, ResultadoResponseDTO } from '../../../models/voto.model';
import { PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-voto-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './voto-list.component.html',
  styleUrls: ['./voto-list.component.scss']
})
export class VotoListComponent implements OnInit {
  votos: VotoResponseDTO[] = [];
  pautas: PautaResponseDTO[] = [];
  selectedPautaId: string = '';
  resultado: ResultadoResponseDTO | null = null;
  isLoading = false;
  error: string | null = null;

  constructor(
    private votoService: VotoService,
    private pautaService: PautaService
  ) {}

  ngOnInit(): void {
    this.loadPautas();
    this.loadVotos();
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

  loadVotos(): void {
    this.isLoading = true;
    const pautaId = this.selectedPautaId ? parseInt(this.selectedPautaId) : undefined;
    this.votoService.getAll(pautaId).subscribe({
      next: (votos: VotoResponseDTO[]) => {
        this.votos = votos;
        this.loadResultado();
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar votos';
        this.isLoading = false;
      }
    });
  }

  loadResultado(): void {
    if (this.selectedPautaId) {
      this.votoService.getResultado(parseInt(this.selectedPautaId)).subscribe({
        next: (resultado: ResultadoResponseDTO) => {
          this.resultado = resultado;
        },
        error: (err: ApiError) => {
          this.error = err.message || 'Erro ao carregar resultado';
        }
      });
    } else {
      this.resultado = null;
    }
  }

  getPautaTitulo(pautaId: number): string {
    const pauta = this.pautas.find(p => p.id === pautaId);
    return pauta ? pauta.titulo : 'Pauta não encontrada';
  }
}
