package com.sms.service.impl;

import com.sms.dao.DdlForAddStudentDao;
import com.sms.service.DdlForAddStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DdlForAddStudentServiceImpl implements DdlForAddStudentService {
    @Autowired
    private DdlForAddStudentDao ddlForAddStudentDao;

    @Override
    public String addDdlForStudentDetails(String schoolCode) throws Exception {
        return ddlForAddStudentDao.addDdlForStudentDetails(schoolCode);
    }
}
