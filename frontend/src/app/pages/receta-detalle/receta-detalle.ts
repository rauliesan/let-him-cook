import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { RecetaService, RecetaResponse } from '../../services/receta.service';
import { FavoritoService } from '../../services/favorito.service';
import { AuthService } from '../../services/auth.service';
import { IaService } from '../../services/ia.service';
import { LogroService } from '../../services/logro.service';

@Component({
  selector: 'app-receta-detalle',
  standalone: true,
  imports: [CommonModule, RouterLink, Revela],
  templateUrl: './receta-detalle.html',
  styleUrl: './receta-detalle.scss',
})
export class RecetaDetalle implements OnInit {

  receta   = signal<RecetaResponse | null>(null);
  cargando = signal(true);
  error    = signal<string | null>(null);

  /* Like */
  liked       = signal(false);
  likeCount   = signal(0);
  loadingLike = signal(false);
  likeAnim    = signal(false);

  instrucciones          = signal<string | null>(null);
  generandoInstrucciones = signal(false);
  errorInstrucciones     = signal<string | null>(null);

  // Estado del modo cocina: Índices de pasos completados
  pasosCompletados = signal<number[]>([]);

  porcentajeProgreso = computed(() => {
    const total = this.instruccionesList().length;
    if (total === 0) return 0;
    return Math.round((this.pasosCompletados().length / total) * 100);
  });

  todoCompletado = computed(() => {
    const total = this.instruccionesList().length;
    return total > 0 && this.pasosCompletados().length === total;
  });

  // Recompensa por cocinar
  recompensaReclamada = signal(false);
  yaCompletadaAntes   = signal(false);
  monedasGanadas      = signal<number | null>(null);

  // Notificación flotante
  mostrarNotificacion = signal(false);
  mensajeNotificacion = signal('');

  ingredientesList = computed(() => {
    const raw = this.receta()?.ingredientes ?? '';
    return this.parseLista(raw);
  });

  instruccionesList = computed(() => {
    const raw = this.receta()?.instrucciones ?? '';
    return this.parseLista(raw);
  });

  alergenosList = computed(() => {
    const raw = this.receta()?.alergenos ?? '';
    return raw.split(',').map(s => s.trim()).filter(Boolean).map(a => ({
      nombre: a,
      icono: this.getIconoAlergeno(a)
    }));
  });

