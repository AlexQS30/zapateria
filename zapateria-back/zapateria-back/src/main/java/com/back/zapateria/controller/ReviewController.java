package com.back.zapateria.controller;

import com.back.zapateria.model.Review;
import com.back.zapateria.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/public")
    public List<Review> getPublicReviews() {
        return reviewRepository.findByApproved(true);
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveReview(@PathVariable Long id) {
        return reviewRepository.findById(id).map(review -> {
            review.setApproved(true);
            reviewRepository.save(review);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
