package com.sms.controller;

import com.sms.model.BookCategoryResponse;
import com.sms.model.BookCategroyDetails;
import com.sms.model.InventoryCategoryDetails;
import com.sms.service.InventoryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.DELETE_BOOK_CATEGORY_FAILED;
import static com.sms.appenum.Message.DELETE_BOOK_CATEGORY_SUCCESS;

@RestController
@RequestMapping("/api")
public class InventoryCategoryController {
    @Autowired
    private InventoryCategoryService inventoryCategoryService;
    @PostMapping("/inventory/category/add/{schoolCode}")
    public ResponseEntity<Object> addInCategoryDetails(@RequestBody InventoryCategoryDetails inventoryCategoryDetails, @PathVariable String schoolCode)throws Exception
    {
        InventoryCategoryDetails result=null;
        try{
            result=inventoryCategoryService.addCategoryDetails(inventoryCategoryDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/inventory/category/get/{inventoryCategoryId}/{schoolCode}")
    public ResponseEntity<Object> getInventoryCategoryById(@PathVariable int inventoryCategoryId, @PathVariable String schoolCode) throws Exception {
        InventoryCategoryDetails result=null;
        try {
            result =inventoryCategoryService.getCategoryDetailsById(inventoryCategoryId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inventory/category/get/all/{schoolCode}")
    public ResponseEntity<Object> getBookCategoryDetails(@PathVariable String schoolCode) throws Exception {
        List<InventoryCategoryDetails> result=null;
        try {
            result =inventoryCategoryService.getAllInventoryCategory(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/inventory/category/{inventoryCategoryId}/{schoolCode}")
    public ResponseEntity<Object> updateInventoryCategoryDetails(@RequestBody InventoryCategoryDetails inventoryCategoryDetails, @PathVariable int inventoryCategoryId, @PathVariable String schoolCode) throws Exception {
        InventoryCategoryDetails result = null;
        try {
            result = inventoryCategoryService.updateInventoryCategoryById(inventoryCategoryDetails,inventoryCategoryId,schoolCode);
                return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/inventory/category/delete/{inventoryCategoryId}/{schoolCode}")
    public ResponseEntity<Object> deleteInventoryCategory(@PathVariable int inventoryCategoryId, @PathVariable String schoolCode) throws Exception {
        boolean result = inventoryCategoryService.deleteInventoryCategoryDetails(inventoryCategoryId,schoolCode);
        if(result){
            BookCategoryResponse response = new BookCategoryResponse(result, 200 , DELETE_BOOK_CATEGORY_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            BookCategoryResponse response = new BookCategoryResponse(result, 400 ,  DELETE_BOOK_CATEGORY_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
