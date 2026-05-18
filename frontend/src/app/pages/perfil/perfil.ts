import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { AuthService } from '../../services/auth.service';
import { UsuarioService, UsuarioResponse } from '../../services/usuario.service';
import { LogroService, LogroResponse, UsuarioLogroResponse } from '../../services/logro.service';
import { RecompensaService, UsuarioRecompensaResponse } from '../../services/recompensa.service';
import { RecetaService, RecetaResponse } from '../../services/receta.service';
import { ForoService } from '../../services/foro.service';
import { IaService } from '../../services/ia.service';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule, FormsModule, RouterLink, Revela],
  templateUrl: './perfil.html',
  styleUrl: './perfil.scss',
})
export class Perfil implements OnInit {

  /* Datos cargados desde la API */
  usuario        = signal<UsuarioResponse | null>(null);
  todosLogros    = signal<LogroResponse[]>([]);
  misLogros      = signal<UsuarioLogroResponse[]>([]);
  misRecompensas = signal<UsuarioRecompensaResponse[]>([]);
  misRecetas     = signal<RecetaResponse[]>([]);

  /* IDs de logros obtenidos para marcar cuáles están desbloqueados */
  misLogrosIds = computed(() => new Set(this.misLogros().map(ul => ul.logro.id)));

  tieneLogro(id: string): boolean { return this.misLogrosIds().has(id); }

  fechaLogro(id: string): string | null {
    return this.misLogros().find(ul => ul.logro.id === id)?.fechaObtenido ?? null;
  }

  /* Modal compartir en foro */
  recetaACompartir  = signal<RecetaResponse | null>(null);
  foroTitulo        = '';
  foroContenido     = '';
  publicandoEnForo  = signal(false);
  errorForo         = signal<string | null>(null);
  foroExito         = signal(false);

  /* Estado de carga */
  cargando   = signal(true);
  errorCarga = signal<string | null>(null);

  /* Tab activa */
  tabActiva = signal('logros');

  /* ── Configuración IA personalizada ── */
  editandoIa    = signal(false);
  iaApiKeyInput  = '';
  iaEndpointInput = '';
  iaModeloInput  = '';
  guardandoIa   = signal(false);
  errorIa       = signal<string | null>(null);

  modelosBd       = signal<any[]>([]);
  cargandoModelos = signal(false);

  /* ── Foto de perfil ── */
  subiendoFoto  = signal(false);
  errorFoto     = signal<string | null>(null);

  constructor(
    public auth: AuthService,
    private usuarioService: UsuarioService,
    private logroService: LogroService,
    private recompensaService: RecompensaService,
    private recetaService: RecetaService,
    private foroService: ForoService,
    private iaService: IaService,
  ) {}

  ngOnInit() {
    this.cargarDatos();
    this.cargarModelosBd();
  }

  cargarModelosBd() {
    this.cargandoModelos.set(true);
    this.iaService.getModelosDisponibles().subscribe({
      next: m => {
        this.modelosBd.set(m);
        this.cargandoModelos.set(false);
      },
      error: () => this.cargandoModelos.set(false)
    });
  }

  cargarDatos() {
    this.cargando.set(true);
    this.errorCarga.set(null);

    /* Carga el perfil primero y luego logros y recompensas en paralelo */
    this.usuarioService.getMe().subscribe({
      next: (u) => {
        this.usuario.set(u);

        this.logroService.getTodosLogros().subscribe({
          next: (todos) => this.todosLogros.set(todos),
          error: () => this.todosLogros.set([]),
        });

        this.logroService.getMisLogros().subscribe({
          next: (p) => this.misLogros.set(p.content),
          error: () => this.misLogros.set([]),
        });

        this.recompensaService.getMisRecompensas().subscribe({
          next: (p) => this.misRecompensas.set(p.content),
          error: () => this.misRecompensas.set([]),
        });

        this.recetaService.getMisRecetas().subscribe({
          next: (r) => this.misRecetas.set(r),
          error: () => this.misRecetas.set([]),
        });

        this.cargando.set(false);
      },
      error: () => {
        this.errorCarga.set('No se pudieron cargar los datos del perfil.');
        this.cargando.set(false);
      },
    });
  }

  /* Tabs con cantidades reales */
  tabs = computed(() => [
    { id: 'logros',      label: 'Logros',      cantidad: this.misLogros().length,      icono: '🏆' },
    { id: 'recompensas', label: 'Recompensas', cantidad: this.misRecompensas().length, icono: '🎁' },
    { id: 'recetas',     label: 'Mis recetas', cantidad: this.misRecetas().length,     icono: '🍳' },
  ]);

