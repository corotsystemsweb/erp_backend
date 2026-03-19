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


//    ---------------------------------------added by karan--------------------------

    private String model;
    private String manufacturer;
    private int yearOfManufacture;
    private Date registrationDate;
    private Date fitnessExpiry;
    private String status;


    // getters & setters for all 6
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public int getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(int yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public Date getFitnessExpiry() { return fitnessExpiry; }
    public void setFitnessExpiry(Date fitnessExpiry) { this.fitnessExpiry = fitnessExpiry; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }





//---------------------------------------------------------------------ended here ----------------------------------------






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
