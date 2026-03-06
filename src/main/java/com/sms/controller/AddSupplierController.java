package com.sms.controller;
import com.sms.model.*;
import com.sms.service.AddSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class AddSupplierController {
    @Autowired
    private AddSupplierService addSupplierService;
    @PostMapping("/add/new/supplier/{schoolCode}")
    public ResponseEntity<Object> addNewSupplier(@RequestBody AddSupplierDetails addSupplierDetails, @PathVariable String schoolCode)throws Exception
    {
        AddSupplierDetails result=null;
        try{
            result=addSupplierService.addSupplierDetails(addSupplierDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get/supplier/{supplierId}/{schoolCode}")
    public ResponseEntity<Object> getBookCategoryDetailsById(@PathVariable int supplierId, @PathVariable String schoolCode) throws Exception {
        AddSupplierDetails result=null;
        try {
            result = addSupplierService.getSupplierDetailsById(supplierId,schoolCode);
            System.out.println("result"+result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all/supplier/{schoolCode}")
    public ResponseEntity<Object> getBookDetails(@PathVariable String schoolCode) throws Exception {
        List<AddSupplierDetails> result=null;
        try {
            result =addSupplierService.getSupplierDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/supplier/details/{supplierId}/{schoolCode}")
    public ResponseEntity<Object> updateBookCategoryDetails(@RequestBody AddSupplierDetails addSupplierDetails, @PathVariable int supplierId, @PathVariable String schoolCode) throws Exception {
        AddSupplierDetails result = null;
        try {
            result = addSupplierService.updateSupplierDetails(addSupplierDetails,supplierId,schoolCode);
                return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/soft/delete/supplier")
    public ResponseEntity<Object> deleteStudent(@RequestParam("supplierId") int supplierId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = addSupplierService.softDeleteSupplierDetails(supplierId,schoolCode);
        if (result) {
            SupplierDetailsResponse response = new SupplierDetailsResponse(result, 200, DELETE_SUPPLIER_DETAILS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            SupplierDetailsResponse response = new SupplierDetailsResponse(result, 400, DELETE_SUPPLIER_DETAILS_FAIELD.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
