import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  image: string;
  discount: number;
  reviews: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getFeaturedProducts(): Observable<Product[]> {
    console.log('Fetching featured products...');
    return this.http.get<Product[]>(`${this.baseUrl}/shoes`);
  }

  getProductsByCategory(categoryName: string): Observable<Product[]> {
    console.log('Fetching products by category:', categoryName);
    return this.http.get<Product[]>(`${this.baseUrl}/shoes/category/${encodeURIComponent(categoryName)}`);
  }

  getOffers(): Observable<any[]> {
    console.log('Fetching offers...');
    return this.http.get<any[]>(`${this.baseUrl}/shoes/offers`);
  }

  getCategories(): Observable<any[]> {
    console.log('Fetching categories...');
    return this.http.get<any[]>(`${this.baseUrl}/categories`);
  }

  getReviews(productId: string): Observable<any[]> {
    console.log('Fetching reviews for product:', productId);
    return this.http.get<any[]>(`${this.baseUrl}/products/${productId}/reviews`);
  }
}
