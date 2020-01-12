package com.example.expensetracker.domain;

public class ToDoObject {

    private String message;
    private User user;
    private String createDate;

    public ToDoObject() {
    }

    public ToDoObject(String message, User user, String createDate) {
        this.message = message;
        this.user = user;
        this.createDate = createDate;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
