import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PautaRequestDTO, PautaResponseDTO } from '../models/pauta.model';

@Injectable({
  providedIn: 'root'
})
export class PautaService {
  private apiUrl = `${environment.apiUrl}/pautas`;

  constructor(private http: HttpClient) {}

  create(pauta: PautaRequestDTO): Observable<PautaResponseDTO> {
    return this.http.post<PautaResponseDTO>(this.apiUrl, pauta);
  }

  getAll(): Observable<PautaResponseDTO[]> {
    return this.http.get<PautaResponseDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<PautaResponseDTO> {
    return this.http.get<PautaResponseDTO>(`${this.apiUrl}/${id}`);
  }

  update(id: number, pauta: PautaRequestDTO): Observable<PautaResponseDTO> {
    return this.http.put<PautaResponseDTO>(`${this.apiUrl}/${id}`, pauta);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
