package com.sms.dao;

import com.sms.model.DriverDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DriverDetailsDao {
    //public boolean addImage(MultipartFile file, int driverId) throws Exception;
    //public DriverDetails getImage(int driverId) throws Exception;
    public boolean addImage(MultipartFile file, String schoolCode, int driverId) throws Exception;
    public DriverDetails getImage(String schoolCode, int driverId) throws Exception;
    public DriverDetails addDriver(DriverDetails driverDetails, String schoolCode) throws Exception;
    public DriverDetails getDriverById(int driverId, String schoolCode) throws Exception;
    public List<DriverDetails> getAllDriver(String schoolCode) throws Exception;
    public DriverDetails updateDriverDetails(DriverDetails driverDetails, int driverId, String schoolCode) throws Exception;
    public boolean deleteDriver(int driverId, String schoolCode) throws Exception;
    public List<DriverDetails> getDriverDetailsBySearchText(String searchText, String schoolCode) throws Exception;
}
