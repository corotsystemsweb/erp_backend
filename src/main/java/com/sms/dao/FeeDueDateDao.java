package com.sms.dao;

import com.sms.model.FeeDueDateDetails;

import java.util.List;

public interface FeeDueDateDao {
    public FeeDueDateDetails addFeeDueDetails(FeeDueDateDetails feeDueDateDetails, String schoolCode) throws Exception;
    public List<FeeDueDateDetails> addFeeDueDetailsList(List<FeeDueDateDetails> feeDueDateDetailsList, String schoolCode) throws Exception;
    //get total fee, total_discount and exact_fee based on year from fee_due_date table
    public List<FeeDueDateDetails> getExactFee(int sessionId, String schoolCode) throws Exception;
    public void updateFeeDueDate(List<FeeDueDateDetails> feeDueDateDetailsList, int faId, String schoolCode) throws Exception;
}
