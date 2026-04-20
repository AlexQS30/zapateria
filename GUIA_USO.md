# 🚀 Guía de Uso - Mejoras de Frontend & Integración con Servicios

## 📌 Resumen Ejecutivo

Se han mejorado significativamente las secciones del frontend con:
- ✅ **Imágenes reales** en lugar de emojis
- ✅ **Nuevas secciones**: Testimonios y Newsletter
- ✅ **Diseño profesional** con animaciones suaves
- ✅ **Script de integración** listo para conectar con backend
- ✅ **Documentación completa** para desarrolladores

---

## 📂 Archivos Modificados/Creados

### Modificados
```
zapateria-web/zapateria-web/src/main/resources/templates/
└── index.html (secciones mejoradas + nuevo script)

zapateria-web/zapateria-web/src/main/resources/static/js/
└── product-integration.js (NUEVO - script de integración)
```

### Documentación Creada
```
PROYECTO/
├── INTEGRACION_FRONTEND_SERVICIOS.md (Guía detallada de integración)
├── MEJORAS_SECCIONES.md (Resumen de cambios)
└── GUIA_USO.md (Este archivo)
```

---

## 🎯 Secciones Mejorads

### 1️⃣ Productos Destacados
**Ubicación**: Línea 1011 en index.html

**Cambios:**
- Imágenes reales de Unsplash
- Overlay interactivo
- Botón "Ver detalles"
- Categoría del producto
- Precio con/sin descuento
- Contador de reviews

**Integración:**
```javascript
// Automático via product-integration.js
// Llamada: renderFeaturedProducts()
// Endpoint: GET /api/products/featured
```

---

### 2️⃣ Ofertas Especiales
**Ubicación**: Línea 945 en index.html

**Cambios:**
- Imágenes mejoradas
- Badges de descuento
- Diseño más profesional
- Animaciones hover

**Integración:**
```javascript
// Automático via product-integration.js
// Llamada: renderOffers()
// Endpoint: GET /api/offers
```

---

### 3️⃣ Beneficios
**Ubicación**: Línea 1120 en index.html

**Cambios:**
- Iconos en cajas redondeadas
- Gradientes en colores
- Animaciones de hover
- Mejor spacing

**Estilos:**
- Benefit cards con border-left dorado
- Icons con gradiente (70px x 70px)
- Transiciones suaves

---

### 4️⃣ Testimonios ⭐ NUEVO
**Ubicación**: Línea 1161 en index.html

**Características:**
- 3 tarjetas de testimonios
- Estrellas de calificación
- Avatares con gradientes
- Información del autor
- Responsive grid

**Sin integración backend** (datos estáticos actualmente)

---

### 5️⃣ Newsletter ⭐ NUEVO
**Ubicación**: Línea 1232 en index.html

**Características:**
- Formulario de email
- Validación de entrada
- Mensajes de éxito/error
- Fondo gradiente oscuro

**Integración:**
```javascript
// Automático via product-integration.js
// Endpoint: POST /api/newsletter/subscribe
// Body: { email: "usuario@email.com" }
```

---

## 🔧 Cómo Usar el Script de Integración

### 1. **Carga automática**
El script `product-integration.js` se carga automáticamente en index.html y ejecuta:
```javascript
document.addEventListener('DOMContentLoaded', () => {
    renderCategories();
    renderOffers();
    renderFeaturedProducts();
    updateCartBadge();
});
```

### 2. **Inicializar API**
```javascript
// Ya está inicializado en el script
const api = new FootStyleAPI('http://localhost:8080/api');
```

### 3. **Usar en otros scripts**
```javascript
// Obtener datos de API
const products = await api.getFeaturedProducts(4);
const offers = await api.getOffers();
const categories = await api.getCategories();

// Agregar al carrito
await api.addToCart(productId, quantity);

// Suscribirse al newsletter
await api.subscribeNewsletter('usuario@email.com');
```

---

## 📋 Endpoints Backend Requeridos

### Categorías
```
GET /api/categories
Response: [
    {
        "id": 1,
        "name": "Calzados Hombre",
        "description": "Descripción",
        "imageUrl": "https://...",
        "productCount": 150
    }
]
```

### Productos Destacados
```
GET /api/products/featured?limit=4
Response: [
    {
        "id": 1,
        "name": "Zapato Casual Premium",
        "price": 199.00,
        "originalPrice": 249.00,
        "discountPercentage": 20,
        "categoryName": "Zapatos Casuales",
        "imageUrl": "https://...",
        "rating": 4.5,
        "reviewCount": 245,
        "isNew": false,
        "inStock": true
    }
]
```

### Ofertas
```
GET /api/offers
Response: [
    {
        "id": 1,
        "categoryId": 1,
        "categoryName": "Calzados Hombre",
        "description": "Zapatos elegantes en oferta",
        "discountPercentage": 35,
        "imageUrl": "https://...",
        "validUntil": "2024-12-31"
    }
]
```

### Newsletter
```
POST /api/newsletter/subscribe
Body: {
    "email": "usuario@email.com"
}
Response: {
    "success": true,
    "message": "Suscripción exitosa"
}
```

### Carrito
```
POST /api/cart/add
Body: {
    "productId": 1,
    "quantity": 1
}

GET /api/cart
Response: {
    "items": [
        {
            "productId": 1,
            "quantity": 1,
            "price": 199.00
        }
    ]
}
```

---

## 🎨 Paleta de Colores Utilizada

