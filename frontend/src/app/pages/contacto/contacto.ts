import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Revela } from '../../shared/revela/revela';

export type Asunto =
  | ''
  | 'Problema técnico'
  | 'Sugerencia de mejora'
  | 'Reporte de contenido'
  | 'Colaboración'
  | 'Otro';

const API = 'http://localhost:9999';

@Component({
  selector: 'app-contacto',
  imports: [CommonModule, FormsModule, Revela],
  templateUrl: './contacto.html',
  styleUrl: './contacto.scss',
})
export class Contacto {

  readonly SOPORTE_EMAIL = 'lethimcooksupport@gmail.com';

  /* ── Estado del formulario ── */
  nombre     = '';
  apellido   = '';
  email      = '';
  asunto: Asunto = '';
  mensaje    = '';
  privacidad = false;

  intentoEnvio = signal(false);
  enviando     = signal(false);
  enviado      = signal(false);
  errorEnvio   = signal<string | null>(null);

  constructor(private http: HttpClient) {}

  /* ── Validaciones ── */
  get nombreValido()    { return this.nombre.trim().length >= 2; }
  get apellidoValido()  { return this.apellido.trim().length >= 2; }
  get emailValido()     { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email); }
  get asuntoValido()    { return this.asunto !== ''; }
  get mensajeValido()   { return this.mensaje.trim().length >= 10; }

  get formularioValido() {
    return this.nombreValido && this.apellidoValido &&
           this.emailValido  && this.asuntoValido  &&
           this.mensajeValido && this.privacidad;
  }

  /* ── Envío al backend ── */
  enviar() {
    this.intentoEnvio.set(true);
    if (!this.formularioValido) return;

    this.enviando.set(true);
    this.errorEnvio.set(null);

    this.http.post(`${API}/contacto`, {
      nombre:   this.nombre.trim(),
      apellido: this.apellido.trim(),
      email:    this.email.trim(),
      asunto:   this.asunto,
      mensaje:  this.mensaje.trim(),
    }).subscribe({
      next: () => {
        this.enviando.set(false);
        this.enviado.set(true);
      },
      error: () => {
        this.enviando.set(false);
        this.errorEnvio.set('No se pudo enviar el mensaje. Inténtalo de nuevo o escríbenos directamente.');
      },
    });
  }

  reiniciar() {
    this.nombre      = '';
    this.apellido    = '';
    this.email       = '';
    this.asunto      = '';
    this.mensaje     = '';
    this.privacidad  = false;
    this.intentoEnvio.set(false);
    this.enviando.set(false);
    this.enviado.set(false);
    this.errorEnvio.set(null);
  }

  mostrarError(valido: boolean) {
    return this.intentoEnvio() && !valido;
  }
}
