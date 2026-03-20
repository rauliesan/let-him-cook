import { Component, ElementRef, HostListener, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, CommonModule, Revela],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export class Landing {
  faqAbierta = signal<number | null>(null);

  constructor(private el: ElementRef<HTMLElement>) {}

  @HostListener('mousemove', ['$event'])
  onMouseMove(e: MouseEvent) {
    const host = this.el.nativeElement;
    const nx = (e.clientX / window.innerWidth  - 0.5) * 2; // -1..1
    const ny = (e.clientY / window.innerHeight - 0.5) * 2;

    // Spotlight existente
    host.style.setProperty('--mx', `${(e.clientX / window.innerWidth) * 100}%`);
    host.style.setProperty('--my', `${(e.clientY / window.innerHeight) * 100}%`);

    // Parallax por capa — valores precomputados con unidades para evitar calc()
    host.style.setProperty('--tx-fondo',  `${(nx * -22).toFixed(1)}px`);
    host.style.setProperty('--ty-fondo',  `${(ny * -14).toFixed(1)}px`);
    host.style.setProperty('--tx-texto',  `${(nx *   5).toFixed(1)}px`);
    host.style.setProperty('--ty-texto',  `${(ny *   4).toFixed(1)}px`);
    host.style.setProperty('--tx-panel',  `${(nx *  16).toFixed(1)}px`);
    host.style.setProperty('--ty-panel',  `${(ny *  12).toFixed(1)}px`);
    host.style.setProperty('--tx-logro',  `${(nx *  24).toFixed(1)}px`);
    host.style.setProperty('--ty-logro',  `${(ny *  18).toFixed(1)}px`);

    // Tilt 3D del card stack
    host.style.setProperty('--tilt-x', `${(ny * -6).toFixed(2)}deg`);
    host.style.setProperty('--tilt-y', `${(nx *  9).toFixed(2)}deg`);

    // Posición del cursor-aura (relativa al hero, centrada en el punto)
    const hero = host.querySelector('.hero') as HTMLElement;
    const rect  = hero?.getBoundingClientRect() ?? { left: 0, top: 0 };
    host.style.setProperty('--aura-x', `${e.clientX - rect.left - 280}px`);
    host.style.setProperty('--aura-y', `${e.clientY - rect.top  - 280}px`);
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    const host = this.el.nativeElement;
    ['--tx-fondo','--ty-fondo','--tx-texto','--ty-texto',
     '--tx-panel','--ty-panel','--tx-logro','--ty-logro']
      .forEach(p => host.style.setProperty(p, '0px'));
    host.style.setProperty('--tilt-x', '0deg');
    host.style.setProperty('--tilt-y', '0deg');
  }

  /* Círculos del fondo — regenerados aleatoriamente en cada carga */
  orbitCirculos = Array.from(
    { length: Math.floor(Math.random() * 4) + 4 },
    () => {
      const isRed = Math.random() > 0.4;
      const base = isRed ? '193, 62, 40' : '232, 184, 75';
      return {
        size:     Math.floor(Math.random() * 300) + 80,
        top:      Math.floor(Math.random() * 75)  + 5,
        left:     Math.floor(Math.random() * 85)  + 5,
        opacity:  +(Math.random() * 0.07 + 0.02).toFixed(2),
        delay:    +(Math.random() * 8).toFixed(1),
        duration: +(Math.random() * 10 + 10).toFixed(1),
        color:    `rgba(${base}, 0.4)`,
      };
    }
  );

  ingredientesTira = [
    'pasta', 'tomate', 'ajo', 'limón', 'pollo', 'arroz',
    'cebolla', 'pimiento', 'queso', 'albahaca', 'huevo',
    'zanahoria', 'champiñón', 'espinacas', 'chorizo', 'salmón'
  ];

  recetas = [
    { titulo: 'Pasta al Pesto con Burrata', tiempo: '20 min', dificultad: 'Fácil', tipo: 'Italiana',  calorias: 480, autor: 'María G.',  emoji: '🍝', color: 'linear-gradient(145deg,#B83520,#6E1A0C)' },
    { titulo: 'Tacos de Ternera Especiada', tiempo: '35 min', dificultad: 'Media',  tipo: 'Mexicana',  calorias: 520, autor: 'Carlos R.', emoji: '🌮', color: 'linear-gradient(145deg,#B87A10,#6E4808)' },
    { titulo: 'Ramen Casero con Huevo',     tiempo: '55 min', dificultad: 'Media',  tipo: 'Japonesa',  calorias: 610, autor: 'Laura M.',  emoji: '🍜', color: 'linear-gradient(145deg,#2A6040,#152E1E)' },
    { titulo: 'Ensalada Mediterránea',       tiempo: '10 min', dificultad: 'Fácil', tipo: 'Saludable', calorias: 280, autor: 'Sofía P.',  emoji: '🥗', color: 'linear-gradient(145deg,#357A35,#1A401A)' },
    { titulo: 'Risotto de Champiñones',      tiempo: '40 min', dificultad: 'Media',  tipo: 'Italiana',  calorias: 540, autor: 'Pedro A.', emoji: '🍄', color: 'linear-gradient(145deg,#285A80,#122A40)' },
    { titulo: 'Curry de Garbanzos',          tiempo: '30 min', dificultad: 'Fácil', tipo: 'Asiática',  calorias: 390, autor: 'Ana B.',    emoji: '🍛', color: 'linear-gradient(145deg,#985808,#4E2C04)' },
  ];

  features = [
    { icono: 'ia', titulo: 'Recetas con IA', desc: 'Dinos qué tienes en la nevera. La IA genera una receta personalizada al instante, adaptada a tus preferencias y restricciones.', badge: 'Groq · ChatGPT · Deepseek', grande: true },
    { icono: 'logros', titulo: 'Gamificación real', desc: 'Cada vez que cocinas, ganas. Puntos, niveles, logros y retos que convierten cocinar en algo adictivo.', badge: null, grande: false },
    { icono: 'comunidad', titulo: 'Comunidad viva', desc: 'Comparte tus creaciones y descubre las de otros. Sigue a cocineros, comenta, guarda favoritos.', badge: null, grande: false },
    { icono: 'mapa', titulo: 'Supermercados', desc: 'Encuentra los súpers más cercanos a ti. Integración directa con Google Maps.', badge: null, grande: false },
  ];

  faqs = [
    { pregunta: '¿Es completamente gratis?', respuesta: 'Sí, LetHimCook es 100% gratuito. Sin suscripciones, sin pagos ocultos, sin límites de uso.' },
    { pregunta: '¿Cómo funciona la generación con IA?', respuesta: 'Introduces los ingredientes que tienes en casa, tus preferencias y restricciones alimentarias, y la IA crea una receta personalizada al instante.' },
    { pregunta: '¿Puedo editar las recetas generadas?', respuesta: 'Por supuesto. Tienes control total para modificar cualquier receta, sea generada por IA o creada por ti desde cero.' },
    { pregunta: '¿En qué consiste el sistema de logros?', respuesta: 'Conforme cocinas, publicas y exploras, ganas puntos y desbloqueas logros. Hay niveles, recompensas y competiciones amistosas entre usuarios.' },
  ];

  toggleFaq(indice: number) {
    this.faqAbierta.update(actual => actual === indice ? null : indice);
  }
}
