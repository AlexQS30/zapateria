package com.back.zapateria.controller;

import com.back.zapateria.service.PurchaseService;
import com.back.zapateria.model.Purchase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
@Tag(name = "Compras", description = "Servicios para registrar y consultar compras")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/{userId}/create")
    @Operation(summary = "Crear compra", description = "Registra una compra para un usuario con sus productos y total")
    public ResponseEntity<Purchase> create(@PathVariable String userId,
                                                           @RequestBody Map<String,Integer> items,
                                                           @RequestParam double total) {
        Purchase p = purchaseService.createPurchase(userId, items, total);
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Listar compras por usuario", description = "Retorna las compras asociadas a un usuario")
    public ResponseEntity<?> list(@PathVariable String userId) {
        return ResponseEntity.ok(purchaseService.listByUser(userId));
    }
}
