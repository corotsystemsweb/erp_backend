package com.sms.service;

import com.sms.model.StaffAndStudentDetails;

import java.util.List;

public interface StaffAndStudentService {
    public List<StaffAndStudentDetails> getUsersByPhoneNumberAndStatus(String phoneNumber) throws Exception;
}
