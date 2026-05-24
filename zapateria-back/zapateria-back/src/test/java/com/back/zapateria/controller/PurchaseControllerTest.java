package com.back.zapateria.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.back.zapateria.dto.CheckoutRequest;
import com.back.zapateria.dto.PurchaseStatusUpdateRequest;
import com.back.zapateria.model.Purchase;
import com.back.zapateria.service.PurchaseService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController controller;

    @Test
    void checkout_success_returnsCreated() {
        Principal principal = () -> "user@demo.com";
        CheckoutRequest request = new CheckoutRequest();
        Purchase purchase = new Purchase("p-1", "1", 120.0);
        when(purchaseService.checkout("user@demo.com", request)).thenReturn(purchase);

        ResponseEntity<?> response = controller.checkout(principal, request);

        assertEquals(201, response.getStatusCode().value());
        assertInstanceOf(Purchase.class, response.getBody());
    }

    @Test
    void checkout_badRequest_whenServiceThrows() {
        Principal principal = () -> "user@demo.com";
        CheckoutRequest request = new CheckoutRequest();
        when(purchaseService.checkout("user@demo.com", request)).thenThrow(new IllegalArgumentException("Carrito vacío"));

        ResponseEntity<?> response = controller.checkout(principal, request);

        assertEquals(400, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Carrito vacío", body.get("message"));
    }

    @Test
    void checkout_badRequest_whenEntityNotFound() {
        Principal principal = () -> "user@demo.com";
        CheckoutRequest request = new CheckoutRequest();
        when(purchaseService.checkout("user@demo.com", request)).thenThrow(new EntityNotFoundException("No encontrado"));

        ResponseEntity<?> response = controller.checkout(principal, request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void list_returnsCurrentUserPurchases() {
        Principal principal = () -> "user@demo.com";
        when(purchaseService.listByUserEmail("user@demo.com")).thenReturn(List.of(new Purchase("p-1", "1", 40.0)));

        ResponseEntity<?> response = controller.list(principal);

        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(List.class, response.getBody());
    }

    @Test
    void getOne_returnsPurchaseForUser() {
        Principal principal = () -> "user@demo.com";
        Purchase purchase = new Purchase("p-2", "1", 300.0);
        when(purchaseService.getByIdForUser("user@demo.com", "p-2")).thenReturn(purchase);

        ResponseEntity<Purchase> response = controller.getOne(principal, "p-2");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("p-2", response.getBody().getId());
    }

    @Test
    void updateStatus_returnsUpdatedPurchase() {
        PurchaseStatusUpdateRequest request = new PurchaseStatusUpdateRequest();
        request.setStatus("pagado");
        Purchase updated = new Purchase("p-3", "1", 50.0);
        updated.setStatus("PAGADO");
        when(purchaseService.updateStatus("p-3", request)).thenReturn(updated);

        ResponseEntity<Purchase> response = controller.updateStatus("p-3", request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PAGADO", response.getBody().getStatus());
    }

    @Test
    void create_legacy_returnsCreated() {
        Map<String, Integer> items = Map.of("shoe-1", 2);
        Purchase created = new Purchase("p-4", "legacy", 80.0);
        when(purchaseService.createPurchase("legacy", items, 80.0)).thenReturn(created);

        ResponseEntity<Purchase> response = controller.create("legacy", items, 80.0);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("p-4", response.getBody().getId());
    }

    @Test
    void legacyList_returnsPurchasesByUserId() {
        when(purchaseService.listByUser("legacy")).thenReturn(List.of(new Purchase("p-5", "legacy", 10.0)));

        ResponseEntity<?> response = controller.legacyList("legacy");

        assertEquals(200, response.getStatusCode().value());
        verify(purchaseService).listByUser("legacy");
    }
}
