package com.sms.controller;
import com.sms.model.CreateExamDetails;
import com.sms.model.ErrorResponse;
import com.sms.model.ExamSubjectsDetails;
import com.sms.service.CreateExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import javax.validation.Valid;
@RestController
@RequestMapping("/api")
public class CreatExamController {
    @Autowired
    private CreateExamService createExamService;

    @PostMapping("/create/exam/{schoolCode}")
    public ResponseEntity<?> createExam(@RequestBody CreateExamDetails createExamDetails, @PathVariable String schoolCode) {
        try {
            createExamService.createExamWithSubjects(createExamDetails,schoolCode);
            return ResponseEntity.ok("Exam created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/exam/details/{sessionId}/{schoolCode}")
    public ResponseEntity<?> getAllScheduledExams(
            @PathVariable int sessionId,
            @PathVariable String schoolCode,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer sectionId,
            @RequestParam(required = false) String examName) {
        try {
            List<CreateExamDetails> result = createExamService.getAllExamDetails(
                    sessionId,
                    schoolCode,
                    classId,
                    sectionId,
                    examName
            );
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                 //   new ErrorResponse("Failed to fetch exam details"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @GetMapping("/exam/timeTable/{examId}/{schoolCode}")
    public ResponseEntity<?> getTimeTableByClass(
            @PathVariable int examId,
            @PathVariable String schoolCode) {
        try {
            List<ExamSubjectsDetails> result = createExamService.getExamTimeTable(examId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}



