package com.sms.service;

import com.sms.model.RegistrationDetails;

public interface RegistrationService {
    public RegistrationDetails registration(RegistrationDetails registrationDetails) throws Exception;
    public RegistrationDetails login(String email, String password);
    public String verifyOtp(String email, String otp) throws Exception;
    public String resendOtpForRegistration(String email) throws Exception;
    public RegistrationDetails getUserByEmail(String email);
}
