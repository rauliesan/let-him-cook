import {
  Component,
  signal,
  computed,
  AfterViewInit,
  OnDestroy,
  ViewEncapsulation,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Revela } from '../../shared/revela/revela';
import * as L from 'leaflet';

interface Supermercado {
  id: number;
  nombre: string;
  municipio: string;
  direccion: string;
  horario: string;
  tipo: string;
  lat: number;
  lon: number;
  telefono: string;
  web: string;
}

/* Agrupacion de tiendas por municipio para la vista lista */
interface GrupoMunicipio {
  municipio: string;
  tiendas: Supermercado[];
}

@Component({
  selector: 'app-mapa',
  imports: [CommonModule, FormsModule, Revela],
  templateUrl: './mapa.html',
  styleUrl: './mapa.scss',
  encapsulation: ViewEncapsulation.None,
})
export class Mapa implements AfterViewInit, OnDestroy {

  private mapa: L.Map | null = null;
  private capaMarcadores: L.LayerGroup | null = null;
  private debounceTimer: ReturnType<typeof setTimeout> | null = null;
  private marcadoresMapa = new Map<number, L.Marker>();
  private abortController: AbortController | null = null;

  /* Acumula TODAS las tiendas cargadas desde que se abre el mapa */
  private todasLasTiendas = new Map<number, Supermercado>();
  tiendasAcumuladas = signal<Supermercado[]>([]);

  /* Agrupa las tiendas acumuladas por municipio, ordenadas alfabeticamente */
  tiendasPorMunicipio = computed<GrupoMunicipio[]>(() => {
    const grupos = new Map<string, Supermercado[]>();
    for (const t of this.tiendasAcumuladas()) {
      const key = t.municipio || 'Sin municipio';
      if (!grupos.has(key)) grupos.set(key, []);
      grupos.get(key)!.push(t);
    }
    return Array.from(grupos.entries())
      .sort(([a], [b]) => a.localeCompare(b, 'es'))
      .map(([municipio, tiendas]) => ({ municipio, tiendas }));
  });

  cargando = signal(false);
  errorMsg = signal('');
  terminoBusqueda = signal('');
  panelAbierto = signal(false);

  supermercadoActivo = signal<Supermercado | null>(null);
  totalResultados = signal(0);

  categoriaActiva = signal('todos');
  listaVisible = signal(false);

  categorias = [
    { id: 'todos',       nombre: 'Todos',          icono: '🏪' },
    { id: 'supermarket', nombre: 'Supermercados',   icono: '🛒' },
    { id: 'convenience', nombre: 'Tiendas 24h',     icono: '🕐' },
    { id: 'greengrocer', nombre: 'Fruterías',        icono: '🍎' },
    { id: 'butcher',     nombre: 'Carnicerías',     icono: '🥩' },
    { id: 'bakery',      nombre: 'Panaderías',      icono: '🥖' },
  ];

  ngAfterViewInit(): void {
    this.inicializarMapa();
  }

  ngOnDestroy(): void {
    if (this.debounceTimer) clearTimeout(this.debounceTimer);
    this.abortController?.abort();
    this.mapa?.remove();
  }

