package com.sms.service.impl;

import com.sms.dao.MasterSequenceDetailsDao;
import com.sms.service.MasterSequenceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterSequenceDetailsServiceImpl implements MasterSequenceDetailsService {
    @Autowired
    private MasterSequenceDetailsDao masterSequenceDetailsDao;
    @Override
    public void addSeqCodeAndCurrentValue(String schoolCode) throws Exception{
        masterSequenceDetailsDao.addSeqCodeAndCurrentValue(schoolCode);
    }
}
