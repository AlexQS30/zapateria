package com.back.zapateria.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    @Test
    void add_get_clear_cart_behaviour() {
        CartService cs = new CartService();
        cs.addToCart("u1", "p1", 2);
        cs.addToCart("u1", "p2", 1);

        Map<String,Integer> cart = cs.getCart("u1");
        assertEquals(2, cart.size());
        assertEquals(2, cart.get("p1"));

        cs.removeFromCart("u1", "p2");
        assertFalse(cs.getCart("u1").containsKey("p2"));

        cs.clearCart("u1");
        assertTrue(cs.getCart("u1").isEmpty());
    }

    // Este test comprueba el comportamiento en memoria del carrito.
}
