import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../services/loading.service';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="spinner-overlay" *ngIf="loading.loading()">
      <div class="spinner"></div>
    </div>
  `,
  styles: [`
    .spinner-overlay{position:fixed;inset:0;display:flex;align-items:center;justify-content:center;z-index:60;background:rgba(0,0,0,0.3)}
    .spinner{width:64px;height:64px;border-radius:50%;border:6px solid rgba(255,255,255,0.08);border-top-color:var(--neon-blue);animation:spin 1s linear infinite}
    @keyframes spin{to{transform:rotate(360deg)}}
  `]
})
export class LoadingSpinnerComponent {
  constructor(public loading: LoadingService) {}
}
