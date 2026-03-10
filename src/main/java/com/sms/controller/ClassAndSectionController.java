package com.sms.controller;

import com.sms.model.ClassAndSectionDetails;
import com.sms.model.ClassResponse;
import com.sms.model.ClassTeacherAllocationDetails;
import com.sms.model.StudentResponse;
import com.sms.service.ClassAndSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class ClassAndSectionController {
    @Autowired
    private ClassAndSectionService classAndSectionService;
   /* @PostMapping("/class/section/add/{schoolCode}")
    public ResponseEntity<Object> addClassAndSection(@RequestBody ClassAndSectionDetails classAndSectionDetails, @PathVariable String schoolCode){
        ClassAndSectionDetails result = null;
        try{
            result = classAndSectionService.addClassAndSection(classAndSectionDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }*/

    @PostMapping("/class/section/add/{schoolCode}")
    public ResponseEntity<Object> addClassAndSection(@RequestBody ClassAndSectionDetails classAndSectionDetails, @PathVariable String schoolCode) {
        try {
            ClassAndSectionDetails result = classAndSectionService.addClassAndSection(classAndSectionDetails,schoolCode);
            if (result == null) {
                return new ResponseEntity<>("Class and Section combination already exists", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
          //  e.printStackTrace();
            return new ResponseEntity<>("An error occurred while adding class and  section", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/class/section/{classSectionId}/{schoolCode}")
    public ResponseEntity<Object> getClassAndSectionById(@PathVariable int classSectionId, @PathVariable String schoolCode){
        ClassAndSectionDetails result = null;
        try{
            result = classAndSectionService.getClassAndSectionById(classSectionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/class/section/{schoolCode}")
    public ResponseEntity<Object> getAllClassAndSection(@PathVariable String schoolCode){
        List<ClassAndSectionDetails> result = null;
        try{
            result = classAndSectionService.getAllClassAndSection(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/class/section/update/{classSectionId}/{schoolCode}")
    public ResponseEntity<Object> updateClassAndSection(@RequestBody ClassAndSectionDetails classAndSectionDetails, @PathVariable int classSectionId, @PathVariable String schoolCode){
        ClassAndSectionDetails result = null;
        try{
            result = classAndSectionService.updateClassAndSection(classAndSectionDetails, classSectionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/class/section/delete/{classSectionId}/{schoolCode}")
    public ResponseEntity<Object> deleteStudentDetailsById(@PathVariable int classSectionId, @PathVariable String schoolCode) throws Exception {
        boolean result = classAndSectionService.deleteClassAndSection(classSectionId, schoolCode);
        if(result){
            ClassResponse response = new ClassResponse(result, 200 , DELETE_CLASS_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            ClassResponse response = new ClassResponse(result, 400 , DELETE_CLASS_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/class/section/{schoolCode}")
    public ResponseEntity<Object> getAllClassRelatedSection(@PathVariable String schoolCode){
        List<ClassAndSectionDetails> result = null;
        try{
            result = classAndSectionService.getClassRelatedSection(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
