/**
 * Script para cargar productos por categoría desde el API del backend
 */

const BACKEND_API = 'http://localhost:8081/api';
let categories = [];

document.addEventListener('DOMContentLoaded', async function() {
    console.log('Categoria.js cargado');
    
    // Cargar categorías del backend PRIMERO
    await loadCategories();
    
    // LUEGO obtener la categoría de la URL
    const currentCategoryId = getCategoryIdFromPath();
    console.log('ID de Categoría actual:', currentCategoryId);
    
    // FINALMENTE cargar productos para esta categoría
    if (currentCategoryId) {
        await loadProductsByCategory(currentCategoryId);
    }
});

/**
 * Carga todas las categorías desde el backend
 */
async function loadCategories() {
    try {
        console.log('Cargando categorías...');
        const response = await fetch(`${BACKEND_API}/categories`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        categories = await response.json();
        console.log('Categorías cargadas:', categories);
        
    } catch (error) {
        console.error('Error cargando categorías:', error.message);
    }
}

/**
 * Obtiene el ID de categoría desde el pathname
 * Resuelve cualquier slug de categoría contra las categorías cargadas desde el backend
 */
function getCategoryIdFromPath() {
    const path = window.location.pathname;
    console.log('Pathname actual:', path);

    const queryCategory = new URLSearchParams(window.location.search).get('categoria');
    if (queryCategory) {
        const normalizedQuery = normalizeSlug(queryCategory);
        const queryCategoryMatch = categories.find(cat => normalizeSlug(cat.name) === normalizedQuery);
        if (queryCategoryMatch) {
            console.log('Categoría encontrada por query:', queryCategoryMatch);
            return queryCategoryMatch.id;
        }
    }

    const slug = path.split('/').filter(Boolean).pop();
    if (!slug) {
        return null;
    }

    const normalizedSlug = normalizeSlug(slug);
    const category = categories.find(cat => normalizeSlug(cat.name) === normalizedSlug);
    if (category) {
        console.log('Categoría encontrada por slug:', category);
        return category.id;
    }

    const aliasMap = {
        deportivos: ['deportivos', 'deportivas', 'niños', 'ninos'],
        formales: ['formales', 'formal'],
        hombre: ['hombre', 'caballero'],
        mujer: ['mujer', 'damas'],
        accesorios: ['accesorios']
    };

    const matchedAlias = Object.entries(aliasMap).find(([, aliases]) => aliases.includes(normalizedSlug));
    if (matchedAlias) {
        const backendSlug = matchedAlias[0];
        const fallbackCategory = categories.find(cat => aliasesMatchBackend(cat.name, backendSlug));
        if (fallbackCategory) {
            console.log('Categoría encontrada por alias:', fallbackCategory);
            return fallbackCategory.id;
        }
    }

    console.warn('No se encontró categoría para slug:', slug, 'en:', categories);
    return null;
}

function normalizeSlug(value) {
    return (value || '')
        .toString()
        .trim()
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[^a-z0-9]+/g, '');
}

function aliasesMatchBackend(categoryName, backendSlug) {
    const normalized = normalizeSlug(categoryName);
    if (backendSlug === 'ninos') {
        return normalized.includes('deport') || normalized.includes('nino');
    }
    if (backendSlug === 'formales') {
        return normalized.includes('formal');
    }
    return normalized === backendSlug || normalized.includes(backendSlug);
}

/**
 * Carga productos por ID de categoría desde el backend
 */
async function loadProductsByCategory(categoryId) {
    try {
        console.log('Cargando productos para categoría ID:', categoryId);
        
        const url = `${BACKEND_API}/shoes/by-category-id/${categoryId}`;
        console.log('URL de API:', url);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const products = await response.json();
        console.log('Productos obtenidos:', products);
        
        renderProducts(products);
        
    } catch (error) {
        console.error('Error cargando productos:', error.message);
        showErrorMessage('Error al cargar productos. Intenta de nuevo más tarde.');
    }
}

/**
 * Renderiza los productos en el HTML
 */
function renderProducts(products) {
    const productsGrid = document.querySelector('.products-grid');
    
    if (!productsGrid) {
        console.error('No se encontró el elemento .products-grid');
        return;
    }
    
    // Limpiar grid actual
    productsGrid.innerHTML = '';
    
    if (!products || products.length === 0) {
        productsGrid.innerHTML = '<p style="text-align: center; padding: 40px; grid-column: 1/-1;">No hay productos en esta categoría.</p>';
        return;
    }
    
    const fragment = document.createDocumentFragment();

    products.forEach(product => {
        const card = createProductCard(product);
        if (card) {
            fragment.appendChild(card);
        }
    });

    productsGrid.appendChild(fragment);
    
    console.log('Productos renderizados:', products.length);
}

