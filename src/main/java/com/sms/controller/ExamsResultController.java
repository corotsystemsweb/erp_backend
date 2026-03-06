package com.sms.controller;

import com.sms.model.ExamResultDetails;
import com.sms.service.ExamsResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-results")
public class ExamsResultController {
    private final ExamsResultService examsResultService;

    public ExamsResultController(ExamsResultService examsResultService) {
        this.examsResultService = examsResultService;
    }

    @GetMapping("/{schoolCode}")
    public List<ExamResultDetails> getAllExamResults(
            @PathVariable String schoolCode,
            @RequestParam(required = false) Integer sessionId,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer sectionId) {

        return examsResultService.getExamResults(sessionId, classId, sectionId,schoolCode);
    }
    @GetMapping("/with-subject/{classId}/{sectionId}/{schoolCode}")
    public List<ExamResultDetails> getAllExamResultsWithSubject(
            @PathVariable Integer classId,
            @PathVariable Integer sectionId,
            @PathVariable String schoolCode
            ) {
        return examsResultService.getExamResultWithSubject(classId,sectionId,schoolCode);
    }

    @GetMapping("/with-subject/{classId}/{sectionId}/{studentId}/{examTypeId}/{schoolCode}")
    public ExamResultDetails getParticularStudentResult(
            @PathVariable Integer classId,
            @PathVariable Integer sectionId,
            @PathVariable Integer studentId,
            @PathVariable Integer examTypeId,
            @PathVariable String schoolCode
    ) {
        return examsResultService.resultForParticularStudent(classId,sectionId,studentId,examTypeId,schoolCode);
    }
}

