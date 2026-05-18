import { Injectable, signal, computed } from '@angular/core';
import { RecetaSugerencia, PublicarRecetaResponse } from './ia.service';

export type Paso = 'ingredientes' | 'sugerencias' | 'editor' | 'exito';

/**
 * Servicio singleton que persiste el estado del wizard de Cocinar
 * entre navegaciones. Al ser providedIn:'root' no se destruye al salir de la página.
 */
@Injectable({ providedIn: 'root' })
export class CocinarStateService {

  /* ── Estado de navegación ── */
  paso = signal<Paso>('ingredientes');

  /* ── Paso 1: ingredientes ── */
  ingredientes = signal<string[]>([]);
  ingredienteInput = '';
  preferencias = '';

  /* ── Paso 2: sugerencias ── */
  sugerencias = signal<RecetaSugerencia[]>([]);
  cargandoIA = signal(false);
  errorIA = signal<string | null>(null);
  sugerenciaSeleccionada = signal<RecetaSugerencia | null>(null);

  /* ── Paso 3: editor ── */
  editorNombre = '';
  editorDescripcion = '';
  editorIngredientes = '';
  editorInstrucciones = '';
  editorTiempoPreparacion = 30;
  editorDificultad: 'BAJA' | 'MEDIA' | 'ALTA' = 'MEDIA';
  editorCalorias = 0;
  editorAlergenos = '';
  editorEsPublica = true;
  editorCategoriaNombre = '';
  editorCategoriaEmoji = '';
  editorCategoriaColor = '';
  publicando = signal(false);
  errorPublicar = signal<string | null>(null);

  /* ── Paso 4: éxito ── */
  resultado = signal<PublicarRecetaResponse | null>(null);

  /* ── Computados ── */
  pasoNum = computed(() => {
    const m: Record<Paso, number> = { ingredientes: 1, sugerencias: 2, editor: 3, exito: 4 };
    return m[this.paso()];
  });

  reiniciar() {
    this.paso.set('ingredientes');
    this.ingredientes.set([]);
    this.ingredienteInput = '';
    this.preferencias = '';
    this.sugerencias.set([]);
    this.sugerenciaSeleccionada.set(null);
    this.resultado.set(null);
    this.errorIA.set(null);
    this.errorPublicar.set(null);
  }
}
