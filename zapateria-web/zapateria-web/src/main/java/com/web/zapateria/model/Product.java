package com.web.zapateria.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private int discount;
    private boolean isNew;
    private double rating;
    private int reviews;

    public Product(String id, String name, String description, double price, 
                   String imageUrl, String category, int discount, boolean isNew, 
                   double rating, int reviews) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.discount = discount;
        this.isNew = isNew;
        this.rating = rating;
        this.reviews = reviews;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public int getDiscount() { return discount; }
    public boolean isNew() { return isNew; }
    public double getRating() { return rating; }
    public int getReviews() { return reviews; }
    public double getFinalPrice() { return price * (1 - discount / 100.0); }
}
