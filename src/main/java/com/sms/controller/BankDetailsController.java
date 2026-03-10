package com.sms.controller;

import com.sms.model.BankDetails;
import com.sms.model.BankDetailsResponse;
import com.sms.model.StudentDetails;
import com.sms.model.StudentResponse;
import com.sms.service.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/bankdetails")
public class BankDetailsController {
    @Autowired
    private BankDetailsService bankDetailsService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object>addBankDetails(@RequestBody BankDetails bankDetails, @PathVariable String schoolCode) throws Exception{
        BankDetails result=null;
        try{
            result=bankDetailsService.addBankDetails(bankDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/{schoolCode}")
    public ResponseEntity<Object> getAllStaffBankDetails(@PathVariable String schoolCode) throws Exception {
        List<BankDetails> result = null;
        try {
            result = bankDetailsService.getAllStaffBankDetails(schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{bdId}/{schoolCode}")
    public ResponseEntity<Object> getAllStaffBankDetailsById(@PathVariable int bdId, @PathVariable String schoolCode) throws Exception {
        BankDetails result = null;
        try {
            result = bankDetailsService.getBankDetailsById(bdId,schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{bdId}/{schoolCode}")
    public ResponseEntity<Object> updateByEmpId(@RequestBody BankDetails bankDetails, @PathVariable int bdId, @PathVariable String schoolCode) {
        try {
            BankDetails result = bankDetailsService.updateByEmpId(bankDetails, bdId, schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Record not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/soft/delete")
    public ResponseEntity<Object> softDeleteBankDetails(@RequestParam("bdId") int bdId,@RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = bankDetailsService.softDeleteBankDetails(bdId,schoolCode);
        if(result){
            BankDetailsResponse response = new BankDetailsResponse(result, 200 ,DELETE_BANKDETAILS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            BankDetailsResponse response = new BankDetailsResponse(result, 400 ,DELETE_BANKDETAILS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search/text")
    public ResponseEntity<Object> getBankDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode) throws Exception {
        try{
            List<BankDetails> result = bankDetailsService.getBankDetailsBySearchText(searchText, schoolCode);
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
   /* @DeleteMapping("/delete/{bdId}")
    public ResponseEntity<Object> deleteBankDetailsById(@PathVariable int bdId) throws Exception {
        boolean result = bankDetailsService.deleteByEmpId(bdId);
        if(result){
            BankDetailsResponse response = new BankDetailsResponse(result, 200 ,DELETE_BANKDETAILS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            BankDetailsResponse response = new BankDetailsResponse(result, 400 ,DELETE_BANKDETAILS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
}
