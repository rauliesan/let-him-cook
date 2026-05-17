import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { Revela } from '../../shared/revela/revela';
import { AuthService } from '../../services/auth.service';
import { UsuarioService, UsuarioResponse } from '../../services/usuario.service';
import { RecetaService, RecetaResponse } from '../../services/receta.service';

@Component({
  selector: 'app-perfil-publico',
  standalone: true,
  imports: [CommonModule, RouterLink, Revela],
  templateUrl: './perfil-publico.html',
  styleUrl: './perfil-publico.scss',
})
export class PerfilPublico implements OnInit {

  usuario   = signal<UsuarioResponse | null>(null);
  recetas   = signal<RecetaResponse[]>([]);
  cargando  = signal(true);
  error     = signal<string | null>(null);

  esAmigo         = signal(false);
  actualizandoAmigo = signal(false);

  readonly RING_CIRCUMFERENCE = 2 * Math.PI * 62;

  esPropioPerfilSignal = signal(false);

  progresoPuntos = computed(() => (this.usuario()?.puntos ?? 0) % 100);

  xpRingOffset = computed(() =>
    this.RING_CIRCUMFERENCE * (1 - this.progresoPuntos() / 100)
  );

  iniciales = computed(() => {
    const n = this.usuario()?.nombre ?? '';
    return n.split(' ').map(p => p[0]).join('').toUpperCase().slice(0, 2) || '?';
  });

  estaEnLinea = computed(() =>
    UsuarioService.estaEnLinea(this.usuario()?.ultimaConexion ?? null)
  );

  tituloPorNivel = computed(() => {
    const nivel = this.usuario()?.nivel ?? 0;
    if (nivel >= 20) return 'Chef Maestro';
    if (nivel >= 15) return 'Chef Expert';
    if (nivel >= 10) return 'Chef Avanzado';
    if (nivel >= 5)  return 'Cocinero';
    return 'Aprendiz';
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public  auth: AuthService,
    private usuarioService: UsuarioService,
    private recetaService: RecetaService,
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) { this.error.set('ID de usuario no válido.'); this.cargando.set(false); return; }

    const propioId = this.auth.sesion()?.usuarioId;
    if (propioId && propioId === id) {
      this.router.navigate(['/perfil']);
      return;
    }

    this.usuarioService.getById(id).subscribe({
      next: u => {
        this.usuario.set(u);
        this.cargando.set(false);
      },
      error: () => { this.error.set('No se pudo cargar el perfil.'); this.cargando.set(false); },
    });

    this.recetaService.getRecetasDeUsuario(id).subscribe({
      next: r => this.recetas.set(r),
      error: () => this.recetas.set([]),
    });

    this.usuarioService.esAmigo(id).subscribe({
      next: res => this.esAmigo.set(res.esAmigo),
      error: () => {},
    });
  }

  toggleAmigo() {
    const id = this.usuario()?.id;
    if (!id || this.actualizandoAmigo()) return;
    this.actualizandoAmigo.set(true);

    const accion$ = this.esAmigo()
      ? this.usuarioService.eliminarAmigo(id)
      : this.usuarioService.agregarAmigo(id);

    accion$.subscribe({
      next: () => {
        this.esAmigo.update(v => !v);
        this.actualizandoAmigo.set(false);
      },
      error: () => this.actualizandoAmigo.set(false),
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
