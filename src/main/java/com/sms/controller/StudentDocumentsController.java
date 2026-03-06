package com.sms.controller;

import com.sms.service.StudentDocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student-documents")
public class StudentDocumentsController {
    @Autowired
    private StudentDocumentsService studentDocumentsService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocuments(
            @RequestParam(required = false)MultipartFile aadharCard,
            @RequestParam(required = false) MultipartFile birthCertificate,
            @RequestParam(required = false) MultipartFile transferCertificate,
            @RequestParam(required = false) MultipartFile marksheet,
            @RequestParam(required = false) MultipartFile incomeCertificate,
            @RequestParam(required = false) MultipartFile ewsCertificate,
            @RequestParam(required = false) MultipartFile casteCertificate,
            @RequestParam int studentId,
            @RequestParam String schoolCode

            ) throws Exception {
        try{
            Map<String, MultipartFile> documents = new HashMap<>();
            documents.put("aadharCard", aadharCard);
            documents.put("birthCertificate", birthCertificate);
            documents.put("transferCertificate", transferCertificate);
            documents.put("marksheet", marksheet);
            documents.put("incomeCertificate", incomeCertificate);
            documents.put("ewsCertificate", ewsCertificate);
            documents.put("casteCertificate", casteCertificate);
            studentDocumentsService.uploadDocuments(documents, studentId, schoolCode);
            return ResponseEntity.ok("Student documents uploaded successfully");
        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Map<String, String>> downloadDocuments(@RequestParam int studentId, @RequestParam String schoolCode) throws Exception {

        Map<String, String> documents = studentDocumentsService.downloadDocuments(studentId, schoolCode);

        if (documents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(documents);
    }

}
