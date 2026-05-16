import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { RecetaService, RecetaResponse } from '../../services/receta.service';

@Component({
  selector: 'app-receta-detalle',
  imports: [CommonModule, RouterLink],
  templateUrl: './receta-detalle.html',
  styleUrl: './receta-detalle.scss',
})
export class RecetaDetalle implements OnInit {

  receta  = signal<RecetaResponse | null>(null);
  cargando = signal(true);
  error    = signal<string | null>(null);

  ingredientesList = computed(() => {
    const ing = this.receta()?.ingredientes ?? '';
    return ing.split(',').map(s => s.trim()).filter(Boolean);
  });

  instruccionesList = computed(() => {
    const raw = this.receta()?.instrucciones ?? '';
    // Split by "Paso N:" pattern, or by newlines, whichever yields steps
    const pasos = raw.split(/Paso\s*\d+\s*:\s*/i).map(s => s.trim()).filter(Boolean);
    if (pasos.length > 1) return pasos;
    // Fallback: split by newlines
    return raw.split('\n').map(s => s.trim()).filter(Boolean);
  });

  constructor(
    private route: ActivatedRoute,
    private recetaService: RecetaService,
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) { this.error.set('ID de receta no válido.'); this.cargando.set(false); return; }

    this.recetaService.getPorId(id).subscribe({
      next: (r) => { this.receta.set(r); this.cargando.set(false); },
      error: () => { this.error.set('No se pudo cargar la receta.'); this.cargando.set(false); },
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
