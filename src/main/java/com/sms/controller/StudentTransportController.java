package com.sms.controller;

import com.sms.model.StudentTransportDetails;
import com.sms.model.StudentTransportRequest;
import com.sms.model.TransportCloseRequest;
import com.sms.service.StudentTransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/transport")
public class StudentTransportController {
    @Autowired
    private StudentTransportService studentTransportService;

    //ADD TRANSPORT SEPARATELY
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<?> addTransport(@RequestBody StudentTransportRequest request, @PathVariable String schoolCode) {
        try {
            StudentTransportDetails result = studentTransportService.addStudentTransport(request.getTransportDetails(), request.getTransportFeeDue(), schoolCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // UPDATE TRANSPORT SEPARATELY
    @PutMapping("/update/{schoolCode}")
    public ResponseEntity<?> updateTransport(@RequestBody StudentTransportRequest request, @PathVariable String schoolCode) {
        try {
            StudentTransportDetails result = studentTransportService.updateStudentTransport(request.getTransportDetails(), request.getTransportFeeDue(), schoolCode);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/close/{schoolCode}")
    public ResponseEntity<?> closeTransport(@RequestBody TransportCloseRequest request, @PathVariable String schoolCode){
        try{
            String result = studentTransportService.closeTransPortFee(request, schoolCode);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
