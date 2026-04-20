// product-integration.js
// Script de integración rápida para conectar el frontend con los servicios backend

class FootStyleAPI {
    constructor(baseUrl = 'http://localhost:8080/api') {
        this.baseUrl = baseUrl;
        this.token = localStorage.getItem('auth_token') || null;
    }

    // Métodos auxiliares
    async request(endpoint, method = 'GET', body = null) {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
                ...(this.token && { 'Authorization': `Bearer ${this.token}` })
            }
        };

        if (body) options.body = JSON.stringify(body);

        try {
            const response = await fetch(`${this.baseUrl}${endpoint}`, options);
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return await response.json();
        } catch (error) {
            console.error(`Error en ${endpoint}:`, error);
            throw error;
        }
    }

    // ==================== CATEGORÍAS ====================
    async getCategories() {
        return this.request('/categories');
    }

    async getCategoryById(id) {
        return this.request(`/categories/${id}`);
    }

    // ==================== PRODUCTOS ====================
    async getFeaturedProducts(limit = 4) {
        return this.request(`/products/featured?limit=${limit}`);
    }

    async getProductById(id) {
        return this.request(`/products/${id}`);
    }

    async getProductsByCategory(categoryId) {
        return this.request(`/products?categoryId=${categoryId}`);
    }

    async searchProducts(query) {
        return this.request(`/products/search?q=${encodeURIComponent(query)}`);
    }

    // ==================== OFERTAS ====================
    async getOffers() {
        return this.request('/offers');
    }

    async getOffersByCategory(categoryId) {
        return this.request(`/offers?categoryId=${categoryId}`);
    }

    // ==================== CARRITO ====================
    async getCart() {
        return this.request('/cart');
    }

    async addToCart(productId, quantity = 1) {
        return this.request('/cart/add', 'POST', {
            productId,
            quantity
        });
    }

    async removeFromCart(productId) {
        return this.request(`/cart/remove/${productId}`, 'DELETE');
    }

    async updateCartItem(productId, quantity) {
        return this.request(`/cart/update/${productId}`, 'PUT', {
            quantity
        });
    }

    async clearCart() {
        return this.request('/cart/clear', 'DELETE');
    }

    // ==================== NEWSLETTER ====================
    async subscribeNewsletter(email) {
        return this.request('/newsletter/subscribe', 'POST', {
            email
        });
    }

    // ==================== RESEÑAS ====================
    async getProductReviews(productId) {
        return this.request(`/reviews/product/${productId}`);
    }

    async createReview(productId, review) {
        return this.request('/reviews', 'POST', {
            productId,
            rating: review.rating,
            comment: review.comment
        });
    }

    // ==================== COMPRAS ====================
    async createPurchase(cartItems) {
        return this.request('/purchases', 'POST', {
            items: cartItems
        });
    }

    async getUserPurchases() {
        return this.request('/purchases/user');
    }

    async getPurchaseById(id) {
        return this.request(`/purchases/${id}`);
    }
}

// ==================== INICIALIZACIÓN ====================
const api = new FootStyleAPI();

// Por defecto, index funciona en modo estático sin consumir APIs.
// Para activar integración real, define: window.FOOTSTYLE_STATIC_MODE = false
const isStaticMode = window.FOOTSTYLE_STATIC_MODE !== undefined
    ? window.FOOTSTYLE_STATIC_MODE
    : true;

// ==================== FUNCIONES DE RENDERIZADO ====================

/**
 * Renderiza categorías en la sección "Encuentra tu Estilo"
 */
