package com.sms.service.impl;

import com.sms.dao.FeeDueDateDao;
import com.sms.model.FeeDueDateDetails;
import com.sms.service.FeeDueDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeDueDateServiceImpl implements FeeDueDateService {
    @Autowired
    private FeeDueDateDao feeDueDateDao;
    @Override
    public FeeDueDateDetails addFeeDueDetails(FeeDueDateDetails feeDueDateDetails, String schoolCode) throws Exception {
        return feeDueDateDao.addFeeDueDetails(feeDueDateDetails, schoolCode);
    }

    @Override
    public List<FeeDueDateDetails> addFeeDueDetailsList(List<FeeDueDateDetails> feeDueDateDetailsList, String schoolCode) throws Exception {
        return feeDueDateDao.addFeeDueDetailsList(feeDueDateDetailsList, schoolCode);
    }

    @Override
    public List<FeeDueDateDetails> getExactFee(int sessionId, String schoolCode) throws Exception {
        return feeDueDateDao.getExactFee(sessionId, schoolCode);
    }

    @Override
    public void updateFeeDueDate(List<FeeDueDateDetails> feeDueDateDetailsList, int faId, String schoolCode) throws Exception {
        feeDueDateDao.updateFeeDueDate(feeDueDateDetailsList, faId, schoolCode);
    }
}
