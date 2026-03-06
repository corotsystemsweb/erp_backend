package com.sms.controller;

import com.sms.model.*;
import com.sms.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class DiscountCodeController {
    @Autowired
    private DiscountCodeService discountCodeService;
    @PostMapping("/discount/add/{schoolCode}")
    public ResponseEntity<Object> addDiscount(@RequestBody DiscountCodeDetails discountCodeDetails, @PathVariable String schoolCode) throws Exception{
        DiscountCodeDetails result = null;
        try{
            result = discountCodeService.addDiscount(discountCodeDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            // Don't print stack trace for duplicate errors
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                // Just return the message, no console output
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            // For other errors, you might still want to print
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed: " + e.getMessage());
        }
    }
    @GetMapping("/discount/get/{dcId}/{schoolCode}")
    public ResponseEntity<Object> getDiscountById(@PathVariable int dcId, @PathVariable String schoolCode) throws Exception {
        DiscountCodeDetails result = null;
        try {
            result = discountCodeService.getDiscountById(dcId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/discount/get/{schoolCode}")
    public ResponseEntity<Object> getAllDiscount(@PathVariable String schoolCode) throws Exception {
        List<DiscountCodeDetails> result = null;

        try {
            result = discountCodeService.getAllDiscount(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/discount/update/{dcId}/{schoolCode}")
    public ResponseEntity<Object> updateDiscountById(@RequestBody DiscountCodeDetails discountCodeDetails, @PathVariable int dcId, @PathVariable String schoolCode) throws Exception{
        DiscountCodeDetails result = null;
        try{
            result = discountCodeService.updateDiscountById(discountCodeDetails, dcId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/discount/delete")
    public ResponseEntity<Object> deleteDiscount(@RequestParam("dcId") int dcId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = discountCodeService.deleteDiscount(dcId, schoolCode);
        if(result){
            DiscountCodeResponse response = new DiscountCodeResponse(result, 200 , DELETE_DISCOUNT_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StudentResponse response = new StudentResponse(result, 400 , DELETE_DISCOUNT_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
