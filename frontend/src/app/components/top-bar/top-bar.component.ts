import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, Theme } from '../../services/theme.service';

@Component({
  selector: 'app-top-bar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {
  constructor(public theme: ThemeService) {}

  // Proxy observable for template type-checking (avoids deep property access on injected service)
  // cast to any to avoid strict type checks from AOT/template checker when the service
  // shape differs between runtime and the compiler metadata.
  public current$ = (this.theme as any).current$;

  ngOnInit(): void {
    // ensure theme is applied (service is safe for SSR)
    this.theme.init();
  }

  toggleTheme(): void {
    const cur: Theme = this.theme.get();
    const next: Theme = cur === 'dark' ? 'light' : 'dark';
    this.theme.set(next);
  }
}
