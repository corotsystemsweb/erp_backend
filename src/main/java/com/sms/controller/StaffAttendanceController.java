package com.sms.controller;

import com.sms.model.StaffAttendanceDetails;
import com.sms.service.StaffAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffAttendanceController {
    @Autowired
    private StaffAttendanceService staffAttendanceService;
    @GetMapping("/getAll/{schoolCode}")
    public ResponseEntity<Object> getAllStaffDetails(@PathVariable String schoolCode) throws Exception{
        List<StaffAttendanceDetails> result = null;
        try{
            result = staffAttendanceService.getAllStaffDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/add/attendance/{schoolCode}")
    public ResponseEntity<Object> addStaffAttendanceDetails(@RequestBody List<StaffAttendanceDetails> staffAttendanceDetails, @PathVariable String schoolCode) throws Exception{
        List<StaffAttendanceDetails> result = null;
        try{
            result = staffAttendanceService.addStaffAttendanceDetails(staffAttendanceDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/getAll/attendance/{schoolCode}")
    public ResponseEntity<Object> getAllStaffAttendanceDetails(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) Integer designationId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @PathVariable String schoolCode) throws Exception{
        List<StaffAttendanceDetails> result = null;
        try{
            result = staffAttendanceService.getAllStaffAttendanceDetails(staffId, staffName, designationId, dateFrom, dateTo, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
}
