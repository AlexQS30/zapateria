package com.back.zapateria.dto;

import java.util.ArrayList;
import java.util.List;

public class CheckoutRequest {
    private List<CheckoutItemRequest> items = new ArrayList<>();
    private String paymentMethod;
    private String shippingAddress;
    private String contactPhone;

    public List<CheckoutItemRequest> getItems() { return items; }
    public void setItems(List<CheckoutItemRequest> items) { this.items = items; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
}