import { Component, HostListener, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

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

  /* AuthService inyectado para acceder al estado de autenticación */
  constructor(public auth: AuthService) {}

  @HostListener('window:scroll')
  alScrollear() {
    this.scrolled.set(window.scrollY > 40);

    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    this.scrollPorcentaje.set(docHeight > 0 ? (window.scrollY / docHeight) * 100 : 0);
  }

  toggleMenu() {
    this.menuAbierto.update(v => !v);
    document.body.style.overflow = this.menuAbierto() ? 'hidden' : '';
  }

  cerrarMenu() {
    this.menuAbierto.set(false);
    document.body.style.overflow = '';
  }

  /* Cierra el menú y llama al logout del servicio */
  salir() {
    this.cerrarMenu();
    this.auth.logout();
  }
}
