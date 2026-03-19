package com.sms.service.impl;

import com.sms.dao.FeeAssignmentDao;
import com.sms.model.FeeAssignmentDetails;
import com.sms.service.FeeAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeAssignmentServiceImpl implements FeeAssignmentService {
    @Autowired
    private FeeAssignmentDao feeAssignmentDao;

    @Override
    public FeeAssignmentDetails addFeeAssignment(FeeAssignmentDetails feeAssignmentDetails, String schoolCode) throws Exception {
        return feeAssignmentDao.addFeeAssignment(feeAssignmentDetails, schoolCode);
    }

    @Override
    public List<FeeAssignmentDetails> getFeeAssignments(int classId, Integer sectionId, int sessionId, Integer studentId, String schoolCode) throws Exception {
        return feeAssignmentDao.getFeeAssignments(classId, sectionId, sessionId, studentId, schoolCode);
    }

    @Override
    public FeeAssignmentDetails getFeeAssignmentById(int faId, String schoolCode) throws Exception {
        return feeAssignmentDao.getFeeAssignmentById(faId, schoolCode);
    }

    @Override
    public List<FeeAssignmentDetails> getAllFeeAssignment(String schoolCode) throws Exception {
        return feeAssignmentDao.getAllFeeAssignment(schoolCode);
    }

    @Override
    public FeeAssignmentDetails getDiscountDetails(int faId, double amount, String schoolCode) throws Exception {
        return feeAssignmentDao.getDiscountDetails(faId, amount, schoolCode);
    }

   /* @Override
    public List<FeeAssignmentDetails> getFeeAssignmentDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return feeAssignmentDao.getFeeAssignmentDetailsBySearchText(searchText, schoolCode);
    }*/
   @Override
   public List<FeeAssignmentDetails> getFeeAssignmentDetailsByFilters(
           String schoolCode,
           Integer sessionId,
           Integer classId,
           Integer sectionId,
           Integer studentId,
           String searchText) throws Exception {

       // Call the DAO method with the same filters
       return feeAssignmentDao.getFeeAssignmentDetailsByFilters(
               schoolCode, sessionId,classId, sectionId, studentId,searchText);
   }


    @Override
    public List<FeeAssignmentDetails> getFeeAssignmentByFaIdAndSession(int faId, int sessionId, String schoolCode) throws Exception {
        return feeAssignmentDao.getFeeAssignmentByFaIdAndSession(faId, sessionId, schoolCode);
    }
}
