import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';
import { RecompensaService, RecompensaResponse, UsuarioRecompensaResponse } from '../../services/recompensa.service';
import { UsuarioService } from '../../services/usuario.service';
import { LogroService, LogroResponse, UsuarioLogroResponse } from '../../services/logro.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-rewards',
  imports: [CommonModule, Revela],
  templateUrl: './rewards.html',
  styleUrl: './rewards.scss',
})
export class Rewards implements OnInit {

  readonly COSTE_TIRADA = 100;
  
  /* Paginación de premios conseguidos */
  private readonly TAM_PAGINA = 5;
  paginaActual = signal(0);
  hayMasPremios = signal(true);

  /* Balance de monedas */
  coins = signal(0);

  /* Estado de la tragaperras */
  girando       = signal(false);
  reels         = [signal('❓'), signal('❓'), signal('❓')];
  premioGanado  = signal<RecompensaResponse | null>(null);
  mostrarPremio = signal(false);

  /* Datos de la API */
  catalogoPremios = signal<RecompensaResponse[]>([]);
  misPremios      = signal<RecompensaResponse[]>([]);
  cargandoMisPremios = signal(false);

  /* Logros */
  todosLogros     = signal<LogroResponse[]>([]);
  misLogrosIds    = signal<Set<string>>(new Set());
  cargandoLogros  = signal(false);

  puedeGirar = computed(() => this.coins() >= this.COSTE_TIRADA && !this.girando());

  constructor(
    private recompensaService: RecompensaService,
    private usuarioService: UsuarioService,
    private logroService: LogroService,
  ) {}

  ngOnInit() {
    this.cargarUsuario();
    this.cargarCatalogo();
    this.cargarMisPremios();
    this.cargarLogros();
  }

  private cargarUsuario() {
    this.usuarioService.getMe().subscribe({
      next: (u) => this.coins.set(u.puntos ?? 0),
    });
  }

  private cargarCatalogo() {
    this.recompensaService.getTodas().subscribe({
      next: (res) => this.catalogoPremios.set(res),
    });
  }

  cargarMisPremios(reset = true) {
    if (reset) {
      this.paginaActual.set(0);
      this.misPremios.set([]);
      this.hayMasPremios.set(true);
    }

    if (!this.hayMasPremios() || this.cargandoMisPremios()) return;

    this.cargandoMisPremios.set(true);
    this.recompensaService.getMisRecompensas(this.paginaActual(), this.TAM_PAGINA)
      .pipe(finalize(() => this.cargandoMisPremios.set(false)))
      .subscribe({
        next: (res) => {
          const nuevos = res.content.map(ur => ur.recompensa);
          this.misPremios.update(list => [...list, ...nuevos]);
          this.hayMasPremios.set(res.number < res.totalPages - 1);
          this.paginaActual.update(p => p + 1);
        }
      });
  }

  girar(): void {
    if (!this.puedeGirar()) return;

    const catalogo = this.catalogoPremios();
    if (catalogo.length === 0) {
      alert('Error: No se han cargado los premios. Por favor, reinicia el servidor backend.');
      return;
    }

    // Deduzco puntos en el backend y luego inicio la animación
    this.usuarioService.cobrarTirada(this.COSTE_TIRADA).subscribe({
      next: () => {
        this.coins.update(c => c - this.COSTE_TIRADA);
        this.iniciarGiro(catalogo);
      },
      error: (err) => {
        alert(err.error?.mensaje || 'No tienes suficientes monedas');
        this.girando.set(false);
      }
    });
  }

  private iniciarGiro(catalogo: RecompensaResponse[]): void {
    this.girando.set(true);
    this.premioGanado.set(null);
    this.mostrarPremio.set(false);

    const random = Math.random();
    let acumulado = 0;
    let premioSeleccionado: RecompensaResponse | null = null;

    // Buscamos si ha caído en algún premio basado en su probabilidad individual
    for (const p of catalogo) {
      acumulado += p.probabilidad;
      if (random <= acumulado) {
        premioSeleccionado = p;
        break;
      }
    }

    // 2. Preparar los iconos finales
    let finales: string[];
    if (premioSeleccionado) {
      // GANA: 3 iguales
      finales = [premioSeleccionado.emoji, premioSeleccionado.emoji, premioSeleccionado.emoji];
    } else {
      // PIERDE: 3 distintos (o al menos no los 3 iguales)
      finales = this.generarPerdida(catalogo);
    }

    // 3. Ejecutar animación de rodillos
    this.animarRodillos(finales, premioSeleccionado);
  }

