import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

/* ═══════════════════════════════════════════
   Interfaces de respuesta del backend
   ═══════════════════════════════════════════ */

export interface ApiResponse {
  id: string;
  nombreServicio: string;
  endpointUrl: string;
  apiKey: string;
}

export interface IaModeloResponse {
  id: string;
  nombreModelo: string;
  apiId: string;
  apiNombreServicio: string;
  apiEndpointUrl: string;
  apiKey: string;
}

export interface LogroAdminResponse {
  id: string;
  nombre: string;
  descripcion: string;
  iconoUrl: string | null;
  fechaObtenido: string | null;
}

export interface RecompensaAdminResponse {
  id: string;
  nombre: string;
  descripcion: string;
  probabilidad: number;
  fechaObtenida: string | null;
}


export interface TipoComidaResponse {
  id: string;
  nombre: string;
  descripcion: string | null;
  iconoUrl: string | null;
  colorHex: string | null;
}

export interface UsuarioAdminResponse {
  id: string;
  nombre: string;
  email: string;
  puntos: number;
  nivel: number;
  fechaInscripcion: string;
  fotoUrl: string | null;
  rol: 'ADMIN' | 'USER';
}

export interface RecetaAdminResponse {
  id: string;
  nombre: string;
  descripcion: string | null;
  ingredientes: string;
  instrucciones: string | null;
  tiempoPreparacion: number | null;
  dificultad: 'BAJA' | 'MEDIA' | 'ALTA' | null;
  calorias: number | null;
  alergenos: string | null;
  esPublica: boolean;
  imagenUrl: string | null;
  fechaCreacion: string;
  tipoComidaId: string | null;
  tipoComidaNombre: string | null;
  usuarioCreadorId: string;
  usuarioCreadorNombre: string;
  totalLikes: number;
}

export interface Pagina<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

/* ═══════════════════════════════════════════
   Interfaces de request
   ═══════════════════════════════════════════ */

export interface ApiRequest {
  nombreServicio: string;
  endpointUrl?: string;
  apiKey: string;
}

export interface IaModeloRequest {
  nombreModelo: string;
  apiId: string;
}

export interface LogroRequest {
  nombre: string;
  descripcion?: string;
  iconoUrl?: string;
}

export interface RecompensaRequest {
  nombre: string;
  descripcion?: string;
  probabilidad?: number;
}


export interface TipoComidaRequest {
  nombre: string;
  descripcion?: string;
  iconoUrl?: string;
}

export interface UsuarioRequest {
  nombre: string;
  email: string;
  password: string;
  fotoUrl?: string;
}

export interface RecetaRequest {
  nombre: string;
  descripcion?: string;
  ingredientes: string;
  instrucciones?: string;
  tiempoPreparacion?: number;
  dificultad?: 'BAJA' | 'MEDIA' | 'ALTA';
  calorias?: number;
  alergenos?: string;
  esPublica?: boolean;
  imagenUrl?: string;
  tipoComidaId?: string;
}

const API = 'http://localhost:9999';

/**
 * Servicio consolidado para todas las operaciones de administración.
 * Agrupa los CRUDs de: APIs, Modelos IA, Logros, Recompensas,
 * Supermercados, Tipos de Comida, Usuarios y Recetas.
 */
@Injectable({ providedIn: 'root' })
export class AdminService {

  constructor(private http: HttpClient) {}

