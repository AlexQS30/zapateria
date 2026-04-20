package com.back.zapateria.controller;

import com.back.zapateria.model.Review;
import com.back.zapateria.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@CrossOrigin(origins = "*")
@Tag(name = "Reseñas", description = "Servicios para listar y crear reseñas de productos")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Listar reseñas de producto", description = "Retorna todas las reseñas asociadas a un producto")
    public ResponseEntity<List<Review>> list(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.listByProduct(productId));
    }

    @PostMapping
    @Operation(summary = "Crear reseña", description = "Crea una reseña para un producto validando la compra previa")
    public ResponseEntity<Review> create(@PathVariable String productId,
                                         @RequestParam String userId,
                                         @RequestParam int rating,
                                         @RequestParam(required = false) String comment) {
        Review r = reviewService.createReview(userId, productId, rating, comment);
        return ResponseEntity.status(201).body(r);
    }
}
