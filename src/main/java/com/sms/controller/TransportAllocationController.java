package com.sms.controller;

import com.sms.model.AddRouteDetails;
import com.sms.model.AddRouteResponse;
import com.sms.model.TransportAllocationDetails;
import com.sms.model.TransportAllocationResponse;
import com.sms.service.TransportAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class TransportAllocationController {
    @Autowired
    private TransportAllocationService transportAllocationService;
    @PostMapping("/transport/add/{schoolCode}")
    public ResponseEntity<Object> addTransportAllocationDetails(@RequestBody TransportAllocationDetails transportAllocationDetails, @PathVariable String schoolCode){
        TransportAllocationDetails result = null;
        try{
            result = transportAllocationService.addTransportAllocation(transportAllocationDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/transport/get/{taId}/{schoolCode}")
    public ResponseEntity<Object> getTransportAllocationById(@PathVariable int taId, @PathVariable String schoolCode) throws Exception {
        TransportAllocationDetails result = null;

        try {
            result = transportAllocationService.getTransportAllocationById(taId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/transport/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllTransportAllocation(@PathVariable String schoolCode) throws Exception {
        List<TransportAllocationDetails> result = null;

        try {
            result = transportAllocationService.getAllTransportAllocation(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/transport/update/{taId}/{schoolCode}")
    public ResponseEntity<Object> updateTransportAllocation(@RequestBody TransportAllocationDetails transportAllocationDetails, @PathVariable int taId, @PathVariable String schoolCode) throws Exception{
        TransportAllocationDetails result = null;
        try{
            result = transportAllocationService.updateTransportAllocation(transportAllocationDetails, taId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/transport/delete/{taId}/{schoolCode}")
    public ResponseEntity<Object> deleteRouteDetails(@PathVariable int taId, @PathVariable String schoolCode) throws Exception {
        boolean result = transportAllocationService.deleteTransportAllocation(taId, schoolCode);
        if(result){
            TransportAllocationResponse response = new TransportAllocationResponse(result, 200 , DELETE_TRANSPORT_ALLOCATION_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            TransportAllocationResponse response = new TransportAllocationResponse(result, 400 , DELETE_TRANSPORT_ALLOCATION_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/transportAllocation/search/text")
    public ResponseEntity<Object> getTransportAllocationDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<TransportAllocationDetails> result = transportAllocationService.getTransportAllocationDetailsBySearchText(searchText, schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.UNAUTHORIZED);
        }
    }
}
