package com.sms.dao;

import com.sms.model.StudentRegistration;

import java.util.List;

public interface StudentRegistrationDao {

    StudentRegistration insertStudent(StudentRegistration student, String schoolCode);
    public boolean deleteRegistration(int stdRegistrationId, String status, String schoolCode);
    List<StudentRegistration> getAllRegistration(int sessionId, String schoolCode);
}
