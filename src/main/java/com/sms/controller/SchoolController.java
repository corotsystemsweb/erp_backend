package com.sms.controller;

import com.sms.model.*;
import com.sms.service.SchoolService;
import com.sms.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;
    @PostMapping("/school/image/add/{schoolCode}/{schoolId}")
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable String schoolCode, @PathVariable int schoolId) throws Exception {
        boolean result = schoolService.addImage(file, schoolCode, schoolId);
        if (result) {
            ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/school/image/get/{schoolCode}/{schoolId}")
    public ResponseEntity<Object> getImage(@PathVariable String schoolCode, @PathVariable int schoolId) throws Exception{
        try{
            SchoolDetails imageData = schoolService.getImage(schoolCode, schoolId);
            return ResponseEntity.ok().body(imageData);
        }catch (IOException e){
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Image not found", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   @PostMapping("/school/add/{schoolCode}")
   public ResponseEntity<Object> addSchoolDetails(@RequestBody SchoolDetails schoolDetails, @PathVariable String schoolCode) throws Exception{
       SchoolDetails result = null;
       try{
           result = schoolService.addSchoolDetails(schoolDetails, schoolCode);
           return new ResponseEntity<>(result, HttpStatus.OK);
       } catch(Exception e){
           return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
       }
   }
    @GetMapping("/school/get")
    public ResponseEntity<Object> getSchoolDetailsById(@RequestParam("schoolId") int schoolId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        SchoolDetails result = null;
        try {
            result = schoolService.getSchoolDetailsById(schoolId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/school/{schoolCode}")
    public ResponseEntity<Object> getAllSchoolDetails(@PathVariable String schoolCode) throws Exception {
        List<SchoolDetails> result = null;

        try {
            result = schoolService.getAllSchoolDetails(schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/school/update/{schoolId}/{schoolCode}")
    public ResponseEntity<Object> updateSchoolDetailsById(@RequestBody SchoolDetails schoolDetails, @PathVariable int schoolId, @PathVariable String schoolCode) throws Exception{
        SchoolDetails result = null;
        try{
            result = schoolService.updateSchoolDetailsById(schoolDetails, schoolId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/school/delete/{schoolId}/{schoolCode}")
    public ResponseEntity<Object> deleteSchoolDetailsById(@PathVariable int schoolId, @PathVariable String schoolCode) throws Exception {
        boolean result = schoolService.deleteSchoolDetailsById(schoolId, schoolCode);
        if(result){
            SchoolResponse response = new SchoolResponse(result, 200 , DELETE_SCHOOL_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            SchoolResponse response = new SchoolResponse(result, 400 , DELETE_SCHOOL_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
