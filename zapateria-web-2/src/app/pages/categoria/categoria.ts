import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductService, Product } from '../../services/product.service';

@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categoria.html',
  styleUrl: './categoria.css',
})
export class CategoriaComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);

  categoryName: string = '';
  products: Product[] = [];

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.categoryName = params['name'];
      this.loadProducts();
    });
  }

  loadProducts() {
    this.productService.getProductsByCategory(this.categoryName).subscribe(products => {
      this.products = products;
    });
  }
}
