package com.sms.service.impl;

import com.sms.dao.ResetPasswordDao;
import com.sms.service.ResetPasswordService;
import com.sms.util.CipherUtils;
import com.sms.util.EmailSender;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {
    @Autowired
    private ResetPasswordDao resetPasswordDao;
    @Autowired
    private EmailSender emailSender;
   // private static final long OTP_VALIDITY_DURATION = 90000; //1.5 minutes
   private static final long OTP_VALIDITY_DURATION = 1800000; // 3 minutes
    private static class OptData{
        String otp;
        long timeStamp;

        public OptData(String otp, long timeStamp) {
            this.otp = otp;
            this.timeStamp = timeStamp;
        }
    }
    private final Map<String, OptData> otpStorage = new ConcurrentHashMap<>();
    /*@Override
    public int checkEmailExist(String email) throws Exception {
        return resetPasswordDao.checkEmailExist(email);
    }*/

    @Override
    public String initiatePasswordReset(String email, String newPassword, String confirmPassword) throws Exception {
        // Check if the email exists
        int count = resetPasswordDao.checkEmailExist(email);

        if(count == 0) {
            throw new IllegalArgumentException("Email not exist");
        }
        //Check newPassword and currentPassword is matched ot not
        if(!newPassword.equals(confirmPassword)){
            throw new IllegalArgumentException("Password is not matched");
        }

        String otp = generateOtp();
        otpStorage.put(email, new OptData(otp, System.currentTimeMillis()));

        try{
            emailSender.sendOtpEmail(email, otp);
        }catch(MessagingException e){
            throw new IllegalArgumentException("Failed to send otp");
        }
        return "A verification code sent to email.";
    }

    @Override
    public String verifyOtp(String email, String otp, String newPassword) throws Exception {

        OptData otpData = otpStorage.get(email);
        if (otpData == null || !otpData.otp.equals(otp) || System.currentTimeMillis() - otpData.timeStamp > OTP_VALIDITY_DURATION) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        String encriptedPassword = CipherUtils.encrypt(newPassword);
        resetPasswordDao.updatePassword(email, encriptedPassword);

        // Clean up
        otpStorage.remove(email);

        return "Password reset successful";
    }

    private String generateOtp(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public String resendOtp(String email) throws Exception {
        //check is the email is available or not
        int count = resetPasswordDao.checkEmailExist(email);
        if(count == 0){
            throw new IllegalArgumentException("Email does not exist");
        }
        String otp = generateOtp();
        otpStorage.put(email, new OptData(otp, System.currentTimeMillis()));
        //send the new otp
        try{
            emailSender.sendOtpEmail(email, otp);
        }catch(MessagingException e){
            throw new IllegalArgumentException("Failed to send otp");
        }
        return "A new verification code has been sent to your email";
    }
}
