package com.sms.controller;

import com.sms.model.*;
import com.sms.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class BookCategoryController {
    @Autowired
    private BookCategoryService bookCategoryService;
    @PostMapping("/book/category/add/{schoolCode}")
    public ResponseEntity<Object> addBookCategoryDetails(@RequestBody BookCategroyDetails bookCategroyDetails, @PathVariable String schoolCode)throws Exception
    {
        BookCategroyDetails result=null;
        try{
            result=bookCategoryService.addBookCategory(bookCategroyDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/book/category/get/{bookCategoryId}/{schoolCode}")
    public ResponseEntity<Object> getBookCategoryDetailsById(@PathVariable int bookCategoryId, @PathVariable String schoolCode) throws Exception {
        BookCategroyDetails result=null;
        try {
            result =bookCategoryService.getBookCategoryById(bookCategoryId,schoolCode);
            System.out.println("result"+result);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/book/category/get/all/{schoolCode}")
    public ResponseEntity<Object> getBookCategoryDetails(@PathVariable String schoolCode) throws Exception {
        List<BookCategroyDetails> result=null;
        try {
            result =bookCategoryService.getBookCategory(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/book/category/{bookCategoryId}/{schoolCode}")
    public ResponseEntity<Object> updateBookCategoryDetails(@RequestBody BookCategroyDetails bookCategroyDetails, @PathVariable int bookCategoryId, @PathVariable String schoolCode) throws Exception {
        BookCategroyDetails result = null;
        try {
            result = bookCategoryService.updateById(bookCategroyDetails,bookCategoryId,schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Another book category with the same category already exists", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/book/category/delete/{bookCategoryId}/{schoolCode}")
    public ResponseEntity<Object> deleteBookCategoryById(@PathVariable int bookCategoryId, @PathVariable String schoolCode) throws Exception {
        boolean result = bookCategoryService.deleteBookCategory(bookCategoryId,schoolCode);
        if(result){
            BookCategoryResponse response = new BookCategoryResponse(result, 200 , DELETE_BOOK_CATEGORY_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            BookCategoryResponse response = new BookCategoryResponse(result, 400 ,  DELETE_BOOK_CATEGORY_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/book/category/get/all/search")
    public ResponseEntity<Object> getIssueBookDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<BookCategroyDetails> result= bookCategoryService.getBookCategoryBySearchText(searchText,schoolCode);
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
