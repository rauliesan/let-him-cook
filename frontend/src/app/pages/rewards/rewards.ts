import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';

/* Tipo que describe cada premio de la ruleta.
   Mapeado a la tabla 'recompensa' de la base de datos:
   nombre, descripcion, probabilidad (0-1 double precision) */
interface Premio {
  nombre: string;
  tipo: 'coins' | 'badge' | 'receta';
  rareza: 'comun' | 'raro' | 'epico' | 'legendario';
  emoji: string;
  descripcion: string;
  valor?: number;         // cantidad de coins si el premio es de tipo coins
  color: string;          // color del segmento de la ruleta
  probabilidad: number;   // probabilidad real entre 0 y 1, mapeada a recompensa.probabilidad
}

/* Entrada en el historial de premios obtenidos */
interface HistorialEntry {
  premio: Premio;
  fecha: string;
}

@Component({
  selector: 'app-rewards',
  imports: [CommonModule, Revela],
  templateUrl: './rewards.html',
  styleUrl: './rewards.scss',
})
export class Rewards {

  /* Coste de cada tirada */
  readonly COSTE_TIRADA = 100;

  /* Balance de monedas del usuario */
  coins = signal(1250);

  /* Estado de la ruleta */
  girando = signal(false);
  anguloActual = signal(0);
  premioGanado = signal<Premio | null>(null);
  mostrarPremio = signal(false);

  /* Historial de premios obtenidos — mapeado a usuario_recompensa en la base de datos */
  historial = signal<HistorialEntry[]>([
    {
      premio: { nombre: '50 Coins', tipo: 'coins', rareza: 'comun', emoji: '🪙', descripcion: 'Un punado de monedas', valor: 50, color: '#E8B84B', probabilidad: 0.19 },
      fecha: 'Hace 2 dias',
    },
    {
      premio: { nombre: 'Afortunado', tipo: 'badge', rareza: 'raro', emoji: '🍀', descripcion: 'Ganaste 3 veces seguidas', color: '#E05533', probabilidad: 0.09 },
      fecha: 'Hace 5 dias',
    },
  ]);

  /* Los 8 segmentos de la ruleta con sus premios.
     El campo 'probabilidad' es un valor entre 0 y 1 que indica la probabilidad exacta,
     mapeado directamente al campo 'probabilidad' de la tabla 'recompensa' en la base de datos.
     La suma de todas las probabilidades es 1.0. */
  premios: Premio[] = [
    { nombre: '50 Coins',       tipo: 'coins',  rareza: 'comun',      emoji: '🪙', descripcion: 'Un punado de monedas',              valor: 50,  color: '#E8B84B', probabilidad: 0.19 },
    { nombre: 'Afortunado',     tipo: 'badge',  rareza: 'raro',       emoji: '🍀', descripcion: 'Badge de la suerte',                           color: '#E05533', probabilidad: 0.09 },
    { nombre: '25 Coins',       tipo: 'coins',  rareza: 'comun',      emoji: '🪙', descripcion: 'Unas pocas monedas',               valor: 25,  color: '#D4A843', probabilidad: 0.22 },
    { nombre: 'Receta Secreta', tipo: 'receta', rareza: 'epico',      emoji: '📜', descripcion: 'Desbloquea una receta exclusiva',              color: '#4A7C59', probabilidad: 0.04 },
    { nombre: '100 Coins',      tipo: 'coins',  rareza: 'raro',       emoji: '💰', descripcion: 'Un buen botin de monedas',          valor: 100, color: '#C9952E', probabilidad: 0.08 },
    { nombre: 'Toca Hierro',    tipo: 'badge',  rareza: 'comun',      emoji: '🔨', descripcion: 'Has girado la ruleta 10 veces',                color: '#78716C', probabilidad: 0.15 },
    { nombre: '10 Coins',       tipo: 'coins',  rareza: 'comun',      emoji: '🪙', descripcion: 'Mejor que nada...',                 valor: 10,  color: '#BFA040', probabilidad: 0.21 },
    { nombre: 'Chef Dorado',    tipo: 'badge',  rareza: 'legendario', emoji: '👨‍🍳', descripcion: 'El badge mas exclusivo de todos',              color: '#C13E28', probabilidad: 0.02 },
  ];

  /* Computed: si puede girar (tiene coins y no esta girando ya) */
  puedeGirar = computed(() => this.coins() >= this.COSTE_TIRADA && !this.girando());

  /* Etiqueta de rareza traducida */
  etiquetaRareza(rareza: string): string {
    const mapa: Record<string, string> = {
      comun: 'Comun',
      raro: 'Raro',
      epico: 'Epico',
      legendario: 'Legendario',
    };
    return mapa[rareza] || rareza;
  }

  /* Mecánica del giro */

  girar(): void {
    if (!this.puedeGirar()) return;

    /* Descontar el coste */
    this.coins.update(c => c - this.COSTE_TIRADA);
    this.girando.set(true);
    this.premioGanado.set(null);
    this.mostrarPremio.set(false);

    /* Elegir un premio con probabilidad ponderada (weighted random).
       Los premios comunes salen mucho mas a menudo que los legendarios */
    const indice = this.elegirPremioAleatorio();
    const premio = this.premios[indice];

    /* Calcular el angulo final.
       Cada segmento ocupa 45 grados (360/8).
       El angulo debe caer en el CENTRO del segmento ganador.
       Se anaden 5-8 vueltas completas antes de parar.
       La flecha apunta desde ARRIBA (0 grados = top), asi que:
       - segmento 0 esta en 0-45 deg, su centro en 22.5 deg
       - pero la ruleta gira en sentido horario, asi que se resta del total */
    const vueltas = 5 + Math.floor(Math.random() * 3); // 5 a 7 vueltas
    const centroSegmento = indice * 45 + 22.5;
    /* Para que la flecha (arriba) apunte al segmento, giramos: 360 - centroSegmento */
    const anguloFinal = vueltas * 360 + (360 - centroSegmento);

    /* Aplicar el angulo — la transicion CSS hace el resto */
    this.anguloActual.set(anguloFinal);

    /* Tras la animacion (4s + 300ms de margen), mostrar el premio */
    setTimeout(() => {
      this.girando.set(false);
      this.premioGanado.set(premio);
      this.mostrarPremio.set(true);

      /* Si el premio son coins, sumarlas al balance */
      if (premio.tipo === 'coins' && premio.valor) {
        this.coins.update(c => c + premio.valor!);
      }

      /* Anadir al historial */
      this.historial.update(h => [
        { premio, fecha: 'Ahora mismo' },
        ...h,
      ]);
    }, 4300);
  }

  /* Seleccion aleatoria ponderada usando los valores de probabilidad (suman 1.0).
     Genera un numero entre 0 y 1 y recorre las probabilidades acumuladas. */
  private elegirPremioAleatorio(): number {
    const rand = Math.random();
    let acumulado = 0;
    for (let i = 0; i < this.premios.length; i++) {
      acumulado += this.premios[i].probabilidad;
      if (rand <= acumulado) return i;
    }
    return this.premios.length - 1; // fallback
  }

  /* Cerrar el overlay de premio y resetear el angulo para el proximo giro */
  recogerPremio(): void {
    this.mostrarPremio.set(false);
    this.premioGanado.set(null);
    /* Resetear angulo sin transicion (se hace via clase CSS) */
    setTimeout(() => this.anguloActual.set(0), 50);
  }
}
