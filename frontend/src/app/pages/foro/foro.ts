import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { ForoService, PostResponse, PostComentarioResponse } from '../../services/foro.service';
import { AuthService } from '../../services/auth.service';
import { RecetaService, RecetaResponse } from '../../services/receta.service';

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

  /* Imagen y receta adjunta al nuevo post */
  nuevoPostImagen              = signal<string | null>(null);
  recetaSeleccionadaPost       = signal<RecetaResponse | null>(null);
  mostrarBuscadorRecetaPost    = signal(false);
  busquedaRecetaPost           = '';
  resultadosBusquedaPost       = signal<RecetaResponse[]>([]);
  buscandoRecetaPost           = signal(false);
  private debounceTimerPost: any;

  /* Formulario comentario */
  nuevoComentario = '';
  enviandoComentario = signal(false);

  /* Receta adjunta al comentario */
  busquedaRecetaComentario = '';
  resultadosBusqueda = signal<RecetaResponse[]>([]);
  recetaSeleccionadaComentario = signal<RecetaResponse | null>(null);
  mostrarBuscadorReceta = signal(false);
  buscandoReceta = signal(false);
  private debounceTimer: any;

  constructor(
    private foroService: ForoService,
    public  auth: AuthService,
    private recetaService: RecetaService,
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
    this.recetaSeleccionadaComentario.set(null);
    this.mostrarBuscadorReceta.set(false);
    this.busquedaRecetaComentario = '';
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
    const imagenUrl = this.nuevoPostImagen() ?? undefined;
    const recetaId  = this.recetaSeleccionadaPost()?.id;
    this.foroService.crearPost(
      this.nuevoTitulo.trim(), this.nuevoContenido.trim(), imagenUrl, recetaId
    ).subscribe({
      next: (post) => {
        this.posts.update(lista => [post, ...lista]);
        this.nuevoTitulo = '';
        this.nuevoContenido = '';
        this.nuevoPostImagen.set(null);
        this.recetaSeleccionadaPost.set(null);
        this.mostrarBuscadorRecetaPost.set(false);
        this.mostrarFormPost.set(false);
        this.enviandoPost.set(false);
      },
      error: () => {
        this.errorPost.set('Error al publicar. Inténtalo de nuevo.');
        this.enviandoPost.set(false);
      },
    });
  }

  /* Imagen para el nuevo post */
  onImagenPostChange(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      // Comprimir a JPEG 0.75 via canvas
      const img = new Image();
      img.onload = () => {
        const MAX = 900;
        let w = img.width, h = img.height;
        if (w > MAX || h > MAX) {
          if (w > h) { h = Math.round(h * MAX / w); w = MAX; }
          else { w = Math.round(w * MAX / h); h = MAX; }
        }
        const canvas = document.createElement('canvas');
        canvas.width = w; canvas.height = h;
        canvas.getContext('2d')!.drawImage(img, 0, 0, w, h);
        this.nuevoPostImagen.set(canvas.toDataURL('image/jpeg', 0.75));
      };
      img.src = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  quitarImagenPost() { this.nuevoPostImagen.set(null); }

  /* Carga las recetas propias del usuario para mostrar por defecto */
  private cargarRecetasPropias(
    destino: (r: RecetaResponse[]) => void,
    cargando: (v: boolean) => void
  ) {
    const uid = this.auth.sesion()?.usuarioId;
    if (!uid) return;
    cargando(true);
    this.recetaService.getRecetasDeUsuario(uid).subscribe({
      next: r => { destino(r.slice(0, 6)); cargando(false); },
      error: () => cargando(false),
    });
  }

  /* Abre el buscador del post y precarga las recetas propias */
  toggleBuscadorRecetaPost() {
    const abriendo = !this.mostrarBuscadorRecetaPost();
    this.mostrarBuscadorRecetaPost.set(abriendo);
    if (abriendo && !this.busquedaRecetaPost.trim()) {
      this.cargarRecetasPropias(
        r => this.resultadosBusquedaPost.set(r),
        v => this.buscandoRecetaPost.set(v)
      );
    }
  }

  /* Búsqueda de recetas para adjuntar al post */
  onBusquedaRecetaPostInput(event: Event) {
    const val = (event.target as HTMLInputElement).value;
    this.busquedaRecetaPost = val;
    clearTimeout(this.debounceTimerPost);
    if (!val.trim()) {
      this.cargarRecetasPropias(
        r => this.resultadosBusquedaPost.set(r),
        v => this.buscandoRecetaPost.set(v)
      );
      return;
    }
    this.debounceTimerPost = setTimeout(() => {
      this.buscandoRecetaPost.set(true);
      this.recetaService.buscarDinamico(val, 'Cualquiera', []).subscribe({
        next: r => { this.resultadosBusquedaPost.set(r.content.slice(0, 6)); this.buscandoRecetaPost.set(false); },
        error: () => this.buscandoRecetaPost.set(false),
      });
    }, 350);
  }

  seleccionarRecetaPost(receta: RecetaResponse) {
    this.recetaSeleccionadaPost.set(receta);
    this.mostrarBuscadorRecetaPost.set(false);
    this.busquedaRecetaPost = '';
    this.resultadosBusquedaPost.set([]);
  }

  quitarRecetaPost() { this.recetaSeleccionadaPost.set(null); }

  /* Envía un comentario al post abierto */
  publicarComentario() {
    const postId = this.postAbierto();
    if (!postId || !this.nuevoComentario.trim()) return;

    const recetaId = this.recetaSeleccionadaComentario()?.id;
    this.enviandoComentario.set(true);
    this.foroService.comentar(postId, this.nuevoComentario.trim(), recetaId).subscribe({
      next: (comentario) => {
        this.comentariosDelPost.update(lista => [...lista, comentario]);
        this.posts.update(lista =>
          lista.map(p => p.id === postId
            ? { ...p, totalComentarios: p.totalComentarios + 1 }
            : p
          )
        );
        this.nuevoComentario = '';
        this.recetaSeleccionadaComentario.set(null);
        this.mostrarBuscadorReceta.set(false);
        this.busquedaRecetaComentario = '';
        this.enviandoComentario.set(false);
      },
      error: () => this.enviandoComentario.set(false),
    });
  }

  /* Abre el buscador del comentario y precarga las recetas propias */
  toggleBuscadorReceta() {
    const abriendo = !this.mostrarBuscadorReceta();
    this.mostrarBuscadorReceta.set(abriendo);
    if (abriendo && !this.busquedaRecetaComentario.trim()) {
      this.cargarRecetasPropias(
        r => this.resultadosBusqueda.set(r),
        v => this.buscandoReceta.set(v)
      );
    }
  }

  /* Búsqueda de recetas para adjuntar */
  onBusquedaRecetaInput(event: Event) {
    const val = (event.target as HTMLInputElement).value;
    this.busquedaRecetaComentario = val;
    clearTimeout(this.debounceTimer);
    if (!val.trim()) {
      this.cargarRecetasPropias(
        r => this.resultadosBusqueda.set(r),
        v => this.buscandoReceta.set(v)
      );
      return;
    }
    this.debounceTimer = setTimeout(() => {
      this.buscandoReceta.set(true);
      this.recetaService.buscarDinamico(val, 'Cualquiera', []).subscribe({
        next: r => { this.resultadosBusqueda.set(r.content.slice(0, 6)); this.buscandoReceta.set(false); },
        error: () => this.buscandoReceta.set(false),
      });
    }, 350);
  }

  seleccionarRecetaComentario(receta: RecetaResponse) {
    this.recetaSeleccionadaComentario.set(receta);
    this.mostrarBuscadorReceta.set(false);
    this.busquedaRecetaComentario = '';
    this.resultadosBusqueda.set([]);
  }

  quitarRecetaComentario() {
    this.recetaSeleccionadaComentario.set(null);
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

  iniciales(nombre: string): string {
    return nombre.split(' ').slice(0, 2).map(p => p[0]).join('').toUpperCase();
  }

  cerrarForm() {
    this.mostrarFormPost.set(false);
    this.nuevoTitulo = '';
    this.nuevoContenido = '';
    this.nuevoPostImagen.set(null);
    this.recetaSeleccionadaPost.set(null);
    this.mostrarBuscadorRecetaPost.set(false);
    this.busquedaRecetaPost = '';
    this.resultadosBusquedaPost.set([]);
    this.errorPost.set(null);
  }

  get miNombre(): string {
    return this.auth.sesion()?.nombre ?? '';
  }

  esAutor(nombreUsuario: string): boolean {
    return this.auth.estaAutenticado() && nombreUsuario === this.miNombre;
  }

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

  readonly MAX_TITULO    = 120;
  readonly MAX_CONTENIDO = 2000;
  // v2 — imagen + receta en nuevo post

  /* Lightbox */
  lightboxSrc = signal<string | null>(null);
  abrirLightbox(src: string) { this.lightboxSrc.set(src); }
  cerrarLightbox() { this.lightboxSrc.set(null); }
}
