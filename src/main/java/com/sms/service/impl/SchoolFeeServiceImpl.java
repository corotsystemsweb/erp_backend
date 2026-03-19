package com.sms.service.impl;

import com.sms.dao.SchoolFeeDao;
import com.sms.model.SchoolFeeDetails;
import com.sms.service.SchoolFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SchoolFeeServiceImpl implements SchoolFeeService {
    @Autowired
    private SchoolFeeDao schoolFeeDao;
    @Override
    public SchoolFeeDetails addSchoolFee(SchoolFeeDetails schoolFeeDetails, String schoolCode) throws Exception {
        return schoolFeeDao.addSchoolFee(schoolFeeDetails, schoolCode);
    }

    @Override
    public SchoolFeeDetails getSchoolFeeById(int feeId, String schoolCode) throws Exception {
        return schoolFeeDao.getSchoolFeeById(feeId, schoolCode);
    }

    @Override
    public List<SchoolFeeDetails> getAllSchoolFee(String schoolCode) throws Exception {
        return schoolFeeDao.getAllSchoolFee(schoolCode);
    }

    @Override
    public SchoolFeeDetails updateSchoolFeeById(SchoolFeeDetails schoolFeeDetails, int feeId, String schoolCode) throws Exception {
        return schoolFeeDao.updateSchoolFeeById(schoolFeeDetails, feeId, schoolCode);
    }

    @Override
    public boolean deleteSchoolFee(int feeId, String schoolCode) throws Exception {
        return schoolFeeDao.deleteSchoolFee(feeId, schoolCode);
    }
}
