/**
 * Script para cargar dinámicamente el menú de categorías
 */

const BACKEND_API = 'http://localhost:8081/api';
const FALLBACK_CATEGORIES = [
    { name: 'Hombre', route: 'hombre' },
    { name: 'Mujer', route: 'mujer' },
    { name: 'Deportivos', route: 'deportivos' },
    { name: 'Formales', route: 'formales' },
    { name: 'Accesorios', route: 'accesorios' }
];

document.addEventListener('DOMContentLoaded', function() {
    console.log('Menu.js cargado');
    loadMenuCategories();
});

/**
 * Carga las categorías para el menú desde el backend
 */
async function loadMenuCategories() {
    try {
        console.log('Cargando categorías del menú...');
        const response = await fetch(`${BACKEND_API}/categories`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const categories = await response.json();
        console.log('Categorías del menú cargadas:', categories);
        renderMenuCategories(Array.isArray(categories) && categories.length ? categories : FALLBACK_CATEGORIES);
        
    } catch (error) {
        console.error('Error cargando categorías del menú:', error.message);
        renderMenuCategories(FALLBACK_CATEGORIES);
    }
}

/**
 * Renderiza las categorías en el menú
 */
function renderMenuCategories(categories) {
    const menu = document.querySelector('#category-menu');
    if (!menu) {
        console.error('No se encontró elemento #category-menu');
        return;
    }
    
    console.log('Renderizando menú con categorías:', categories);
    const fragment = document.createDocumentFragment();

    categories.forEach(category => {
        const route = category.route || mapCategoryRoute(category.name);
        const li = document.createElement('li');
        const a = document.createElement('a');

        a.href = `/${route}`;
        a.textContent = (category.label || category.name || '').toUpperCase();

        li.appendChild(a);
        fragment.appendChild(li);
    });

    const ofertasLi = document.createElement('li');
    const ofertasLink = document.createElement('a');
    ofertasLink.href = '/#ofertas';
    ofertasLink.textContent = 'OFERTAS';
    ofertasLi.appendChild(ofertasLink);
    fragment.appendChild(ofertasLi);

    menu.replaceChildren(fragment);
    
    console.log('Menú de categorías renderizado correctamente');
}

function mapCategoryRoute(name) {
    const normalized = (name || '').toLowerCase();
    if (normalized.includes('hombre')) return 'hombre';
    if (normalized.includes('mujer')) return 'mujer';
    if (normalized.includes('deport')) return 'deportivos';
    if (normalized.includes('formal')) return 'formales';
    if (normalized.includes('acces')) return 'accesorios';
    return normalized || 'hombre';
}
