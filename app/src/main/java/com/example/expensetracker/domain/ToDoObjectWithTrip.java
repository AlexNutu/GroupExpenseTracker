package com.example.expensetracker.domain;

public class ToDoObjectWithTrip {

    private Boolean approved;
    private String message;
    private User user;
    private Trip trip;

    public ToDoObjectWithTrip() {
    }

    public ToDoObjectWithTrip(Boolean approved, String message, User user, Trip trip) {
        this.approved = approved;
        this.message = message;
        this.user = user;
        this.trip = trip;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
