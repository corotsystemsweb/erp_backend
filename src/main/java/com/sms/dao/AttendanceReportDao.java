package com.sms.dao;

import com.sms.model.FeeSummary;
import com.sms.model.StudentAttendanceSummary;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface AttendanceReportDao {
    List<StudentAttendanceSummary> getMonthlyAttendanceSummary(
            int classId, int sectionId, int sessionId,
            String startDate, String endDate, String schoolCode
    );

    List<StudentAttendanceSummary> getDateRangeAttendanceSummary(
            int classId, int sectionId,
            String startDate, String endDate, String schoolCode
    );

    List<Map<String, Object>> getMonthlyAttendanceSummaryforDashboard(Long classId, Long sectionId, int month, int year, String schoolCode);
    List<Map<String, Object>> getClassSectionSummary(String schoolCode);
    List<Map<String, Object>> getStudentAttendanceDetails(Long studentId, Long classId, Long sectionId, Date startDate, Date endDate, String schoolCode);
    public List<StudentAttendanceSummary> getAttendanceSummaryByClass(String schoolCode) throws Exception;
    List<StudentAttendanceSummary>getAttendanceDetails(Long studentId, String intervalType, Integer year, Integer classId,Integer sectionId,String schoolCode) throws Exception;
}