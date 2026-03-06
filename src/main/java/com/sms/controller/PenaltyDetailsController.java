package com.sms.controller;

import com.sms.model.AddBookDetails;
import com.sms.model.PenaltyDetails;
import com.sms.model.TransportAllocationDetails;
import com.sms.service.PenaltyDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PenaltyDetailsController {
    @Autowired
    private PenaltyDetailsService penaltyDetailsService;
    @PostMapping("/penalty/add/{schoolCode}")
    public ResponseEntity<Object> addPenaltyDetails(@RequestBody PenaltyDetails penaltyDetails, @PathVariable String schoolCode)throws Exception
    {
        PenaltyDetails result=null;
        try{
            result=penaltyDetailsService.addPenalty(penaltyDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/penalty/get/all/{schoolCode}")
    public ResponseEntity<Object>getAllPenaltyDetails(@PathVariable String schoolCode)throws Exception
    {
        List<PenaltyDetails> result=null;
        try{
            result=penaltyDetailsService.getAllPenaltyDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/penalty/get/{penaltyId}/{schoolCode}")
    public ResponseEntity<Object>getPenaltyDetails(@PathVariable int penaltyId,@PathVariable String schoolCode)throws Exception
    {
        PenaltyDetails result=null;
        try{
            result=penaltyDetailsService.getPenalty(penaltyId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @PutMapping("/penalty/update/{penaltyId}/{schoolCode}")
    public ResponseEntity<Object> updatePenaltyDetails(@RequestBody PenaltyDetails penaltyDetails, @PathVariable int penaltyId, @PathVariable String schoolCode) throws Exception{
        PenaltyDetails result = null;
        try{
            result = penaltyDetailsService.updatePenaltyDetails(penaltyDetails,penaltyId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
