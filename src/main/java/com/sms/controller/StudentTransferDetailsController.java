package com.sms.controller;

import com.sms.model.StudentTransferCertificateDetails;
import com.sms.service.StudentTransferDetailsService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/student/transfer-certificate")
public class StudentTransferDetailsController {
    private final StudentTransferDetailsService studentTransferDetailsService;

    public StudentTransferDetailsController(StudentTransferDetailsService studentTransferDetailsService) {
        this.studentTransferDetailsService = studentTransferDetailsService;
    }

    @PostMapping("/issue/{schoolCode}")
    public ResponseEntity<StudentTransferCertificateDetails> issueTransferCertificate(
            @RequestBody StudentTransferCertificateDetails tc,
            @PathVariable String schoolCode) {
        try {
            System.out.println(tc);
            StudentTransferCertificateDetails savedTc = studentTransferDetailsService.issueTransferCertificate(tc, schoolCode);
            return ResponseEntity.ok(savedTc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/pdf/{tcId}/{schoolCode}")
    public ResponseEntity<byte[]> getTCPdf(@PathVariable Long tcId, @PathVariable String schoolCode) {
        System.out.println("controller"+tcId);
        try {
            byte[] pdfBytes = studentTransferDetailsService.generateTCAsPDF(tcId, schoolCode);
            if (pdfBytes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transfer_certificate_" + tcId + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/tc/{tcId}/{schoolCode}")
    public ResponseEntity<StudentTransferCertificateDetails> getTc(
            @PathVariable Long tcId,
            @PathVariable String schoolCode) {

        StudentTransferCertificateDetails result = null;
        try {
            result = studentTransferDetailsService.getTC(tcId,schoolCode);

            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @GetMapping("/tc/all/{sessionId}/{schoolCode}")
    public ResponseEntity<List<StudentTransferCertificateDetails>> getAllTc(
            @PathVariable int sessionId,
            @PathVariable String schoolCode) {

        List<StudentTransferCertificateDetails> result = null;
        try {
            result = studentTransferDetailsService.getAllTc(sessionId, schoolCode);

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.emptyList());
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @PutMapping("/{tcId}/{schoolCode}")
    public ResponseEntity<?> updateTransferCertificate(
            @PathVariable String schoolCode,
            @PathVariable Long tcId,
            @RequestBody StudentTransferCertificateDetails tcDetails) {
        try {
            StudentTransferCertificateDetails updatedTC = studentTransferDetailsService.updateTC(tcDetails, tcId, schoolCode);
            return ResponseEntity.ok(updatedTC);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating transfer certificate");
        }
    }
}
