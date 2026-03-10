package com.sms.controller;

import com.sms.model.*;
import com.sms.service.ClassSubjectTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/class/subject/teacher")
public class ClassSubjectTeacherController {
    @Autowired
    private ClassSubjectTeacherService classSubjectTeacherService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> assignSubjectClassTeacher(@RequestBody ClassSubjectTeacherDetails classSubjectTeacherDetails, @PathVariable String schoolCode) throws Exception{
        ClassSubjectTeacherDetails result = null;
        try{
            result = classSubjectTeacherService.assignSubjectClassTeacher(classSubjectTeacherDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getClassSubjectTeacher(@PathVariable String schoolCode) throws Exception {
        List<ClassSubjectTeacherDetails> result = null;
        try {
            result = classSubjectTeacherService.ClassSubjectTeacher(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{cstaId}/{schoolCode}")
    public ResponseEntity<Object> getClassSubjectTeacherById(@PathVariable int cstaId, @PathVariable String schoolCode) throws Exception {
        ClassSubjectTeacherDetails result = null;
        try {
            result = classSubjectTeacherService.getClassSubjectTeacherById(cstaId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{cstaId}/{schoolCode}")
    public ResponseEntity<Object> updateSubjectDetailsById(@RequestBody ClassSubjectTeacherDetails classSubjectTeacherDetails, @PathVariable int cstaId, @PathVariable String schoolCode) throws Exception{
        ClassSubjectTeacherDetails result = null;
        try{
            result = classSubjectTeacherService.updateClassSubjectTeacher(classSubjectTeacherDetails,cstaId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{cstaId}/{schoolCode}")
    public ResponseEntity<Object> deleteSubjectDetailsById(@PathVariable int cstaId, @PathVariable String schoolCode) throws Exception {
        boolean result = classSubjectTeacherService.deleteClassSubjectTeacher(cstaId,schoolCode);
        if(result){
            ClassSubjectTeacherResponse response = new ClassSubjectTeacherResponse(result, 200 , DELETE_CLASS_SUBJECT_TEACHER_ALLOCATION_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            ClassSubjectTeacherResponse response = new ClassSubjectTeacherResponse(result, 400 ,  DELETE_CLASS_SUBJECT_TEACHER_ALLOCATION_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/bulk-insert/{schoolCode}")
    public ResponseEntity<String> bulkInsert(@RequestBody List<ClassSubjectTeacherDetails> classSubjectTeacherDetails, @PathVariable String schoolCode) {
        classSubjectTeacherService.bulkInsertAllocations(classSubjectTeacherDetails,schoolCode);
        return ResponseEntity.ok("Bulk insert completed successfully");
    }
    @GetMapping("/get/{schoolCode}")
    public ResponseEntity<Object> getAllocatedTeacher(
            @PathVariable String schoolCode,
            @RequestParam int sessionId,
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam int subjectId
    ) throws Exception {

        List<ClassSubjectTeacherDetails> result = null;
        try {
            result = classSubjectTeacherService.findAllocatedTeacher(sessionId,classId,sectionId,subjectId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
