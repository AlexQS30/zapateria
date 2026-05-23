package com.back.zapateria.repository;

import com.back.zapateria.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProduct_Id(String productId);

    Optional<ProductVariant> findByProduct_IdAndSizeIgnoreCaseAndColorIgnoreCase(String productId, String size, String color);
}