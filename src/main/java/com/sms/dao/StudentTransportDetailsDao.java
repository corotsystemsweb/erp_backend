package com.sms.dao;

import com.sms.model.StudentTransportDetails;

import java.util.List;

public interface StudentTransportDetailsDao {
    public StudentTransportDetails addStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception;
    //public StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception;
    StudentTransportDetails updateStudentTransportWithHistory(StudentTransportDetails details, String schoolCode) throws Exception;
    StudentTransportDetails deactivateStudentTransport(int studentTransportId, String schoolCode) throws Exception;
    StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception;
    Object getStudentTransportDetails(int sessionId, Integer studentId, String schoolCode) throws Exception;
    List<StudentTransportDetails> getStudentTransport(int sessionId, String schoolCode) throws Exception;
    Object getActiveStudentTransportDetails(
            int sessionId,
            Integer studentId,
            String status,
            String schoolCode) throws Exception;


}
