import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { RecetaService, RecetaResponse } from '../../services/receta.service';

@Component({
  selector: 'app-receta-detalle',
  standalone: true,
  imports: [CommonModule, RouterLink, Revela],
  templateUrl: './receta-detalle.html',
  styleUrl: './receta-detalle.scss',
})
export class RecetaDetalle implements OnInit {

  receta  = signal<RecetaResponse | null>(null);
  cargando = signal(true);
  error    = signal<string | null>(null);

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
  monedasGanadas      = signal<number | null>(null);

  // Notificación flotante
  mostrarNotificacion = signal(false);
  mensajeNotificacion = signal('');

  ingredientesList = computed(() => {
    const ing = this.receta()?.ingredientes ?? '';
    return ing.split(',').map(s => s.trim()).filter(Boolean);
  });

  instruccionesList = computed(() => {
    const raw = this.receta()?.instrucciones ?? '';
    const pasos = raw.split(/Paso\s*\d+\s*:\s*/i).map(s => s.trim()).filter(Boolean);
    if (pasos.length > 1) return pasos;
    return raw.split('\n').map(s => s.trim()).filter(Boolean);
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
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cargarReceta(id);
      this.cargarProgresoLocal(id);
      
      // Si ya estaba al 100% al cargar, intentamos reclamar (el backend ya controla el límite diario)
      setTimeout(() => {
        if (this.todoCompletado() && !this.recompensaReclamada()) {
          this.reclamarRecompensa();
        }
      }, 500);
    } else {
      this.error.set('ID de receta no válido.');
      this.cargando.set(false);
    }
  }

  private cargarReceta(id: string) {
    this.recetaService.getPorId(id).subscribe({
      next: (r) => { 
        this.receta.set(r); 
        this.cargando.set(false); 
      },
      error: () => { this.error.set('No se pudo cargar la receta.'); this.cargando.set(false); },
    });
  }

  private cargarProgresoLocal(recetaId: string): void {
    const guardado = localStorage.getItem(`progreso_receta_${recetaId}`);
    if (guardado) {
      try {
        this.pasosCompletados.set(JSON.parse(guardado));
      } catch (e) {
        console.error('Error cargando progreso local', e);
      }
    }
  }

  private guardarProgresoLocal(): void {
    const id = this.receta()?.id;
    if (id) {
      localStorage.setItem(`progreso_receta_${id}`, JSON.stringify(this.pasosCompletados()));
    }
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
    this.guardarProgresoLocal();

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
        
        if (monedas > 0) {
          this.lanzarToast(`+${monedas} 🪙`);
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
    this.guardarProgresoLocal();
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

  etiquetaDificultad(d: string | null): string {
    const mapa: Record<string, string> = { BAJA: 'Fácil', MEDIA: 'Media', ALTA: 'Difícil' };
    return d ? (mapa[d] ?? d) : '—';
  }
}
