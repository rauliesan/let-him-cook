import {
  Component,
  signal,
  AfterViewInit,
  OnDestroy,
  ViewEncapsulation,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Revela } from '../../shared/revela/revela';
import * as L from 'leaflet';

/* Tipo para representar un supermercado obtenido de Overpass */
interface Supermercado {
  id: number;
  nombre: string;
  direccion: string;
  horario: string;
  tipo: string;
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-mapa',
  imports: [CommonModule, FormsModule, Revela],
  templateUrl: './mapa.html',
  styleUrl: './mapa.scss',
  /* ViewEncapsulation.None permite que los estilos de los marcadores custom
     se apliquen al DOM que Leaflet inyecta fuera de la vista Angular */
  encapsulation: ViewEncapsulation.None,
})
export class Mapa implements AfterViewInit, OnDestroy {

  /* Instancias de Leaflet (no reactivas, se manejan imperativamente) */
  private mapa: L.Map | null = null;
  private marcadores: L.LayerGroup | null = null;
  private debounceTimer: ReturnType<typeof setTimeout> | null = null;

  /* Estado reactivo de la UI */
  cargando = signal(false);
  errorMsg = signal('');
  terminoBusqueda = signal('');
  panelAbierto = signal(false);

  /* Supermercado seleccionado al hacer click en un marcador */
  supermercadoActivo = signal<Supermercado | null>(null);

  /* Lista de resultados de la consulta Overpass */
  supermercados = signal<Supermercado[]>([]);
  totalResultados = signal(0);

  /* Filtro de categoria activo */
  categoriaActiva = signal('todos');

  /* Categorias disponibles con emoji y query Overpass */
  categorias = [
    { id: 'todos',       nombre: 'Todos',        icono: '🏪' },
    { id: 'supermarket', nombre: 'Supermercados', icono: '🛒' },
    { id: 'convenience', nombre: 'Tiendas 24h',   icono: '🏠' },
    { id: 'greengrocer', nombre: 'Fruterias',     icono: '🍎' },
    { id: 'butcher',     nombre: 'Carnicerías',   icono: '🥩' },
    { id: 'bakery',      nombre: 'Panaderías',    icono: '🥖' },
  ];

  /* ------------------------------------------------------------------ */
  /*  Ciclo de vida                                                      */
  /* ------------------------------------------------------------------ */

  ngAfterViewInit(): void {
    this.inicializarMapa();
  }

  ngOnDestroy(): void {
    if (this.debounceTimer) clearTimeout(this.debounceTimer);
    this.mapa?.remove();
  }

  /* ------------------------------------------------------------------ */
  /*  Inicialización del mapa                                            */
  /* ------------------------------------------------------------------ */

