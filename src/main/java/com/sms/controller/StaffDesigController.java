package com.sms.controller;

import com.sms.model.StaffDesig;
import com.sms.model.StaffDesigResponce;
import com.sms.service.StaffDesigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class StaffDesigController {
    @Autowired
    private StaffDesigService staffDesigService;

    @PostMapping("/designation/add/{schoolCode}")
    public ResponseEntity<Object> addStaffDesig(@RequestBody StaffDesig staffDesig, @PathVariable String schoolCode) throws Exception {
        StaffDesig result = null;
        try {
            result = staffDesigService.addStaffDesig(staffDesig,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/designation/get/{sdId}/{schoolCode}")
    public ResponseEntity<Object> getDesigById(@PathVariable int sdId, @PathVariable String schoolCode) throws Exception {
        StaffDesig result = null;
        try {
            result = staffDesigService.getStaffDesignById(sdId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/designation/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllDesig(@PathVariable String schoolCode) {
        List<StaffDesig> result = null;
        try {
            result = staffDesigService.getAllStaffDesig(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/designation/update/{sdId}/{schoolCode}")
    public ResponseEntity<Object>updateStaffDesigById(@RequestBody StaffDesig staffDesig,@PathVariable int sdId, @PathVariable String schoolCode)
    {
        StaffDesig result=null;
        try{
            result=staffDesigService.updateStaffDesig(staffDesig,sdId,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @DeleteMapping("/designation/delete/{sdId}/{schoolCode}")
    public ResponseEntity<Object> deleteStaffDesig(@PathVariable int sdId, @PathVariable String schoolCode) throws Exception {
        boolean result = staffDesigService.deleteStaffDesig(sdId,schoolCode);
        if(result){
            StaffDesigResponce response = new StaffDesigResponce(result, 200,DELETE_STAFFDESIG_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StaffDesigResponce response = new StaffDesigResponce(result, 400, DELETE_STAFFDESIG_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
