package com.example.expensetracker.domain;

public class User {
    private Long id;
    private String createDate;
    private String modifyDate;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User() {
    }

    public User(Long id, String createDate, String modifyDate, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getFirstLastName(){ return this.firstName + " " + this.lastName;}
}
