package com.sms.service;

import com.sms.model.AddVehicleDetails;

import java.util.List;

public interface AddVehicleDetailsService {
    public AddVehicleDetails addVehicle(AddVehicleDetails addVehicleDetails, String schoolCode) throws Exception;
    public AddVehicleDetails getVehicleById(int vehicleId, String schoolCode) throws Exception;
    public List<AddVehicleDetails> getAllVehicle(String schoolCode) throws Exception;
    public AddVehicleDetails updateVehicle(AddVehicleDetails addVehicleDetails, int vehicleId, String schoolCode) throws Exception;
    public boolean deleteVehicle(int vehicleId, String schoolCode) throws Exception;
    public List<AddVehicleDetails> getAllVehicleNumber(String schoolCode) throws Exception;
    public List<AddVehicleDetails> getVehicleDetailsBySearchText(String searchText, String schoolCode) throws Exception;
}
