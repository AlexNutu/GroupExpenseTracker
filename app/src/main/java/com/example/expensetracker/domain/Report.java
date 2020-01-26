package com.example.expensetracker.domain;

public class Report {

    private String expensiveType;
    private String product;
    private float sum;
    private String currency;
    private User user;
    private Integer tripId;

    public Report(String expensiveType, String product, float sum, String currency, User user) {
        this.expensiveType = expensiveType;
        this.product = product;
        this.sum = sum;
        this.currency = currency;
        this.user = user;
    }

    public String getExpensiveType() {
        return expensiveType;
    }

    public void setExpensiveType(String expensiveType) {
        this.expensiveType = expensiveType;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }
}
