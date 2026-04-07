import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  modoActivo = signal<'login' | 'registro'>('login');

  /* Campos del formulario de login */
  loginEmail    = '';
  loginPassword = '';

  /* Campos del formulario de registro */
  regNombre   = '';
  regEmail    = '';
  regPassword = '';
  regTerminos = false;

  /* Estado de la petición */
  cargando = signal(false);
  error    = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router) {}

  cambiarModo(modo: 'login' | 'registro') {
    this.modoActivo.set(modo);
    this.error.set(null);
  }

  /* Envío del formulario de login */
  submitLogin() {
    if (!this.loginEmail || !this.loginPassword) {
      this.error.set('Por favor completa todos los campos.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    this.auth.login(this.loginEmail, this.loginPassword).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        this.cargando.set(false);
        /* El backend devuelve 401 con un mensaje claro en el body */
        const msg = err.error?.message || err.error?.error || 'Credenciales incorrectas.';
        this.error.set(msg);
      },
    });
  }

  /* Envío del formulario de registro */
  submitRegistro() {
    if (!this.regNombre || !this.regEmail || !this.regPassword) {
      this.error.set('Por favor completa todos los campos.');
      return;
    }
    if (!this.regTerminos) {
      this.error.set('Debes aceptar los términos de servicio.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    this.auth.registro(this.regNombre, this.regEmail, this.regPassword).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        this.cargando.set(false);
        const msg = err.error?.message || err.error?.error || 'Error al crear la cuenta.';
        this.error.set(msg);
      },
    });
  }
}
