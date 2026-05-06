import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  modoActivo = signal<'login' | 'registro' | 'recuperar'>('login');

  /* Campos del formulario de login */
  loginEmail    = '';
  loginPassword = '';

  /* Campos del formulario de registro */
  regNombre   = '';
  regEmail    = '';
  regPassword = '';
  regTerminos = false;

  /* Campos de recuperación */
  recuperarPaso = signal<1 | 2 | 3 | 4>(1); // 1: Email, 2: Código, 3: Nueva Password, 4: Éxito
  recuperarEmail = '';
  recuperarCodigo = '';
  recuperarNuevaPassword = '';

  /* Estado de la petición */
  cargando = signal(false);
  error    = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    // 1. Check query params for mode (from navbar)
    this.route.queryParams.subscribe(params => {
      if (params['mode'] === 'registro' || params['mode'] === 'login' || params['mode'] === 'recuperar') {
        this.modoActivo.set(params['mode']);
      }
    });

    // 2. Load recovery state from session storage (so it survives a refresh)
    const savedPaso = sessionStorage.getItem('recuperarPaso');
    const savedEmail = sessionStorage.getItem('recuperarEmail');
    if (this.modoActivo() === 'recuperar' && savedPaso && savedEmail) {
      this.recuperarPaso.set(parseInt(savedPaso, 10) as any);
      this.recuperarEmail = savedEmail;
    }
  }

  cambiarModo(modo: 'login' | 'registro' | 'recuperar') {
    this.modoActivo.set(modo);
    this.error.set(null);
    this.recuperarPaso.set(1);
    this.recuperarEmail = '';
    this.recuperarCodigo = '';
    this.recuperarNuevaPassword = '';
    sessionStorage.removeItem('recuperarPaso');
    sessionStorage.removeItem('recuperarEmail');
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
        const msg = err.error?.mensaje || err.error?.message || 'Credenciales incorrectas.';
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
        const msg = err.error?.mensaje || err.error?.message || 'Error al crear la cuenta.';
        this.error.set(msg);
      },
    });
  }

  /* Recuperar contraseña - Paso 1: Solicitar código */
  submitSolicitarRecuperacion() {
    if (!this.recuperarEmail) {
      this.error.set('Por favor ingresa tu email.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    this.auth.solicitarRecuperacion(this.recuperarEmail).subscribe({
      next: () => {
        this.cargando.set(false);
        this.recuperarPaso.set(2);
        sessionStorage.setItem('recuperarPaso', '2');
        sessionStorage.setItem('recuperarEmail', this.recuperarEmail);
      },
      error: (err) => {
        this.cargando.set(false);
        // Mostrar el error si el email no está registrado (o cualquier otro error del servidor)
        this.error.set(err.error?.mensaje || 'Error al solicitar recuperación.');
      }
    });
  }

  /* Recuperar contraseña - Paso 2: Verificar código */
  submitVerificarCodigo() {
    if (!this.recuperarCodigo || this.recuperarCodigo.length !== 6) {
      this.error.set('El código debe tener 6 dígitos.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    this.auth.verificarCodigo(this.recuperarEmail, this.recuperarCodigo).subscribe({
      next: () => {
        this.cargando.set(false);
        this.recuperarPaso.set(3);
        sessionStorage.setItem('recuperarPaso', '3');
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err.error?.mensaje || 'Código inválido o expirado.');
      }
    });
  }

  /* Recuperar contraseña - Paso 3: Nueva contraseña */
  submitNuevaPassword() {
    if (!this.recuperarNuevaPassword || this.recuperarNuevaPassword.length < 8) {
      this.error.set('La nueva contraseña debe tener al menos 8 caracteres.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    this.auth.resetPassword(this.recuperarEmail, this.recuperarCodigo, this.recuperarNuevaPassword).subscribe({
      next: () => {
        this.cargando.set(false);
        this.recuperarPaso.set(4);
        sessionStorage.removeItem('recuperarPaso');
        sessionStorage.removeItem('recuperarEmail');
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err.error?.mensaje || 'Error al actualizar la contraseña.');
      }
    });
  }
}
