# 🔐 INTEGRACIÓN API LOGIN - ZAPATERIA-WEB

## ✅ Lo que se ha hecho

Se ha integrado la API REST del backend (`zapateria-back`) en el frontend (`zapateria-web`), respetando tu arquitectura con Thymeleaf.

### Archivos Modificados:

1. **`login.html`** (Modificado)
   - ✅ Cambió de `POST` tradicional a llamada AJAX
   - ✅ Integra `auth-integration.js`
   - ✅ Mantiene tu diseño y estilos
   - ✅ Gestiona sesión con `sessionStorage`

2. **`register.html`** (Creado)
   - ✅ Nuevo formulario de registro
   - ✅ Mismo diseño que login.html
   - ✅ Validaciones en cliente
   - ✅ Integra con API de backend

3. **`HomeController.java`** (Modificado)
   - ✅ Agregada ruta `GET /register`
   - ✅ Actualizado `POST /login` con documentación

4. **`auth-integration.js`** (Creado en `static/js/`)
   - ✅ Librería para consumir API
   - ✅ Funciones: `loginUser()`, `registerUser()`, `checkEmailExists()`
   - ✅ Gestión de sesión

---

## 🚀 Cómo Funciona

### Flujo de Login:

```
Usuario llena formulario
    ↓
JavaScript captura submit
    ↓
Llamada AJAX a /api/auth/login (backend)
    ↓
Backend valida credenciales
    ↓
Respuesta JSON { success, user, message }
    ↓
JavaScript guarda en sessionStorage
    ↓
Redirige a /
```

### Archivos Relevantes:

```
zapateria-web/
├── src/main/resources/
│   ├── templates/
│   │   ├── login.html ✏️ (Integra API)
│   │   ├── register.html 🆕 (Nuevo)
│   │   └── ... (otros)
│   └── static/js/
│       ├── auth-integration.js 🆕 (Librería)
│       └── ... (otros)
└── src/main/java/com/web/zapateria/controller/
    └── HomeController.java ✏️ (Ruta /register)
```

---

## 📝 Funciones JavaScript Disponibles

### 1. `loginUser(email, password)`
Realiza login en el backend

```javascript
const result = await loginUser('user@example.com', 'password123');
if (result.success) {
    console.log('Bienvenido:', result.user.firstName);
}
```

### 2. `registerUser(userData)`
Registra nuevo usuario

```javascript
const userData = {
    email: 'new@example.com',
    password: 'password123',
    firstName: 'Juan',
    lastName: 'Pérez',
    phoneNumber: '987654321',
    address: 'Calle 123',
    city: 'Lima',
    postalCode: '01001'
};

const result = await registerUser(userData);
```

### 3. `checkEmailExists(email)`
Verifica si un email ya está registrado

```javascript
const exists = await checkEmailExists('user@example.com');
console.log(exists); // true o false
```

### 4. `isUserLoggedIn()`
Verifica si hay sesión activa

```javascript
if (isUserLoggedIn()) {
    const user = getCurrentUser();
    console.log('Usuario:', user.firstName);
}
```

### 5. `logout()`
Cierra sesión

```javascript
logout(); // Limpia sessionStorage y redirige
```

---

## 🔌 Integración con tus Vistas Existentes

### En cualquier template Thymeleaf, puedes usar:

```html
<!-- Cargar la librería -->
<script src="/js/auth-integration.js"></script>

<!-- Verificar si está autenticado -->
<script>
if (isUserLoggedIn()) {
    const user = getCurrentUser();
    document.getElementById('userName').textContent = user.firstName;
}
</script>
```

### Mostrar/Ocultar contenido según autenticación:

```html
<div id="loggedIn" style="display: none;">
    <p>Hola, <span id="userName"></span>!</p>
    <a href="#" onclick="logout()">Cerrar sesión</a>
</div>

<div id="notLoggedIn">
    <a href="/login">Iniciar sesión</a>
    <a href="/register">Registrarse</a>
</div>

<script>
if (isUserLoggedIn()) {
    const user = getCurrentUser();
    document.getElementById('userName').textContent = user.firstName;
    document.getElementById('loggedIn').style.display = 'block';
    document.getElementById('notLoggedIn').style.display = 'none';
} else {
    document.getElementById('loggedIn').style.display = 'none';
    document.getElementById('notLoggedIn').style.display = 'block';
}
</script>
```

---

## ✅ Requisitos

1. **Backend ejecutándose**: `http://localhost:8080`
2. **API disponible**: `/api/auth/*` en el backend
3. **Base de datos PostgreSQL**: Tabla `users` creada

---

## 🧪 Probar

### 1. Ir a la página de login
```
http://localhost:8080/login
```

### 2. Usar credenciales de prueba
```
Email: test@example.com
Contraseña: password123
```

### 3. O crear nueva cuenta
```
http://localhost:8080/register
```

---

## 🔒 Seguridad

- ✅ Contraseñas encriptadas con BCrypt en backend
- ✅ Validación de datos en cliente y servidor
- ✅ Sesión almacenada en `sessionStorage` (cliente)
- ✅ CORS configurado en backend
- ✅ Email único en base de datos

---

## 📌 Puntos Importantes

1. **sessionStorage**: Se limpia cuando se cierra el navegador
   - Más seguro que localStorage
   - Perfecto para sesiones de corta duración

2. **CORS**: Ya está configurado en backend
   - Permite llamadas desde `http://localhost:*`

3. **API URL**: Configurable en `auth-integration.js`
   ```javascript
   const API_BASE_URL = 'http://localhost:8080/api/auth';
   ```

4. **Errores**: Se muestran en alertas (puedes personalizar)
   - Modifica el script en login.html para mostrar en UI

---

## 🚀 Próximos Pasos

1. **Agregar info de usuario en header**: 
   - Mostrar nombre del usuario autenticado
   - Botón de logout

2. **Proteger rutas**:
   - Redirigir a login si no está autenticado
   - Verificar `isUserLoggedIn()` en cada página

3. **Mejorar UI de errores**:
   - En lugar de alertas, mostrar mensajes en la página
   - Validaciones más detalladas

4. **Persistencia de sesión**:
   - Considerar usar localStorage en lugar de sessionStorage
   - O implementar tokens JWT

---

## 📞 Soporte Rápido

**¿Por qué no funciona el login?**
- ✅ Verifica que el backend esté ejecutándose en `http://localhost:8080`
- ✅ Verifica la consola del navegador (F12) para ver errores
- ✅ Verifica que exista el usuario en la BD

**¿Cómo agrego autenticación a otras páginas?**
- Copia el código de verificación de `isUserLoggedIn()` en tu template

**¿Cómo guardo datos de sesión en el servidor?**
- Actualmente se guarda en `sessionStorage` del cliente
- Para persistir en servidor, considera implementar JWT tokens

---

## 📚 Archivos de Referencia

- **Backend API**: `zapateria-back/.../LoginController.java`
- **BD Schema**: `zapateria-back/database_setup.sql`
- **Librería JS**: `zapateria-web/static/js/auth-integration.js`
- **Página Login**: `zapateria-web/templates/login.html`
- **Página Registro**: `zapateria-web/templates/register.html`

---

**¡Integración completa y funcionando! ✅**
