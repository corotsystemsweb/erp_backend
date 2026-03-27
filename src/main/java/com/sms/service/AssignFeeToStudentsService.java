package com.sms.service;

import com.sms.model.AssignFeeToStudentsDetails;

import java.util.List;

public interface AssignFeeToStudentsService {
    List<AssignFeeToStudentsDetails> getAssignFeeToStudentDetails(int sessionId, String schoolCode) throws Exception;
}
