package com.sms.service.impl;

import com.sms.dao.DynamicTablesGeneratorDao;
import com.sms.service.DynamicTablesGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicTablesGeneratorServiceImpl implements DynamicTablesGeneratorService {
    @Autowired
    private DynamicTablesGeneratorDao dynamicTablesGeneratorDao;

    @Override
    public String executeDdl(String schoolCode) throws Exception {
        return dynamicTablesGeneratorDao.executeDdl(schoolCode);
    }
}
