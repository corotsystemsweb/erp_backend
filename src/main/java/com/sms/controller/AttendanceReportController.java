package com.sms.controller;

import com.sms.model.FeeSummary;
import com.sms.model.StudentAttendanceSummary;
import com.sms.service.AttendanceReportService;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-reports")
public class AttendanceReportController {

    private final AttendanceReportService reportService;

    public AttendanceReportController(AttendanceReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{schoolCode}")
    public ResponseEntity<List<StudentAttendanceSummary>> getMonthlyReport(
            @PathVariable String schoolCode,
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam int sessionId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        List<StudentAttendanceSummary> report = reportService.generateMonthlyReport(
                classId, sectionId, sessionId, startDate, endDate, schoolCode
        );
        return ResponseEntity.ok(report);
    }

    @GetMapping("/date-range/{schoolCode}")
    public ResponseEntity<List<StudentAttendanceSummary>> getDateRangeReport(
            @PathVariable String schoolCode,
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        List<StudentAttendanceSummary> report = reportService.generateDateRangeReport(
                classId, sectionId, startDate, endDate, schoolCode
        );
        return ResponseEntity.ok(report);
    }

    @GetMapping("/dashboard/monthly/{schoolCode}")
    public ResponseEntity<?> getMonthlyAttendance(
            @PathVariable String schoolCode,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam int month,
            @RequestParam int year) {

        List<Map<String, Object>> data = reportService.fetchMonthlyAttendance(classId, sectionId, month, year,schoolCode);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/dashboard/summary/{schoolCode}")
    public ResponseEntity<?> getClassSectionSummary(@PathVariable String schoolCode) {
        return ResponseEntity.ok(reportService.fetchClassSectionSummary(schoolCode));
    }

    @GetMapping("/dashboard/student/{schoolCode}")
    public ResponseEntity<?> getStudentAttendance(
            @PathVariable String schoolCode,
            @RequestParam Long studentId,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {

        List<Map<String, Object>> data = reportService.fetchStudentAttendance(
                studentId, classId, sectionId, startDate, endDate,schoolCode);
        return ResponseEntity.ok(data);
    }
    @GetMapping("/currentDate/{schoolCode}")
    public ResponseEntity<Object> currentDateAttendanceByClass(@PathVariable String schoolCode) throws Exception {
        List<StudentAttendanceSummary> result = null;

        try {
            result = reportService.getAttendanceSummaryByClass(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/{studentId}/{schoolCode}")
    public ResponseEntity<?> getAttendanceDetails(
            @PathVariable Long studentId,
            @PathVariable String schoolCode,
            @RequestParam (required = false) String intervalType,
            @RequestParam(required = false) Integer year,
            @RequestParam Integer classId,
            @RequestParam Integer sectionId
    ) {
        try {
            if (studentId == null || studentId <= 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid student ID provided.");
            }

            List<StudentAttendanceSummary> attendanceList =reportService.fetchAttendanceDetails(studentId, intervalType,year,classId,sectionId,schoolCode);

            if (attendanceList.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No attendance records found for student ID: " + studentId);
            }

            return ResponseEntity.ok(attendanceList);

        } catch (DataAccessException dae) {
            // Handle Database-related exception
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error occurred: " + dae.getMessage());
        } catch (Exception ex) {
            // Handle any other unexpected exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }


}