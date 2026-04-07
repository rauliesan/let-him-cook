import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PostResponse {
  id: string;
  titulo: string;
  contenido: string;
  imagenUrl: string | null;
  fechaCreacion: string;
  totalComentarios: number;
  usuarioId: string;
  usuarioNombre: string;
  usuarioFotoUrl: string | null;
  recetaVinculadaId: string | null;
  recetaVinculadaNombre: string | null;
}

export interface PostComentarioResponse {
  id: string;
  contenido: string;
  fechaCreacion: string;
  usuarioId: string;
  usuarioNombre: string;
  usuarioFotoUrl: string | null;
  postId: string;
}

const API = 'http://localhost:9999';

@Injectable({ providedIn: 'root' })
export class ForoService {

  constructor(private http: HttpClient) {}

  /* Lista todos los posts del foro (público) */
  getPosts(): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${API}/posts`);
  }

  /* Detalle de un post */
  getPost(id: string): Observable<PostResponse> {
    return this.http.get<PostResponse>(`${API}/posts/${id}`);
  }

  /* Crea un nuevo post (requiere auth) */
  crearPost(titulo: string, contenido: string, imagenUrl?: string, recetaVinculadaId?: string): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${API}/posts`, { titulo, contenido, imagenUrl, recetaVinculadaId });
  }

  /* Elimina un post (requiere auth) */
  eliminarPost(id: string): Observable<void> {
    return this.http.delete<void>(`${API}/posts/${id}`);
  }

  /* Comentarios de un post (público) */
  getComentarios(postId: string): Observable<PostComentarioResponse[]> {
    return this.http.get<PostComentarioResponse[]>(`${API}/posts/${postId}/comentarios`);
  }

  /* Comenta en un post (requiere auth) */
  comentar(postId: string, contenido: string): Observable<PostComentarioResponse> {
    return this.http.post<PostComentarioResponse>(`${API}/posts/${postId}/comentarios`, { contenido });
  }

  /* Elimina un comentario (requiere auth) */
  eliminarComentario(postId: string, comentarioId: string): Observable<void> {
    return this.http.delete<void>(`${API}/posts/${postId}/comentarios/${comentarioId}`);
  }
}
