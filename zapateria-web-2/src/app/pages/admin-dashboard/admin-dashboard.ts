import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboardComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly API_AUTH = 'http://localhost:8081/api/auth';
  private readonly API_SHOES = 'http://localhost:8081/api/shoes';
  private readonly API_PURCHASES = 'http://localhost:8081/api/purchases';

  user = this.authService.getCurrentUser();
  activeSection = 'dashboard';
  
  users: any[] = [];
  products: any[] = [];
  purchases: any[] = [];
  
  stats = { users: 0, products: 0, sales: 0, revenue: 0 };
  
  currentProduct: any = null;
  isEdit = false;

  ngOnInit() {
    if (!this.authService.isLoggedIn() || this.user?.role !== 'ADMIN') {
        this.authService.logout();
    }
    this.loadStats();
  }

  mostrarSeccion(seccion: string) {
    this.activeSection = seccion;
    if (seccion === 'usuarios') this.loadUsers();
    if (seccion === 'productos') this.loadProducts();
    if (seccion === 'ordenes') this.loadPurchases();
  }

  loadStats() {
    this.http.get<any[]>(`${this.API_AUTH}/users`).subscribe(u => this.stats.users = u.length);
    this.http.get<any[]>(`${this.API_SHOES}`).subscribe(p => this.stats.products = p.length);
    this.http.get<any[]>(`${this.API_PURCHASES}/me`).subscribe(p => {
        this.purchases = p;
        this.stats.sales = p.length;
        this.stats.revenue = p.reduce((acc, curr) => acc + (curr.total || 0), 0);
    });
  }

  loadUsers() {
    const token = this.authService.getToken();
    this.http.get<any[]>(`${this.API_AUTH}/users`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe(users => this.users = users);
  }

  loadProducts() {
    this.http.get<any[]>(`${this.API_SHOES}`).subscribe(p => this.products = p);
  }

  loadPurchases() {
    const token = this.authService.getToken();
    this.http.get<any[]>(`${this.API_PURCHASES}/me`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe(p => this.purchases = p);
  }

  initProductForm(product: any = null) {
    this.isEdit = !!product;
    this.currentProduct = product ? { ...product } : { name: '', price: 0, stock: 0, discount: 0, image: '', category: { id: 1 } };
  }

  saveProduct() {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' });
    
    if (this.isEdit) {
      this.http.put(`${this.API_SHOES}/${this.currentProduct.id}`, this.currentProduct, { headers }).subscribe(() => this.finishProductForm());
    } else {
      this.http.post(`${this.API_SHOES}`, this.currentProduct, { headers }).subscribe(() => this.finishProductForm());
    }
  }

  finishProductForm() {
    this.currentProduct = null;
    this.loadProducts();
  }

  deleteProduct(id: string) {
    if (!confirm("¿Eliminar producto?")) return;
    const token = this.authService.getToken();
    this.http.delete(`${this.API_SHOES}/${id}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe(() => this.loadProducts());
  }

  updatePurchaseStatus(id: string, status: string) {
    const token = this.authService.getToken();
    this.http.patch(`${this.API_PURCHASES}/${id}/status`, { status }, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' })
    }).subscribe(() => this.loadPurchases());
  }

  logout() {
    this.authService.logout();
  }
}
