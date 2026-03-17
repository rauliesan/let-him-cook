import { Routes } from '@angular/router';
import { Landing } from './pages/landing/landing';
import { Explorar } from './pages/explorar/explorar';
import { Perfil } from './pages/perfil/perfil';
import { Contacto } from './pages/contacto/contacto';
import { Login } from './pages/login/login';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'explorar', component: Explorar },
  { path: 'perfil', component: Perfil },
  { path: 'contacto', component: Contacto },
  { path: 'login', component: Login },
  { path: 'mapa', loadComponent: () => import('./pages/mapa/mapa').then(m => m.Mapa) },
  { path: '**', redirectTo: '' }
];