/**
 * Crea una tarjeta de producto
 */
function createProductCard(product) {
    const card = document.createElement('div');
    card.className = 'product-card';
    
    // Calcular si hay descuento
    const discount = product.discount || 0;
    const badgeHtml = discount > 0 
        ? `<span class="badge">-${discount}%</span>`
        : product.new 
            ? '<span class="badge new">Nuevo</span>'
            : '';
    
    // Generar estrellas de rating
    const rating = product.rating || 4.5;
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 > 0;
    
    let starsHtml = '';
    for (let i = 0; i < fullStars; i++) {
        starsHtml += '<i class="fas fa-star"></i>';
    }
    if (hasHalfStar) {
        starsHtml += '<i class="fas fa-star-half-alt"></i>';
    }
    for (let i = fullStars + (hasHalfStar ? 1 : 0); i < 5; i++) {
        starsHtml += '<i class="far fa-star"></i>';
    }
    
    const imageUrl = product.image || 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=280&h=280&fit=crop';
    const priceValue = Number(product.price || 0);

    const imageWrapper = document.createElement('div');
    imageWrapper.className = 'product-image';

    const image = document.createElement('img');
    image.src = imageUrl;
    image.alt = product.name || 'Producto';
    image.loading = 'lazy';
    imageWrapper.appendChild(image);

    if (badgeHtml) {
        const badge = document.createElement('span');
        badge.className = discount > 0 ? 'badge' : 'badge new';
        badge.textContent = discount > 0 ? `-${discount}%` : 'Nuevo';
        imageWrapper.appendChild(badge);
    }

    const title = document.createElement('h4');
    title.textContent = product.name || 'Producto';

    const price = document.createElement('p');
    price.className = 'price';
    price.textContent = `S/. ${priceValue.toFixed(2)}`;

    const ratingWrapper = document.createElement('div');
    ratingWrapper.className = 'rating';
    ratingWrapper.innerHTML = starsHtml;

    const button = document.createElement('button');
    button.className = 'btn btn-outline';
    button.textContent = 'Agregar al Carrito';
    button.addEventListener('click', () => addToCart(product));

    const detailButton = document.createElement('button');
    detailButton.className = 'btn btn-secondary';
    detailButton.textContent = 'Ver detalle';
    detailButton.addEventListener('click', () => {
        window.location.href = `/producto?id=${encodeURIComponent(product.id)}`;
    });

    image.addEventListener('click', () => {
        window.location.href = `/producto?id=${encodeURIComponent(product.id)}`;
    });
    image.style.cursor = 'pointer';

    const actions = document.createElement('div');
    actions.style.display = 'flex';
    actions.style.gap = '8px';
    actions.style.flexWrap = 'wrap';
    actions.appendChild(detailButton);
    actions.appendChild(button);

    card.appendChild(imageWrapper);
    card.appendChild(title);
    card.appendChild(price);
    card.appendChild(ratingWrapper);
    card.appendChild(actions);
    
    return card;
}

/**
 * Agrega un producto al carrito
 */
function addToCart(product) {
    const payload = {
        productId: product.id,
        name: product.name || 'Producto',
        price: Number(product.price || 0),
        imageUrl: product.image || product.imageUrl || '',
        quantity: 1,
        size: '37',
        color: 'Negro'
    };

    if (typeof window.footstyleAddItemToCart === 'function') {
        window.footstyleAddItemToCart(payload);
        if (typeof window.footstyleRefreshCartBadge === 'function') {
            window.footstyleRefreshCartBadge();
        }
        if (typeof window.showNotification === 'function') {
            window.showNotification(`${payload.name} agregado al carrito`, 'success');
        }
        return;
    }

    const cartBadge = document.querySelector('.action-item.cart .badge');
    if (cartBadge) {
        cartBadge.textContent = String((parseInt(cartBadge.textContent || '0', 10) || 0) + 1);
    }

    alert(`${payload.name} agregado al carrito`);
}

/**
 * Muestra un mensaje de error
 */
function showErrorMessage(message) {
    const productsGrid = document.querySelector('.products-grid');
    if (productsGrid) {
        productsGrid.innerHTML = `<p style="text-align: center; padding: 40px; grid-column: 1/-1; color: #e74c3c;">${message}</p>`;
    }
}
