import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { Footer } from './shared/footer/footer';
import { Preloader } from './core/preloader/preloader';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { UsuarioService } from './services/usuario.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Footer, Preloader, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit, OnDestroy {
  cargando = signal(true);
  private heartbeatInterval: any;

  constructor(
    private auth: AuthService,
    private usuarioService: UsuarioService,
  ) {}

  ngOnInit() {
    setTimeout(() => this.cargando.set(false), 2800);

    // Heartbeat de presencia: cada 60 s mientras el usuario esté autenticado
    this.heartbeatInterval = setInterval(() => {
      if (this.auth.estaAutenticado()) {
        this.usuarioService.heartbeat().subscribe({ error: () => {} });
      }
    }, 60_000);

    // También enviar uno al inicio
    if (this.auth.estaAutenticado()) {
      this.usuarioService.heartbeat().subscribe({ error: () => {} });
    }
  }

  ngOnDestroy() {
    clearInterval(this.heartbeatInterval);
  }
}
