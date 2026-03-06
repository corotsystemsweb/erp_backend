package com.sms.service;

import com.sms.model.StudentTransportDetails;
import com.sms.model.TransportCloseRequest;
import com.sms.model.TransportFeeDue;

import java.util.List;

public interface StudentTransportService {
    StudentTransportDetails addStudentTransport(StudentTransportDetails details, List<TransportFeeDue> feeDueList, String schoolCode) throws Exception;
    StudentTransportDetails updateStudentTransport(StudentTransportDetails details, List<TransportFeeDue> feeDueList, String schoolCode) throws Exception;
    public String closeTransPortFee(TransportCloseRequest request, String schoolCode) throws Exception;
}
