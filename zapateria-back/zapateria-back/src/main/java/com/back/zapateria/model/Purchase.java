package com.back.zapateria.model;

import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    public Purchase() {}

    public Purchase(String id, String userId, double total) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public double getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public List<PurchaseItem> getItems() { return items; }

    public void addItem(PurchaseItem item) {
        items.add(item);
        item.setPurchase(this);
    }
}
