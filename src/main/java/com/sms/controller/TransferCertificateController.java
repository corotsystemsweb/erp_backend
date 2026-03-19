
package com.sms.controller;

import com.sms.model.TransferCertificateDetails;
import com.sms.service.TransferCertificateService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/transfer-certificates")
public class TransferCertificateController {
    private final TransferCertificateService transferCertificateService;

    public TransferCertificateController(TransferCertificateService transferCertificateService) {
        this.transferCertificateService = transferCertificateService;
    }

    @PostMapping("/issue/{schoolCode}")
    public ResponseEntity<TransferCertificateDetails> issueTransferCertificate(
            @RequestBody TransferCertificateDetails tc,
            @PathVariable String schoolCode) {
        try {
            System.out.println(tc);
            TransferCertificateDetails savedTc = transferCertificateService.issueTransferCertificate(tc, schoolCode);
            return ResponseEntity.ok(savedTc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/pdf/{tcId}/{schoolCode}")
    public ResponseEntity<byte[]> getTCPdf(@PathVariable Long tcId, @PathVariable String schoolCode) {
        System.out.println("controller"+tcId);
        try {
            byte[] pdfBytes = transferCertificateService.generateTCAsPDF(tcId, schoolCode);
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
    public ResponseEntity<TransferCertificateDetails> getTc(
            @PathVariable Long tcId,
            @PathVariable String schoolCode) {

        TransferCertificateDetails result = null;
        try {
            result = transferCertificateService.getTC(tcId,schoolCode);

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
    public ResponseEntity<List<TransferCertificateDetails>> getAllTc(
            @PathVariable int sessionId,
            @PathVariable String schoolCode) {

        List<TransferCertificateDetails> result = null;
        try {
            result = transferCertificateService.getAllTc(sessionId, schoolCode);

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
            @RequestBody TransferCertificateDetails tcDetails) {
        try {
            TransferCertificateDetails updatedTC = transferCertificateService.updateTC(tcDetails, tcId, schoolCode);
            return ResponseEntity.ok(updatedTC);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating transfer certificate");
        }
    }

    @GetMapping("/tc/admissionNo/{admissionNo}/{schoolCode}")
    public ResponseEntity<TransferCertificateDetails> getTcByAdmissionNo(
            @PathVariable String admissionNo,
            @PathVariable String schoolCode) {
        try {
            TransferCertificateDetails savedTc = transferCertificateService.getTcByAdmissionNo(admissionNo, schoolCode);
            return ResponseEntity.ok(savedTc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}


