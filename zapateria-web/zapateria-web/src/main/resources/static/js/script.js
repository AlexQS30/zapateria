/* ========================================
   FUNCIONALIDADES JAVASCRIPT
   ======================================== */

// Modo estático por defecto: no consume backend en index.
// Para activar integración, define: window.FOOTSTYLE_STATIC_MODE = false
const isStaticMode = window.FOOTSTYLE_STATIC_MODE !== undefined
    ? window.FOOTSTYLE_STATIC_MODE
    : true;

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar todas las funcionalidades
    initNavigation();
    initProductCards();
    initTabs();
    initGallery();
    initCart();
    initSearch();
    initMobileMenu();
    // Solo cargar desde backend cuando integración está activa
    if (!isStaticMode) {
        fetchCategories();
        fetchProducts();
    }
    // If on product detail page, load detail
    const params = new URLSearchParams(window.location.search);
    const pid = params.get('id') || params.get('productoId');
    if (!isStaticMode && document.querySelector('.product-detail') && pid) {
        fetchProductDetail(pid);
    }
});

/* ========================================
   NAVEGACIÓN
   ======================================== */

function initNavigation() {
    const menuLinks = document.querySelectorAll('.menu a');
    
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Marcar enlace activo
            menuLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // Smooth scroll para el navegador
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });
}

/* ========================================
   INTEGRACIÓN CON BACKEND
   ======================================== */

const API_BASE = 'http://localhost:8081/api';

function fetchCategories() {
    showLoader('#categories-loader');
    hideEmpty('#categories-empty');
    fetch(`${API_BASE}/categories`, { mode: 'cors' })
        .then(res => res.json())
        .then(list => {
            hideLoader('#categories-loader');
            if (!list || list.length === 0) {
                showEmpty('#categories-empty', 'No hay categorías para mostrar');
                return;
            }
            renderCategories(list);
        })
        .catch(err => { hideLoader('#categories-loader'); console.error('Error cargando categorías:', err); showEmpty('#categories-empty','Error cargando categorías'); });
}

function renderCategories(categories) {
    const grid = document.querySelector('.categories-grid');
    if (!grid) return;
    grid.innerHTML = '';
    // Update main menu with categories (preserve 'OFERTAS')
    try {
        const menu = document.querySelector('.menu');
        if (menu) {
            menu.innerHTML = '';
            categories.forEach(cat => {
                const li = document.createElement('li');
                const a = document.createElement('a');
                a.href = `/categoria?categoria=${encodeURIComponent(cat.name)}`;
                a.textContent = (cat.name || '').toString().toUpperCase();
                li.appendChild(a);
                menu.appendChild(li);
            });
            const liOffers = document.createElement('li');
            liOffers.innerHTML = '<a href="#ofertas">OFERTAS</a>';
            menu.appendChild(liOffers);
        }
    } catch (e) { console.error('Error actualizando menú:', e); }
    categories.forEach(cat => {
        const card = document.createElement('div');
        card.className = 'category-card';
        const imgUrl = `https://images.unsplash.com/random/400x400?${encodeURIComponent('shoe,' + (cat.name || 'shoe'))}`;
        card.innerHTML = `
            <div class="category-image">
                <img src="${imgUrl}" alt="${escapeHtml(cat.name)}" style="width:100%; height:100%; object-fit:cover;" loading="lazy">
            </div>
            <div class="category-info">
                <h3>${escapeHtml(cat.name)}</h3>
                <p>${escapeHtml(cat.description || '')}</p>
                <a href="/categoria?categoria=${encodeURIComponent(cat.name)}" class="btn btn-secondary">Ver Colección</a>
            </div>
        `;
        grid.appendChild(card);
    });
}

function fetchProducts() {
    showLoader('#products-loader');
    hideEmpty('#products-empty');
    const params = new URLSearchParams(window.location.search);
    const cat = params.get('categoria');
    const url = cat ? `${API_BASE}/shoes/category/${encodeURIComponent(cat)}` : `${API_BASE}/shoes`;
    fetch(url, { mode: 'cors' })
        .then(res => {
            if (!res.ok) throw new Error('Error fetching products');
            return res.json();
        })
        .then(list => {
            hideLoader('#products-loader');
            if (cat) {
                const titleEl = document.querySelector('.category-header h1');
                if (titleEl) titleEl.textContent = `Calzados - ${capitalize(cat)}`;
            }
            if (!list || list.length === 0) {
                showEmpty('#products-empty', 'No hay productos para mostrar');
                return;
            }
            renderProducts(list);
        })
        .catch(err => { hideLoader('#products-loader'); console.error('Error cargando productos:', err); showEmpty('#products-empty','Error cargando productos'); });
}

