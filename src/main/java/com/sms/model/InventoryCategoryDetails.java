package com.sms.model;

import java.sql.Timestamp;

public class InventoryCategoryDetails {
    private int inventoryCategoryId;
    private int schoolId;
    private int sessionId;
    private String categoryDetails;
    private String categoryDescription;
    private int updatedBy;
    private Timestamp updateDateTime;

    public int getInventoryCategoryId() {
        return inventoryCategoryId;
    }

    public void setInventoryCategoryId(int inventoryCategoryId) {
        this.inventoryCategoryId = inventoryCategoryId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(String categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Timestamp updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
}
