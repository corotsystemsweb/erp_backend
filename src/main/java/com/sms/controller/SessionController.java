package com.sms.controller;

import com.sms.appenum.Message;
import com.sms.model.*;
import com.sms.service.SessionService;
import com.sms.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class SessionController {
    @Autowired
    private SessionService sessionService;
    @PostMapping("/session/add/{schoolCode}")
    public ResponseEntity<Object> addSessionDetails(@RequestBody SessionDetails sessionDetails, @PathVariable String schoolCode) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SessionDetails result = null;
        try {
            result = sessionService.addSession(sessionDetails, schoolCode);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Message.SESSION_ADDITION_FAILED.val());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Message.SESSION_ADDITION_FAILED.val());
        }
    }
    @GetMapping("/session/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getSessionDetailsById(@PathVariable int sessionId, @PathVariable String schoolCode) throws Exception {
        SessionDetails result = null;

        try {
            result = sessionService.getSessionById(sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            // Handle the exception when an error occurs during the retrieval process
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/session/{schoolCode}")
    public ResponseEntity<Object> getAllSessionDetails(@PathVariable String schoolCode) throws Exception {
        List<SessionDetails> result = null;

        try {
            result = sessionService.getAllSessionDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            // Handle the exception when an error occurs during the retrieval process
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/session/update/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> updateSessionDetailsById(@RequestBody SessionDetails sessionDetails, @PathVariable int sessionId, @PathVariable String schoolCode) throws Exception{
        SessionDetails result = null;
        try{
            result = sessionService.updateSessionDetailsById(sessionDetails, sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/session/delete/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> deleteStudentDetailsById(@PathVariable int sessionId, @PathVariable String schoolCode) throws Exception {
        boolean result = sessionService.deleteSessionDetailsById(sessionId, schoolCode);
        if(result){
            SessionResponse response = new SessionResponse(result, 200 , DELETE_SESSION_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StudentResponse response = new StudentResponse(result, 400 , DELETE_SESSION_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
