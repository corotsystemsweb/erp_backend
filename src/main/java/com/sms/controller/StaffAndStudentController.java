package com.sms.controller;

import com.sms.model.StaffAndStudentDetails;
import com.sms.service.StaffAndStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/staff")
public class StaffAndStudentController {
    @Autowired
    private StaffAndStudentService staffAndStudentService;

    @GetMapping("/search")
    public ResponseEntity<Object> getUsersByPhoneNumberAndStatus(@RequestParam("phoneNumber") String phoneNumber){
        List<StaffAndStudentDetails> result = null;
        try{
            result = staffAndStudentService.getUsersByPhoneNumberAndStatus(phoneNumber);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
