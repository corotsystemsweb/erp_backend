package com.sms.service.impl;

import com.sms.dao.StudentFeeCollectionDao;
import com.sms.model.StudentFeeCollectionDetails;
import com.sms.service.StudentFeeCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentFeeCollectionServiceImpl implements StudentFeeCollectionService {
    @Autowired
    private StudentFeeCollectionDao studentFeeCollectionDao;
    @Override
    public List<StudentFeeCollectionDetails> getAllStudentFeeCollection(String schoolCode) throws Exception {
        return studentFeeCollectionDao.getAllStudentFeeCollection(schoolCode);
    }

    @Override
    public StudentFeeCollectionDetails getStudentFeeCollectionByStudentId(int studentId, String schoolCode) throws Exception {
        return studentFeeCollectionDao.getStudentFeeCollectionByStudentId(studentId, schoolCode);
    }
}
