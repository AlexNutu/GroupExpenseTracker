package com.example.expensetracker.domain;

public class Expense {

    private String expensiveType;
    private String product;
    private float sum;
    private String currency;
    private User user;
    private Trip trip;

    public Expense() {
    }

    public Expense(String expensiveType, String product, float sum, String currency, User user, Trip trip) {
        this.expensiveType = expensiveType;
        this.product = product;
        this.sum = sum;
        this.currency = currency;
        this.user = user;
        this.trip = trip;
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

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
