package com.sms.controller;


import com.sms.model.StudentTransportDetails;
import com.sms.service.StudentTransportDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport/details")
public class StudentTransportDetailsController {
    @Autowired
    private StudentTransportDetailsService studentTransportDetailsService;

    @PutMapping("/update/{schoolCode}")
    public ResponseEntity<Object> updateStudentTransportDetails(@RequestBody StudentTransportDetails details, @PathVariable String schoolCode){
        try{
            StudentTransportDetails result =  studentTransportDetailsService.updateStudentTransportDetails(details, schoolCode);
            if(result == null){
                return new ResponseEntity<>("Student transport details not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Failed to update student transport details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllTeacher(@PathVariable int sessionId, @PathVariable String schoolCode){
        List<StudentTransportDetails> result = null;
        try{
            result = studentTransportDetailsService.StudentTransport(sessionId, schoolCode);

            return  new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students/{sessionId}/{studentId}/{schoolCode}")
    public ResponseEntity<Object> getStudent(
            @PathVariable int sessionId,
            @PathVariable Integer studentId,
            @PathVariable String schoolCode) {

        Object result;

        try {
            result = studentTransportDetailsService.getStudentTransportDetail(sessionId, studentId, schoolCode);

            if (result == null) {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active-students")
    public ResponseEntity<Object> getStudent(
            @RequestParam int sessionId,
            @RequestParam Integer studentId,
            @RequestParam String status,
            @RequestParam String schoolCode) {

        Object result;

        try {
            result = studentTransportDetailsService
                    .getActiveStudentTransportDetail(sessionId, studentId, status, schoolCode);

            if (result == null) {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
