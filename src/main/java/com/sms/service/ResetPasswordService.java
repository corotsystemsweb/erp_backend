package com.sms.service;

public interface ResetPasswordService {
    public String initiatePasswordReset(String email, String newPassword, String confirmPassword) throws Exception;
    public String verifyOtp(String email, String otp, String newPassword) throws Exception;
    public String resendOtp(String email) throws Exception;
}