  private inicializarMapa(): void {
    /* Crear instancia del mapa apuntando al div #mapa */
    this.mapa = L.map('mapa', {
      center: [37.1622, -5.9244],  // Los Palacios y Villafranca (Sevilla) como fallback
      zoom: 15,
      zoomControl: true,
    });

    /* Tiles oscuros de CartoDB Dark Matter — pegan con la paleta del sitio */
    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="https://carto.com/">CARTO</a>',
      maxZoom: 19,
    }).addTo(this.mapa);

    /* LayerGroup para gestionar los marcadores de supermercados */
    this.marcadores = L.layerGroup().addTo(this.mapa);

    /* Re-consultar Overpass cada vez que el usuario mueva el mapa (con debounce) */
    this.mapa.on('moveend', () => {
      if (this.debounceTimer) clearTimeout(this.debounceTimer);
      this.debounceTimer = setTimeout(() => this.consultarOverpass(), 600);
    });

    /* Intentar centrar en la ubicacion del usuario */
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          this.mapa?.flyTo([pos.coords.latitude, pos.coords.longitude], 15);
        },
        () => {
          /* Si no da permiso, se queda en Madrid y busca */
          this.consultarOverpass();
        }
      );
    } else {
      this.consultarOverpass();
    }
  }

  /* ------------------------------------------------------------------ */
  /*  Consulta Overpass API                                              */
  /* ------------------------------------------------------------------ */

  async consultarOverpass(): Promise<void> {
    if (!this.mapa) return;

    this.cargando.set(true);
    this.errorMsg.set('');

    const bounds = this.mapa.getBounds();
    const sur = bounds.getSouth();
    const oeste = bounds.getWest();
    const norte = bounds.getNorth();
    const este = bounds.getEast();

    /* Construir el filtro segun la categoria seleccionada */
    const cat = this.categoriaActiva();
    let filtro: string;
    if (cat === 'todos') {
      filtro = '["shop"~"supermarket|convenience|greengrocer|butcher|bakery"]';
    } else {
      filtro = `["shop"="${cat}"]`;
    }

    /* Query Overpass — busca nodos y ways con el tag shop dentro del bbox visible.
       body: incluye todos los tags (nombre, horario, direccion, etc.)
       center: calcula el punto central para ways (poligonos)
       qt: ordenacion por quadtile para mejor rendimiento */
    const query = `
      [out:json][timeout:15];
      (
        node${filtro}(${sur},${oeste},${norte},${este});
        way${filtro}(${sur},${oeste},${norte},${este});
      );
      out body center qt 80;
    `;

    /* Servidores Overpass: si el principal falla, se intenta el de respaldo */
    const servidores = [
      'https://overpass-api.de/api/interpreter',
      'https://overpass.kumi.systems/api/interpreter',
    ];

    const cuerpo = `data=${encodeURIComponent(query)}`;
    let data: any = null;

    for (const servidor of servidores) {
      try {
        const resp = await fetch(servidor, {
          method: 'POST',
          body: cuerpo,
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        });
        if (!resp.ok) continue;
        data = await resp.json();
        break;
      } catch {
        continue;
      }
    }

    if (!data) {
      this.errorMsg.set('No se pudieron cargar los supermercados. Intentalo de nuevo.');
      this.cargando.set(false);
      return;
    }

    try {
      /* Mapear los elementos OSM a nuestro tipo Supermercado.
         Filtramos los que no tienen nombre porque no aportan info util al usuario. */
      const resultados: Supermercado[] = data.elements
        .filter((el: any) => el.tags?.name)
        .map((el: any) => ({
          id: el.id,
          nombre: el.tags.name,
          direccion: this.construirDireccion(el.tags),
          horario: this.formatearHorario(el.tags?.opening_hours),
          tipo: el.tags?.shop || 'supermarket',
          lat: el.lat ?? el.center?.lat,
          lon: el.lon ?? el.center?.lon,
        }))
        .filter((s: Supermercado) => s.lat && s.lon);

      this.supermercados.set(resultados);
      this.totalResultados.set(resultados.length);
      this.actualizarMarcadores(resultados);
    } catch {
      this.errorMsg.set('Error al procesar los datos. Intentalo de nuevo.');
    } finally {
      this.cargando.set(false);
    }
  }

  /* ------------------------------------------------------------------ */
  /*  Marcadores custom                                                  */
  /* ------------------------------------------------------------------ */

  /* Crea un DivIcon con el pin terracota y el emoji del tipo de tienda */
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

  /* Borra los marcadores actuales y pinta los nuevos */
  private actualizarMarcadores(lista: Supermercado[]): void {
    this.marcadores?.clearLayers();

    lista.forEach(s => {
      const marcador = L.marker([s.lat, s.lon], {
        icon: this.crearIcono(s.tipo),
      });

      marcador.on('click', () => {
        this.supermercadoActivo.set(s);
        this.panelAbierto.set(true);
      });

      this.marcadores?.addLayer(marcador);
    });
  }

  /* ------------------------------------------------------------------ */
  /*  Busqueda por nombre de lugar (Nominatim)                          */
  /* ------------------------------------------------------------------ */

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
        /* El evento moveend se encarga de disparar consultarOverpass */
        this.mapa.flyTo([parseFloat(data[0].lat), parseFloat(data[0].lon)], 15);
      } else {
        this.errorMsg.set('No se encontro esa ubicacion.');
      }
    } catch {
      this.errorMsg.set('Error al buscar la ubicacion.');
    } finally {
      this.cargando.set(false);
    }
  }

  /* ------------------------------------------------------------------ */
  /*  Acciones de UI                                                     */
  /* ------------------------------------------------------------------ */

  cerrarPanel(): void {
    this.panelAbierto.set(false);
    this.supermercadoActivo.set(null);
  }

  seleccionarCategoria(id: string): void {
    this.categoriaActiva.set(id);
    this.consultarOverpass();
  }

  centrarEnUsuario(): void {
    if (!('geolocation' in navigator)) {
      this.errorMsg.set('Tu navegador no soporta geolocalizacion.');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => this.mapa?.flyTo([pos.coords.latitude, pos.coords.longitude], 15),
      () => this.errorMsg.set('No se pudo obtener tu ubicacion.')
    );
  }

  /* Devuelve el emoji correspondiente a un tipo de tienda */
  emojiTipo(tipo: string): string {
    return this.categorias.find(c => c.id === tipo)?.icono || '🏪';
  }

  /* ------------------------------------------------------------------ */
  /*  Helpers                                                            */
  /* ------------------------------------------------------------------ */

  /* Construye la direccion a partir de los tags de OSM.
     Intenta addr:street primero, y si no existe busca en otros campos comunes. */
  private construirDireccion(tags: any): string {
    if (!tags) return '';
    const calle = tags['addr:street'];
    const numero = tags['addr:housenumber'];
    const ciudad = tags['addr:city'] || tags['addr:town'] || tags['addr:village'];
    const cp = tags['addr:postcode'];

    if (calle) {
      const partes = [calle, numero, ciudad, cp].filter(Boolean);
      return partes.join(', ');
    }

    /* Fallback: algunos nodos tienen la direccion completa en un solo campo */
    if (tags['addr:full']) return tags['addr:full'];

    /* Ultimo recurso: construir algo con lo que haya */
    if (ciudad) return ciudad + (cp ? `, ${cp}` : '');

    return '';
  }

  /* Formatea el horario de OSM a algo mas legible.
     OSM usa formatos tipo "Mo-Fr 09:00-21:00; Sa 09:00-14:00".
     Traducimos las abreviaturas inglesas a espanol. */
  private formatearHorario(raw: string | undefined): string {
    if (!raw) return 'Horario no disponible';

    const dias: Record<string, string> = {
      'Mo': 'Lun', 'Tu': 'Mar', 'We': 'Mie', 'Th': 'Jue',
      'Fr': 'Vie', 'Sa': 'Sab', 'Su': 'Dom',
      'PH': 'Festivos', 'SH': 'Vacaciones',
    };

    let resultado = raw;
    for (const [en, es] of Object.entries(dias)) {
      resultado = resultado.replace(new RegExp(`\\b${en}\\b`, 'g'), es);
    }

    /* Separar bloques con " | " en vez de ";" para mejor lectura */
    resultado = resultado.replace(/;\s*/g, ' | ');

    return resultado;
  }
}
