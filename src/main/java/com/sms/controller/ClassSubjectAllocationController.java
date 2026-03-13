
package com.sms.controller;

import com.sms.dao.ClassSubjectAllocationDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.model.ClassTeacherAllocationDetails;
import com.sms.model.StudentResponse;
import com.sms.model.SubjectResponse;
import com.sms.service.ClassSubjectAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/class/subject/allocation")
public class ClassSubjectAllocationController {
@Autowired
   private ClassSubjectAllocationService classSubjectAllocationService;
    @PostMapping("/add/{schoolCode}")
    public ResponseEntity<Object> addAllocateSubject(@RequestBody List<ClassSubjectAllocationDetails> classSubjectAllocationDetailsList, @PathVariable String schoolCode) {
        try {
            List<ClassSubjectAllocationDetails> insertedSubjectList = classSubjectAllocationService.addAllocateSubject(classSubjectAllocationDetailsList, schoolCode);
            if (insertedSubjectList == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Already allocated");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(insertedSubjectList);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllAllocatedSubject(@PathVariable String schoolCode) throws Exception {
        List<ClassSubjectAllocationDetails> result = null;
        try {
            result = classSubjectAllocationService.getAllAlocatedSubject(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{csaId}/{schoolCode}")
    public ResponseEntity<Object> getAllAllocatedSubjectById(@PathVariable int csaId, @PathVariable String schoolCode) throws Exception {
        ClassSubjectAllocationDetails result = null;
        try {
            result = classSubjectAllocationService.getAllocatedClassSubjectById(csaId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{csaId}/{schoolCode}")
    public ResponseEntity<Object> updateClassSubjectAllocation(
            @RequestBody ClassSubjectAllocationDetails classSubjectAllocationDetails,
            @PathVariable int csaId,
            @PathVariable String schoolCode) {

        try {
            ClassSubjectAllocationDetails result = classSubjectAllocationService.updateAllocatedSubject(classSubjectAllocationDetails, csaId, schoolCode);
            if (result == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Already allocated");
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("class_subject_allocation_class_id_section_id_subject_id_key")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Already allocated");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{csaId}/{schoolCode}")
    public ResponseEntity<Object> deleteSubjectDetailsById(@PathVariable int csaId, @PathVariable String schoolCode) throws Exception {
        boolean result = classSubjectAllocationService.deleteAllocatedSubject(csaId, schoolCode);
        if(result){
            SubjectResponse response = new SubjectResponse(result, 200 , DELETE_ALLOCATED_SUBJECT_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            StudentResponse response = new StudentResponse(result, 400 , DELETE_ALLOCATED_SUBJECT_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/subject/{classId}/{sectionId}/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllAllSubject(@PathVariable int classId,@PathVariable int sectionId, @PathVariable int sessionId,  @PathVariable String schoolCode) throws Exception {
        List<ClassSubjectAllocationDetails> result = null;
        try {
            result = classSubjectAllocationService.findSubject(classId,sectionId,sessionId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/structure/{schoolCode}")
    public ResponseEntity<List<Map<String, Object>>> getStructure(
            @PathVariable String schoolCode
    ) throws Exception {
        return ResponseEntity.ok(classSubjectAllocationService.getClassSectionSubjectStructure(schoolCode));
    }

    @PutMapping("/update/{schoolCode}")
    public ResponseEntity<String> updateAllocation(
            @PathVariable String schoolCode,
            @RequestParam int schoolId,
            @RequestParam int sessionId,
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestBody List<Integer> subjectIds
    ) {
        try {
            classSubjectAllocationService.updateAllocatedSubjects(
                    schoolId,
                    sessionId,
                    classId,
                    sectionId,
                    subjectIds,
                    schoolCode
            );
            return ResponseEntity.ok("Subjects updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating subjects: " + e.getMessage());
        }
    }
}
