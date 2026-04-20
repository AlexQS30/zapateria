package com.back.zapateria.repository;

import com.back.zapateria.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_Id(String productId);
}
