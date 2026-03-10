package com.sms.dao;

import com.sms.model.ClassDetails;
import com.sms.model.FeeDepositDetails;
import com.sms.model.FeeDueDateDetails;

import java.util.List;

public interface FeeDepositDao {
    public FeeDepositDetails addFeeDeposit(FeeDepositDetails feeDepositDetails, String schoolCode) throws Exception;
   public FeeDepositDetails getFeeDepositById(int fdId, String schoolCode) throws Exception;
   public List<FeeDepositDetails> getAllFeeDeposit(String schoolCode) throws Exception;
   public FeeDepositDetails updateFeeDepositById(FeeDepositDetails feeDepositDetails, int fdId, String schoolCode) throws Exception;
   public double getTotalFeeDeposit(String schoolCode) throws Exception;
   public List<FeeDepositDetails> getTotalAmountPaidByStudents(String schoolCode) throws Exception;
   public FeeDepositDetails getTotalAmountPaidByParticularStudent(int studentId, String schoolCode) throws Exception;
   //calculate yearly total fee deposit
   public List<FeeDepositDetails> getYearlyTotalDeposit(int sessionId, String schoolCode) throws Exception;
   public List<FeeDepositDetails> getUnsettledFeesByClassSectionSessionAndStudentId(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception;
   public FeeDepositDetails updateStatus(FeeDepositDetails feeDepositDetails, int fdId, String schoolCode) throws Exception;
}
