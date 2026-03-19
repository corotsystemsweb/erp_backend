package com.sms.controller;

import com.sms.model.ClassDetails;
import com.sms.model.ExamTypeManager;
import com.sms.service.ExamTypeManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamTypeManagerController {
    @Autowired
    private ExamTypeManagerService examTypeManagerService;
    @PostMapping("/exam/type/{schoolCode}")
    public ResponseEntity<Object> addExamType(@RequestBody ExamTypeManager examTypeManager, @PathVariable String schoolCode) throws Exception{
        ExamTypeManager result = null;
        try{
            result = examTypeManagerService.addExamType(examTypeManager,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/all/exam/type/{sessionId}/{schoolCode}")
    public ResponseEntity<Object>getAllExamType(@PathVariable int sessionId,@PathVariable String schoolCode) throws Exception {
        List<ExamTypeManager> result = null;
        try {
           result=examTypeManagerService.getAllExamType(sessionId,schoolCode);
           return new ResponseEntity<>(result,HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/exam/type/{examTypeId}/{schoolCode}")
    public ResponseEntity<Object>getExamTypeById(@PathVariable int examTypeId,@PathVariable String schoolCode) throws Exception {
        ExamTypeManager result = null;
        try {
            result=examTypeManagerService.getExamTypeById(examTypeId,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/exam/type/{examTypeId}/{schoolCode}")
    public ResponseEntity<Object>updateExamType(@RequestBody ExamTypeManager examTypeManager,@PathVariable int examTypeId,@PathVariable String schoolCode) throws Exception {
        ExamTypeManager result = null;
        try {
            result=examTypeManagerService.updateById(examTypeManager,examTypeId,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
}
