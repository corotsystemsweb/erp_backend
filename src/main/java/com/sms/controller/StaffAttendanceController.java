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

    @GetMapping("/attendance/{schoolCode}")
    public ResponseEntity<Object> getStaffAttendance(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer designationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @PathVariable String schoolCode) throws Exception {
        try{
            List<StaffAttendanceDetails> result = staffAttendanceService.getStaffAttendance(staffId, departmentId, designationId, date, schoolCode);

            // No records found
            if (result == null || result.isEmpty()) {
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
