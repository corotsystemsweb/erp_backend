package com.sms.controller;

import com.sms.model.ManageFeeTypeDetails;
import com.sms.service.ManageFeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/manage-fee-types")
public class ManageFeeTypeController {

    @Autowired
    private ManageFeeTypeService manageFeeTypeService;

    @GetMapping("/{sessionId}/{schoolCode}")
    public ResponseEntity<?> getManageFeeType(@PathVariable int sessionId, @PathVariable String schoolCode){
        try {
            List<ManageFeeTypeDetails> result = manageFeeTypeService.getManageFeeType(sessionId, schoolCode);

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No records found");
            }
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
}
