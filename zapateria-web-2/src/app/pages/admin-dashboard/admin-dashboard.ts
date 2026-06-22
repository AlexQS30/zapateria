import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
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
  private cdr = inject(ChangeDetectorRef);
  private readonly API_AUTH = 'http://localhost:8081/api/auth';
  private readonly API_SHOES = 'http://localhost:8081/api/shoes';
  private readonly API_PURCHASES = 'http://localhost:8081/api/purchases';
  private readonly API_DASHBOARD = 'http://localhost:8081/api/dashboard';

  user = this.authService.getCurrentUser();
  activeSection = 'dashboard';
  isLoading = false;
  
  users: any[] = [];
  products: any[] = [];
  purchases: any[] = [];
  categories: any[] = [];
  
  // Cache flags
  private loadedUsers = false;
  private loadedProducts = false;
  private loadedPurchases = false;
  private loadedCategories = false;
  
  stats = { users: 0, products: 0, sales: 0, revenue: 0 };
  
  currentProduct: any = null;
  currentUser: any = null;
  currentCategory: any = null;
  isEdit = false;
  isEditUser = false;
  isEditCategory = false;
  showPassword = false;

  ngOnInit() {
    if (!this.authService.isLoggedIn() || this.user?.role !== 'ADMIN') {
        this.authService.logout();
    }
    this.loadStats();
  }

  mostrarSeccion(seccion: string) {
    this.activeSection = seccion;
    if (seccion === 'usuarios' && !this.loadedUsers) this.loadUsers();
    if (seccion === 'productos' && !this.loadedProducts) this.loadProducts();
    if (seccion === 'ordenes' && !this.loadedPurchases) this.loadPurchases();
    if (seccion === 'categorias' && !this.loadedCategories) this.loadCategories();
  }

  errorMessage = '';

  loadStats() {
    this.isLoading = true;
    this.errorMessage = '';
    console.time('LoadStats');
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    
    this.http.get<any>(`${this.API_DASHBOARD}/stats`, { headers }).subscribe({
        next: (s) => {
            this.stats = s;
            this.isLoading = false;
            console.timeEnd('LoadStats');
            this.cdr.detectChanges();
        },
        error: (err) => {
            this.errorMessage = 'Error al cargar las estadísticas. Intenta de nuevo más tarde.';
            console.error('Error loading stats:', err);
            this.isLoading = false;
            this.cdr.detectChanges();
        }
    });
  }

  loadUsers() {
    this.isLoading = true;
    this.errorMessage = '';
    console.time('LoadUsers');
    const token = this.authService.getToken();
    this.http.get<any[]>(`${this.API_AUTH}/users`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe({
        next: (users) => {
            this.users = users;
            this.loadedUsers = true;
            this.isLoading = false;
            console.timeEnd('LoadUsers');
            this.cdr.detectChanges();
        },
        error: () => {
            this.errorMessage = 'No se pudieron cargar los usuarios.';
            this.isLoading = false;
            this.cdr.detectChanges();
        }
    });
  }

  loadProducts() {
    this.isLoading = true;
    this.errorMessage = '';
    console.time('LoadProducts');
    this.http.get<any[]>(`${this.API_SHOES}`).subscribe({
        next: (p) => {
            this.products = p;
            this.loadedProducts = true;
            this.isLoading = false;
            console.timeEnd('LoadProducts');
            this.cdr.detectChanges();
        },
        error: () => {
            this.errorMessage = 'No se pudieron cargar los productos.';
            this.isLoading = false;
            this.cdr.detectChanges();
        }
    });
  }

  loadPurchases() {
    this.isLoading = true;
    this.errorMessage = '';
    console.time('LoadPurchases');
    const token = this.authService.getToken();
    this.http.get<any[]>(`${this.API_PURCHASES}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe({
        next: (p) => {
            this.purchases = p;
            this.loadedPurchases = true;
            this.isLoading = false;
            console.timeEnd('LoadPurchases');
            this.cdr.detectChanges();
        },
        error: () => {
            this.errorMessage = 'No se pudieron cargar las órdenes.';
            this.isLoading = false;
            this.cdr.detectChanges();
        }
    });
  }

  loadCategories() {
    this.isLoading = true;
    this.errorMessage = '';
    console.time('LoadCategories');
    this.http.get<any[]>(`${this.API_SHOES.replace('/shoes', '/categories')}`).subscribe({
        next: (c) => {
            this.categories = c;
            this.loadedCategories = true;
            this.isLoading = false;
            console.timeEnd('LoadCategories');
            this.cdr.detectChanges();
        },
        error: () => {
            this.errorMessage = 'No se pudieron cargar las categorías.';
            this.isLoading = false;
            this.cdr.detectChanges();
        }
    });
  }

  initProductForm(product: any = null) {
    this.isEdit = !!product;
    this.currentProduct = product ? { ...product } : { name: '', price: 0, stock: 0, discount: 0, image: '', category: { id: 1 } };
    if (!this.loadedCategories) this.loadCategories();
  }

  initUserForm(user: any = null) {
    this.isEditUser = !!user;
    this.currentUser = user ? { ...user, password: '' } : { firstName: '', lastName: '', email: '', password: '', role: 'USER', isActive: true };
  }

  initCategoryForm(category: any = null) {
    this.isEditCategory = !!category;
    this.currentCategory = category ? { ...category } : { name: '', image: '' };
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

  saveUser() {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' });
    
    if (this.isEditUser) {
        // Assume PUT /api/auth/users/{id}
        this.http.put(`${this.API_AUTH}/users/${this.currentUser.id}`, this.currentUser, { headers }).subscribe(() => this.finishUserForm());
    } else {
        // Assume POST /api/auth/register
        this.http.post(`${this.API_AUTH}/register`, this.currentUser, { headers }).subscribe(() => this.finishUserForm());
    }
  }

  saveCategory() {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' });
    const url = `${this.API_SHOES.replace('/shoes', '/categories')}`;
    
    if (this.isEditCategory) {
        this.http.put(`${url}/${this.currentCategory.id}`, this.currentCategory, { headers }).subscribe(() => this.finishCategoryForm());
    } else {
        this.http.post(url, this.currentCategory, { headers }).subscribe(() => this.finishCategoryForm());
    }
  }

  finishProductForm() {
    this.currentProduct = null;
    this.loadProducts();
  }

  finishUserForm() {
    this.currentUser = null;
    this.loadUsers();
  }

  finishCategoryForm() {
    this.currentCategory = null;
    this.loadCategories();
  }

  deleteProduct(id: string) {
    if (!confirm("¿Eliminar producto?")) return;
    const token = this.authService.getToken();
    this.http.delete(`${this.API_SHOES}/${id}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe(() => this.loadProducts());
  }

  deleteUser(id: string) {
    if (!confirm("¿Eliminar usuario?")) return;
    const token = this.authService.getToken();
    this.http.delete(`${this.API_AUTH}/users/${id}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe(() => this.loadUsers());
  }

  deleteCategory(id: string) {
    if (!confirm("¿Eliminar categoría?")) return;
    const token = this.authService.getToken();
    this.http.delete(`${this.API_SHOES.replace('/shoes', '/categories')}/${id}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` })
    }).subscribe(() => this.loadCategories());
  }

  logout() {
    this.authService.logout();
  }
}
