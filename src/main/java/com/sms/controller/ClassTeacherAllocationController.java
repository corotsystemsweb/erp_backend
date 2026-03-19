package com.sms.controller;

import com.sms.model.*;
import com.sms.service.ClassTeacherAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/class/teacher/allocation")
public class ClassTeacherAllocationController {
    @Autowired
    private ClassTeacherAllocationService classTeacherAllocationService;

    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addClassTeacherAllocation(@RequestBody ClassTeacherAllocationDetails classTeacherAllocationDetails, @PathVariable String schoolCode) {
        try {
            ClassTeacherAllocationDetails result = classTeacherAllocationService.addClassTeacherAllocation(classTeacherAllocationDetails, schoolCode);
            if (result == null) {
                return new ResponseEntity<>("Class and Section combination already exists", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while adding class teacher allocation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{ctaId}/{schoolCode}")
    public ResponseEntity<Object> getSubjectDetailsById(@PathVariable int ctaId, @PathVariable String schoolCode) throws Exception {
        ClassTeacherAllocationDetails result = null;
        try {
            result = classTeacherAllocationService.getClassTeacherAllocationById(ctaId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getSubjectDetailsById(@PathVariable String schoolCode) throws Exception {
        List<ClassTeacherAllocationDetails> result = null;
        try {
            result = classTeacherAllocationService.getAllClassTeacherAllocation(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{ctaId}/{schoolCode}")
    public ResponseEntity<Object> updateClassTeacherAllocationById(@RequestBody ClassTeacherAllocationDetails classTeacherAllocationDetails, @PathVariable int ctaId, @PathVariable String schoolCode) throws Exception{
        ClassTeacherAllocationDetails result = null;
        try{
            result = classTeacherAllocationService.updateClassTeacherAllocationById(classTeacherAllocationDetails, ctaId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{ctaId}/{schoolCode}")
    public ResponseEntity<Object> deleteClassTeacherAllocationById(@PathVariable int ctaId, @PathVariable String schoolCode) throws Exception {
        boolean result = classTeacherAllocationService.deleteClassTeacherAllocationById(ctaId,schoolCode);
        if(result){
            ClassTeacherAllocationResponse response = new ClassTeacherAllocationResponse(result, 200 , DELETE_CLASS_TEACHER_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StudentResponse response = new StudentResponse(result, 400 , DELETE_CLASS_TEACHER_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("all/teacher/{sessionId}/{classId}/{sectionId}/{schoolCode}")
    public ResponseEntity<Object> getAllAllTeacherByClassAndSection(@PathVariable int sessionId,@PathVariable int classId,@PathVariable int sectionId,@PathVariable String schoolCode) throws Exception {
        List<ClassTeacherAllocationDetails> result = null;
        try {
            result = classTeacherAllocationService.findAllTeacherByClassAndSection(sessionId,classId,sectionId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
