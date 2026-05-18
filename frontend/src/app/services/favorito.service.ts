import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FavoritoResponse {
  id: string;
  recetaId: string;
  recetaNombre: string;
  fechaAgregada: string;
}

const API = 'http://localhost:9999';

@Injectable({ providedIn: 'root' })
export class FavoritoService {

  constructor(private http: HttpClient) { }

  /* Lista los favoritos del usuario autenticado */
  getMisFavoritos(): Observable<FavoritoResponse[]> {
    return this.http.get<FavoritoResponse[]>(`${API}/favoritos-recetas/mis-favoritos/todos`);
  }

  /* Agrega una receta a favoritos */
  agregar(recetaId: string): Observable<FavoritoResponse> {
    return this.http.post<FavoritoResponse>(`${API}/favoritos-recetas`, { recetaId });
  }

  /* Elimina una receta de favoritos */
  eliminar(recetaId: string): Observable<void> {
    return this.http.delete<void>(`${API}/favoritos-recetas/${recetaId}`);
  }
}
