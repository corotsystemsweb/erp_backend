package com.sms.dao;

import com.sms.model.StudentDetails;
import com.sms.model.StudentIdCardDetails;

import java.util.List;

public interface StudentIdCardDao {
    public List<StudentIdCardDetails> getAllStudentImage(String schoolCode, int studentId) throws Exception;
    public List<StudentIdCardDetails> searchByClassSectionAndSession(int studentClassId, int studentSectionId, int sessionId, String schoolCode) throws Exception;
}
