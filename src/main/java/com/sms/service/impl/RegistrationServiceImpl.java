package com.sms.service.impl;

import com.sms.dao.RegistrationDao;
import com.sms.model.RegistrationDetails;
import com.sms.util.EmailSender;
import com.sms.service.RegistrationService;
import com.sms.util.CipherUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private RegistrationDao registrationDao;
    @Autowired
    private EmailSender emailSender;
    //private static final long OTP_VALIDITY_DURATION = 90000; // 1.5 minutes
    private static final long OTP_VALIDITY_DURATION = 1800000; // 3 minutes

    private static class OtpData {
        String otp;
        long timestamp;

        OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, RegistrationDetails> tempUserStorage = new ConcurrentHashMap<>();

    @Override
    public RegistrationDetails registration(RegistrationDetails registrationDetails) throws Exception {
        //check if email is already exists
        int count = registrationDao.checkEmailExists(registrationDetails.getEmail());
        if(count > 0){
            throw new IllegalArgumentException("Email is already exists");
        }
        //Generate otp
        String otp = generateOtp();
        otpStorage.put(registrationDetails.getEmail(), new OtpData(otp, System.currentTimeMillis()));
        tempUserStorage.put(registrationDetails.getEmail(), registrationDetails);
        try{
            emailSender.sendOtpEmail(registrationDetails.getEmail(), otp);
        }catch(MessagingException e){
            throw new IllegalArgumentException("Failed to send OTP email");
        }
        return registrationDetails;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public String verifyOtp(String email, String otp) throws Exception {
        OtpData otpData = otpStorage.get(email);
        if (otpData == null || !otpData.otp.equals(otp) || System.currentTimeMillis() - otpData.timestamp > OTP_VALIDITY_DURATION) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        RegistrationDetails registrationDetails = tempUserStorage.get(email);
        if (registrationDetails == null) {
            throw new IllegalArgumentException("No registration details found for email");
        }
         //Encrypt phone number
        String encryptPhoneNumber = CipherUtils.encrypt(registrationDetails.getPhoneNumber());
        registrationDetails.setPhoneNumber(encryptPhoneNumber);
        // Encrypt password
        String encryptedPassword = CipherUtils.encrypt(registrationDetails.getPassword());
        registrationDetails.setPassword(encryptedPassword);

        // Complete registration
        registrationDao.registration(registrationDetails);

        // Clean up
        otpStorage.remove(email);
        tempUserStorage.remove(email);

        return "Registration successful";
    }

    @Override
    public String resendOtpForRegistration(String email) throws Exception {
        String otp = generateOtp();
        otpStorage.put(email, new OtpData(otp, System.currentTimeMillis()));
        try{
            emailSender.sendOtpEmail(email, otp);
        }catch(MessagingException e){
            throw new IllegalArgumentException("Failed to send OTP email");
        }
        return "A verification code send to email";
    }

    @Override
    public RegistrationDetails login(String email, String password) {
            return registrationDao.login(email, password);
    }
    @Override
    public RegistrationDetails getUserByEmail(String email){
        return registrationDao.getUserByEmail(email);
    }
}
