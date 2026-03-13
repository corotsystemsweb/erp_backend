package com.sms.controller;

import com.sms.model.AdminDetails;
import com.sms.model.AdminResponse;
import com.sms.model.StudentResponse;
import com.sms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @PostMapping("/admin")
    public ResponseEntity<Object> addAdminDetails(@RequestBody AdminDetails adminDetails) throws Exception {
        boolean result = adminService.addAdmin(adminDetails);
        if (result) {
            AdminResponse response = new AdminResponse(result, 200, ADD_ADMIN_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            AdminResponse response = new AdminResponse(result, 401, ADD_ADMIN_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/admin/{id}")
    public ResponseEntity<Object> getAdminDetails(@PathVariable int id) throws Exception {
        AdminDetails result = null;
        try {
           result = adminService.getAdminDetailsById(id);
           return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/admin")
    public ResponseEntity<Object> getAllAdminDetails() throws Exception{
        List<AdminDetails> result = null;
        try{
            result = adminService.getAllAdminDetails();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
