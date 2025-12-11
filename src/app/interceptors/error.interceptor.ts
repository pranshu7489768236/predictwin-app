import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoadingService } from '../services/loading.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const loading = inject(LoadingService);
  return next(req).pipe(
    catchError((err: any) => {
      // central place for error handling (show toast, log)
      console.error('HTTP error', err);
      // ensure loading state cleared
      loading.reset();
      return throwError(() => err);
    })
  );
};
