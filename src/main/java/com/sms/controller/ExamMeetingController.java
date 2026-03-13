package com.sms.controller;

import com.sms.appenum.Message;
import com.sms.model.*;
import com.sms.service.ExamMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/exam/meeting")
public class ExamMeetingController {
    @Autowired
    private ExamMeetingService examMeetingService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addExamMeetingDetails(@RequestBody ExamMeetingDetails examMeetingDetails, @PathVariable String schoolCode) {
        ExamMeetingDetails result = null;
        try {
            result =examMeetingService.addExamMeeting(examMeetingDetails,schoolCode) ;
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get/{emId}/{schoolCode}")
    public ResponseEntity<Object> getExamMeetingById(@PathVariable int emId, @PathVariable String schoolCode) throws Exception {
        ExamMeetingDetails result = null;
        try {
            result = examMeetingService.getExamMeetingById(emId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllExamMeeting(@PathVariable String schoolCode) throws Exception {
        List<ExamMeetingDetails> result = null;
        try {
            result = examMeetingService.getAllExamMeeting(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{emId}/{schoolCode}")
    public ResponseEntity<Object> updateSubjectDetailsById(@RequestBody ExamMeetingDetails examMeetingDetails, @PathVariable int emId, @PathVariable String schoolCode) throws Exception{
        ExamMeetingDetails result = null;
        try{
            result = examMeetingService.updateExamMeeting(examMeetingDetails,emId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/soft/delete")
    public ResponseEntity<Object> deleteExamMeeting(@RequestParam("emId") int emId, @RequestParam("schoolCode") String schoolCode) throws Exception {
        boolean result = examMeetingService.softDeleteExamMeeting(emId,schoolCode);
        if (result) {
            // If deletion is successful
            ExamMeetingResponse response = new ExamMeetingResponse(result, 200, DELETE_EXAM_MEETING_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // If deletion fails
            ExamMeetingResponse response = new ExamMeetingResponse(result, 400, DELETE_EXAM_MEETING_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search/text")
    public ResponseEntity<Object> getExamMeetingDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<ExamMeetingDetails> result = examMeetingService.getExamMeetingDetailsBySearchText(searchText, schoolCode);
            if(result.isEmpty()){
                return new ResponseEntity<>("No results found!", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (EmptyResultDataAccessException e){
            return new ResponseEntity<>("No records found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("No records found!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
