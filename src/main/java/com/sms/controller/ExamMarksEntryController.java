package com.sms.controller;

import com.sms.model.ExamMarksEntryDetails;
import com.sms.model.ExamMarksResponse;
import com.sms.model.TransportAllocationResponse;
import com.sms.service.ExamMarksEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/exam/marks/entry")
public class ExamMarksEntryController {
    @Autowired
    private ExamMarksEntryService examMarksEntryService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addExamMarks(@RequestBody ExamMarksEntryDetails examMarksEntryDetails, @PathVariable String schoolCode){
        ExamMarksEntryDetails result = null;
        try{
            result = examMarksEntryService.addExamMarks(examMarksEntryDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/{emeId}/{schoolCode}")
    public ResponseEntity<Object> getExamMarksById(@PathVariable int emeId, @PathVariable String schoolCode) throws Exception {
        ExamMarksEntryDetails result = null;

        try {
            result = examMarksEntryService.getExamMarksById(emeId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllExamMarks(@PathVariable String schoolCode) throws Exception {
        List<ExamMarksEntryDetails> result = null;

        try {
            result = examMarksEntryService.getAllExamMarks(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{emeId}/{schoolCode}")
    public ResponseEntity<Object> updateExamMarks(@RequestBody ExamMarksEntryDetails examMarksEntryDetails, @PathVariable int emeId, @PathVariable String schoolCode) throws Exception{
        ExamMarksEntryDetails result = null;
        try{
            result = examMarksEntryService.updateExamMarks(examMarksEntryDetails, emeId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{emeId}/{schoolCode}")
    public ResponseEntity<Object> deleteExamMarks(@PathVariable int emeId, @PathVariable String schoolCode) throws Exception {
        boolean result = examMarksEntryService.deleteExamMarks(emeId, schoolCode);
        if(result){
            ExamMarksResponse response = new ExamMarksResponse(result, 200 , DELETE_EXAM_MARKS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            TransportAllocationResponse response = new TransportAllocationResponse(result, 400 , DELETE_EXAM_MARKS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
