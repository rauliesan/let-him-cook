import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/* Recompensa individual */
export interface RecompensaResponse {
  id: string;
  nombre: string;
  descripcion: string;
  probabilidad: number;
  fechaObtenida: string | null;
}

/* Entrada de recompensa ganada por el usuario */
export interface UsuarioRecompensaResponse {
  id: string;
  recompensa: RecompensaResponse;
  fechaObtenida: string;
}

/* Página genérica */
export interface Pagina<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

const API = 'http://localhost:9999';

@Injectable({ providedIn: 'root' })
export class RecompensaService {

  constructor(private http: HttpClient) {}

  /* Historial de recompensas del usuario autenticado */
  getMisRecompensas(pagina = 0, tam = 20): Observable<Pagina<UsuarioRecompensaResponse>> {
    return this.http.get<Pagina<UsuarioRecompensaResponse>>(
      `${API}/mis-recompensas?page=${pagina}&size=${tam}`
    );
  }

  /* Registrar que el usuario ganó una recompensa */
  concederRecompensa(recompensaId: string): Observable<UsuarioRecompensaResponse> {
    return this.http.post<UsuarioRecompensaResponse>(`${API}/mis-recompensas`, {
      recompensaId,
    });
  }
}