  constructor(
    private route: ActivatedRoute,
    private recetaService: RecetaService,
    private favoritoService: FavoritoService,
    public  auth: AuthService,
    private iaService: IaService,
    private logroService: LogroService,
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cargarReceta(id);
    } else {
      this.error.set('ID de receta no válido.');
      this.cargando.set(false);
    }
  }

  private cargarReceta(id: string) {
    this.recetaService.getPorId(id).subscribe({
      next: (r) => {
        this.receta.set(r);
        this.likeCount.set(r.totalLikes);
        this.cargando.set(false);
        if (this.auth.estaAutenticado()) {
          // Comprobar like
          this.favoritoService.getMisFavoritos().subscribe({
            next: (favs) => this.liked.set(favs.some(f => f.recetaId === r.id)),
            error: () => {},
          });
          // Comprobar si ya la cocinó antes
          this.recetaService.haCompletado(r.id).subscribe({
            next: (completada) => {
              if (completada) {
                this.yaCompletadaAntes.set(true);
                this.recompensaReclamada.set(true);
              }
            },
            error: () => {},
          });
        }
      },
      error: () => { this.error.set('No se pudo cargar la receta.'); this.cargando.set(false); },
    });
  }

  toggleLike() {
    if (!this.auth.estaAutenticado() || this.loadingLike()) return;
    const recetaId = this.receta()?.id;
    if (!recetaId) return;

    const estabaMarcado = this.liked();
    this.liked.set(!estabaMarcado);
    this.likeCount.update(n => estabaMarcado ? Math.max(0, n - 1) : n + 1);
    this.likeAnim.set(true);
    setTimeout(() => this.likeAnim.set(false), 600);

    this.loadingLike.set(true);

    const revertir = () => {
      this.liked.set(estabaMarcado);
      this.likeCount.update(n => estabaMarcado ? n + 1 : Math.max(0, n - 1));
      this.loadingLike.set(false);
    };

    if (estabaMarcado) {
      this.favoritoService.eliminar(recetaId).subscribe({
        next: () => this.loadingLike.set(false),
        error: revertir,
      });
    } else {
      this.favoritoService.agregar(recetaId).subscribe({
        next: () => this.loadingLike.set(false),
        error: revertir,
      });
    }
  }

  generarInstruccionesIA() {
    const r = this.receta();
    if (!r || this.generandoInstrucciones()) return;
    this.generandoInstrucciones.set(true);
    this.errorInstrucciones.set(null);
    this.iaService.generarInstrucciones(r.nombre, r.ingredientes).subscribe({
      next: (res) => {
        this.instrucciones.set(res.instrucciones);
        this.generandoInstrucciones.set(false);
      },
      error: (err) => {
        this.errorInstrucciones.set(err.error?.mensaje ?? 'No se pudieron generar las instrucciones.');
        this.generandoInstrucciones.set(false);
      },
    });
  }

  togglePaso(idx: number): void {
    const actuales = this.pasosCompletados();
    let nuevos: number[];
    if (actuales.includes(idx)) {
      nuevos = actuales.filter(i => i !== idx);
    } else {
      nuevos = [...actuales, idx];
    }
    this.pasosCompletados.set(nuevos);

    // Si acaba de completar todo y no ha reclamado en esta sesión
    if (this.todoCompletado() && !this.recompensaReclamada()) {
      this.reclamarRecompensa();
    }
  }

  private reclamarRecompensa(): void {
    const id = this.receta()?.id;
    if (!id) return;

    this.recetaService.completarReceta(id).subscribe({
      next: (monedas) => {
        this.monedasGanadas.set(monedas);
        this.recompensaReclamada.set(true);

        if (monedas === -1) {
          // Ya la había cocinado antes
          this.yaCompletadaAntes.set(true);
        } else {
          // Primera vez cocinando (monedas puede ser 0 si se alcanzó el límite diario)
          if (monedas > 0) {
            this.lanzarToast(`+${monedas} 🪙`);
          }
          // Verificar logros y mostrar notificación si se ha desbloqueado alguno
          this.logroService.verificarLogros().subscribe({
            next: (nuevos) => {
              nuevos.forEach((nombre, i) => {
                setTimeout(() => this.lanzarToast(`🏆 Logro desbloqueado: ${nombre}`), i * 1800);
              });
            },
            error: () => {}
          });
        }
      },
      error: (err) => console.error('Error al reclamar recompensa', err)
    });
  }

  private lanzarToast(msj: string) {
    this.mensajeNotificacion.set(msj);
    this.mostrarNotificacion.set(true);
    setTimeout(() => this.mostrarNotificacion.set(false), 4000);
  }

  reiniciarPasos(): void {
    this.pasosCompletados.set([]);
  }

  isPasoCompletado(idx: number): boolean {
    return this.pasosCompletados().includes(idx);
  }

  getIconoAlergeno(nombre: string): string {
    const n = nombre.toLowerCase();
    if (n.includes('gluten') || n.includes('trigo')) return '🌾';
    if (n.includes('leche') || n.includes('lactosa')) return '🥛';
    if (n.includes('huevo')) return '🥚';
    if (n.includes('frutos secos') || n.includes('nuez') || n.includes('almendra')) return '🥜';
    if (n.includes('pescado')) return '🐟';
    if (n.includes('marisco') || n.includes('gamba')) return '🦐';
    if (n.includes('soja')) return '🫘';
    if (n.includes('mostaza')) return '🍯';
    if (n.includes('apio')) return '🌿';
    if (n.includes('sésamo')) return '🥯';
    return '⚠️';
  }
  formatearTiempo(minutos: number | null): string {
    if (!minutos) return '—';
    if (minutos < 60) return `${minutos} min`;
    const h = Math.floor(minutos / 60);
    const m = minutos % 60;
    return m > 0 ? `${h}h ${m}min` : `${h}h`;
  }

  /**
   * Parsea "1. Step. 2. Step. 10. Step" en array de strings limpios.
   * Divide por el patrón de límite entre pasos: ". N. " para evitar
   * partir números de dos cifras (ej: "10" no debe partirse en "1" + "0").
   */
  parseLista(texto: string): string[] {
    if (!texto) return [];
    // Divide en los límites " . N. " entre pasos numerados
    const byBoundary = texto
      .split(/\.\s+(?=\d+\.\s)/)
      .map(s => s.replace(/^\d+\.\s*/, '').trim())
      .filter(Boolean);
    if (byBoundary.length > 1) return byBoundary;
    // Fallback: salto de línea
    const byNewline = texto.split(/\n+/).map(s => s.replace(/^\d+\.\s*/, '').trim()).filter(Boolean);
    if (byNewline.length > 1) return byNewline;
    // Fallback final: coma
    return texto.split(',').map(s => s.trim()).filter(Boolean);
  }

  etiquetaDificultad(d: string | null): string {
    const mapa: Record<string, string> = { BAJA: 'Fácil', MEDIA: 'Media', ALTA: 'Difícil' };
    return d ? (mapa[d] ?? d) : '—';
  }
}
