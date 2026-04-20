# Configuración de Login y Autenticación - Zapatería Backend

## 📋 Resumen

Se ha creado un sistema completo de autenticación y login para la aplicación zapateria-back que incluye:

- **Entidad User**: Modelo de datos para usuarios
- **Repositorio UserRepository**: Acceso a datos de usuarios
- **Servicio LoginService**: Lógica de autenticación y registro
- **Controlador LoginController**: Endpoints REST
- **Configuración de Seguridad**: SecurityConfig
- **DTOs**: Objetos de transferencia de datos para solicitudes/respuestas

## 🗄️ Base de Datos

### Configuración PostgreSQL

Las propiedades de conexión ya están configuradas en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/zapateria
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

**⚠️ IMPORTANTE**: Cambia las credenciales según tu configuración local de PostgreSQL.

### Crear la Base de Datos y Tabla

1. **Crear la base de datos en PostgreSQL**:
```sql
CREATE DATABASE zapateria;
```

2. **Ejecutar el script SQL** proporcionado en `database_setup.sql`:
```bash
psql -U postgres -d zapateria -f database_setup.sql
```

O copiar el contenido del archivo `database_setup.sql` y ejecutarlo en pgAdmin o en la consola de PostgreSQL.

## 📦 Dependencias Agregadas

Se han agregado al `pom.xml`:
- `spring-boot-starter-data-jpa`: ORM con Hibernate
- `spring-boot-starter-validation`: Validación de datos
- `postgresql`: Driver de PostgreSQL

## 🔐 Endpoints REST

### 1. Login de Usuario
**POST** `/api/auth/login`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response Exitoso (200)**:
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "message": "Login exitoso",
  "success": true
}
```

**Response Error (401)**:
```json
{
  "id": null,
  "email": null,
  "firstName": null,
  "lastName": null,
  "message": "Usuario no encontrado",
  "success": false
}
```

### 2. Registro de Usuario
**POST** `/api/auth/register`

**Request Body**:
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "Pedro",
  "lastName": "García",
  "phoneNumber": "987654321",
  "address": "Calle Principal 123",
  "city": "Lima",
  "postalCode": "01001"
}
```

**Response Exitoso (201)**:
```json
{
  "id": 2,
  "email": "newuser@example.com",
  "firstName": "Pedro",
  "lastName": "García",
  "message": "Registro exitoso",
  "success": true
}
```

### 3. Verificar Disponibilidad de Email
**GET** `/api/auth/check-email/{email}`

**Response**:
```json
true  // si el email ya existe
false // si el email está disponible
```

## 🔒 Seguridad

### Encriptación de Contraseña
Las contraseñas se encriptan usando **BCrypt**. El servicio `LoginService` automáticamente:
- Encripta la contraseña al registrar un usuario
- Verifica la contraseña al hacer login

### CORS
Se ha habilitado CORS para permitir solicitudes desde el frontend (zapateria-web):
```java
@CrossOrigin(origins = "*", maxAge = 3600)
```

**Nota**: En producción, reemplaza `"*"` con los orígenes específicos permitidos.

## 📝 Modelo de Datos - Tabla `users`

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
```

### Campos:
- **id**: Identificador único (clave primaria)
- **email**: Email único del usuario (requerido)
- **password**: Contraseña encriptada (requerido)
- **first_name**: Primer nombre (requerido)
- **last_name**: Apellido (requerido)
- **phone_number**: Teléfono (opcional)
- **address**: Dirección (opcional)
- **city**: Ciudad (opcional)
- **postal_code**: Código postal (opcional)
- **created_at**: Fecha de creación automática
- **updated_at**: Fecha de última actualización automática
- **is_active**: Estado del usuario (default: true)

## 🚀 Compilación e Instalación

1. **Actualizar Maven** (descargar dependencias):
```bash
cd zapateria-back/zapateria-back
mvn clean install
```

2. **Ejecutar la aplicación**:
```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 🧪 Pruebas con cURL

### Prueba de Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Prueba de Registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "password123",
    "firstName": "Carlos",
    "lastName": "López",
    "phoneNumber": "987654321",
    "address": "Av. Principal 456",
    "city": "Lima",
    "postalCode": "01002"
  }'
```

### Verificar Email
```bash
curl http://localhost:8080/api/auth/check-email/test@example.com
```

## 📂 Estructura de Archivos Creados

```
zapateria-back/zapateria-back/
├── src/main/java/com/back/zapateria/
│   ├── model/
│   │   └── User.java                 (Entidad de usuario)
│   ├── repository/
│   │   └── UserRepository.java       (Repositorio de acceso a datos)
│   ├── service/
│   │   └── LoginService.java         (Lógica de negocio)
│   ├── controller/
│   │   └── LoginController.java      (Endpoints REST)
│   ├── dto/
│   │   ├── LoginRequest.java         (DTO para login)
│   │   ├── LoginResponse.java        (DTO respuesta de login)
│   │   └── RegisterRequest.java      (DTO para registro)
│   └── config/
│       └── SecurityConfig.java       (Configuración de seguridad)
├── src/main/resources/
│   └── application.properties        (Propiedades configuradas)
└── database_setup.sql                (Script SQL)
```

## ⚙️ Validaciones

El sistema implementa validaciones usando **Jakarta Validation**:

- **Email**: Debe ser válido y único
- **Contraseña**: Mínimo 6 caracteres
- **Nombre/Apellido**: Campos requeridos
- **Campos opcionales**: Dirección, ciudad, teléfono, código postal

## 🔄 Próximos Pasos

1. **Integración en zapateria-web**:
   - Crear página de login que consuma el endpoint `/api/auth/login`
   - Crear página de registro que consuma el endpoint `/api/auth/register`
   - Almacenar datos de sesión en localStorage o sessionStorage

2. **Mejoras de seguridad**:
   - Implementar JWT (JSON Web Tokens) para sesiones
   - Agregar refresh tokens
   - Implementar 2FA (Autenticación de dos factores)

3. **Funcionalidades adicionales**:
   - Recuperación de contraseña olvidada
   - Cambio de contraseña
   - Perfil de usuario
   - Historial de órdenes

## 📞 Soporte

Para más información sobre Spring Security, JPA y PostgreSQL, consulta:
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
