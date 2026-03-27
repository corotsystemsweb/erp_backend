package com.sms.controller;

import com.sms.model.AssignFeeToStudentsDetails;
import com.sms.service.AssignFeeToStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assign-fee-to-students")
public class AssignFeeToStudentsController {
    @Autowired
    private AssignFeeToStudentsService assignFeeToStudentsService;

    @GetMapping("/{sessionId}/{schoolCode}")
    public ResponseEntity<?> getAssignFeeToStudentDetails(@PathVariable int sessionId,@PathVariable String schoolCode){
        try{
            List<AssignFeeToStudentsDetails> result = assignFeeToStudentsService.getAssignFeeToStudentDetails(sessionId, schoolCode);
            if(result == null || result.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Non records found");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
}
