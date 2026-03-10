package com.sms.controller;

import com.sms.model.StudentIdCardDetails;
import com.sms.service.StudentIdCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentIdCardController {
    @Autowired
    private StudentIdCardService studentIdCardService;

    @GetMapping("/student/idCard")
    public ResponseEntity<List<StudentIdCardDetails>> searchByClassSectionAndSessionForIdCard(@RequestParam("studentClassId") int studentClassId,
                                                                                              @RequestParam("studentSectionId") int studentSectionId,
                                                                                              @RequestParam("sessionId") int sessionId,
                                                                                              @RequestParam("schoolCode") String schoolCode){
        List<StudentIdCardDetails> studentIdCardDetailsList = null;
        try{
            studentIdCardDetailsList = studentIdCardService.searchByClassSectionAndSession(studentClassId, studentSectionId, sessionId, schoolCode);
            if(studentIdCardDetailsList != null && !studentIdCardDetailsList.isEmpty() ){
               for(StudentIdCardDetails studentIdCardDetails : studentIdCardDetailsList){
                   List<StudentIdCardDetails> studentImages = studentIdCardService.getAllStudentImage(schoolCode, studentIdCardDetails.getStudentId());
                   if(studentImages !=null && !studentImages.isEmpty()){
                       studentIdCardDetails.setStudentImageUrl(studentImages.get(0).getStudentImageUrl());
                   }else{
                       studentIdCardDetails.setStudentImageUrl(null);
                   }
               }
            }
            return ResponseEntity.ok(studentIdCardDetailsList);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
