import { Component, Input, HostListener, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-preloader',
  imports: [CommonModule],
  templateUrl: './preloader.html',
  styleUrl: './preloader.scss',
})
export class Preloader {
  @Input() visible = true;

  /* Cada capa se mueve a una velocidad distinta para crear profundidad */
  pxPatron  = signal('translate(0px, 0px)');
  pxAnillos = signal('translate(0px, 0px)');
  pxSarten  = signal('translate(0px, 0px)');
  pxMarca   = signal('translate(0px, 0px)');

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(e: MouseEvent): void {
    if (!this.visible) return;
    const x = (e.clientX / window.innerWidth  - 0.5) * 2; // -1 a 1
    const y = (e.clientY / window.innerHeight - 0.5) * 2;

    this.pxPatron.set( `translate(${(x *  4).toFixed(1)}px, ${(y *  3).toFixed(1)}px)`);
    this.pxAnillos.set(`translate(${(x *  9).toFixed(1)}px, ${(y *  7).toFixed(1)}px)`);
    this.pxSarten.set( `translate(${(x * 15).toFixed(1)}px, ${(y * 11).toFixed(1)}px)`);
    this.pxMarca.set(  `translate(${(x * -6).toFixed(1)}px, ${(y * -4).toFixed(1)}px)`);
  }
}
