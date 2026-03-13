package com.sms.model;

import java.util.List;

public class StudentFeeCollectionDetails {
    private int schoolId;
    private String schoolName;
    private String schoolBuilding;
    private String schoolAddress;
    private String emailAddress;
    private String schoolCity;
    private String schoolState;
    private String schoolCountry;
    private String schoolZipcode;
    private int studentId;
    private String studentName;
    private int classId;
    private String className;
    private int sectionId;
    private String sectionName;
    private Double totalFeeAssigned;
    private Double totalDiscount;
    private Double grossStudentFee;
    private List<String> feeDetails;
    private int sessionId;
    private String SessionName;

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolBuilding() {
        return schoolBuilding;
    }

    public void setSchoolBuilding(String schoolBuilding) {
        this.schoolBuilding = schoolBuilding;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSchoolCity() {
        return schoolCity;
    }

    public void setSchoolCity(String schoolCity) {
        this.schoolCity = schoolCity;
    }

    public String getSchoolState() {
        return schoolState;
    }

    public void setSchoolState(String schoolState) {
        this.schoolState = schoolState;
    }

    public String getSchoolCountry() {
        return schoolCountry;
    }

    public void setSchoolCountry(String schoolCountry) {
        this.schoolCountry = schoolCountry;
    }

    public String getSchoolZipcode() {
        return schoolZipcode;
    }

    public void setSchoolZipcode(String schoolZipcode) {
        this.schoolZipcode = schoolZipcode;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Double getTotalFeeAssigned() {
        return totalFeeAssigned;
    }

    public void setTotalFeeAssigned(Double totalFeeAssigned) {
        this.totalFeeAssigned = totalFeeAssigned;
    }

    public Double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Double getGrossStudentFee() {
        return grossStudentFee;
    }

    public void setGrossStudentFee(Double grossStudentFee) {
        this.grossStudentFee = grossStudentFee;
    }

    public List<String> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(List<String> feeDetails) {
        this.feeDetails = feeDetails;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return SessionName;
    }

    public void setSessionName(String sessionName) {
        SessionName = sessionName;
    }
}
