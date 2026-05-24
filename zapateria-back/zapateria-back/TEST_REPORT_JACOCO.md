# Reporte de Tests y JaCoCo

## Resumen

- Proyecto: zapateria-back
- Fecha de ejecucion: 2026-05-23
- Java: 21
- Spring Boot: 4.0.5
- Ejecucion usada: run-mvn-jdk21.bat (clean test jacoco:report)

## Resultado global de pruebas

- Total de pruebas: 92
- Exitosas: 92
- Fallidas: 0
- Con error: 0
- Omitidas: 0
- Tasa de exito: 100%

Fuentes:
- target/surefire-reports/TEST-*.xml
- salida Maven de la ultima ejecucion

## Suites ejecutadas

| Suite | Cantidad | Estado |
|---|---:|---|
| com.back.zapateria.config.JwtAuthenticationFilterTest | 4 | PASS |
| com.back.zapateria.controller.CartControllerTest | 4 | PASS |
| com.back.zapateria.controller.CategoryControllerTest | 4 | PASS |
| com.back.zapateria.controller.LoginControllerTest | 8 | PASS |
| com.back.zapateria.controller.PurchaseControllerTest | 8 | PASS |
| com.back.zapateria.controller.ReviewControllerTest | 3 | PASS |
| com.back.zapateria.controller.ShoeControllerTest | 9 | PASS |
| com.back.zapateria.model.ProductPhotoTest | 3 | PASS |
| com.back.zapateria.model.ProductTest | 4 | PASS |
| com.back.zapateria.service.CartServiceTest | 2 | PASS |
| com.back.zapateria.service.CategoryServiceTest | 5 | PASS |
| com.back.zapateria.service.JwtServiceTest | 4 | PASS |
| com.back.zapateria.service.LoginServiceTest | 11 | PASS |
| com.back.zapateria.service.PurchaseServiceTest | 9 | PASS |
| com.back.zapateria.service.ReviewServiceTest | 3 | PASS |
| com.back.zapateria.service.ShoeServiceTest | 10 | PASS |
| com.back.zapateria.ZapateriaBackApplicationTests | 1 | PASS |

## Nuevas pruebas agregadas en esta iteracion

### JWT y seguridad

- src/test/java/com/back/zapateria/service/JwtServiceTest.java
	- generateToken_and_extractClaims_successfully
	- extractUserId_handlesStringUidClaim
	- extractRole_returnsNull_whenRoleIsMissing
	- isTokenValid_returnsFalse_whenTokenExpired

- src/test/java/com/back/zapateria/config/JwtAuthenticationFilterTest.java
	- shouldNotFilter_returnsTrue_withoutBearerHeader
	- doFilterInternal_setsAuthentication_whenTokenIsValid
	- doFilterInternal_setsAuthenticationWithNoAuthorities_whenRoleIsNull
	- doFilterInternal_clearsContext_whenJwtServiceThrows

### Modelos

- src/test/java/com/back/zapateria/model/ProductTest.java
	- getGallery_mergesImageAndPhotos_distinctAndTrimmed
	- variantMethods_setBackReference_andManageCollection
	- photoMethods_setBackReference_andManageCollection
	- categoryId_returnsNullWithoutCategory_andValueWithCategory

- src/test/java/com/back/zapateria/model/ProductPhotoTest.java
	- constructor_initializesFields
	- setters_and_getters_workForAllFields
	- defaultConstructor_keepsNullableFieldsNull

## Cobertura JaCoCo

Fuentes:
- target/site/jacoco/jacoco.csv
- target/site/jacoco/index.html

### Cobertura global

- Instrucciones: 86.08% (2807/3261)
- Ramas: 61.06% (127/208)

### Cobertura del paquete de servicios

- Instrucciones: 87.82% (1175/1338)
- Ramas: 57.43% (85/148)

### Mejora puntual en clases objetivo de esta iteracion

| Clase | Cobertura instrucciones | Cobertura ramas |
|---|---:|---:|
| JwtService | 97.90% (140/143) | 66.67% (8/12) |
| JwtAuthenticationFilter | 98.75% (79/80) | 70.00% (7/10) |
| Product | 94.80% (237/250) | 75.00% (15/20) |
| ProductPhoto | 100.00% (43/43) | N/A |

## Como ejecutar

```bash
cd zapateria-back/zapateria-back
run-mvn-jdk21.bat
```

Abrir reporte HTML:

```text
target/site/jacoco/index.html
```
