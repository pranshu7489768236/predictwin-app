import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <div style="display:flex;align-items:center;gap:12px">
        <div style="width:56px;height:56px;border-radius:28px;background:var(--surface)"></div>
        <div>
          <div style="font-weight:700">John Doe</div>
          <div style="color:var(--muted)">+91 98765 43210</div>
        </div>
      </div>

      <div style="margin-top:12px" class="stack">
        <button class="btn">Game History</button>
        <button class="btn">Transaction History</button>
        <button class="btn">KYC Status: Not Submitted</button>
      </div>
    </div>
  `
})
export class ProfileComponent {}
