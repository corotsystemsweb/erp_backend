package com.sms.controller;

import com.sms.model.MarkSheetDetails;
import com.sms.service.MarkSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/all/student/mark")
public class MarkSheetController {
    @Autowired
    private MarkSheetService markSheetService;

    @GetMapping("/get")
    public ResponseEntity<List<MarkSheetDetails>> searchAllStudentMarks(
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam int sessionId,
            @RequestParam String schoolCode
    ){
        try{
            List<MarkSheetDetails> result = markSheetService.searchAllStudentMarks(classId, sectionId, sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
