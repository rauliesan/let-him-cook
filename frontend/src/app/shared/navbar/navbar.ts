import { Component, HostListener, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  scrolled = signal(false);
  menuAbierto = signal(false);
  scrollPorcentaje = signal(0);

  @HostListener('window:scroll')
  alScrollear() {
    this.scrolled.set(window.scrollY > 40);

    /* Calcula el porcentaje de scroll para la barra de progreso */
    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    this.scrollPorcentaje.set(docHeight > 0 ? (window.scrollY / docHeight) * 100 : 0);
  }

  toggleMenu() {
    this.menuAbierto.update(v => !v);
    /* Bloquea el scroll del body cuando el menu esta abierto */
    document.body.style.overflow = this.menuAbierto() ? 'hidden' : '';
  }

  cerrarMenu() {
    this.menuAbierto.set(false);
    document.body.style.overflow = '';
  }
}
