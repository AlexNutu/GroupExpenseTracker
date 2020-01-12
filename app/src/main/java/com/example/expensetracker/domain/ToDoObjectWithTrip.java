package com.example.expensetracker.domain;

public class ToDoObjectWithTrip {

    private String message;
    private User user;
    private Trip trip;

    public ToDoObjectWithTrip() {
    }

    public ToDoObjectWithTrip(String message, User user, Trip trip) {
        this.message = message;
        this.user = user;
        this.trip = trip;
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
