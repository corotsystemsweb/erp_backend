package com.sms.dao;

import com.sms.model.FeeAssignmentDetails;

import java.util.List;

public interface FeeAssignmentDao {
    public FeeAssignmentDetails addFeeAssignment(FeeAssignmentDetails feeAssignmentDetails, String schoolCode) throws Exception;
    public List<FeeAssignmentDetails> getFeeAssignments(int classId, Integer sectionId, int sessionId, Integer studentId, String schoolCode) throws Exception;
    public FeeAssignmentDetails getFeeAssignmentById(int faId, String schoolCode) throws Exception;
    public List<FeeAssignmentDetails> getAllFeeAssignment(String schoolCode) throws Exception;
    public FeeAssignmentDetails getDiscountDetails(int faId, double amount, String schoolCode) throws Exception;
//    public List<FeeAssignmentDetails> getFeeAssignmentDetailsBySearchText(String searchText, String schoolCode) throws Exception;
public List<FeeAssignmentDetails> getFeeAssignmentDetailsByFilters(
        String schoolCode,
        Integer sessionId,
        Integer classId,
        Integer sectionId,
        Integer studentId,
        String searchText) throws Exception;
    public List<FeeAssignmentDetails> getFeeAssignmentByFaIdAndSession(int faId, int sessionId, String schoolCode) throws Exception;
}
