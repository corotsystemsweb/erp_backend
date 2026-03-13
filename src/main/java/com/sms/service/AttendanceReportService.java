package com.sms.service;

import com.sms.model.StudentAttendanceSummary;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface AttendanceReportService {
    List<StudentAttendanceSummary> generateMonthlyReport(
            int classId, int sectionId, int sessionId,
            String startDate, String endDate, String schoolCode
    );

    List<StudentAttendanceSummary> generateDateRangeReport(
            int classId, int sectionId,
            String startDate, String endDate, String schoolCode
    );

    List<Map<String, Object>> fetchMonthlyAttendance(Long classId, Long sectionId, int month, int year, String schoolCode);
    List<Map<String, Object>> fetchClassSectionSummary(String schoolCode);
    List<Map<String, Object>> fetchStudentAttendance(Long studentId, Long classId, Long sectionId, Date startDate, Date endDate, String schoolCode);
    public List<StudentAttendanceSummary> getAttendanceSummaryByClass(String schoolCode) throws Exception;
    List<StudentAttendanceSummary> fetchAttendanceDetails(Long studentId, String intervalType,Integer year, Integer classId,Integer sectionId,String schoolCode) throws Exception;

}