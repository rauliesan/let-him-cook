import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';

@Component({
  selector: 'app-explorar',
  imports: [CommonModule, Revela],
  templateUrl: './explorar.html',
  styleUrl: './explorar.scss',
})
export class Explorar {
  filtroActivo = signal('Todas');
  dificultadActiva = signal('Cualquiera');
  tiempoActivo = signal('Cualquiera');

  categorias = [
    { nombre: 'Italiana',  recetas: 234, color: '#C13E28', emoji: '🍝' },
    { nombre: 'Española',  recetas: 189, color: '#A02818', emoji: '🥘' },
    { nombre: 'Japonesa',  recetas: 156, color: '#2D6040', emoji: '🍱' },
    { nombre: 'Mexicana',  recetas: 142, color: '#B87010', emoji: '🌮' },
    { nombre: 'Postres',   recetas: 321, color: '#C88A10', emoji: '🍰' },
    { nombre: 'Saludable', recetas: 278, color: '#1E5A7A', emoji: '🥗' },
    { nombre: 'Fast Food', recetas: 167, color: '#7A3ABE', emoji: '🍔' },
    { nombre: 'Francesa',  recetas: 198, color: '#3A2010', emoji: '🥐' },
  ];

  dificultades = ['Cualquiera', 'Fácil', 'Media', 'Difícil'];
  tiempos = ['Cualquiera', '< 30 min', '30-60 min', '> 60 min'];

  recetas = [
    { titulo: 'Pasta Carbonara Auténtica',  autor: 'Marco R.',  tiempo: '25 min',  dificultad: 'Media',   tipo: 'Italiana',  puntuacion: 4.8, emoji: '🍝', color: 'linear-gradient(145deg,#B83520,#6E1A0C)' },
    { titulo: 'Paella Valenciana',           autor: 'Carmen L.', tiempo: '55 min',  dificultad: 'Difícil', tipo: 'Española',  puntuacion: 4.9, emoji: '🥘', color: 'linear-gradient(145deg,#A02818,#5A100C)' },
    { titulo: 'Sushi Casero Fácil',          autor: 'Yuki T.',   tiempo: '45 min',  dificultad: 'Media',   tipo: 'Japonesa',  puntuacion: 4.7, emoji: '🍱', color: 'linear-gradient(145deg,#2A6040,#152E1E)' },
    { titulo: 'Guacamole Fresco',            autor: 'Miguel P.', tiempo: '10 min',  dificultad: 'Fácil',   tipo: 'Mexicana',  puntuacion: 4.6, emoji: '🥑', color: 'linear-gradient(145deg,#2D7A30,#163A18)' },
    { titulo: 'Cheesecake de Frutos Rojos',  autor: 'Laura B.',  tiempo: '90 min',  dificultad: 'Media',   tipo: 'Postres',   puntuacion: 4.9, emoji: '🍰', color: 'linear-gradient(145deg,#B87A10,#6E4808)' },
    { titulo: 'Buddha Bowl de Quinoa',       autor: 'Ana M.',    tiempo: '20 min',  dificultad: 'Fácil',   tipo: 'Saludable', puntuacion: 4.5, emoji: '🥗', color: 'linear-gradient(145deg,#285A80,#122A40)' },
    { titulo: 'Croissant Mantequilla',       autor: 'Pierre D.', tiempo: '180 min', dificultad: 'Difícil', tipo: 'Francesa',  puntuacion: 4.8, emoji: '🥐', color: 'linear-gradient(145deg,#5C2E1A,#2E1208)' },
    { titulo: 'Ramen de Cerdo',              autor: 'Kenji S.',  tiempo: '120 min', dificultad: 'Difícil', tipo: 'Japonesa',  puntuacion: 4.9, emoji: '🍜', color: 'linear-gradient(145deg,#2A4A60,#101E30)' },
    { titulo: 'Tortilla Española',           autor: 'Pilar G.',  tiempo: '30 min',  dificultad: 'Media',   tipo: 'Española',  puntuacion: 4.7, emoji: '🍳', color: 'linear-gradient(145deg,#985808,#4E2C04)' },
  ];

  seleccionarFiltro(nombre: string) {
    this.filtroActivo.set(nombre);
  }

  seleccionarDificultad(d: string) {
    this.dificultadActiva.set(d);
  }

  seleccionarTiempo(t: string) {
    this.tiempoActivo.set(t);
  }

  estrellas(puntuacion: number) {
    return Math.round(puntuacion);
  }
}
