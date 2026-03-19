package com.sms.controller;

import com.sms.exception.StudentNotFoundException;
import com.sms.model.*;
import com.sms.service.FeeDepositDetailsService;
import com.sms.util.DatabaseUtil;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-deposit-details")
public class FeeDepositDetailsController {
    @Autowired
    private FeeDepositDetailsService feeDepositDetailsService;
    private final JdbcTemplate jdbcTemplate;

    public FeeDepositDetailsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   /* @PostMapping("/add/{schoolCode}")
    public ResponseEntity<List<FeeDepositDetails>> addFeeDepositDetails(@RequestBody List<FeeDepositDetails> feeDepositDetails, @PathVariable String schoolCode){
        List<FeeDepositDetails> result = null;
        try{
            result = feeDepositDetailsService.addFeeDepositDetails(feeDepositDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }*/

    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<CombinedFeeDepositResponse> addFeeDepositDetailsWithTotal(@RequestBody FeeDepositRequest feeDepositRequest, @PathVariable String schoolCode) {
        try {
            CombinedFeeDepositResponse result = feeDepositDetailsService.addFeeDepositDetails(feeDepositRequest, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{classId}/{sectionId}/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getStudentFeeDetailsBasedOnClassSectionSession(@PathVariable int classId, @PathVariable int sectionId, @PathVariable int sessionId, @PathVariable String schoolCode) throws Exception {
        List<FeeDepositDetails> result = feeDepositDetailsService.getStudentFeeDetailsBasedOnClassSectionSession(classId, sectionId, sessionId, schoolCode);
        if (result == null || result.isEmpty()) {
            throw new StudentNotFoundException("Fee details not found for studentId: " + classId + ", sectionId: " + sectionId + ", sessionId: " + sessionId);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/fee/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllStudentFeeDetails(@PathVariable int sessionId, @PathVariable String schoolCode) throws Exception {
        List<FeeDepositDetails> result = feeDepositDetailsService.getAllStudentFeeDetails(sessionId, schoolCode);
        if (result == null || result.isEmpty()) {
            throw new StudentNotFoundException("Fee details not found");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/student-fee-segregation")
    public ResponseEntity<Object> getStudentFeeSegregation(@RequestParam("schoolId") int schoolId, @RequestParam("sessionId") int sessionId,
                                                           @RequestParam("classId") int classId, @RequestParam("sectionId") int sectionId,
                                                           @RequestParam("studentId") int studentId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        List<FeeDepositDetails> result = null;
        try {
            result = feeDepositDetailsService.getStudentFeeSegregation(schoolId, sessionId, classId, sectionId, studentId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*@GetMapping("/screen")
    public ResponseEntity<Object> getAcademicFessForSubmit(@RequestParam("schoolId") int schoolId, @RequestParam("sessionId") int sessionId,
                                                           @RequestParam("studentId") int studentId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        Map<String, Object> result = null;
        try {
            result =  feeDepositDetailsService.getMonthlyDueDetailsForScreen(schoolId,sessionId,studentId,schoolCode);
            System.out.println(result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
    @GetMapping("/screen")
    public ResponseEntity<Object> getAcademicFessForSubmit(
            @RequestParam("schoolId") int schoolId,
            @RequestParam("sessionId") int sessionId,
            @RequestParam("schoolCode") String schoolCode,
            @RequestParam(value = "studentId", required = false) Integer studentId,
            @RequestParam(value = "admissionNo", required = false) String admissionNo,
            @RequestParam(value = "registrationNo", required = false) String registrationNo
    ) throws Exception {
        Map<String, Object> result = null;
        try {
            if (studentId == null) {
                JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

                if (admissionNo != null) {
                    studentId = jdbcTemplate.queryForObject(
                            "SELECT student_id FROM student_academic_details WHERE admission_no = ? AND school_id = ? AND session_id = ?",
                            new Object[]{admissionNo, schoolId, sessionId},
                            Integer.class
                    );
                } else if (registrationNo != null) {
                    System.out.println("reg"+registrationNo+"sch"+schoolId+"sess"+sessionId);
                    studentId = jdbcTemplate.queryForObject(
                            "SELECT student_id FROM student_academic_details WHERE registration_number = ? AND school_id = ? AND session_id = ?",
                            new Object[]{registrationNo, schoolId, sessionId},
                            Integer.class
                    );
                }

                DatabaseUtil.closeDataSource(jdbcTemplate);
            }

            if (studentId == null) {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }

            result = feeDepositDetailsService.getMonthlyDueDetailsForScreen(schoolId, sessionId, studentId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/receipt/{classId}/{sectionId}/{sessionId}/{studentId}/{schoolCode}")
    public ResponseEntity<Object> getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(@PathVariable int classId, @PathVariable int sectionId, @PathVariable int sessionId, @PathVariable int studentId, @PathVariable String schoolCode) throws Exception {
        FeeDepositDetails result = feeDepositDetailsService.getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(classId, sectionId, sessionId, studentId, schoolCode);
        if (result == null) {
            throw new StudentNotFoundException("Fee details not found for studentId: " + classId + ", sectionId: " + sectionId + ", sessionId: " + sessionId + ", studentId: " + studentId);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{fdId}/school/{schoolId}/{schoolCode}")
    public ResponseEntity<List<FeeDepositDetails>> getFeeDepositDetails(
            @PathVariable int fdId,
            @PathVariable int schoolId,
            @PathVariable String schoolCode
    ) throws Exception {
        List<FeeDepositDetails> details = feeDepositDetailsService.getFeeDepositDetails(fdId, schoolId, schoolCode);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/fee/history/{schoolCode}")
    public ResponseEntity<?> getFeeDeposits(
            @PathVariable String schoolCode,
            @RequestParam int schoolId,
            @RequestParam int sessionId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) String studentName
            ) {

        try {
            List<FeeDepositDetails> result = feeDepositDetailsService.getFeeDeposits(
                    schoolCode,
                    schoolId,
                    sessionId,
                    studentId,
                    studentName
            );
            return ResponseEntity.ok(result);
        } catch (ServiceException e) {
            return handleException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("message", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

}
