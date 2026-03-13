package com.sms.controller;

import com.sms.model.FuelExpenseDetails;
import com.sms.model.FuelResponse;
import com.sms.model.HrPayroleDetails;
import com.sms.service.FuelExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/fuel/expense")
public class FuelExpenseController {
    @Autowired
    private FuelExpenseService fuelExpenseService;

    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addFuelExpense(@RequestBody FuelExpenseDetails fuelExpenseDetails, @PathVariable String schoolCode) throws Exception{
        FuelExpenseDetails result = null;
        try{
            result = fuelExpenseService.addFuelExpense(fuelExpenseDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/{fuelExpenseId}/{schoolCode}")
    public ResponseEntity<Object> getFuelExpenseById(@PathVariable int fuelExpenseId,@PathVariable String schoolCode) throws Exception {
        FuelExpenseDetails result = null;

        try {
            result = fuelExpenseService.getFuelExpenseById(fuelExpenseId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllFuelExpense(@PathVariable String schoolCode) throws Exception {
        List<FuelExpenseDetails> result = null;
        try {
            result = fuelExpenseService.getAllFuelExpense(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{fuelExpenseId}/{schoolCode}")
    public ResponseEntity<Object> updateFuelExpense(@RequestBody FuelExpenseDetails fuelExpenseDetails, @PathVariable int fuelExpenseId, @PathVariable String schoolCode) throws Exception{
        FuelExpenseDetails result = null;
        try{
            result = fuelExpenseService.updateFuelExpense(fuelExpenseDetails, fuelExpenseId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/delete/{fuelExpenseId}/{schoolCode}")
    public ResponseEntity<Object> deleteFuelExpense(@PathVariable int fuelExpenseId,@PathVariable String schoolCode) throws Exception {
        boolean result = fuelExpenseService.deleteFuelExpense(fuelExpenseId, schoolCode);
        if(result){
            FuelResponse response = new FuelResponse(result, 200 , DELETE_FUEL_EXPENSE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            FuelResponse response = new FuelResponse(result, 400 , DELETE_FUEL_EXPENSE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/fuel/report")
    public ResponseEntity<Object> getFuelExpenseReport(@RequestParam("reportType") String reportType, @RequestParam("schoolCode") String schoolCode) {
        try {
            List<FuelExpenseDetails> result = fuelExpenseService.getFuelExpenseReport(reportType, schoolCode); // Change report type as needed
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/search/text")
    public ResponseEntity<Object> getFuelExpenseReportBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<FuelExpenseDetails> result = fuelExpenseService.getFuelExpenseReportBySearchText(searchText, schoolCode);
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
