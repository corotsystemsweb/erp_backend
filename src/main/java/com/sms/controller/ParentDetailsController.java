package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.model.ParentDetails;
import com.sms.service.ParentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parentDetails")
public class ParentDetailsController {
    @Autowired
    private ParentDetailsService parentDetailsService;

    @PostMapping("/bulk/add/{schoolCode}")
    public ResponseEntity<Object> addBulkParentDetails(@RequestParam("parentDetails") String parentDetailsJson, @PathVariable String schoolCode, MultipartHttpServletRequest request) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ParentDetails> parentList = mapper.readValue(parentDetailsJson, mapper.getTypeFactory().constructCollectionType(List.class, ParentDetails.class));

            // FIX: Convert all files to byte[] immediately
            Map<String, byte[]> imageBytesMap = new HashMap<>();

            request.getFileMap().forEach((key, file) -> {
                try {
                    if (!file.isEmpty()) {
                        imageBytesMap.put(key, file.getBytes());
                    }
                } catch (Exception ignored) {}
            });

            List<ParentDetails> result = parentDetailsService.addBulkParentDetails(parentList, imageBytesMap, schoolCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/getAll/{schoolCode}")
    public ResponseEntity<Object> getAllParentDetails(@PathVariable String schoolCode){
        List<ParentDetails> result = null;
        try{
            result = parentDetailsService.getAllParentDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getById/{parentId}/{schoolCode}")
    public ResponseEntity<Object> getParentDetailsById(@PathVariable int parentId, @PathVariable String schoolCode){
        ParentDetails result = null;
        try{
            result = parentDetailsService.getParentDetailsById(parentId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/bulk/update/{schoolCode}")
    public ResponseEntity<Object> updateBulkParentDetails(@RequestParam("parentDetails") String parentDetailsJson, @PathVariable String schoolCode, MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            List<ParentDetails> parentList = mapper.readValue(parentDetailsJson, mapper.getTypeFactory().constructCollectionType(List.class, ParentDetails.class));

            // Convert Multipart → byte[]
            Map<String, byte[]> imageBytesMap = new HashMap<>();

            request.getFileMap().forEach((key, file) -> {
                try {
                    if (!file.isEmpty()) {
                        imageBytesMap.put(
                                key.toLowerCase().replaceAll("\\s+", "_"),
                                file.getBytes()
                        );
                    }
                } catch (Exception ignored) {}
            });

            List<ParentDetails> result = parentDetailsService.updateBulkParentDetailsById(parentList, imageBytesMap, schoolCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/bulk/delete/{schoolCode}")
    public ResponseEntity<Object> deleteBulkParents(
            @RequestParam("parentIds") List<Integer> parentIds,
            @PathVariable String schoolCode) {

        try {
            boolean status = parentDetailsService.softDeleteBulkParentDetails(parentIds, schoolCode);
            return new ResponseEntity<>(status, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
