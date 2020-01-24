package com.example.expensetracker.domain;

import java.io.Serializable;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class User implements Serializable {
    private Long id;
    private String createDate;
    private String modifyDate;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean receiveNotifications;
    private String errorMessage;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password, Boolean receiveNotifications, String errorMessage) {
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.receiveNotifications = receiveNotifications;
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(Boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFirstLastName(){ return this.firstName + " " + this.lastName;}
}
