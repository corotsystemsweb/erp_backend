package com.sms.controller;

import com.sms.model.DiscountCodeDetails;
import com.sms.model.FeeDepositDetails;
import com.sms.model.HolidayDetails;
import com.sms.model.StudentFeeCollectionDetails;
import com.sms.service.FeeDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeeDepositController {
    @Autowired
    private FeeDepositService feeDepositService;
    @PostMapping("/fee/deposit/add/{schoolCode}")
    public ResponseEntity<Object> addFeeDeposit(@RequestBody FeeDepositDetails feeDepositDetails, @PathVariable String schoolCode) throws Exception{
        FeeDepositDetails result = null;
        try{
            result =feeDepositService.addFeeDeposit(feeDepositDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/fee/deposit/{fdId}/{schoolCode}")
    public ResponseEntity<Object> getFeeDepositById(@PathVariable int fdId, @PathVariable String schoolCode) throws Exception {
        FeeDepositDetails result = null;
        try {
            result = feeDepositService.getFeeDepositById(fdId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/fee/deposit/{schoolCode}")
    public ResponseEntity<Object> getAllFeeDeposit(@PathVariable String schoolCode) throws Exception {
        List<FeeDepositDetails> result = null;

        try {
            result = feeDepositService.getAllFeeDeposit(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/fee/deposit/{fdId}/{schoolCode}")
    public ResponseEntity<Object> updateFeeDepositById(@RequestBody FeeDepositDetails feeDepositDetails, @PathVariable int fdId, @PathVariable String schoolCode) throws Exception{
        FeeDepositDetails result = null;
        try{
            result = feeDepositService.updateFeeDepositById(feeDepositDetails, fdId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/total/fee/deposit/{schoolCode}")
    public ResponseEntity<Object> getTotalFeeDeposit(@PathVariable String schoolCode) throws Exception{
        Double result = null;
        try{
            result = feeDepositService.getTotalFeeDeposit(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/students/total/amount/paid/{schoolCode}")
    public ResponseEntity<Object> getTotalAmountPaidByStudents(@PathVariable String schoolCode) throws Exception{
        List<FeeDepositDetails> result = null;
        try {
            result = feeDepositService.getTotalAmountPaidByStudents(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   /* @GetMapping("/get/students/total/amount/paid/{studentId}/{schoolCode}")
    public ResponseEntity<Object> getTotalAmountPaidByParticularStudent(@PathVariable int studentId, @PathVariable String schoolCode) throws Exception{
        FeeDepositDetails result = null;
        try {
            result = feeDepositService.getTotalAmountPaidByParticularStudent(studentId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
   @GetMapping("/get/students/total/amount/paid/{studentId}/{schoolCode}")
   public ResponseEntity<Object> getTotalAmountPaidByParticularStudent(@PathVariable int studentId, @PathVariable String schoolCode) throws Exception {
       FeeDepositDetails result = feeDepositService.getTotalAmountPaidByParticularStudent(studentId, schoolCode);
       if (result != null) {
           return new ResponseEntity<>(result, HttpStatus.OK);
       } else {
           return new ResponseEntity<>("Details not found", HttpStatus.NOT_FOUND);
       }
   }
    @GetMapping("get/yearly/total/deposit/amount/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getYearlyTotalDeposit(@PathVariable int sessionId, @PathVariable String schoolCode) throws Exception{
        List<FeeDepositDetails> result = null;
        try{
            result = feeDepositService.getYearlyTotalDeposit(sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("unsettledStudentFee")
    public ResponseEntity<Object> getUnsettledFeesByClassSectionSessionAndStudentId(
            @RequestParam("classId") int classId,
            @RequestParam("sectionId") int sectionId,
            @RequestParam("sessionId") int sessionId,
            @RequestParam("studentId") int studentId,
            @RequestParam("schoolCode") String schoolCode) throws Exception{
        List<FeeDepositDetails> result = null;
        try{
            result = feeDepositService.getUnsettledFeesByClassSectionSessionAndStudentId(classId, sectionId, sessionId, studentId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
