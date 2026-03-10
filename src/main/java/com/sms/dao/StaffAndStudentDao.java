package com.sms.dao;

import com.sms.model.StaffAndStudentDetails;

import java.util.List;

public interface StaffAndStudentDao {
    public List<StaffAndStudentDetails> getUsersByPhoneNumberAndStatus(String phoneNumber) throws Exception;
}
