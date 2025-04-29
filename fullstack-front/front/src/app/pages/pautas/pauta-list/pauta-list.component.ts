import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PautaService } from '../../../services/pauta.service';
import { PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-pauta-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pauta-list.component.html',
  styleUrls: ['./pauta-list.component.scss']
})
export class PautaListComponent implements OnInit {
  pautas: PautaResponseDTO[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(private pautaService: PautaService) {}

  ngOnInit(): void {
    this.loadPautas();
  }

  loadPautas(): void {
    this.isLoading = true;
    this.pautaService.getAll().subscribe({
      next: (pautas: PautaResponseDTO[]) => {
        this.pautas = pautas;
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar pautas';
        this.isLoading = false;
      }
    });
  }

  deletePauta(id: number): void {
    if (confirm('Deseja excluir esta pauta?')) {
      this.isLoading = true;
      this.pautaService.delete(id).subscribe({
        next: () => this.loadPautas(),
        error: (err: ApiError) => {
          this.error = err.message || 'Erro ao deletar pauta';
          this.isLoading = false;
        }
      });
    }
  }
}
