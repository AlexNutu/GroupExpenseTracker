package com.example.expensetracker.domain;

public class ToDoObjectWithTrip {

    private Integer id;
    private String createDate;
    private String modifyDate;
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

    public ToDoObjectWithTrip(Integer id, String message, User user, Trip trip, String createDate, String modifyDate) {
        this.id = id;
        this.message = message;
        this.user = user;
        this.trip = trip;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}
