package com.sms.service;

import com.sms.model.StudentDueFeeDetails;

import java.util.List;

public interface StudentDueFeeService {
    public List<StudentDueFeeDetails> calculateDueFees(String schoolCode) throws Exception;
    public StudentDueFeeDetails calculateDueFeeBasedOnStudentId(int studentId, String schoolCode) throws Exception;
    public List<StudentDueFeeDetails> calculateDueFeesBySearchText(String searchText, String schoolCode) throws Exception;
}
