import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService, Product } from '../../services/product.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeComponent implements OnInit, OnDestroy, AfterViewInit {
  private el = inject(ElementRef);
  private productService = inject(ProductService);
  private cdr = inject(ChangeDetectorRef);
  private intervalId: any;

  featuredProducts: Product[] = [];
  categories: any[] = [];
  offers: any[] = [];
  reviews: any[] = [];

  ngOnInit() {
    this.loadData();
  }

  ngAfterViewInit() {
    // Inicializamos el slider nativamente
    this.initSlider();
  }

  ngOnDestroy() {
    this.stopSlider();
  }

  loadData() {
    this.productService.getFeaturedProducts().subscribe({
      next: products => { 
        this.featuredProducts = products; 
        console.log('Featured products loaded:', products); 
        this.cdr.detectChanges();
      },
      error: err => console.error('Error loading featured products:', err)
    });
    
    this.productService.getCategories().subscribe({
      next: categories => { 
        this.categories = categories; 
        console.log('Categories loaded:', categories); 
        this.cdr.detectChanges();
      },
      error: err => console.error('Error loading categories:', err)
    });
    
    this.productService.getOffers().subscribe({
      next: offers => { 
        this.offers = offers; 
        console.log('Offers loaded:', offers); 
        this.cdr.detectChanges();
      },
      error: err => console.error('Error loading offers:', err)
    });

    this.productService.getReviews('2').subscribe({
      next: reviews => { 
        this.reviews = reviews; 
        console.log('Reviews loaded:', reviews); 
        this.cdr.detectChanges();
      },
      error: err => console.error('Error loading reviews:', err)
    });
  }

  private initSlider() {
    console.log('Iniciando Slider...');
    const slider = this.el.nativeElement.querySelector('#hero-slider');
    console.log('Slider element:', slider);
    
    if (!slider) {
      console.error('No se encontró el elemento #hero-slider');
      return;
    }

    const slides = Array.from(slider.querySelectorAll('.hero-slide')) as HTMLElement[];
    console.log('Slides encontrados:', slides.length);
    
    const dots = Array.from(slider.querySelectorAll('.hero-dot')) as HTMLElement[];
    const prevBtn = slider.querySelector('.hero-control.prev');
    const nextBtn = slider.querySelector('.hero-control.next');
    
    console.log('Prev:', prevBtn, 'Next:', nextBtn);
    
    let index = 0;

    const setSlide = (newIndex: number) => {
        // En lugar de buscar el activo mediante loop, mantenemos el estado de 'index' como la fuente de verdad.
        // Pero para asegurar que el DOM se actualice, eliminamos 'active' de todos y añadimos al nuevo.
        console.log('Cambiando a slide:', newIndex);
        index = (newIndex + slides.length) % slides.length;
        
        slides.forEach((slide, i) => {
            slide.classList.toggle('active', i === index);
        });
        dots.forEach((dot, i) => {
            dot.classList.toggle('active', i === index);
        });
    };

    nextBtn?.addEventListener('click', () => {
        console.log('Click en Next');
        setSlide(index + 1);
    });
    prevBtn?.addEventListener('click', () => {
        console.log('Click en Prev');
        setSlide(index - 1);
    });
    dots.forEach((dot, i) => dot.addEventListener('click', () => setSlide(i)));

    this.intervalId = setInterval(() => setSlide(index + 1), 5500);
    console.log('Slider inicializado.');
  }

  private stopSlider() {
    if (this.intervalId) clearInterval(this.intervalId);
  }
}
