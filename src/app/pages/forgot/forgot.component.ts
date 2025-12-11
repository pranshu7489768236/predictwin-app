import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forgot.component.html',
  styleUrls: ['./forgot.component.css']
})
export class ForgotComponent {
  step: 1 | 2 = 1;
  mobile = '';
  otp = '';
  password = '';
  loading = false;
  message = '';
  // OTP timer (seconds)
  otpTimer = 0;
  private otpInterval: any = null;
  otpExpired = false;

  constructor(private auth: AuthService, private router: Router) {}

  sendOtp(){
    if(!this.isPhoneValid()) { this.message = 'Please enter a valid mobile number'; return; }
    this.loading = true; this.message = '';
    this.auth.sendReset(this.mobile).subscribe({
      next: (res:any)=>{
        this.loading = false;
        this.step = 2;
        this.message = res && res.devFallback ? 'Development: OTP simulated' : 'OTP sent';
        // start 60s OTP timer
        this.startOtpTimer(60);
      },
      error: (err)=>{ this.loading = false; this.message = 'Failed to send OTP'; }
    });
  }

  startOtpTimer(seconds: number){
    this.clearOtpTimer();
    this.otpTimer = seconds;
    this.otpExpired = false;
    this.otpInterval = setInterval(()=>{
      this.otpTimer = Math.max(0, this.otpTimer - 1);
      if(this.otpTimer <= 0){
        this.otpExpired = true;
        this.clearOtpTimer();
      }
    }, 1000);
  }

  clearOtpTimer(){
    if(this.otpInterval){ clearInterval(this.otpInterval); this.otpInterval = null; }
  }

  resendOtp(){
    if(this.loading) return;
    this.loading = true; this.message = '';
    this.auth.sendReset(this.mobile).subscribe({
      next: (res:any)=>{
        this.loading = false;
        this.message = res && res.devFallback ? 'Development: OTP simulated' : 'OTP resent';
        this.startOtpTimer(60);
      },
      error: ()=>{ this.loading = false; this.message = 'Failed to resend OTP'; }
    });
  }

  isPhoneValid(){
    const digits = (this.mobile || '').replace(/\D/g, '');
    return digits.length >= 6 && digits.length <= 15;
  }

  isOtpValid(){
    const c = (this.otp || '').trim();
    return c.length >= 3 && c.length <= 8;
  }

  isNewPasswordValid(){
    return (this.password || '').length >= 6;
  }

  reset(){
    if(!this.otp || !this.password) { this.message = 'Enter OTP and new password'; return; }
    if(this.otpExpired){ this.message = 'OTP expired — please resend and try again'; return; }
    this.loading = true; this.message = '';
    this.auth.resetPassword(this.mobile, this.otp, this.password).subscribe({
      next: (res:any)=>{
        this.loading = false;
        this.message = res && res.devFallback ? 'Password reset (dev).' : 'Password reset successful.';
        setTimeout(()=> this.router.navigateByUrl('/me'), 900);
      },
      error: ()=>{ this.loading = false; this.message = 'Failed to reset password'; }
    });
  }

  ngOnDestroy(): void {
    this.clearOtpTimer();
  }
}
