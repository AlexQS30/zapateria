package com.back.zapateria.dto;

import java.util.List;

public class DashboardAnalytics {
    private List<Object[]> topBuyers;
    private List<Object[]> topProducts;
    private String peakMonth;

    public DashboardAnalytics(List<Object[]> topBuyers, List<Object[]> topProducts, String peakMonth) {
        this.topBuyers = topBuyers;
        this.topProducts = topProducts;
        this.peakMonth = peakMonth;
    }

    public List<Object[]> getTopBuyers() { return topBuyers; }
    public List<Object[]> getTopProducts() { return topProducts; }
    public String getPeakMonth() { return peakMonth; }
}
