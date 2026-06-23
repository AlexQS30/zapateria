package com.back.zapateria.service;

import com.back.zapateria.dto.CheckoutItemRequest;
import com.back.zapateria.dto.CheckoutRequest;
import com.back.zapateria.dto.PurchaseStatusUpdateRequest;
import com.back.zapateria.model.Category;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.back.zapateria.model.Product;
import com.back.zapateria.model.ProductVariant;
import com.back.zapateria.model.Purchase;
import com.back.zapateria.model.User;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.ProductVariantRepository;
import com.back.zapateria.repository.PurchaseRepository;
import com.back.zapateria.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

class PurchaseServiceTest {

    private PurchaseService purchaseService;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private UserRepository userRepository;

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
            var f3 = PurchaseService.class.getDeclaredField("productVariantRepository");
            f3.setAccessible(true);
            f3.set(purchaseService, productVariantRepository);
            var f4 = PurchaseService.class.getDeclaredField("userRepository");
            f4.setAccessible(true);
            f4.set(purchaseService, userRepository);
            // default save should return the passed entity to mimic repository behavior
            org.mockito.Mockito.when(purchaseRepository.save(org.mockito.Mockito.any())).thenAnswer(i -> i.getArgument(0));
            org.mockito.Mockito.when(productRepository.save(org.mockito.Mockito.any())).thenAnswer(i -> i.getArgument(0));
            org.mockito.Mockito.when(productVariantRepository.save(org.mockito.Mockito.any())).thenAnswer(i -> i.getArgument(0));
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            throw new IllegalStateException("No se pudo inyectar el mock en PurchaseService", ignored);
        }
    }

    // Este test valida que una compra se construye con los productos encontrados en el repositorio.
    @Test
    void createPurchase_persists_whenReposPresent() {
        when(productRepository.findById("2")).thenReturn(java.util.Optional.of(new Product("2","P",1.0,"/i", (String) null, true,1,0,5.0)));
        Purchase p = purchaseService.createPurchase("u1", Map.of("2",1), 1.0);
        assertNotNull(p);
        assertEquals("u1", p.getUserId());
        assertFalse(p.getItems().isEmpty());
    }

    // Este test comprueba que listar compras por usuario devuelve lo que el repositorio reporta.
    @Test
    void listByUser_returnsRepositoryResults() {
        when(purchaseRepository.findByUserIdOrderByCreatedAtDesc("u1")).thenReturn(java.util.List.of(new Purchase("p1", "u1", 10.0)));

        var purchases = purchaseService.listByUser("u1");
        assertEquals(1, purchases.size());
        assertEquals("u1", purchases.get(0).getUserId());
    }

    @Test
    void checkout_withVariants_updatesVariantAndProductStock() {
        User user = user("cliente@example.com", 2L, "Calle 123");
        Product product = productWithVariantStock("1", 10, "39", "Negro", 6);

        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productVariantRepository.findByProduct_IdAndSizeIgnoreCaseAndColorIgnoreCase("1", "39", "Negro"))
                .thenReturn(Optional.of(product.getVariants().get(0)));

        CheckoutRequest request = checkoutRequest("1", 2, "39", "Negro", "Yape", null, "999111222");
        Purchase purchase = purchaseService.checkout("cliente@example.com", request);

        assertNotNull(purchase);
        assertEquals(1, purchase.getItems().size());
        assertEquals(8, product.getStock());
        assertEquals(4, product.getVariants().get(0).getStock());
        assertEquals("YAPE", purchase.getPaymentMethod());
        assertEquals("Calle 123", purchase.getShippingAddress());
    }

    @Test
    void checkout_requiresSizeAndColor_whenProductHasVariants() {
        User user = user("cliente@example.com", 2L, "Dir");
        Product product = productWithVariantStock("1", 10, "39", "Negro", 6);

        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        CheckoutRequest request = checkoutRequest("1", 1, null, null, "efectivo", "Dir 2", "900000000");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> purchaseService.checkout("cliente@example.com", request));

        assertTrue(ex.getMessage().contains("talla y color"));
    }

    @Test
    void checkout_throwsWhenVariantStockIsInsufficient() {
        User user = user("cliente@example.com", 2L, "Dir");
        Product product = productWithVariantStock("1", 10, "39", "Negro", 1);

        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productVariantRepository.findByProduct_IdAndSizeIgnoreCaseAndColorIgnoreCase("1", "39", "Negro"))
                .thenReturn(Optional.of(product.getVariants().get(0)));

        CheckoutRequest request = checkoutRequest("1", 2, "39", "Negro", "cash", "Dir", "900");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> purchaseService.checkout("cliente@example.com", request));
        assertTrue(ex.getMessage().contains("stock suficiente"));
    }

    @Test
    void checkout_throwsWhenVariantDoesNotExist() {
        User user = user("cliente@example.com", 2L, "Dir");
        Product product = productWithVariantStock("1", 10, "39", "Negro", 6);

        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productVariantRepository.findByProduct_IdAndSizeIgnoreCaseAndColorIgnoreCase("1", "40", "Azul"))
                .thenReturn(Optional.empty());

        CheckoutRequest request = checkoutRequest("1", 1, "40", "Azul", "tarjeta", "Dir", "900");

        assertThrows(EntityNotFoundException.class, () -> purchaseService.checkout("cliente@example.com", request));
    }

    @Test
    void checkout_withoutVariants_decrementsProductStock() {
        User user = user("cliente@example.com", 2L, "Dir");
        Product product = new Product("2", "Sin variante", 120.0, "/img", "Hombre", false, 3, 0, 4.0);

        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById("2")).thenReturn(Optional.of(product));

        CheckoutRequest request = checkoutRequest("2", 2, null, null, "tarjeta", "Av. Nueva", "900");
        Purchase purchase = purchaseService.checkout("cliente@example.com", request);

        assertEquals(1, product.getStock());
        assertEquals("TARJETA_CREDITO", purchase.getPaymentMethod());
        assertEquals("Av. Nueva", purchase.getShippingAddress());
    }

    @Test
    void listByUserEmail_and_getByIdForUser_workWithAuthenticatedUser() {
        User user = user("cliente@example.com", 2L, "Dir");
        Purchase purchase = new Purchase("p100", "2", 55.0);

        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(purchaseRepository.findByUserIdOrderByCreatedAtDesc("2")).thenReturn(java.util.List.of(purchase));
        when(purchaseRepository.findByIdAndUserId("p100", "2")).thenReturn(Optional.of(purchase));

        assertEquals(1, purchaseService.listByUserEmail("cliente@example.com").size());
        assertEquals("p100", purchaseService.getByIdForUser("cliente@example.com", "p100").getId());
    }

    @Test
    void updateStatus_changesAndPersistsStatus() {
        Purchase purchase = new Purchase("p200", "2", 90.0);
        PurchaseStatusUpdateRequest request = new PurchaseStatusUpdateRequest();
        request.setStatus("pagado");

        when(purchaseRepository.findById("p200")).thenReturn(Optional.of(purchase));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        Purchase updated = purchaseService.updateStatus("p200", request);
        assertEquals("PAGADO", updated.getStatus());
    }

    private static CheckoutRequest checkoutRequest(String productId, int qty, String size, String color,
                                                   String paymentMethod, String shippingAddress, String phone) {
        CheckoutItemRequest item = new CheckoutItemRequest();
        item.setProductId(productId);
        item.setQuantity(qty);
        item.setSize(size);
        item.setColor(color);

        CheckoutRequest request = new CheckoutRequest();
        request.setItems(java.util.List.of(item));
        request.setPaymentMethod(paymentMethod);
        request.setShippingAddress(shippingAddress);
        request.setContactPhone(phone);
        return request;
    }

    private static Product productWithVariantStock(String id, int totalStock, String size, String color, int variantStock) {
        Product product = new Product(id, "Con variante", 150.0, "/img", new Category("Hombre"), false, totalStock, 0, 4.5);
        ProductVariant variant = new ProductVariant(product, size, color, variantStock);
        product.addVariant(variant);
        return product;
    }

    private static User user(String email, Long id, String address) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setAddress(address);
        user.setRole("USER");
        return user;
    }
 
}
