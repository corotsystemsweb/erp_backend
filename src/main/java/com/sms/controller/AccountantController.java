package com.sms.controller;

import com.sms.model.AccountantDetails;
import com.sms.model.AccountantResponse;
import com.sms.service.AccountantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.ADD_ACCOUNTANT_FAILED;
import static com.sms.appenum.Message.ADD_ACCOUNTANT_SUCCESS;

@RestController
@RequestMapping("/api")
public class AccountantController {
    @Autowired
    private AccountantService accountantService;
    @PostMapping("/accountant")
    public ResponseEntity<Object> addAccountantDetails(@RequestBody AccountantDetails accountantDetails) throws Exception {
        boolean result = accountantService.addAccount(accountantDetails);
        if(result){
            AccountantResponse response = new AccountantResponse(result, 200, ADD_ACCOUNTANT_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            AccountantResponse response = new AccountantResponse(result, 401, ADD_ACCOUNTANT_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/accountant/{id}")
    public ResponseEntity<Object> getAccountantDetailsById(@PathVariable int id) throws Exception {
        AccountantDetails result = null;
        try{
            result = accountantService.getAccountantDetailsById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/accountant")
    public ResponseEntity<Object> getAllAccountantDetails(){
        List<AccountantDetails> result = null;
        try{
            result = accountantService.getAllAccountantDetails();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
