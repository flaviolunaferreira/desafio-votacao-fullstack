import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { VotoRequestDTO, VotoResponseDTO, ResultadoResponseDTO, SessaoAbertaDTO } from '../models/voto.model';
import {SessaoAberta} from '../models/sessao-votacao.model';

@Injectable({
  providedIn: 'root'
})
export class VotoService {
  private apiUrl = `${environment.apiUrl}/votos`;

  constructor(private http: HttpClient) {}

  create(voto: VotoRequestDTO): Observable<VotoResponseDTO> {
    return this.http.post<VotoResponseDTO>(this.apiUrl, voto);
  }

  getAll(pautaId?: number): Observable<VotoResponseDTO[]> {
    const url = pautaId ? `${this.apiUrl}?pautaId=${pautaId}` : this.apiUrl;
    return this.http.get<VotoResponseDTO[]>(url);
  }

  getById(id: number): Observable<VotoResponseDTO> {
    return this.http.get<VotoResponseDTO>(`${this.apiUrl}/${id}`);
  }

  update(id: number, voto: VotoRequestDTO): Observable<VotoResponseDTO> {
    return this.http.put<VotoResponseDTO>(`${this.apiUrl}/${id}`, voto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getResultado(pautaId: number): Observable<ResultadoResponseDTO> {
    return this.http.get<ResultadoResponseDTO>(`${this.apiUrl}/resultado/${pautaId}`);
  }

  getSessoesAbertasNaoVotadas(cpf: string): Observable<SessaoAbertaDTO[]> {
    return this.http.get<SessaoAbertaDTO[]>(`${this.apiUrl}/sessoes-abertas?cpf=${cpf}`);
  }

  validarCpf(cpf: string): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/validar-cpf`, cpf);
  }

  listarSessoesAbertasSemVoto(cpf: string): Observable<SessaoAberta[]> {
    return this.http.get<SessaoAberta[]>(`${this.apiUrl}/sessoes-abertas-sem-voto/${cpf}`);
  }

  votar(voto: { pautaId: number; cpf: string; voto: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}`, voto);
  }
}
