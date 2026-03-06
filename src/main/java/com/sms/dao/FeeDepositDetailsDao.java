package com.sms.dao;

import com.sms.model.FeeDepositDetails;
import com.sms.model.FeeDueDateDetails;

import java.util.List;

public interface FeeDepositDetailsDao {
    public List<FeeDepositDetails> addFeeDepositDetails(List<FeeDepositDetails> feeDepositDetails, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getStudentFeeDetailsBasedOnClassSectionSession(int classId, int sectionId, int sessionId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getAllStudentFeeDetails(int sessionId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getStudentFeeSegregation(int schoolId, int sessionId, int classId, int sectionId, int studentId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getAcademicFeesScreen(int schoolId, int sessionId, int studentId, String schoolCode) throws Exception;
    public FeeDepositDetails getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception;
    List<FeeDepositDetails> findFeeDepositDetailsById(int fdId, int schoolId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> findFeeDeposits(Integer studentId, String studentName,
                                                   int schoolId, int sessionId, String schoolCode)
            throws Exception;
}
