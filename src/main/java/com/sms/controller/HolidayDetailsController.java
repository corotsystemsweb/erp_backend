package com.sms.controller;

import com.sms.model.*;
import com.sms.service.HolidayDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class HolidayDetailsController {
    @Autowired
    private HolidayDetailsService holidayDetailsService;
    @PostMapping("/holiday/add/{schoolCode}")
    public ResponseEntity<Object> addHolidayDetails(@RequestBody HolidayDetails holidayDetails, @PathVariable String schoolCode) throws Exception{
        HolidayDetails result = null;
        try{
            result =holidayDetailsService.addHolidayDetails(holidayDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    //@PreAuthorize("hasRole('Student')")
    @GetMapping("/holiday/get/school/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllHolidayDetails(@PathVariable int sessionId,@PathVariable String schoolCode) throws Exception {
        List<HolidayDetails> result = null;

        try {
            result = holidayDetailsService.getAllHolidayDetails(sessionId,schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception when an error occurs during the retrieval process
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/holiday/get/today/school/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getPastOrTodayHoliday(@PathVariable int sessionId,@PathVariable String schoolCode) throws Exception {
        List<HolidayDetails> result = null;

        try {
            result = holidayDetailsService.getPastOrTodayHolidays(sessionId,schoolCode);// List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception when an error occurs during the retrieval process
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@PreAuthorize("hasRole('Teacher')")
    @GetMapping("/holiday/get/{id}/{schoolCode}")
    public ResponseEntity<Object> getHolidayDetailsById(@PathVariable int id,@PathVariable String schoolCode) throws Exception {
        HolidayDetails result = null;
        try {
            result = holidayDetailsService.getHolidayDetailsById(id,schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                // Handle the case when no holiday is found for the given ID
                return new ResponseEntity<>("Holiday not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/holiday/update/{id}/{schoolCode}")
    public ResponseEntity<Object> updateHolidayDetailsById(@RequestBody HolidayDetails holidayDetails, @PathVariable int id, @PathVariable String schoolCode) throws Exception{
        HolidayDetails result = null;
        try{
            result = holidayDetailsService.updateHolidayDetailsById(holidayDetails,id, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/holiday/delete/{id}/{schoolCode}")
    public ResponseEntity<Object> deleteHolidayDetailsById(@PathVariable int id, @PathVariable String schoolCode) throws Exception {
        boolean result = holidayDetailsService.deleteHolidayDetails(id, schoolCode);
        if(result){
            HolidayResponse response = new HolidayResponse(result, 200 , DELETE_HOLIDAY_DETAILS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            HolidayResponse response = new HolidayResponse(result, 400 , DELETE_HOLIDAY_DETAILS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
