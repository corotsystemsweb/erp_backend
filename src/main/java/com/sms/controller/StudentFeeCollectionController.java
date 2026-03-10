package com.sms.controller;

import com.sms.model.StudentFeeCollectionDetails;
import com.sms.service.StudentFeeCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentFeeCollectionController {
    @Autowired
    private StudentFeeCollectionService studentFeeCollectionService;
    @GetMapping("/get/all/students/gross/fee/{schoolCode}")
    public ResponseEntity<Object> getAllStudentFeeCollection(@PathVariable String schoolCode) throws Exception{
        List<StudentFeeCollectionDetails> result = null;
        try {
            result = studentFeeCollectionService.getAllStudentFeeCollection(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/students/gross/fee/{studentId}/{schoolCode}")
    public ResponseEntity<Object> getStudentFeeCollectionByStudentId(@PathVariable int studentId, @PathVariable String schoolCode) throws Exception{
        StudentFeeCollectionDetails result = null;
        try {
            result = studentFeeCollectionService.getStudentFeeCollectionByStudentId(studentId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