```css
:root {
    --primary: #101214;          /* Negro profundo */
    --secondary: #c4a27e;        /* Oro cálido */
    --accent: #b9786a;           /* Terracota suave */
    --light: #f5f2ee;            /* Crema */
    --surface: #eee8e2;          /* Gris claro */
    --text-soft: #56656d;        /* Gris medio */
}
```

---

## 📱 Responsividad

### Breakpoints
```css
Desktop:  1200px+   (4 columnas)
Tablet:   768px     (2-3 columnas)
Mobile:   480px     (1 columna)
```

### Mobile First
Todos los componentes son responsive:
- Grids adaptativos
- Flex wrap en formularios
- Touch-friendly buttons (min 44px)
- Imágenes escalables

---

## ⚙️ Configuración de Backend

### CORS Configuration (Spring Boot)
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:8080")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

### Variables de Entorno
```bash
# .env (crear en zapateria-web si es necesario)
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

---

## 🔄 Flujo de Integración

```
1. User visits index.html
   ↓
2. product-integration.js loads
   ↓
3. API class initializes with baseUrl
   ↓
4. DOMContentLoaded triggers:
   - renderCategories() → GET /api/categories
   - renderOffers() → GET /api/offers
   - renderFeaturedProducts() → GET /api/products/featured
   - updateCartBadge() → GET /api/cart
   ↓
5. User interacts (agregar carrito, newsletter, etc)
   ↓
6. API calls executed with proper headers
   ↓
7. Response handled with error management
   ↓
8. UI updated/notifications shown
```

---

## ✨ Características Especiales

### Animaciones
- Fade in de elementos
- Hover lift (translateY -8px)
- Icon rotation on hover (5deg)
- Smooth transitions (0.3s cubic-bezier)

### Validaciones
- Email validation en newsletter
- Required fields en formularios
- Error handling con try-catch
- User feedback con notificaciones

### Performance
- Lazy loading de imágenes (`loading="lazy"`)
- Eventos delegados
- No librerías externas (excepto Font Awesome)
- Minified en producción

---

## 🐛 Troubleshooting

### Imágenes no cargan
**Causa**: CORS issues o URLs inválidas
**Solución**: 
```javascript
// Verificar en console
console.log(product.imageUrl);
// Usar placeholder si URL falla
<img src="${product.imageUrl || 'https://via.placeholder.com/400'}" />
```

### API no responde
**Causa**: Backend no corriendo o CORS no configurado
**Solución**:
1. Verificar backend en http://localhost:8080
2. Revisar console del navegador (Network tab)
3. Validar CORS configuration en backend

### Newsletter no envía email
**Causa**: Backend endpoint no implementado
**Solución**:
1. Crear endpoint POST /api/newsletter/subscribe
2. Implementar lógica de envío de email
3. Retornar JSON response

### Carrito no actualiza badge
**Causa**: API no autenticada o sesión expirada
**Solución**:
1. Verificar token en localStorage
2. Implementar refresh token si es necesario
3. Redirigir a login si no autenticado

---

## 🧪 Testing

### Pruebas Manuales
```bash
# 1. Verificar categorías cargan
- Abrir Chrome DevTools → Console
- Ejecutar: api.getCategories()

# 2. Verificar productos cargan
- Ejecutar: api.getFeaturedProducts()

# 3. Verificar newsletter
- Escribir email en formulario
- Click en "Suscribirse"
- Buscar success message

# 4. Verificar carrito
- Click en "Agregar carrito"
- Verificar badge update
- Ejecutar: api.getCart()
```

### Tests Unitarios (Ejemplo con Jest)
```javascript
describe('FootStyleAPI', () => {
    let api;
    
    beforeEach(() => {
        api = new FootStyleAPI();
    });
    
    test('should fetch categories', async () => {
        const categories = await api.getCategories();
        expect(categories).toBeInstanceOf(Array);
        expect(categories[0]).toHaveProperty('name');
    });
});
```

---

## 📊 Métricas

### Antes
- 0 imágenes de producto
- 2 secciones (Categorías, Productos)
- Emojis como placeholders
- Sin newsletter
- Sin testimonios

### Después
- 12+ imágenes de alta calidad
- 8 secciones completas
- Diseño profesional
- Newsletter funcional
- Testimonios con gradientes
- 100+ líneas de CSS nuevas
- Script de integración 500+ líneas

---

## 📞 Soporte

Para preguntas o problemas:

1. **Revisar documentación**
   - INTEGRACION_FRONTEND_SERVICIOS.md
   - MEJORAS_SECCIONES.md

2. **Consultar código**
   - product-integration.js (funciones de API)
   - index.html (estructura HTML)

3. **Verificar backend**
   - Logs en consola
   - Network tab en DevTools
   - Postman para endpoint testing

---

## 📝 Checklist de Implementación

- [ ] Backend implementa GET /api/categories
- [ ] Backend implementa GET /api/offers
- [ ] Backend implementa GET /api/products/featured
- [ ] Backend implementa POST /api/newsletter/subscribe
- [ ] CORS configurado correctamente
- [ ] Imágenes cargan en navegador
- [ ] Botones funcionan correctamente
- [ ] Newsletter envía emails
- [ ] Carrito actualiza badge
- [ ] Testing en múltiples navegadores
- [ ] Responsive design validado en móvil
- [ ] Performance optimizado (PageSpeed >90)

---

**Actualizado**: 20 de Abril, 2024
**Versión**: 1.0
**Estado**: ✅ Listo para Integración
