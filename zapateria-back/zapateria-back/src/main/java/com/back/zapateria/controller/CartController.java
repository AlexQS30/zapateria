package com.back.zapateria.controller;

import com.back.zapateria.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@Tag(name = "Carrito", description = "Servicios para administrar el carrito de compras")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}/add")
    @Operation(summary = "Agregar al carrito", description = "Agrega un producto al carrito de un usuario")
    public ResponseEntity<Void> add(@PathVariable String userId,
                                    @RequestParam String productId,
                                    @RequestParam(defaultValue = "1") int qty) {
        cartService.addToCart(userId, productId, qty);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/remove")
    @Operation(summary = "Quitar del carrito", description = "Elimina un producto del carrito del usuario")
    public ResponseEntity<Void> remove(@PathVariable String userId,
                                       @RequestParam String productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Ver carrito", description = "Retorna el contenido actual del carrito del usuario")
    public ResponseEntity<Map<String, Integer>> get(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/clear")
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito del usuario")
    public ResponseEntity<Void> clear(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
