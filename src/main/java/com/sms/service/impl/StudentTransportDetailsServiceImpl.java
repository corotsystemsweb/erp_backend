package com.sms.service.impl;

import com.sms.dao.StudentTransportDetailsDao;
import com.sms.model.StudentTransportDetails;
import com.sms.service.StudentTransportDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentTransportDetailsServiceImpl implements StudentTransportDetailsService {
    @Autowired
    private StudentTransportDetailsDao studentTransportDetailsDao;
//    @Override
//    public StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception {
//        return studentTransportDetailsDao.updateStudentTransportDetails(details, schoolCode);
//    }

    @Override
    public StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception {
        if("CLOSE".equalsIgnoreCase(details.getActionType())){
            return studentTransportDetailsDao.deactivateStudentTransport(details.getStudentTransportId(), schoolCode);
        }
        if("CHANGE".equalsIgnoreCase(details.getActionType())){
            return studentTransportDetailsDao.updateStudentTransportWithHistory(details, schoolCode);
        }
        throw new IllegalArgumentException("Invalid actionType. Allowed values: CHANGE, CLOSE");
    }

    @Override
    public List<StudentTransportDetails> StudentTransport(int sessionId, String schoolCode) {
        try {
            return studentTransportDetailsDao.getStudentTransport(sessionId, schoolCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getStudentTransportDetail(int sessionId, Integer studentId, String schoolCode) {
        try {
            return studentTransportDetailsDao.getStudentTransportDetails(sessionId, studentId, schoolCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StudentTransportDetails getActiveStudentTransportDetail(
            int sessionId,
            Integer studentId,
            String status,
            String schoolCode) {

        try {
            return (StudentTransportDetails) studentTransportDetailsDao
                    .getActiveStudentTransportDetails(sessionId, studentId, status, schoolCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
