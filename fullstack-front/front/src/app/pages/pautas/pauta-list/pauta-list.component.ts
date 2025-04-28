import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DynamicCrudComponent, DynamicTableConfig } from '../../../shared/dynamic-crud/dynamic-crud.component';
import { PautaService } from '../../../services/pauta.service';
import { PautaRequestDTO, PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-pauta-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    DynamicCrudComponent
  ],
  templateUrl: './pauta-list.component.html',
  styleUrls: ['./pauta-list.component.scss']
})
export class PautaListComponent implements OnInit {
  pautas: PautaResponseDTO[] = [];
  isLoading = false;
  error: string | null = null;

  config: DynamicTableConfig = {
    title: 'Lista de Pautas',
    fields: [
      {
        name: 'titulo',
        label: 'Título',
        type: 'input',
        dataType: 'string',
        required: true,
        maxLength: 100,
        showInTable: true,
        filterable: true,
        disabled: false
      },
      {
        name: 'descricao',
        label: 'Descrição',
        type: 'textarea',
        dataType: 'string',
        required: true,
        maxLength: 500,
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

  constructor(private pautaService: PautaService, private router: Router) {}

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

  createPauta(data: PautaRequestDTO): void {
    this.isLoading = true;
    this.pautaService.create(data).subscribe({
      next: () => this.loadPautas(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao criar pauta';
        this.isLoading = false;
      }
    });
  }

  updatePauta(event: { id: number, data: PautaRequestDTO }): void {
    this.isLoading = true;
    this.pautaService.update(event.id, event.data).subscribe({
      next: () => this.loadPautas(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao atualizar pauta';
        this.isLoading = false;
      }
    });
  }

  deletePauta(id: number): void {
    this.isLoading = true;
    this.pautaService.delete(id).subscribe({
      next: () => this.loadPautas(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao deletar pauta';
        this.isLoading = false;
      }
    });
  }

  viewPauta(item: PautaResponseDTO): void {
    this.router.navigate([`/pautas/${item.id}`]);
  }
}
