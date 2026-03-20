package com.sms.controller;

import com.sms.model.TimetableDetails;
import com.sms.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {
    @Autowired
    private TimetableService timetableService;

    @PostMapping("/add/{schoolCode}")
    public void createTimetableEntry(@RequestBody TimetableDetails timetable, @PathVariable String schoolCode) {
        try {
            timetableService.addTimetableEntry(timetable, schoolCode);
            System.out.println("Timetable entry created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating timetable entry: " + e.getMessage());
        }
    }

    @PostMapping("/create-weekly/{schoolCode}")
    public ResponseEntity<?> createWeeklyTimetable(@RequestBody List<TimetableDetails> timetableDetails, @PathVariable String schoolCode){
        try{
            String response = timetableService.createTimetableBulkWithMaster(timetableDetails, schoolCode);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/class-section/{schoolCode}")
    public ResponseEntity<List<TimetableDetails>> getTimetable(
            @RequestParam int classId,
            @RequestParam int sectionId,
            @RequestParam int sessionId,
            @PathVariable String schoolCode) {
        try {
            List<TimetableDetails> timetable = timetableService.getAllTimeTableBasedClassSection(classId, sectionId, sessionId, schoolCode);
            return ResponseEntity.ok(timetable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{schoolCode}/bulk")
    public ResponseEntity<int[]> updateTimetableBulk(
            @PathVariable String schoolCode,
            @RequestBody List<TimetableDetails> timetableUpdates) {
        try {
            int[] updateCounts = timetableService.updateTimetableBulk(timetableUpdates, schoolCode);
            return ResponseEntity.ok(updateCounts);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Bulk Delete Endpoint
    @DeleteMapping("/{schoolCode}/bulk-delete")
    public ResponseEntity<Map<String, Object>> deleteTimetablesBulk(
            @PathVariable String schoolCode,
            @RequestBody List<Integer> timetableIds) throws SQLException {

        int[] deleteResults = timetableService.deleteTimetableBulk(timetableIds, schoolCode);
        int successCount = Arrays.stream(deleteResults).sum();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deleted " + successCount + " timetable entries");
        response.put("affectedRows", deleteResults);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/staff/{schoolCode}")
    public ResponseEntity<?> getAllTimeTableBasedOnStaffId(@RequestParam int sessionId, @RequestParam int staffId, @PathVariable String schoolCode) {
        try {
            List<TimetableDetails> timetable = timetableService.getAllTimeTableBasedOnStaffId(sessionId, staffId, schoolCode);
            if(timetable == null || timetable.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No timetable found for this teacher.");
            }
            return ResponseEntity.ok(timetable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching timetable");
        }
    }

    @GetMapping("/all/schedule")
    public ResponseEntity<Object> getAllTimeTableSchedule(@RequestParam int sessionId, @RequestParam String schoolCode){
        try{
            List<TimetableDetails> timetableDetailsList = timetableService.getAllTimeTableSchedule(sessionId, schoolCode);
            if(timetableDetailsList == null || timetableDetailsList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No timetable schedule is found. ");
            }
            return ResponseEntity.ok(timetableDetailsList);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching timetable schedule");
        }
    }

    @PutMapping("/update/{schoolCode}")
    public ResponseEntity<?> updateTimetable(@RequestBody TimetableDetails timetableDetails, @PathVariable String schoolCode) {

        try {
            TimetableDetails result = timetableService.updateTimetable(timetableDetails, schoolCode);
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong while updating timetable");
        }
    }

    @DeleteMapping("/delete/{timetableId}/{schoolCode}")
    public ResponseEntity<?> deleteTimetable(@PathVariable int timetableId, @PathVariable String schoolCode){
        try{
            String result = timetableService.deleteTimetable(timetableId, schoolCode);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while deleting timetable: " + e.getMessage());
        }
    }
}
