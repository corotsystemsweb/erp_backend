package com.sms.service;

import com.sms.model.FeeDepositDetails;

import java.util.List;

public interface FeeDepositService {
    public FeeDepositDetails addFeeDeposit(FeeDepositDetails feeDepositDetails, String schoolCode) throws Exception;
    public FeeDepositDetails getFeeDepositById(int fdId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getAllFeeDeposit(String schoolCode) throws Exception;
    public FeeDepositDetails updateFeeDepositById(FeeDepositDetails feeDepositDetails, int fdId, String schoolCode) throws Exception;
    public double getTotalFeeDeposit(String schoolCode) throws Exception;
    public List<FeeDepositDetails> getTotalAmountPaidByStudents(String schoolCode) throws Exception;
    public FeeDepositDetails getTotalAmountPaidByParticularStudent(int studentId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getYearlyTotalDeposit(int sessionId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getUnsettledFeesByClassSectionSessionAndStudentId(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception;
}
