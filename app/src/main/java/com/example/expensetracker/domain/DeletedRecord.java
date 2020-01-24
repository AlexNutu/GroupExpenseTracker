package com.example.expensetracker.domain;

public class DeletedRecord {
    Integer id;
    Integer recordId;
    String tableName;
    String createDate;
    String modifyDate;

    public DeletedRecord(){};

    public DeletedRecord(Integer id, Integer recordId, String tableName, String createDate, String modifyDate) {
        this.id = id;
        this.recordId = recordId;
        this.tableName = tableName;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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
