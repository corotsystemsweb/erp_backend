package com.sms.service;

import com.sms.model.FeeAssignmentDetailsNew;

import java.util.List;

public interface FeeAsWithDDateService {
    boolean saveFeeAssignment(FeeAssignmentDetailsNew details,String schoolCode);
    FeeAssignmentDetailsNew getFeeAssignmentDetailsWithStudents(long faId,String schoolCode);
    List<FeeAssignmentDetailsNew> getAllFeeAssignments(String schoolCode,Integer classId, Integer sectionId, Integer studentId);
    void editFeeAssignment(FeeAssignmentDetailsNew updatedAssignment, String schoolCode);
}
