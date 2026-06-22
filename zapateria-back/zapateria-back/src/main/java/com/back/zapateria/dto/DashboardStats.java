package com.back.zapateria.dto;

public class DashboardStats {
    private long users;
    private long products;
    private long sales;
    private double revenue;

    public DashboardStats(long users, long products, long sales, double revenue) {
        this.users = users;
        this.products = products;
        this.sales = sales;
        this.revenue = revenue;
    }

    public long getUsers() { return users; }
    public long getProducts() { return products; }
    public long getSales() { return sales; }
    public double getRevenue() { return revenue; }
}
