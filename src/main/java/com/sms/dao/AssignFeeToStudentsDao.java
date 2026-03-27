package com.sms.dao;

import com.sms.model.AssignFeeToStudentsDetails;

import java.util.List;

public interface AssignFeeToStudentsDao {
    List<AssignFeeToStudentsDetails> getAssignFeeToStudentDetails(int sessionId, String schoolCode) throws Exception;
}
