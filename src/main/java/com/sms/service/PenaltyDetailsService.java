package com.sms.service;

import com.sms.model.PenaltyDetails;

import java.util.List;

public interface PenaltyDetailsService {
    public PenaltyDetails addPenalty(PenaltyDetails penaltyDetails, String schoolCode)throws Exception;
    public List<PenaltyDetails> getAllPenaltyDetails(String schoolCode) throws Exception;
    public PenaltyDetails getPenalty(int penaltyId,String schoolCode) throws Exception;
    public PenaltyDetails updatePenaltyDetails(PenaltyDetails penaltyDetails,int penaltyId,String schoolCode) throws Exception;

}
