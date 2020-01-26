package com.example.expensetracker.domain;

public class Expense {

    private Long id;
    private String expensiveType;
    private String product;
    private float sum;
    private String currency;
    private float percent;
    private User user;
    private Trip trip;
    private String createDate;
    private String modifyDate;
    private Integer status;

    private String errorMessage;

    public Expense() {
    }

    public Expense(String expensiveType, String product, float sum, String currency, float percent, User user, Trip trip) {
        this.expensiveType = expensiveType;
        this.product = product;
        this.sum = sum;
        this.currency = currency;
        this.percent = percent;
        this.user = user;
        this.trip = trip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCreateDate() { return createDate; }

    public void setCreateDate(String createDate) { this.createDate = createDate; }

    public String getModifyDate() { return modifyDate; }

    public void setModifyDate(String modifyDate) { this.modifyDate = modifyDate; }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }
}
