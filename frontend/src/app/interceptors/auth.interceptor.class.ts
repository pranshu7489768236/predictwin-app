import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptorClass implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Enforce cookie-based requests for secure, HttpOnly-authenticated flows
    let cloned = req.clone({ withCredentials: true });

    // Attach Authorization header when access token available
    const token = this.auth.getToken?.();
    if (token) {
      cloned = cloned.clone({ headers: cloned.headers.set('Authorization', `Bearer ${token}`) });
    }

    // Attach CSRF token header when available (server must provide a token in a meta tag)
    const csrf = this.auth.getCsrfToken?.();
    if (csrf) {
      cloned = cloned.clone({ headers: cloned.headers.set('X-CSRF-Token', csrf) });
    }

    return next.handle(cloned).pipe(
      catchError((err) => {
        // If unauthorized, try to refresh the access token once and retry
        if (err && err.status === 401 && !req.url.includes('/api/auth/refresh') && !req.url.includes('/api/auth/login')) {
          return this.auth.refreshToken().pipe(
            switchMap((ok) => {
              if (ok) {
                const newToken = this.auth.getToken?.();
                const retry = cloned.clone({ headers: cloned.headers.set('Authorization', `Bearer ${newToken}`) });
                return next.handle(retry);
              }
              return throwError(() => err);
            }),
            catchError(() => throwError(() => err))
          );
        }
        return throwError(() => err);
      })
    );
  }
}
