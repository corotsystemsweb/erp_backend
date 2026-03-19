package com.sms.controller;

import com.sms.model.FeeSummary;
import com.sms.model.FrequencyDetails;
import com.sms.model.MonthWiseFeeDueDetails;
import com.sms.model.PendingFee;
import com.sms.service.FeeReportService;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/fee-reports")
public class FeeReportController {
    private final FeeReportService feeReportService;

    public FeeReportController(FeeReportService feeReportService) {
        this.feeReportService = feeReportService;
    }

/*    @GetMapping("/summary/{reportType}/{schoolCode}")
    public ResponseEntity<?> getFeeSummary(
            @PathVariable String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PathVariable String schoolCode)
    {

        try {
            validateReportType(reportType);
            return ResponseEntity.ok(feeReportService.getFeeSummary(reportType, start, end,schoolCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.internalServerError().body("Error retrieving data");
        }
    }*/

    @GetMapping("/summary/{reportType}/{schoolCode}")
    public ResponseEntity<?> getFeeSummary(
            @PathVariable String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PathVariable String schoolCode) {

        try {
            validateReportType(reportType);

            // Step 1: Fetch data list
            List<FeeSummary> data = feeReportService.getFeeSummary(reportType, start, end, schoolCode);

            // Step 2: Calculate the total sum
            BigDecimal totalSum = data.stream()
                    .map(FeeSummary::getTotalCollected)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Step 3: Build response map (custom JSON structure)
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            response.put("totalSum", totalSum);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.internalServerError().body("Error retrieving data");
        }
    }


    private void validateReportType(String type) {
        if (!Set.of("DAILY", "MONTHLY", "YEARLY").contains(type.toUpperCase())) {
            throw new IllegalArgumentException("Invalid report type. Valid values: DAILY, MONTHLY, YEARLY");
        }
    }

    @GetMapping("/pending/{sessionId}/{schoolCode}")
    public ResponseEntity<?> getPendingFees(
            @RequestParam String level,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long studentId,
            @PathVariable Long sessionId,
            @PathVariable String schoolCode) {

        try {
            List<Map<String, Object>> result = feeReportService.getPendingFees(
                    level, classId, sectionId, studentId, sessionId, schoolCode
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

   @GetMapping("/demand-slip/{sessionId}/{schoolCode}")
   public ResponseEntity<?> getDemandSlip(
           @RequestParam(required = false) Long classId,
           @RequestParam(required = false) Long sectionId,
           @RequestParam(required = false) Long studentId,
           @PathVariable Long sessionId,
           @PathVariable String schoolCode,
           @RequestParam(required = false) LocalDate cutoffDate

   ) {

       try {
           List<PendingFee> result = feeReportService.getDemandSlip(
                   classId, sectionId, studentId, sessionId, schoolCode,cutoffDate
           );
           return ResponseEntity.ok(result);
       } catch (IllegalArgumentException e) {
           return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
       }
   }

    @GetMapping("/currentDate/{schoolCode}")
    public ResponseEntity<Object> getAllFrequencyDetails(@PathVariable String schoolCode) throws Exception {
        List<FeeSummary> result = null;
        try {
            result = feeReportService.getFeeSummaryByClass(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

  @GetMapping("/monthly-due/{schoolCode}")
  public List<MonthWiseFeeDueDetails> getMonthlyDue(
          @RequestParam int sessionId,
          @RequestParam int classId,
          @RequestParam(required = false) Integer sectionId,  // Made optional
          @RequestParam(required = false) Integer studentId, // Added optional student filter
          @RequestParam(required = false) String month,      // Added optional month filter (format: "Mon YYYY")
          @PathVariable String schoolCode
  ) {
      return feeReportService.getMonthlyDueDetails(
              sessionId,
              classId,
              sectionId,
              studentId,
              month,
              schoolCode
      );
  }
}
