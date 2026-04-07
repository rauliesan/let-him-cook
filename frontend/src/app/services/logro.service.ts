import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/* Logro individual (dentro de UsuarioLogroResponse) */
export interface LogroResponse {
  id: string;
  nombre: string;
  descripcion: string;
  iconoUrl: string | null;
  fechaObtenido: string | null;
}

/* Respuesta de GET /mis-logros (paginada) */
export interface UsuarioLogroResponse {
  id: string;
  logro: LogroResponse;
  fechaObtenido: string;
}

/* Página genérica del backend */
export interface Pagina<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

const API = 'http://localhost:9999';

@Injectable({ providedIn: 'root' })
export class LogroService {

  constructor(private http: HttpClient) {}

  /* Logros conseguidos por el usuario autenticado */
  getMisLogros(pagina = 0, tam = 50): Observable<Pagina<UsuarioLogroResponse>> {
    return this.http.get<Pagina<UsuarioLogroResponse>>(
      `${API}/mis-logros?page=${pagina}&size=${tam}`
    );
  }
}
