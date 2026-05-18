import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

/* Respuesta que devuelve el backend al hacer login o registro */
export interface AuthResponse {
  id: string;
  token: string;
  email: string;
  nombre: string;
  rol: string;
}

/* Datos que se guardan en localStorage para sesión persistente */
export interface SesionUsuario {
  usuarioId: string;
  token: string;
  email: string;
  nombre: string;
  rol: string;
}

const API = 'http://localhost:9999';
const CLAVE_TOKEN = 'lhc_token';
const CLAVE_SESION = 'lhc_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  /* Signal reactivo, true si hay sesión activa */
  private _autenticado = signal<boolean>(this.hayToken());

  /* Signal con el nombre del usuario logueado (para la navbar) */
  private _sesion = signal<SesionUsuario | null>(this.cargarSesion());

  /* Acceso público de solo lectura */
  estaAutenticado = this._autenticado.asReadonly();
  sesion = this._sesion.asReadonly();

  constructor(private http: HttpClient, private router: Router) {}

  /* Inicio de sesión */
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API}/auth/login`, { email, password })
      .pipe(tap(res => this.guardarSesion(res)));
  }

  /* Registro de cuenta nueva, iaModeloSeleccionadoId es opcional en el backend */
  registro(nombre: string, email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API}/auth/register`, { nombre, email, password })
      .pipe(tap(res => this.guardarSesion(res)));
  }

  /* Cierre de sesión */
  logout(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    localStorage.removeItem(CLAVE_SESION);
    this._autenticado.set(false);
    this._sesion.set(null);
    this.router.navigate(['/login']);
  }

  /* Recuperación de contraseña - Paso 1 */
  solicitarRecuperacion(email: string): Observable<any> {
    return this.http.post(`${API}/auth/recuperar-password`, { email });
  }

  /* Recuperación de contraseña - Paso 2 */
  verificarCodigo(email: string, codigo: string): Observable<any> {
    return this.http.post(`${API}/auth/verificar-codigo`, { email, codigo });
  }

  /* Recuperación de contraseña - Paso 3 */
  resetPassword(email: string, codigo: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${API}/auth/reset-password`, { email, codigo, nuevaPassword });
  }

  /* Devuelve el token JWT o null */
  getToken(): string | null {
    return localStorage.getItem(CLAVE_TOKEN);
  }

  /* Guarda token y datos de sesión en localStorage y actualiza signals */
  private guardarSesion(res: AuthResponse): void {
    const sesion: SesionUsuario = {
      usuarioId: res.id,
      token: res.token,
      email: res.email,
      nombre: res.nombre,
      rol: res.rol,
    };
    localStorage.setItem(CLAVE_TOKEN, res.token);
    localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
    this._autenticado.set(true);
    this._sesion.set(sesion);
  }

  /* Carga la sesión guardada en localStorage al arrancar la app */
  private cargarSesion(): SesionUsuario | null {
    const raw = localStorage.getItem(CLAVE_SESION);
    if (!raw) return null;
    try { return JSON.parse(raw); }
    catch { return null; }
  }

  private hayToken(): boolean {
    return !!localStorage.getItem(CLAVE_TOKEN);
  }
}
