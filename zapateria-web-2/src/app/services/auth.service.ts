import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_BASE = 'http://localhost:8081/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user';
  private http = inject(HttpClient);
  private router = inject(Router);

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.API_BASE}/login`, { email, password }).pipe(
      tap((response: any) => {
        if (response.success) {
          const user = {
            id: response.id,
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            role: response.role
          };
          this.setSession(user, response.token);
        }
      })
    );
  }

  redirectByRole(): void {
    const user = this.getCurrentUser();
    if (user && user.role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else {
      this.router.navigate(['/']);
    }
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.API_BASE}/register`, userData);
  }

  setSession(user: any, token: string): void {
    sessionStorage.setItem(this.USER_KEY, JSON.stringify(user));
    sessionStorage.setItem('loggedIn', 'true');
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  logout(): void {
    sessionStorage.removeItem(this.USER_KEY);
    sessionStorage.removeItem('loggedIn');
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return sessionStorage.getItem('loggedIn') === 'true' && !!localStorage.getItem(this.TOKEN_KEY);
  }

  getCurrentUser(): any {
    const user = sessionStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
}
