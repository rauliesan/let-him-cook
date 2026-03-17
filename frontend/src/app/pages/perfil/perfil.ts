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

  tabs = [
    { id: 'recetas', label: 'Mis Recetas', cantidad: 156 },
    { id: 'favoritos', label: 'Favoritos', cantidad: 89 },
    { id: 'colecciones', label: 'Colecciones', cantidad: 12 },
    { id: 'logros', label: 'Logros', cantidad: 34 },
  ];

  recetasUsuario = [
    { titulo: 'Tortellini al Pesto', tipo: 'Italiana', tiempo: '30 min', emoji: '🍝', color: 'linear-gradient(145deg,#B83520,#6E1A0C)', publicada: true },
    { titulo: 'Ceviche de Gambas',   tipo: 'Mariscos', tiempo: '20 min', emoji: '🦐', color: 'linear-gradient(145deg,#285A80,#122A40)', publicada: true },
    { titulo: 'Tarta de Manzana',    tipo: 'Postres',  tiempo: '75 min', emoji: '🍎', color: 'linear-gradient(145deg,#B87A10,#6E4808)', publicada: false },
    { titulo: 'Gazpacho Andaluz',    tipo: 'Española', tiempo: '15 min', emoji: '🍅', color: 'linear-gradient(145deg,#A02818,#5A100C)', publicada: true },
    { titulo: 'Pad Thai Casero',     tipo: 'Asiática', tiempo: '40 min', emoji: '🍜', color: 'linear-gradient(145deg,#2A6040,#152E1E)', publicada: true },
    { titulo: 'Focaccia Romero',     tipo: 'Italiana', tiempo: '90 min', emoji: '🌿', color: 'linear-gradient(145deg,#5C3D2E,#2E1A0E)', publicada: false },
  ];

  logros = [
    { nombre: 'Primer Plato',      descripcion: 'Publicaste tu primera receta',   desbloqueado: true,  fecha: '15 Ene 2025', color: '#E8B84B', icono: '⭐' },
    { nombre: 'Chef Estrella',     descripcion: 'Alcanzaste 4.5+ de valoración',  desbloqueado: true,  fecha: '3 Mar 2025',  color: '#E87B2A', icono: '👨‍🍳' },
    { nombre: 'En Llamas',         descripcion: '10 recetas en tendencia',         desbloqueado: true,  fecha: '22 Jun 2025', color: '#E05533', icono: '🔥' },
    { nombre: 'Maestro Culinario', descripcion: 'Publica 200 recetas',             desbloqueado: false, progreso: 78,    meta: 200,   color: '#9A9A9A', icono: '🏆' },
    { nombre: 'Rey de la Cocina',  descripcion: '50K seguidores',                 desbloqueado: false, progreso: 24500, meta: 50000, color: '#9A9A9A', icono: '👑' },
    { nombre: 'Perfección',        descripcion: 'Mantén 5.0 en 50 recetas',       desbloqueado: false, progreso: 12,    meta: 50,    color: '#9A9A9A', icono: '💎' },
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
