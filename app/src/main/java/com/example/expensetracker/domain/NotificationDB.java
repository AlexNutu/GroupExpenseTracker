package com.example.expensetracker.domain;

public class NotificationDB {

    private Long id;
    private String title;
    private String message;
    private String createDate;
    private String modifyDate;
    private Boolean sent;

    public NotificationDB() {
    }

    public NotificationDB(Long id, String title, String message, String createDate, String modifyDate, Boolean sent) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.sent = sent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }
}
