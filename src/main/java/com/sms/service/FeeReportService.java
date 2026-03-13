package com.sms.service;

import com.sms.model.FeeSummary;
import com.sms.model.MonthWiseFeeDueDetails;
import com.sms.model.PendingFee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FeeReportService {
    public List<FeeSummary> getFeeSummary(String reportType, LocalDate startDate, LocalDate endDate, String schoolCode);

    List<Map<String, Object>> getPendingFees(String level, Long classId, Long sectionId,
                                             Long studentId, Long sessionId, String schoolCode);

    public List<PendingFee> getDemandSlip(Long classId, Long sectionId,
                                          Long studentId, Long sessionId,
                                          String schoolCode,LocalDate cutoffDate) ;
    public List<FeeSummary> getFeeSummaryByClass(String schoolCode) throws Exception;

  //  List<MonthWiseFeeDueDetails> getMonthlyDueDetails(int sessionId, int classId, int sectionId,String schoolCode);
    public List<MonthWiseFeeDueDetails> getMonthlyDueDetails(int sessionId, int classId,
                                                               Integer sectionId, Integer studentId, String monthFilter, String schoolCode);
}
