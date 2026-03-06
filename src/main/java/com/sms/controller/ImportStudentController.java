package com.sms.controller;

import com.sms.exception.DuplicatePromotionException;
import com.sms.exception.InvalidPromotionException;
import com.sms.model.HrPayroleDetails;
import com.sms.model.ImportStudent;
import com.sms.service.ImportStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImportStudentController {
    @Autowired
    private ImportStudentService importStudentService;
    @GetMapping("/get/{schoolCode}")
    public ResponseEntity<Object> getStudentForImport(
            @PathVariable String schoolCode,
            @RequestParam(required = false) Integer sessionId,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer sectionId) {

        try {
            List<ImportStudent> result = importStudentService.getStudentDetails(sessionId, classId, sectionId, schoolCode);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching student details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/promote/{schoolCode}/{currentSessionId}")
    public ResponseEntity<?> promoteStudents(
            @PathVariable String schoolCode, // Change Integer to String if schoolCode is alphanumeric
            @PathVariable Integer currentSessionId,
            @RequestBody ImportStudent request
    ) {
        try {
            importStudentService.promoteStudents(
                    request,
                    currentSessionId,
                    schoolCode
            );
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Students promoted successfully"
            ));
        } catch (InvalidPromotionException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (DuplicatePromotionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/graduation-eligible/{sessionId}/{classId}/{sectionId}/{schoolCode}")
    public ResponseEntity<List<ImportStudent>> getGraduationEligibleStudents(
            @PathVariable Integer sessionId,
            @PathVariable Integer classId,
            @PathVariable Integer sectionId,
            @PathVariable String schoolCode) {
        try {
            List<ImportStudent> students = importStudentService.getGraduationEligibleStudents(
                    sessionId, classId, sectionId, schoolCode);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/graduate/{schoolCode}")
    public ResponseEntity<?> graduateStudents(
            @RequestBody ImportStudent request,
            @PathVariable String schoolCode) {
        try {
            ImportStudent importStudent = new ImportStudent();
            importStudent.setStudentIds(request.getStudentIds());
            importStudent.setClassId(request.getClassId());
            System.out.println("sessionId"+request.getSessionId());
            importStudentService.graduateStudents(
                    importStudent,
                    request.getSessionId(),
                    schoolCode);

            return ResponseEntity.ok().body(
                    Map.of("message", "Students graduated successfully"));

        } catch (InvalidPromotionException | DuplicatePromotionException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Graduation failed: " + e.getMessage()));
        }
    }


}
