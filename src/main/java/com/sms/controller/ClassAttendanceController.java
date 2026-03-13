package com.sms.controller;

import com.sms.model.ClassAttendanceDetails;
import com.sms.model.ClassDetails;
import com.sms.service.ClassAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequestMapping("/api")
public class ClassAttendanceController {
    @Autowired
    private ClassAttendanceService classAttendanceService;
    @PostMapping("/classAttendance/add/{schoolCode}")
    public ResponseEntity<Object> addClassAttendance(@RequestBody ClassAttendanceDetails classAttendanceDetails, @PathVariable String schoolCode) throws Exception{
        ClassAttendanceDetails result = null;
        try{
            result =classAttendanceService.addClassAttendance(classAttendanceDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/attendance/classAttendanceId")
    public ResponseEntity<?> getClassAttendanceId(@RequestParam int classId,@RequestParam int sectionId,@RequestParam int subjectId,@RequestParam int teacherId,@RequestParam Date presentDate,@RequestParam String schoolCode) {
        System.out.print(presentDate);
        try {
            int attendanceId = classAttendanceService.getClassAttendanceId(classId,sectionId,subjectId,teacherId,presentDate,schoolCode);

            return ResponseEntity.ok(attendanceId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving attendance id");
        }
    }

}
