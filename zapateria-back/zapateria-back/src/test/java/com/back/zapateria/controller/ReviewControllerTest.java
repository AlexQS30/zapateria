package com.back.zapateria.controller;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.back.zapateria.model.Product;
import com.back.zapateria.model.Review;
import com.back.zapateria.service.ReviewService;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController controller;

    @Test
    void list_returnsProductReviews() {
        Product product = new Product("p1", "Zapatilla", 100.0, "/img", "hombre", false, 3, 0, 4.0);
        when(reviewService.listByProduct("p1")).thenReturn(List.of(new Review(product, "u1", 5, "Excelente")));

        ResponseEntity<List<Review>> response = controller.list("p1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(reviewService).listByProduct("p1");
    }

    @Test
    void create_returnsCreatedReview() {
        Principal principal = () -> "user@demo.com";
        Product product = new Product("p1", "Zapatilla", 100.0, "/img", "hombre", false, 3, 0, 4.0);
        Review review = new Review(product, "1", 4, "Buena compra");
        when(reviewService.createReview("user@demo.com", "p1", 4, "Buena compra")).thenReturn(review);

        ResponseEntity<Review> response = controller.create(principal, "p1", 4, "Buena compra");

        assertEquals(201, response.getStatusCode().value());
        assertEquals(4, response.getBody().getRating());
        verify(reviewService).createReview("user@demo.com", "p1", 4, "Buena compra");
    }

    @Test
    void myReviews_returnsAuthenticatedUserReviews() {
        Principal principal = () -> "user@demo.com";
        when(reviewService.listByUser("user@demo.com")).thenReturn(List.of());

        ResponseEntity<List<Review>> response = controller.myReviews(principal);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().size());
        verify(reviewService).listByUser("user@demo.com");
    }
}
