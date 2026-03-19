package com.sms.service;

import com.sms.model.StudentRegistration;

import java.util.List;

public interface StudentRegistrationService {

    StudentRegistration registerStudent(StudentRegistration studentRegistration, String schoolCode);
    StudentRegistration updateStudentRegistration(StudentRegistration studentRegistration, int stdRegistrationId, String schoolCode);
    boolean deleteRegistration(int stdRegistrationId, String status, String schoolCode);
    List<StudentRegistration> getAllStudentDetails(int sessionId, String schoolCode);
}
