package com.sms.controller;

import com.sms.model.StudentDueFeeDetails;
import com.sms.model.YearlyDueAmountDetails;
import com.sms.service.YearlyDueAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class YearlyDueAmountController {
    @Autowired
    private YearlyDueAmountService yearlyDueAmountService;
    @GetMapping("/due/amount/yearly/{sessionId}/{schoolCode}")
    public ResponseEntity<List<YearlyDueAmountDetails>> getYearlyDueAmounts(@PathVariable int sessionId, @PathVariable String schoolCode) {
        try {
            List<YearlyDueAmountDetails> dueAmount = yearlyDueAmountService.calculateYearlyDueAmounts(sessionId, schoolCode);
            return ResponseEntity.ok(dueAmount); // Return the due fees with a 200 OK status
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // Handle exceptions gracefully
        }
    }
}