function renderProducts(products) {
    const grid = document.querySelector('.products-grid');
    if (!grid) return;
    grid.innerHTML = '';
    products.forEach(p => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = `
            <div class="product-image">
                <img src="${escapeHtml(p.imageUrl || 'https://source.unsplash.com/random/280x280?shoe')}" alt="${escapeHtml(p.name)}" loading="lazy">
                ${p.onOffer ? '<span class="badge-discount">- ' + Math.round((p.discount||0) * 100) + '%</span>' : ''}
            </div>
            <div class="product-info">
                <div class="product-name">${escapeHtml(p.name)}</div>
                <div class="rating">${renderStars(p.rating||4)}</div>
                <div class="product-price">S/. ${p.price.toFixed(2)}</div>
                <button class="btn btn-outline add-to-cart" data-id="${escapeHtml(p.id)}">Agregar al Carrito</button>
            </div>
        `;
        grid.appendChild(card);
    });
    // Re-bind add-to-cart buttons
    document.querySelectorAll('.add-to-cart').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.dataset.id;
            addProductToLocalCart(id);
            showNotification('Producto agregado al carrito', 'success');
            updateCartCount();
        });
    });
}

function fetchProductDetail(id) {
    showLoader('#product-detail-loader');
    hideEmpty('#related-empty');
    fetch(`${API_BASE}/shoes/${encodeURIComponent(id)}`, { mode: 'cors' })
        .then(res => {
            if (!res.ok) throw new Error('Producto no encontrado');
            return res.json();
        })
        .then(p => { hideLoader('#product-detail-loader'); renderProductDetail(p); })
        .catch(err => { hideLoader('#product-detail-loader'); console.error('Error cargando detalle:', err); showEmpty('#related-empty','Error cargando detalle'); });
}

function renderProductDetail(p) {
    // main image
    const mainImg = document.querySelector('.product-gallery .main-image img');
    if (mainImg) mainImg.src = p.image || (p.imageUrl || 'https://source.unsplash.com/random/500x500?shoe');
    // badge (discount)
    const badge = document.querySelector('.product-gallery .main-image .badge') || document.querySelector('.product-gallery .main-image span.badge');
    if (badge) {
        if (p.discount && p.discount > 0) {
            badge.textContent = `-${p.discount}%`;
            badge.style.display = '';
        } else {
            badge.style.display = 'none';
        }
    }
    // title
    const title = document.querySelector('.product-info h1');
    if (title) title.textContent = p.name;
    // prices
    const current = document.querySelector('.current-price');
    const original = document.querySelector('.original-price');
    const discountEl = document.querySelector('.discount');
    const price = Number(p.price || 0);
    if (current) current.textContent = `S/. ${price.toFixed(2)}`;
    if (p.discount && p.discount > 0) {
        const orig = price / (1 - (p.discount/100));
        if (original) original.textContent = `S/. ${orig.toFixed(2)}`;
        if (discountEl) discountEl.textContent = `-${p.discount}% DESCUENTO`;
    } else {
        if (original) original.style.display = 'none';
        if (discountEl) discountEl.style.display = 'none';
    }
    // description
    const desc = document.querySelector('.product-description');
    if (desc) desc.textContent = p.description || p.longDescription || '';
    // rating
    const stars = document.querySelector('.product-rating .stars');
    if (stars) {
        stars.innerHTML = '';
        const r = Math.round((p.rating||4));
        for (let i=0;i<5;i++) stars.innerHTML += `<i class="${i<r ? 'fas' : 'far'} fa-star"></i>`;
    }
    // related products: fetch all and show same category
    if (p.category && p.category.name) {
        const catName = p.category.name;
        fetch(`${API_BASE}/shoes/category/${encodeURIComponent(catName)}`, { mode: 'cors' })
            .then(r => {
                if (!r.ok) throw new Error('Error fetching related');
                return r.json();
            })
            .then(list => {
                const related = list.filter(x => x.id !== p.id).slice(0,4);
                const grid = document.querySelector('.related-products .products-grid');
                if (grid) {
                    grid.innerHTML = '';
                    related.forEach(x => {
                        const card = document.createElement('div');
                        card.className = 'product-card';
                        card.innerHTML = `
                            <div class="product-image"><img src="${escapeHtml(x.image||'https://source.unsplash.com/random/250x250?shoe')}" alt="${escapeHtml(x.name)}"></div>
                            <h4 class="product-name">${escapeHtml(x.name)}</h4>
                            <p class="price">S/. ${Number(x.price).toFixed(2)}</p>
                            <div class="rating">${renderStars(x.rating||4)}</div>
                            <button class="btn btn-outline" onclick="window.location='/producto?id=${encodeURIComponent(x.id)}'">Ver</button>
                        `;
                        grid.appendChild(card);
                    });
                    if (related.length === 0) showEmpty('#related-empty', 'No hay productos relacionados');
                }
            });
    }
}

