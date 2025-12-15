import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

export type Theme = 'dark' | 'light' | 'grey';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private key = 'colorx.theme';
  private _current$ = new BehaviorSubject<Theme>('light');
  public current$ = this._current$.asObservable();
  private isBrowser: boolean;

  constructor(@Inject(DOCUMENT) private doc: Document, @Inject(PLATFORM_ID) private platformId: any) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    // Initialize from storage if available (browser only)
    if (this.isBrowser) {
      const stored = this.safeLocalStorageGet(this.key) as Theme | null;
      const t = stored || 'light';
      this._current$.next(t);
      this.apply(t);
    }
  }

  get(): Theme {
    return this._current$.value;
  }

  set(t: Theme) {
    if (this.isBrowser) {
      this.safeLocalStorageSet(this.key, t);
    }
    this._current$.next(t);
    this.apply(t);
  }

  apply(t: Theme) {
    if (!this.doc) return;
    // Apply theme class to the documentElement (html) so it matches CSS rules using :root.theme-*
    const el = this.doc.documentElement || this.doc.body;
    // remove from both html and body to be safe (cleanup any previous wrong placement)
    try {
      this.doc.documentElement.classList.remove('theme-dark', 'theme-light', 'theme-grey');
    } catch (e) {}
    try {
      this.doc.body.classList.remove('theme-dark', 'theme-light', 'theme-grey');
    } catch (e) {}
    el.classList.add(`theme-${t}`);
  }

  init() {
    if (!this.isBrowser) return;
    const stored = this.safeLocalStorageGet(this.key) as Theme | null;
    const t = stored || this._current$.value || 'light';
    this._current$.next(t);
    this.apply(t);
  }

  private safeLocalStorageGet(k: string): string | null {
    try {
      return (window && window.localStorage && window.localStorage.getItem(k)) || null;
    } catch (e) {
      return null;
    }
  }

  private safeLocalStorageSet(k: string, v: string) {
    try {
      if (window && window.localStorage) window.localStorage.setItem(k, v);
    } catch (e) {
      // ignore
    }
  }
}
