package com.back.zapateria.service;

import com.back.zapateria.model.Product;
import com.back.zapateria.model.Purchase;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PurchaseServiceTest {

    private PurchaseService purchaseService;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        purchaseService = new PurchaseService();
        try {
            var f = PurchaseService.class.getDeclaredField("purchaseRepository");
            f.setAccessible(true);
            f.set(purchaseService, purchaseRepository);
            var f2 = PurchaseService.class.getDeclaredField("productRepository");
            f2.setAccessible(true);
            f2.set(purchaseService, productRepository);
            // default save should return the passed entity to mimic repository behavior
            org.mockito.Mockito.when(purchaseRepository.save(org.mockito.Mockito.any())).thenAnswer(i -> i.getArgument(0));
        } catch (Exception ignored) {}
    }

    @Test
    void createPurchase_persists_whenReposPresent() {
        when(productRepository.findById("2")).thenReturn(java.util.Optional.of(new Product("2","P",1.0,"/i", (String) null, true,1,0,5.0)));
        // We won't mock purchaseRepository.save here; the method should return a Purchase instance even if repo null
        Purchase p = purchaseService.createPurchase("u1", Map.of("2",1), 1.0);
        assertNotNull(p);
        assertEquals("u1", p.getUserId());
        assertFalse(p.getItems().isEmpty());
    }
 
}