function showLoader(selector) {
    try { const el = document.querySelector(selector); if (el) el.style.display = ''; } catch(e){}
}
function hideLoader(selector) { try { const el = document.querySelector(selector); if (el) el.style.display = 'none'; } catch(e){}
}
function showEmpty(selector, message) { try { const el = document.querySelector(selector); if (el) { el.textContent = message; el.style.display = ''; } } catch(e){}
}
function hideEmpty(selector) { try { const el = document.querySelector(selector); if (el) el.style.display = 'none'; } catch(e){}
}

function addProductToLocalCart(id) {
    let cart = JSON.parse(localStorage.getItem('footstyleCart')) || [];
    cart.push({ id: id, qty: 1 });
    localStorage.setItem('footstyleCart', JSON.stringify(cart));
}

function renderStars(n) {
    let out = '';
    for (let i=0;i<5;i++) out += `<i class="fas fa-star"></i>`;
    return out;
}

function escapeHtml(s) {
    if (s === null || s === undefined) return '';
    return String(s).replace(/[&<>"']/g, function(c){
        return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"}[c];
    });
}

function capitalize(s){ if(!s) return s; return s.charAt(0).toUpperCase()+s.slice(1); }

/* ======================================== */

/* ========================================
   TARJETAS DE PRODUCTO
   ======================================== */

function initProductCards() {
    const productCards = document.querySelectorAll('.product-card');
    
    productCards.forEach(card => {
        const addBtn = card.querySelector('.btn');
        
        if (addBtn) {
            addBtn.addEventListener('click', function(e) {
                e.preventDefault();
                showNotification('Producto agregado al carrito', 'success');
                updateCartCount();
            });
        }
    });
}

/* ========================================
   GALERÍA DE IMÁGENES
   ======================================== */

function initGallery() {
    const thumbnails = document.querySelectorAll('.thumbnail-gallery img');
    const mainImage = document.querySelector('.main-image img');
    
    thumbnails.forEach(thumb => {
        thumb.addEventListener('click', function() {
            // Remover clase active de todas las miniaturas
            thumbnails.forEach(t => t.classList.remove('active'));
            
            // Agregar clase active a la miniatura clickeada
            this.classList.add('active');
            
            // Cambiar imagen principal
            if (mainImage) {
                mainImage.src = this.src.replace('80x80', '500x500');
            }
        });
    });
}

/* ========================================
   TABS
   ======================================== */

function initTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            
            // Remover clase active de todos los botones y panes
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabPanes.forEach(pane => pane.classList.remove('active'));
            
            // Agregar clase active al botón clickeado
            this.classList.add('active');
            
            // Mostrar el pane correspondiente
            const activePane = document.getElementById(tabId);
            if (activePane) {
                activePane.classList.add('active');
            }
        });
    });
}

/* ========================================
   CARRITO DE COMPRAS
   ======================================== */

