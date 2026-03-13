package com.sms.controller;

import com.sms.model.StudentPerformanceRecordDetails;
import com.sms.service.StudentPerformanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentPerformanceRecordController {
    @Autowired
    private StudentPerformanceRecordService studentPerformanceRecordService;
    /*public List<StudentPerformanceRecordDetails> getStudentPerformanceRecord(@RequestParam int classId, @RequestParam int sectionId, @RequestParam int sessionId, @RequestParam int studentId,  @RequestParam String examType, @RequestParam String schoolCode) throws Exception{
        List<StudentPerformanceRecordDetails> result = null;
        try{
            result = studentPerformanceRecordService.getStudentPerformanceRecord(classId, sectionId, sessionId, studentId, examType, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
    @GetMapping("/student/performance/record/get")
    public ResponseEntity<List<StudentPerformanceRecordDetails>> getStudentPerformanceRecord(
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam int sessionId,
            @RequestParam int studentId,
            @RequestParam String examType,
            @RequestParam String schoolCode) {
        try {
            List<StudentPerformanceRecordDetails> result = studentPerformanceRecordService.getStudentPerformanceRecord(classId, sectionId, sessionId, studentId, examType, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
