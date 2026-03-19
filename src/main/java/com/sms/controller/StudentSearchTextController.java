package com.sms.controller;

import com.sms.exception.StudentNotFoundException;
import com.sms.model.FeeDepositDetails;
import com.sms.model.StudentSearchTextDetails;
import com.sms.service.StudentSearchTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentSearchTextController {
    @Autowired
    private StudentSearchTextService studentSearchTextService;

    @GetMapping("/student-search-text")
    public ResponseEntity<Object> getStudentDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<StudentSearchTextDetails> result = studentSearchTextService.getStudentDetailsBySearchText(searchText, schoolCode);
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
    @GetMapping("/{classId}/{sectionId}/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getStudentFeeDetailsBasedOnClassSectionSessionForSearchText(
            @PathVariable int classId,
            @PathVariable int sectionId,
            @PathVariable int sessionId,
            @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
            @PathVariable String schoolCode) throws Exception{
        List<StudentSearchTextDetails> result = studentSearchTextService.getStudentFeeDetailsBasedOnClassSectionSessionForSearchText(classId, sectionId, sessionId, searchText, schoolCode);
        if (result == null || result.isEmpty()) {
            throw new StudentNotFoundException("Fee details not found for studentId: " + classId + ", sectionId: " + sectionId + ", sessionId: " + sessionId);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getFeeDetailsBasedOnSession(
            @PathVariable int sessionId,
            @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
            @PathVariable String schoolCode) throws Exception{
        List<StudentSearchTextDetails> result = studentSearchTextService.getFeeDatilsBasedOnSession(sessionId,searchText,schoolCode);
        if (result == null || result.isEmpty()) {
            throw new StudentNotFoundException("Fee details not found for studentId: ");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
