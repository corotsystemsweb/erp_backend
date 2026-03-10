package com.sms.controller;

import com.sms.service.DdlForAddStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DdlForAddStudentController {
    @Autowired
    private DdlForAddStudentService ddlForAddStudentService;
    @PostMapping("/add")
    public ResponseEntity<String> executeDDL(@RequestParam("schoolCode")String schoolCode) throws Exception {
        String result=null;
        try{
            result = ddlForAddStudentService.addDdlForStudentDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
