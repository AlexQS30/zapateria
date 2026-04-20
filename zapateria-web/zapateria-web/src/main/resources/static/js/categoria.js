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
    
    // Crear tarjeta para cada producto
    products.forEach(product => {
        const card = createProductCard(product);
        productsGrid.appendChild(card);
    });
    
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
    while ((fullStars + (hasHalfStar ? 1 : 0)) < 5) {
        starsHtml += '<i class="far fa-star"></i>';
    }
    
    // Usar la imagen del producto o una por defecto
    const imageUrl = product.image || 'https://source.unsplash.com/random/280x280?shoe';
    
    card.innerHTML = `
        <div class="product-image">
            <img src="${imageUrl}" alt="${product.name}" loading="lazy">
            ${badgeHtml}
        </div>
        <h4>${product.name}</h4>
        <p class="price">S/. ${product.price.toFixed(2)}</p>
        <div class="rating">
            ${starsHtml}
        </div>
        <button class="btn btn-outline" onclick="addToCart('${product.id}', '${product.name}', ${product.price})">
            Agregar al Carrito
        </button>
    `;
    
    return card;
}

/**
 * Agrega un producto al carrito
 */
function addToCart(productId, productName, productPrice) {
    console.log('Agregando al carrito:', productId, productName, productPrice);
    
    // Actualizar badge del carrito
    const cartBadge = document.querySelector('.action-item.cart .badge');
    if (cartBadge) {
        cartBadge.textContent = parseInt(cartBadge.textContent || 0) + 1;
    }
    
    // Mostrar notificación
    alert(`${productName} agregado al carrito`);
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
