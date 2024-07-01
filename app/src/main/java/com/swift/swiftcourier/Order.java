package com.swift.swiftcourier;


public class Order {
    private String orderId;
    private String userId;
    private String description;
    private String status;
    private String courierId;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String orderId, String userId, String description, String status, String courierId) {
        this.orderId = orderId;
        this.userId = userId;
        this.description = description;
        this.status = status;
        this.courierId = courierId;
    }

    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }
}
