package com.sms.controller;

import com.sms.model.FrequencyResponse;
import com.sms.model.SchoolFeeDetails;
import com.sms.model.SchoolFeeResponse;
import com.sms.service.SchoolFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class SchoolFeeController {
    @Autowired
    private SchoolFeeService schoolFeeService;
    @PostMapping("/school/fee/add/{schoolCode}")
    public ResponseEntity<Object> addSchoolFee(@RequestBody SchoolFeeDetails schoolFeeDetails, @PathVariable String schoolCode) throws Exception{
        SchoolFeeDetails result = null;
        try{
            result = schoolFeeService.addSchoolFee(schoolFeeDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/school/fee")
    public ResponseEntity<Object> getSchoolFeeById(@RequestParam("feeId") int feeId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        SchoolFeeDetails result = null;
        try {
            result = schoolFeeService.getSchoolFeeById(feeId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/school/all/fee/{schoolCode}")
    public ResponseEntity<Object> getAllSchoolFee(@PathVariable String schoolCode) throws Exception {
        List<SchoolFeeDetails> result = null;

        try {
            result = schoolFeeService.getAllSchoolFee(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/school/fee/update/{feeId}/{schoolCode}")
    public ResponseEntity<Object> updateSchoolFeeById(@RequestBody SchoolFeeDetails schoolFeeDetails, @PathVariable int feeId, @PathVariable String schoolCode) throws Exception{
        SchoolFeeDetails result = null;
        try{
            result = schoolFeeService.updateSchoolFeeById(schoolFeeDetails, feeId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/school/fee/delete")
    public ResponseEntity<Object> deleteSchoolFee(@RequestParam("feeId") int feeId, @RequestParam ("schoolCode") String schoolCode) throws Exception {
        boolean result = schoolFeeService.deleteSchoolFee(feeId, schoolCode);
        if (result) {
            // If deletion is successful
            SchoolFeeResponse response = new SchoolFeeResponse(result, 200, DELETE_FEE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // If deletion fails
            FrequencyResponse response = new FrequencyResponse(result, 400, DELETE_FEE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}