  /* Iniciales para el avatar */
  iniciales = computed(() => {
    const nombre = this.usuario()?.nombre ?? '';
    return nombre.split(' ').map(p => p[0]).join('').toUpperCase().slice(0, 2) || '?';
  });

  /* Título según nivel */
  tituloPorNivel = computed(() => {
    const nivel = this.usuario()?.nivel ?? 0;
    if (nivel >= 20) return 'Chef Maestro';
    if (nivel >= 15) return 'Chef Expert';
    if (nivel >= 10) return 'Chef Avanzado';
    if (nivel >= 5)  return 'Cocinero';
    return 'Aprendiz';
  });

  /* Porcentaje de la barra de experiencia */
  readonly RING_CIRCUMFERENCE = 2 * Math.PI * 62;

  /* Progreso dentro del nivel actual (0-99). Cada nivel = 100 pts. */
  progresoPuntos = computed(() => (this.usuario()?.puntos ?? 0) % 100);

  /* XP dentro del nivel actual para mostrar en pantalla */
  xpEnNivel = computed(() => (this.usuario()?.puntos ?? 0) % 100);

  /* Offset del anillo SVG: empieza lleno (sin progreso) y se reduce */
  xpRingOffset = computed(() =>
    this.RING_CIRCUMFERENCE * (1 - this.progresoPuntos() / 100)
  );

  /* ── Métodos de configuración IA ── */
  abrirEditarIa() {
    this.iaApiKeyInput   = '';
    this.iaEndpointInput = this.usuario()?.iaCustomEndpoint ?? 'https://api.deepseek.com/v1/chat/completions';
    this.iaModeloInput   = this.usuario()?.iaCustomModelo  ?? 'deepseek-chat';
    this.errorIa.set(null);
    this.editandoIa.set(true);
  }

  setPreset(preset: 'deepseek' | 'openai' | 'openrouter' | 'ollama') {
    if (preset === 'deepseek') {
      this.iaEndpointInput = 'https://api.deepseek.com/v1/chat/completions';
      this.iaModeloInput   = 'deepseek-chat';
    } else if (preset === 'openai') {
      this.iaEndpointInput = 'https://api.openai.com/v1/chat/completions';
      this.iaModeloInput   = 'gpt-4o-mini';
    } else if (preset === 'openrouter') {
      this.iaEndpointInput = 'https://openrouter.ai/api/v1/chat/completions';
      this.iaModeloInput   = 'deepseek/deepseek-chat:free';
    } else {
      this.iaEndpointInput = 'http://localhost:11434/v1/chat/completions';
      this.iaModeloInput   = 'llama3.2';
    }
  }

  guardarIaConfig() {
    if (!this.iaApiKeyInput.trim()) return;
    this.guardandoIa.set(true);
    this.errorIa.set(null);

    this.usuarioService.guardarIaConfig(
      this.iaApiKeyInput.trim(),
      this.iaEndpointInput.trim() || undefined,
      this.iaModeloInput.trim()   || undefined,
    ).subscribe({
      next: u => {
        this.usuario.update(curr => curr ? { ...curr,
          iaCustomConfigured: u.iaCustomConfigured,
          iaCustomEndpoint:   u.iaCustomEndpoint,
          iaCustomModelo:     u.iaCustomModelo,
        } : curr);
        this.guardandoIa.set(false);
        this.editandoIa.set(false);
      },
      error: err => {
        this.guardandoIa.set(false);
        this.errorIa.set(err.error?.mensaje ?? err.error?.message ?? 'Error al guardar la configuración.');
      },
    });
  }

  eliminarIaConfig() {
    this.usuarioService.eliminarIaConfig().subscribe({
      next: () => {
        this.usuario.update(curr => curr ? { ...curr,
          iaCustomConfigured: false,
          iaCustomEndpoint:   null,
          iaCustomModelo:     null,
        } : curr);

        // Si hay seleccionado un modelo de la BD, también lo limpiamos para volver al global
        if (this.usuario()?.iaModeloSeleccionadoNombre) {
            this.iaService.actualizarIaModelo(null).subscribe({
               next: () => {
                  this.usuario.update(curr => curr ? { ...curr, iaModeloSeleccionadoNombre: null } : curr);
               }
            });
        }
        
        this.editandoIa.set(false);
      },
      error: () => {},
    });
  }

