package com.back.zapateria.repository;

import com.back.zapateria.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, String> {
    List<Purchase> findByUserId(String userId);
    boolean existsByUserIdAndItems_Product_Id(String userId, String productId);
}
