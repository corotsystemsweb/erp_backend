package com.sms.controller;

import com.sms.model.LessonPlanDetails;
import com.sms.service.LessonPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LessionPlanController {
    private final LessonPlanService lessonPlanService;

    public LessionPlanController(LessonPlanService lessonPlanService) {
        this.lessonPlanService = lessonPlanService;
    }

    @PostMapping("/lession/plan/{schoolCode}")
    public ResponseEntity<?> createLessonPlan(@RequestBody LessonPlanDetails lessonPlanDetails, @PathVariable String schoolCode) {
        try {
            LessonPlanDetails createdPlan = lessonPlanService.save(lessonPlanDetails,schoolCode);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
