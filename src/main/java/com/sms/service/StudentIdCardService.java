package com.sms.service;

import com.sms.model.StudentIdCardDetails;

import java.util.List;

public interface StudentIdCardService {
    public List<StudentIdCardDetails> getAllStudentImage(String schoolCode, int studentId) throws Exception;
    public List<StudentIdCardDetails> searchByClassSectionAndSession(int studentClassId, int studentSectionId, int sessionId, String schoolCode) throws Exception;
}
