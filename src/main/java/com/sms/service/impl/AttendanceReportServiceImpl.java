package com.sms.service.impl;

import com.sms.dao.AttendanceReportDao;
import com.sms.model.StudentAttendanceSummary;
import com.sms.service.AttendanceReportService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceReportDao attendanceReportDao;

    public AttendanceReportServiceImpl(AttendanceReportDao attendanceReportDao) {
        this.attendanceReportDao = attendanceReportDao;
    }

    @Override
    public List<StudentAttendanceSummary> generateMonthlyReport(
            int classId, int sectionId, int sessionId,
            String startDate, String endDate, String schoolCode
    ) {
        return attendanceReportDao.getMonthlyAttendanceSummary(
                classId, sectionId, sessionId, startDate, endDate, schoolCode
        );
    }

    @Override
    public List<StudentAttendanceSummary> generateDateRangeReport(
            int classId, int sectionId,
            String startDate, String endDate, String schoolCode
    ) {
        return attendanceReportDao.getDateRangeAttendanceSummary(
                classId, sectionId, startDate, endDate, schoolCode
        );
    }

    @Override
    public List<Map<String, Object>> fetchMonthlyAttendance(Long classId, Long sectionId, int month, int year,String schoolCode) {
        return attendanceReportDao.getMonthlyAttendanceSummaryforDashboard(classId,sectionId,month,year,schoolCode);
    }

    @Override
    public List<Map<String, Object>> fetchClassSectionSummary(String schoolCode) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> fetchStudentAttendance(Long studentId, Long classId, Long sectionId, Date startDate, Date endDate, String schoolCode) {
        return List.of();
    }

    @Override
    public List<StudentAttendanceSummary> getAttendanceSummaryByClass(String schoolCode) throws Exception {
        return attendanceReportDao.getAttendanceSummaryByClass(schoolCode);
    }

    @Override
    public List<StudentAttendanceSummary> fetchAttendanceDetails(Long studentId, String intervalType,Integer year, Integer classId,Integer sectionId,String schoolCode) throws Exception {
        return attendanceReportDao.getAttendanceDetails(studentId,intervalType,year,classId,sectionId,schoolCode);
    }


}