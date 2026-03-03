import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { I18nService } from '../services/i18n.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);
  const i18n = inject(I18nService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        if (!req.url.includes('/api/auth/login')) {
          localStorage.removeItem('jwt_token');
          localStorage.removeItem('username');
          localStorage.removeItem('role');

          snackBar.open(i18n.t('auth.sessionExpired'), i18n.t('common.close'), {
            duration: 5000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: ['error-snackbar']
          });

          router.navigate(['/login']);
        }
      } else if (error.status === 403) {
        snackBar.open(i18n.t('auth.accessDenied'), i18n.t('common.close'), {
          duration: 5000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
      
      return throwError(() => error);
    })
  );
};
