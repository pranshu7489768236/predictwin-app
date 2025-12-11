import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private _count = 0;
  public loading = signal(false);

  show() {
    this._count++;
    this.loading.set(true);
  }

  hide() {
    this._count = Math.max(0, this._count - 1);
    if (this._count === 0) this.loading.set(false);
  }

  reset() { this._count = 0; this.loading.set(false); }
}
