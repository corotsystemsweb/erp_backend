package com.sms.service;

import com.sms.model.StudentPerformanceRecordDetails;

import java.util.List;

public interface StudentPerformanceRecordService {
    public List<StudentPerformanceRecordDetails> getStudentPerformanceRecord(int classId, int sectionId, int sessionId, int studentId, String examType, String schoolCode) throws Exception;

}
