package com.sms.controller;

import com.sms.model.ExpenseType;
import com.sms.model.ExpenseTypeResponse;
import com.sms.model.FuelExpenseDetails;
import com.sms.service.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/expense/type")
public class ExpenseTypeController {
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addExpenseType(@RequestBody ExpenseType expenseType, @PathVariable String schoolCode) throws Exception{
        ExpenseType result = null;
        try{
            result = expenseTypeService.addExpenseType(expenseType, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/{expenseTypeId}/{schoolCode}")
    public ResponseEntity<Object> getExpenseTypeById(@PathVariable int expenseTypeId, @PathVariable String schoolCode) throws Exception {
        ExpenseType result = null;

        try {
            result = expenseTypeService.getExpenseTypeById(expenseTypeId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllExpenseType(@PathVariable String schoolCode) throws Exception {
        List<ExpenseType> result = null;
        try {
            result = expenseTypeService.getAllExpenseType(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{expenseTypeId}/{schoolCode}")
    public ResponseEntity<Object> updateExpenseType(@RequestBody ExpenseType expenseType, @PathVariable int expenseTypeId, @PathVariable String schoolCode) throws Exception{
        ExpenseType result = null;
        try{
            result = expenseTypeService.updateExpenseType(expenseType, expenseTypeId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/delete/{expenseTypeId}/{schoolCode}")
    public ResponseEntity<Object> softDeleteExpenseType(@PathVariable int expenseTypeId, @PathVariable String schoolCode) throws Exception {
        boolean result = expenseTypeService.softDeleteExpenseType(expenseTypeId, schoolCode);
        if(result){
            ExpenseTypeResponse response = new ExpenseTypeResponse(result, 200 , DELETE_EXPENSE_TYPE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            ExpenseTypeResponse response = new ExpenseTypeResponse(result, 400 , DELETE_EXPENSE_TYPE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/search/text")
    public ResponseEntity<Object> getExpenseTypeBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<ExpenseType> result = expenseTypeService.getExpenseTypeBySearchText(searchText, schoolCode);
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
