package com.sms.controller;

import com.sms.model.TransportFeeDepositDetails;
import com.sms.model.TransportFeeDepositRequest;
import com.sms.service.TransportFeeDepositDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport-fee-deposit-details")
public class TransportFeeDepositDetailsController {

    @Autowired
    private TransportFeeDepositDetailsService transportFeeDepositDetailsService;

    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<List<TransportFeeDepositDetails>> addTransportFeeDepositDetailsWithTotal(@RequestBody TransportFeeDepositRequest transportFeeDepositRequest, @PathVariable String schoolCode) {
        List<TransportFeeDepositDetails> result = null;
        try {
            result = transportFeeDepositDetailsService.addTransportDepositDetails(transportFeeDepositRequest, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
