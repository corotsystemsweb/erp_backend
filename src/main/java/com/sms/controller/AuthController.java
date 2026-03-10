package com.sms.controller;

import com.sms.model.RefreshToken;
import com.sms.model.RegistrationDetails;
import com.sms.service.RefreshTokenService;
import com.sms.service.RegistrationService;
import com.sms.util.JwtToken;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private JwtToken jwtToken;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @PostMapping("/registration")
    public ResponseEntity<Object> register(@RequestBody RegistrationDetails registrationDetails) {
        try {
            RegistrationDetails registeredDetails = registrationService.registration(registrationDetails);
            return ResponseEntity.ok("A verification code has been sent to your email. Please verify.");
        } catch (InvalidDataAccessApiUsageException e) {
            //return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Already has an account");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<Object> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            String result = registrationService.verifyOtp(email, otp);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
           // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during OTP verification.");
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/resendOtp/registration")
   public ResponseEntity<String> resendOtpForRegistration(@RequestParam("email") String email){
        try{
            String result = registrationService.resendOtpForRegistration(email);
            return ResponseEntity.ok(result);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
   }
   /* Below code is for normal login */
   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody RegistrationDetails registrationDetails) {
       try {
           RegistrationDetails result = registrationService.login(registrationDetails.getEmail(), registrationDetails.getPassword());
           if (result != null) {
               // If login successful, generate a token
               String rolesString = String.join(",", result.getRole());
               // After successful login, create the token
               String token = jwtToken.getJwtToken(result.getEmail(), result.getSchoolCode(), rolesString);
               System.out.println("Generated Token: " + token);

               // Set the token to the result object
               result.setToken(token);

               return ResponseEntity.ok(result);
           } else {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
           }
       } catch (IllegalArgumentException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
       } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
   }
    //////////////////////////////////////////////////////////////////////////////////
    /* Below code is for Refresh token related login */
   /*@PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody RegistrationDetails registrationDetails) {
       try {
           RegistrationDetails user = registrationService.login(registrationDetails.getEmail(), registrationDetails.getPassword());
           if (user != null) {
               // Generate access token using existing JwtToken class
               String accessToken = jwtToken.getJwtToken(user.getEmail(), user.getSchoolCode(), user.getRole());

               // Generate a refresh token
               RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
               //user.setAccessToken(accessToken);
               user.setToken(accessToken);
               //user.setRefreshToken(refreshToken);
               user.setRefreshToken(refreshToken.getRefreshToken());

               return ResponseEntity.ok(user);
           } else {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
           }
       } catch (IllegalArgumentException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
       } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
   }*/

   @PostMapping("/refresh-token")
   public ResponseEntity<?> refreshToken(@RequestBody RefreshToken request) {
       String refreshToken = request.getRefreshToken(); // Get the refresh token

       try {
           // Verify the refresh token
           RefreshToken refreshTokenObj = refreshTokenService.verifyRefreshToken(refreshToken);

           // Fetch the user details based on the refresh token
           RegistrationDetails user = registrationService.getUserByEmail(refreshTokenObj.getUserName());

           // If login successful, generate a token
           String rolesString = String.join(",", user.getRole());

           // Generate a new access token
           String newAccessToken = jwtToken.getJwtToken(user.getEmail(), user.getSchoolCode(), rolesString);

           // Return the new access token and user details in the response
           return ResponseEntity.ok(Map.of(
                   "accessToken", newAccessToken,
                   "userDetails", user
           ));

       } catch (RuntimeException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token: " + e.getMessage());
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to refresh token. Please try again.");
       }
   }




    //////////////////////////////////////////////////////////////////////////////////
   /* @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
        }

        String token = authHeader.substring(7).trim();

        try {
            // Validate the token
            jwtToken.isValidJWT(token);

            // Generate a refreshed token
            String refreshedToken = jwtToken.refreshJwtToken(token);
            Map<String, String> response = new HashMap<>();
            response.put("refreshToken", refreshedToken);
            return ResponseEntity.ok(response);
        } catch (ExpiredJwtException e) {
            // Handle the expired token scenario
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session is expired");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            // Handle other token-related exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token refresh failed");
        }
    }*/
}
