package com.sms.controller;

import com.sms.exception.StudentNotFoundException;
import com.sms.model.FeeDueDateDetails;
import com.sms.model.StaffDetails;
import com.sms.service.FeeDueDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeeDueDateController {
    @Autowired
    private FeeDueDateService feeDueDateService;

    @PostMapping("/add/fee/due/date/{schoolCode}")
    public ResponseEntity<Object> addFeeDueDetails(@RequestBody FeeDueDateDetails feeDueDateDetails, @PathVariable String schoolCode)throws Exception
    {
        FeeDueDateDetails result=null;
        try{
            result = feeDueDateService.addFeeDueDetails(feeDueDateDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
   /* @PostMapping("/due/date/add")
    public ResponseEntity<List<FeeDueDateDetails>> addFeeDueDetailsList(@RequestBody List<FeeDueDateDetails> feeDueDateDetailsList) {
        List<FeeDueDateDetails> result = null;
        try {
            result = feeDueDateService.addFeeDueDetailsList(feeDueDateDetailsList);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
   @PostMapping("/due/date/add/{schoolCode}")
   public ResponseEntity<Object> addFeeDueDetailsList(@RequestBody List<FeeDueDateDetails> feeDueDateDetailsList, @PathVariable String schoolCode) {
       try {
           List<FeeDueDateDetails> result = feeDueDateService.addFeeDueDetailsList(feeDueDateDetailsList, schoolCode);
           return new ResponseEntity<>(result, HttpStatus.CREATED);
       } catch (DataIntegrityViolationException e) {
           return new ResponseEntity<>("Data validation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
       } catch (Exception e) {
           return new ResponseEntity<>("Error processing request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
   @GetMapping("/get/yearly/exact/fee/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getExactFee(@PathVariable int sessionId, @PathVariable String schoolCode) throws Exception{
       List<FeeDueDateDetails> result = null;
       try {
           result = feeDueDateService.getExactFee(sessionId, schoolCode);
           return new ResponseEntity<>(result, HttpStatus.OK);
       }catch (Exception e){
           return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
   @PutMapping("update/feeDueDate/{faId}/{schoolCode}")
   public ResponseEntity<Object> updateFeeDueDate(@RequestBody List<FeeDueDateDetails> feeDueDateDetailsList, @PathVariable int faId, @PathVariable String schoolCode){
       try{
           feeDueDateService.updateFeeDueDate(feeDueDateDetailsList, faId, schoolCode);
           return  ResponseEntity.ok("Fees updated successfully");
       }catch(Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating fees");
       }
   }
}
