package com.back.zapateria.controller;

import com.back.zapateria.model.Product;
import com.back.zapateria.model.ProductVariant;
import com.back.zapateria.service.ShoeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shoes")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Productos", description = "Servicios para listar, filtrar y administrar productos")
public class ShoeController {

    @Autowired
    private ShoeService shoeService;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Retorna todos los productos o filtra por nombre usando el parámetro q")
    public ResponseEntity<List<Product>> listAll(@RequestParam(value = "q", required = false) String q) {
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(shoeService.searchByName(q));
        }
        return ResponseEntity.ok(shoeService.getAllProducts());
    }

    @GetMapping("/offers")
    @Operation(summary = "Listar ofertas", description = "Retorna productos con descuento y stock disponible")
    public ResponseEntity<List<Product>> offers() {
        List<Product> all = shoeService.getAllProducts();
        List<Product> offers = all.stream()
                .filter(p -> p.getDiscount() > 0 && p.getStock() > 0)
                .toList();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar por categoría", description = "Retorna productos filtrados por nombre de categoría")
    public ResponseEntity<List<Product>> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(shoeService.getCategoryProducts(category));
    }

    @GetMapping("/by-category-id/{categoryId}")
    @Operation(summary = "Filtrar por ID de categoría", description = "Retorna productos asociados a un categoryId específico")
    public ResponseEntity<List<Product>> byCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(shoeService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Retorna el detalle de un producto por su ID")
    public ResponseEntity<Product> getOne(@PathVariable String id) {
        Product p = shoeService.getProductDetail(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/variants")
    @Operation(summary = "Listar variantes", description = "Retorna las variantes de talla, color y stock de un producto")
    public ResponseEntity<List<ProductVariant>> variants(@PathVariable String id) {
        return ResponseEntity.ok(shoeService.getProductVariants(id));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Disponibilidad del producto", description = "Retorna stock total y variantes disponibles")
    public ResponseEntity<Map<String, Object>> availability(@PathVariable String id) {
        return ResponseEntity.ok(shoeService.getAvailability(id));
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product created = shoeService.createProduct(product);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody Product product) {
        Product updated = shoeService.updateProduct(id, product);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean removed = shoeService.deleteProduct(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
