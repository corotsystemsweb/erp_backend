package com.sms.controller;

import com.sms.model.FrequencyDetails;
import com.sms.model.FrequencyResponse;
import com.sms.model.SchoolDetails;
import com.sms.model.StudentResponse;
import com.sms.service.FrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class FrequencyController {
    @Autowired
    private FrequencyService frequencyService;
    @PostMapping("/frequency/add/{schoolCode}")
    public ResponseEntity<Object> addFrequency(@RequestBody FrequencyDetails frequencyDetails, @PathVariable String schoolCode) throws Exception{
        FrequencyDetails result = null;
        try{
            result = frequencyService.addFrequency(frequencyDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/frequency/{frequencyId}/{schoolCode}")
    public ResponseEntity<Object> getFrequencyDetailsById(@PathVariable int frequencyId, @PathVariable String schoolCode) throws Exception {
        FrequencyDetails result = null;
        try {
            result = frequencyService.getFrequencyDetailsById(frequencyId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/frequency/{schoolCode}")
    public ResponseEntity<Object> getAllFrequencyDetails(@PathVariable String schoolCode) throws Exception {
        List<FrequencyDetails> result = null;

        try {
            result = frequencyService.getAllFrequencyDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/frequency/update/{frequencyId}/{schoolCode}")
    public ResponseEntity<Object> updateFrequencyDetailsById(@RequestBody FrequencyDetails frequencyDetails, @PathVariable int frequencyId, @PathVariable String schoolCode) throws Exception{
        FrequencyDetails result = null;
        try{
            result = frequencyService.updateFrequencyDetailsById(frequencyDetails, frequencyId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/frequency/soft/delete")
    public ResponseEntity<Object> deleteFrequency(@RequestParam("frequencyId") int frequencyId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = frequencyService.deleteFrequency(frequencyId, schoolCode);
        if (result) {
            // If deletion is successful
            FrequencyResponse response = new FrequencyResponse(result, 200, DELETE_FREQUENCY_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // If deletion fails
            FrequencyResponse response = new FrequencyResponse(result, 400, DELETE_FREQUENCY_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
