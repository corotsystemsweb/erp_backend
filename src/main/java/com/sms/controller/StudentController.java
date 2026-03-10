package com.sms.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.model.*;
import com.sms.service.MasterSequenceDetailsService;
import com.sms.service.StudentService;
import com.sms.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private final StudentService studentService;
    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/image/add/{schoolCode}/{studentId}")
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @PathVariable String schoolCode, @PathVariable int studentId) throws Exception {
        boolean result = studentService.addImage(file, schoolCode, studentId);
        if (result) {
            ImageResponse response = new ImageResponse(result, 200, ADD_IMAGE_SUCCESS.val());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ImageResponse response = new ImageResponse(result, 400, ADD_IMAGE_FAILED.val());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/image/get/{schoolCode}/{studentId}")
    public ResponseEntity<Object> getImage(@PathVariable String schoolCode, @PathVariable int studentId) {
        try {
            StudentDetails imageData = studentService.getImage(schoolCode, studentId);
            return ResponseEntity.ok().body(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Image not found", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/full/add/{schoolCode}")
    public ResponseEntity<Object> addFullStudent(@RequestParam("data") String json, @PathVariable String schoolCode, @RequestParam(value = "studentImage", required = false) MultipartFile studentImage, MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Convert JSON → Java object
            StudentFullRequestDetails requestData = mapper.readValue(json, StudentFullRequestDetails.class);

            // ------------------------------------------
            // Extract ONLY Parent Images Dynamically
            // Exclude "studentImage"
            // ------------------------------------------
            Map<String, byte[]> parentImageMap = new HashMap<>();

            request.getFileMap().forEach((key, file) -> {
                try {
                    if (!key.equals("studentImage") && !file.isEmpty()) {
                        parentImageMap.put(key.toLowerCase().replaceAll("\\s+", "_"), file.getBytes());
                    }
                } catch (Exception ignored) {}
            });

            // ------------------------------------------
            // Call Service
            // ------------------------------------------
            StudentFullResponseDetails result = studentService.addFullStudentData(requestData, schoolCode, parentImageMap, studentImage);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed: " + e.getMessage());
            if (e.getMessage() == null || !e.getMessage().contains("already exists")) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed: " + e.getMessage());
        }
    }

    @PostMapping("/personal/details/add/{schoolCode}")
    public ResponseEntity<Object> addStudentPersonalDetails(@RequestBody StudentDetails studentDetails, @PathVariable String schoolCode) throws Exception{
        StudentDetails result = null;
        try{
            result = studentService.addStudentPersonalDetails(studentDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/academic/details/add/{schoolCode}")
    public ResponseEntity<Object> addStudentAcademicDetails(@RequestBody StudentDetails studentDetails, @PathVariable String schoolCode) throws Exception{
        StudentDetails result = null;
        try{
            result = studentService.addStudentAcademicDetails(studentDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/check/registration/{registrationNumber}/{schoolCode}")
    public ResponseEntity<Object> checkRegistrationNumberExists(@PathVariable String registrationNumber, @PathVariable String schoolCode) throws Exception{
        try{
            boolean exists = studentService.checkRegistrationNumberExists(registrationNumber, schoolCode);
            if(exists){
                return new ResponseEntity<>("Registration number is already available.", HttpStatus.CONFLICT);
            }else {
                return new ResponseEntity<>("Registration number is not available",HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>("An error occurred while checking the registration number.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/details")
    public ResponseEntity<Object> getStudentDetailsById(@RequestParam("studentId") int studentId, @RequestParam("schoolCode") String schoolCode) throws Exception{
        StudentFullResponseDetails result = null;
        try{
            result = studentService.getStudentDetailsById(studentId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all/details/{sessionId}/{schoolCode}")
    public ResponseEntity<Object> getAllStudentDetails(@PathVariable int sessionId,@PathVariable String schoolCode) throws Exception {
        List<StudentFullResponseDetails> result = null;

        try {
            result = studentService.getAllStudentDetails(sessionId,schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PutMapping("/personal/details/update/{studentId}/{schoolCode}")
//    public ResponseEntity<Object> updateStudentPersonalDetails(@RequestBody StudentDetails studentDetails, @PathVariable int studentId, @PathVariable String schoolCode) throws Exception{
//        StudentDetails result = null;
//        try{
//            result = studentService.updateStudentPersonalDetails(studentDetails, studentId, schoolCode);
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        }catch(Exception e){
//            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    @PutMapping("/academic/details/update/{studentId}/{schoolCode}")
//    public ResponseEntity<Object> updateStudentAcademicDetails(@RequestBody StudentDetails studentDetails, @PathVariable int studentId, @PathVariable String schoolCode) throws Exception{
//        StudentDetails result = null;
//        try{
//            result = studentService.updateStudentAcademicDetails(studentDetails, studentId, schoolCode);
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        }catch(Exception e){
//            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping("/update/{schoolCode}")
    public ResponseEntity<Object> updateFullStudent(@RequestParam("data") String json, @PathVariable String schoolCode, @RequestParam(value = "studentImage", required = false) MultipartFile studentImage, MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            StudentFullRequestDetails req = mapper.readValue(json, StudentFullRequestDetails.class);

            // Parent Image Map — SAME LOGIC AS ADD
            Map<String, byte[]> parentImages = new HashMap<>();

            request.getFileMap().forEach((key, file) -> {
                try {
                    if (!key.equals("studentImage") && !file.isEmpty()) {
                        parentImages.put(key.toLowerCase().replaceAll("\\s+", "_"), file.getBytes());
                    }
                } catch (Exception ignored) {}
            });

            // Call Update Service
            StudentFullResponseDetails res = studentService.updateFullStudentData(req, schoolCode, parentImages, studentImage);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PatchMapping("/soft/delete")
   public ResponseEntity<Object> deleteStudent(@RequestParam("studentId") int studentId, @RequestParam("schoolCode") String schoolCode) throws Exception {
       boolean result = studentService.softDeleteStudent(studentId, schoolCode);
       if (result) {
           // If deletion is successful
           StudentResponse response = new StudentResponse(result, 200, DELETE_STUDENT_SUCCESS.val());
           return new ResponseEntity<>(response, HttpStatus.OK);
       } else {
           // If deletion fails
           StudentResponse response = new StudentResponse(result, 400, DELETE_STUDENT_FAILED.val());
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
   //searching the student details based on studentClass and studentSection
    @GetMapping("/attendance/search")
    public ResponseEntity<Object> searchStudentByClassNameAndSection(
            @RequestParam("studentClass") String studentClass,
            @RequestParam("studentSection") String studentSection,
            @RequestParam("schoolCode") String schoolCode) {
        List<StudentDetails> result = null;
        try {
            result = studentService.searchStudentByClassNameAndSection(studentClass, studentSection, schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/fee/search")
    public ResponseEntity<Object> searchByClassSectionAndSession(@RequestParam("studentClass") int studentClass,
                                                                 @RequestParam("studentSection") int studentSection,
                                                                 @RequestParam("sessionId") int sessionId,
                                                                 @RequestParam("schoolCode") String schoolCode){
        List<StudentDetails> result = null;
        try{
            result = studentService.searchByClassSectionAndSession(studentClass, studentSection, sessionId, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //calculating the total number of student available in school
    @GetMapping("/total/{schoolCode}")
    public ResponseEntity<Object> getTotalStudent(@PathVariable String schoolCode){
        int result = 0;
        try{
            result = studentService.getTotalStudent(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*@GetMapping("/id/card/{studentId}/{schoolCode}")
    public ResponseEntity<Object> getStudentWithPhoto(@PathVariable int studentId, @PathVariable String schoolCode) {
        try {
            StudentDetails studentDetails = studentService.getStudentDetailsById(studentId, schoolCode);
            if (studentDetails != null) {
                StudentDetails studentImage = studentService.getImage(schoolCode, studentId);
                studentDetails.setStudentImage(studentImage.getStudentImage());
            }
            return new ResponseEntity<>(studentDetails, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @GetMapping("/search/text")
    public ResponseEntity<Object> getStudentDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<StudentDetails> result = studentService.getStudentDetailsBySearchText(searchText, schoolCode);
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
    /////////////////////////////////////////////////////////////////////////////
    @PostMapping("/upload/student/personal/details")
    public ResponseEntity<Object> uploadExcelForStudentPersonalDetails(@RequestParam("file") MultipartFile file, @RequestParam("schoolCode") String schoolCode) {
        // First, check if the file is in the correct Excel format
        if (!ExcelHelper.checkExcelFormat(file)) {
            return new ResponseEntity<>("Invalid file format. Please upload an Excel file.", HttpStatus.BAD_REQUEST);
        }
        try {
            // Process the Excel file and insert student details
            List<StudentDetails> students = studentService.processExcelFileForStudentPersonalDetails(file, schoolCode);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log error for debugging
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/upload/student/academic/details")
    public ResponseEntity<Object> uploadExcelForStudentAcademicDetails(@RequestParam("file") MultipartFile file, @RequestParam("schoolCode") String schoolCode) {
        // First, check if the file is in the correct Excel format
        if (!ExcelHelper.checkExcelFormat(file)) {
            // Return a "Bad Request" response if the file is not a valid Excel format
            return new ResponseEntity<>("Invalid file format. Please upload an Excel file.", HttpStatus.BAD_REQUEST);
        }
        try {
            // If the file is in the correct format, process it
            List<StudentDetails> students = studentService.processExcelFileForStudentAcademicDetails(file, schoolCode);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            // Print stack trace for debugging and return an Internal Server Error response
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/details/{parentId}/{schoolCode}")
    public ResponseEntity<Object> getStudentDetailsByParentId(@PathVariable int parentId, @PathVariable String schoolCode) throws Exception {
        List<StudentDetails> result = null;

        try {
            result = studentService.getStudentDetailsByParentId(parentId, schoolCode);   // List of object
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   /* @GetMapping("/tc/eligibility/{studentId}/{schoolCode}")
    public ResponseEntity<Object> checkStudentTcEligibilty(@PathVariable long studentId, @PathVariable String schoolCode) throws Exception {
        boolean result = false;

        try {
            result = studentService.isEligibleForTC(studentId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @GetMapping("/tc/eligibility/{studentId}/{schoolCode}")
    public ResponseEntity<Map<String, Object>> checkStudentTcEligibility(
            @PathVariable long studentId,
            @PathVariable String schoolCode) {

        Map<String, Object> response = new HashMap<>();

        try {
            boolean isEligible = studentService.isEligibleForTC(studentId, schoolCode);
            response.put("eligible", isEligible);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Could not determine eligibility: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    @GetMapping("/details/for/tc/{sessionId}")
    public ResponseEntity<Object> getStudentDetailsForTc(@RequestParam("studentId") int studentId,@PathVariable int sessionId, @RequestParam("schoolCode") String schoolCode) throws Exception{
        StudentDetails result = null;
        try{
            result = studentService.getStudentDetailsForTc(studentId,sessionId,schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/birthday/{schoolCode}")
    public ResponseEntity<Object> getBirthday(@PathVariable String schoolCode){
        List<StudentDetails> birthday = null;
        try{
            birthday = studentService.getBirthday(schoolCode);
            return new ResponseEntity<>(birthday, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(birthday, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/global/search/{schoolCode}")
    public ResponseEntity<List<StudentDetails>> globalSearch(
            @PathVariable String schoolCode,
            @RequestParam int sessionId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String fatherName,
            @RequestParam(required = false) String admissionNumber,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String rollNumber) {

        try {
            List<StudentDetails> searchResults = studentService.globalSearch(
                    firstName,
                    lastName,
                    fatherName,
                    admissionNumber,
                    phoneNumber,
                    rollNumber,
                    sessionId,
                    schoolCode
            );
            return new ResponseEntity<>(searchResults, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();  // Consider logging properly
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
