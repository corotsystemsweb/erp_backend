package com.sms.controller;

import com.sms.model.StudentDueFeeDetails;
import com.sms.service.StudentDueFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentDueFeeController {
    @Autowired
    private StudentDueFeeService studentDueFeeService;

    @GetMapping("/due/fees/{schoolCode}")
    public ResponseEntity<List<StudentDueFeeDetails>> getStudentDueFees(@PathVariable String schoolCode) {
        try {
            List<StudentDueFeeDetails> dueFees = studentDueFeeService.calculateDueFees(schoolCode);
            return ResponseEntity.ok(dueFees); // Return the due fees with a 200 OK status
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // Handle exceptions gracefully
        }
    }
    @GetMapping("/due/fees/{studentId}/{schoolCode}")
    public ResponseEntity<StudentDueFeeDetails> calculateDueFeeBasedOnStudentId(@PathVariable int studentId, @PathVariable String schoolCode) {
        try {
            StudentDueFeeDetails dueFees = studentDueFeeService.calculateDueFeeBasedOnStudentId(studentId,schoolCode);
            return ResponseEntity.ok(dueFees); // Return the due fees with a 200 OK status
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // Handle exceptions gracefully
        }
    }
    @GetMapping("/due/fees/search")
    public ResponseEntity<?> searchDueFees(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode) {
        try {
            List<StudentDueFeeDetails> dueFees = studentDueFeeService.calculateDueFeesBySearchText(searchText, schoolCode);
            if (dueFees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No records found!");
            }
            return ResponseEntity.ok(dueFees);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }
}
