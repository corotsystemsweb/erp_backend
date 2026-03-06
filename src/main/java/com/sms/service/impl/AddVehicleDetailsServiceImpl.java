package com.sms.service.impl;

import com.sms.dao.AddVehicleDetailsDao;
import com.sms.model.AddVehicleDetails;
import com.sms.service.AddVehicleDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddVehicleDetailsServiceImpl implements AddVehicleDetailsService {
    @Autowired
    private AddVehicleDetailsDao addVehicleDetailsDao;
    @Override
    public AddVehicleDetails addVehicle(AddVehicleDetails addVehicleDetails, String schoolCode) throws Exception {
        return addVehicleDetailsDao.addVehicle(addVehicleDetails, schoolCode);
    }

    @Override
    public AddVehicleDetails getVehicleById(int vehicleId, String schoolCode) throws Exception {
        return addVehicleDetailsDao.getVehicleById(vehicleId, schoolCode);
    }

    @Override
    public List<AddVehicleDetails> getAllVehicle(String schoolCode) throws Exception {
        return addVehicleDetailsDao.getAllVehicle(schoolCode);
    }

    @Override
    public AddVehicleDetails updateVehicle(AddVehicleDetails addVehicleDetails, int vehicleId, String schoolCode) throws Exception {
        return addVehicleDetailsDao.updateVehicle(addVehicleDetails, vehicleId, schoolCode);
    }

    @Override
    public boolean deleteVehicle(int vehicleId, String schoolCode) throws Exception {
        return addVehicleDetailsDao.deleteVehicle(vehicleId, schoolCode);
    }

    @Override
    public List<AddVehicleDetails> getAllVehicleNumber(String schoolCode) throws Exception {
        return addVehicleDetailsDao.getAllVehicleNumber(schoolCode);
    }

    @Override
    public List<AddVehicleDetails> getVehicleDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return addVehicleDetailsDao.getVehicleDetailsBySearchText(searchText, schoolCode);
    }
}
