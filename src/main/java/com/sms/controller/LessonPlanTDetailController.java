package com.sms.controller;

import com.sms.model.LessonPlanTitleDetails;
import com.sms.service.LessonPlanTDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class LessonPlanTDetailController {
    private LessonPlanTDetailService lessonPlanTDetailService;
    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkDetails(
            @RequestBody List<LessonPlanTitleDetails> details,
            @RequestParam String schoolCode) {
        try {
            List<LessonPlanTitleDetails> createdDetails = lessonPlanTDetailService.createBulkDetails(details, schoolCode);
            return new ResponseEntity<>(createdDetails, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
