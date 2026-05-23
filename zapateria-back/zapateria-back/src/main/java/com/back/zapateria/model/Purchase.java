package com.back.zapateria.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Purchase {

    @Id
    private String id;

    private String userId;

    private double total;

    private Instant createdAt;

    private String status = PurchaseStatus.REGISTRADO.name();

    @Column(name = "payment_method")
    private String paymentMethod = PaymentMethod.EFECTIVO.name();

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "contact_phone")
    private String contactPhone;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    public Purchase() {}

    public Purchase(String id, String userId, double total) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.createdAt = Instant.now();
    }

    public Purchase(String id, String userId, double total, String paymentMethod, String shippingAddress, String contactPhone) {
        this(id, userId, total);
        if (paymentMethod != null && !paymentMethod.isBlank()) {
            this.paymentMethod = paymentMethod;
        }
        this.shippingAddress = shippingAddress;
        this.contactPhone = contactPhone;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public double getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getShippingAddress() { return shippingAddress; }
    public String getContactPhone() { return contactPhone; }
    public List<PurchaseItem> getItems() { return items; }

    public void addItem(PurchaseItem item) {
        items.add(item);
        item.setPurchase(this);
    }

    public void setTotal(double total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
}
