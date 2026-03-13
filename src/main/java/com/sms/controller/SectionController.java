package com.sms.controller;

import com.sms.model.ClassResponse;
import com.sms.model.SectionDetails;
import com.sms.model.SectionResponse;
import com.sms.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class SectionController {
    @Autowired
    private SectionService sectionService;
    @PostMapping("/section/add/{schoolCode}")
    public ResponseEntity<Object> addSectionDetails(@RequestBody SectionDetails sectionDetails, @PathVariable String schoolCode) throws Exception{
        SectionDetails result = null;
        try{
            result = sectionService.addSection(sectionDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/section/{sectionId}/{schoolCode}")
    public ResponseEntity<Object> getSectionDetailsById(@PathVariable int sectionId, @PathVariable String schoolCode) throws Exception {
        SectionDetails result = null;

        try {
            result = sectionService.getSectionDetailsById(sectionId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/section/{schoolCode}")
    public ResponseEntity<Object> getAllSectionDetails(@PathVariable String schoolCode) throws Exception {
        List<SectionDetails> result = null;
        try {
            result = sectionService.getAllSectionDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/section/update/{sectionId}/{schoolCode}")
    public ResponseEntity<Object> updateSectionDetailsById(@RequestBody SectionDetails sectionDetails, @PathVariable int sectionId, @PathVariable String schoolCode) throws Exception{
        SectionDetails result = null;
        try{
            result = sectionService.updateSectionDetailsById(sectionDetails, sectionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/section/delete/{sectionId}/{schoolCode}")
    public ResponseEntity<Object> deleteSectionDetailsById(@PathVariable int sectionId, @PathVariable String schoolCode) throws Exception {
        boolean result = sectionService.deleteSection(sectionId, schoolCode);
        if(result){
            SectionResponse response = new SectionResponse(result, 200 , DELETE_SECTION_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            ClassResponse response = new ClassResponse(result, 400 , DELETE_SECTION_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
