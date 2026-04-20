package com.back.zapateria.service;

import com.back.zapateria.model.Purchase;
import com.back.zapateria.model.PurchaseItem;
import com.back.zapateria.model.Product;
import com.back.zapateria.repository.PurchaseRepository;
import com.back.zapateria.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PurchaseService {

    @Autowired(required = false)
    private PurchaseRepository purchaseRepository;

    @Autowired(required = false)
    private ProductRepository productRepository;

    public Purchase createPurchase(String userId, Map<String,Integer> items, double total) {
        String id = UUID.randomUUID().toString();
        Purchase p = new Purchase(id, userId, total);
        if (items != null && productRepository != null) {
            for (Map.Entry<String,Integer> e : items.entrySet()) {
                productRepository.findById(e.getKey()).ifPresent(prod -> {
                    PurchaseItem it = new PurchaseItem(prod, e.getValue());
                    p.addItem(it);
                });
            }
        }
        if (purchaseRepository != null) return purchaseRepository.save(p);
        return p;
    }

    public List<Purchase> listByUser(String userId) {
        if (purchaseRepository != null) return purchaseRepository.findByUserId(userId);
        return List.of();
    }
}
