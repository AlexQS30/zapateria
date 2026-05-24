package com.back.zapateria.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
public class Product implements Serializable {

    @Id
    private String id;

    private String name;
    private double price;
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private int stock;
    private boolean isNew;
    private int discount; // porcentaje
    private double rating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC, id ASC")
    private List<ProductPhoto> photos = new ArrayList<>();

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
    @JsonIgnore
    public Category getCategory() { return category; }
    public boolean isNew() { return isNew; }
    public int getStock() { return stock; }
    public int getDiscount() { return discount; }
    public double getRating() { return rating; }
    public List<ProductVariant> getVariants() { return variants; }
    public List<ProductPhoto> getPhotos() { return photos; }

    @JsonProperty("gallery")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getGallery() {
        List<String> urls = new ArrayList<>();
        if (image != null && !image.isBlank()) {
            urls.add(image);
        }
        if (photos != null) {
            photos.stream()
                    .map(ProductPhoto::getImageUrl)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(url -> !url.isBlank())
                    .forEach(urls::add);
        }
        return urls.stream().distinct().toList();
    }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
    @JsonProperty("category")
    public void setCategory(Category category) { this.category = category; }

    @JsonProperty("categoryId")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }

    @JsonProperty("categoryId")
    public void setCategoryId(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        if (this.category == null) {
            this.category = new Category();
        }
        this.category.setId(categoryId);
    }

    public void setStock(int stock) { this.stock = stock; }
    public void setNew(boolean aNew) { isNew = aNew; }
    public void setDiscount(int discount) { this.discount = discount; }
    public void setRating(double rating) { this.rating = rating; }

    public void setVariants(List<ProductVariant> variants) {
        this.variants.clear();
        if (variants != null) {
            variants.forEach(this::addVariant);
        }
    }

    public void setPhotos(List<ProductPhoto> photos) {
        this.photos.clear();
        if (photos != null) {
            photos.forEach(this::addPhoto);
        }
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            return;
        }
        variant.setProduct(this);
        this.variants.add(variant);
    }

    public void clearVariants() {
        this.variants.clear();
    }

    public void addPhoto(ProductPhoto photo) {
        if (photo == null) {
            return;
        }
        photo.setProduct(this);
        this.photos.add(photo);
    }

    public void clearPhotos() {
        this.photos.clear();
    }
}