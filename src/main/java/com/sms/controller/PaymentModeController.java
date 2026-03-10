package com.sms.controller;

import com.sms.model.ImageResponse;
import com.sms.model.PaymentDetails;
import com.sms.model.SessionDetails;
import com.sms.service.PaymentModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.sms.appenum.Message.ADD_IMAGE_FAILED;
import static com.sms.appenum.Message.ADD_IMAGE_SUCCESS;

@RestController
@RequestMapping("/api/paymentMode")
public class PaymentModeController {
    @Autowired
    private PaymentModeService paymentModeService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addPaymentMode(@RequestBody PaymentDetails paymentDetails, @PathVariable String schoolCode) throws Exception {
        PaymentDetails result = null;
        try {
            result = paymentModeService.addPaymentMode(paymentDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/{pmId}/{schoolCode}")
    public ResponseEntity<Object> getPaymentModeDetailsById(@PathVariable int pmId, @PathVariable String schoolCode) throws Exception {
        PaymentDetails result = null;

        try {
            result = paymentModeService.getPaymentModeById(pmId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            // Handle the exception when an error occurs during the retrieval process
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{schoolCode}")
    public ResponseEntity<Object> getAllSessionDetails(@PathVariable String schoolCode) throws Exception {
        List<PaymentDetails> result = null;

        try {
            result = paymentModeService.getAllPaymentMode(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            // Handle the exception when an error occurs during the retrieval process
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
