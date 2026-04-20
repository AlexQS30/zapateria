package com.back.zapateria.service;

import com.back.zapateria.model.Product;
import com.back.zapateria.model.Review;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.PurchaseRepository;
import com.back.zapateria.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReviewServiceTest {

    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService();
        try {
            var f = ReviewService.class.getDeclaredField("reviewRepository");
            f.setAccessible(true); f.set(reviewService, reviewRepository);
            var f2 = ReviewService.class.getDeclaredField("purchaseRepository");
            f2.setAccessible(true); f2.set(reviewService, purchaseRepository);
            var f3 = ReviewService.class.getDeclaredField("productRepository");
            f3.setAccessible(true); f3.set(reviewService, productRepository);
            // mock save to return the entity provided
            org.mockito.Mockito.when(reviewRepository.save(org.mockito.Mockito.any())).thenAnswer(i -> i.getArgument(0));
        } catch (Exception ignored) {}
    }

    // Este test valida que solo se permita crear una reseña cuando el usuario ya compró el producto.
    @Test
    void createReview_requiresPurchase() {
        when(purchaseRepository.existsByUserIdAndItems_Product_Id("u1","2")).thenReturn(true);
        when(productRepository.findById("2")).thenReturn(java.util.Optional.of(new Product("2","P",1.0,"/i", (String) null, true,1,0,5.0)));

        Review r = reviewService.createReview("u1","2",5,"Good");
        assertNotNull(r);
        assertEquals(5, r.getRating());
    }

    // Este test comprueba que el servicio devuelve las reseñas del producto cuando el repositorio está disponible.
    @Test
    void listByProduct_returnsRepositoryReviews() {
        when(reviewRepository.findByProduct_Id("2")).thenReturn(java.util.List.of(new Review(null, "u1", 4, "Nice")));

        var reviews = reviewService.listByProduct("2");
        assertEquals(1, reviews.size());
        assertEquals(4, reviews.get(0).getRating());
    }

    // Este test verifica que el servicio rechaza una reseña si el usuario no compró el producto.
    @Test
    void createReview_throwsWhenUserDidNotPurchase() {
        when(purchaseRepository.existsByUserIdAndItems_Product_Id("u1", "2")).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> reviewService.createReview("u1", "2", 5, "Good"));
        assertTrue(ex.getMessage().contains("purchased"));
    }
 
}
