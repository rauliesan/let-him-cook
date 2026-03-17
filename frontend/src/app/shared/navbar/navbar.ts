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

  @HostListener('window:scroll')
  alScrollear() {
    this.scrolled.set(window.scrollY > 40);
  }

  toggleMenu() {
    this.menuAbierto.update(v => !v);
  }
}
