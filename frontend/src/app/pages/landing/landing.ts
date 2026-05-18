import { Component, ElementRef, HostListener, OnInit, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';
import { CountUp } from '../../shared/countup/countup';
import { UsuarioService } from '../../services/usuario.service';
import { RecetaService, RecetaResponse, TipoComidaResponse } from '../../services/receta.service';
import { FavoritoService } from '../../services/favorito.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, CommonModule, Revela, CountUp],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export class Landing implements OnInit {
  faqAbierta = signal<number | null>(null);

  /* Métricas crudas, CountUp las anima y formatea en el template */
  totalUsuariosRaw = signal<number>(0);
  totalRecetasRaw  = signal<number>(0);

  /* Función de formato para la directiva CountUp: "5+" / "1.2k+" */
  formatearMetrica = (n: number): string => {
    if (n === 0) return '0';
    if (n >= 1000) return (n / 1000).toFixed(1) + 'k+';
    return `${n}+`;
  };

  /* Categorías cargadas desde la BD */
  categorias       = signal<TipoComidaResponse[]>([]);
  private categoriasMapa = computed(() =>
    new Map(this.categorias().map(c => [c.nombre, c]))
  );

  /* Recetas populares, las 6 más recientes de la BD */
  recetasPopulares = signal<RecetaResponse[]>([]);
  cargandoRecetas  = signal(true);

  /* IDs de recetas marcadas como favoritas por el usuario */
  favoritosIds     = signal<Set<string>>(new Set());

  /* IDs de recetas cuyo like-button está animándose ahora mismo */
  likesAnimando    = signal<Set<string>>(new Set());

  constructor(
    private el: ElementRef<HTMLElement>,
    private usuarioService: UsuarioService,
    private recetaService: RecetaService,
    public  auth: AuthService,
    private favoritoService: FavoritoService,
  ) {}

  ngOnInit() {
    /* Carga categorías para resolver emoji y color de cada receta */
    this.recetaService.getCategorias().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {},
    });

    /* Carga el total de usuarios registrados */
    this.usuarioService.contarUsuarios().subscribe({
      next:  (res) => this.totalUsuariosRaw.set(res.totalElements ?? 0),
      error: ()    => this.totalUsuariosRaw.set(0),
    });

    /* Carga favoritos del usuario si está logueado */
    if (this.auth.estaAutenticado()) {
      this.favoritoService.getMisFavoritos().subscribe({
        next: (favs) => this.favoritosIds.set(new Set(favs.map(f => f.recetaId))),
        error: () => {},
      });
    }

    /* Carga recetas: métrica + sección populares */
    this.recetaService.getTodas().subscribe({
      next: (lista) => {
        this.totalRecetasRaw.set(lista.length);
        const ordenadas = lista
          .filter(r => r.esPublica)
          .sort((a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime())
          .slice(0, 6);
        this.recetasPopulares.set(ordenadas);
        this.cargandoRecetas.set(false);
      },
      error: () => this.cargandoRecetas.set(false),
    });
  }

  /* Emoji de la categoría, usa el iconoUrl almacenado en la BD */
  emojiCategoria(nombre: string): string {
    return this.categoriasMapa().get(nombre)?.iconoUrl ?? '🍽️';
  }

  /* Gradiente de la tarjeta de receta según la categoría, con variación por índice */
  colorReceta(tipoComidaNombre: string | null, idx: number): string {
    const hex = this.categoriasMapa().get(tipoComidaNombre ?? '')?.colorHex ?? '#B83520';
    const variaciones = [0, -10, +8, -5];
    const offset = variaciones[idx % variaciones.length];
    const claro = this.ajustarBrillo(hex, offset);
    const oscuro = this.ajustarBrillo(hex, offset - 28);
    return `linear-gradient(145deg, ${claro}, ${oscuro})`;
  }

  private ajustarBrillo(hex: string, delta: number): string {
    const n = parseInt(hex.replace('#', ''), 16);
    const r = Math.max(0, Math.min(255, ((n >> 16) & 255) + delta));
    const g = Math.max(0, Math.min(255, ((n >> 8)  & 255) + delta));
    const b = Math.max(0, Math.min(255, ((n)       & 255) + delta));
    return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`;
  }

  formatearTiempo(minutos: number | null): string {
    if (!minutos) return '—';
    if (minutos < 60) return `${minutos} min`;
    const h = Math.floor(minutos / 60);
    const m = minutos % 60;
    return m > 0 ? `${h}h ${m}min` : `${h}h`;
  }

  etiquetaDificultad(d: string | null): string {
    const mapa: Record<string, string> = { BAJA: 'Fácil', MEDIA: 'Media', ALTA: 'Difícil' };
    return d ? (mapa[d] ?? d) : '—';
  }

  esFavorito(recetaId: string): boolean {
    return this.favoritosIds().has(recetaId);
  }

  /**
   * Toggle de favorito con:
   * - Actualización optimista del contador (anima CountUp)
   * - Animación de corazón (heartPop + ring)
   * - Corazón flotante que sube y desaparece
   * - Reversión automática si falla la petición
   */
  toggleFavorito(recetaId: string, event: Event) {
    event.stopPropagation();
    if (!this.auth.estaAutenticado()) return;

    const delta = this.esFavorito(recetaId) ? -1 : 1;

    /* Actualizar contador en el array de recetas, CountUp lo animará */
    this.recetasPopulares.update(lista =>
      lista.map(r => r.id === recetaId
        ? { ...r, totalLikes: Math.max(0, r.totalLikes + delta) }
        : r
      )
    );

    /* Activar animación del botón */
    this.likesAnimando.update(s => new Set([...s, recetaId]));
    setTimeout(() => {
      this.likesAnimando.update(s => { const n = new Set(s); n.delete(recetaId); return n; });
    }, 650);

    /* Corazón flotante que sube y desvanece */
    this.lanzarCorazonFx(event.currentTarget as HTMLElement);

    /* Toggle del set de favoritos */
    if (delta === -1) {
      this.favoritosIds.update(s => { const n = new Set(s); n.delete(recetaId); return n; });
      this.favoritoService.eliminar(recetaId).subscribe({
        error: () => {
          /* Revertir si falla */
          this.recetasPopulares.update(l => l.map(r => r.id === recetaId ? { ...r, totalLikes: r.totalLikes + 1 } : r));
          this.favoritosIds.update(s => new Set([...s, recetaId]));
        },
      });
    } else {
      this.favoritosIds.update(s => new Set([...s, recetaId]));
      this.favoritoService.agregar(recetaId).subscribe({
        error: () => {
          this.recetasPopulares.update(l => l.map(r => r.id === recetaId ? { ...r, totalLikes: Math.max(0, r.totalLikes - 1) } : r));
          this.favoritosIds.update(s => { const n = new Set(s); n.delete(recetaId); return n; });
        },
      });
    }
  }

  /* Crea un ♥ flotante que sube y desvanece encima del botón */
  private lanzarCorazonFx(boton: HTMLElement) {
    const fx = document.createElement('span');
    fx.className = 'like-fx-heart';
    fx.textContent = '♥';
    boton.appendChild(fx);
    setTimeout(() => fx.remove(), 700);
  }

  @HostListener('mousemove', ['$event'])
  onMouseMove(e: MouseEvent) {
    const host = this.el.nativeElement;
    const nx = (e.clientX / window.innerWidth  - 0.5) * 2;
    const ny = (e.clientY / window.innerHeight - 0.5) * 2;

    host.style.setProperty('--mx', `${(e.clientX / window.innerWidth) * 100}%`);
    host.style.setProperty('--my', `${(e.clientY / window.innerHeight) * 100}%`);
    host.style.setProperty('--tx-fondo',  `${(nx * -22).toFixed(1)}px`);
    host.style.setProperty('--ty-fondo',  `${(ny * -14).toFixed(1)}px`);
    host.style.setProperty('--tx-texto',  `${(nx *   5).toFixed(1)}px`);
    host.style.setProperty('--ty-texto',  `${(ny *   4).toFixed(1)}px`);
    host.style.setProperty('--tx-panel',  `${(nx *  16).toFixed(1)}px`);
    host.style.setProperty('--ty-panel',  `${(ny *  12).toFixed(1)}px`);
    host.style.setProperty('--tx-logro',  `${(nx *  24).toFixed(1)}px`);
    host.style.setProperty('--ty-logro',  `${(ny *  18).toFixed(1)}px`);
    host.style.setProperty('--tilt-x', `${(ny * -6).toFixed(2)}deg`);
    host.style.setProperty('--tilt-y', `${(nx *  9).toFixed(2)}deg`);

    const hero = host.querySelector('.hero') as HTMLElement;
    const rect  = hero?.getBoundingClientRect() ?? { left: 0, top: 0 };
    host.style.setProperty('--aura-x', `${e.clientX - rect.left - 280}px`);
    host.style.setProperty('--aura-y', `${e.clientY - rect.top  - 280}px`);
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    const host = this.el.nativeElement;
    ['--tx-fondo','--ty-fondo','--tx-texto','--ty-texto',
     '--tx-panel','--ty-panel','--tx-logro','--ty-logro']
      .forEach(p => host.style.setProperty(p, '0px'));
    host.style.setProperty('--tilt-x', '0deg');
    host.style.setProperty('--tilt-y', '0deg');
  }

  orbitCirculos = Array.from(
    { length: Math.floor(Math.random() * 4) + 4 },
    () => {
      const isRed = Math.random() > 0.4;
      const base = isRed ? '193, 62, 40' : '232, 184, 75';
      return {
        size:     Math.floor(Math.random() * 300) + 80,
        top:      Math.floor(Math.random() * 75)  + 5,
        left:     Math.floor(Math.random() * 85)  + 5,
        opacity:  +(Math.random() * 0.07 + 0.02).toFixed(2),
        delay:    +(Math.random() * 8).toFixed(1),
        duration: +(Math.random() * 10 + 10).toFixed(1),
        color:    `rgba(${base}, 0.4)`,
      };
    }
  );

  ingredientesTira = [
    'pasta', 'tomate', 'ajo', 'limón', 'pollo', 'arroz',
    'cebolla', 'pimiento', 'queso', 'albahaca', 'huevo',
    'zanahoria', 'champiñón', 'espinacas', 'chorizo', 'salmón'
  ];

  features = [
    { icono: 'ia',        titulo: 'Recetas con IA',       desc: 'Dinos qué tienes en la nevera. La IA genera una receta personalizada al instante, adaptada a tus preferencias y restricciones.', badge: 'Groq · ChatGPT · Deepseek', grande: true },
    { icono: 'logros',    titulo: 'Gamificación real',    desc: 'Cada vez que cocinas, ganas. Puntos, niveles, logros y retos que convierten cocinar en algo adictivo.', badge: null, grande: false },
    { icono: 'comunidad', titulo: 'Comunidad viva',       desc: 'Comparte tus creaciones y descubre las de otros. Sigue a cocineros, comenta, guarda favoritos.', badge: null, grande: false },
    { icono: 'mapa',      titulo: 'Supermercados',        desc: 'Encuentra los súpers más cercanos a ti. Integración directa con Google Maps.', badge: null, grande: false },
  ];

  faqs = [
    { pregunta: '¿Es completamente gratis?', respuesta: 'Sí, LetHimCook es 100% gratuito. Sin suscripciones, sin pagos ocultos, sin límites de uso.' },
    { pregunta: '¿Cómo funciona la generación con IA?', respuesta: 'Introduces los ingredientes que tienes en casa, tus preferencias y restricciones alimentarias, y la IA crea una receta personalizada al instante.' },
    { pregunta: '¿Puedo editar las recetas generadas?', respuesta: 'Por supuesto. Tienes control total para modificar cualquier receta, sea generada por IA o creada por ti desde cero.' },
    { pregunta: '¿En qué consiste el sistema de logros?', respuesta: 'Conforme cocinas, publicas y exploras, ganas puntos y desbloqueas logros. Hay niveles, recompensas y competiciones amistosas entre usuarios.' },
  ];

  toggleFaq(indice: number) {
    this.faqAbierta.update(actual => actual === indice ? null : indice);
  }
}