  seleccionarModeloBD(modeloId: string) {
    if (!modeloId) {
       this.eliminarIaConfig(); // Vuelve al por defecto de la aplicación
       return;
    }

    this.guardandoIa.set(true);
    this.iaService.actualizarIaModelo(modeloId).subscribe({
      next: () => {
        const selected = this.modelosBd().find(m => m.id === modeloId);
        this.usuario.update(curr => curr ? { ...curr, 
            iaModeloSeleccionadoNombre: selected?.nombreModelo 
        } : curr);
        
        // Si teníamos una IA custom, la borramos para que prevalezca la de la BD
        if (this.usuario()?.iaCustomConfigured) {
           this.usuarioService.eliminarIaConfig().subscribe({
             next: () => {
                this.usuario.update(c => c ? { ...c, iaCustomConfigured: false, iaCustomEndpoint: null, iaCustomModelo: null } : c);
             }
           });
        }
        this.guardandoIa.set(false);
        this.editandoIa.set(false);
      },
      error: () => this.guardandoIa.set(false)
    });
  }

  /* Formatea números grandes: 1500 → "1.5k" */
  formatNum(n: number): string {
    if (n >= 1000) return (n / 1000).toFixed(1) + 'k';
    return String(n);
  }

  /* Fecha ISO → "12 ene 2025" */
  formatearFecha(iso: string | null | undefined): string {
    if (!iso) return '—';
    return new Date(iso).toLocaleDateString('es-ES', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  }

  /* ── Foto de perfil ── */
  /** Dispara el click sobre el input file oculto */
  abrirSelectorFoto() {
    const input = document.getElementById('foto-input') as HTMLInputElement;
    if (input) input.click();
  }

  /** Lee el archivo seleccionado, lo convierte a Base64 y lo sube al backend */
  onFotoSeleccionada(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const archivo = input.files[0];

    // Validar tipo y tamaño (máx 2 MB)
    if (!archivo.type.startsWith('image/')) {
      this.errorFoto.set('El archivo debe ser una imagen.');
      return;
    }
    if (archivo.size > 2 * 1024 * 1024) {
      this.errorFoto.set('La imagen no puede superar los 2 MB.');
      return;
    }

    this.errorFoto.set(null);
    this.subiendoFoto.set(true);

    const reader = new FileReader();
    reader.onload = () => {
      const base64 = reader.result as string;
      this.usuarioService.actualizarFoto(base64).subscribe({
        next: (u) => {
          this.usuario.update(curr => curr ? { ...curr, fotoUrl: u.fotoUrl } : curr);
          this.subiendoFoto.set(false);
          // Limpiar el input para permitir volver a seleccionar el mismo archivo
          input.value = '';
        },
        error: () => {
          this.errorFoto.set('No se pudo actualizar la foto. Inténtalo de nuevo.');
          this.subiendoFoto.set(false);
          input.value = '';
        },
      });
    };
    reader.readAsDataURL(archivo);
  }

  /* ── Compartir receta en el foro ── */
  abrirCompartirForo(receta: RecetaResponse) {
    this.recetaACompartir.set(receta);
    this.foroTitulo    = `Receta: ${receta.nombre}`;
    this.foroContenido = '';
    this.errorForo.set(null);
    this.foroExito.set(false);
  }

  cerrarModalForo() {
    this.recetaACompartir.set(null);
    this.foroExito.set(false);
  }

  compartirEnForo() {
    const receta = this.recetaACompartir();
    if (!receta || !this.foroTitulo.trim()) return;
    this.publicandoEnForo.set(true);
    this.errorForo.set(null);
    this.foroService.crearPost(this.foroTitulo.trim(), this.foroContenido.trim(), undefined, receta.id).subscribe({
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

  etiquetaDificultad(d: string | null): string {
    const mapa: Record<string, string> = { BAJA: 'Fácil', MEDIA: 'Media', ALTA: 'Difícil' };
    return d ? (mapa[d] ?? d) : '—';
  }

  /* ── Compartir Perfil ── */
  compartirPerfil() {
    const titulo = 'Let Him Cook';
    const texto = '¡Mira mi progreso en Let Him Cook!';
    const url = window.location.origin;

    if (navigator.share) {
      navigator.share({
        title: titulo,
        text: texto,
        url: url
      }).catch(console.error);
    } else {
      // Fallback si Web Share API no está disponible
      const contenidoCopiar = `${texto} ${url}`;
      navigator.clipboard.writeText(contenidoCopiar).then(() => {
        alert('Enlace copiado al portapapeles');
      }).catch(() => {
        alert('No se pudo copiar el enlace al portapapeles');
      });
    }
  }
}
