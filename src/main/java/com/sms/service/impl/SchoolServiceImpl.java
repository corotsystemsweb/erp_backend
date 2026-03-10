package com.sms.service.impl;

import com.sms.dao.SchoolDao;
import com.sms.model.SchoolDetails;
import com.sms.model.StaffDetails;
import com.sms.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {
    @Autowired
    private SchoolDao schoolDao;

    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int schoolId) throws Exception {
        return schoolDao.addImage(file, schoolCode, schoolId);
    }

    @Override
    public SchoolDetails getImage(String schoolCode, int schoolId) throws Exception {
        return schoolDao.getImage(schoolCode, schoolId);
    }

    @Override
    public SchoolDetails addSchoolDetails(SchoolDetails schoolDetails, String schoolCode) throws Exception {
        return schoolDao.addSchoolDetails(schoolDetails, schoolCode);
    }

    @Override
    public SchoolDetails getSchoolDetailsById(int schoolId, String schoolCode) throws Exception {
        return schoolDao.getSchoolDetailsById(schoolId, schoolCode);
    }

    @Override
    public List<SchoolDetails> getAllSchoolDetails(String schoolCode) throws Exception {
        return schoolDao.getAllSchoolDetails(schoolCode);
    }

    @Override
    public SchoolDetails updateSchoolDetailsById(SchoolDetails schoolDetails, int schoolId, String schoolCode) throws Exception {
        return schoolDao.updateSchoolDetailsById(schoolDetails, schoolId, schoolCode);
    }

    @Override
    public boolean deleteSchoolDetailsById(int schoolId, String schoolCode) throws Exception {
        return schoolDao.deleteSchoolDetailsById(schoolId, schoolCode);
    }
}
