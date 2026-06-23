package com.back.zapateria.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String userId;

    private int rating;

    private String comment;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean approved;

    private Instant createdAt;

    public Review() {}

    public Review(Product product, String userId, int rating, String comment) {
        this.product = product;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.approved = false;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public String getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public boolean isApproved() { return approved; }
    public Instant getCreatedAt() { return createdAt; }
    public void setProduct(Product product) { this.product = product; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
