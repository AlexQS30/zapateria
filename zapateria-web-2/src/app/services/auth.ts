import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  token?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = 'http://localhost:8081/api/auth';
  private currentUser: User | null = null;

  constructor(private http: HttpClient) {
    const savedUser = sessionStorage.getItem('user');
    if (savedUser) {
      this.currentUser = JSON.parse(savedUser);
    }
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.success) {
          this.currentUser = {
            id: response.id,
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            role: response.role
          };
          sessionStorage.setItem('user', JSON.stringify(this.currentUser));
          sessionStorage.setItem('loggedIn', 'true');
          localStorage.setItem('auth_token', response.token);
        }
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/register`, userData);
  }

  logout() {
    this.currentUser = null;
    sessionStorage.clear();
    localStorage.removeItem('auth_token');
  }

  isLoggedIn(): boolean {
    return sessionStorage.getItem('loggedIn') === 'true';
  }

  getCurrentUser(): User | null {
    return this.currentUser;
  }
}
