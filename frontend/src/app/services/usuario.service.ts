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
  /** true si el usuario tiene su propia API key guardada */
  iaCustomConfigured: boolean;
  /** Endpoint de la IA personalizada (nunca se expone la key) */
  iaCustomEndpoint: string | null;
  /** Nombre del modelo de la IA personalizada */
  iaCustomModelo: string | null;
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

  /** Guarda configuración de IA personalizada (BYOAI) en el perfil */
  guardarIaConfig(apiKey: string, endpoint?: string, modelo?: string): Observable<UsuarioResponse> {
    return this.http.put<UsuarioResponse>(`${API}/usuarios/me/ia-config`, {
      apiKey,
      endpoint: endpoint ?? null,
      modelo: modelo ?? null,
    });
  }

  /** Elimina la configuración personalizada — vuelve al DeepSeek de la app */
  eliminarIaConfig(): Observable<UsuarioResponse> {
    return this.http.delete<UsuarioResponse>(`${API}/usuarios/me/ia-config`);
  }

  /** Actualiza únicamente la foto de perfil del usuario autenticado */
  actualizarFoto(fotoUrl: string): Observable<UsuarioResponse> {
    return this.http.patch<UsuarioResponse>(`${API}/usuarios/me/foto`, { fotoUrl });
  }

  /** Deduce puntos por una tirada en la ruleta */
  cobrarTirada(coste = 100): Observable<void> {
    return this.http.post<void>(`${API}/usuarios/me/cobrar-tirada?coste=${coste}`, {});
  }

  getById(id: string): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>(`${API}/usuarios/${id}`);
  }

  agregarAmigo(id: string): Observable<void> {
    return this.http.post<void>(`${API}/usuarios/${id}/amigos`, {});
  }

  eliminarAmigo(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/usuarios/${id}/amigos`);
  }

  esAmigo(id: string): Observable<{ esAmigo: boolean }> {
    return this.http.get<{ esAmigo: boolean }>(`${API}/usuarios/${id}/es-amigo`);
  }

  getMisAmigos(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(`${API}/usuarios/me/amigos`);
  }
}
