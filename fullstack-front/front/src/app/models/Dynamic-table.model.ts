export interface DynamicTableConfig {
  title: string;
  fields: FieldConfig[];
  actions: {
    create?: boolean;
    edit?: boolean;
    delete?: boolean;
    view?: boolean;
  };
  filterable?: boolean;
}

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
  options?: { value: string; label: string }[];
  showInTable?: boolean;
  filterable?: boolean;
  disabled?: boolean;
  defaultValue?: any;
}
