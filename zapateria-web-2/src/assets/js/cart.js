(function () {
    const CART_STORAGE_KEY = 'footstyleCart';
    const API_BASE = 'http://localhost:8081/api';
    const CART_COOKIE_KEY = 'footstyleCart';
    const SIZE_OPTIONS = ['35', '36', '37', '38', '39', '40', '41', '42'];
    const COLOR_OPTIONS = [
        { label: 'Negro', value: 'Negro', hex: '#111111' },
        { label: 'Blanco', value: 'Blanco', hex: '#ffffff', border: true },
        { label: 'Azul', value: 'Azul', hex: '#1a3a5c' },
        { label: 'Marrón', value: 'Marrón', hex: '#8b5a3c' }
    ];

    function getToken() {
        return (typeof window.getAuthToken === 'function' ? window.getAuthToken() : localStorage.getItem('auth_token')) || null;
    }

    function getCart() {
        try {
            const rawStorageCart = localStorage.getItem(CART_STORAGE_KEY);
            const rawCookieCart = getCookie(CART_COOKIE_KEY);
            const rawCart = JSON.parse(rawStorageCart || rawCookieCart || '[]') || [];
            let changed = false;
            const normalizedCart = rawCart.map(item => {
                const normalized = normalizeItem(item);
                const legacyProductId = item.productId ?? item.id;
                const legacyQuantity = item.quantity ?? item.qty ?? 1;
                const legacyPrice = item.price ?? item.unitPrice ?? 0;
                const legacyImage = item.imageUrl || item.image || '';
                const legacySize = item.size || '37';
                const legacyColor = item.color || 'Negro';

                if (
                    normalized.productId !== String(legacyProductId ?? '') ||
                    normalized.quantity !== Math.max(1, Number(legacyQuantity) || 1) ||
                    normalized.price !== Number(legacyPrice || 0) ||
                    normalized.imageUrl !== legacyImage ||
                    normalized.size !== legacySize ||
                    normalized.color !== legacyColor ||
                    normalized.name !== (item.name || 'Producto')
                ) {
                    changed = true;
                }

                return normalized;
            }).filter(item => item.productId && item.productId !== 'undefined');

            if (changed) {
                persistCart(normalizedCart);
            }

            return normalizedCart;
        } catch (error) {
            return [];
        }
    }

    function setCookie(name, value) {
        document.cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}; path=/; max-age=${60 * 60 * 24 * 30}`;
    }

    function getCookie(name) {
        const encodedName = `${encodeURIComponent(name)}=`;
        return document.cookie
            .split('; ')
            .find(entry => entry.startsWith(encodedName))
            ?.slice(encodedName.length)
            ? decodeURIComponent(document.cookie.split('; ').find(entry => entry.startsWith(encodedName)).slice(encodedName.length))
            : '';
    }

    function persistCart(cart) {
        const serialized = JSON.stringify(cart);
        localStorage.setItem(CART_STORAGE_KEY, serialized);
        setCookie(CART_COOKIE_KEY, serialized);
    }

    function saveCart(cart) {
        persistCart(cart);
        updateBadge();
    }

    function updateBadge() {
        const cart = getCart();
        const totalQty = cart.reduce((sum, item) => sum + (Number(item.quantity) || 1), 0);
        document.querySelectorAll('.action-item.cart .badge').forEach(badge => {
            badge.textContent = String(totalQty);
        });
    }

    function normalizeItem(item) {
        const productId = item.productId ?? item.id;
        const quantity = item.quantity ?? item.qty ?? 1;
        return {
            productId: String(productId),
            name: item.name || 'Producto',
            price: Number(item.price ?? item.unitPrice ?? 0),
            imageUrl: item.imageUrl || item.image || '',
            quantity: Math.max(1, Number(quantity) || 1),
            size: item.size || '37',
            color: item.color || 'Negro'
        };
    }

    function upsertCartItem(item) {
        const normalized = normalizeItem(item);
        const cart = getCart();
        const index = cart.findIndex(existing =>
            existing.productId === normalized.productId &&
            existing.size === normalized.size &&
            existing.color === normalized.color
        );

        if (index >= 0) {
            cart[index].quantity += normalized.quantity;
        } else {
            cart.push(normalized);
        }

        saveCart(cart);
    }

    async function fetchProduct(productId) {
        const response = await fetch(`${API_BASE}/shoes/${encodeURIComponent(productId)}`);
        if (!response.ok) {
            throw new Error('No se pudo cargar el producto');
        }
        return response.json();
    }

    async function fetchProductVariants(productId) {
        try {
            const response = await fetch(`${API_BASE}/shoes/${encodeURIComponent(productId)}/variants`);
            if (!response.ok) {
                return [];
            }
            const variants = await response.json();
            return Array.isArray(variants) ? variants : [];
        } catch (error) {
            return [];
        }
    }

    function sameText(a, b) {
        return String(a || '').trim().toLowerCase() === String(b || '').trim().toLowerCase();
    }

    function findMatchingVariant(variants, size, color) {
        return variants.find(variant => sameText(variant.size, size) && sameText(variant.color, color)) || null;
    }

    function pickFallbackVariant(variants) {
        return variants.find(variant => Number(variant.stock || 0) > 0) || null;
    }

    async function hydrateCart(cart) {
        let changed = false;
        const hydrated = await Promise.all(cart.map(async item => {
            if (item.name && item.name !== 'Producto' && Number(item.price) > 0 && item.imageUrl) {
                const variants = await fetchProductVariants(item.productId);
                if (!variants.length) {
                    return item;
                }

                const availableVariants = variants.filter(variant => Number(variant.stock || 0) > 0);
                if (!availableVariants.length) {
                    changed = true;
                    return null;
                }

                const current = findMatchingVariant(variants, item.size, item.color);
                if (current && Number(current.stock || 0) > 0) {
                    const normalizedQty = Math.min(item.quantity, Number(current.stock || 1));
                    if (normalizedQty !== item.quantity) {
                        changed = true;
                        return normalizeItem({ ...item, quantity: normalizedQty });
                    }
                    return item;
                }

                const fallback = pickFallbackVariant(availableVariants);
                if (!fallback) {
                    changed = true;
                    return null;
                }

                changed = true;
                return normalizeItem({
                    ...item,
                    size: fallback.size,
                    color: fallback.color,
                    quantity: Math.min(item.quantity, Number(fallback.stock || 1))
                });
            }

            try {
                const product = await fetchProduct(item.productId);
                const variants = await fetchProductVariants(item.productId);
                const normalized = normalizeItem({
                    ...item,
                    productId: product.id ?? item.productId,
                    name: product.name || item.name,
                    price: product.price ?? item.price,
                    imageUrl: product.imageUrl || product.image || item.imageUrl
                });

                if (variants.length) {
                    const availableVariants = variants.filter(variant => Number(variant.stock || 0) > 0);
                    if (!availableVariants.length) {
                        changed = true;
                        return null;
                    }

                    const matching = findMatchingVariant(variants, normalized.size, normalized.color);
                    if (!matching || Number(matching.stock || 0) <= 0) {
                        const fallback = pickFallbackVariant(availableVariants);
                        if (fallback) {
                            changed = true;
                            normalized.size = fallback.size;
                            normalized.color = fallback.color;
                            normalized.quantity = Math.min(normalized.quantity, Number(fallback.stock || 1));
                        }
                    } else {
                        const adjustedQty = Math.min(normalized.quantity, Number(matching.stock || 1));
                        if (adjustedQty !== normalized.quantity) {
                            changed = true;
                            normalized.quantity = adjustedQty;
                        }
                    }
                }

                return normalized;
            } catch (error) {
                return item;
            }
        }));

        const filteredHydrated = hydrated.filter(Boolean);

        if (changed) {
            persistCart(filteredHydrated);
        }

        return filteredHydrated;
    }

    function ensureModalStyles() {
        if (document.getElementById('footstyle-cart-style')) return;
        const style = document.createElement('style');
        style.id = 'footstyle-cart-style';
        style.textContent = `
            .footstyle-overlay {
                position: fixed; inset: 0; background: rgba(16,18,20,0.68); z-index: 12000;
                display: flex; align-items: center; justify-content: center; padding: 16px;
            }
            .footstyle-panel {
                width: min(1120px, 100%); max-height: 92vh; overflow: auto; background: #fff; border-radius: 22px;
                box-shadow: 0 30px 80px rgba(0,0,0,0.35); padding: 24px;
            }
            .footstyle-grid { display: grid; grid-template-columns: 1.25fr 0.85fr; gap: 20px; }
            .footstyle-item { display: grid; grid-template-columns: 88px 1fr auto; gap: 14px; align-items: center; padding: 14px 0; border-bottom: 1px solid #eee; }
            .footstyle-item img { width: 88px; height: 88px; object-fit: cover; border-radius: 14px; }
            .footstyle-row { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
            .footstyle-pill { border: 1px solid #ddd; border-radius: 999px; padding: 8px 12px; background: #fafafa; }
            .footstyle-actions { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; }
            .footstyle-close { background: transparent; border: 0; font-size: 28px; cursor: pointer; line-height: 1; }
            .footstyle-empty-state {
                border: 1px dashed #d7cdc2; border-radius: 18px; padding: 28px; background: linear-gradient(180deg, #fffaf5 0%, #fff 100%);
                display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 280px; text-align: center;
            }
            .footstyle-empty-icon {
                width: 84px; height: 84px; border-radius: 50%; display: grid; place-items: center; margin-bottom: 14px;
                background: #101214; color: #fff; font-size: 30px;
            }
            .footstyle-form label { display:block; font-weight: 600; margin-bottom: 6px; }
            .footstyle-form input, .footstyle-form select, .footstyle-form textarea {
                width: 100%; padding: 12px 14px; border: 1px solid #d9d9d9; border-radius: 12px; font: inherit; box-sizing: border-box;
            }
            .footstyle-form textarea { min-height: 92px; resize: vertical; }
            .footstyle-size-grid, .footstyle-color-grid { display:flex; gap:8px; flex-wrap:wrap; }
            .footstyle-chip { border:1px solid #ddd; background:#fff; border-radius:999px; padding:8px 10px; cursor:pointer; }
            .footstyle-chip.active { border-color:#101214; background:#101214; color:#fff; }
            @media (max-width: 860px) { .footstyle-grid { grid-template-columns: 1fr; } .footstyle-item { grid-template-columns: 72px 1fr; } .footstyle-item .footstyle-actions { grid-column: 1 / -1; justify-content: flex-start; } }
        `;
        document.head.appendChild(style);
    }

    function closeModal() {
        const existing = document.getElementById('footstyle-cart-modal');
        if (existing) existing.remove();
    }

    function renderSizeOptions(active = '37', options = SIZE_OPTIONS) {
        return options.map(size => `<button type="button" class="footstyle-chip ${size === active ? 'active' : ''}" data-size="${size}">${size}</button>`).join('');
    }

    function renderColorOptions(active = 'Negro', options = COLOR_OPTIONS) {
        return options.map(color => `<button type="button" class="footstyle-chip ${color.value === active ? 'active' : ''}" data-color="${color.value}" style="${color.border ? 'border-color:#ddd;' : ''}background:${color.hex};color:${color.hex === '#ffffff' ? '#111' : '#fff'};">${color.label}</button>`).join('');
    }

    function getSelectedVariant(modal) {
        const size = modal.querySelector('[data-size].active')?.dataset.size || '37';
        const color = modal.querySelector('[data-color].active')?.dataset.color || 'Negro';
        const quantity = Math.max(1, Number(modal.querySelector('[name="quantity"]')?.value || 1));
        return { size, color, quantity };
    }

    async function openConfigurator(productId) {
        try {
            const [product, variants] = await Promise.all([
                fetchProduct(productId),
                fetchProductVariants(productId)
            ]);

            const hasVariants = variants.length > 0;
            const availableVariants = variants.filter(variant => Number(variant.stock || 0) > 0);
            const firstVariant = pickFallbackVariant(availableVariants.length ? availableVariants : variants);
            const sizeOptions = hasVariants
                ? [...new Set((availableVariants.length ? availableVariants : variants).map(variant => String(variant.size)))]
                : SIZE_OPTIONS;
            const colorMap = Object.fromEntries(COLOR_OPTIONS.map(color => [color.value.toLowerCase(), color]));
            const colorOptions = hasVariants
                ? [...new Set((availableVariants.length ? availableVariants : variants).map(variant => String(variant.color)))]
                    .map(color => {
                        const preset = colorMap[color.toLowerCase()];
                        return preset || { label: color, value: color, hex: '#777777' };
                    })
                : COLOR_OPTIONS;

            ensureModalStyles();
            closeModal();

            const overlay = document.createElement('div');
            overlay.className = 'footstyle-overlay';
            overlay.id = 'footstyle-cart-modal';
            overlay.innerHTML = `
                <div class="footstyle-panel">
                    <div class="footstyle-row" style="justify-content:space-between; margin-bottom: 16px;">
                        <div>
                            <h2 style="margin:0 0 4px;">Configurar carrito</h2>
                            <div style="color:#666;">Selecciona talla, color y cantidad</div>
                        </div>
                        <button class="footstyle-close" type="button" aria-label="Cerrar">&times;</button>
                    </div>
                    <div class="footstyle-grid">
                        <div>
                            <div class="footstyle-item" style="grid-template-columns: 120px 1fr; align-items:start;">
                                <img src="${product.imageUrl || product.image || ''}" alt="${product.name}">
                                <div>
                                    <h3 style="margin:0 0 8px;">${product.name}</h3>
                                    <div style="color:#666; margin-bottom:10px;">S/. ${Number(product.price || 0).toFixed(2)}</div>
                                    <p style="margin:0; color:#555;">${product.description || 'Producto disponible para compra'}</p>
                                </div>
                            </div>
                            <div style="margin-top:20px;">
                                <label>Talla</label>
                                <div class="footstyle-size-grid">${renderSizeOptions(firstVariant?.size || '37', sizeOptions)}</div>
                            </div>
                            <div style="margin-top:20px;">
                                <label>Color</label>
                                <div class="footstyle-color-grid">${renderColorOptions(firstVariant?.color || 'Negro', colorOptions)}</div>
                            </div>
                            <div style="margin-top:20px; max-width:180px;">
                                <label>Cantidad</label>
                                <input type="number" name="quantity" min="1" value="1" max="${Math.max(1, Number(firstVariant?.stock || 99))}">
                                ${hasVariants ? `<div style="margin-top:6px; font-size:12px; color:#666;" data-stock-info>Stock disponible: ${Math.max(0, Number(firstVariant?.stock || 0))}</div>` : ''}
                            </div>
                        </div>
                        <div>
                            <div style="background:#f7f5f1; border-radius:16px; padding:18px;">
                                <h3 style="margin-top:0;">Resumen</h3>
                                <p style="color:#666; margin-top:0;">Agrega el producto al carrito con tus preferencias y finaliza la compra autenticado.</p>
                                <div class="footstyle-actions">
                                    <button type="button" class="btn btn-secondary" data-close>Cancelar</button>
                                    <button type="button" class="btn btn-primary" data-add>Agregar al carrito</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;

            function updateStockBySelection() {
                if (!hasVariants) return null;
                const selected = getSelectedVariant(overlay);
                const variant = findMatchingVariant(variants, selected.size, selected.color);
                const stockInfo = overlay.querySelector('[data-stock-info]');
                const quantityInput = overlay.querySelector('[name="quantity"]');
                if (!variant) {
                    stockInfo && (stockInfo.textContent = 'Combinación no disponible');
                    if (quantityInput) {
                        quantityInput.max = '1';
                        quantityInput.value = '1';
                    }
                    return null;
                }
                const stock = Math.max(0, Number(variant.stock || 0));
                stockInfo && (stockInfo.textContent = `Stock disponible: ${stock}`);
                if (quantityInput) {
                    quantityInput.max = String(Math.max(1, stock));
                    quantityInput.value = String(Math.min(Math.max(1, Number(quantityInput.value || 1)), Math.max(1, stock)));
                }
                return variant;
            }

            overlay.querySelectorAll('[data-size]').forEach(btn => btn.addEventListener('click', () => {
                overlay.querySelectorAll('[data-size]').forEach(x => x.classList.remove('active'));
                btn.classList.add('active');
                updateStockBySelection();
            }));
            overlay.querySelectorAll('[data-color]').forEach(btn => btn.addEventListener('click', () => {
                overlay.querySelectorAll('[data-color]').forEach(x => x.classList.remove('active'));
                btn.classList.add('active');
                updateStockBySelection();
            }));
            overlay.querySelector('[data-add]').addEventListener('click', () => {
                const variant = getSelectedVariant(overlay);
                if (hasVariants) {
                    const selectedVariant = findMatchingVariant(variants, variant.size, variant.color);
                    if (!selectedVariant) {
                        showNotificationSafe('Esa combinación de talla y color no está disponible', 'warning');
                        return;
                    }
                    const availableStock = Math.max(0, Number(selectedVariant.stock || 0));
                    if (availableStock < variant.quantity) {
                        showNotificationSafe(`Solo hay ${availableStock} unidades para esa variante`, 'warning');
                        return;
                    }
                }
                addItemToCart({
                    productId: product.id,
                    name: product.name,
                    price: product.price,
                    imageUrl: product.imageUrl || product.image,
                    ...variant
                });
                closeModal();
                showNotificationSafe(`${product.name} agregado al carrito`, 'success');
            });
            updateStockBySelection();
            overlay.querySelector('[data-close]').addEventListener('click', closeModal);
            overlay.querySelector('.footstyle-close').addEventListener('click', closeModal);
            overlay.addEventListener('click', event => {
                if (event.target === overlay) closeModal();
            });

            document.body.appendChild(overlay);
        } catch (error) {
            showNotificationSafe('No se pudo abrir el configurador del producto', 'error');
        }
    }

    function showNotificationSafe(message, type) {
        if (typeof window.showNotification === 'function') {
            window.showNotification(message, type);
            return;
        }
        alert(message);
    }

    function addItemToCart(item) {
        upsertCartItem(item);
    }

    async function renderCartModal() {
        ensureModalStyles();
        closeModal();

        const cart = await hydrateCart(getCart());
        const total = cart.reduce((sum, item) => sum + (Number(item.price) || 0) * (Number(item.quantity) || 1), 0);
        const isEmpty = cart.length === 0;

        const overlay = document.createElement('div');
        overlay.className = 'footstyle-overlay';
        overlay.id = 'footstyle-cart-modal';
        overlay.innerHTML = `
            <div class="footstyle-panel">
                <div class="footstyle-row" style="justify-content:space-between; margin-bottom: 16px;">
                    <div>
                        <h2 style="margin:0 0 4px;">Tu carrito</h2>
                        <div style="color:#666;">Revisa tus productos antes de pagar</div>
                    </div>
                    <button class="footstyle-close" type="button" aria-label="Cerrar">&times;</button>
                </div>
                <div class="footstyle-grid">
                    <div>
                        ${isEmpty ? `
                            <div class="footstyle-empty-state">
                                <div class="footstyle-empty-icon"><i class="fas fa-shopping-bag"></i></div>
                                <h3 style="margin:0 0 8px;">Tu carrito está vacío</h3>
                                <p style="margin:0 0 18px; color:#666; max-width:320px;">Agrega productos, elige talla y color, y luego completa tu compra con tarjeta, efectivo o Yape.</p>
                                <button type="button" class="btn btn-secondary" data-close>Seguir comprando</button>
                            </div>
                        ` : cart.map((item, index) => `
                            <div class="footstyle-item" data-index="${index}">
                                <img src="${item.imageUrl || ''}" alt="${item.name}">
                                <div>
                                    <h4 style="margin:0 0 6px;">${item.name}</h4>
                                    <div class="footstyle-row">
                                        <span class="footstyle-pill">Talla ${item.size}</span>
                                        <span class="footstyle-pill">Color ${item.color}</span>
                                        <span class="footstyle-pill">S/. ${Number(item.price).toFixed(2)}</span>
                                    </div>
                                    <div style="margin-top:10px;" class="footstyle-row">
                                        <label style="margin:0;">Cantidad</label>
                                        <input type="number" min="1" value="${item.quantity}" data-qty style="width:90px;">
                                        <button type="button" class="btn btn-secondary" data-save-qty>Actualizar</button>
                                        <button type="button" class="btn btn-outline" data-remove>Eliminar</button>
                                    </div>
                                </div>
                                <div class="footstyle-actions" style="flex-direction:column; align-items:flex-end;">
                                    <strong>S/. ${(Number(item.price) * Number(item.quantity)).toFixed(2)}</strong>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    <div>
                        <div style="background:#f7f5f1; border-radius:16px; padding:18px; position:sticky; top:16px; ${isEmpty ? 'opacity:0.65;' : ''}">
                            <h3 style="margin-top:0;">Checkout</h3>
                            <div class="footstyle-form">
                                <label>Método de pago</label>
                                <select name="paymentMethod">
                                    <option value="TARJETA_CREDITO">Tarjeta de crédito</option>
                                    <option value="EFECTIVO">Efectivo</option>
                                    <option value="YAPE">Yape</option>
                                </select>
                                <div style="height:12px"></div>
                                <label>Dirección de envío</label>
                                <textarea name="shippingAddress" placeholder="Ingresa tu dirección"></textarea>
                                <div style="height:12px"></div>
                                <label>Teléfono de contacto</label>
                                <input type="text" name="contactPhone" placeholder="Ej. 999 999 999">
                                <div style="height:18px"></div>
                                <div class="footstyle-row" style="justify-content:space-between; align-items:center; margin-bottom: 12px;">
                                    <span>Total</span>
                                    <strong>S/. ${total.toFixed(2)}</strong>
                                </div>
                                <button type="button" class="btn btn-primary" data-checkout ${isEmpty ? 'disabled' : ''}>Pagar ahora</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        overlay.querySelector('.footstyle-close').addEventListener('click', closeModal);
        overlay.addEventListener('click', event => {
            if (event.target === overlay) closeModal();
        });

        overlay.querySelectorAll('[data-remove]').forEach(button => {
            button.addEventListener('click', () => {
                const row = button.closest('[data-index]');
                const index = Number(row.dataset.index);
                const updated = getCart();
                updated.splice(index, 1);
                saveCart(updated);
                renderCartModal();
            });
        });

        overlay.querySelectorAll('[data-save-qty]').forEach(button => {
            button.addEventListener('click', () => {
                const row = button.closest('[data-index]');
                const index = Number(row.dataset.index);
                const qtyInput = row.querySelector('[data-qty]');
                const updated = getCart();
                updated[index].quantity = Math.max(1, Number(qtyInput.value || 1));
                saveCart(updated);
                renderCartModal();
            });
        });

        const checkoutButton = overlay.querySelector('[data-checkout]');
        checkoutButton?.addEventListener('click', async () => {
            const token = getToken();
            const hasClientSession = (typeof window.isUserLoggedIn === 'function' && window.isUserLoggedIn())
                && (typeof window.getCurrentUser === 'function' && !!window.getCurrentUser());
            if (!token || !hasClientSession) {
                showNotificationSafe('Debes iniciar sesión para comprar', 'warning');
                window.location.href = '/login';
                return;
            }

            const paymentMethod = overlay.querySelector('[name="paymentMethod"]').value;
            const shippingAddress = overlay.querySelector('[name="shippingAddress"]').value.trim();
            const contactPhone = overlay.querySelector('[name="contactPhone"]').value.trim();

            if (!shippingAddress) {
                showNotificationSafe('Ingresa una dirección de envío', 'warning');
                return;
            }

            try {
                const checkoutCart = await hydrateCart(getCart());
                saveCart(checkoutCart);

                if (!checkoutCart.length) {
                    showNotificationSafe('Tu carrito no tiene variantes disponibles para pagar', 'warning');
                    renderCartModal();
                    return;
                }

                const response = await fetch(`${API_BASE}/purchases/checkout`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        items: checkoutCart.map(item => ({
                            productId: item.productId,
                            quantity: item.quantity,
                            size: item.size,
                            color: item.color
                        })),
                        paymentMethod,
                        shippingAddress,
                        contactPhone
                    })
                });

                if (!response.ok) {
                    const errorPayload = await response.json().catch(() => null);
                    const message = errorPayload?.message || errorPayload?.error || `HTTP ${response.status}`;
                    throw new Error(message);
                }

                saveCart([]);
                closeModal();
                showNotificationSafe('Compra registrada correctamente', 'success');
            } catch (error) {
                showNotificationSafe(error.message || 'No se pudo completar la compra', 'error');
            }
        });

        document.body.appendChild(overlay);
    }

    function bindCartTriggers() {
        document.addEventListener('click', event => {
            const cartIcon = event.target.closest('.action-item.cart');
            if (cartIcon) {
                event.preventDefault();
                renderCartModal();
                return;
            }

            const addButton = event.target.closest('.add-to-cart');
            if (addButton) {
                event.preventDefault();
                const productId = addButton.dataset.id;
                if (productId) {
                    openConfigurator(productId);
                }
                return;
            }

            const detailButton = event.target.closest('.product-actions .btn-primary');
            if (detailButton && document.querySelector('.product-detail')) {
                event.preventDefault();
                const productId = new URLSearchParams(window.location.search).get('id') || new URLSearchParams(window.location.search).get('productoId');
                const name = document.querySelector('.product-info h1')?.textContent || 'Producto';
                const priceText = document.querySelector('.current-price')?.textContent || 'S/. 0.00';
                const price = Number(priceText.replace(/[^0-9.,]/g, '').replace(',', '.')) || 0;
                const size = document.querySelector('.size-btn.active')?.textContent || '37';
                const color = document.querySelector('.color-btn.active')?.getAttribute('data-color') || 'Negro';
                const quantity = Number(document.querySelector('.quantity-input input')?.value || 1);
                const imageUrl = document.querySelector('.main-image img')?.src || '';

                addItemToCart({ productId, name, price, imageUrl, size, color, quantity });
                showNotificationSafe(`${name} agregado al carrito`, 'success');
            }
        });
    }

    window.footstyleAddToCart = function (productId) {
        if (productId) {
            openConfigurator(productId);
        }
    };

    window.footstyleRefreshCartBadge = updateBadge;
    window.footstyleOpenCart = renderCartModal;
    window.footstyleAddItemToCart = addItemToCart;

    document.addEventListener('DOMContentLoaded', () => {
        updateBadge();
        bindCartTriggers();
    });
})();