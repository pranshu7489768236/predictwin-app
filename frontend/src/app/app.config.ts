import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';

import { routes } from './app.routes';
import { AuthInterceptorClass } from './interceptors/auth.interceptor.class';
import { LoadingInterceptorClass } from './interceptors/loading.interceptor.class';
import { ErrorInterceptorClass } from './interceptors/error.interceptor.class';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorClass, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptorClass, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptorClass, multi: true }
  ]
};
