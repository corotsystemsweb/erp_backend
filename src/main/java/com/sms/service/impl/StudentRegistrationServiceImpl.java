package com.sms.service.impl;

import com.sms.dao.StudentRegistrationDao;
import com.sms.model.StudentRegistration;
import com.sms.service.StudentRegistrationService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

    private final StudentRegistrationDao studentRegistrationDao;

    public StudentRegistrationServiceImpl(StudentRegistrationDao studentRegistrationDao) {
        this.studentRegistrationDao = studentRegistrationDao;
    }

    @Override
    public StudentRegistration registerStudent(StudentRegistration student, String schoolCode) {

        // backend-controlled values
        if (student.getRegistrationDate() == null) {
            student.setRegistrationDate(new Date());
        }

        return studentRegistrationDao.insertStudent(student, schoolCode);
    }


    @Override
    public StudentRegistration updateStudentRegistration(StudentRegistration studentRegistration, int stdRegistrationId, String schoolCode) {
        return null;
    }

    @Override
    public boolean deleteRegistration(int stdRegistrationId, String status, String schoolCode) {
        return studentRegistrationDao.deleteRegistration(stdRegistrationId, status, schoolCode);
    }

    @Override
    public List<StudentRegistration> getAllStudentDetails(int sessionId, String schoolCode) {
        return studentRegistrationDao.getAllRegistration(sessionId, schoolCode);
    }


}
