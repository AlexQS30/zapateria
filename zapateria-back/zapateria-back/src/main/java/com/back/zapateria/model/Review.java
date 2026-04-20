package com.back.zapateria.model;

import jakarta.persistence.*;
import java.time.Instant;

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

    private Instant createdAt;

    public Review() {}

    public Review(Product product, String userId, int rating, String comment) {
        this.product = product;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public String getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}
