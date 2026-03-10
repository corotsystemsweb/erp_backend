package com.sms.controller;

import com.sms.service.DynamicTablesGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executeDdl")
public class DynamicTablesGeneratorController {
    @Autowired
    private DynamicTablesGeneratorService dynamicTablesGeneratorService;
    @PostMapping("/add")
    public ResponseEntity<String> executeDdl(@RequestParam("schoolCode") String schoolCode) throws Exception{
        String result = null;
        try{
            result = dynamicTablesGeneratorService.executeDdl(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
