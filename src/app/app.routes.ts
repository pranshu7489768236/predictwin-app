import { Routes } from '@angular/router';


export const routes: Routes = [
	{ path: '', redirectTo: 'home', pathMatch: 'full' },
	{ path: 'home', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
	{ path: 'search', loadComponent: () => import('./pages/search/search.component').then(m => m.SearchComponent) },
	{ path: 'me', loadComponent: () => import('./pages/me/me.component').then(m => m.MeComponent) },
	{ path: 'forgot', loadComponent: () => import('./pages/forgot/forgot.component').then(m => m.ForgotComponent) },
	{ path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent) },
	{ path: 'game', loadComponent: () => import('./pages/game/game.component').then(m => m.GameComponent) },
	{ path: 'wallet', loadComponent: () => import('./pages/wallet/wallet.component').then(m => m.WalletComponent) },
	{ path: 'profile', loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent) },
	{ path: '**', redirectTo: 'home' }
];
