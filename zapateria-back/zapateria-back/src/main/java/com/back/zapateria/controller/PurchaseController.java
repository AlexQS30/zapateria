package com.back.zapateria.controller;

import com.back.zapateria.service.PurchaseService;
import com.back.zapateria.model.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/{userId}/create")
    public ResponseEntity<Purchase> create(@PathVariable String userId,
                                                           @RequestBody Map<String,Integer> items,
                                                           @RequestParam double total) {
        Purchase p = purchaseService.createPurchase(userId, items, total);
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> list(@PathVariable String userId) {
        return ResponseEntity.ok(purchaseService.listByUser(userId));
    }
}
