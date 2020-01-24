package com.example.expensetracker.domain;

import java.util.List;

public class Trip {

    private Integer id;
    private String name;
    private String destination;
    private String startDate;
    private String endDate;
    private String createDate;
    private String modifyDate;
    private List<User> members;
    private Integer status;


    public Trip() {
    }

    public Trip(Integer id, String name, String destination, String startDate, String endDate, String createDate, String modifyDate, List<User> members, Integer status) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.members = members;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }
}
