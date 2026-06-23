package com.back.zapateria.controller;

import java.util.List;
import java.util.Optional;

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
import com.back.zapateria.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewController controller;

    @Test
    void list_returnsAllReviews() {
        Product product = new Product("p1", "Zapatilla", 100.0, "/img", "hombre", false, 3, 0, 4.0);
        when(reviewRepository.findAll()).thenReturn(List.of(new Review(product, "u1", 5, "Excelente")));

        List<Review> response = controller.getAllReviews();

        assertEquals(1, response.size());
        verify(reviewRepository).findAll();
    }

    @Test
    void approveReview_updatesStatus() {
        Review review = new Review(new Product(), "u1", 4, "Buena");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ResponseEntity<?> response = controller.approveReview(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, review.isApproved());
        verify(reviewRepository).save(review);
    }
}
