package com.sms.controller;

import com.sms.model.ExamResult;
import com.sms.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class ExamResultController {
    private final ExamResultService examResultService;

    @PostMapping("/save/exam/{schoolCode}")
    public ResponseEntity<?> saveMarks(@RequestBody List<ExamResult> results, @PathVariable String schoolCode) {
        try {
            examResultService.saveMarks(results,schoolCode);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   /* @GetMapping("/by-subject/{examSubjectId}")
    public ResponseEntity<List<ExamResult>> getMarksBySubject(
            @PathVariable Long examSubjectId
    ) {
        // ... same implementation
    }*/
}
