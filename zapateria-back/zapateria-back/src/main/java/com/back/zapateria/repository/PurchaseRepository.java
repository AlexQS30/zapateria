package com.back.zapateria.repository;

import com.back.zapateria.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, String> {
    
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.product WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Purchase> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.product")
    List<Purchase> findAll();

    boolean existsByUserIdAndItems_Product_Id(String userId, String productId);
    
    Optional<Purchase> findByIdAndUserId(String id, String userId);
}
