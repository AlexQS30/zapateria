package com.back.zapateria.controller;

import com.back.zapateria.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> add(@PathVariable String userId,
                                    @RequestParam String productId,
                                    @RequestParam(defaultValue = "1") int qty) {
        cartService.addToCart(userId, productId, qty);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/remove")
    public ResponseEntity<Void> remove(@PathVariable String userId,
                                       @RequestParam String productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Integer>> get(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/clear")
    public ResponseEntity<Void> clear(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