function initCart() {
    const cartIcon = document.querySelector('.action-item.cart');
    const cartBadge = document.querySelector('.action-item.cart .badge');
    
    // Recuperar carrito del localStorage
    let cart = JSON.parse(localStorage.getItem('footstyleCart')) || [];
    
    // Actualizar contador
    if (cartBadge) {
        cartBadge.textContent = cart.length;
    }
    
    // Click en el icono del carrito
    if (cartIcon) {
        cartIcon.addEventListener('click', function() {
            showCartPreview();
        });
    }
    
    // Selector de cantidad
    const quantityButtons = document.querySelectorAll('.quantity-input button');
    const quantityInput = document.querySelector('.quantity-input input');
    
    quantityButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const current = parseInt(quantityInput.value);
            
            if (this.textContent === '+') {
                quantityInput.value = current + 1;
            } else if (this.textContent === '-' && current > 1) {
                quantityInput.value = current - 1;
            }
        });
    });
}

function updateCartCount() {
    let cart = JSON.parse(localStorage.getItem('footstyleCart')) || [];
    cart.push({ id: Date.now(), name: 'Producto', price: 199 });
    localStorage.setItem('footstyleCart', JSON.stringify(cart));
    
    const badge = document.querySelector('.action-item.cart .badge');
    if (badge) {
        badge.textContent = cart.length;
    }
}

function showCartPreview() {
    const cart = JSON.parse(localStorage.getItem('footstyleCart')) || [];
    
    if (cart.length === 0) {
        showNotification('Tu carrito está vacío', 'info');
        return;
    }
    
    let message = `Productos en carrito: ${cart.length}\n\n`;
    cart.forEach((item, index) => {
        message += `${index + 1}. ${item.name} - S/. ${item.price}\n`;
    });
    
    showNotification(message, 'info');
}

/* ========================================
   BÚSQUEDA
   ======================================== */

function initSearch() {
    const searchInput = document.querySelector('.navbar-search input');
    const searchButton = document.querySelector('.navbar-search button');
    
    if (searchButton) {
        searchButton.addEventListener('click', function() {
            if (searchInput && searchInput.value.trim()) {
                console.log('Buscando:', searchInput.value);
                showNotification(`Buscando: "${searchInput.value}"`, 'info');
                // Aquí puedes agregar la lógica de búsqueda real
            }
        });
    }
    
    // Buscar al presionar Enter
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchButton.click();
            }
        });
    }
}

/* ========================================
   MENÚ MÓVIL
   ======================================== */

function initMobileMenu() {
    // Agregar funcionalidad de menú responsivo si es necesario
    const menuBar = document.querySelector('.menu-bar');
    const navbar = document.querySelector('.navbar');
    
    if (window.innerWidth <= 768) {
        // Lógica para pantallas móviles
        if (menuBar) {
            menuBar.style.display = 'none';
        }
    }
}

