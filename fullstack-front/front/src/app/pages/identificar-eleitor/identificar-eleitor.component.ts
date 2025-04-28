import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxMaskDirective } from 'ngx-mask';
import { EleitorService } from '../../services/eleitor.service';
import { ApiError } from '../../models/api-error.model';

@Component({
  selector: 'app-identificar-eleitor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NgxMaskDirective],
  templateUrl: './identificar-eleitor.component.html',
  styleUrls: ['./identificar-eleitor.component.scss']
})
export class IdentificarEleitorComponent {
  form: FormGroup;
  isLoading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private eleitorService: EleitorService,
    private router: Router
  ) {
    this.form = this.fb.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isLoading = true;
    this.error = null;
    const cpf = this.form.value.cpf.replace(/[^\d]/g, '');

    this.eleitorService.validarCpf(cpf).subscribe({
      next: (response) => {
        if (response.status === 'ABLE_TO_VOTE') {
          this.router.navigate(['/votos/novo'], { queryParams: { cpf } });
        } else {
          this.error = 'CPF não habilitado para votação';
        }
        this.isLoading = false;
      },
      error: (err: ApiError) => {
        this.error = err.message || 'Erro ao validar CPF';
        this.isLoading = false;
      }
    });
  }
}
