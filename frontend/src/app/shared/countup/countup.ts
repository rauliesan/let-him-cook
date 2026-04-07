import { Directive, Input, OnChanges, SimpleChanges, HostBinding } from '@angular/core';

/**
 * Directiva CountUp — anima un número de 0 (o del valor anterior) al objetivo.
 * La duración se ajusta automáticamente según la magnitud del número:
 * más rápido para deltas pequeños (like +1), más suave para métricas grandes.
 *
 * Uso básico:            <span [countUp]="totalLikes"></span>
 * Con formato custom:    <strong [countUp]="totalUsuariosRaw" [formato]="fn"></strong>
 */
@Directive({
  selector: '[countUp]',
  standalone: true,
})
export class CountUp implements OnChanges {

  @Input('countUp') objetivo: number = 0;

  /* Función de formato opcional — si no se proporciona se muestra el número tal cual */
  @Input() formato?: (n: number) => string;

  @HostBinding('textContent') texto = '0';

  private rafId?: number;

  ngOnChanges(cambios: SimpleChanges) {
    if ('objetivo' in cambios) {
      const anterior = +(cambios['objetivo'].previousValue ?? 0);
      const actual   = this.objetivo;
      if (anterior !== actual) {
        this.animar(anterior, actual);
      }
    }
  }

  private animar(desde: number, hasta: number) {
    if (this.rafId) cancelAnimationFrame(this.rafId);

    const delta = Math.abs(hasta - desde);

    /* Para deltas pequeños (±1 al dar like) usa duración corta y directa */
    const duracion = delta <= 5
      ? 280
      : Math.min(1500, Math.max(500, Math.log10(hasta + 2) * 580));

    const inicio = performance.now();

    const step = (ahora: number) => {
      const t = Math.min(1, (ahora - inicio) / duracion);
      /* easeOutCubic — arranca rápido y frena al final */
      const e = 1 - Math.pow(1 - t, 3);
      const n = Math.round(desde + (hasta - desde) * e);
      this.texto = this.formato ? this.formato(n) : `${n}`;
      if (t < 1) {
        this.rafId = requestAnimationFrame(step);
      }
    };

    this.rafId = requestAnimationFrame(step);
  }
}
