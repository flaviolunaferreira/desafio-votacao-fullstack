import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidatorFn } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { NotificationService } from '../services/notification.service';

export interface FieldConfig {
  name: string;
  label: string;
  type: 'input' | 'textarea' | 'dropdown' | 'date' | 'datetime-local' | 'checkbox' | 'radio' | 'switch';
  dataType: 'string' | 'number' | 'date';
  required?: boolean;
  maxLength?: number;
  minValue?: number;
  pattern?: RegExp;
  mask?: string;
  options?: string[];
  showInTable?: boolean;
  filterable?: boolean;
  disabled: boolean;
  defaultValue?: any;
}

export interface DynamicTableConfig {
  title: string;
  fields: FieldConfig[];
  actions: {
    view: boolean;
    edit: boolean;
    delete: boolean;
  };
}

export function cpfValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const cpf = control.value?.replace(/[^\d]/g, '');
    if (!cpf || cpf.length !== 11) return { invalidCpf: true };

    let sum = 0;
    let remainder;
    for (let i = 1; i <= 9; i++) {
      sum += parseInt(cpf.substring(i - 1, i)) * (11 - i);
    }
    remainder = (sum * 10) % 11;
    if (remainder === 10 || remainder === 11) remainder = 0;
    if (remainder !== parseInt(cpf.substring(9, 10))) return { invalidCpf: true };

    sum = 0;
    for (let i = 1; i <= 10; i++) {
      sum += parseInt(cpf.substring(i - 1, i)) * (12 - i);
    }
    remainder = (sum * 10) % 11;
    if (remainder === 10 || remainder === 11) remainder = 0;
    if (remainder !== parseInt(cpf.substring(10, 11))) return { invalidCpf: true };

    return null;
  };
}

@Component({
  selector: 'app-dynamic-crud',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgxMaskDirective],
  templateUrl: './dynamic-crud.component.html',
  styleUrls: ['./dynamic-crud.component.scss']
})
export class DynamicCrudComponent<T extends Record<string, any>> implements OnInit {
  @Input() config: DynamicTableConfig | null = null;
  @Input() initialData: T[] = [];
  @Input() isLoading = false;
  @Input() error: string | null = null;
  @Output() onCreate = new EventEmitter<T>();
  @Output() onUpdate = new EventEmitter<{ id: number, data: T }>();
  @Output() onRemove = new EventEmitter<number>();
  @Output() onLoadData = new EventEmitter<void>();
  @Output() onView = new EventEmitter<T>();
  @Output() onError = new EventEmitter<string>();

  form: FormGroup;
  isEditMode = false;
  currentEditId: number | null = null;
  showForm = false;
  filteredData: T[] = [];
  filterValues: { [key: string]: any } = {};
  notification: string | null = null;
  hasFilterableFields = false;

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationService
  ) {
    this.form = this.fb.group({});
    this.notificationService.getNotification().subscribe(notification => {
      this.notification = notification;
    });
  }

  ngOnInit(): void {
    if (this.config) {
      this.initForms();
      this.filteredData = [...this.initialData];
      this.hasFilterableFields = this.config.fields.some(f => f.filterable ?? false);
    }
    this.onLoadData.emit();
  }

  private initForms(): void {
    const formGroup: any = {};
    this.config!.fields.forEach(field => {
      const validators = [];
      if (field.required) validators.push(Validators.required);
      if (field.maxLength) validators.push(Validators.maxLength(field.maxLength));
      if (field.pattern) validators.push(Validators.pattern(field.pattern));
      if (field.minValue !== undefined) validators.push(Validators.min(field.minValue));
      if (field.name === 'cpf') validators.push(cpfValidator());
      formGroup[field.name] = [{ value: field.defaultValue || '', disabled: field.disabled }, validators];
    });
    this.form = this.fb.group(formGroup);
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const formData = this.form.value as T;
    if (this.isEditMode && this.currentEditId !== null) {
      this.onUpdate.emit({ id: this.currentEditId, data: formData });
      this.notificationService.showSuccess('Item atualizado com sucesso!');
    } else {
      this.onCreate.emit(formData);
      this.notificationService.showSuccess('Item criado com sucesso!');
    }
    this.resetForm();
  }

  editItem(item: T): void {
    if (!this.hasId(item)) {
      this.onError.emit('Item sem ID não pode ser editado');
      return;
    }
    this.isEditMode = true;
    this.currentEditId = item['id'];
    this.showForm = true;
    this.form.patchValue(item);
  }

  deleteItem(id: number): void {
    this.onRemove.emit(id);
    this.notificationService.showSuccess('Item excluído com sucesso!');
  }

  viewItem(item: T): void {
    this.onView.emit(item);
  }

  resetForm(): void {
    this.form.reset();
    this.isEditMode = false;
    this.currentEditId = null;
    this.showForm = false;
  }

  applyFilter(): void {
    this.filteredData = this.initialData.filter(item => {
      return Object.keys(this.filterValues).every(key => {
        const value = this.filterValues[key];
        if (!value) return true;
        return String(item[key] ?? '').toLowerCase().includes(String(value).toLowerCase());
      });
    });
  }

  updateFilter(fieldName: string, event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.filterValues[fieldName] = value;
    this.applyFilter();
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.resetForm();
  }

  getFieldValue(item: T, fieldName: string): any {
    return item[fieldName] ?? '';
  }

  hasId(item: T): item is T & { id: number } {
    return typeof item['id'] === 'number';
  }
}
