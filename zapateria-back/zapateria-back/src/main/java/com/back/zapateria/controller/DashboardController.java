package com.back.zapateria.controller;

import com.back.zapateria.dto.DashboardStats;
import com.back.zapateria.repository.UserRepository;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        long userCount = userRepository.count();
        long productCount = productRepository.count();
        var purchases = purchaseRepository.findAll();
        long saleCount = purchases.size();
        double revenue = purchases.stream().mapToDouble(p -> p.getTotal()).sum();
        
        return ResponseEntity.ok(new DashboardStats(userCount, productCount, saleCount, revenue));
    }
}
