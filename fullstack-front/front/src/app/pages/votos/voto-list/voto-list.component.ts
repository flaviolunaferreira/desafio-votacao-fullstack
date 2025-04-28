import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DynamicCrudComponent, DynamicTableConfig } from '../../../shared/dynamic-crud/dynamic-crud.component';
import { VotoService } from '../../../services/voto.service';
import { VotoRequestDTO, VotoResponseDTO } from '../../../models/voto.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-voto-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    DynamicCrudComponent
  ],
  templateUrl: './voto-list.component.html',
  styleUrls: ['./voto-list.component.scss']
})
export class VotoListComponent implements OnInit {
  votos: VotoResponseDTO[] = [];
  isLoading = false;
  error: string | null = null;

  config: DynamicTableConfig = {
    title: 'Lista de Votos',
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
        name: 'cpf',
        label: 'CPF',
        type: 'input',
        dataType: 'string',
        required: true,
        mask: '000.000.000-00',
        pattern: /^\d{3}\.\d{3}\.\d{3}-\d{2}$/,
        showInTable: true,
        filterable: true,
        disabled: false
      },
      {
        name: 'voto',
        label: 'Voto',
        type: 'dropdown',
        dataType: 'string',
        required: true,
        options: ['SIM', 'NAO'],
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

  constructor(private votoService: VotoService, private router: Router) {}

  ngOnInit(): void {
    this.loadVotos();
  }

  loadVotos(): void {
    this.isLoading = true;
    this.votoService.getAll().subscribe({
      next: (votos: VotoResponseDTO[]) => {
        this.votos = votos;
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar votos';
        this.isLoading = false;
      }
    });
  }

  createVoto(data: VotoRequestDTO): void {
    this.isLoading = true;
    this.votoService.create(data).subscribe({
      next: () => this.loadVotos(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao registrar voto';
        this.isLoading = false;
      }
    });
  }

  updateVoto(event: { id: number, data: VotoRequestDTO }): void {
    this.isLoading = true;
    this.votoService.update(event.id, event.data).subscribe({
      next: () => this.loadVotos(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao atualizar voto';
        this.isLoading = false;
      }
    });
  }

  deleteVoto(id: number): void {
    this.isLoading = true;
    this.votoService.delete(id).subscribe({
      next: () => this.loadVotos(),
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao deletar voto';
        this.isLoading = false;
      }
    });
  }

  viewVoto(item: VotoResponseDTO): void {
    this.router.navigate([`/votos/${item.id}`]);
  }
}
