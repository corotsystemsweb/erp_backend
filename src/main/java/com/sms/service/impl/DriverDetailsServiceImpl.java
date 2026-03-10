package com.sms.service.impl;

import com.sms.dao.DriverDetailsDao;
import com.sms.model.DriverDetails;
import com.sms.service.DriverDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DriverDetailsServiceImpl implements DriverDetailsService {
    @Autowired
    private DriverDetailsDao driverDetailsDao;
    /*@Override
    public boolean addImage(MultipartFile file, int driverId) throws Exception {
        return driverDetailsDao.addImage(file, driverId);
    }

    @Override
    public DriverDetails getImage(int driverId) throws Exception {
        return driverDetailsDao.getImage(driverId);
    }*/

    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int driverId) throws Exception {
        return driverDetailsDao.addImage(file, schoolCode, driverId);
    }

    @Override
    public DriverDetails getImage(String schoolCode, int driverId) throws Exception {
        return driverDetailsDao.getImage(schoolCode, driverId);
    }

    @Override
    public DriverDetails addDriver(DriverDetails driverDetails, String schoolCode) throws Exception {
        return driverDetailsDao.addDriver(driverDetails, schoolCode);
    }

    @Override
    public DriverDetails getDriverById(int driverId, String schoolCode) throws Exception {
        return driverDetailsDao.getDriverById(driverId, schoolCode);
    }

    @Override
    public List<DriverDetails> getAllDriver(String schoolCode) throws Exception {
        return driverDetailsDao.getAllDriver(schoolCode);
    }

    @Override
    public DriverDetails updateDriverDetails(DriverDetails driverDetails, int driverId, String schoolCode) throws Exception {
        return driverDetailsDao.updateDriverDetails(driverDetails, driverId, schoolCode);
    }

    @Override
    public boolean deleteDriver(int driverId, String schoolCode) throws Exception {
        return driverDetailsDao.deleteDriver(driverId, schoolCode);
    }

    @Override
    public List<DriverDetails> getDriverDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return driverDetailsDao.getDriverDetailsBySearchText(searchText, schoolCode);
    }
}
