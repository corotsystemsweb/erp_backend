package com.sms.controller;

import com.sms.appenum.Message;
import com.sms.model.FeeAssignmentDetails;
import com.sms.model.SubjectDetails;
import com.sms.service.FeeAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeeAssignmentController {
    @Autowired
    private FeeAssignmentService feeAssignmentService;
    @PostMapping("/fee/assignment/{schoolCode}")
    public ResponseEntity<Object> addFeeAssignment(@RequestBody FeeAssignmentDetails feeAssignmentDetails, @PathVariable String schoolCode) {
        FeeAssignmentDetails result = null;
        try {
             result = feeAssignmentService.addFeeAssignment(feeAssignmentDetails, schoolCode);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Message.FEE_ASSIGNMENT_FAILED.val());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Message.FEE_ASSIGNMENT_FAILED.val());
        }
    }
    //get fee assignment details based on sessionId, classId, sectionId and studentId
    @GetMapping("/get/fee/assignments")
    public ResponseEntity<List<FeeAssignmentDetails>> getFeeAssignments(
            @RequestParam("classId") int classId,
            @RequestParam(value = "sectionId", required = false) Integer sectionId,
            @RequestParam("sessionId") int sessionId,
            @RequestParam(value = "studentId", required = false) Integer studentId,
            @RequestParam(value = "schoolCode") String schoolCode) throws Exception {

        List<FeeAssignmentDetails> feeAssignments = feeAssignmentService.getFeeAssignments(classId, sectionId, sessionId, studentId, schoolCode);
        return new ResponseEntity<>(feeAssignments, HttpStatus.OK);
    }
    //get fee assignment details by id
    @GetMapping("/fee/assignment/{faId}/{schoolCode}")
    public ResponseEntity<Object> getFeeAssignmentById(@PathVariable int faId, @PathVariable String schoolCode) throws Exception {
        FeeAssignmentDetails result = null;
        try {
            result = feeAssignmentService.getFeeAssignmentById(faId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //get all fee assignment details
    @GetMapping("/fee/assignment/{schoolCode}")
    public ResponseEntity<Object> getAllFeeAssignmentBy(@PathVariable String schoolCode) throws Exception {
        List<FeeAssignmentDetails> result = null;
        try {
            result = feeAssignmentService.getAllFeeAssignment(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/fee/discount/{faId}/{amount}/{schoolCode}")
    public ResponseEntity<Object> getDiscountDetails(@PathVariable int faId, @PathVariable double amount, @PathVariable String schoolCode) throws Exception{
        FeeAssignmentDetails result = null;
        try{
            result = feeAssignmentService.getDiscountDetails(faId, amount, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   /* @GetMapping("/search/text/fee/assignment")
    public ResponseEntity<Object> getFeeAssignmentDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<FeeAssignmentDetails> result = feeAssignmentService.getFeeAssignmentDetailsBySearchText(searchText, schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.UNAUTHORIZED);
        }
    }*/
   @GetMapping("/search/fee/assignment/{sessionId}")
   public ResponseEntity<Object> getFeeAssignmentDetailsByFilters(
           @RequestParam("schoolCode") String schoolCode,
           @PathVariable Integer sessionId,
           @RequestParam(value = "classId", required = false) Integer classId,
           @RequestParam(value = "sectionId", required = false) Integer sectionId,
           @RequestParam(value = "studentId", required = false) Integer studentId,
           @RequestParam(value = "searchText", required = false) String searchText) {
       try {
           List<FeeAssignmentDetails> result = feeAssignmentService.getFeeAssignmentDetailsByFilters(
                   schoolCode, sessionId,classId, sectionId, studentId, searchText);

           if (result.isEmpty()) {
               return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
           }
           return new ResponseEntity<>(result, HttpStatus.OK);
       } catch (Exception e) {
           return new ResponseEntity<>("An error occurred while processing the request.", HttpStatus.UNAUTHORIZED);
       }
   }


    @GetMapping("/get/feeAssignment/byFaIdAndSession/{faId}/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getFeeAssignmentByFaIdAndSession(@PathVariable int faId, @PathVariable int sessionId, @PathVariable String schoolCode){
        try{
            List<FeeAssignmentDetails> result = feeAssignmentService.getFeeAssignmentByFaIdAndSession(faId, sessionId, schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.UNAUTHORIZED);
        }
    }
}
