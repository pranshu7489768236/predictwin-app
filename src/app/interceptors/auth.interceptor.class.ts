import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptorClass implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Enforce cookie-based requests for secure, HttpOnly-authenticated flows
    let cloned = req.clone({ withCredentials: true });

    // Attach CSRF token header when available (server must provide a token in a meta tag)
    const csrf = this.auth.getCsrfToken?.();
    if (csrf) {
      cloned = cloned.clone({ headers: cloned.headers.set('X-CSRF-Token', csrf) });
    }

    return next.handle(cloned);
  }
}
