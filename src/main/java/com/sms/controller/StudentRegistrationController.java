package com.sms.controller;

import com.sms.model.*;
import com.sms.service.StudentRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.sms.appenum.Message.DELETE_CLASS_FAILED;
import static com.sms.appenum.Message.DELETE_CLASS_SUCCESS;

@RestController
@RequestMapping("/api/student")
public class StudentRegistrationController {

    private final StudentRegistrationService studentRegistrationService;

    public StudentRegistrationController(StudentRegistrationService studentRegistrationService) {
        this.studentRegistrationService = studentRegistrationService;
    }

    @PostMapping("/register/{schoolCode}")
    public ResponseEntity<StudentRegistration> registerStudent(@RequestBody StudentRegistration request, @PathVariable String schoolCode)throws Exception{
        StudentRegistration result = null;
        try {
            result = studentRegistrationService.registerStudent(request, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @GetMapping("/registration/all/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllStudentDetails(@PathVariable int sessionId, @PathVariable String schoolCode) throws Exception {
        List<StudentRegistration> result = null;
        try {
            result = studentRegistrationService.getAllStudentDetails(sessionId,schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PutMapping("/update/{stdRegistationId}/{schoolCode}")
//    public ResponseEntity<Object> updateClassAndSection(@RequestBody StudentRegistration studentRegistration, @PathVariable int stdRegistrationId, @PathVariable String schoolCode){
//        StudentRegistration result = null;
//        try{
//            result = studentRegistrationService.updateStudentRegistration(studentRegistration, stdRegistrationId, schoolCode);
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        }catch (Exception e){
//            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
@PutMapping("/delete/{stdRegistrationId}/{schoolCode}")
public ResponseEntity<Object> deleteStudentDetailsById(
        @PathVariable int stdRegistrationId,
        @RequestBody Map<String, String> request,  // Accept JSON object
        @PathVariable String schoolCode) {

    // Extract the status from JSON
    String status = request.get("status");
    if (status == null || status.isEmpty()) {
        status = "INACTIVE"; // default if not provided
    }

    boolean result = studentRegistrationService.deleteRegistration(stdRegistrationId, status, schoolCode);

    if(result){
        ClassResponse response = new ClassResponse(result, 200 , DELETE_CLASS_SUCCESS.val());
        return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
        ClassResponse response = new ClassResponse(result, 400 , DELETE_CLASS_FAILED.val());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


}
