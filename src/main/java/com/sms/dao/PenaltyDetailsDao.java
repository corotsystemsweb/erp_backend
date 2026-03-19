package com.sms.dao;

import com.sms.model.PenaltyDetails;

import java.util.List;

public interface PenaltyDetailsDao {
    public PenaltyDetails addPenalty(PenaltyDetails penaltyDetails,String schoolCode)throws Exception;
    public List<PenaltyDetails> getAllPenaltyDetails(String schoolCode) throws Exception;
    public PenaltyDetails getPenalty(int penaltyId,String schoolCode) throws Exception;
    public PenaltyDetails updatePenaltyDetails(PenaltyDetails penaltyDetails,int penaltyId,String schoolCode) throws Exception;
}
