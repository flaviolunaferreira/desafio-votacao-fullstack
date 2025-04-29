import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface ValidarCpfResponse {
  status: 'ABLE_TO_VOTE' | 'UNABLE_TO_VOTE';
}

@Injectable({
  providedIn: 'root'
})
export class EleitorService {
  private apiUrl = `${environment.apiUrl}/votos`;

  constructor(private http: HttpClient) {}

  validarCpf(cpf: string): Observable<ValidarCpfResponse> {
    return this.http.post<boolean>(`${this.apiUrl}/validar-cpf`, { cpf }).pipe(
      map((isValid: boolean) => ({
        status: isValid ? 'ABLE_TO_VOTE' : 'UNABLE_TO_VOTE'
      }))
    );
  }
  
}
