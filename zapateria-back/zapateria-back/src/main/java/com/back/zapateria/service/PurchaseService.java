package com.back.zapateria.service;

import com.back.zapateria.dto.CheckoutItemRequest;
import com.back.zapateria.dto.CheckoutRequest;
import com.back.zapateria.dto.PurchaseStatusUpdateRequest;
import com.back.zapateria.model.PaymentMethod;
import com.back.zapateria.model.Purchase;
import com.back.zapateria.model.PurchaseItem;
import com.back.zapateria.model.Product;
import com.back.zapateria.model.ProductVariant;
import com.back.zapateria.model.PurchaseStatus;
import com.back.zapateria.model.User;
import com.back.zapateria.repository.PurchaseRepository;
import com.back.zapateria.repository.ProductRepository;
import com.back.zapateria.repository.ProductVariantRepository;
import com.back.zapateria.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PurchaseService {

    @Autowired(required = false)
    private PurchaseRepository purchaseRepository;

    @Autowired(required = false)
    private ProductRepository productRepository;

    @Autowired(required = false)
    private ProductVariantRepository productVariantRepository;

    @Autowired(required = false)
    private UserRepository userRepository;

    public Purchase createPurchase(String userId, java.util.Map<String,Integer> items, double total) {
        String id = UUID.randomUUID().toString();
        Purchase p = new Purchase(id, userId, total);
        if (items != null && productRepository != null) {
            for (Map.Entry<String,Integer> e : items.entrySet()) {
                productRepository.findById(e.getKey()).ifPresent(prod -> {
                    PurchaseItem it = new PurchaseItem(prod, e.getValue());
                    p.addItem(it);
                });
            }
        }
        if (purchaseRepository != null) return purchaseRepository.save(p);
        return p;
    }

    public Purchase checkout(String userEmail, CheckoutRequest request) {
        User user = requireUser(userEmail);
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        String purchaseId = UUID.randomUUID().toString();
        String paymentMethod = normalizePaymentMethod(request.getPaymentMethod());
        String shippingAddress = request.getShippingAddress();
        if (shippingAddress == null || shippingAddress.isBlank()) {
            shippingAddress = user.getAddress();
        }

        Purchase purchase = new Purchase(purchaseId, String.valueOf(user.getId()), 0.0, paymentMethod, shippingAddress, request.getContactPhone());

        double total = 0.0;
        for (CheckoutItemRequest itemRequest : request.getItems()) {
            if (itemRequest == null || itemRequest.getProductId() == null) {
                continue;
            }

            Product product = productRepository != null
                    ? productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + itemRequest.getProductId()))
                    : null;

            if (product == null) {
                continue;
            }

            int quantity = Math.max(1, itemRequest.getQuantity());

            if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                if (itemRequest.getSize() == null || itemRequest.getSize().isBlank() || itemRequest.getColor() == null || itemRequest.getColor().isBlank()) {
                    throw new IllegalArgumentException("Debes seleccionar talla y color para este producto");
                }

                ProductVariant variant = productVariantRepository != null
                        ? productVariantRepository.findByProduct_IdAndSizeIgnoreCaseAndColorIgnoreCase(product.getId(), itemRequest.getSize(), itemRequest.getColor())
                        .orElseThrow(() -> new EntityNotFoundException("Variante no encontrada para el producto: " + itemRequest.getProductId()))
                        : null;

                if (variant != null) {
                    if (variant.getStock() < quantity) {
                        throw new IllegalStateException("No hay stock suficiente para la talla y color seleccionados");
                    }
                    variant.setStock(variant.getStock() - quantity);
                    product.setStock(Math.max(0, product.getStock() - quantity));
                    if (productVariantRepository != null) {
                        productVariantRepository.save(variant);
                    }
                    if (productRepository != null) {
                        productRepository.save(product);
                    }
                }
            } else if (product.getStock() < quantity) {
                throw new IllegalStateException("No hay stock suficiente para este producto");
            } else {
                product.setStock(product.getStock() - quantity);
                if (productRepository != null) {
                    productRepository.save(product);
                }
            }

            PurchaseItem item = new PurchaseItem(product, quantity, itemRequest.getSize(), itemRequest.getColor());
            purchase.addItem(item);
            total += product.getPrice() * quantity;
        }

        purchase.setTotal(total);
        purchase.setStatus(PurchaseStatus.REGISTRADO.name());

        return purchaseRepository != null ? purchaseRepository.save(purchase) : purchase;
    }

    public List<Purchase> listByUser(String userId) {
        if (purchaseRepository != null) return purchaseRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return List.of();
    }

    public List<Purchase> listAll() {
        if (purchaseRepository != null) return purchaseRepository.findAll();
        return List.of();
    }

    public List<Purchase> listByUserEmail(String userEmail) {
        User user = requireUser(userEmail);
        return listByUser(String.valueOf(user.getId()));
    }

    public Purchase getByIdForUser(String userEmail, String purchaseId) {
        User user = requireUser(userEmail);
        if (purchaseRepository == null) {
            throw new EntityNotFoundException("Repositorio de compras no disponible");
        }

        return purchaseRepository.findByIdAndUserId(purchaseId, String.valueOf(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada"));
    }

    public Purchase updateStatus(String purchaseId, PurchaseStatusUpdateRequest request) {
        if (purchaseRepository == null) {
            throw new EntityNotFoundException("Repositorio de compras no disponible");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada"));

        PurchaseStatus status = PurchaseStatus.valueOf((request.getStatus() == null ? "" : request.getStatus()).trim().toUpperCase());
        purchase.setStatus(status.name());
        return purchaseRepository.save(purchase);
    }

    private User requireUser(String userEmail) {
        if (userRepository == null) {
            throw new EntityNotFoundException("Repositorio de usuarios no disponible");
        }
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return PaymentMethod.EFECTIVO.name();
        }

        String normalized = paymentMethod.trim().toUpperCase().replace(' ', '_');
        return switch (normalized) {
            case "TARJETA", "TARJETA_CREDITO", "CREDITO", "CREDITO_TARJETA" -> PaymentMethod.TARJETA_CREDITO.name();
            case "YAPE" -> PaymentMethod.YAPE.name();
            case "EFECTIVO", "CASH" -> PaymentMethod.EFECTIVO.name();
            default -> PaymentMethod.EFECTIVO.name();
        };
    }
}
