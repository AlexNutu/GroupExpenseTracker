package com.example.expensetracker.domain;

public class ToDoObjectWithTrip {

    private Long id;
    private Boolean approved;
    private String message;
    private User user;
    private Trip trip;
    private String createDate;
    private String modifyDate;
    private Integer status;

    public ToDoObjectWithTrip() {
    }

    public ToDoObjectWithTrip(Long id, Boolean approved, String message, User user, Trip trip, String createDate, String modifyDate) {
        this.id = id;
        this.approved = approved;
        this.message = message;
        this.user = user;
        this.trip = trip;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }
}
