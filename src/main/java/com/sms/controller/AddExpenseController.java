package com.sms.controller;

import com.sms.model.AddExpenseDetails;
import com.sms.model.AddExpenseResponse;
import com.sms.model.ExpenseType;
import com.sms.model.FuelExpenseDetails;
import com.sms.service.AddExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/add/expense")
public class AddExpenseController {
    @Autowired
    private AddExpenseService addExpenseService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addExpenseDetails(@RequestBody AddExpenseDetails addExpenseDetails, @PathVariable String schoolCode) throws Exception{
        AddExpenseDetails result = null;
        try{
            result = addExpenseService.addExpenseDetails(addExpenseDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/{addExpenseId}/{schoolCode}")
    public ResponseEntity<Object> getAddExpenseDetailsById(@PathVariable int addExpenseId, @PathVariable String schoolCode) throws Exception {
        AddExpenseDetails result = null;

        try {
            result = addExpenseService.getAddExpenseDetailsById(addExpenseId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllAddExpenseDetails(@PathVariable String schoolCode) throws Exception {
        List<AddExpenseDetails> result = null;
        try {
            result = addExpenseService.getAllAddExpenseDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{addExpenseId}/{schoolCode}")
    public ResponseEntity<Object> updateAddExpenseDetails(@RequestBody AddExpenseDetails addExpenseDetails, @PathVariable int addExpenseId, @PathVariable String schoolCode) throws Exception{
        AddExpenseDetails result = null;
        try{
            result = addExpenseService.updateAddExpenseDetails(addExpenseDetails, addExpenseId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/delete/{addExpenseId}/{schoolCode}")
    public ResponseEntity<Object> softDeleteAddExpenseDetails(@PathVariable int addExpenseId, @PathVariable String schoolCode) throws Exception {
        boolean result = addExpenseService.softDeleteAddExpenseDetails(addExpenseId, schoolCode);
        if(result){
            AddExpenseResponse response = new AddExpenseResponse(result, 200 , DELETE_ADD_EXPENSE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            AddExpenseResponse response = new AddExpenseResponse(result, 400 , DELETE_ADD_EXPENSE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/expense/report")
    public ResponseEntity<Object> getExpenseReport(@RequestParam("expenseTitle") String expenseTitle, @RequestParam("reportType") String reportType, @RequestParam("schoolCode") String schoolCode) {
        try {
            List<AddExpenseDetails> result = addExpenseService.getExpenseReport(expenseTitle, reportType, schoolCode); // Change report type as needed
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/search/text")
    public ResponseEntity<Object> getExpenseDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<AddExpenseDetails> result = addExpenseService.getExpenseDetailsBySearchText(searchText, schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
