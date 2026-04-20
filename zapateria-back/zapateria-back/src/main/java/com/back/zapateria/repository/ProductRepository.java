package com.back.zapateria.repository;

import com.back.zapateria.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String q);
    List<Product> findByCategory_NameIgnoreCase(String categoryName);
    List<Product> findByCategory_Id(Long categoryId);
}
