package com.sms.service.impl;

import com.sms.dao.PenaltyDetailsDao;
import com.sms.model.PenaltyDetails;
import com.sms.service.PenaltyDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenaltyDetailsServiceImpl implements PenaltyDetailsService {
    @Autowired
    private PenaltyDetailsDao penaltyDetailsDao;
    @Override
    public PenaltyDetails addPenalty(PenaltyDetails penaltyDetails, String schoolCode) throws Exception {
        return penaltyDetailsDao.addPenalty(penaltyDetails,schoolCode);
    }

    @Override
    public List<PenaltyDetails> getAllPenaltyDetails(String schoolCode) throws Exception {
        return penaltyDetailsDao.getAllPenaltyDetails(schoolCode);
    }

    @Override
    public PenaltyDetails getPenalty(int penaltyId, String schoolCode) throws Exception {
        return penaltyDetailsDao.getPenalty(penaltyId,schoolCode);
    }

    @Override
    public PenaltyDetails updatePenaltyDetails(PenaltyDetails penaltyDetails, int penaltyId, String schoolCode) throws Exception {
        return penaltyDetailsDao.updatePenaltyDetails(penaltyDetails,penaltyId,schoolCode);
    }
}
