package com.sms.controller;
import com.sms.model.StudentAttendanceDetails;
import com.sms.service.StudentAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentAttendanceController  {
    @Autowired
    private StudentAttendanceService studentAttendanceService;
    @PostMapping("/absent/add/{schoolCode}")
    public ResponseEntity<Object> addStudentAttendance(@RequestBody List<StudentAttendanceDetails> studentAttendanceDetails, @PathVariable String schoolCode) {
        List<StudentAttendanceDetails> result = null;
        try {
            // Filter out rows where attendance is marked as absent ("No")
            List<StudentAttendanceDetails> presentAttendanceDetails = new ArrayList<>();
            for (StudentAttendanceDetails attendance : studentAttendanceDetails) {
                if (!"Present".equals(attendance.getAbsent())) {
                    presentAttendanceDetails.add(attendance);
                }
            }
            result =studentAttendanceService.addAttendance(presentAttendanceDetails, schoolCode);
            System.out.print(result+"hello");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   @GetMapping("/attendance/total")
   public List<StudentAttendanceDetails> getTotalAttendance(
           @RequestParam(required = false) Integer studentId,
           @RequestParam(required = false) Integer subjectId,
           @RequestParam(required = false) Date dateFrom,
           @RequestParam(required = false) Date dateTo,
           @RequestParam String schoolCode) throws Exception {
       return studentAttendanceService.getTotalAttendance(studentId, subjectId, dateFrom, dateTo, schoolCode);
   }
}
