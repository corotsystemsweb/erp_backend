package com.sms.controller;

import com.sms.model.HrPayroleDetails;
import com.sms.model.PaySalaryDetails;
import com.sms.service.PaySalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pay/salary")
public class PaySalaryController {
    @Autowired
    private PaySalaryService paySalaryService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addSalary(@RequestBody PaySalaryDetails paySalaryDetails, @PathVariable String schoolCode) throws Exception{
        PaySalaryDetails result = null;
        try{
            result = paySalaryService.addCalculatedSalary(paySalaryDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/calculated/{salaryAmount}/{leaveCount}")
    public ResponseEntity<Object> getCalculatedSalary(@PathVariable String salaryAmount, @PathVariable String leaveCount) throws Exception{
        String result = null;
        try{
            result = paySalaryService.payableSalary(salaryAmount,leaveCount);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

   @GetMapping("/get/calculated/byMonth")
   public ResponseEntity<Object> getCalculatedSalaryByMonth(@RequestParam("paySalaryMonth") String paySalaryMonth, @RequestParam("paySalaryYear") String paySalaryYear, @RequestParam("schoolCode") String schoolCode) throws Exception{
       try{
           List<PaySalaryDetails> result = paySalaryService.findSalaryDetailsByMonth(paySalaryMonth,paySalaryYear,schoolCode);
           if(result.isEmpty()){
               return new ResponseEntity<>("No records found! ", HttpStatus.NOT_FOUND);
           }
           return new ResponseEntity<>(result, HttpStatus.OK);
       } catch(EmptyResultDataAccessException e){
           return new ResponseEntity<>("No records found! ", HttpStatus.NOT_FOUND);
       }catch(Exception e){
           return new ResponseEntity<>("No records found! ",HttpStatus.UNAUTHORIZED);
       }
   }
    @GetMapping("/get/calculated/byStaff")
    public ResponseEntity<Object> getCalculatedSalaryByStaffId(@RequestParam("staffId") int staffId,@RequestParam("paySalaryMonth") String paySalaryMonth, @RequestParam("paySalaryYear") String paySalaryYear, @RequestParam("designationId") int designationId, @RequestParam("schoolCode") String schoolCode) throws Exception{

        try{
            PaySalaryDetails result = paySalaryService.findSalaryDetailsByStaffId(staffId,paySalaryMonth,paySalaryYear,designationId,schoolCode);
            if(result == null){
                return new ResponseEntity<>("No records found! ", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found! ",HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>("No records found! ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


