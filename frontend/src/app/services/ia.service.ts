import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GenerarRecetasRequest {
  ingredientes: string[];
  preferencias?: string;
}

export interface RecetaSugerencia {
  nombre: string;
  descripcion: string;
  ingredientes: string;
  instrucciones: string;
  tiempoPreparacion: number;
  dificultad: 'BAJA' | 'MEDIA' | 'ALTA';
  calorias: number;
  alergenos: string;
  categoria: string;
  categoriaEmoji: string;
  categoriaColor: string;
}

export interface PublicarRecetaIaRequest {
  nombre: string;
  descripcion: string;
  ingredientes: string;
  tiempoPreparacion?: number;
  dificultad?: 'BAJA' | 'MEDIA' | 'ALTA';
  calorias?: number;
  alergenos?: string;
  esPublica: boolean;
  categoriaNombre?: string;
  categoriaEmoji?: string;
  categoriaColor?: string;
  iaModeloId?: string;
}

export interface PublicarRecetaResponse {
  receta: { id: string; nombre: string; descripcion: string; tipoComidaNombre: string | null };
  puntosGanados: number;
  nuevosTotalPuntos: number;
}

const API = 'http://localhost:9999';

@Injectable({ providedIn: 'root' })
export class IaService {
  constructor(private http: HttpClient) {}

  generarSugerencias(req: GenerarRecetasRequest): Observable<RecetaSugerencia[]> {
    return this.http.post<RecetaSugerencia[]>(`${API}/ia/generar`, req);
  }

  publicarReceta(req: PublicarRecetaIaRequest): Observable<PublicarRecetaResponse> {
    return this.http.post<PublicarRecetaResponse>(`${API}/ia/publicar`, req);
  }

  actualizarIaModelo(iaModeloId: string | null): Observable<any> {
    const params = iaModeloId ? `?iaModeloId=${iaModeloId}` : '';
    return this.http.put(`${API}/usuarios/me/ia-modelo${params}`, {});
  }
}
