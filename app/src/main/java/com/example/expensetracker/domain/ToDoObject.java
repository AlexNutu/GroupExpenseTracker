package com.example.expensetracker.domain;

public class ToDoObject {

    private long idNote;
    private Boolean approved;
    private String message;
    private User user;
    private String createDate;

    public ToDoObject() {
    }

    public ToDoObject(long idNote, Boolean approved, String message, User user, String createDate) {
        this.idNote = idNote;
        this.approved = approved;
        this.message = message;
        this.user = user;
        this.createDate = createDate;
    }

    public long getIdNote() {
        return idNote;
    }

    public void setIdNote(long idNote) {
        this.idNote = idNote;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
