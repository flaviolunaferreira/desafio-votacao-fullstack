import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { DynamicCrudComponent, DynamicTableConfig } from '../../../shared/dynamic-crud/dynamic-crud.component';
import { PautaService } from '../../../services/pauta.service';
import { PautaRequestDTO, PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-pauta-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    DynamicCrudComponent
  ],
  templateUrl: './pauta-form.component.html',
  styleUrls: ['./pauta-form.component.scss']
})
export class PautaFormComponent implements OnInit {
  pauta: PautaRequestDTO | PautaResponseDTO = { titulo: '', descricao: '' };
  isLoading = false;
  error: string | null = null;
  isEditMode = false;

  config: DynamicTableConfig = {
    title: 'Gerenciar Pauta',
    fields: [
      {
        name: 'titulo',
        label: 'Título',
        type: 'input',
        dataType: 'string',
        required: true,
        maxLength: 100,
        showInTable: false,
        filterable: false,
        disabled: false
      },
      {
        name: 'descricao',
        label: 'Descrição',
        type: 'textarea',
        dataType: 'string',
        required: true,
        maxLength: 500,
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
    private pautaService: PautaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadPauta(+id);
    }
  }

  loadPauta(id: number): void {
    this.isLoading = true;
    this.pautaService.getById(id).subscribe({
      next: (pauta: PautaResponseDTO) => {
        this.pauta = pauta;
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar pauta';
        this.isLoading = false;
      }
    });
  }

  savePauta(data: PautaRequestDTO): void {
    this.isLoading = true;
    const action = this.isEditMode && (this.pauta as PautaResponseDTO).id
      ? this.pautaService.update((this.pauta as PautaResponseDTO).id, data)
      : this.pautaService.create(data);

    action.subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/pautas']);
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao salvar pauta';
        this.isLoading = false;
      }
    });
  }
}
