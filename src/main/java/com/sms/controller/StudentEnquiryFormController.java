package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.model.ClassResponse;
import com.sms.model.StudentEnquiryFormDetails;
import com.sms.model.StudentEnquiryFormResponse;
import com.sms.service.StudentEnquiryFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/student-enquiry-form")
public class StudentEnquiryFormController {

    @Autowired
    private StudentEnquiryFormService studentEnquiryFormService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/save")
    public ResponseEntity<?> saveStudentEnquiry(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam("schoolCode") String schoolCode) {
        try {
            StudentEnquiryFormDetails studentEnquiryFormDetails = objectMapper.readValue(dataJson, StudentEnquiryFormDetails.class);

            StudentEnquiryFormDetails response = studentEnquiryFormService.saveStudentEnquiry(studentEnquiryFormDetails, file, schoolCode);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStudentEnquiry(@RequestParam(required = false) String status, @RequestParam String schoolCode){
        try{
            List<StudentEnquiryFormDetails> list = studentEnquiryFormService.getAllStudentEnquiry(status, schoolCode);

            if(list == null || list.isEmpty()){
                return ResponseEntity.ok("No records found.");
            }

            return ResponseEntity.ok(list);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Error fetching student enquiry data: " + e.getMessage());
        }
    }

    @GetMapping("/getById/{studentEnquiryId}/{schoolCode}")
    public ResponseEntity<?> getStudentEnquiryById(@PathVariable int studentEnquiryId, @PathVariable String schoolCode){
        try{
            StudentEnquiryFormDetails student = studentEnquiryFormService.getStudentEnquiryById(studentEnquiryId, schoolCode);

            if(student == null ){
                return ResponseEntity.ok("No records found.");
            }

            return ResponseEntity.ok(student);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Error fetching student enquiry data: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateStudentEnquiryById(@RequestPart("data") String dataJson, @RequestPart(value = "file", required = false) MultipartFile file, @RequestParam("schoolCode") String schoolCode) {

        try {
            // Convert JSON string → Object
            StudentEnquiryFormDetails studentEnquiryFormDetails = objectMapper.readValue(dataJson, StudentEnquiryFormDetails.class);

            // Call service
            StudentEnquiryFormDetails response = studentEnquiryFormService.updateStudentEnquiryById(studentEnquiryFormDetails, file, schoolCode);

            // If not found
            if (response == null) {
                return ResponseEntity.ok("No records found.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating student enquiry data: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{studentEnquiryId}/{schoolCode}")
    public ResponseEntity<Object> deleteStudentEnquiry(@PathVariable int studentEnquiryId, @PathVariable String schoolCode) throws Exception {
        boolean result = studentEnquiryFormService.deleteStudentEnquiry(studentEnquiryId, schoolCode);
        if(result){
            StudentEnquiryFormResponse response = new StudentEnquiryFormResponse(result, 200 , DELETE_ENQUIRY_FORM_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StudentEnquiryFormResponse response = new StudentEnquiryFormResponse(result, 400 , DELETE_ENQUIRY_FORM_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
