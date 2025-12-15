import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-bottom-nav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bottom-nav.component.html',
  styleUrls: ['./bottom-nav.component.css']
})
export class BottomNavComponent {
  constructor(private router: Router) {}

  isActive(path: string){
    const url = this.router.url || '/';
    if(path === '/') return url === '/' || url.startsWith('/home');
    return url.startsWith(path);
  }

  navigate(path: string){
    this.router.navigateByUrl(path);
  }
}
