// Modal Login Functions
console.log('Modal.js cargado');

function openLoginModal() {
    console.log('openLoginModal llamado');
    const modal = document.getElementById('loginModal');
    console.log('Modal element:', modal);
    if (modal) {
        modal.classList.add('show');
        console.log('Clase show agregada');
        document.body.style.overflow = 'hidden';
    } else {
        console.log('Modal no encontrado!');
    }
}

function closeLoginModal() {
    const modal = document.getElementById('loginModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = 'auto';
    }
}

/**
 * Maneja el login desde el modal con manejo robusto de errores
 */
async function handleModalLogin(event) {
    event.preventDefault();
    console.log('handleModalLogin llamado');
    
    const emailInput = document.getElementById('modalEmail');
    const passwordInput = document.getElementById('modalPassword');
    const loginBtn = document.getElementById('modalLoginBtn');
    
    if (!emailInput || !passwordInput) {
        console.error('No se encontraron los inputs del modal');
        alert('Error: No se encontraron los campos del formulario');
        return;
    }
    
    const email = emailInput.value.trim();
    const password = passwordInput.value;
    
    if (!email || !password) {
        alert('Por favor completa todos los campos');
        return;
    }
    
    // Cambiar texto del botón
    const originalText = loginBtn.textContent;
    loginBtn.textContent = 'Autenticando...';
    loginBtn.disabled = true;
    
    try {
        // Verificar que la función exista
        if (typeof window.loginUser !== 'function') {
            throw new Error('Función de login no disponible. Por favor recarga la página.');
        }
        
        // Llamar al servicio de login desde auth-integration.js
        const result = await window.loginUser(email, password);
        
        if (result.success) {
            alert(`¡Bienvenido ${result.user.firstName}!`);
            closeLoginModal();
            // Limpiar formulario
            emailInput.value = '';
            passwordInput.value = '';
            // Redirigir según el rol
            if (typeof window.redirectByRole === 'function') {
                window.redirectByRole();
            } else {
                window.location.href = '/';
            }
        } else {
            // Mostrar mensaje de error específico
            const errorMsg = result.message || 'Error desconocido en el login';
            console.error('Error en login:', errorMsg);
            alert(`Error: ${errorMsg}`);
        }
    } catch (error) {
        console.error('Error en login modal:', error.message, error.stack);
        
        // Mostrar mensaje de error amigable
        let userMessage = 'Error de conexión con el servidor';
        
        if (error.message.includes('not a function')) {
            userMessage = 'Error: Las funciones de autenticación no están disponibles. Por favor recarga la página.';
        } else if (error.message.includes('Failed to fetch')) {
            userMessage = 'Error: No se puede conectar con el servidor. Verifica que esté ejecutándose en http://localhost:8081';
        } else if (error.message) {
            userMessage = error.message;
        }
        
        alert(userMessage);
    } finally {
        // Restaurar estado del botón
        loginBtn.textContent = originalText;
        loginBtn.disabled = false;
    }
}

// Close modal when clicking outside
window.addEventListener('click', function(event) {
    const modal = document.getElementById('loginModal');
    if (modal && event.target === modal) {
        closeLoginModal();
    }
});

// Close modal with Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeLoginModal();
    }
});

// Add to cart functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Loaded - configurando botones');
    document.querySelectorAll('.btn-outline').forEach(btn => {
        btn.addEventListener('click', function() {
            const cart = document.querySelector('.action-item:last-child .badge');
            if (cart) {
                cart.textContent = parseInt(cart.textContent) + 1;
            }
        });
    });
    
    // Configurar el formulario del modal de login si existe
    const loginForm = document.getElementById('modalLoginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleModalLogin);
        console.log('Formulario modal de login configurado');
    }
});
