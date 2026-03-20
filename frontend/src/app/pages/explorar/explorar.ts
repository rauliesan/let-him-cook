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

  /* Categorias — mapeadas a la tabla 'tipo_comida' (nombre, descripcion, icono_url).
     El campo 'emoji' representa el icono_url en formato emoji para el mock. */
  categorias = [
    { nombre: 'Italiana',  recetas: 234, color: '#C13E28', emoji: '🍝', descripcion: 'Pasta, risotto y mucho mas' },
    { nombre: 'Española',  recetas: 189, color: '#A02818', emoji: '🥘', descripcion: 'Tradicion y sabor mediterraneo' },
    { nombre: 'Japonesa',  recetas: 156, color: '#2D6040', emoji: '🍱', descripcion: 'Sushi, ramen y cocina oriental' },
    { nombre: 'Mexicana',  recetas: 142, color: '#B87010', emoji: '🌮', descripcion: 'Tacos, guacamole y picante' },
    { nombre: 'Postres',   recetas: 321, color: '#C88A10', emoji: '🍰', descripcion: 'Dulces, tartas y reposteria' },
    { nombre: 'Saludable', recetas: 278, color: '#1E5A7A', emoji: '🥗', descripcion: 'Recetas nutritivas y equilibradas' },
    { nombre: 'Fast Food', recetas: 167, color: '#7A3ABE', emoji: '🍔', descripcion: 'Rapido, sabroso y sin complicaciones' },
    { nombre: 'Francesa',  recetas: 198, color: '#3A2010', emoji: '🥐', descripcion: 'Gastronomia refinada y clasica' },
  ];

  dificultades = ['Cualquiera', 'Fácil', 'Media', 'Difícil'];
  tiempos = ['Cualquiera', '< 30 min', '30-60 min', '> 60 min'];

  /* Recetas — mapeadas a la tabla 'receta'.
     calorias → receta.calorias, alergenos → receta.alergenos.
     La puntuación es el promedio de comentario.valoracion para cada receta. */
  recetas = [
    { titulo: 'Pasta Carbonara Autentica',  autor: 'Marco R.',  tiempo: '25 min',  dificultad: 'Media',   tipo: 'Italiana',  puntuacion: 4.8, emoji: '🍝', color: 'linear-gradient(145deg,#B83520,#6E1A0C)', calorias: 520, alergenos: 'Gluten, Huevo, Lacteos' },
    { titulo: 'Paella Valenciana',           autor: 'Carmen L.', tiempo: '55 min',  dificultad: 'Dificil', tipo: 'Española',  puntuacion: 4.9, emoji: '🥘', color: 'linear-gradient(145deg,#A02818,#5A100C)', calorias: 410, alergenos: 'Marisco, Gluten' },
    { titulo: 'Sushi Casero Facil',          autor: 'Yuki T.',   tiempo: '45 min',  dificultad: 'Media',   tipo: 'Japonesa',  puntuacion: 4.7, emoji: '🍱', color: 'linear-gradient(145deg,#2A6040,#152E1E)', calorias: 320, alergenos: 'Pescado, Soja' },
    { titulo: 'Guacamole Fresco',            autor: 'Miguel P.', tiempo: '10 min',  dificultad: 'Facil',   tipo: 'Mexicana',  puntuacion: 4.6, emoji: '🥑', color: 'linear-gradient(145deg,#2D7A30,#163A18)', calorias: 180, alergenos: '' },
    { titulo: 'Cheesecake de Frutos Rojos',  autor: 'Laura B.',  tiempo: '90 min',  dificultad: 'Media',   tipo: 'Postres',   puntuacion: 4.9, emoji: '🍰', color: 'linear-gradient(145deg,#B87A10,#6E4808)', calorias: 380, alergenos: 'Lacteos, Huevo, Gluten' },
    { titulo: 'Buddha Bowl de Quinoa',       autor: 'Ana M.',    tiempo: '20 min',  dificultad: 'Facil',   tipo: 'Saludable', puntuacion: 4.5, emoji: '🥗', color: 'linear-gradient(145deg,#285A80,#122A40)', calorias: 290, alergenos: 'Soja' },
    { titulo: 'Croissant Mantequilla',       autor: 'Pierre D.', tiempo: '180 min', dificultad: 'Dificil', tipo: 'Francesa',  puntuacion: 4.8, emoji: '🥐', color: 'linear-gradient(145deg,#5C2E1A,#2E1208)', calorias: 340, alergenos: 'Gluten, Lacteos, Huevo' },
    { titulo: 'Ramen de Cerdo',              autor: 'Kenji S.',  tiempo: '120 min', dificultad: 'Dificil', tipo: 'Japonesa',  puntuacion: 4.9, emoji: '🍜', color: 'linear-gradient(145deg,#2A4A60,#101E30)', calorias: 560, alergenos: 'Gluten, Soja, Huevo' },
    { titulo: 'Tortilla Española',           autor: 'Pilar G.',  tiempo: '30 min',  dificultad: 'Media',   tipo: 'Española',  puntuacion: 4.7, emoji: '🍳', color: 'linear-gradient(145deg,#985808,#4E2C04)', calorias: 240, alergenos: 'Huevo' },
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
