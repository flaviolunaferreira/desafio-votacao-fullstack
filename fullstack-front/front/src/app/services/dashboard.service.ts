import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardResumoDTO, ParticipacaoSessaoDTO, TendenciaVotosDTO } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/v1/dashboard';

  constructor(private http: HttpClient) {}

  getResumo(): Observable<DashboardResumoDTO> {
    return this.http.get<DashboardResumoDTO>(`${this.apiUrl}/resumo`);
  }

  getParticipacaoSessoes(): Observable<ParticipacaoSessaoDTO[]> {
    return this.http.get<ParticipacaoSessaoDTO[]>(`${this.apiUrl}/sessoes/participacao`);
  }

  getTendenciaVotos(inicio?: string, fim?: string, granularidade?: string): Observable<TendenciaVotosDTO[]> {
    let params = new HttpParams();
    if (inicio) params = params.set('inicio', inicio);
    if (fim) params = params.set('fim', fim);
    if (granularidade) params = params.set('granularidade', granularidade);
    return this.http.get<TendenciaVotosDTO[]>(`${this.apiUrl}/votos/tendencia`, { params });
  }
}
