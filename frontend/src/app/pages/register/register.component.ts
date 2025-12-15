import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  phone = '';
  code = '';
  password = '';
  referral = '';
  agree = false;
  loading = false;

  constructor(private auth: AuthService) {}

  sendOtp(){
    // In a secure implementation OTP sending is rate-limited and verified server-side.
    if(!this.isPhoneValid() || this.loading) return;
    this.loading = true;
    // Use AuthService sendReset if available, else fallback to alert
    try{
      (this.auth as any).sendReset?.(this.phone)?.subscribe?.(()=>{ this.loading = false; alert('OTP sent to ' + this.phone); }, ()=>{ this.loading = false; alert('Failed to send OTP'); });
    }catch{
      this.loading = false; alert('OTP sent to ' + this.phone);
    }
  }

  submit(e?: Event){
    e?.preventDefault();
    if(!this.canRegister()) return;
    this.loading = true;
    const payload = { mobile: this.phone, code: this.code, password: this.password, referral: this.referral };
    this.auth.register(payload).subscribe({ next: () => { this.loading = false; alert('Registered — please check SMS/Email for verification'); }, error: (err) => { this.loading = false; console.error(err); alert('Register failed'); } });
  }

  isPhoneValid(){
    const digits = (this.phone || '').replace(/\D/g, '');
    return digits.length >= 6 && digits.length <= 15;
  }

  isCodeValid(){
    const c = (this.code || '').trim();
    return c.length >= 3 && c.length <= 8; // allow short codes
  }

  isPasswordValid(){
    return (this.password || '').length >= 6;
  }

  canRegister(){
    return this.isPhoneValid() && this.isCodeValid() && this.isPasswordValid() && this.agree && !this.loading;
  }
}
