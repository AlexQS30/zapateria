(function () {
    const API_BASE = 'http://localhost:8081/api';

    function getToken() {
        return (typeof window.getAuthToken === 'function' ? window.getAuthToken() : localStorage.getItem('auth_token')) || null;
    }

    function showMessage(message, type = 'info') {
        if (typeof window.showNotification === 'function') {
            window.showNotification(message, type);
            return;
        }
        alert(message);
    }

    function formatStatus(status) {
        const value = (status || '').toUpperCase();
        return ({
            REGISTRADO: 'Registrado',
            EN_CAMINO: 'En camino',
            RECHAZADO: 'Rechazado',
            ENTREGADO: 'Entregado'
        })[value] || status || 'Registrado';
    }

    function statusColor(status) {
        const value = (status || '').toUpperCase();
        if (value === 'ENTREGADO') return '#2e7d32';
        if (value === 'EN_CAMINO') return '#1565c0';
        if (value === 'RECHAZADO') return '#c62828';
        return '#8d6e63';
    }

    async function loadOrders() {
        const list = document.getElementById('orders-list');
        const message = document.getElementById('orders-message');
        if (!list || !message) return;

        const token = getToken();
        if (!token) {
            message.style.display = 'block';
            message.textContent = 'Inicia sesión para ver tus pedidos.';
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/purchases/me`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const orders = await response.json();
            if (!Array.isArray(orders) || orders.length === 0) {
                message.style.display = 'block';
                message.textContent = 'Todavía no tienes pedidos registrados.';
                list.innerHTML = '';
                return;
            }

            message.style.display = 'none';
            list.innerHTML = orders.map(order => `
                <section class="order-card" style="background:#fff; border:1px solid #eee; border-radius:18px; padding:20px; box-shadow:0 12px 35px rgba(0,0,0,0.05);">
                    <div style="display:flex; justify-content:space-between; gap:16px; flex-wrap:wrap; margin-bottom:16px;">
                        <div>
                            <h3 style="margin:0 0 6px;">Pedido ${order.id}</h3>
                            <div style="color:#666;">Fecha: ${new Date(order.createdAt).toLocaleString('es-PE')}</div>
                        </div>
                        <div style="text-align:right;">
                            <div style="display:inline-flex; padding:8px 14px; border-radius:999px; background:${statusColor(order.status)}; color:#fff; font-weight:700;">${formatStatus(order.status)}</div>
                            <div style="margin-top:8px; color:#666;">Pago: ${order.paymentMethod || 'EFECTIVO'}</div>
                        </div>
                    </div>
                    <div style="display:grid; gap:12px; margin-bottom:16px;">
                        ${(order.items || []).map(item => `
                            <div style="display:grid; grid-template-columns:72px 1fr auto; gap:14px; align-items:center; padding:12px; border:1px solid #f0f0f0; border-radius:14px;">
                                <img src="${item.product?.image || item.product?.imageUrl || ''}" alt="${item.product?.name || 'Producto'}" style="width:72px; height:72px; object-fit:cover; border-radius:12px;">
                                <div>
                                    <strong>${item.product?.name || 'Producto'}</strong>
                                    <div style="color:#666; margin-top:4px;">Talla ${item.size || '-'} · Color ${item.color || '-' } · Cantidad ${item.quantity}</div>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px; align-items:flex-end;">
                                    <strong>S/. ${(Number(item.unitPrice || item.product?.price || 0) * Number(item.quantity || 1)).toFixed(2)}</strong>
                                    <button type="button" class="btn btn-secondary" data-review-product="${item.product?.id || ''}" data-review-name="${item.product?.name || 'Producto'}">Reseñar</button>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    <div style="display:flex; justify-content:space-between; gap:12px; flex-wrap:wrap; border-top:1px solid #f0f0f0; padding-top:14px;">
                        <span style="color:#666;">Dirección: ${order.shippingAddress || 'No registrada'}</span>
                        <strong>Total: S/. ${Number(order.total || 0).toFixed(2)}</strong>
                    </div>
                </section>
            `).join('');

            list.querySelectorAll('[data-review-product]').forEach(button => {
                button.addEventListener('click', async () => {
                    const productId = button.dataset.reviewProduct;
                    const productName = button.dataset.reviewName;
                    const rating = prompt(`Califica ${productName} del 1 al 5`);
                    if (!rating) return;

                    const comment = prompt('Escribe tu comentario (opcional)') || '';

                    try {
                        const reviewResponse = await fetch(`${API_BASE}/products/${encodeURIComponent(productId)}/reviews?rating=${encodeURIComponent(rating)}&comment=${encodeURIComponent(comment)}`, {
                            method: 'POST',
                            headers: { Authorization: `Bearer ${token}` }
                        });

                        if (!reviewResponse.ok) {
                            throw new Error(`HTTP ${reviewResponse.status}`);
                        }

                        showMessage('Reseña enviada correctamente', 'success');
                    } catch (error) {
                        showMessage('No se pudo enviar la reseña', 'error');
                    }
                });
            });
        } catch (error) {
            message.style.display = 'block';
            message.textContent = 'No se pudieron cargar tus pedidos.';
        }
    }

    document.addEventListener('DOMContentLoaded', loadOrders);
})();