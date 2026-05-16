import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/* Estructura que devuelve el backend para cada receta */
export interface RecetaResponse {
  id: string;
  nombre: string;
  descripcion: string | null;
  ingredientes: string;
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

  constructor(private http: HttpClient) {}

  /* Lista todas las recetas (sin paginación — endpoint público) */
  getTodas(): Observable<RecetaResponse[]> {
    return this.http.get<RecetaResponse[]>(`${API}/recetas/todas`);
  }

  /* Lista paginada de recetas */
  getPaginado(pagina = 0, tam = 12): Observable<Pagina<RecetaResponse>> {
    return this.http.get<Pagina<RecetaResponse>>(
      `${API}/recetas/pagina?page=${pagina}&size=${tam}&sort=fechaCreacion,desc`
    );
  }

  /* Lista todas las categorías (sin paginación — endpoint público) */
  getCategorias(): Observable<TipoComidaResponse[]> {
    return this.http.get<TipoComidaResponse[]>(`${API}/tipos-comida`);
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
    let url = `${API}/recetas/busqueda?termino=${termino}&page=${pagina}&size=${tam}`;
    if (dificultad && dificultad !== 'Cualquiera') {
      url += `&dificultad=${dificultad}`;
    }
    if (categorias && categorias.length > 0) {
      url += `&categorias=${categorias.join(',')}`;
    }
    return this.http.get<Pagina<RecetaResponse>>(url);
  }
}
