package com.back.zapateria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Product implements Serializable {

    @Id
    private String id;

    private String name;
    private double price;
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    private int stock;
    private boolean isNew;
    private int discount; // porcentaje
    private double rating;

    public Product() {}

    public Product(String id, String name, double price, String image,
                   Category category, boolean isNew, int stock, int discount, double rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.stock = stock;
        this.isNew = isNew;
        this.discount = discount;
        this.rating = rating;
    }

    // Backwards-compatible constructor used by tests and DTOs that supply category name
    public Product(String id, String name, double price, String image,
                   String categoryName, boolean isNew, int stock, int discount, double rating) {
        this(id, name, price, image, categoryName == null ? null : new Category(categoryName), isNew, stock, discount, rating);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public Category getCategory() { return category; }
    public boolean isNew() { return isNew; }
    public int getStock() { return stock; }
    public int getDiscount() { return discount; }
    public double getRating() { return rating; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
    public void setCategory(Category category) { this.category = category; }

    @JsonProperty("categoryId")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }
    public void setStock(int stock) { this.stock = stock; }
    public void setNew(boolean aNew) { isNew = aNew; }
    public void setDiscount(int discount) { this.discount = discount; }
    public void setRating(double rating) { this.rating = rating; }
}