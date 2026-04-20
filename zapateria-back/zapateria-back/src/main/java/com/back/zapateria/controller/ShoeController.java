package com.back.zapateria.controller;

import com.back.zapateria.model.Product;
import com.back.zapateria.service.ShoeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shoes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShoeController {

    @Autowired
    private ShoeService shoeService;

    @GetMapping
    public ResponseEntity<List<Product>> listAll(@RequestParam(value = "q", required = false) String q) {
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(shoeService.searchByName(q));
        }
        return ResponseEntity.ok(shoeService.getAllProducts());
    }

    @GetMapping("/offers")
    public ResponseEntity<List<Product>> offers() {
        List<Product> all = shoeService.getAllProducts();
        List<Product> offers = all.stream()
                .filter(p -> p.getDiscount() > 0 && p.getStock() > 0)
                .toList();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(shoeService.getCategoryProducts(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable String id) {
        Product p = shoeService.getProductDetail(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product created = shoeService.createProduct(product);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody Product product) {
        Product updated = shoeService.updateProduct(id, product);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean removed = shoeService.deleteProduct(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
