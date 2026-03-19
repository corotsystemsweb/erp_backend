package com.sms.controller;

import com.sms.model.*;
import com.sms.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class UserRegistrationController {
    @Autowired
    private UserRegistrationService userRegistrationService;

    @PostMapping("/userRegistration")
    public ResponseEntity<Object> addUser(@RequestBody UserRegistrationDetails userRegistrationDetails){
        try{
            UserRegistrationDetails result = userRegistrationService.adduser(userRegistrationDetails);
            return ResponseEntity.ok("Registration successful ");
        }catch(InvalidDataAccessApiUsageException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Already has an account ");
        }catch (IllegalArgumentException e){
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password and Confirm Password do not match");
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception raise during user registration");
        }
    }
    @GetMapping("/get/all/userDetails/{schoolCode}")
    public ResponseEntity<Object> getAllUserDetails(@PathVariable String schoolCode) throws Exception {
        List<UserRegistrationDetails> result = null;

        try {
            result = userRegistrationService.getAllUserDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/userDetails/{userId}/{schoolCode}")
    public ResponseEntity<Object> getUserDetailsById(@PathVariable int userId, @PathVariable String schoolCode) throws Exception {
        UserRegistrationDetails result = null;

        try {
            result = userRegistrationService.getUserDetailsById(userId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/userDetails/{userId}/{schoolCode}")
    public ResponseEntity<Object> updateUserDetailsById(@RequestBody UserRegistrationDetails userRegistrationDetails, @PathVariable int userId, @PathVariable String schoolCode) throws Exception{
        String result = null;
        try{
            result = userRegistrationService.updateUserDetailsById(userRegistrationDetails, userId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/userDetails/{userId}/{schoolCode}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId, @PathVariable String schoolCode) throws Exception {
        boolean result = userRegistrationService.deleteUser(userId, schoolCode);
        if(result){
            UserDetailsResponse response = new UserDetailsResponse(result, 200 , DELETE_USER_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            UserDetailsResponse response = new UserDetailsResponse(result, 400 , DELETE_USER_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/allRole")
    public ResponseEntity<Object> getAllRole() throws Exception{
        List<UserRegistrationDetails> result = null;
        try{
            result = userRegistrationService.getAllRole();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
