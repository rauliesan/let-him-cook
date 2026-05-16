import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  IaService,
  GenerarRecetasRequest,
  PublicarRecetaIaRequest,
  RecetaSugerencia,
} from '../../services/ia.service';
import { CocinarStateService } from '../../services/cocinar-state.service';

@Component({
  selector: 'app-cocinar',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cocinar.html',
  styleUrl: './cocinar.scss',
})
export class Cocinar {

  constructor(private iaService: IaService, public st: CocinarStateService) {}

  /* ── Ingredientes ── */
  agregarIngrediente() {
    const val = this.st.ingredienteInput.trim().replace(/,+$/, '');
    if (val && !this.st.ingredientes().includes(val)) {
      this.st.ingredientes.update(l => [...l, val]);
    }
    this.st.ingredienteInput = '';
  }

  quitarIngrediente(idx: number) {
    this.st.ingredientes.update(l => l.filter((_, i) => i !== idx));
  }

  onIngredienteKeydown(e: KeyboardEvent) {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      this.agregarIngrediente();
    }
  }

  /* ── Generar ── */
  generarRecetas() {
    if (this.st.ingredientes().length === 0) return;
    this.st.cargandoIA.set(true);
    this.st.errorIA.set(null);

    const req: GenerarRecetasRequest = {
      ingredientes: this.st.ingredientes(),
      preferencias: this.st.preferencias || undefined,
    };

    this.iaService.generarSugerencias(req).subscribe({
      next: sugs => {
        this.st.sugerencias.set(sugs);
        this.st.cargandoIA.set(false);
        this.st.paso.set('sugerencias');
      },
      error: err => {
        this.st.cargandoIA.set(false);
        this.st.errorIA.set(
          err.error?.mensaje ?? err.error?.message ?? 'Error al conectar con la IA. Configura tu IA en tu perfil.'
        );
      },
    });
  }

  /* ── Seleccionar sugerencia ── */
  seleccionarSugerencia(s: RecetaSugerencia) {
    this.st.sugerenciaSeleccionada.set(s);
    this.st.editorNombre = s.nombre;
    this.st.editorDescripcion = s.descripcion;
    this.st.editorIngredientes = s.ingredientes;
    this.st.editorInstrucciones = s.instrucciones;
    this.st.editorTiempoPreparacion = s.tiempoPreparacion ?? 30;
    this.st.editorDificultad = (s.dificultad as 'BAJA' | 'MEDIA' | 'ALTA') || 'MEDIA';
    this.st.editorCalorias = s.calorias ?? 0;
    this.st.editorAlergenos = s.alergenos ?? '';
    this.st.editorCategoriaNombre = s.categoria;
    this.st.editorCategoriaEmoji = s.categoriaEmoji;
    this.st.editorCategoriaColor = s.categoriaColor;
    this.st.paso.set('editor');
  }

  /* ── Publicar ── */
  publicarReceta() {
    if (!this.st.editorNombre.trim()) return;
    this.st.publicando.set(true);
    this.st.errorPublicar.set(null);

    const req: PublicarRecetaIaRequest = {
      nombre: this.st.editorNombre,
      descripcion: this.st.editorDescripcion,
      ingredientes: this.st.editorIngredientes,
      instrucciones: this.st.editorInstrucciones || undefined,
      tiempoPreparacion: this.st.editorTiempoPreparacion || undefined,
      dificultad: this.st.editorDificultad,
      calorias: this.st.editorCalorias || undefined,
      alergenos: this.st.editorAlergenos || undefined,
      esPublica: this.st.editorEsPublica,
      categoriaNombre: this.st.editorCategoriaNombre || undefined,
      categoriaEmoji: this.st.editorCategoriaEmoji || undefined,
      categoriaColor: this.st.editorCategoriaColor || undefined,
    };

    this.iaService.publicarReceta(req).subscribe({
      next: res => {
        this.st.resultado.set(res);
        this.st.publicando.set(false);
        this.st.paso.set('exito');
      },
      error: err => {
        this.st.publicando.set(false);
        this.st.errorPublicar.set(
          err.error?.mensaje ?? err.error?.message ?? 'Error al publicar la receta. Inténtalo de nuevo.'
        );
      },
    });
  }

  /* ── Helpers ── */
  dificuladLabel = computed(() => {
    const map: Record<string, string> = { BAJA: 'Fácil', MEDIA: 'Media', ALTA: 'Difícil' };
    return map[this.st.editorDificultad] ?? this.st.editorDificultad;
  });

  volver() {
    const p = this.st.paso();
    if (p === 'sugerencias') this.st.paso.set('ingredientes');
    else if (p === 'editor') this.st.paso.set('sugerencias');
  }

  confettiItems = [1,2,3,4,5,6,7,8,9,10,11,12];
}
