import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.css']
})
export class MeComponent {
  phone = '';
  password = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  login(e?: Event){
    e?.preventDefault();
    if(!this.canLogin()) return;
    this.loading = true;
    this.auth.login(this.phone, this.password).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res && res.accessToken) {
          this.auth.setToken(res.accessToken);
        }
        alert('Login success');
        this.router.navigateByUrl('/');
      },
      error: (err) => { this.loading = false; console.error(err); alert('Login failed'); }
    });
  }

  // Simple validation helpers
  isPhoneValid(){
    const digits = (this.phone || '').replace(/\D/g, '');
    return digits.length >= 6 && digits.length <= 15;
  }

  isPasswordValid(){
    return (this.password || '').length >= 6;
  }

  canLogin(){
    return this.isPhoneValid() && this.isPasswordValid() && !this.loading;
  }
}
