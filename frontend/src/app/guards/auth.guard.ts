import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/* Guard funcional — protege rutas que requieren login */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.estaAutenticado()) {
    return true;
  }

  /* Redirige a /login si no hay sesión */
  return router.createUrlTree(['/login']);
};
