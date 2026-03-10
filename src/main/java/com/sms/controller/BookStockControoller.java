package com.sms.controller;

import com.sms.model.AddBookDetails;
import com.sms.model.BookStockDetails;
import com.sms.service.BookStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookStockControoller {
    @Autowired
    private BookStockService bookStockService;
    @PostMapping("/book/stock/add/{schoolCode}")
    public ResponseEntity<Object> addBookStock(@RequestBody BookStockDetails bookStockDetails, @PathVariable String schoolCode)throws Exception
    {
     BookStockDetails result=null;
        try{
            result=bookStockService.addBookStock(bookStockDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/book/stock/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllBookStock(@PathVariable String schoolCode) throws Exception {
        List<BookStockDetails> result=null;
        try {
            result =bookStockService.getAllBookStock(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/book/stock/search/text")
    public ResponseEntity<Object> getAllBookStockBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<BookStockDetails> result= bookStockService.getAllBookStockBySearchText(searchText,schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.UNAUTHORIZED);
        }
    }

}
