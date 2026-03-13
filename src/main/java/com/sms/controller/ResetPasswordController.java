package com.sms.controller;

import com.sms.model.ResetPasswordDetails;
import com.sms.service.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResetPasswordController {
    @Autowired
    ResetPasswordService resetPasswordService;

    @PostMapping("/reset/password")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam("email") String email, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword) throws Exception {
        try{
            String response = resetPasswordService.initiatePasswordReset(email, newPassword, confirmPassword);
            return ResponseEntity.ok("A verification code has been sent to your email. Please verify.");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/password/verify/otp")
    public ResponseEntity<String> verifyOtpAndResetPassword(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp,
            @RequestParam("newPassword") String newPassword) {
        try {
            String response = resetPasswordService.verifyOtp(email, otp, newPassword);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/resend/otp")
    public ResponseEntity<String> resendOtp(@RequestParam("email") String email) throws Exception {
        try{
            String response = resetPasswordService.resendOtp(email);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
