package com.sms.dao;

import com.sms.model.DemandSlip;
import com.sms.model.FeeSummary;
import com.sms.model.MonthWiseFeeDueDetails;
import com.sms.model.PendingFee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FeeReportDAO {
    public List<FeeSummary> getFeeSummary(String reportType, LocalDate startDate, LocalDate endDate, String schoolCode);
    List<Map<String, Object>> getClassPendingFees(Long classId, Long sessionId, String schoolCode);
    List<Map<String, Object>> getSectionPendingFees(Long classId, Long sectionId, Long sessionId, String schoolCode);
    List<Map<String, Object>> getStudentPendingFees(Long classId, Long sectionId,Long studentId, Long sessionId, String schoolCode);
    public List<Map<String, Object>> getDemandSlips(Long classId, Long sectionId, Long studentId, Long sessionId, String schoolCode, LocalDate cutoffDate);
    public List<FeeSummary> getFeeSummaryByClass(String schoolCode) throws Exception;
    public List<MonthWiseFeeDueDetails> fetchMonthlyDueDetails(int sessionId, int classId,
                                                               Integer sectionId, Integer studentId, String monthFilter, String schoolCode);
}
