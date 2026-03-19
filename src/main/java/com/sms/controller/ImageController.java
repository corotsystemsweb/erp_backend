package com.sms.controller;

import com.sms.model.ImageDetails;
import com.sms.model.ImageResponse;
import com.sms.model.StudentDetails;
import com.sms.model.StudentResponse;
import com.sms.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.sms.appenum.Message.ADD_IMAGE_FAILED;
import static com.sms.appenum.Message.ADD_IMAGE_SUCCESS;
@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private ImageService imageService;
   @PostMapping("/images/{id}")
   public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable int id) throws Exception {
       boolean result = imageService.addImage(file,id);
       if (result) {
           ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
           return new ResponseEntity<>(response, HttpStatus.OK);
       } else {
           ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }

    /*@GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            byte[] imageData = imageService.getImage(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // You may need to adjust the media type based on your image type
                    .body(imageData);
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
    /*@GetMapping("/{id}")
    public ResponseEntity<String> getImage(@PathVariable int id){
        try {
            String imageData = imageService.getImage(id);
            return ResponseEntity.ok()
                    .body(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
    @GetMapping("/{id}")
    public ResponseEntity<Object> getImage(@PathVariable int id){
        try {
            ImageDetails imageData = imageService.getImage(id);
            return ResponseEntity.ok()
                    .body(imageData);
        } catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
