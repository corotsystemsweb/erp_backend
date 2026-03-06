package com.sms.controller;

import com.sms.model.*;
import com.sms.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class ClassController {
    @Autowired
    private ClassService classService;
    @PostMapping("/class/add/{schoolCode}")
    public ResponseEntity<Object> addClassDetails(@RequestBody ClassDetails classDetails,@PathVariable String schoolCode) throws Exception{
        ClassDetails result = null;
        try{
            result = classService.addClass(classDetails,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/class/{classId}/{schoolCode}")
    public ResponseEntity<Object> getClassDetailsById(@PathVariable int classId,@PathVariable String schoolCode) throws Exception {
        ClassDetails result = null;

        try {
            result = classService.getClassDetailsById(classId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/class/{schoolCode}")
    public ResponseEntity<Object> getAllClassDetails(@PathVariable String schoolCode) throws Exception {
        List<ClassDetails> result = null;
        try {
            result = classService.getAllClassDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/class/update/{classId}/{schoolCode}")
    public ResponseEntity<Object> updateClassDetailsById(@RequestBody ClassDetails classDetails, @PathVariable int classId, @PathVariable String schoolCode) throws Exception{
        ClassDetails result = null;
        try{
            result = classService.updateClassDetailsById(classDetails, classId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/class/delete/{classId}/{schoolCode}")
    public ResponseEntity<Object> deleteClassDetailsById(@PathVariable int classId,@PathVariable String schoolCode) throws Exception {
        boolean result = classService.deleteClass(classId,schoolCode);
        if(result){
            ClassResponse response = new ClassResponse(result, 200 , DELETE_CLASS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            ClassResponse response = new ClassResponse(result, 400 , DELETE_CLASS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
