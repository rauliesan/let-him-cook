import { Component, computed, signal } from '@angular/core';
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
import { ForoService } from '../../services/foro.service';

@Component({
  selector: 'app-cocinar',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cocinar.html',
  styleUrl: './cocinar.scss',
})
export class Cocinar {

  /* Modal compartir en foro */
  foroTitulo        = '';
  foroContenido     = '';
  publicandoEnForo  = signal(false);
  errorForo         = signal<string | null>(null);
  foroExito         = signal(false);
  modalForoAbierto  = signal(false);

  /** Toggle del paso 3: publicar automáticamente en el foro al guardar */
  compartirEnForoAlPublicar = false;

  constructor(private iaService: IaService, public st: CocinarStateService, private foroService: ForoService) {}

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
    this.compartirEnForoAlPublicar = false;
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

        // Si el toggle de foro estaba activo, publicamos automáticamente
        if (this.compartirEnForoAlPublicar) {
          this.foroService.crearPost(
            `Receta: ${res.receta.nombre}`,
            res.receta.descripcion || '',
            undefined,
            res.receta.id
          ).subscribe();
          this.compartirEnForoAlPublicar = false;
        }
      },
      error: err => {
        this.st.publicando.set(false);
        this.st.errorPublicar.set(
          err.error?.mensaje ?? err.error?.message ?? 'Error al publicar la receta. Inténtalo de nuevo.'
        );
      },
    });
  }

  /* ── Helpers para parsear texto de la IA ── */
  /** Parsea ingredientes: "1. X. 2. Y." → ['X', 'Y'] */
  get ingredientesLista(): string[] {
    return this.parsearLista(this.st.editorIngredientes);
  }

  /** Parsea instrucciones: "1. Paso uno. 2. Paso dos." → ['Paso uno', 'Paso dos'] */
  get pasosLista(): string[] {
    return this.parsearLista(this.st.editorInstrucciones);
  }

  private parsearLista(texto: string): string[] {
    if (!texto) return [];
    // Divide en los límites ". N. " entre pasos numerados
    // Usar \.\\s+(?=\\d+\\.\\s) evita partir números de dos cifras como "10" → "1" + "0"
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

  dificuladLabel = computed(() => {
    const map: Record<string, string> = { BAJA: '🟢 Fácil', MEDIA: '🟡 Media', ALTA: '🔴 Difícil' };
    return map[this.st.editorDificultad] ?? this.st.editorDificultad;
  });

  volver() {
    const p = this.st.paso();
    if (p === 'sugerencias') this.st.paso.set('ingredientes');
    else if (p === 'editor') this.st.paso.set('sugerencias');
  }

  /* ── Compartir en foro (modal post-publicación) ── */
  abrirCompartirForo() {
    const res = this.st.resultado();
    if (!res) return;
    this.foroTitulo    = `Receta: ${res.receta.nombre}`;
    this.foroContenido = res.receta.descripcion || '';
    this.errorForo.set(null);
    this.foroExito.set(false);
    this.modalForoAbierto.set(true);
  }

  cerrarModalForo() {
    this.modalForoAbierto.set(false);
    this.foroExito.set(false);
  }

  compartirEnForo() {
    const res = this.st.resultado();
    if (!res || !this.foroTitulo.trim()) return;
    this.publicandoEnForo.set(true);
    this.errorForo.set(null);
    this.foroService.crearPost(
      this.foroTitulo.trim(),
      this.foroContenido.trim(),
      undefined,
      res.receta.id
    ).subscribe({
      next: () => {
        this.publicandoEnForo.set(false);
        this.foroExito.set(true);
        setTimeout(() => this.cerrarModalForo(), 1800);
      },
      error: (err) => {
        this.publicandoEnForo.set(false);
        this.errorForo.set(err.error?.mensaje ?? err.error?.message ?? 'Error al publicar en el foro.');
      },
    });
  }

  confettiItems = [1,2,3,4,5,6,7,8,9,10,11,12];
}
