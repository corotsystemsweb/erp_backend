package com.sms.model;

import java.util.Date;

public class AddVehicleDetails {
    private int vehicleId;
    private int schoolId;
    private String vehicleNumber;
    private String vehicleType;
    private int numberOfSeat;
    private double refuelAmount;
    private Date lastInsuranceDate;
    private Date renewalInsuranceDate;
    private Date lastServiceDate;

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getNumberOfSeat() {
        return numberOfSeat;
    }

    public void setNumberOfSeat(int numberOfSeat) {
        this.numberOfSeat = numberOfSeat;
    }

    public double getRefuelAmount() {
        return refuelAmount;
    }

    public void setRefuelAmount(double refuelAmount) {
        this.refuelAmount = refuelAmount;
    }

    public Date getLastInsuranceDate() {
        return lastInsuranceDate;
    }

    public void setLastInsuranceDate(Date lastInsuranceDate) {
        this.lastInsuranceDate = lastInsuranceDate;
    }

    public Date getRenewalInsuranceDate() {
        return renewalInsuranceDate;
    }

    public void setRenewalInsuranceDate(Date renewalInsuranceDate) {
        this.renewalInsuranceDate = renewalInsuranceDate;
    }

    public Date getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(Date lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }
}
