package com.sms.controller;

import com.sms.appenum.Message;
import com.sms.model.*;
import com.sms.service.AddRouteDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sms.appenum.Message.*;

@RestController
@RequestMapping("/api")
public class AddRouteDetailsController {
    @Autowired
    private AddRouteDetailsService addRouteDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AddRouteDetailsController.class);

    @PostMapping("/route/add/{schoolCode}")
    public ResponseEntity<Object> addRoute(@RequestBody List<AddRouteDetails> addRouteDetailsList, @PathVariable String schoolCode) {
        try {
            String result = addRouteDetailsService.addBulkRoute(addRouteDetailsList, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while adding employee deductions for school code : {}", schoolCode, e);
            return new ResponseEntity<>("Failed to add employee allowance", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/route/addAtTimeUpdate/{schoolCode}")
    public ResponseEntity<Object> addBulkRouteWithoutAcceptingHashValueInUpdate(@RequestBody List<AddRouteDetails> addRouteDetailsList, @PathVariable String schoolCode) {
        try {
            String result = addRouteDetailsService.addBulkRouteWithoutAcceptingHashValueInUpdate(addRouteDetailsList, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while adding employee deductions for school code : {}", schoolCode, e);
            return new ResponseEntity<>("Failed to add employee allowance", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/route/get/all/{schoolCode}")
    public ResponseEntity<Object> getAllRouteDetails(@PathVariable String schoolCode) throws Exception {
        List<AddRouteDetails> result = null;

        try {
            result = addRouteDetailsService.getAllRouteDetails(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/route/bulk-delete")
    public ResponseEntity<Object> deleteRouteDetails(@RequestBody List<Integer> routeIds, @RequestParam String schoolCode) throws Exception {
        // Validate the input list in the controller
        if (routeIds == null || routeIds.isEmpty()) {
            logger.warn("Received null or empty route details IDs list for school code: {}", schoolCode);
            return new ResponseEntity<>("No records to delete", HttpStatus.BAD_REQUEST);
        }
        try {
            String result = addRouteDetailsService.deleteRouteDetails(routeIds, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while bulk deleting route details for school code: {}", schoolCode, e);
            return new ResponseEntity<>("Failed to bulk delete route details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/route/search/text")
    public ResponseEntity<Object> getRouteDetailsBySearchText(@RequestParam("searchText") String searchText, @RequestParam("schoolCode") String schoolCode){
        try{
            List<AddRouteDetails> result = addRouteDetailsService.getRouteDetailsBySearchText(searchText, schoolCode);
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
    @GetMapping("/route/hash-by")
    public ResponseEntity<Object> getGroupedRoutes(@RequestParam("hashValue") String hashValue, @RequestParam("schoolCode") String schoolCode){
        RouteGroupDto result = null;
        try{
            result = addRouteDetailsService.getGroupedRoutes(hashValue, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/route/update/hash-by/{schoolCode}")
    public ResponseEntity<Object> updateRouteByHash(@RequestBody RouteGroupDto dto,@PathVariable String schoolCode) throws Exception{
        RouteGroupDto result = null;
        try{
            result = addRouteDetailsService.updateRouteByHash(dto, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
