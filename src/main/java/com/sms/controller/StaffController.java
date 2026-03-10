package com.sms.controller;

import com.sms.appenum.Message;
import com.sms.model.*;
import com.sms.service.StaffService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.sms.appenum.Message.*;
import static com.sms.appenum.Message.ADD_IMAGE_FAILED;

@RestController
@RequestMapping("/api")
public class StaffController{
    @Autowired
    private StaffService staffService;
    /*@PostMapping("staff/image/add/{staffId}")
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable int staffId) throws Exception {
        boolean result = staffService.addImage(file,staffId);
        if (result) {
            ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("staff/image/get/{staffId}")
    public ResponseEntity<StaffDetails> getImage(@PathVariable int staffId){
        try {
            StaffDetails imageData =staffService.getImage(staffId);
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
    @PostMapping("/staff/image/add/{schoolCode}/{staffId}")
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable String schoolCode, @PathVariable int staffId) throws Exception {
        boolean result = staffService.addImage(file, schoolCode, staffId);
        if (result) {
            ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/image/get/{schoolCode}/{staffId}")
    public ResponseEntity<Object> getImage(@PathVariable String schoolCode, @PathVariable int staffId) throws Exception{
        try{
            StaffDetails imageData = staffService.getImage(schoolCode, staffId);
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
    @PostMapping("/staff/add/{schoolCode}")
    public ResponseEntity<Object> addStaffDetailsOneByOne(@RequestBody StaffDetails staffDetails, @PathVariable String schoolCode)throws Exception
    {
       StaffDetails result=null;
       try{
           result=staffService.addStaffOneBYOne(staffDetails,schoolCode);
           //System.out.print(result);
           return new ResponseEntity<>(result,HttpStatus.OK);
       }catch(Exception e){
           return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
       }
    }

    @GetMapping("/staff/{staffId}/{schoolCode}")
    public ResponseEntity<Object> getStaffDetailsById(@PathVariable int staffId,@PathVariable String schoolCode) throws Exception{
        StaffDetails result=null;
        try{
            result=staffService.getStaffDetailsById(staffId,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/all/{schoolCode}")
    public ResponseEntity<Object> getAllstaffDetails(@RequestParam(required = false) String type, @PathVariable String schoolCode){
        List<StaffDetails> result = null;
        try{
            result = staffService.getAllStaffDetails(type, schoolCode);

            return  new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/staff/update/{staffId}/{schoolCode}")
    public ResponseEntity<Object>updateStaffDetailsById(@RequestBody StaffDetails staffDetails,@PathVariable int staffId,@PathVariable String schoolCode)
    {
        StaffDetails result=null;
        try{
            result=staffService.updateStaffById(staffDetails,staffId,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch(Exception e)
        {
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
@PatchMapping("/staff/soft/delete")
public ResponseEntity<Object> softDeleteStaff(@RequestParam("staffId") int staffId,@RequestParam("schoolCode") String schoolCode) throws Exception{
        boolean result=staffService.softDeleteStaff(staffId,schoolCode);
        if(result) {
            StaffResponse response=new StaffResponse(result,200,DELETE_STAFF_SUCCESS.val());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else {
            StaffResponse response=new StaffResponse(result,400,DELETE_STAFF_FAILED.val());
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/total/{schoolCode}")
    public ResponseEntity<Object> getTotalStaff(@PathVariable String schoolCode){
        int result = 0;
        try{
            result = staffService.getTotalStaff(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/teacher/{schoolCode}")
    public ResponseEntity<?> getAllTeacher(
            @PathVariable String schoolCode,
            @RequestParam(name = "staffType", required = false, defaultValue = "all") String staffType,
            @RequestParam(name = "filter", required = false, defaultValue = "all") String filter) {

        try {

            // Normalize staffType
            if (staffType == null || staffType.trim().isEmpty()) {
                staffType = "all";
            }

            // Normalize filter
            if (filter == null || filter.trim().isEmpty()) {
                filter = "all";
            }

            List<StaffDetails> result =
                    staffService.totalTeacher(
                            schoolCode,
                            filter.trim().toLowerCase(),
                            staffType.trim().toLowerCase()
                    );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching teacher list");
        }
    }


    @GetMapping("/staff/staffId")
    public ResponseEntity<Object> getStaffId(@RequestParam("staffName") String staffName, @RequestParam("schoolCode") String schoolCode) throws Exception{
        StaffDetails result=null;
        try{
            result=staffService.getStaffId(staffName,schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/designation/{designationId}/{schoolCode}")
    public ResponseEntity<Object> getAllStaffByDesignation(@PathVariable int designationId ,@PathVariable String schoolCode){
        List<StaffDetails> result = null;
        try{
            result = staffService.getStaffByDesignation(designationId,schoolCode);
            return  new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/salary/designation/{designationId}/{schoolCode}")
    public ResponseEntity<Object> getAllStaffSalaryByDesignation(@PathVariable int designationId ,@PathVariable String schoolCode){
        List<StaffDetails> result = null;
        try{
            result = staffService.getSalaryByDesignation(designationId,schoolCode);
            return  new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staff/search/text")
    public ResponseEntity<Object> getStaffDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<StaffDetails> result = staffService.getStaffDetailsBySearchText(searchText, schoolCode);
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
    @GetMapping("/allStaff/IdCardGeneration/{schoolCode}")
    public ResponseEntity<Object> getAllStaffForIdCardGeneration(@PathVariable String schoolCode) throws Exception{
        List<StaffDetails> result=null;
        try{
            result=staffService.getAllStaffForIdCardGeneration(schoolCode);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/staffIdCardById/{staffId}/{schoolCode}")
    public ResponseEntity<Object> getStaffByIdForIdCardGeneration(@PathVariable int staffId, @PathVariable String schoolCode){
        StaffDetails result = null;
        try{
            result = staffService.getStaffByIdForIdCardGeneration(staffId, schoolCode);

            return  new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
