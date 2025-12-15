import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <strong>Wallet</strong>
      <div style="margin-top:8px;color:var(--muted)">Add / Withdraw money, payment methods</div>
      <div style="display:flex;gap:8px;margin-top:12px">
        <button class="btn">Add Money</button>
        <button class="btn" style="background:var(--neon-green)">Withdraw</button>
      </div>
    </div>
  `
})
export class WalletComponent {}
