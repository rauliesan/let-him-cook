import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { RecetaService, RecetaResponse } from '../../services/receta.service';
import { IaService } from '../../services/ia.service';

@Component({
  selector: 'app-receta-detalle',
  imports: [CommonModule, RouterLink],
  templateUrl: './receta-detalle.html',
  styleUrl: './receta-detalle.scss',
})
export class RecetaDetalle implements OnInit {

  receta   = signal<RecetaResponse | null>(null);
  cargando = signal(true);
  error    = signal<string | null>(null);

  instrucciones          = signal<string | null>(null);
  generandoInstrucciones = signal(false);
  errorInstrucciones     = signal<string | null>(null);

  ingredientesList = computed(() => {
    const ing = this.receta()?.ingredientes ?? '';
    return ing.split(',').map(s => s.trim()).filter(Boolean);
  });

  instruccionesPasos = computed(() => {
    const text = this.instrucciones();
    if (!text) return [];
    return text.split(/\n+/).map(s => s.trim().replace(/^\d+[.)]\s*/, '')).filter(Boolean);
  });

  constructor(
    private route: ActivatedRoute,
    private recetaService: RecetaService,
    private iaService: IaService,
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) { this.error.set('ID de receta no válido.'); this.cargando.set(false); return; }

    this.recetaService.getPorId(id).subscribe({
      next: (r) => { this.receta.set(r); this.cargando.set(false); },
      error: () => { this.error.set('No se pudo cargar la receta.'); this.cargando.set(false); },
    });
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
