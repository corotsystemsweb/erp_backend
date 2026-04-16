package com.sms.controller;

import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.service.ClassSubjectAllocationStudentCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class/subject/student-count")
public class ClassSubjectAllocationStudentCountController {

    @Autowired
    private ClassSubjectAllocationStudentCountService classSubjectAllocationStudentCountService;

    // Endpoint 1: Get all allocations with student count for a session
    @GetMapping("/all/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllSubjectAllocationsWithStudentCount(
            @PathVariable int sessionId,
            @PathVariable String schoolCode) {

        List<ClassSubjectAllocationDetails> result = null;
        try {
            result = classSubjectAllocationStudentCountService.getAllSubjectAllocationsWithStudentCount(sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint 2: Get filtered allocations (with optional parameters)
    @GetMapping("/filter/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getFilteredSubjectAllocations(
            @PathVariable int sessionId,
            @PathVariable String schoolCode,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer sectionId,
            @RequestParam(required = false) Integer subjectId) {

        List<ClassSubjectAllocationDetails> result = null;
        try {
            result = classSubjectAllocationStudentCountService.getSubjectAllocationsWithStudentCount(
                    sessionId, classId, sectionId, subjectId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint 3: Get student count for specific allocation (path variables)
    @GetMapping("/count/{sessionId}/{classId}/{sectionId}/{subjectId}/{schoolCode}")
    public ResponseEntity<Object> getStudentCountForAllocation(
            @PathVariable int sessionId,
            @PathVariable int classId,
            @PathVariable int sectionId,
            @PathVariable int subjectId,
            @PathVariable String schoolCode) {

        Integer result = null;
        try {
            result = classSubjectAllocationStudentCountService.getStudentCountForAllocation(
                    subjectId, classId, sectionId, sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint 4: Get student count for specific allocation (request params)
    @GetMapping("/count")
    public ResponseEntity<Object> getStudentCountForAllocationByParams(
            @RequestParam int sessionId,
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam int subjectId,
            @RequestParam String schoolCode) {

        Integer result = null;
        try {
            result = classSubjectAllocationStudentCountService.getStudentCountForAllocation(
                    subjectId, classId, sectionId, sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint 5: Get student count for specific allocation with custom response (like your reference)
    @GetMapping("/count/details/{sessionId}/{classId}/{sectionId}/{subjectId}/{schoolCode}")
    public ResponseEntity<Object> getStudentCountForAllocationWithDetails(
            @PathVariable int sessionId,
            @PathVariable int classId,
            @PathVariable int sectionId,
            @PathVariable int subjectId,
            @PathVariable String schoolCode) {

        try {
            Integer result = classSubjectAllocationStudentCountService.getStudentCountForAllocation(
                    subjectId, classId, sectionId, sessionId, schoolCode);

            if (result == null || result == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No students found for the given allocation");
            }

            // Custom response object
            StudentCountResponse response = new StudentCountResponse(
                    result,
                    HttpStatus.OK.value(),
                    "Student count retrieved successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            StudentCountResponse errorResponse = new StudentCountResponse(
                    0,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error fetching student count: " + e.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Inner class for response wrapper
    static class StudentCountResponse {
        private int studentCount;
        private int statusCode;
        private String message;

        public StudentCountResponse(int studentCount, int statusCode, String message) {
            this.studentCount = studentCount;
            this.statusCode = statusCode;
            this.message = message;
        }

        public int getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(int studentCount) {
            this.studentCount = studentCount;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}