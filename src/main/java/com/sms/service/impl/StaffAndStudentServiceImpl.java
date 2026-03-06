package com.sms.service.impl;

import com.sms.dao.StaffAndStudentDao;
import com.sms.model.StaffAndStudentDetails;
import com.sms.service.StaffAndStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffAndStudentServiceImpl implements StaffAndStudentService {
    @Autowired
    private StaffAndStudentDao staffAndStudentDao;
    @Override
    public List<StaffAndStudentDetails> getUsersByPhoneNumberAndStatus(String phoneNumber) throws Exception {
        return staffAndStudentDao.getUsersByPhoneNumberAndStatus(phoneNumber);
    }
}
