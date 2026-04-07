import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { AuthService } from '../../services/auth.service';
import { UsuarioService, UsuarioResponse } from '../../services/usuario.service';
import { LogroService, UsuarioLogroResponse } from '../../services/logro.service';
import { RecompensaService, UsuarioRecompensaResponse } from '../../services/recompensa.service';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule, RouterLink, Revela],
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
