
package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.model.*;
import com.sms.service.HomeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

import static com.sms.appenum.Message.*;
import static com.sms.appenum.Message.DELETE_SCHOOL_FAILED;

@RestController
@RequestMapping("/api/homework")
public class HomeWorkController {
    @Autowired
    private HomeWorkService homeWorkService;
   /* @PostMapping("/pdf/add/{hwId}")
    public ResponseEntity<Object> addPDF(
            @RequestParam("pdf") MultipartFile file,
           @PathVariable int hwId
    ) throws Exception {
        boolean result = homeWorkService.addPDF(file,hwId);
        if (result) {
            HomeWorkResponse response = new HomeWorkResponse(result, 200, ADD_PDF_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_PDF_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
   @PostMapping("/pdf/add/{schoolCode}/{hwId}")
   public ResponseEntity<Object> addPDF(@RequestParam("pdf") MultipartFile file, @PathVariable String schoolCode, @PathVariable int hwId) throws Exception {
       boolean result = homeWorkService.addPDF(file, schoolCode, hwId);
       if (result) {
           HomeWorkResponse response = new HomeWorkResponse(result, 200, ADD_PDF_SUCCESS.val());
           return new ResponseEntity<>(response, HttpStatus.OK);
       } else {
           ImageResponse response = new ImageResponse(result, 400, ADD_PDF_FAILED.val());
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addHomeWorkDetails(@RequestBody HomeWorkDetails homeWorkDetails, @PathVariable String schoolCode) throws Exception{
        HomeWorkDetails result = null;
        try{
            result = homeWorkService.addHomeWork(homeWorkDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
  
   /* @GetMapping("/get/pdf/{hwId}")
    public ResponseEntity<Object> getPDF(@PathVariable int hwId) {
        try {
            byte[] pdfData = homeWorkService.getPDFBytes(hwId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", hwId + ".pdf");
            headers.setContentLength(pdfData.length);
            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (IOException e) {
            String errorMessage = "PDF is not found";
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            String errorMessage = "An error occurred";
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
   @GetMapping("/get/pdf/{schoolCode}/{hwId}")
   public ResponseEntity<Object> getPDF(@PathVariable String schoolCode, @PathVariable int hwId) {
       try {
           byte[] pdfData = homeWorkService.getPDFBytes(schoolCode, hwId);
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_PDF);
           headers.setContentDispositionFormData("filename", hwId + ".pdf");
           headers.setContentLength(pdfData.length);
           return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
       } catch (IOException e) {
           String errorMessage = "PDF is not found";
           return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
       } catch (Exception e) {
           String errorMessage = "An error occurred";
           return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
    @GetMapping("/get/{schoolCode}")
    public ResponseEntity<Object> getAllHomeWorkDetails(@PathVariable  String schoolCode) throws Exception {
        List<HomeWorkDetails> result = null;

        try {
            result = homeWorkService.getAssignHomeWork(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{hwId}/{schoolCode}")
    public ResponseEntity<Object> getAllHomeWorkDetailsById(@PathVariable int hwId, @PathVariable String schoolCode) throws Exception {
        HomeWorkDetails result = null;

        try {
            result = homeWorkService.getAssignHomeWorkById(hwId, schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{hwId}/{schoolCode}")
    public ResponseEntity<Object> updateAssignHomeWORKById(@RequestBody HomeWorkDetails homeWorkDetails, @PathVariable int hwId, @PathVariable String schoolCode) throws Exception{
        HomeWorkDetails result = null;
        try{
            result = homeWorkService.updateAssignHomeWorkById(homeWorkDetails,hwId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{hwId}/{schoolCode}")
    public ResponseEntity<Object> deleteSchoolDetailsById(@PathVariable int hwId, @PathVariable String schoolCode) throws Exception {
        boolean result = homeWorkService.deleteAssignHomeWorkById(hwId,schoolCode);
        if(result){
            HomeWorkResponse response = new HomeWorkResponse(result, 200 , DELETE_HOMEWORK_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            HomeWorkResponse response = new HomeWorkResponse(result, 400 , DELETE_HOMEWORK_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}





