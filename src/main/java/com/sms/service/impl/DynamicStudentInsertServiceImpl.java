package com.sms.service.impl;

import com.sms.dao.DynamicStudentInsertDao;
import com.sms.service.DynamicStudentInsertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicStudentInsertServiceImpl implements DynamicStudentInsertService {
    @Autowired
    private DynamicStudentInsertDao dynamicStudentInsertDao;

    @Override
    public String executeDdlForStudentInsert(String schoolCode) throws Exception {
        return dynamicStudentInsertDao.executeDdlForStudentInsert(schoolCode);
    }
}
