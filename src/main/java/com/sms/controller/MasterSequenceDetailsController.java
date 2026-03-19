package com.sms.controller;

import com.sms.service.MasterSequenceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MasterSequenceDetailsController {
    @Autowired
    private MasterSequenceDetailsService masterSequenceDetailsService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addSeqCodeAndCurrentValue(@PathVariable String schoolCode) {
        try {
            masterSequenceDetailsService.addSeqCodeAndCurrentValue(schoolCode);
            return ResponseEntity.ok().build(); // HTTP 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }
}
