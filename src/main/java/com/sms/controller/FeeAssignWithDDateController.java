/*
package com.sms.controller;

import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.service.FeeAsWithDDateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FeeAssignWithDDateController {

    private final FeeAsWithDDateService feeAsWithDDateService;

    public FeeAssignWithDDateController(FeeAsWithDDateService feeAsWithDDateService) {
        this.feeAsWithDDateService = feeAsWithDDateService;
    }


    @PostMapping("/fee-assignment")
    public ResponseEntity<String> assignFee(@RequestBody FeeAssignmentDetailsNew details, @PathVariable String schoolCode) {
        boolean success = feeAsWithDDateService.saveFeeAssignment(details,schoolCode);
        if (success) {
            return ResponseEntity.ok("{\"status\":\"success\"}");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"error\"}");
        }
    }
}
*/
package com.sms.controller;

import com.sms.exception.FeeAssignmentConflictException;
import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.service.FeeAsWithDDateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeeAssignWithDDateController {

    private final FeeAsWithDDateService feeAsWithDDateService;

    public FeeAssignWithDDateController(FeeAsWithDDateService feeAsWithDDateService) {
        this.feeAsWithDDateService = feeAsWithDDateService;
    }

   /* @PostMapping("/fee-assignment/{schoolCode}")
    public ResponseEntity<Object> assignFee(
            @RequestBody FeeAssignmentDetailsNew details,
            @PathVariable String schoolCode) {

        try {
            // Validate school code
            if (schoolCode == null || schoolCode.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"status\":\"error\", \"message\":\"School code is required\"}");
            }

            // Process fee assignment
            boolean success = feeAsWithDDateService.saveFeeAssignment(details, schoolCode);

            if (success) {
                return ResponseEntity.ok()
                        .body("{\"status\":\"success\", \"message\":\"Fee assignment created successfully\"}");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"status\":\"error\", \"message\":\"Failed to create fee assignment\"}");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
*/
   @PostMapping("/fee-assignment/{schoolCode}")
   public ResponseEntity<Object> assignFee(
           @RequestBody FeeAssignmentDetailsNew details,
           @PathVariable String schoolCode) {

       try {
           // Validate school code
           if (schoolCode == null || schoolCode.isBlank()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                       .body("{\"status\":\"error\", \"message\":\"School code is required\"}");
           }

           // Process fee assignment
           boolean success = feeAsWithDDateService.saveFeeAssignment(details, schoolCode);

           if (success) {
               return ResponseEntity.ok()
                       .body("{\"status\":\"success\", \"message\":\"Fee assignment created successfully\"}");
           } else {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("{\"status\":\"error\", \"message\":\"Failed to create fee assignment\"}");
           }

       } catch (FeeAssignmentConflictException e) {
           // Proper conflict response
           return ResponseEntity.status(HttpStatus.CONFLICT)
                   .body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");

       } catch (IllegalArgumentException e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");

       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("{\"status\":\"error\", \"message\":\"Server error: " + e.getMessage() + "\"}");
       }
   }

    @GetMapping("/assignment/edit/{schoolCode}/{faId}")
    public FeeAssignmentDetailsNew getFeeAssignmentEdit(@PathVariable long faId,@PathVariable String schoolCode) {
        return feeAsWithDDateService.getFeeAssignmentDetailsWithStudents(faId,schoolCode);
    }

    @GetMapping("/feewithdue/all/{schoolCode}")
    public List<FeeAssignmentDetailsNew> getAllFeeAssignments(
            @PathVariable String schoolCode,
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) Integer sectionId,
            @RequestParam(required = false) Integer studentId
    ) {
        return feeAsWithDDateService.getAllFeeAssignments(schoolCode, classId, sectionId, studentId);
    }


    @PutMapping("/fee-assignment/edit/{schoolCode}")
    public ResponseEntity<String> editFeeAssignment(
            @PathVariable String schoolCode,
            @RequestBody FeeAssignmentDetailsNew updatedAssignment
    ) {
        feeAsWithDDateService.editFeeAssignment(updatedAssignment, schoolCode);
        return ResponseEntity.ok("Fee assignment updated successfully");
    }

}