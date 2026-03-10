package com.sms.controller;

import com.sms.model.AttendanceSession;
import com.sms.service.AttendanceService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark/{sessionId}/{schoolCode}")
    public ResponseEntity<?> markAttendance(
            @PathVariable("sessionId") int sessionId,
            @PathVariable String schoolCode,
            @RequestBody Map<String, Object> requestBody) {

        try {
            AttendanceSession session = new AttendanceSession();
            session.setSessionId(sessionId);
            session.setClassId((Integer) requestBody.get("classId"));
            session.setSectionId((Integer) requestBody.get("sectionId"));
            session.setSubjectId((Integer) requestBody.get("subjectId"));
            session.setTeacherId((Integer) requestBody.get("teacherId"));
            session.setSessionType(((String) requestBody.get("attendanceType")).toUpperCase());
           // session.setSessionDate(LocalDate.now());

            String sessionDateStr = (String) requestBody.get("sessionDate");
            LocalDate sessionDate = LocalDate.parse(sessionDateStr);
            session.setSessionDate(sessionDate);
            System.out.println(sessionDate);
            List<Integer> absentStudentIds = (List<Integer>) requestBody.get("absentStudentIds");

            attendanceService.markAttendance(session, absentStudentIds, schoolCode);
            return ResponseEntity.ok("Attendance marked successfully");

        } catch (ClassCastException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Invalid request format");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.internalServerError().body("Database error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }


}