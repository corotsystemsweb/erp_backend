package com.sms.service.impl;

import com.sms.dao.ManageFeeTypeDao;
import com.sms.model.ManageFeeTypeDetails;
import com.sms.service.ManageFeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageFeeTypeServiceImpl implements ManageFeeTypeService {

    @Autowired
    private ManageFeeTypeDao manageFeeTypeDao;


    @Override
    public List<ManageFeeTypeDetails> getManageFeeType(int sessionId, String schoolCode) throws Exception {
        return manageFeeTypeDao.getManageFeeType(sessionId, schoolCode);
    }
}
