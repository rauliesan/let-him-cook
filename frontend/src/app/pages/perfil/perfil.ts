import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { AuthService } from '../../services/auth.service';
import { UsuarioService, UsuarioResponse } from '../../services/usuario.service';
import { LogroService, UsuarioLogroResponse } from '../../services/logro.service';
import { RecompensaService, UsuarioRecompensaResponse } from '../../services/recompensa.service';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule, FormsModule, RouterLink, Revela],
  templateUrl: './perfil.html',
  styleUrl: './perfil.scss',
})
export class Perfil implements OnInit {

  /* Datos cargados desde la API */
  usuario        = signal<UsuarioResponse | null>(null);
  misLogros      = signal<UsuarioLogroResponse[]>([]);
  misRecompensas = signal<UsuarioRecompensaResponse[]>([]);

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

  constructor(
    public auth: AuthService,
    private usuarioService: UsuarioService,
    private logroService: LogroService,
    private recompensaService: RecompensaService,
  ) {}

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    this.cargando.set(true);
    this.errorCarga.set(null);

    /* Carga el perfil primero y luego logros y recompensas en paralelo */
    this.usuarioService.getMe().subscribe({
      next: (u) => {
        this.usuario.set(u);

        this.logroService.getMisLogros().subscribe({
          next: (p) => this.misLogros.set(p.content),
          error: () => this.misLogros.set([]),
        });

        this.recompensaService.getMisRecompensas().subscribe({
          next: (p) => this.misRecompensas.set(p.content),
          error: () => this.misRecompensas.set([]),
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
    { id: 'logros',      label: 'Logros',     cantidad: this.misLogros().length,      icono: '🏆' },
    { id: 'recompensas', label: 'Recompensas', cantidad: this.misRecompensas().length, icono: '🎁' },
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
  progresoPuntos = computed(() => {
    const puntos = this.usuario()?.puntos ?? 0;
    const nivel  = Math.max(1, this.usuario()?.nivel ?? 1);
    const puntosPorNivel = nivel * 100;
    return Math.min(100, Math.round(((puntos % puntosPorNivel) / puntosPorNivel) * 100));
  });

  /* ── Métodos de configuración IA ── */
  abrirEditarIa() {
    this.iaApiKeyInput   = '';
    this.iaEndpointInput = this.usuario()?.iaCustomEndpoint ?? '';
    this.iaModeloInput   = this.usuario()?.iaCustomModelo  ?? '';
    this.errorIa.set(null);
    this.editandoIa.set(true);
  }

  setPreset(preset: 'deepseek' | 'openai' | 'ollama') {
    if (preset === 'deepseek') {
      this.iaEndpointInput = 'https://api.deepseek.com/v1/chat/completions';
      this.iaModeloInput   = 'deepseek-chat';
    } else if (preset === 'openai') {
      this.iaEndpointInput = 'https://api.openai.com/v1/chat/completions';
      this.iaModeloInput   = 'gpt-4o-mini';
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
        this.editandoIa.set(false);
      },
      error: () => {},
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
}
