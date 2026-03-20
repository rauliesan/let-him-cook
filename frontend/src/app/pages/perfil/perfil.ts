import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Revela } from '../../shared/revela/revela';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule, RouterLink, Revela],
  templateUrl: './perfil.html',
  styleUrl: './perfil.scss',
})
export class Perfil {
  tabActiva = signal('recetas');

  /* Solo tres tabs — 'colecciones' eliminado porque no existe tabla en la base de datos */
  tabs = [
    { id: 'recetas',   label: 'Mis Recetas', cantidad: 156, icono: '📝' },
    { id: 'favoritos', label: 'Favoritos',   cantidad: 89,  icono: '❤️' },
    { id: 'logros',    label: 'Logros',      cantidad: 34,  icono: '🏆' },
  ];

  recetasUsuario = [
    { titulo: 'Tortellini al Pesto', tipo: 'Italiana', tiempo: '30 min', emoji: '🍝', color: 'linear-gradient(145deg,#B83520,#6E1A0C)', publicada: true,  likes: 342 },
    { titulo: 'Ceviche de Gambas',   tipo: 'Mariscos', tiempo: '20 min', emoji: '🦐', color: 'linear-gradient(145deg,#285A80,#122A40)', publicada: true,  likes: 128 },
    { titulo: 'Tarta de Manzana',    tipo: 'Postres',  tiempo: '75 min', emoji: '🍎', color: 'linear-gradient(145deg,#B87A10,#6E4808)', publicada: false, likes: 0 },
    { titulo: 'Gazpacho Andaluz',    tipo: 'Española', tiempo: '15 min', emoji: '🍅', color: 'linear-gradient(145deg,#A02818,#5A100C)', publicada: true,  likes: 567 },
    { titulo: 'Pad Thai Casero',     tipo: 'Asiática', tiempo: '40 min', emoji: '🍜', color: 'linear-gradient(145deg,#2A6040,#152E1E)', publicada: true,  likes: 89 },
    { titulo: 'Focaccia Romero',     tipo: 'Italiana', tiempo: '90 min', emoji: '🌿', color: 'linear-gradient(145deg,#5C3D2E,#2E1A0E)', publicada: false, likes: 0 },
  ];

  /* Logros → tablas 'logro' y 'usuario_logro'. El campo 'icono' representa icono_url en formato emoji. */
  logros = [
    { nombre: 'Primer Plato',      descripcion: 'Publicaste tu primera receta',              desbloqueado: true,  fecha: '15 Ene 2025', color: '#E8B84B', icono: '⭐' },
    { nombre: 'Chef Estrella',     descripcion: 'Alcanzaste 4.5+ de valoracion media',       desbloqueado: true,  fecha: '3 Mar 2025',  color: '#E87B2A', icono: '👨‍🍳' },
    { nombre: 'En Llamas',         descripcion: '10 recetas en tendencia',                   desbloqueado: true,  fecha: '22 Jun 2025', color: '#E05533', icono: '🔥' },
    { nombre: 'Maestro Culinario', descripcion: 'Publica 200 recetas',                       desbloqueado: false, progreso: 78,  meta: 200, color: '#9A9A9A', icono: '🏆' },
    { nombre: 'Gran Anfitrian',    descripcion: 'Consigue 50 amigos en la plataforma',       desbloqueado: false, progreso: 47,  meta: 50,  color: '#9A9A9A', icono: '🤝' },
    { nombre: 'Perfeccion',        descripcion: 'Mantén valoracion 5.0 en 50 recetas',      desbloqueado: false, progreso: 12,  meta: 50,  color: '#9A9A9A', icono: '💎' },
  ];

  /* Puntos/monedas del usuario — mapeado a usuario.puntos en la base de datos */
  coins = 1250;

  /* Numero de amigos — mapeado a usuario_amigo en la base de datos */
  amigos = 47;

  /* Modelo de IA seleccionado — mapeado a usuario.ia_modelo_seleccionado_id → ia_modelo */
  modeloIA = 'Groq Llama 3';

  /* Preferencias de tipo de comida — mapeado a usuario_preferencia → tipo_comida */
  preferencias = ['Italiana', 'Japonesa', 'Saludable'];

  /* Recetas guardadas como favoritas — mapeado a favorito_receta → receta */
  recetasFavoritas = [
    { titulo: 'Pasta Carbonara Autentica', autor: 'Marco R.',  tipo: 'Italiana', tiempo: '25 min', emoji: '🍝', color: 'linear-gradient(145deg,#B83520,#6E1A0C)' },
    { titulo: 'Paella Valenciana',         autor: 'Carmen L.', tipo: 'Española', tiempo: '55 min', emoji: '🥘', color: 'linear-gradient(145deg,#A02818,#5A100C)' },
    { titulo: 'Sushi Casero Facil',        autor: 'Yuki T.',   tipo: 'Japonesa', tiempo: '45 min', emoji: '🍱', color: 'linear-gradient(145deg,#2A6040,#152E1E)' },
  ];

  /* Supermercados favoritos — mapeado a favorito_supermercado → supermercado */
  favoritosSupermercados = [
    { nombre: 'Mercadona', descripcion: 'Supermercado de confianza', valoracion: 4.2, direccion: 'Av. Principal 12', horario: 'Lun-Sab 09:00-21:00' },
    { nombre: 'Lidl',      descripcion: 'Productos frescos y ofertas', valoracion: 4.0, direccion: 'C/ Mayor 5',     horario: 'Lun-Dom 08:00-22:00' },
  ];

  cambiarTab(id: string) {
    this.tabActiva.set(id);
  }

  porcentaje(progreso: number, meta: number) {
    return Math.round((progreso / meta) * 100);
  }

  formatNum(n: number) {
    return n >= 1000 ? (n / 1000).toFixed(1) + 'K' : n.toString();
  }
}
