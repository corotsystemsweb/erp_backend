package com.sms.model;

import java.util.Date;

public class FuelExpenseDetails {
    private int fuelExpenseId;
    private int schoolId;
    private int sessionId;
    private int vehicleId;
    private String vehicleNumber;
    private Date fuelDate;
    private double fuelLiter;
    private double totalFuelLiter;
    private double refuelAmount;
    private double totalRefuelAmount;
    private int updatedBy;
    private Date updatedDateTime;

    public int getFuelExpenseId() {
        return fuelExpenseId;
    }

    public void setFuelExpenseId(int fuelExpenseId) {
        this.fuelExpenseId = fuelExpenseId;
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

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getFuelDate() {
        return fuelDate;
    }

    public void setFuelDate(Date fuelDate) {
        this.fuelDate = fuelDate;
    }

    public double getFuelLiter() {
        return fuelLiter;
    }

    public void setFuelLiter(double fuelLiter) {
        this.fuelLiter = fuelLiter;
    }

    public double getTotalFuelLiter() {
        return totalFuelLiter;
    }

    public void setTotalFuelLiter(double totalFuelLiter) {
        this.totalFuelLiter = totalFuelLiter;
    }

    public double getRefuelAmount() {
        return refuelAmount;
    }

    public void setRefuelAmount(double refuelAmount) {
        this.refuelAmount = refuelAmount;
    }

    public double getTotalRefuelAmount() {
        return totalRefuelAmount;
    }

    public void setTotalRefuelAmount(double totalRefuelAmount) {
        this.totalRefuelAmount = totalRefuelAmount;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Date updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
