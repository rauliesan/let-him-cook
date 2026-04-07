import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/* Estructura que devuelve GET /usuarios/me */
export interface UsuarioResponse {
  id: string;
  nombre: string;
  email: string;
  puntos: number;
  nivel: number;
  fechaInscripcion: string;
  fotoUrl: string | null;
  rol: 'ADMIN' | 'USER';
  iaModeloSeleccionadoId: string | null;
  iaModeloSeleccionadoNombre: string | null;
}

const API = 'http://localhost:9999';

@Injectable({ providedIn: 'root' })
export class UsuarioService {

  constructor(private http: HttpClient) {}

  /* Perfil del usuario autenticado (requiere JWT) */
  getMe(): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>(`${API}/usuarios/me`);
  }

  /* Lista paginada para saber el total de usuarios registrados */
  contarUsuarios(): Observable<{ totalElements: number }> {
    return this.http.get<{ totalElements: number }>(`${API}/usuarios/busqueda?size=1`);
  }
}
