# Levantar proyecto Zapateria

Este proyecto tiene dos aplicaciones Spring Boot:

- Backend: `zapateria-back/zapateria-back`
- Frontend: `zapateria-web/zapateria-web`

## Requisitos

- Java 21
- Maven
- PostgreSQL corriendo localmente
- Base de datos `zapateria`
- Usuario de BD: `reibin`
- Password de BD: `reibin`

## 1. Levantar backend

Abre una terminal en la carpeta del backend y ejecuta:

```powershell
Set-Location "d:\Reibin\Trabajos\Iniciativas\zapateria\zapateria\zapateria-back\zapateria-back"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
mvn spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8081
```

## 2. Levantar frontend

Abre otra terminal en la carpeta del frontend y ejecuta:

```powershell
Set-Location "d:\Reibin\Trabajos\Iniciativas\zapateria\zapateria\zapateria-web\zapateria-web"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
mvn spring-boot:run
```

El frontend normalmente queda disponible en:

```text
http://localhost:8080
```

## 3. Orden recomendado

1. Levanta PostgreSQL.
2. Levanta el backend.
3. Levanta el frontend.
4. Abre el navegador y prueba login, catalogo y flujo con JWT.

## 4. Datos de prueba

Con el seed actual puedes probar estos usuarios:

- `admin@example.com` / `admin123`
- `user@example.com` / `user123`
- `test@example.com` / `test123`

## 5. URLs utiles

- Backend auth: `http://localhost:8081/api/auth/login`
- Backend productos: `http://localhost:8081/api/shoes`
- Backend categorias: `http://localhost:8081/api/categories`
- Backend compras: `http://localhost:8081/api/purchases`
- Backend rese?as: `http://localhost:8081/api/products/{productId}/reviews`

## 6. Pruebas rapidas

- Importa la coleccion de Postman desde `zapateria-back/postman/zapateria-back.postman_collection.json`.
- Usa el environment `zapateria-back/postman/zapateria-local.postman_environment.json`.
- Haz login y reutiliza el token en las peticiones protegidas.

## 7. Si falla el arranque

- Verifica que PostgreSQL este levantado.
- Verifica que exista la base `zapateria`.
- Verifica credenciales y puerto `5432`.
- Revisa que el backend este escuchando en `8081` antes de abrir el frontend.
