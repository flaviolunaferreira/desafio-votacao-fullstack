import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface ValidarCpfResponse {
  status: 'ABLE_TO_VOTE' | 'UNABLE_TO_VOTE';
}

@Injectable({
  providedIn: 'root'
})
export class EleitorService {
  private apiUrl = `${environment.apiUrl}/eleitores`;

  constructor(private http: HttpClient) {}

  validarCpf(cpf: string): Observable<ValidarCpfResponse> {
    return this.http.post<ValidarCpfResponse>(`${this.apiUrl}/validar`, { cpf });
  }
}