  private inicializarMapa(): void {
    this.mapa = L.map('mapa', {
      center: [37.1622, -5.9244],
      zoom: 15,
      zoomControl: true,
    });

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="https://carto.com/">CARTO</a>',
      maxZoom: 19,
    }).addTo(this.mapa);

    this.capaMarcadores = L.layerGroup().addTo(this.mapa);

    this.mapa.on('moveend', () => {
      if (this.debounceTimer) clearTimeout(this.debounceTimer);
      this.debounceTimer = setTimeout(() => this.consultarOverpass(), 600);
    });

    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          this.mapa?.flyTo([pos.coords.latitude, pos.coords.longitude], 15);
        },
        () => {
          this.consultarOverpass();
        }
      );
    } else {
      this.consultarOverpass();
    }
  }

  async consultarOverpass(): Promise<void> {
    if (!this.mapa) return;

    this.abortController?.abort();
    this.abortController = new AbortController();
    const abortSignal = this.abortController.signal;

    this.cargando.set(true);
    this.errorMsg.set('');

    const bounds = this.mapa.getBounds();
    const sur    = bounds.getSouth();
    const oeste  = bounds.getWest();
    const norte  = bounds.getNorth();
    const este   = bounds.getEast();

    const cat = this.categoriaActiva();
    const filtro = cat === 'todos'
      ? '["shop"~"supermarket|convenience|greengrocer|butcher|bakery"]'
      : `["shop"="${cat}"]`;

    const query = `
      [out:json][timeout:25];
      (
        node${filtro}(${sur},${oeste},${norte},${este});
        way${filtro}(${sur},${oeste},${norte},${este});
      );
      out body center qt 150;
    `;

    const servidores = [
      'https://overpass-api.de/api/interpreter',
      'https://overpass.kumi.systems/api/interpreter',
    ];

    const cuerpo = `data=${encodeURIComponent(query)}`;
    let data: any = null;

    for (const servidor of servidores) {
      if (abortSignal.aborted) break;
      try {
        const resp = await fetch(servidor, {
          method: 'POST',
          body: cuerpo,
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          signal: abortSignal,
        });
        if (!resp.ok) continue;
        data = await resp.json();
        break;
      } catch (err: any) {
        if (err?.name === 'AbortError') {
          this.cargando.set(false);
          return;
        }
        continue;
      }
    }

    if (!data) {
      if (!abortSignal.aborted) {
        this.errorMsg.set('No se pudieron cargar los supermercados. Intentalo de nuevo.');
      }
      this.cargando.set(false);
      return;
    }

    try {
      const resultados: Supermercado[] = data.elements
        .filter((el: any) => el.tags?.name)
        .map((el: any) => ({
          id:        el.id,
          nombre:    el.tags.name,
          municipio: el.tags?.['addr:city'] || el.tags?.['addr:town'] || el.tags?.['addr:village'] || el.tags?.['addr:municipality'] || '',
          direccion: this.construirDireccion(el.tags),
          horario:   this.formatearHorario(el.tags?.opening_hours),
          tipo:      el.tags?.shop || 'supermarket',
          lat:       el.lat ?? el.center?.lat,
          lon:       el.lon ?? el.center?.lon,
          telefono:  el.tags?.phone || el.tags?.['contact:phone'] || '',
          web:       el.tags?.website || el.tags?.['contact:website'] || '',
        }))
        .filter((s: Supermercado) => s.lat && s.lon);

      this.actualizarMarcadores(resultados);
    } catch {
      if (!abortSignal.aborted) {
        this.errorMsg.set('Error al procesar los datos. Intentalo de nuevo.');
      }
    } finally {
      this.cargando.set(false);
    }
  }

  private crearIcono(tipo: string): L.DivIcon {
    const emoji = this.categorias.find(c => c.id === tipo)?.icono || '🏪';
    return L.divIcon({
      className: 'marcador-custom',
      html: `<div class="marcador-pin"><span class="marcador-emoji">${emoji}</span></div>`,
      iconSize: [40, 48],
      iconAnchor: [20, 48],
      popupAnchor: [0, -48],
    });
  }

  /* Añade marcadores y acumula tiendas. Nunca borra lo ya cargado. */
  private actualizarMarcadores(lista: Supermercado[]): void {
    if (!this.capaMarcadores) return;

    let huboNuevas = false;

    for (const s of lista) {
      if (this.marcadoresMapa.has(s.id)) continue;

      const marcador = L.marker([s.lat, s.lon], {
        icon: this.crearIcono(s.tipo),
      });

      marcador.on('click', () => {
        this.supermercadoActivo.set(s);
        this.panelAbierto.set(true);
      });

      this.capaMarcadores.addLayer(marcador);
      this.marcadoresMapa.set(s.id, marcador);
      this.todasLasTiendas.set(s.id, s);
      huboNuevas = true;
    }

    if (huboNuevas) {
      this.tiendasAcumuladas.set([...this.todasLasTiendas.values()]);
      this.totalResultados.set(this.todasLasTiendas.size);
    }
  }

  async buscarLugar(): Promise<void> {
    const termino = this.terminoBusqueda().trim();
    if (!termino || !this.mapa) return;

    this.cargando.set(true);
    this.errorMsg.set('');

    try {
      const resp = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(termino)}&limit=1`,
        { headers: { 'User-Agent': 'LetHimCook/1.0' } }
      );
      const data = await resp.json();

      if (data.length > 0) {
        this.mapa.flyTo([parseFloat(data[0].lat), parseFloat(data[0].lon)], 15);
      } else {
        this.errorMsg.set('No se encontró esa ubicación.');
        this.cargando.set(false);
      }
    } catch {
      this.errorMsg.set('Error al buscar la ubicación.');
      this.cargando.set(false);
    }
  }

  cerrarPanel(): void {
    this.panelAbierto.set(false);
    this.supermercadoActivo.set(null);
  }

  seleccionarCategoria(id: string): void {
    this.categoriaActiva.set(id);
    this.capaMarcadores?.clearLayers();
    this.marcadoresMapa.clear();
    this.todasLasTiendas.clear();
    this.tiendasAcumuladas.set([]);
    this.totalResultados.set(0);
    this.consultarOverpass();
  }

  centrarEnUsuario(): void {
    if (!('geolocation' in navigator)) {
      this.errorMsg.set('Tu navegador no soporta geolocalización.');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => this.mapa?.flyTo([pos.coords.latitude, pos.coords.longitude], 15),
      () => this.errorMsg.set('No se pudo obtener tu ubicación.')
    );
  }

  emojiTipo(tipo: string): string {
    return this.categorias.find(c => c.id === tipo)?.icono || '🏪';
  }

  nombreTipo(tipo: string): string {
    const nombres: Record<string, string> = {
      supermarket: 'Supermercado',
      convenience: 'Tienda 24h',
      greengrocer: 'Frutería',
      butcher:     'Carnicería',
      bakery:      'Panadería',
    };
    return nombres[tipo] || 'Tienda';
  }

  mostrarMapa() {
    this.listaVisible.set(false);
    setTimeout(() => this.mapa?.invalidateSize(), 10);
  }

  toggleVista() {
    this.listaVisible.update(v => !v);
    if (!this.listaVisible()) {
      setTimeout(() => this.mapa?.invalidateSize(), 10);
    }
  }

  abrirEnMapa(s: Supermercado) {
    this.listaVisible.set(false);
    setTimeout(() => {
      this.mapa?.invalidateSize();
      this.mapa?.flyTo([s.lat, s.lon], 17);
      this.supermercadoActivo.set(s);
      this.panelAbierto.set(true);
    }, 10);
  }

  private construirDireccion(tags: any): string {
    if (!tags) return '';
    const calle  = tags['addr:street'];
    const numero = tags['addr:housenumber'];
    const ciudad = tags['addr:city'] || tags['addr:town'] || tags['addr:village'];
    const cp     = tags['addr:postcode'];

    if (calle) {
      return [calle, numero, ciudad, cp].filter(Boolean).join(', ');
    }
    if (tags['addr:full']) return tags['addr:full'];
    if (ciudad) return ciudad + (cp ? `, ${cp}` : '');
    return '';
  }

  private formatearHorario(raw: string | undefined): string {
    if (!raw) return 'Horario no disponible';

    const dias: Record<string, string> = {
      'Mo': 'Lun', 'Tu': 'Mar', 'We': 'Mié', 'Th': 'Jue',
      'Fr': 'Vie', 'Sa': 'Sáb', 'Su': 'Dom',
      'PH': 'Festivos', 'SH': 'Vacaciones',
    };

    let resultado = raw;
    for (const [en, es] of Object.entries(dias)) {
      resultado = resultado.replace(new RegExp(`\\b${en}\\b`, 'g'), es);
    }
    return resultado.replace(/;\s*/g, ' | ');
  }
}
