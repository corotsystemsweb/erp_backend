package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.model.StudentEnquiryFormDetails;
import com.sms.service.StudentEnquiryFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/get-all/{schoolCode}")
    public ResponseEntity<?> getAllStudentEnquiry(@PathVariable String schoolCode){
        try{
            List<StudentEnquiryFormDetails> list = studentEnquiryFormService.getAllStudentEnquiry(schoolCode);

            if(list == null || list.isEmpty()){
                return ResponseEntity.ok("No records found.");
            }

            return ResponseEntity.ok(list);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Error fetching student enquiry data: " + e.getMessage());
        }
    }
}
