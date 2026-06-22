import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-mis-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-pedidos.html',
  styleUrls: ['./mis-pedidos.css']
})
export class MisPedidosComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly API_BASE = 'http://localhost:8081/api';

  orders: any[] = [];
  message = '';

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    const token = this.authService.getToken();
    if (!token) {
      this.message = 'Inicia sesión para ver tus pedidos.';
      return;
    }

    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.get<any[]>(`${this.API_BASE}/purchases/me`, { headers }).subscribe({
      next: (orders) => {
        this.orders = orders;
        if (this.orders.length === 0) {
          this.message = 'Todavía no tienes pedidos registrados.';
        }
      },
      error: () => {
        this.message = 'No se pudieron cargar tus pedidos.';
      }
    });
  }

  formatStatus(status: string): string {
    const statuses: { [key: string]: string } = {
      REGISTRADO: 'Registrado',
      EN_CAMINO: 'En camino',
      RECHAZADO: 'Rechazado',
      ENTREGADO: 'Entregado'
    };
    return statuses[(status || '').toUpperCase()] || status || 'Registrado';
  }

  statusColor(status: string): string {
    const value = (status || '').toUpperCase();
    if (value === 'ENTREGADO') return '#2e7d32';
    if (value === 'EN_CAMINO') return '#1565c0';
    if (value === 'RECHAZADO') return '#c62828';
    return '#8d6e63';
  }

  reviewProduct(productId: string, productName: string) {
    const rating = prompt(`Califica ${productName} del 1 al 5`);
    if (!rating) return;
    const comment = prompt('Escribe tu comentario (opcional)') || '';
    
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    
    this.http.post(`${this.API_BASE}/products/${productId}/reviews?rating=${rating}&comment=${comment}`, {}, { headers }).subscribe({
        next: () => alert('Reseña enviada correctamente'),
        error: () => alert('No se pudo enviar la reseña')
    });
  }
}