async function renderCategories() {
    try {
        const categories = await api.getCategories();
        const grid = document.querySelector('.categories-grid');

        if (!grid) return;

        grid.innerHTML = categories.map(cat => `
            <div class="category-card">
                <div class="category-image">
                    <img src="${cat.imageUrl || 'https://via.placeholder.com/400'}" 
                         alt="${cat.name}" 
                         loading="lazy">
                </div>
                <div class="category-info">
                    <h3>${cat.name}</h3>
                    <p>${cat.description || 'Descubre nuestra colección'}</p>
                    <a href="/categoria/${cat.id}" class="btn btn-secondary">
                        Ver Colección
                    </a>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error renderizando categorías:', error);
        document.getElementById('categories-empty').textContent = 
            'Error al cargar categorías. Por favor intenta más tarde.';
    }
}

/**
 * Renderiza ofertas especiales
 */
async function renderOffers() {
    try {
        const offers = await api.getOffers();
        const grid = document.querySelector('.ofertas .categories-grid');

        if (!grid) return;

        grid.innerHTML = offers.slice(0, 4).map(offer => `
            <div class="category-card" style="position: relative;">
                <div class="category-image" style="position: relative;">
                    <img src="${offer.imageUrl || 'https://via.placeholder.com/400'}" 
                         alt="${offer.categoryName}"
                         loading="lazy">
                    <div style="position: absolute; top: 15px; right: 15px; 
                                background: #FFD700; color: #333; padding: 12px 20px; 
                                border-radius: 8px; font-weight: bold; font-size: 18px;">
                        -${offer.discountPercentage || 25}%
                    </div>
                </div>
                <div class="category-info" style="background: rgba(255,255,255,0.95);">
                    <h3 style="color: #333;">${offer.categoryName}</h3>
                    <p style="color: #666;">${offer.description || 'Descuentos especiales'}</p>
                    <div class="offer-percent">Hasta ${offer.discountPercentage || 25}% OFF</div>
                    <a href="/categoria/${offer.categoryId}" class="btn btn-secondary">
                        Ver Oferta
                    </a>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error renderizando ofertas:', error);
    }
}

/**
 * Renderiza productos destacados
 */
async function renderFeaturedProducts() {
    const loader = document.getElementById('products-loader');
    const grid = document.querySelector('.products-grid');

    if (!grid) return;

    if (loader) loader.style.display = 'block';

    try {
        const products = await api.getFeaturedProducts(4);

        grid.innerHTML = products.map(product => `
            <div class="product-card">
                <div class="product-image">
                    <img src="${product.imageUrl || 'https://via.placeholder.com/400'}" 
                         alt="${product.name}"
                         loading="lazy"
                         style="width: 100%; height: 100%; object-fit: cover;">
                    ${product.discountPercentage ? 
                        `<span class="badge-discount">-${product.discountPercentage}%</span>` : ''}
                    ${product.isNew ? `<span class="badge-new">★ Nuevo</span>` : ''}
                    <div class="product-overlay">
                        <button class="btn-icon" onclick="navigateTo('/producto/${product.id}')">
                            <i class="fas fa-eye"></i> Ver detalles
                        </button>
                    </div>
                </div>
                <div class="product-info">
                    <h4 class="product-name">${product.name}</h4>
                    <p class="product-category">${product.categoryName || 'Categoría'}</p>
                    <div class="rating">
                        ${renderStars(product.rating || 4.5)}
                        <span>(${product.reviewCount || 0})</span>
                    </div>
                    <div class="product-prices">
                        ${product.originalPrice ? 
                            `<span class="price-original">S/. ${product.originalPrice.toFixed(2)}</span>` : ''}
                        <span class="product-price">S/. ${product.price.toFixed(2)}</span>
                    </div>
                    <button class="btn btn-outline" onclick="addToCart(${product.id})">
                        <i class="fas fa-shopping-cart"></i> Agregar al carrito
                    </button>
                </div>
            </div>
        `).join('');

        if (loader) loader.style.display = 'none';
    } catch (error) {
        console.error('Error renderizando productos:', error);
        if (loader) loader.textContent = 'Error al cargar productos';
    }
}

/**
 * Genera HTML de estrellas de calificación
 */
function renderStars(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= Math.floor(rating)) {
            stars += '<i class="fas fa-star"></i>';
        } else if (i - 0.5 <= rating) {
            stars += '<i class="fas fa-star-half-alt"></i>';
        } else {
            stars += '<i class="far fa-star"></i>';
        }
    }
    return stars;
}

// ==================== FUNCIONES DE ACCIÓN ====================

/**
 * Agregar producto al carrito
 */
async function addToCart(productId) {
    try {
        await api.addToCart(productId, 1);
        showNotification('Producto agregado al carrito', 'success');
        updateCartBadge();
    } catch (error) {
        showNotification('Error al agregar al carrito', 'error');
    }
}

/**
 * Actualizar badge del carrito
 */
async function updateCartBadge() {
    try {
        const cart = await api.getCart();
        const badge = document.querySelector('.action-item:has(i.fa-shopping-cart) .badge');
        if (badge) {
            badge.textContent = cart.items ? cart.items.length : 0;
        }
    } catch (error) {
        console.error('Error actualizando carrito:', error);
    }
}

/**
 * Navegar a una URL
 */
function navigateTo(url) {
    window.location.href = url;
}

/**
 * Mostrar notificación temporal
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        background: ${type === 'success' ? '#28a745' : '#dc3545'};
        color: white;
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
}

// ==================== NEWSLETTER ====================

const newsletterForm = document.getElementById('newsletterForm');
if (newsletterForm && !isStaticMode) {
    newsletterForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = newsletterForm.querySelector('input[type="email"]').value;
        const message = document.getElementById('newsletter-message');

        try {
            await api.subscribeNewsletter(email);
            message.style.display = 'block';
            message.style.color = 'var(--secondary)';
            message.innerHTML = '✓ ¡Suscripción exitosa! Revisa tu correo.';
            newsletterForm.reset();

            setTimeout(() => {
                message.style.display = 'none';
            }, 4000);
        } catch (error) {
            message.style.display = 'block';
            message.style.color = '#ff6b6b';
            message.innerHTML = 'Error al suscribirse. Intenta nuevamente.';
        }
    });
}

// ==================== INICIALIZACIÓN EN CARGA ====================

document.addEventListener('DOMContentLoaded', () => {
    if (isStaticMode) {
        return;
    }

    renderCategories();
    renderOffers();
    renderFeaturedProducts();
    updateCartBadge();
});

// ==================== EXPORTAR API ====================
// Para usar en otros scripts: import { api } from './product-integration.js';
// O si se carga con <script>: window.FootStyleAPI = FootStyleAPI;
