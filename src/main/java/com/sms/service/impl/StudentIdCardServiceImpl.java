package com.sms.service.impl;

import com.sms.dao.StudentIdCardDao;
import com.sms.model.StudentIdCardDetails;
import com.sms.service.StudentIdCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentIdCardServiceImpl implements StudentIdCardService {
    @Autowired
    private StudentIdCardDao studentIdCardDao;
    @Override
    public List<StudentIdCardDetails> getAllStudentImage(String schoolCode, int studentId) throws Exception {
        return studentIdCardDao.getAllStudentImage(schoolCode, studentId);
    }

    @Override
    public List<StudentIdCardDetails> searchByClassSectionAndSession(int studentClassId, int studentSectionId, int sessionId, String schoolCode) throws Exception {
        return studentIdCardDao.searchByClassSectionAndSession(studentClassId, studentSectionId, sessionId, schoolCode);
    }
}
