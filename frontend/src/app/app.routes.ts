import { Routes } from '@angular/router';
import { Landing } from './pages/landing/landing';
import { Explorar } from './pages/explorar/explorar';
import { Perfil } from './pages/perfil/perfil';
import { Contacto } from './pages/contacto/contacto';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'explorar', component: Explorar, canActivate: [authGuard] },
  {
    path: 'foro',
    loadComponent: () => import('./pages/foro/foro').then(m => m.Foro),
  },
  /* Perfil, mapa y rewards requieren estar autenticado */
  { path: 'perfil',  component: Perfil,  canActivate: [authGuard] },
  { path: 'contacto', component: Contacto },
  { path: 'login', component: Login },
  {
    path: 'mapa',
    loadComponent: () => import('./pages/mapa/mapa').then(m => m.Mapa),
    canActivate: [authGuard],
  },
  {
    path: 'rewards',
    loadComponent: () => import('./pages/rewards/rewards').then(m => m.Rewards),
    canActivate: [authGuard],
  },
  {
    path: 'cocinar',
    loadComponent: () => import('./pages/cocinar/cocinar').then(m => m.Cocinar),
    canActivate: [authGuard],
  },
  { path: '**', redirectTo: '' },
];
