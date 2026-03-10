package com.sms.controller;

import com.sms.model.*;
import com.sms.service.DriverDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class DriverDetailsController {
    @Autowired
    private DriverDetailsService driverDetailsService;
    /*@PostMapping("driver/image/add/{driverId}")
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable int driverId) throws Exception {
        boolean result = driverDetailsService.addImage(file,driverId);
        if (result) {
            ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("driver/image/get/{driverId}")
    public ResponseEntity<DriverDetails> getImage(@PathVariable int driverId){
        try {
            DriverDetails imageData =driverDetailsService.getImage(driverId);
            return ResponseEntity.ok()
                    .body(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
    @PostMapping("/driver/image/add/{schoolCode}/{driverId}")
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable String schoolCode, @PathVariable int driverId) throws Exception {
        boolean result = driverDetailsService.addImage(file, schoolCode, driverId);
        if (result) {
            ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/driver/image/get/{schoolCode}/{driverId}")
    public ResponseEntity<Object> getImage(@PathVariable String schoolCode, @PathVariable int driverId){
        try {
            DriverDetails imageData =driverDetailsService.getImage(schoolCode, driverId);
            return ResponseEntity.ok().body(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Image not found", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Internal server error: ", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/driver/add/{schoolCode}")
    public ResponseEntity<Object> addDriver(@RequestBody DriverDetails driverDetails, @PathVariable String schoolCode) throws Exception{
        DriverDetails result = null;
        try{
            result = driverDetailsService.addDriver(driverDetails, schoolCode);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/driver/{driverId}/{schoolCode}")
    public ResponseEntity<Object> getDriverById(@PathVariable int driverId, @PathVariable String schoolCode) throws Exception {
        DriverDetails result = null;

        try {
            result = driverDetailsService.getDriverById(driverId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/driver/{schoolCode}")
    public ResponseEntity<Object> getAllDriver(@PathVariable String schoolCode) throws Exception {
        List<DriverDetails> result = null;
        try {
            result = driverDetailsService.getAllDriver(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/driver/update/{driverId}/{schoolCode}")
    public ResponseEntity<Object> updateDriverDetails(@RequestBody DriverDetails driverDetails, @PathVariable int driverId, @PathVariable String schoolCode) throws Exception{
        DriverDetails result = null;
        try{
            result = driverDetailsService.updateDriverDetails(driverDetails, driverId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/driver/delete/{driverId}/{schoolCode}")
    public ResponseEntity<Object> deleteDriver(@PathVariable int driverId, @PathVariable String schoolCode) throws Exception {
        boolean result = driverDetailsService.deleteDriver(driverId, schoolCode);
        if(result){
            DriverResponse response = new DriverResponse(result, 200 , DELETE_DRIVER_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            DriverResponse response = new DriverResponse(result, 400 , DELETE_DRIVER_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/driver/search/text")
    public ResponseEntity<Object> getDriverDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<DriverDetails> result = driverDetailsService.getDriverDetailsBySearchText(searchText, schoolCode);
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
