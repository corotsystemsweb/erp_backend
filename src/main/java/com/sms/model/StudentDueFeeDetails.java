package com.sms.model;

import java.util.Date;
import java.util.List;

public class StudentDueFeeDetails {
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
    private double grossFee;
    private double paidAmount;
    private double dueFee;
    private int sessionId;
    private String sessionName;
    private List<String> feeDetails;
    private Double totalFeeAssigned;
    private Double totalDiscount;
    private String currentDate;
    private List<String> totalAmountPaidList;

    public StudentDueFeeDetails(int schoolId, String schoolName, String schoolBuilding, String schoolAddress, String emailAddress, String schoolCity, String schoolState, String schoolCountry, String schoolZipcode, int studentId, String studentName, int classId, String className, int sectionId, String sectionName, double grossFee, double paidAmount, double dueFee, int sessionId, String sessionName, List<String> feeDetails, Double totalFeeAssigned, Double totalDiscount, String currentDate) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.schoolBuilding = schoolBuilding;
        this.schoolAddress = schoolAddress;
        this.emailAddress = emailAddress;
        this.schoolCity = schoolCity;
        this.schoolState = schoolState;
        this.schoolCountry = schoolCountry;
        this.schoolZipcode = schoolZipcode;
        this.studentId = studentId;
        this.studentName = studentName;
        this.classId = classId;
        this.className = className;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.grossFee = grossFee;
        this.paidAmount = paidAmount;
        this.dueFee = dueFee;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.feeDetails = feeDetails;
        this.totalFeeAssigned = totalFeeAssigned;
        this.totalDiscount = totalDiscount;
        this.currentDate = currentDate;
    }

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public double getGrossFee() {
        return grossFee;
    }

    public void setGrossFee(double grossFee) {
        this.grossFee = grossFee;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDueFee() {
        return dueFee;
    }

    public void setDueFee(double dueFee) {
        this.dueFee = dueFee;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public List<String> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(List<String> feeDetails) {
        this.feeDetails = feeDetails;
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

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public List<String> getTotalAmountPaidList() {
        return totalAmountPaidList;
    }

    public void setTotalAmountPaidList(List<String> totalAmountPaidList) {
        this.totalAmountPaidList = totalAmountPaidList;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
