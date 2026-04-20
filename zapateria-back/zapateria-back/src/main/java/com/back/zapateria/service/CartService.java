package com.back.zapateria.service;

import com.back.zapateria.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    // simple in-memory cart: userId -> map(productId -> quantity)
    private final Map<String, Map<String, Integer>> carts = new HashMap<>();

    public void addToCart(String userId, String productId, int qty) {
        carts.computeIfAbsent(userId, k -> new HashMap<>());
        Map<String, Integer> cart = carts.get(userId);
        cart.put(productId, cart.getOrDefault(productId, 0) + qty);
    }

    public void removeFromCart(String userId, String productId) {
        Map<String, Integer> cart = carts.get(userId);
        if (cart != null) cart.remove(productId);
    }

    public Map<String, Integer> getCart(String userId) {
        return carts.getOrDefault(userId, Collections.emptyMap());
    }

    public void clearCart(String userId) { carts.remove(userId); }
}
