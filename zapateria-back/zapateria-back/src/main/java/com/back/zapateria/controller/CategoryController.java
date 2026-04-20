package com.back.zapateria.controller;

import com.back.zapateria.model.Category;
import com.back.zapateria.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Categorías", description = "Servicios para administrar categorías de productos")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Retorna todas las categorías registradas")
    public ResponseEntity<List<Category>> list() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría")
    public ResponseEntity<Category> create(@RequestBody Category c) {
        Category created = categoryService.create(c);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría", description = "Retorna una categoría por su ID")
    public ResponseEntity<Category> getOne(@PathVariable Long id) {
        return categoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría por su ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
