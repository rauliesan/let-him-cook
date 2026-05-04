import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { ForoService, PostResponse, PostComentarioResponse } from '../../services/foro.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-foro',
  imports: [CommonModule, FormsModule, RouterLink, Revela],
  templateUrl: './foro.html',
  styleUrl: './foro.scss',
})
export class Foro implements OnInit {

  posts        = signal<PostResponse[]>([]);
  cargando     = signal(true);
  errorCarga   = signal<string | null>(null);

  /* Post expandido (ver comentarios) */
  postAbierto  = signal<string | null>(null);
  comentariosDelPost = signal<PostComentarioResponse[]>([]);
  cargandoComentarios = signal(false);

  /* Formulario nuevo post */
  mostrarFormPost = signal(false);
  nuevoTitulo   = '';
  nuevoContenido = '';
  enviandoPost  = signal(false);
  errorPost     = signal<string | null>(null);

  /* Formulario comentario */
  nuevoComentario = '';
  enviandoComentario = signal(false);

  constructor(
    private foroService: ForoService,
    public  auth: AuthService,
  ) {}

  ngOnInit() {
    this.cargarPosts();
  }

  cargarPosts() {
    this.cargando.set(true);
    this.errorCarga.set(null);
    this.foroService.getPosts().subscribe({
      next: (lista) => {
        this.posts.set(lista);
        this.cargando.set(false);
      },
      error: () => {
        this.errorCarga.set('No se pudo cargar el foro.');
        this.cargando.set(false);
      },
    });
  }

  /* Abre o cierra un post para ver sus comentarios */
  togglePost(postId: string) {
    if (this.postAbierto() === postId) {
      this.postAbierto.set(null);
      this.comentariosDelPost.set([]);
      return;
    }
    this.postAbierto.set(postId);
    this.nuevoComentario = '';
    this.cargarComentarios(postId);
  }

  cargarComentarios(postId: string) {
    this.cargandoComentarios.set(true);
    this.foroService.getComentarios(postId).subscribe({
      next: (lista) => {
        this.comentariosDelPost.set(lista);
        this.cargandoComentarios.set(false);
      },
      error: () => this.cargandoComentarios.set(false),
    });
  }

  /* Envía un nuevo post */
  publicarPost() {
    if (!this.nuevoTitulo.trim() || !this.nuevoContenido.trim()) {
      this.errorPost.set('El título y el contenido son obligatorios.');
      return;
    }
    this.enviandoPost.set(true);
    this.errorPost.set(null);
    this.foroService.crearPost(this.nuevoTitulo.trim(), this.nuevoContenido.trim()).subscribe({
      next: (post) => {
        this.posts.update(lista => [post, ...lista]);
        this.nuevoTitulo = '';
        this.nuevoContenido = '';
        this.mostrarFormPost.set(false);
        this.enviandoPost.set(false);
      },
      error: () => {
        this.errorPost.set('Error al publicar. Inténtalo de nuevo.');
        this.enviandoPost.set(false);
      },
    });
  }

  /* Envía un comentario al post abierto */
  publicarComentario() {
    const postId = this.postAbierto();
    if (!postId || !this.nuevoComentario.trim()) return;

    this.enviandoComentario.set(true);
    this.foroService.comentar(postId, this.nuevoComentario.trim()).subscribe({
      next: (comentario) => {
        this.comentariosDelPost.update(lista => [...lista, comentario]);
        /* Actualizar contador del post en la lista */
        this.posts.update(lista =>
          lista.map(p => p.id === postId
            ? { ...p, totalComentarios: p.totalComentarios + 1 }
            : p
          )
        );
        this.nuevoComentario = '';
        this.enviandoComentario.set(false);
      },
      error: () => this.enviandoComentario.set(false),
    });
  }

  /* Elimina un post propio */
  eliminarPost(postId: string, event: Event) {
    event.stopPropagation();
    this.foroService.eliminarPost(postId).subscribe({
      next: () => this.posts.update(lista => lista.filter(p => p.id !== postId)),
    });
  }

  /* Elimina un comentario propio */
  eliminarComentario(comentarioId: string) {
    const postId = this.postAbierto();
    if (!postId) return;
    this.foroService.eliminarComentario(postId, comentarioId).subscribe({
      next: () => {
        this.comentariosDelPost.update(lista => lista.filter(c => c.id !== comentarioId));
        this.posts.update(lista =>
          lista.map(p => p.id === postId
            ? { ...p, totalComentarios: Math.max(0, p.totalComentarios - 1) }
            : p
          )
        );
      },
    });
  }

  /* Formatea fecha relativa */
  formatearFecha(iso: string): string {
    const now = new Date();
    const fecha = new Date(iso);
    const diff = Math.floor((now.getTime() - fecha.getTime()) / 1000);
    if (diff < 60)  return 'ahora mismo';
    if (diff < 3600) return `hace ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `hace ${Math.floor(diff / 3600)}h`;
    if (diff < 86400 * 7) return `hace ${Math.floor(diff / 86400)} días`;
    return fecha.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
  }

  /* Iniciales del autor para el avatar */
  iniciales(nombre: string): string {
    return nombre.split(' ').slice(0, 2).map(p => p[0]).join('').toUpperCase();
  }

  /* Cierra el formulario de nuevo post */
  cerrarForm() {
    this.mostrarFormPost.set(false);
    this.nuevoTitulo = '';
    this.nuevoContenido = '';
    this.errorPost.set(null);
  }

  /* Nombre del usuario en sesión — se usa para detectar si eres el autor */
  get miNombre(): string {
    return this.auth.sesion()?.nombre ?? '';
  }

  esAutor(nombreUsuario: string): boolean {
    return this.auth.estaAutenticado() && nombreUsuario === this.miNombre;
  }

  /* Paleta de colores para avatares — determinista según el nombre */
  private readonly AVATAR_PALETTE = [
    '#C13E28','#2563eb','#059669','#d97706',
    '#7c3aed','#db2777','#0891b2','#65a30d',
  ];

  avatarColor(name: string): string {
    if (!name) return this.AVATAR_PALETTE[0];
    let h = 0;
    for (let i = 0; i < name.length; i++) h = name.charCodeAt(i) + ((h << 5) - h);
    return this.AVATAR_PALETTE[Math.abs(h) % this.AVATAR_PALETTE.length];
  }

  /* Límites de caracteres */
  readonly MAX_TITULO    = 120;
  readonly MAX_CONTENIDO = 2000;
}
