package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Date;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class BankDetails {

    private int bdId;
    private int schoolId;
    private int staffId;
    private String staffName;
    private String phoneNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String permanentAccountNumber;
    private String branchName;
    private String bankAddress;
    private int updatedBy;
    private Date updateDateTime;
    private boolean deleted;


    public int getBdId() {
        return bdId;
    }

    public void setBdId(int bdId) {
        this.bdId = bdId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getPermanentAccountNumber() {
        return permanentAccountNumber;
    }

    public void setPermanentAccountNumber(String permanentAccountNumber) {
        this.permanentAccountNumber = permanentAccountNumber;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
