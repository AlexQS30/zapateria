# Reporte de Tests y Swagger

## Resumen

- Proyecto: zapateria-back
- Fecha de ejecucion: 2026-04-20
- Java: 21
- Spring Boot: 4.0.5
- Ejecucion usada: mvnw.cmd clean test jacoco:report

## Resultado global de pruebas

- Total de pruebas: 38
- Exitosas: 38
- Fallidas: 0
- Con error: 0
- Omitidas: 0
- Tasa de exito: 100%

Fuente:
- target/surefire-reports/TEST-*.xml

## Cantidad de pruebas por suite

| Suite | Cantidad | Estado |
|---|---:|---|
| com.back.zapateria.service.LoginServiceTest | 11 | PASS |
| com.back.zapateria.service.CategoryServiceTest | 5 | PASS |
| com.back.zapateria.service.CartServiceTest | 2 | PASS |
| com.back.zapateria.service.PurchaseServiceTest | 2 | PASS |
| com.back.zapateria.service.ReviewServiceTest | 3 | PASS |
| com.back.zapateria.service.ShoeServiceTest | 7 | PASS |
| com.back.zapateria.controller.ShoeControllerTest | 7 | PASS |
| com.back.zapateria.ZapateriaBackApplicationTests | 1 | PASS |

## Casos de prueba de servicios (30)

### LoginServiceTest (11)

Archivo: src/test/java/com/back/zapateria/service/LoginServiceTest.java

1. authenticate_success: autentica un usuario activo con credenciales validas.
2. authenticate_userNotFound: rechaza login cuando el email no existe.
3. authenticate_wrongPassword: rechaza login por password incorrecto.
4. authenticate_inactiveUser: bloquea login de usuario inactivo.
5. register_success: registra usuario nuevo con datos completos.
6. register_emailAlreadyExists: evita registro con email duplicado.
7. getAllUsers_returnsOnlyActiveUsers: lista solo usuarios activos.
8. updateUser_changesFields: actualiza datos principales del usuario.
9. deleteUser_marksInactive: aplica borrado logico (inactivo).
10. emailExists_returnsTrueForExistingEmail: confirma email existente.
11. emailExists_returnsFalseForMissingEmail: confirma email no existente.

### CategoryServiceTest (5)

Archivo: src/test/java/com/back/zapateria/service/CategoryServiceTest.java

1. getAll_returnsList: devuelve categorias del repositorio.
2. getByName_findsCategoryIgnoringCase: busca por nombre sin importar mayusculas.
3. getOrCreateByName_returnsExistingCategory: reutiliza categoria existente.
4. getOrCreateByName_createsNewCategory: crea categoria cuando no existe.
5. delete_removesCategory: elimina categoria por id.

### CartServiceTest (2)

Archivo: src/test/java/com/back/zapateria/service/CartServiceTest.java

1. add_get_clear_cart_behaviour: agrega, consulta, quita y limpia carrito.
2. addToCart_accumulatesQuantityForSameProduct: acumula cantidad del mismo producto.

### PurchaseServiceTest (2)

Archivo: src/test/java/com/back/zapateria/service/PurchaseServiceTest.java

1. createPurchase_persists_whenReposPresent: crea compra con items validados en repositorio.
2. listByUser_returnsRepositoryResults: lista compras por usuario.

### ReviewServiceTest (3)

Archivo: src/test/java/com/back/zapateria/service/ReviewServiceTest.java

1. createReview_requiresPurchase: permite reseña solo si hubo compra previa.
2. listByProduct_returnsRepositoryReviews: lista reseñas por producto.
3. createReview_throwsWhenUserDidNotPurchase: lanza error si no hay compra previa.

### ShoeServiceTest (7)

Archivo: src/test/java/com/back/zapateria/service/ShoeServiceTest.java

1. getAllProducts_notEmpty: lista productos disponibles.
2. createProduct_assignsId: crea producto y asigna id.
3. updateProduct_changesFields: actualiza campos de producto.
4. deleteProduct_removes: elimina producto existente.
5. searchByName_finds: busca por nombre.
6. getProductsByCategoryId_filtersByCategoryId: filtra productos por categoryId.
7. getProductsByCategoryId_returnsEmptyWhenNoMatch: responde vacio cuando no hay coincidencia.

## Cobertura JaCoCo

Fuente:
- target/site/jacoco/jacoco.csv
- target/site/jacoco/index.html

### Cobertura global

- Instrucciones (total proyecto): 76.88%
- Ramas (total proyecto): 52.56%

### Cobertura del paquete de servicios

- Instrucciones (com.back.zapateria.service): 92.53%
- Ramas (com.back.zapateria.service): 58.00%

### Detalle por servicio

| Clase | Cobertura instrucciones | Cobertura ramas |
|---|---:|---:|
| CartService | 100.00% | 50.00% |
| LoginService | 96.97% | 75.00% |
| PurchaseService | 94.59% | 60.00% |
| ReviewService | 93.22% | 60.00% |
| CategoryService | 87.23% | N/A |
| ShoeService | 80.60% | 33.33% |

## Swagger/OpenAPI de servicios

Se agrego documentacion OpenAPI con Springdoc y anotaciones por controlador.

Cambios principales:
- Dependencia springdoc-openapi-starter-webmvc-ui en pom.xml
- Configuracion de metadata en src/main/java/com/back/zapateria/config/OpenApiConfig.java
- Permisos de seguridad para Swagger en src/main/java/com/back/zapateria/config/SecurityConfig.java
- Tags y operaciones documentadas en controladores de autenticacion, productos, categorias, carrito, compras y reseñas

### URLs

- Swagger UI: http://localhost:8081/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs

## Como ejecutar

```bash
cd zapateria-back/zapateria-back
mvnw.cmd clean test jacoco:report
```

Abrir reporte HTML de cobertura:

```text
target/site/jacoco/index.html
```
