package com.sms.service;

import com.sms.model.CombinedFeeDepositResponse;
import com.sms.model.FeeDepositDetails;
import com.sms.model.FeeDepositRequest;
import com.sms.model.FeeDueDateDetails;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface FeeDepositDetailsService {
    public CombinedFeeDepositResponse addFeeDepositDetails(FeeDepositRequest feeDepositRequest, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getStudentFeeDetailsBasedOnClassSectionSession(int classId, int sectionId, int sessionId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getAllStudentFeeDetails(int sessionId, String schoolCode) throws Exception;
    public List<FeeDepositDetails> getStudentFeeSegregation(int schoolId, int sessionId, int classId, int sectionId, int studentId, String schoolCode) throws Exception;
    public Map<String, Object> getMonthlyDueDetailsForScreen(int schoolId, int sessionId, int studentId, String schoolCode) throws Exception;
    public FeeDepositDetails getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception;
    List<FeeDepositDetails> getFeeDepositDetails(int fdId, int schoolId,String schoolCode) throws Exception;
    public List<FeeDepositDetails> getFeeDeposits(
            String schoolCode,
            int schoolId,
            int sessionId,
            @Nullable Integer studentId,
            @Nullable String studentName
            ) throws Exception;
}
