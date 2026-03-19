package com.sms.service.impl;

import com.sms.dao.FrequencyDao;
import com.sms.model.FrequencyDetails;
import com.sms.service.FrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrequencyServiceImpl implements FrequencyService {
    @Autowired
    private FrequencyDao frequencyDao;

    @Override
    public FrequencyDetails addFrequency(FrequencyDetails frequencyDetails, String schoolCode) throws Exception {
        return frequencyDao.addFrequency(frequencyDetails, schoolCode);
    }

    @Override
    public FrequencyDetails getFrequencyDetailsById(int frequencyId, String schoolCode) throws Exception {
        return frequencyDao.getFrequencyDetailsById(frequencyId, schoolCode);
    }

    @Override
    public List<FrequencyDetails> getAllFrequencyDetails(String schoolCode) throws Exception {
        return frequencyDao.getAllFrequencyDetails(schoolCode);
    }

    @Override
    public FrequencyDetails updateFrequencyDetailsById(FrequencyDetails frequencyDetails, int frequencyId, String schoolCode) throws Exception {
        return frequencyDao.updateFrequencyDetailsById(frequencyDetails, frequencyId, schoolCode);
    }

    @Override
    public boolean deleteFrequency(int frequencyId, String schoolCode) throws Exception {
        return frequencyDao.deleteFrequency(frequencyId, schoolCode);
    }
}
