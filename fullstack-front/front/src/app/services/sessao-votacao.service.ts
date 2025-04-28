import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SessaoVotacaoRequestDTO, SessaoVotacaoResponseDTO } from '../models/sessao-votacao.model';

@Injectable({
  providedIn: 'root'
})
export class SessaoVotacaoService {
  private apiUrl = `${environment.apiUrl}/sessoes`;

  constructor(private http: HttpClient) {}

  create(sessao: SessaoVotacaoRequestDTO): Observable<SessaoVotacaoResponseDTO> {
    return this.http.post<SessaoVotacaoResponseDTO>(this.apiUrl, sessao);
  }

  getAll(): Observable<SessaoVotacaoResponseDTO[]> {
    return this.http.get<SessaoVotacaoResponseDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<SessaoVotacaoResponseDTO> {
    return this.http.get<SessaoVotacaoResponseDTO>(`${this.apiUrl}/${id}`);
  }

  update(id: number, sessao: SessaoVotacaoRequestDTO): Observable<SessaoVotacaoResponseDTO> {
    return this.http.put<SessaoVotacaoResponseDTO>(`${this.apiUrl}/${id}`, sessao);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
