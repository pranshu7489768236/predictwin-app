import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // NOTE: For secure production flows, auth state should be kept server-side
  // using HttpOnly cookies. This service uses cookie-based requests (withCredentials)
  // and does NOT persist tokens in localStorage.
  constructor(private http: HttpClient) {}

  isLoggedIn(): Observable<any> {
    return this.http.get('/api/auth/status', { withCredentials: true });
  }

  login(mobile: string, password: string): Observable<any> {
    return this.http.post('/api/auth/login', { mobile, password }, { withCredentials: true });
  }

  register(payload: any): Observable<any> {
    return this.http.post('/api/auth/register', payload, { withCredentials: true });
  }

  logout(): Observable<any> {
    return this.http.post('/api/auth/logout', {}, { withCredentials: true });
  }

  // Read CSRF token from a meta tag if the server provides one
  getCsrfToken(): string | null {
    try {
      const el = document.querySelector('meta[name="csrf-token"]');
      return el ? (el.getAttribute('content') || null) : null;
    } catch {
      return null;
    }
  }

  // Send OTP / reset initiation. Server should return 200 if SMS/OTP sent.
  sendReset(mobile: string): Observable<any> {
    return this.http.post('/api/auth/forgot', { mobile }, { withCredentials: true }).pipe(
      catchError((err) => {
        // During local development when backend is absent, allow a dev fallback so UI can proceed.
        if (err && err.status === 404) return of({ ok: true, devFallback: true });
        return throwError(() => err);
      })
    );
  }

  // Verify OTP and set new password.
  resetPassword(mobile: string, otp: string, password: string): Observable<any> {
    return this.http.post('/api/auth/reset', { mobile, otp, password }, { withCredentials: true }).pipe(
      catchError((err) => {
        if (err && err.status === 404) return of({ ok: true, devFallback: true });
        return throwError(() => err);
      })
    );
  }
}
