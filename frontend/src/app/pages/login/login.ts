import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  modoActivo = signal<'login' | 'registro'>('login');

  cambiarModo(modo: 'login' | 'registro') {
    this.modoActivo.set(modo);
  }
}
