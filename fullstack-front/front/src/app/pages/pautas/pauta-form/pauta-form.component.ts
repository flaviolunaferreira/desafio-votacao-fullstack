import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PautaService } from '../../../services/pauta.service';
import { PautaRequestDTO, PautaResponseDTO } from '../../../models/pauta.model';
import { ApiError } from '../../../models/api-error.model';

@Component({
  selector: 'app-pauta-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pauta-form.component.html',
  styleUrls: ['./pauta-form.component.scss']
})
export class PautaFormComponent implements OnInit {
  pautaForm!: ReturnType<FormBuilder['group']>;
  isLoading = false;
  error: string | null = null;
  isEditMode = false;
  pautaId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private pautaService: PautaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
  this.pautaForm = this.fb.group({
    titulo: ['', [Validators.required, Validators.maxLength(100)]],
    descricao: ['', [Validators.required, Validators.maxLength(500)]]
  });

  const id = this.route.snapshot.paramMap.get('id');
  if (id) {
    this.isEditMode = true;
    this.pautaId = +id;
    this.loadPauta(this.pautaId);
  }
  }

  loadPauta(id: number): void {
    this.isLoading = true;
    this.pautaService.getById(id).subscribe({
      next: (pauta: PautaResponseDTO) => {
        this.pautaForm.patchValue(pauta);
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao carregar pauta';
        this.isLoading = false;
      }
    });
  }

  savePauta(): void {
    if (this.pautaForm.invalid) return;
    this.isLoading = true;
    const pautaData: PautaRequestDTO = this.pautaForm.value as PautaRequestDTO;

    const action = this.isEditMode && this.pautaId
      ? this.pautaService.update(this.pautaId, pautaData)
      : this.pautaService.create(pautaData);

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

  cancel(): void {
    this.router.navigate(['/pautas']);
  }
}
