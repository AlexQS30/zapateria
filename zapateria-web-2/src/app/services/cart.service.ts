import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, lastValueFrom } from 'rxjs';

export interface CartItem {
  productId: string;
  name: string;
  price: number;
  quantity: number;
  size: string;
  color: string;
  image: string;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly CART_STORAGE_KEY = 'footstyleCart';
  private readonly API_BASE = 'http://localhost:8081/api';
  private http = inject(HttpClient);
  
  private _cartItems = signal<CartItem[]>(this.loadCart());
  public readonly cartItems = this._cartItems.asReadonly();
  public readonly cartCount = computed(() => this._cartItems().reduce((sum, item) => sum + item.quantity, 0));

  private loadCart(): CartItem[] {
    const saved = localStorage.getItem(this.CART_STORAGE_KEY);
    return saved ? JSON.parse(saved) : [];
  }

  private saveCart(items: CartItem[]): void {
    localStorage.setItem(this.CART_STORAGE_KEY, JSON.stringify(items));
    this._cartItems.set(items);
  }

  addItem(item: CartItem): void {
    const current = this._cartItems();
    const existing = current.find(i => 
      i.productId === item.productId && 
      i.size === item.size && 
      i.color === item.color
    );
    
    if (existing) {
      existing.quantity += item.quantity;
      this.saveCart([...current]);
    } else {
      this.saveCart([...current, item]);
    }
  }

  removeItem(productId: string, size: string, color: string): void {
    this.saveCart(this._cartItems().filter(i => 
      !(i.productId === productId && i.size === size && i.color === color)
    ));
  }

  updateQuantity(productId: string, size: string, color: string, quantity: number): void {
    const current = this._cartItems();
    const item = current.find(i => i.productId === productId && i.size === size && i.color === color);
    if (item) {
      item.quantity = Math.max(1, quantity);
      this.saveCart([...current]);
    }
  }

  clearCart(): void {
    this.saveCart([]);
  }

  // Métodos auxiliares para variantes/stock (migrados de cart.js)
  getProductVariants(productId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_BASE}/shoes/${productId}/variants`);
  }
}