  private generarPerdida(catalogo: RecompensaResponse[]): string[] {
    const emojis = catalogo.map(c => c.emoji);
    const r1 = emojis[Math.floor(Math.random() * emojis.length)];
    let r2 = emojis[Math.floor(Math.random() * emojis.length)];
    let r3 = emojis[Math.floor(Math.random() * emojis.length)];

    // Asegurar que NO sean los 3 iguales
    while (r1 === r2 && r2 === r3) {
      r3 = emojis[Math.floor(Math.random() * emojis.length)];
    }
    return [r1, r2, r3];
  }

  private animarRodillos(finales: string[], premio: RecompensaResponse | null) {
    const duraciones = [2000, 2800, 3600];
    
    duraciones.forEach((ms, i) => {
      setTimeout(() => {
        this.reels[i].set(finales[i]);
        
        // Cuando se detiene el último rodillo (i === 2)
        if (i === 2) {
          // Esperamos un poco más para que el usuario vea el resultado antes del popup
          setTimeout(() => {
            this.finalizarGiro(premio);
          }, 800);
        }
      }, ms);
    });
  }

  private finalizarGiro(premio: RecompensaResponse | null) {
    this.girando.set(false);
    if (premio) {
      this.premioGanado.set(premio);
      this.mostrarPremio.set(true);

      // Registrar en la BD
      this.recompensaService.concederRecompensa(premio.id).subscribe({
        next: () => {
          this.cargarMisPremios(true);
          // Comprobar logros desbloqueables tras ganar
          this.intentarLogrosSlot();
        },
        error: (err) => {
          if (err.status === 409) {
             console.log('Ya tienes este premio.');
          }
          // Intentar logros igualmente (p.ej. si el premio era duplicado)
          this.intentarLogrosSlot();
        }
      });
    }
  }

  /**
   * Intenta conceder logros relacionados con la tragaperras.
   * Busca en el catálogo de logros los que tengan iconoUrl de tipo slot/casino
   * o cuyo nombre contenga palabras clave relevantes.
   */
  private intentarLogrosSlot() {
    const logros = this.todosLogros();
    const palabrasClave = ['slot', 'casino', 'suerte', 'premio', 'recompensa', 'primera', 'coleccion'];
    for (const logro of logros) {
      const texto = (logro.nombre + ' ' + (logro.descripcion ?? '')).toLowerCase();
      if (palabrasClave.some(p => texto.includes(p))) {
        // Intentar conceder; silencioso si ya lo tiene (409)
        this.logroService.concederLogro(logro.id).subscribe({
          next: () => {
            this.misLogrosIds.update(s => new Set([...s, logro.id]));
          },
          error: () => {},
        });
      }
    }
  }

  cargarLogros() {
    this.cargandoLogros.set(true);
    this.logroService.getTodosLogros().subscribe({
      next: (todos) => {
        this.todosLogros.set(todos);
        // Cargar cuáles tiene el usuario
        this.logroService.getMisLogros(0, 200).subscribe({
          next: (res) => {
            this.misLogrosIds.set(new Set(res.content.map(ul => ul.logro.id)));
            this.cargandoLogros.set(false);
          },
          error: () => this.cargandoLogros.set(false),
        });
      },
      error: () => this.cargandoLogros.set(false),
    });
  }

  tieneLogro(id: string): boolean { return this.misLogrosIds().has(id); }

  recogerPremio(): void {
    this.mostrarPremio.set(false);
    this.premioGanado.set(null);
  }

  etiquetaRareza(rareza: string): string {
    const mapa: Record<string, string> = {
      comun: 'Común', raro: 'Raro', epico: 'Épico', legendario: 'Legendario',
    };
    return mapa[rareza] || rareza;
  }
}
