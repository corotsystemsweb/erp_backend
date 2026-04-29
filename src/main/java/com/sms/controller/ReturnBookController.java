package com.sms.controller;

import com.sms.model.ReturnBookDetails;
import com.sms.service.ReturnBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReturnBookController {

    @Autowired
    private ReturnBookService returnBookService;

    @PostMapping("/return/book/add/{schoolCode}")
    public ResponseEntity<Object> addReturnBook(@RequestBody ReturnBookDetails details,
                                                @PathVariable String schoolCode) {
        ReturnBookDetails result = null;
        try {
            result = returnBookService.addReturnBook(details, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/return/book/getAll/{schoolCode}")
    public ResponseEntity<Object> getReturnBookDetails(@PathVariable String schoolCode) throws Exception {
        List<ReturnBookDetails> result = null;
        try {
            result = returnBookService.getReturnBookDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/return/book/update/{returnBookId}/{schoolCode}")
    public ResponseEntity<Object> updateReturnBook(@RequestBody ReturnBookDetails details,
                                                   @PathVariable int returnBookId,
                                                   @PathVariable String schoolCode) {

        ReturnBookDetails result = null;
        try {
            result = returnBookService.updateReturnBook(details, returnBookId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}