package com.sms.controller;

import com.sms.model.StudentDetails;
import com.sms.model.StudentResponse;
import com.sms.model.SubjectDetails;
import com.sms.model.SubjectResponse;
import com.sms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;
    @PostMapping("/subject/add/{schoolCode}")
    public ResponseEntity<Object> addSubjectDetails(@RequestBody SubjectDetails subjectDetails, @PathVariable String schoolCode) throws Exception{
        try{
            SubjectDetails result = subjectService.addSubject(subjectDetails, schoolCode);
            if(result == null){
                return new ResponseEntity<>("Subject is already exist", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>("An error occurred while adding the subject",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/subject/get/{subjectId}/{schoolCode}")
    public ResponseEntity<Object> getSubjectDetailsById(@PathVariable int subjectId, @PathVariable String schoolCode) throws Exception {
        SubjectDetails result = null;
        try {
            result = subjectService.getSubjectDetailsById(subjectId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/subject/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllSubjectDetails(@PathVariable String schoolCode) throws Exception {
        List<SubjectDetails> result = null;

        try {
            result = subjectService.getAllSubjectDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/subject/update/{subjectId}/{schoolCode}")
    public ResponseEntity<Object> updateSubjectDetailsById(@RequestBody SubjectDetails subjectDetails, @PathVariable int subjectId, @PathVariable String schoolCode) throws Exception {
        SubjectDetails result = null;
        try {
            result = subjectService.updateSubjectDetailsById(subjectDetails, subjectId, schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Another subject with the same name already exists", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/subject/delete/{subjectId}/{schoolCode}")
    public ResponseEntity<Object> deleteSubjectDetailsById(@PathVariable int subjectId, @PathVariable String schoolCode) throws Exception {
        boolean result = subjectService.deleteSubject(subjectId, schoolCode);
        if(result){
            SubjectResponse response = new SubjectResponse(result, 200 , DELETE_SUBJECT_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            SubjectResponse response = new SubjectResponse(result, 400 , DELETE_SUBJECT_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
