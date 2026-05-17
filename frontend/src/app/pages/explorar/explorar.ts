import { Component, OnInit, signal, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { CountUp } from '../../shared/countup/countup';
import { RecetaService, RecetaResponse, TipoComidaResponse } from '../../services/receta.service';
import { FavoritoService } from '../../services/favorito.service';

/* Agrupación de categorías en el acordeón — los nombres deben coincidir con la BD */
const GRUPOS: { nombre: string; emoji: string; cats: string[] }[] = [
  {
    nombre: 'Cocinas del Mundo', emoji: '🌍',
    cats: [
      'Italiana', 'Española', 'Japonesa', 'Mexicana', 'China', 'Francesa',
      'India', 'Americana', 'Griega', 'Tailandesa', 'Árabe', 'Peruana',
      'Coreana', 'Vietnamita', 'Marroquí', 'Turca', 'Brasileña', 'Alemana',
      'Mediterránea', 'Asiática', 'Fusión',
    ],
  },
  {
    nombre: 'Tipo de Plato', emoji: '🍽️',
    cats: [
      'Sopas', 'Ensaladas', 'Pastas', 'Arroces', 'Postres', 'Panadería',
      'Aperitivos', 'Bebidas', 'Desayunos', 'Fast Food', 'Horneado', 'A la plancha',
    ],
  },
  {
    nombre: 'Proteína Principal', emoji: '🥩',
    cats: ['Carnes', 'Aves', 'Cerdo', 'Ternera', 'Pescado', 'Mariscos', 'Legumbres'],
  },
  {
    nombre: 'Estilo y Dieta', emoji: '🥗',
    cats: [
      'Vegana', 'Vegetariana', 'Saludable', 'Sin gluten', 'Sin lactosa',
      'Alto en proteína', 'Bajo en calorías', 'Rápido', 'Económico',
      'Para niños', 'Picante',
    ],
  },
];

const COLOR_FALLBACK = '#B83520';

@Component({
  selector: 'app-explorar',
  imports: [CommonModule, RouterLink, Revela, CountUp],
  templateUrl: './explorar.html',
  styleUrl: './explorar.scss',
})
export class Explorar implements OnInit, OnDestroy {

  readonly grupos = GRUPOS;

  /* Animación del buscador */
  placeholderActual = signal('');
  private frasesBuscador = [
    'Buscar espaguetis a la carbonara...',
    '¿Qué tal un risotto de setas?',
    'Busca ingredientes como "aguacate"',
    'Encuentra recetas de "Chef Ramsey"',
    '¿Cocinamos algo con pollo?',
    'Prueba con "Postres sin azúcar"',
    'Recetas de menos de 30 minutos',
    'Busca "Tacos al pastor"',
    '¿Cómo se hace un Pad Thai?',
    'Cenas saludables para hoy',
  ];
  private fraseIndex = 0;
  private charIndex = 0;
  private borrando = false;
  private timeoutId: any;

  /* Datos de la API */
  categorias = signal<TipoComidaResponse[]>([]);
  todasRecetas = signal<RecetaResponse[]>([]);

  /* Modo amigos */
  modoAmigos    = signal(false);
  recetasAmigos = signal<RecetaResponse[]>([]);

  private categoriasMapa = computed(() =>
    new Map(this.categorias().map(c => [c.nombre, c]))
  );

  /* IDs de recetas en favoritos */
  favoritosIds = signal<Set<string>>(new Set());
  likesAnimando = signal<Set<string>>(new Set());

  /* Estado de carga */
  cargando = signal(true);
  errorCarga = signal<string | null>(null);

  /* Filtros activos */
  /* Set de categorías activas — vacío significa "Todas" */
  filtrosActivos = signal<Set<string>>(new Set());
  dificultadActiva = signal('Cualquiera');

  /**
   * 'OR' (defecto): mostrar recetas que tengan AL MENOS UNA categoría seleccionada.
   * 'AND': mostrar solo recetas que tengan TODAS las categorías seleccionadas.
   */
  modoFiltro = signal<'OR' | 'AND'>('OR');

  /* Grupos del acordeón abiertos */
  gruposAbiertos = signal<Set<string>>(new Set());

  dificultades = ['Cualquiera', 'BAJA', 'MEDIA', 'ALTA'];

  /* Búsqueda dinámica */
  terminoBusqueda = signal('');
  buscando        = signal(false);

  constructor(
    private recetaService: RecetaService,
    private favoritoService: FavoritoService,
  ) { }

  ngOnInit() {
    this.cargarDatos();
    this.animarPlaceholder();
  }

  ngOnDestroy() {
    if (this.timeoutId) clearTimeout(this.timeoutId);
  }

  animarPlaceholder() {
    const frase = this.frasesBuscador[this.fraseIndex];
    const delaySiguienteFrase = 2500;
    const delayBorrando = 40;
    const delayEscribiendo = 80;

    if (this.borrando) {
      this.placeholderActual.set(frase.substring(0, this.charIndex--) + '|');
      if (this.charIndex < 0) {
        this.borrando = false;
        this.fraseIndex = (this.fraseIndex + 1) % this.frasesBuscador.length;
        this.timeoutId = setTimeout(() => this.animarPlaceholder(), 500);
        return;
      }
    } else {
      this.placeholderActual.set(frase.substring(0, this.charIndex++) + '|');
      if (this.charIndex > frase.length) {
        this.borrando = true;
        this.placeholderActual.set(frase); // Quitar cursor al final
        this.timeoutId = setTimeout(() => this.animarPlaceholder(), delaySiguienteFrase);
        return;
      }
    }

    const velocidad = this.borrando ? delayBorrando : delayEscribiendo;
    this.timeoutId = setTimeout(() => this.animarPlaceholder(), velocidad);
  }

  cargarDatos() {
    this.cargando.set(true);
    this.errorCarga.set(null);

    this.recetaService.getCategorias().subscribe({
      next: (cats) => this.categorias.set(cats.filter(c => c.colorHex)),
      error: () => this.categorias.set([]),
    });

    this.recetaService.getTodas().subscribe({
      next: (r) => {
        this.todasRecetas.set(r);
        this.cargando.set(false);
      },
      error: () => {
        this.errorCarga.set('No se pudieron cargar las recetas.');
        this.cargando.set(false);
      },
    });

    this.favoritoService.getMisFavoritos().subscribe({
      next: (favs) => this.favoritosIds.set(new Set(favs.map(f => f.recetaId))),
      error: () => this.favoritosIds.set(new Set()),
    });
  }

  /* Ejecutar búsqueda en el servidor */
  buscar(event?: Event) {
    if (event) event.preventDefault();
    this.modoAmigos.set(false);
    const termino = this.terminoBusqueda().trim();
    
    // Convertir nombres de categorías activas a sus IDs reales de la BD
    const categoriasIds = [...this.filtrosActivos()]
      .map(nombre => this.categoriasMapa().get(nombre)?.id)
      .filter((id): id is string => !!id);

    this.buscando.set(true);
    this.recetaService.buscarDinamico(termino, this.dificultadActiva(), categoriasIds).subscribe({
      next: (res) => {
        this.todasRecetas.set(res.content);
        this.buscando.set(false);
      },
      error: () => {
        this.buscando.set(false);
      }
    });
  }

  onInputSearch(event: Event) {
    const el = event.target as HTMLInputElement;
    this.terminoBusqueda.set(el.value);
    // Podríamos añadir debounce aquí, pero por ahora lo dejamos con el botón y Enter
  }

  /* Las recetas se cargan directamente desde el servidor según los filtros aplicados en buscar() */
  recetasFiltradas = computed(() =>
    this.modoAmigos() ? this.recetasAmigos() : this.todasRecetas()
  );

  /* Toggle de una categoría — si ya está activa la quita, si no la añade */
  seleccionarFiltro(nombre: string) {
    this.modoAmigos.set(false);
    if (nombre === 'Todas') {
      this.filtrosActivos.set(new Set());
      this.buscar();
      return;
    }
    this.filtrosActivos.update(s => {
      const n = new Set(s);
      n.has(nombre) ? n.delete(nombre) : n.add(nombre);
      return n;
    });
    this.buscar();
  }

  seleccionarDificultad(d: string) {
    this.modoAmigos.set(false);
    this.dificultadActiva.set(d);
    this.buscar();
  }

  activarModoAmigos() {
    if (this.modoAmigos()) {
      this.modoAmigos.set(false);
      this.buscar();
      return;
    }
    this.modoAmigos.set(true);
    this.filtrosActivos.set(new Set());
    this.cargando.set(true);
    this.recetaService.getRecetasDeAmigos().subscribe({
      next: r => { this.recetasAmigos.set(r); this.cargando.set(false); },
      error: () => { this.recetasAmigos.set([]); this.cargando.set(false); },
    });
  }

  toggleGrupo(nombre: string) {
    this.gruposAbiertos.update(s => {
      const n = new Set(s);
      n.has(nombre) ? n.delete(nombre) : n.add(nombre);
      return n;
    });
  }

  /* Categorías cargadas de la API que pertenecen al grupo */
  categoriasDeGrupo(nombres: string[]): TipoComidaResponse[] {
    const mapa = this.categoriasMapa();
    return nombres.filter(n => mapa.has(n)).map(n => mapa.get(n)!);
  }

  /* Primeros 4 emojis del grupo — se muestran en el header colapsado */
  previewEmojisGrupo(nombres: string[]): string[] {
    return this.categoriasDeGrupo(nombres).slice(0, 5).map(c => c.iconoUrl ?? '🍽️');
  }

  /* True si alguno de los filtros activos pertenece a este grupo */
  grupoContieneFiltro(nombres: string[]): boolean {
    const f = this.filtrosActivos();
    return nombres.some(n => f.has(n));
  }

  esFavorito(recetaId: string): boolean {
    return this.favoritosIds().has(recetaId);
  }

  toggleFavorito(recetaId: string, event: Event) {
    event.stopPropagation();
    const delta = this.esFavorito(recetaId) ? -1 : 1;

    this.todasRecetas.update(lista =>
      lista.map(r => r.id === recetaId
        ? { ...r, totalLikes: Math.max(0, r.totalLikes + delta) }
        : r
      )
    );

    this.likesAnimando.update(s => new Set([...s, recetaId]));
    setTimeout(() => {
      this.likesAnimando.update(s => { const n = new Set(s); n.delete(recetaId); return n; });
    }, 650);

    this.lanzarCorazonFx(event.currentTarget as HTMLElement);

    if (delta === -1) {
      this.favoritosIds.update(s => { const n = new Set(s); n.delete(recetaId); return n; });
      this.favoritoService.eliminar(recetaId).subscribe({
        error: () => {
          this.todasRecetas.update(l => l.map(r => r.id === recetaId ? { ...r, totalLikes: r.totalLikes + 1 } : r));
          this.favoritosIds.update(s => new Set([...s, recetaId]));
        },
      });
    } else {
      this.favoritosIds.update(s => new Set([...s, recetaId]));
      this.favoritoService.agregar(recetaId).subscribe({
        error: () => {
          this.todasRecetas.update(l => l.map(r => r.id === recetaId ? { ...r, totalLikes: Math.max(0, r.totalLikes - 1) } : r));
          this.favoritosIds.update(s => { const n = new Set(s); n.delete(recetaId); return n; });
        },
      });
    }
  }

  private lanzarCorazonFx(boton: HTMLElement) {
    const fx = document.createElement('span');
    fx.className = 'like-fx-heart';
    fx.textContent = '♥';
    boton.appendChild(fx);
    setTimeout(() => fx.remove(), 700);
  }

  /* Emoji desde la BD */
  emojiCategoria(nombre: string): string {
    return this.categoriasMapa().get(nombre)?.iconoUrl ?? '🍽️';
  }

  /* Emojis de todas las categorías de una receta (hasta 3) */
  emojisReceta(receta: RecetaResponse): string[] {
    return [receta.tipoComidaNombre, receta.tipoComida2Nombre, receta.tipoComida3Nombre]
      .filter((c): c is string => !!c)
      .map(c => this.emojiCategoria(c));
  }

  colorCategoria(catNombre: string): string {
    return this.categoriasMapa().get(catNombre)?.colorHex ?? COLOR_FALLBACK;
  }

  colorReceta(tipoComidaNombre: string | null, idx: number): string {
    const hex = this.categoriasMapa().get(tipoComidaNombre ?? '')?.colorHex ?? COLOR_FALLBACK;
    const variaciones = [0, -10, +8, -5];
    const offset = variaciones[idx % variaciones.length];
    const claro = this.ajustarBrillo(hex, offset);
    const oscuro = this.ajustarBrillo(hex, offset - 28);
    return `linear-gradient(145deg, ${claro}, ${oscuro})`;
  }

  private ajustarBrillo(hex: string, delta: number): string {
    const n = parseInt(hex.replace('#', ''), 16);
    const r = Math.max(0, Math.min(255, ((n >> 16) & 255) + delta));
    const g = Math.max(0, Math.min(255, ((n >> 8) & 255) + delta));
    const b = Math.max(0, Math.min(255, ((n) & 255) + delta));
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
}
