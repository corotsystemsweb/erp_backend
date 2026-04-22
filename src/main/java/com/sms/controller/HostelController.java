
package com.sms.controller;
import com.sms.model.*;
import com.sms.model.HostelDetails;
import com.sms.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

import static com.sms.appenum.Message.*;
@RestController
@RequestMapping("/api")
public class HostelController {

    @Autowired
    private HostelService hostelService;

    @PostMapping("/hostel/add/{schoolCode}")
    public ResponseEntity<Object> addHostel(
            @RequestBody HostelDetails hostelDetails,
            @PathVariable String schoolCode
    ) throws Exception {
        try {
            HostelDetails result = hostelService.addHostel(hostelDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/hostel/get/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> getHostelById(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            HostelDetails result = hostelService.getHostelById(schoolCode, hostelId);
            if (result == null) {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/hostel/all/{schoolCode}")
    public ResponseEntity<Object> getAllHostels(@PathVariable String schoolCode) throws Exception {
        try {
            List<HostelDetails> result = hostelService.getAllHostels(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/hostel/update/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> updateHostel(
            @PathVariable String schoolCode,
            @PathVariable int hostelId,
            @RequestBody HostelDetails hostelDetails
    ) throws Exception {
        try {
            hostelDetails.setHostelId(hostelId);
            HostelDetails result = hostelService.updateHostel(hostelDetails, schoolCode);
            if (result == null) {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/hostel/delete/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> deleteHostel(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            boolean result = hostelService.deleteHostel(schoolCode, hostelId); // Soft delete
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}