package com.back.zapateria.controller;

import com.back.zapateria.service.PurchaseService;
import com.back.zapateria.model.Purchase;
import com.back.zapateria.dto.CheckoutRequest;
import com.back.zapateria.dto.PurchaseStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.security.Principal;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
@Tag(name = "Compras", description = "Servicios para registrar y consultar compras")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/checkout")
    @Operation(summary = "Crear compra autenticada", description = "Registra una compra para el usuario autenticado")
    public ResponseEntity<?> checkout(Principal principal, @RequestBody CheckoutRequest request) {
        try {
            Purchase p = purchaseService.checkout(principal.getName(), request);
            return ResponseEntity.status(201).body(p);
        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Listar compras del usuario autenticado", description = "Retorna las compras asociadas al usuario autenticado")
    public ResponseEntity<?> list(Principal principal) {
        return ResponseEntity.ok(purchaseService.listByUserEmail(principal.getName()));
    }

    @GetMapping("/me/{purchaseId}")
    @Operation(summary = "Obtener compra del usuario autenticado", description = "Retorna una compra por su ID para el usuario autenticado")
    public ResponseEntity<Purchase> getOne(Principal principal, @PathVariable String purchaseId) {
        return ResponseEntity.ok(purchaseService.getByIdForUser(principal.getName(), purchaseId));
    }

    @PatchMapping("/{purchaseId}/status")
    @Operation(summary = "Actualizar estado de compra", description = "Permite cambiar el estado de un pedido")
    public ResponseEntity<Purchase> updateStatus(@PathVariable String purchaseId, @RequestBody PurchaseStatusUpdateRequest request) {
        return ResponseEntity.ok(purchaseService.updateStatus(purchaseId, request));
    }

    @PostMapping("/{userId}/create")
    @Operation(summary = "Crear compra (compatibilidad)", description = "Mantiene compatibilidad con clientes antiguos")
    public ResponseEntity<Purchase> create(@PathVariable String userId,
                                                           @RequestBody Map<String,Integer> items,
                                                           @RequestParam double total) {
        Purchase p = purchaseService.createPurchase(userId, items, total);
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Listar compras por usuario (compatibilidad)", description = "Retorna las compras asociadas a un usuario")
    public ResponseEntity<?> legacyList(@PathVariable String userId) {
        return ResponseEntity.ok(purchaseService.listByUser(userId));
    }
}
