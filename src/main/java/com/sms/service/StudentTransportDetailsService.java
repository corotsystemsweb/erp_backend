package com.sms.service;

import com.sms.model.StudentTransportDetails;

import java.util.Date;
import java.util.List;

public interface StudentTransportDetailsService {
    public StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception;

    List<StudentTransportDetails> StudentTransport(int sessionId, String schoolCode);

    Object getStudentTransportDetail(int sessionId, Integer studentId, String schoolCode);

    Object getActiveStudentTransportDetail(int sessionId, Integer studentId, String status, String schoolCode);

    List<StudentTransportDetails> getAllStudentsTransportDetails(int sessionId, String status, Integer routeId, Date dueMonth, String schoolCode) throws Exception;
}

