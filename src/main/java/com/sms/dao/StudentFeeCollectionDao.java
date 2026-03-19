package com.sms.dao;

import com.sms.model.StudentFeeCollectionDetails;

import java.util.List;

public interface StudentFeeCollectionDao {
    public List<StudentFeeCollectionDetails> getAllStudentFeeCollection(String schoolCode) throws Exception;
    public StudentFeeCollectionDetails getStudentFeeCollectionByStudentId(int studentId, String schoolCode) throws Exception;
}