/* ========================================
   NOTIFICACIONES
   ======================================== */

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    // Estilos en línea para la notificación
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background-color: ${getNotificationColor(type)};
        color: white;
        border-radius: 4px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        z-index: 1000;
        animation: slideIn 0.3s ease-in-out;
        max-width: 400px;
    `;
    
    document.body.appendChild(notification);
    
    // Remover notificación después de 3 segundos
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-in-out';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

function getNotificationColor(type) {
    const colors = {
        'success': '#28a745',
        'error': '#dc3545',
        'info': '#17a2b8',
        'warning': '#ffc107'
    };
    return colors[type] || colors['info'];
}

// Agregar animaciones CSS
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

/* ========================================
   FORMULARIO DE CONTACTO
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const contactForm = document.querySelector('.contact-form');
    
    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const nombre = document.getElementById('nombre').value;
            const email = document.getElementById('email').value;
            const asunto = document.getElementById('asunto').value;
            const mensaje = document.getElementById('mensaje').value;
            
            if (nombre && email && asunto && mensaje) {
                showNotification('Mensaje enviado exitosamente', 'success');
                contactForm.reset();
                
                // Aquí puedes agregar lógica para enviar el formulario al servidor
            } else {
                showNotification('Por favor completa todos los campos requeridos', 'warning');
            }
        });
    }
});

/* ========================================
   BOTONES DE TALLA Y COLOR
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const sizeBtns = document.querySelectorAll('.size-btn');
    const colorBtns = document.querySelectorAll('.color-btn');
    
    // Seleccionar talla
    sizeBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            sizeBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });
    
    // Seleccionar color
    colorBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            colorBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });
});

/* ========================================
   FILTROS DE CATEGORÍA
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const filterBtn = document.querySelector('.sidebar .btn-primary');
    
    if (filterBtn) {
        filterBtn.addEventListener('click', function() {
            const filters = {
                price: document.querySelector('.filter-group input[type="range"]').value,
                sizes: Array.from(document.querySelectorAll('.filter-group input[type="checkbox"]:checked'))
                    .map(cb => cb.parentElement.textContent.trim()),
                colors: Array.from(document.querySelectorAll('.filter-group input[type="checkbox"]:checked'))
                    .map(cb => cb.parentElement.textContent.trim())
            };
            
            console.log('Filtros aplicados:', filters);
            showNotification('Filtros aplicados correctamente', 'success');
        });
    }
});

/* ========================================
   VISTA PREVIA DE PRODUCTO
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const productCards = document.querySelectorAll('.product-card');
    
    productCards.forEach(card => {
        card.addEventListener('click', function(e) {
            if (!e.target.classList.contains('btn')) {
                const productName = card.querySelector('h4').textContent;
                console.log('Ver detalle de:', productName);
            }
        });
    });
});

/* ========================================
   FAVORITOS
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const heartIcon = document.querySelector('.action-item:not(.cart)');
    
    if (heartIcon) {
        heartIcon.addEventListener('click', function() {
            this.classList.toggle('active');
            
            if (this.classList.contains('active')) {
                showNotification('Agregado a favoritos', 'success');
            } else {
                showNotification('Removido de favoritos', 'info');
            }
        });
    }
});

/* ========================================
   PAGINACIÓN
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const paginationBtns = document.querySelectorAll('.pagination button');
    
    paginationBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            if (!this.disabled && !this.classList.contains('active')) {
                paginationBtns.forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                
                // Simular cambio de página
                window.scrollTo({ top: 0, behavior: 'smooth' });
                console.log('Página:', this.textContent);
            }
        });
    });
});

/* ========================================
   OPCIONES DE VISTA
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const viewToggles = document.querySelectorAll('.view-toggle');
    const productsGrid = document.querySelector('.products-grid');
    
    viewToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            viewToggles.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            if (this.querySelector('i').classList.contains('fa-list')) {
                if (productsGrid) {
                    productsGrid.style.gridTemplateColumns = 'repeat(auto-fit, minmax(100%, 1fr))';
                }
            } else {
                if (productsGrid) {
                    productsGrid.style.gridTemplateColumns = 'repeat(auto-fit, minmax(220px, 1fr))';
                }
            }
        });
    });
});

/* ========================================
   ORDENAR PRODUCTOS
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const sortSelect = document.querySelector('.sort-options select');
    
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const selectedOption = this.value;
            console.log('Ordenar por:', selectedOption);
            
            if (selectedOption !== '') {
                showNotification(`Ordenando por: ${selectedOption}`, 'info');
            }
        });
    }
});

/* ========================================
   AGREGAR A FAVORITOS DESDE DETALLE
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const favoriteBtn = document.querySelector('.product-actions .btn-secondary');
    
    if (favoriteBtn) {
        favoriteBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            const icon = this.querySelector('i');
            if (icon.classList.contains('far')) {
                icon.classList.remove('far');
                icon.classList.add('fas');
                showNotification('Agregado a favoritos', 'success');
            } else {
                icon.classList.remove('fas');
                icon.classList.add('far');
                showNotification('Removido de favoritos', 'info');
            }
        });
    }
});

/* ========================================
   AGREGAR AL CARRITO DESDE DETALLE
   ======================================== */

document.addEventListener('DOMContentLoaded', function() {
    const addToCartBtn = document.querySelector('.product-actions .btn-primary');
    
    if (addToCartBtn) {
        addToCartBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            const quantity = document.querySelector('.quantity-input input').value;
            const size = document.querySelector('.size-btn.active').textContent;
            const productName = document.querySelector('.product-info h1').textContent;
            
            showNotification(`${quantity} x ${productName} (Talla ${size}) agregado al carrito`, 'success');
            updateCartCount();
        });
    }
});

/* ========================================
   VALIDACIÓN Y UTILIDADES
   ======================================== */

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('es-PE', {
        style: 'currency',
        currency: 'PEN'
    }).format(amount);
}

/* ========================================
   CARGAR MÁS PRODUCTOS
   ======================================== */

let currentPage = 1;

function loadMoreProducts() {
    currentPage++;
    console.log('Cargando página:', currentPage);
    showNotification(`Cargando página ${currentPage}...`, 'info');
}
