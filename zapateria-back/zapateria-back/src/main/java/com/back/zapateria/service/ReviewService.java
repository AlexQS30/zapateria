package com.back.zapateria.service;

import com.back.zapateria.model.Product;
import com.back.zapateria.model.Review;
import com.back.zapateria.model.User;
import com.back.zapateria.repository.PurchaseRepository;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.ReviewRepository;
import com.back.zapateria.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired(required = false)
    private ReviewRepository reviewRepository;

    @Autowired(required = false)
    private PurchaseRepository purchaseRepository;

    @Autowired(required = false)
    private ProductRepository productRepository;

    @Autowired(required = false)
    private UserRepository userRepository;

    public List<Review> listByProduct(String productId) {
        if (reviewRepository != null) return reviewRepository.findByProduct_Id(productId);
        return List.of();
    }

    public Review createReview(String userEmail, String productId, int rating, String comment) {
        User user = requireUser(userEmail);
        String userId = String.valueOf(user.getId());

        // Require that user bought the product
        if (purchaseRepository != null && !purchaseRepository.existsByUserIdAndItems_Product_Id(userId, productId)) {
            throw new IllegalStateException("User must have purchased the product to review it");
        }

        Product product = null;
        if (productRepository != null) product = productRepository.findById(productId).orElse(null);
        Review r = new Review(product, userId, rating, comment);
        if (reviewRepository != null) return reviewRepository.save(r);
        return r;
    }

    public List<Review> listByUser(String userEmail) {
        User user = requireUser(userEmail);
        if (reviewRepository != null) return reviewRepository.findByUserIdOrderByCreatedAtDesc(String.valueOf(user.getId()));
        return List.of();
    }

    private User requireUser(String userEmail) {
        if (userRepository == null) {
            throw new IllegalStateException("Repositorio de usuarios no disponible");
        }

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
