import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';
import { RecompensaService, UsuarioRecompensaResponse } from '../../services/recompensa.service';
import { UsuarioService } from '../../services/usuario.service';

/* Tipo que describe cada premio de la ruleta.
   Los 8 premios tienen probabilidades fijas en el frontend.
   Si en la BD existieran recompensas con esos mismos nombres, se enlazarían aquí. */
interface Premio {
  nombre: string;
  tipo: 'coins' | 'badge' | 'receta';
  rareza: 'comun' | 'raro' | 'epico' | 'legendario';
  emoji: string;
  descripcion: string;
  valor?: number;
  color: string;
  probabilidad: number;
}

/* Entrada en el historial visual de la página */
interface HistorialEntry {
  nombre: string;
  emoji: string;
  fecha: string;
}

@Component({
  selector: 'app-rewards',
  imports: [CommonModule, Revela],
  templateUrl: './rewards.html',
  styleUrl: './rewards.scss',
})
export class Rewards implements OnInit {

  readonly COSTE_TIRADA = 100;

  /* Balance de monedas = puntos del usuario desde la API */
  coins = signal(0);

  /* Estado de la ruleta */
  girando       = signal(false);
  anguloActual  = signal(0);
  premioGanado  = signal<Premio | null>(null);
  mostrarPremio = signal(false);

  /* Historial de recompensas ganadas en esta sesión + las de la BD */
  historial = signal<HistorialEntry[]>([]);

  /* Los 8 segmentos fijos de la ruleta */
  premios: Premio[] = [
    { nombre: '50 Coins',       tipo: 'coins',  rareza: 'comun',      emoji: '🪙', descripcion: 'Un puñado de monedas',              valor: 50,  color: '#E8B84B', probabilidad: 0.19 },
    { nombre: 'Afortunado',     tipo: 'badge',  rareza: 'raro',       emoji: '🍀', descripcion: 'Badge de la suerte',                           color: '#E05533', probabilidad: 0.09 },
    { nombre: '25 Coins',       tipo: 'coins',  rareza: 'comun',      emoji: '🪙', descripcion: 'Unas pocas monedas',               valor: 25,  color: '#D4A843', probabilidad: 0.22 },
    { nombre: 'Receta Secreta', tipo: 'receta', rareza: 'epico',      emoji: '📜', descripcion: 'Desbloquea una receta exclusiva',              color: '#4A7C59', probabilidad: 0.04 },
    { nombre: '100 Coins',      tipo: 'coins',  rareza: 'raro',       emoji: '💰', descripcion: 'Un buen botín de monedas',          valor: 100, color: '#C9952E', probabilidad: 0.08 },
    { nombre: 'Toca Hierro',    tipo: 'badge',  rareza: 'comun',      emoji: '🔨', descripcion: 'Has girado la ruleta 10 veces',                color: '#78716C', probabilidad: 0.15 },
    { nombre: '10 Coins',       tipo: 'coins',  rareza: 'comun',      emoji: '🪙', descripcion: 'Mejor que nada...',                 valor: 10,  color: '#BFA040', probabilidad: 0.21 },
    { nombre: 'Chef Dorado',    tipo: 'badge',  rareza: 'legendario', emoji: '👨‍🍳', descripcion: 'El badge más exclusivo de todos',              color: '#C13E28', probabilidad: 0.02 },
  ];

  puedeGirar = computed(() => this.coins() >= this.COSTE_TIRADA && !this.girando());

  constructor(
    private recompensaService: RecompensaService,
    private usuarioService: UsuarioService,
  ) {}

  ngOnInit() {
    /* Carga los puntos del usuario como balance de "coins" */
    this.usuarioService.getMe().subscribe({
      next: (u) => this.coins.set(u.puntos ?? 0),
      error: () => { /* si falla, coins se queda en 0 */ },
    });

    /* Carga el historial de recompensas ya ganadas en la BD */
    this.recompensaService.getMisRecompensas().subscribe({
      next: (p) => {
        const enBD = p.content.map(ur => ({
          nombre: ur.recompensa.nombre,
          emoji: '🎁',
          fecha: this.formatearFecha(ur.fechaObtenida),
        }));
        this.historial.set(enBD);
      },
      error: () => { /* historial vacío si falla */ },
    });
  }

  etiquetaRareza(rareza: string): string {
    const mapa: Record<string, string> = {
      comun: 'Común', raro: 'Raro', epico: 'Épico', legendario: 'Legendario',
    };
    return mapa[rareza] || rareza;
  }

  girar(): void {
    if (!this.puedeGirar()) return;

    this.coins.update(c => c - this.COSTE_TIRADA);
    this.girando.set(true);
    this.premioGanado.set(null);
    this.mostrarPremio.set(false);

    const indice = this.elegirPremioAleatorio();
    const premio = this.premios[indice];

    const vueltas = 5 + Math.floor(Math.random() * 3);
    const centroSegmento = indice * 45 + 22.5;
    const anguloFinal = vueltas * 360 + (360 - centroSegmento);
    this.anguloActual.set(anguloFinal);

    setTimeout(() => {
      this.girando.set(false);
      this.premioGanado.set(premio);
      this.mostrarPremio.set(true);

      if (premio.tipo === 'coins' && premio.valor) {
        this.coins.update(c => c + premio.valor!);
      }

      /* Añadir al historial visual */
      this.historial.update(h => [
        { nombre: premio.nombre, emoji: premio.emoji, fecha: 'Ahora mismo' },
        ...h,
      ]);

      /* Registrar en la BD si hay un ID de recompensa que coincida */
      this.registrarEnBD(premio);

    }, 4300);
  }

  private elegirPremioAleatorio(): number {
    const rand = Math.random();
    let acumulado = 0;
    for (let i = 0; i < this.premios.length; i++) {
      acumulado += this.premios[i].probabilidad;
      if (rand <= acumulado) return i;
    }
    return this.premios.length - 1;
  }

  /* Busca en las recompensas de la BD una que coincida por nombre y la registra */
  private registrarEnBD(premio: Premio): void {
    /* Por ahora, el endpoint POST /mis-recompensas requiere un recompensaId UUID.
       Como los premios de la ruleta son ficticios (no tienen ID en la BD),
       este método es un placeholder para cuando se creen en la BD. */
  }

  recogerPremio(): void {
    this.mostrarPremio.set(false);
    this.premioGanado.set(null);
    setTimeout(() => this.anguloActual.set(0), 50);
  }

  private formatearFecha(iso: string | null | undefined): string {
    if (!iso) return '—';
    return new Date(iso).toLocaleDateString('es-ES', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  }
}
