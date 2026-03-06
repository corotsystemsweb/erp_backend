package com.sms.controller;

import com.sms.model.AddBookItemsResponse;
import com.sms.model.AddInventoryItemsDetails;
import com.sms.service.AddInventoryItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class AddInventoryItemsController {
    @Autowired
    private AddInventoryItemsService addInventoryItemsService;
    @PostMapping("/inventory/items/add/{schoolCode}")
    public ResponseEntity<Object> addItems(@RequestBody AddInventoryItemsDetails addInventoryItemsDetails, @PathVariable String schoolCode)throws Exception
    {
        AddInventoryItemsDetails result=null;
        try{
            result=addInventoryItemsService.addItems(addInventoryItemsDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/inventory/items/get/{addItemsId}/{schoolCode}")
    public ResponseEntity<Object> getItemsById(@PathVariable int addItemsId, @PathVariable String schoolCode) throws Exception {
        AddInventoryItemsDetails result=null;
        try {
            result =addInventoryItemsService.getItemsById(addItemsId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inventory/items/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllItems(@PathVariable String schoolCode) throws Exception {
        List<AddInventoryItemsDetails> result=null;
        try {
            result =addInventoryItemsService.getAllItems(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/inventory/items/{addItemsId}/{schoolCode}")
    public ResponseEntity<Object> updateItems(@RequestBody AddInventoryItemsDetails addInventoryItemsDetails, @PathVariable int addItemsId, @PathVariable String schoolCode) throws Exception {
        AddInventoryItemsDetails result = null;
        try {
            result = addInventoryItemsService.updateItems(addInventoryItemsDetails,addItemsId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/inventory/items/delete/{addItemsId}/{schoolCode}")
    public ResponseEntity<Object> softDeleteItems(@PathVariable int addItemsId, @PathVariable String schoolCode) throws Exception {
        boolean result = addInventoryItemsService.softDeleteItems(addItemsId,schoolCode);
        if(result){
            AddBookItemsResponse response = new AddBookItemsResponse(result, 200 , DELETE_BOOK_ITEMS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            AddBookItemsResponse response = new AddBookItemsResponse(result, 400 ,  DELETE_BOOK_ITEMS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
