import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

/* Estructura que devuelve el backend para cada receta */
export interface RecetaResponse {
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
  tipoComidaNombre: string | null;     /* categoría principal (determina color) */
  tipoComida2Nombre: string | null;
  tipoComida3Nombre: string | null;
  usuarioCreadorId: string;
  usuarioCreadorNombre: string;
  totalLikes: number;
}

/* Estructura de TipoComida (categoría) */
export interface TipoComidaResponse {
  id: string;
  nombre: string;
  descripcion: string | null;
  iconoUrl: string | null;   /* emoji almacenado en la BD */
  colorHex: string | null;   /* color base de la categoría, ej. "#C13E28" */
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
export class RecetaService {

  // caché en memoria, TTL 5 minutos
  private readonly CACHE_TTL = 5 * 60 * 1000;
  private cacheRecetas: RecetaResponse[] | null = null;
  private cacheCategorias: TipoComidaResponse[] | null = null;
  private cacheRecetasTiempo = 0;

  constructor(private http: HttpClient) {}

  /** Invalida la caché de recetas (llamar al crear/editar/borrar una receta) */
  invalidarCacheRecetas() { this.cacheRecetas = null; }

  /* Lista todas las recetas, caché 5 min */
  getTodas(): Observable<RecetaResponse[]> {
    if (this.cacheRecetas && Date.now() - this.cacheRecetasTiempo < this.CACHE_TTL) {
      return of(this.cacheRecetas);
    }
    return this.http.get<RecetaResponse[]>(`${API}/recetas/todas`).pipe(
      tap(r => { this.cacheRecetas = r; this.cacheRecetasTiempo = Date.now(); })
    );
  }

  /* Lista paginada de recetas */
  getPaginado(pagina = 0, tam = 12): Observable<Pagina<RecetaResponse>> {
    return this.http.get<Pagina<RecetaResponse>>(
      `${API}/recetas/pagina?page=${pagina}&size=${tam}&sort=fechaCreacion,desc`
    );
  }

  /* Lista todas las categorías, caché permanente en sesión (raramente cambian) */
  getCategorias(): Observable<TipoComidaResponse[]> {
    if (this.cacheCategorias) return of(this.cacheCategorias);
    return this.http.get<TipoComidaResponse[]>(`${API}/tipos-comida`).pipe(
      tap(r => { this.cacheCategorias = r; })
    );
  }

  /* Obtiene una receta por su ID */
  getPorId(id: string): Observable<RecetaResponse> {
    return this.http.get<RecetaResponse>(`${API}/recetas/${id}`);
  }

  /* Recetas creadas por el usuario autenticado */
  getMisRecetas(): Observable<RecetaResponse[]> {
    return this.http.get<RecetaResponse[]>(`${API}/recetas/mis-recetas`);
  }

  /* Búsqueda dinámica en el servidor */
  buscarDinamico(termino: string, dificultad?: string, categorias?: string[], pagina = 0, tam = 12): Observable<Pagina<RecetaResponse>> {
    let url = `${API}/recetas/busqueda?termino=${encodeURIComponent(termino)}&page=${pagina}&size=${tam}`;
    if (dificultad && dificultad !== 'Cualquiera') {
      url += `&dificultad=${dificultad}`;
    }
    if (categorias && categorias.length > 0) {
      url += `&categorias=${categorias.join(',')}`;
    }
    return this.http.get<Pagina<RecetaResponse>>(url);
  }

  /* Registra que se ha completado la receta y devuelve las monedas ganadas (-1 = ya cocinada antes) */
  completarReceta(id: string): Observable<number> {
    return this.http.post<number>(`${API}/recetas/${id}/completar`, {});
  }

  /* Comprueba si el usuario ya completó esta receta */
  haCompletado(id: string): Observable<boolean> {
    return this.http.get<boolean>(`${API}/recetas/${id}/completada`);
  }

  /* Recetas públicas de los amigos del usuario autenticado */
  getRecetasDeAmigos(): Observable<RecetaResponse[]> {
    return this.http.get<RecetaResponse[]>(`${API}/recetas/amigos`);
  }

  /* Recetas públicas de un usuario concreto */
  getRecetasDeUsuario(usuarioId: string): Observable<RecetaResponse[]> {
    return this.http.get<RecetaResponse[]>(`${API}/recetas/usuario/${usuarioId}`);
  }
}
