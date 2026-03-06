package com.sms.service.impl;

import com.sms.dao.StudentPerformanceRecordDao;
import com.sms.model.StudentPerformanceRecordDetails;
import com.sms.service.StudentPerformanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentPerformanceRecordServiceImpl implements StudentPerformanceRecordService {
    @Autowired
    private StudentPerformanceRecordDao studentPerformanceRecordDao;

    @Override
    public List<StudentPerformanceRecordDetails> getStudentPerformanceRecord(int classId, int sectionId, int sessionId, int studentId, String examType, String schoolCode) throws Exception {
        return studentPerformanceRecordDao.getStudentPerformanceRecord(classId, sectionId, sessionId, studentId, examType, schoolCode);
    }
}
