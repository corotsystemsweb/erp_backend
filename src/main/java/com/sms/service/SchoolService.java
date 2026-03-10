package com.sms.service;

import com.sms.model.SchoolDetails;
import com.sms.model.StaffDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SchoolService {
    public boolean addImage(MultipartFile file, String schoolCode, int schoolId) throws Exception;
    public SchoolDetails getImage(String schoolCode, int schoolId) throws Exception;
    public SchoolDetails addSchoolDetails(SchoolDetails schoolDetails, String schoolCode) throws Exception;
    public SchoolDetails getSchoolDetailsById(int schoolId, String schoolCode) throws Exception;
    public List<SchoolDetails> getAllSchoolDetails(String schoolCode) throws Exception;
    public SchoolDetails updateSchoolDetailsById(SchoolDetails schoolDetails, int schoolId, String schoolCode) throws Exception;
    public boolean deleteSchoolDetailsById(int schoolId, String schoolCode) throws Exception;
}