  /* ── APIs (/admin/apis) ── */
  getApis(): Observable<ApiResponse[]> {
    return this.http.get<ApiResponse[]>(`${API}/admin/apis`);
  }
  crearApi(dto: ApiRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${API}/admin/apis`, dto);
  }
  actualizarApi(id: string, dto: ApiRequest): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${API}/admin/apis/${id}`, dto);
  }
  eliminarApi(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/admin/apis/${id}`);
  }

  /* ── Modelos IA (/admin/ia-modelos) ── */
  getIaModelos(): Observable<IaModeloResponse[]> {
    return this.http.get<IaModeloResponse[]>(`${API}/admin/ia-modelos`);
  }
  crearIaModelo(dto: IaModeloRequest): Observable<IaModeloResponse> {
    return this.http.post<IaModeloResponse>(`${API}/admin/ia-modelos`, dto);
  }
  actualizarIaModelo(id: string, dto: IaModeloRequest): Observable<IaModeloResponse> {
    return this.http.put<IaModeloResponse>(`${API}/admin/ia-modelos/${id}`, dto);
  }
  eliminarIaModelo(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/admin/ia-modelos/${id}`);
  }

  /* ── Logros (/admin/logros) ── */
  getLogros(): Observable<LogroAdminResponse[]> {
    return this.http.get<LogroAdminResponse[]>(`${API}/admin/logros`);
  }
  crearLogro(dto: LogroRequest): Observable<LogroAdminResponse> {
    return this.http.post<LogroAdminResponse>(`${API}/admin/logros`, dto);
  }
  actualizarLogro(id: string, dto: LogroRequest): Observable<LogroAdminResponse> {
    return this.http.put<LogroAdminResponse>(`${API}/admin/logros/${id}`, dto);
  }
  eliminarLogro(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/admin/logros/${id}`);
  }

  /* ── Recompensas (/admin/recompensas) ── */
  getRecompensas(): Observable<RecompensaAdminResponse[]> {
    return this.http.get<RecompensaAdminResponse[]>(`${API}/admin/recompensas`);
  }
  crearRecompensa(dto: RecompensaRequest): Observable<RecompensaAdminResponse> {
    return this.http.post<RecompensaAdminResponse>(`${API}/admin/recompensas`, dto);
  }
  actualizarRecompensa(id: string, dto: RecompensaRequest): Observable<RecompensaAdminResponse> {
    return this.http.put<RecompensaAdminResponse>(`${API}/admin/recompensas/${id}`, dto);
  }
  eliminarRecompensa(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/admin/recompensas/${id}`);
  }


  /* ── Tipos de Comida (/tipos-comida) ── */
  getTiposComida(): Observable<TipoComidaResponse[]> {
    return this.http.get<TipoComidaResponse[]>(`${API}/tipos-comida`);
  }
  crearTipoComida(dto: TipoComidaRequest): Observable<TipoComidaResponse> {
    return this.http.post<TipoComidaResponse>(`${API}/tipos-comida`, dto);
  }
  actualizarTipoComida(id: string, dto: TipoComidaRequest): Observable<TipoComidaResponse> {
    return this.http.put<TipoComidaResponse>(`${API}/tipos-comida/${id}`, dto);
  }
  eliminarTipoComida(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/tipos-comida/${id}`);
  }

  /* ── Usuarios (/usuarios) ── */
  getUsuarios(): Observable<UsuarioAdminResponse[]> {
    return this.http.get<UsuarioAdminResponse[]>(`${API}/usuarios`);
  }
  getUsuariosPaginado(nombre?: string, page = 0, size = 10): Observable<Pagina<UsuarioAdminResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (nombre) params = params.set('nombre', nombre);
    return this.http.get<Pagina<UsuarioAdminResponse>>(`${API}/usuarios/busqueda`, { params });
  }
  crearUsuario(dto: UsuarioRequest): Observable<UsuarioAdminResponse> {
    return this.http.post<UsuarioAdminResponse>(`${API}/usuarios`, dto);
  }
  actualizarUsuario(id: string, dto: UsuarioRequest): Observable<UsuarioAdminResponse> {
    return this.http.put<UsuarioAdminResponse>(`${API}/usuarios/${id}`, dto);
  }
  eliminarUsuario(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/usuarios/${id}`);
  }

  /* ── Recetas (/recetas) ── */
  getRecetas(): Observable<RecetaAdminResponse[]> {
    return this.http.get<RecetaAdminResponse[]>(`${API}/recetas/todas`);
  }
  actualizarReceta(id: string, dto: RecetaRequest): Observable<RecetaAdminResponse> {
    return this.http.put<RecetaAdminResponse>(`${API}/recetas/${id}`, dto);
  }
  eliminarReceta(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/recetas/${id}`);
  }
}
