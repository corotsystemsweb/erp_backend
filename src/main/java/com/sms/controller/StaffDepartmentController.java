package com.sms.controller;

import com.sms.model.StaffDepartment;
import com.sms.model.StaffDepartmentResponse;
import com.sms.model.StaffDesigResponce;
import com.sms.service.StaffDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/department")
public class StaffDepartmentController {
    @Autowired
    private StaffDepartmentService staffDepartmentService;

    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addStaffDepartment(@RequestBody StaffDepartment staffDepartment, @PathVariable String schoolCode) throws Exception {
        StaffDepartment result = null;
        try {
            result = staffDepartmentService.addStaffDepartment(staffDepartment,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get/{stDpId}/{schoolCode}")
    public ResponseEntity<Object> getStaffDepartmentById(@PathVariable int stDpId, @PathVariable String schoolCode) throws Exception {
        StaffDepartment result = null;
        try {
            result = staffDepartmentService.getStaffDepartmentById(stDpId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllStaffDepartment(@PathVariable String schoolCode) {
        List<StaffDepartment> result = null;
        try {
            result = staffDepartmentService.getAllStaffDepartment(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{stDpId}/{schoolCode}")
    public ResponseEntity<Object>updateStaffDesigById(@RequestBody StaffDepartment staffDepartment,@PathVariable int stDpId, @PathVariable String schoolCode)
    {
        StaffDepartment result=null;
        try{
            result=staffDepartmentService.updateStaffDepartment(staffDepartment,stDpId,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @DeleteMapping("/delete/{stDpId}/{schoolCode}")
    public ResponseEntity<Object> deleteStaffDepartment(@PathVariable int stDpId, @PathVariable String schoolCode) throws Exception {
        boolean result = staffDepartmentService.deleteStaffDepartment(stDpId,schoolCode);
        if(result){
            StaffDepartmentResponse response = new StaffDepartmentResponse(result, 200,DELETE_STAFFDEPARTMENT_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StaffDepartmentResponse response = new StaffDepartmentResponse(result, 400, DELETE_STAFFDEPARTMENT_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
