package com.sms.service;

import com.sms.model.FeeDueDateDetails;

import java.util.List;

public interface FeeDueDateService {
    public FeeDueDateDetails addFeeDueDetails(FeeDueDateDetails feeDueDateDetails, String schoolCode) throws Exception;
    public List<FeeDueDateDetails> addFeeDueDetailsList(List<FeeDueDateDetails> feeDueDateDetailsList, String schoolCode) throws Exception;
    public List<FeeDueDateDetails> getExactFee(int sessionId, String schoolCode) throws Exception;
    public void updateFeeDueDate(List<FeeDueDateDetails> feeDueDateDetailsList, int faId, String schoolCode) throws Exception;
}
