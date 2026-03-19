package com.sms.dao;

import com.sms.model.FrequencyDetails;
import com.sms.model.SchoolFeeDetails;

import java.util.List;

public interface SchoolFeeDao {
    public SchoolFeeDetails addSchoolFee(SchoolFeeDetails schoolFeeDetails, String schoolCode) throws Exception;
    public SchoolFeeDetails getSchoolFeeById(int feeId, String schoolCode) throws Exception;
    public List<SchoolFeeDetails> getAllSchoolFee(String schoolCode) throws Exception;
    public SchoolFeeDetails updateSchoolFeeById(SchoolFeeDetails schoolFeeDetails, int feeId, String schoolCode) throws Exception;
    public boolean deleteSchoolFee(int feeId, String schoolCode) throws Exception;
}
