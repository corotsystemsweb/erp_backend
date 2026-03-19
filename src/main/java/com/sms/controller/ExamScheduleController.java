package com.sms.controller;

import com.sms.model.*;
import com.sms.service.ExamScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class ExamScheduleController {
    @Autowired
    private ExamScheduleService examScheduleService;
    @PostMapping("/add/exam/schedule/{schoolCode}")
    public ResponseEntity<List<ExamScheduleDetails>> addExamScheduleDetails(@RequestBody List<ExamScheduleDetails> examScheduleDetails, @PathVariable String schoolCode) {
        try {
            List<ExamScheduleDetails> examScheduleDetailsList =examScheduleService.addExamScheduleDetails(examScheduleDetails,schoolCode);
            return new ResponseEntity<>(examScheduleDetailsList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/add/exam/{schoolCode}")
    public ResponseEntity<Object> addScheduleExam(@RequestBody ExamScheduleDetails examScheduleDetails, @PathVariable String schoolCode) throws Exception{
        ExamScheduleDetails result = null;
        try{
            result = examScheduleService.addExam(examScheduleDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/all/{sessionId}/{classId}/{sectionId}/{examType}/{schoolCode}")
    public ResponseEntity<Object> getAllSubjectDetails(@PathVariable int sessionId,@PathVariable int classId, @PathVariable int sectionId,@PathVariable String examType,@PathVariable String schoolCode) throws Exception {
        List<ExamScheduleDetails> result = null;
        try {
            result = examScheduleService.getExamScheduleDetails(sessionId,classId,sectionId,examType,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/schedule/exam/soft/delete")
    public ResponseEntity<Object> deleteExamSchedule(@RequestParam("esId") int esId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = examScheduleService.softDeleteExamSchedule(esId,schoolCode);
        if (result) {
            // If deletion is successful
            ExamScheduleResponse response = new ExamScheduleResponse(result, 200, DELETE_SCHEDULE_EXAM_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // If deletion fails
            ExamScheduleResponse response = new ExamScheduleResponse(result, 400, DELETE_SCHEDULE_EXAM_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
