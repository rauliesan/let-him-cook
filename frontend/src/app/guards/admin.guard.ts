import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/* Guard funcional — protege rutas que requieren rol ADMIN */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.estaAutenticado() && auth.sesion()?.rol === 'ADMIN') {
    return true;
  }

  /* Redirige a / si no es admin */
  return router.createUrlTree(['/']);
};
