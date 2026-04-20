package com.back.zapateria.controller;

import com.back.zapateria.model.Review;
import com.back.zapateria.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> list(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.listByProduct(productId));
    }

    @PostMapping
    public ResponseEntity<Review> create(@PathVariable String productId,
                                         @RequestParam String userId,
                                         @RequestParam int rating,
                                         @RequestParam(required = false) String comment) {
        Review r = reviewService.createReview(userId, productId, rating, comment);
        return ResponseEntity.status(201).body(r);
    }
}
