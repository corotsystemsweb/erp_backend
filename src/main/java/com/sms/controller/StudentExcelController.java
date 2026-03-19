package com.sms.controller;

import com.sms.model.StudentExcelDetails;
import com.sms.service.StudentExcelService;
import com.sms.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;


import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/studentDetails")
public class StudentExcelController {
    @Autowired
    private StudentExcelService studentExcelService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadExcelForStudentDetails(@RequestParam("file") MultipartFile file, @RequestParam("schoolCode") String schoolCode) {
        if (!ExcelHelper.checkExcelFormat(file)) {
            return new ResponseEntity<>("Invalid file format. Please upload an Excel file.", HttpStatus.BAD_REQUEST);
        }
        try {
            List<StudentExcelDetails> students = studentExcelService.processExcelFileForStudentExcelDetails(file, schoolCode);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            // Print stack trace for debugging and return an Internal Server Error response
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/excel/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            InputStreamResource resource = studentExcelService.validateAndFetchFile(fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
            pd.setType(URI.create("http://my-app-host.com/errors/misc"));
            pd.setTitle("Error processing the file");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error");
            pd.setType(URI.create("http://my-app-host.com/errors/misc"));
            pd.setTitle("Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
        }
    }
}
