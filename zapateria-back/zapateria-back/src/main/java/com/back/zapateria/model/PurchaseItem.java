package com.back.zapateria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    @JsonIgnore
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private String size;

    private String color;

    @Column(name = "unit_price")
    private double unitPrice;

    public PurchaseItem() {}

    public PurchaseItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product != null ? product.getPrice() : 0.0;
    }

    public PurchaseItem(Product product, int quantity, String size, String color) {
        this(product, quantity);
        this.size = size;
        this.color = color;
    }

    public Long getId() { return id; }
    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public double getUnitPrice() { return unitPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
