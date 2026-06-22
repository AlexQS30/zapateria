import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  credentials = { email: '', password: '' };
  errorMessage = '';

  onLogin() {
    this.authService.login(this.credentials.email, this.credentials.password).subscribe({
      next: (response) => {
        if (response.success) {
          this.authService.redirectByRole();
        } else {
          this.errorMessage = response.message || 'Error en el login';
        }
      },
      error: (err) => {
        this.errorMessage = err.message || 'Error de conexión con el servidor';
      }
    });
  }
}
