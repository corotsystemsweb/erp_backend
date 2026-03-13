package com.sms.service;

import com.sms.model.StudentFeeCollectionDetails;

import java.util.List;

public interface StudentFeeCollectionService {
    public List<StudentFeeCollectionDetails> getAllStudentFeeCollection(String schoolCode) throws Exception;
    public StudentFeeCollectionDetails getStudentFeeCollectionByStudentId(int studentId, String schoolCode) throws Exception;
}
