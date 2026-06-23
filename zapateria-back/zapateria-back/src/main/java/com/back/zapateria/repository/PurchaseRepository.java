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

    @Query("SELECT CONCAT(u.firstName, ' ', u.lastName), COUNT(p) FROM Purchase p JOIN User u ON CAST(p.userId AS long) = u.id GROUP BY u.firstName, u.lastName ORDER BY COUNT(p) DESC LIMIT 5")
    List<Object[]> findTopBuyers();

    @Query("SELECT i.product.name, SUM(i.quantity) FROM PurchaseItem i GROUP BY i.product.name ORDER BY SUM(i.quantity) DESC LIMIT 5")
    List<Object[]> findTopProducts();

    @Query(value = "SELECT TO_CHAR(created_at, 'Month YYYY'), COUNT(*) FROM purchase GROUP BY TO_CHAR(created_at, 'Month YYYY') ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findPeakSalesMonth();

    boolean existsByUserIdAndItems_Product_Id(String userId, String productId);
    
    Optional<Purchase> findByIdAndUserId(String id, String userId);
}
