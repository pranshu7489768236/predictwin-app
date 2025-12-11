import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy{
  images = ['/assets/slider1.svg','/assets/slider2.svg','/assets/slider3.svg'];
  idx = 0;
  intervalId: any = null;

  trending = [
    {title: 'Quick Predict', subtitle: '4 colors', prize: 'x2.5'},
    {title: 'Pro Match', subtitle: '10 colors', prize: 'x5'},
    {title: 'Daily Rush', subtitle: '3 colors', prize: 'x1.8'}
  ];

  hotColors = ['#FF7EB6','#7AFCC6','#FFD36E','#7B61FF','#00D4FF'];

  todaysWins = [
    {user: 'Alice', win: '₹1200'},
    {user: 'Raj', win: '₹450'},
    {user: 'Maya', win: '₹360'}
  ];

  ngOnInit(){
    this.intervalId = setInterval(()=> this.next(), 3000);
  }

  ngOnDestroy(){
    if(this.intervalId) clearInterval(this.intervalId);
  }

  next(){
    this.idx = (this.idx + 1) % this.images.length;
  }

  go(i:number){
    this.idx = i;
  }
}

