package com.sms.controller;

import com.sms.model.*;
import com.sms.service.AddBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class AddBookController {
    @Autowired
    private AddBookService addBookService;
    @PostMapping("/new/book/add/{schoolCode}")
    public ResponseEntity<Object> addNewBookDetails(@RequestBody AddBookDetails addBookDetails, @PathVariable String schoolCode)throws Exception
    {
        AddBookDetails result=null;
        try{
            result=addBookService.addNewBookDetails(addBookDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/new/book/get/{bookId}/{schoolCode}")
    public ResponseEntity<Object> getBookCategoryDetailsById(@PathVariable int bookId, @PathVariable String schoolCode) throws Exception {
        AddBookDetails result=null;
        try {
            result =addBookService.getNewBookById(bookId,schoolCode);
            System.out.println("result"+result);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/new/book/get/all/{schoolCode}")
    public ResponseEntity<Object> getBookDetails(@PathVariable String schoolCode) throws Exception {
        List<AddBookDetails> result=null;
        try {
            result =addBookService.getAllBook(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/new/book/update/{bookId}/{schoolCode}")
    public ResponseEntity<Object> updateBookCategoryDetails(@RequestBody AddBookDetails addBookDetails, @PathVariable int bookId, @PathVariable String schoolCode) throws Exception {
        AddBookDetails result = null;
        try {
            result = addBookService.updateById(addBookDetails,bookId,schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Another book  with the same details already exists", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/******/
    @PatchMapping("/new/book/soft/delete")
    public ResponseEntity<Object> deleteStudent(@RequestParam("bookId") int bookId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = addBookService.softDeleteBook(bookId,schoolCode);
        if (result) {
            AddBookResponse response = new AddBookResponse(result, 200, DELETE_ADD_BOOK_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            AddBookResponse response = new AddBookResponse(result, 400, DELETE_ADD_BOOK_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/new/book/search/text")
    public ResponseEntity<Object> getBookDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<AddBookDetails> result= addBookService.getAllBookBySearchText(searchText,schoolCode);
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
