# Integración Frontend con Servicios Backend - FootStyle

## Descripción General
Este documento detalla cómo integrar las secciones del frontend con los servicios REST del backend de FootStyle.

---

## 1. Sección: Categorías ("Encuentra tu Estilo")

### HTML ID
```html
<section class="categories" id="categorias">
```

### Endpoint Backend Requerido
```
GET /api/categories
GET /api/categories/{id}
```

### Integración JavaScript
```javascript
async function loadCategories() {
    try {
        const response = await fetch('http://localhost:8080/api/categories');
        const categories = await response.json();
        
        const grid = document.querySelector('.categories-grid');
        grid.innerHTML = categories.map(cat => `
            <div class="category-card">
                <div class="category-image">
                    <img src="${cat.imageUrl}" alt="${cat.name}">
                </div>
                <div class="category-info">
                    <h3>${cat.name}</h3>
                    <p>${cat.description}</p>
                    <a href="/categoria/${cat.id}" class="btn btn-secondary">Ver Colección</a>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error cargando categorías:', error);
        document.getElementById('categories-empty').textContent = 'Error al cargar categorías';
    }
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', loadCategories);
```

### Datos Esperados del Backend
```json
[
    {
        "id": 1,
        "name": "Calzados Hombre",
        "description": "Zapatos elegantes y cómodos",
        "imageUrl": "https://images.unsplash.com/photo-1542291026-7eec264c27ff"
    },
    {
        "id": 2,
        "name": "Calzados Mujer",
        "description": "Estilo y comodidad en cada paso",
        "imageUrl": "https://images.unsplash.com/photo-1546261150-ce1b0db87a6f"
    }
]
```

---

## 2. Sección: Ofertas Especiales

### HTML ID
```html
<section class="ofertas offers" id="ofertas">
```

### Endpoint Backend Requerido
```
GET /api/offers
GET /api/products?discount=true
```

### Integración JavaScript
```javascript
async function loadOffers() {
    try {
        const response = await fetch('http://localhost:8080/api/offers');
        const offers = await response.json();
        
        const grid = document.querySelector('.ofertas .categories-grid');
        grid.innerHTML = offers.map(offer => `
            <div class="category-card" style="position: relative;">
                <div class="category-image" style="position: relative;">
                    <img src="${offer.imageUrl}" alt="${offer.name}">
                    <div style="position: absolute; top: 15px; right: 15px; background: #FFD700; color: #333; padding: 12px 20px; border-radius: 8px; font-weight: bold; font-size: 18px;">
                        -${offer.discountPercentage}%
                    </div>
                </div>
                <div class="category-info" style="background: rgba(255,255,255,0.95);">
                    <h3 style="color: #333;">${offer.categoryName}</h3>
                    <p style="color: #666;">${offer.description}</p>
                    <div class="offer-percent">Hasta ${offer.discountPercentage}% OFF</div>
                    <a href="/categoria/${offer.categoryId}" class="btn btn-secondary">Ver Oferta</a>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error cargando ofertas:', error);
    }
}

document.addEventListener('DOMContentLoaded', loadOffers);
```

---

## 3. Sección: Productos Destacados

### HTML ID
```html
<section class="featured" id="featured">
```

### Endpoint Backend Requerido
```
GET /api/products/featured
GET /api/products?limit=4&sort=rating
```

### Integración JavaScript
```javascript
async function loadFeaturedProducts() {
    const loader = document.getElementById('products-loader');
    loader.style.display = 'block';
    
    try {
        const response = await fetch('http://localhost:8080/api/products/featured');
        const products = await response.json();
        
        const grid = document.querySelector('.products-grid');
        grid.innerHTML = products.map(product => `
            <div class="product-card">
                <div class="product-image">
                    <img src="${product.imageUrl}" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;">
                    ${product.discount ? `<span class="badge-discount">-${product.discount}%</span>` : ''}
                    ${product.isNew ? `<span class="badge-new">★ Nuevo</span>` : ''}
                    <div class="product-overlay">
                        <button class="btn-icon" onclick="openProductDetail(${product.id})">
                            <i class="fas fa-eye"></i> Ver detalles
                        </button>
                    </div>
                </div>
                <div class="product-info">
                    <h4 class="product-name">${product.name}</h4>
                    <p class="product-category">${product.categoryName}</p>
                    <div class="rating">
                        ${generateStars(product.rating)} 
                        <span>(${product.reviews})</span>
                    </div>
                    <div class="product-prices">
                        ${product.originalPrice ? `<span class="price-original">S/. ${product.originalPrice}</span>` : ''}
                        <span class="product-price">S/. ${product.price}</span>
                    </div>
                    <button class="btn btn-outline" onclick="addToCart(${product.id})">
                        <i class="fas fa-shopping-cart"></i> Agregar al carrito
                    </button>
                </div>
            </div>
        `).join('');
        
        loader.style.display = 'none';
    } catch (error) {
        console.error('Error cargando productos:', error);
        loader.textContent = 'Error al cargar productos';
    }
}

function generateStars(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            stars += '<i class="fas fa-star"></i>';
        } else if (i - 0.5 <= rating) {
            stars += '<i class="fas fa-star-half-alt"></i>';
        } else {
            stars += '<i class="far fa-star"></i>';
        }
    }
    return stars;
}

document.addEventListener('DOMContentLoaded', loadFeaturedProducts);
```

---

## 4. Sección: Newsletter

### HTML ID
```html
<section class="newsletter" id="newsletter">
```

### Endpoint Backend Requerido
```
POST /api/newsletter/subscribe
```

### Integración JavaScript (Ya incluida)
```javascript
const newsletterForm = document.getElementById('newsletterForm');
const newsletterMessage = document.getElementById('newsletter-message');

if (newsletterForm) {
    newsletterForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = newsletterForm.querySelector('input[type="email"]').value;
        
        try {
            const response = await fetch('http://localhost:8080/api/newsletter/subscribe', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email })
            });
            
            if (response.ok) {
                newsletterMessage.style.display = 'block';
                newsletterMessage.style.color = 'var(--secondary)';
                newsletterMessage.textContent = '✓ ¡Suscripción exitosa! Revisa tu correo.';
                newsletterForm.reset();
                
                setTimeout(() => {
                    newsletterMessage.style.display = 'none';
                }, 4000);
            } else {
                throw new Error('Error en la respuesta');
            }
        } catch (error) {
            newsletterMessage.style.display = 'block';
            newsletterMessage.style.color = '#ff6b6b';
            newsletterMessage.textContent = 'Error al suscribirse. Intenta nuevamente.';
        }
    });
}
```

---

## 5. Funciones Auxiliares Necesarias

### Agregar al Carrito
```javascript
async function addToCart(productId) {
    try {
        const response = await fetch('http://localhost:8080/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
                productId: productId,
                quantity: 1
            })
        });
        
        if (response.ok) {
            alert('Producto agregado al carrito');
            updateCartBadge();
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al agregar al carrito');
    }
}
```

### Abrir Detalle de Producto
```javascript
function openProductDetail(productId) {
    window.location.href = `/producto/${productId}`;
}
```

### Actualizar Badge del Carrito
```javascript
async function updateCartBadge() {
    try {
        const response = await fetch('http://localhost:8080/api/cart', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const cart = await response.json();
        document.querySelector('.action-item:has(i.fa-shopping-cart) .badge').textContent = cart.items.length;
    } catch (error) {
        console.error('Error actualizando carrito:', error);
    }
}
```

---

## 6. Variables de Configuración

Crea un archivo `config.js` en la carpeta `/static/js/`:

```javascript
// config.js
const API_BASE_URL = 'http://localhost:8080/api';
const SOCKET_URL = 'ws://localhost:8080/ws';

export const API = {
    CATEGORIES: `${API_BASE_URL}/categories`,
    PRODUCTS: `${API_BASE_URL}/products`,
    OFFERS: `${API_BASE_URL}/offers`,
    CART: `${API_BASE_URL}/cart`,
    NEWSLETTER: `${API_BASE_URL}/newsletter`,
    LOGIN: `${API_BASE_URL}/auth/login`,
    REGISTER: `${API_BASE_URL}/auth/register`
};
```

---

## 7. Estructura de Datos Esperados

### Producto
```json
{
    "id": 1,
    "name": "Zapato Casual Premium",
    "categoryId": 1,
    "categoryName": "Zapatos Casuales",
    "description": "Descripción del producto",
    "price": 199.00,
    "originalPrice": 249.00,
    "discount": 20,
    "imageUrl": "https://images.unsplash.com/...",
    "rating": 4.5,
    "reviews": 245,
    "isNew": false,
    "inStock": true
}
```

### Categoría
```json
{
    "id": 1,
    "name": "Calzados Hombre",
    "description": "Zapatos elegantes y cómodos",
    "imageUrl": "https://images.unsplash.com/...",
    "productCount": 150
}
```

### Oferta
```json
{
    "id": 1,
    "categoryId": 1,
    "categoryName": "Calzados Hombre",
    "name": "Oferta Especial",
    "description": "Zapatos elegantes en oferta",
    "discountPercentage": 35,
    "imageUrl": "https://images.unsplash.com/...",
    "validUntil": "2024-12-31"
}
```

---

## 8. CORS Configuration en Backend

Asegurate que el backend incluya CORS:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## 9. Checklist de Integración

- [ ] Crear endpoint GET `/api/categories`
- [ ] Crear endpoint GET `/api/offers`
- [ ] Crear endpoint GET `/api/products/featured`
- [ ] Crear endpoint POST `/api/newsletter/subscribe`
- [ ] Crear endpoint POST `/api/cart/add`
- [ ] Crear endpoint GET `/api/cart`
- [ ] Configurar CORS en backend
- [ ] Implementar autenticación con JWT
- [ ] Crear página de detalles de producto
- [ ] Implementar carrito de compras funcional
- [ ] Verificar imágenes cargan correctamente
- [ ] Validar formularios en frontend
- [ ] Agregar manejo de errores completo

---

## 10. Próximos Pasos

1. **Backend**: Implementar todos los endpoints listados
2. **Frontend**: Crear archivos `.js` separados para cada integración
3. **Testing**: Probar cada sección con datos reales del backend
4. **Optimización**: Implementar lazy loading para imágenes
5. **Seguridad**: Validar y sanitizar todos los datos del usuario

---

**Última actualización**: 2024 | **Versión**: 1.0
