package com.sms.service;

import com.sms.model.StaffDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StaffService {

    //public boolean addImage(MultipartFile file, int staffId) throws Exception;
    //public StaffDetails getImage(int staffId) throws Exception;
    public boolean addImage(MultipartFile file, String schoolCode, int staffId) throws Exception;
    public StaffDetails getImage(String schoolCode, int staffId) throws Exception;

    public StaffDetails addStaffOneBYOne(StaffDetails staffDetails,String schoolCode) throws Exception;

    public StaffDetails getStaffDetailsById(int staffId,String schoolCode) throws Exception;

    public List<StaffDetails> getAllStaffDetails(String type, String schoolCode) throws Exception;
    public StaffDetails updateStaffById(StaffDetails staffDetails,int staffId,String schoolCode) throws Exception;
    public boolean softDeleteStaff(int staffId,String schoolCode) throws Exception;
    public int getTotalStaff(String schoolCode) throws Exception;
    public List<StaffDetails> totalTeacher(String schoolCode, String staffType, String filter) throws Exception;
    public StaffDetails getStaffId(String staffName,String schoolCode) throws Exception;
    public List<StaffDetails> getStaffByDesignation(int designationId,String schoolCode) throws  Exception;
    public List<StaffDetails> getSalaryByDesignation(int designationId, String schoolCode) throws Exception;
    public List<StaffDetails> getStaffDetailsBySearchText(String searchText, String schoolCode) throws Exception;
    public List<StaffDetails> getAllStaffForIdCardGeneration(String schoolCode) throws Exception;
    public StaffDetails getStaffByIdForIdCardGeneration(int staffId, String schoolCode) throws Exception;


}
