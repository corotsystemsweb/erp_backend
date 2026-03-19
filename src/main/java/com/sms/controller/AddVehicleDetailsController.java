package com.sms.controller;

import com.sms.model.*;
import com.sms.service.AddVehicleDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class AddVehicleDetailsController {
    @Autowired
    private AddVehicleDetailsService addVehicleDetailsService;
    @PostMapping("/vehicle/add/{schoolCode}")
    public ResponseEntity<Object> addVehicle(@RequestBody AddVehicleDetails addVehicleDetails, @PathVariable String schoolCode) throws Exception{
        AddVehicleDetails result = null;
        try{
            result = addVehicleDetailsService.addVehicle(addVehicleDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/vehicle/{vehicleId}/{schoolCode}")
    public ResponseEntity<Object> getVehicleById(@PathVariable int vehicleId, @PathVariable String schoolCode) throws Exception {
        AddVehicleDetails result = null;

        try {
            result = addVehicleDetailsService.getVehicleById(vehicleId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/vehicle/{schoolCode}")
    public ResponseEntity<Object> getAllVehicle(@PathVariable String schoolCode) throws Exception {
        List<AddVehicleDetails> result = null;
        try {
            result = addVehicleDetailsService.getAllVehicle(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/vehicle/update/{vehicleId}/{schoolCode}")
    public ResponseEntity<Object> updateVehicle(@RequestBody AddVehicleDetails addVehicleDetails, @PathVariable int vehicleId, @PathVariable String schoolCode) throws Exception{
        AddVehicleDetails result = null;
        try{
            result = addVehicleDetailsService.updateVehicle(addVehicleDetails, vehicleId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/vehicle/delete/{vehicleId}/{schoolCode}")
    public ResponseEntity<Object> deleteClassDetailsById(@PathVariable int vehicleId, @PathVariable String schoolCode) throws Exception {
        boolean result = addVehicleDetailsService.deleteVehicle(vehicleId, schoolCode);
        if(result){
            AddVehicleResponse response = new AddVehicleResponse(result, 200 , DELETE_VEHICLE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            AddVehicleResponse response = new AddVehicleResponse(result, 400 , DELETE_VEHICLE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/vehicle/number/{schoolCode}")
    public ResponseEntity<Object> getAllVehicleNumber(@PathVariable String schoolCode) throws Exception{
        List<AddVehicleDetails> result = null;
        try{
            result = addVehicleDetailsService.getAllVehicleNumber(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/vehicle/search/text")
    public ResponseEntity<Object> getVehicleDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<AddVehicleDetails> result = addVehicleDetailsService.getVehicleDetailsBySearchText(searchText, schoolCode);
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
