package com.sms.controller;

import com.sms.model.LibraryMemberDetails;
import com.sms.service.LibraryMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library/member")
public class LibraryMemberController {

    @Autowired
    private LibraryMemberService service;

    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addMember(
            @RequestBody LibraryMemberDetails details,
            @PathVariable String schoolCode) {

        try {
            return new ResponseEntity<>(service.addMember(details, schoolCode), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll/{schoolCode}")
    public ResponseEntity<Object> getAll(@PathVariable String schoolCode) {

        try {
            return new ResponseEntity<>(service.getAllMembers(schoolCode), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}