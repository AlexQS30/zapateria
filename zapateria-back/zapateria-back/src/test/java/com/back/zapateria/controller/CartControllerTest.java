package com.back.zapateria.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.back.zapateria.service.CartService;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController controller;

    @Test
    void add_callsService_andReturnsOk() {
        ResponseEntity<Void> response = controller.add("u1", "p1", 2);

        assertEquals(200, response.getStatusCode().value());
        verify(cartService).addToCart("u1", "p1", 2);
    }

    @Test
    void remove_callsService_andReturnsOk() {
        ResponseEntity<Void> response = controller.remove("u1", "p1");

        assertEquals(200, response.getStatusCode().value());
        verify(cartService).removeFromCart("u1", "p1");
    }

    @Test
    void get_returnsCurrentCart() {
        when(cartService.getCart("u1")).thenReturn(Map.of("p1", 1, "p2", 3));

        ResponseEntity<Map<String, Integer>> response = controller.get("u1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertEquals(3, response.getBody().get("p2"));
    }

    @Test
    void clear_callsService_andReturnsNoContent() {
        ResponseEntity<Void> response = controller.clear("u1");

        assertEquals(204, response.getStatusCode().value());
        verify(cartService).clearCart("u1");
    }
}
