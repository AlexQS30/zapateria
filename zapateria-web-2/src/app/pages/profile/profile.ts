import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container" style="padding: 40px 0;">
        <div class="card" style="max-width: 500px; margin: 0 auto; padding: 30px;">
            <h2>Mi Perfil</h2>
            <div *ngIf="errorMessage" style="color: red; margin-bottom: 10px;">{{ errorMessage }}</div>
            <div *ngIf="successMessage" style="color: green; margin-bottom: 10px;">{{ successMessage }}</div>
            
            <input type="text" [(ngModel)]="user.firstName" placeholder="Nombre" style="width: 100%; padding: 10px; margin-bottom: 10px;">
            <input type="text" [(ngModel)]="user.lastName" placeholder="Apellido" style="width: 100%; padding: 10px; margin-bottom: 10px;">
            <input type="email" [(ngModel)]="user.email" placeholder="Email" style="width: 100%; padding: 10px; margin-bottom: 10px;" disabled>
            <input type="password" [(ngModel)]="user.password" placeholder="Nueva Contraseña (dejar vacío para no cambiar)" style="width: 100%; padding: 10px; margin-bottom: 10px;">
            
            <button class="btn btn-primary" (click)="saveProfile()" style="width: 100%; padding: 10px;">Guardar Cambios</button>
        </div>
    </div>
  `
})
export class ProfileComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  user: any = {};
  errorMessage = '';
  successMessage = '';

  ngOnInit() {
    this.user = { ...this.authService.getCurrentUser() };
  }

  saveProfile() {
    this.errorMessage = '';
    this.successMessage = '';
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' });
    
    // PUT /api/auth/users/{id}
    this.http.put(`http://localhost:8081/api/auth/users/${this.user.id}`, this.user, { headers }).subscribe({
        next: () => {
            this.successMessage = 'Perfil actualizado correctamente';
            // Optionally update user in storage
        },
        error: () => this.errorMessage = 'Error al actualizar perfil'
    });
  }
}
