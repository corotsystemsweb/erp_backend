/*
package com.sms.service.impl;

import com.sms.dao.FeeAsWithDDateDao;
import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.service.FeeAsWithDDateService;
import org.springframework.stereotype.Service;

@Service
public class FeeAsWithDDateServiceImpl implements FeeAsWithDDateService {

    private  FeeAsWithDDateDao feeAsWithDDateDao;

    public FeeAsWithDDateServiceImpl(FeeAsWithDDateDao feeAsWithDDateDao) {
        this.feeAsWithDDateDao = feeAsWithDDateDao;
    }

    @Override
    public boolean saveFeeAssignment(FeeAssignmentDetailsNew details,String schoolCode) {
        return feeAsWithDDateDao.assignFeeWithDueDates(details,schoolCode);
    }
}
*/
package com.sms.service.impl;

import com.sms.dao.FeeAsWithDDateDao;
import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.service.FeeAsWithDDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeeAsWithDDateServiceImpl implements FeeAsWithDDateService {

    private final FeeAsWithDDateDao feeAsWithDDateDao;

    public FeeAsWithDDateServiceImpl(FeeAsWithDDateDao feeAsWithDDateDao) {
        this.feeAsWithDDateDao = feeAsWithDDateDao;
    }
    @Transactional
    @Override
    public boolean saveFeeAssignment(FeeAssignmentDetailsNew details, String schoolCode) {
        // Validate input
        if (details == null || schoolCode == null || schoolCode.isBlank()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        // Check for duplicates based on assignment level
        /*if (isDuplicateAssignment(details, schoolCode)) {
            throw new IllegalStateException("Duplicate fee assignment detected");
        }*/

        // Save to database
        return feeAsWithDDateDao.assignFeeWithDueDates(details, schoolCode);
    }

    @Override
    public FeeAssignmentDetailsNew getFeeAssignmentDetailsWithStudents(long faId, String schoolCode) {
        return feeAsWithDDateDao.getFeeAssignmentDetailsWithStudents(faId,schoolCode);
    }

    @Override
    public List<FeeAssignmentDetailsNew> getAllFeeAssignments(String schoolCode,Integer classId, Integer sectionId, Integer studentId) {
        return feeAsWithDDateDao.getAllFeeAssignments(schoolCode,classId,sectionId,studentId);
    }

    @Override
    public void editFeeAssignment(FeeAssignmentDetailsNew updatedAssignment, String schoolCode) {
         feeAsWithDDateDao.updateFeeAssignment(updatedAssignment,schoolCode);
    }


    /*private boolean isDuplicateAssignment(FeeAssignmentDetailsNew details, String schoolCode) {
        if (details.getSectionId() == null && details.getStudentId() == null) {
            // Class-level assignment
            return feeAsWithDDateDao.existsClassAssignment(
                    schoolCode,
                    details.getSchoolId(),
                    details.getSessionId(),
                    details.getClassId(),
                    details.getFeeId()
            );
        } else if (details.getStudentId() == null) {
            // Section-level assignment
            return feeAsWithDDateDao.existsSectionAssignment(
                    schoolCode,
                    details.getSchoolId(),
                    details.getSessionId(),
                    details.getClassId(),
                    details.getSectionId(),
                    details.getFeeId()
            );
        } else {
            // Student-level assignment
            return feeAsWithDDateDao.existsStudentAssignment(
                    schoolCode,
                    details.getSchoolId(),
                    details.getSessionId(),
                    details.getStudentId(),
                    details.getFeeId()
            );
        }
    }*/
}