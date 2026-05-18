import { Directive, ElementRef, Input, OnDestroy, OnInit, signal } from '@angular/core';

/* Directiva que añade la clase 'visible' cuando el elemento entra en el viewport.
   Usa un Signal para que el cambio se detecte incluso fuera de la zona de Angular. */
@Directive({
  selector: '[revela]',
  standalone: true,
  host: { '[class.visible]': 'esVisible()' }
})
export class Revela implements OnInit, OnDestroy {
  @Input() retardo = 0;

  esVisible = signal(false);
  private observer!: IntersectionObserver;

  constructor(private el: ElementRef<HTMLElement>) {}

  ngOnInit() {
    this.observer = new IntersectionObserver(
      ([entrada]) => {
        if (entrada.isIntersecting) {
          setTimeout(() => {
            this.esVisible.set(true);
          }, this.retardo);
          this.observer.disconnect();
        }
      },
      { threshold: 0.1 }
    );
    this.observer.observe(this.el.nativeElement);
  }

  ngOnDestroy() {
    this.observer?.disconnect();
  }
}
