
package com.sms.controller;
import com.sms.model.*;
import com.sms.model.HostelDetails;
import com.sms.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

import static com.sms.appenum.Message.*;
@RestController
@RequestMapping("/api")
public class HostelController {

    @Autowired
    private HostelService hostelService;
        // add hostel
    @PostMapping("/hostel/add/{schoolCode}")
    public ResponseEntity<Object> addHostel(
            @RequestBody HostelDetails hostelDetails,
            @PathVariable String schoolCode
    ) throws Exception {
        try {
            HostelDetails result = hostelService.addHostel(hostelDetails, schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   //get hostel by id
    @GetMapping("/hostel/get/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> getHostelById(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            HostelDetails result = hostelService.getHostelById(schoolCode, hostelId);
            if (result == null) {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  // get all hostels
    @GetMapping("/hostel/all/{schoolCode}")
    public ResponseEntity<Object> getAllHostels(@PathVariable String schoolCode) throws Exception {
        try {
            List<HostelDetails> result = hostelService.getAllHostels(schoolCode);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  Add Room with Beds
    @PostMapping("/hostel/room/add/{schoolCode}")
    public ResponseEntity<Object> addRoom(
            @RequestBody AddRoomRequest request,
            @PathVariable String schoolCode
    ) throws Exception {
        try {
            RoomDetails result = hostelService.addRoom(request, schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Failed to add room", HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Rooms by Hostel ID
    @GetMapping("/hostel/rooms/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> getRoomsByHostel(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            List<RoomDetails> result = hostelService.getRoomsByHostel(schoolCode, hostelId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get hostel capacity status
    @GetMapping("/hostel/capacity-status/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> getHostelCapacityStatus(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            HostelCapacityStatus result = hostelService.getHostelCapacityStatus(schoolCode, hostelId);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
       // update hostel
    @PutMapping("/hostel/update/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> updateHostel(
            @PathVariable String schoolCode,
            @PathVariable int hostelId,
            @RequestBody HostelDetails hostelDetails
    ) throws Exception {
        try {
            hostelDetails.setHostelId(hostelId);
            HostelDetails result = hostelService.updateHostel(hostelDetails, schoolCode);
            if (result == null) {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
     // delete hostel by id
    @DeleteMapping("/hostel/delete/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> deleteHostel(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            boolean result = hostelService.deleteHostel(schoolCode, hostelId); // Soft delete
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Hostel not found", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add or Update Hostel Fees
    @PostMapping("/hostel/fees/add/{schoolCode}")
    public ResponseEntity<Object> addHostelFees(
            @RequestBody AddHostelFeesRequest request,
            @PathVariable String schoolCode
    ) throws Exception {
        try {
            HostelFeesDetails result = hostelService.addHostelFees(request, schoolCode);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Failed to add/update hostel fees", HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  Get Hostel Fees by Hostel ID
    @GetMapping("/hostel/fees/{schoolCode}/{hostelId}")
    public ResponseEntity<Object> getHostelFeesByHostel(
            @PathVariable String schoolCode,
            @PathVariable int hostelId
    ) throws Exception {
        try {
            List<HostelFeesDetails> result = hostelService.getHostelFeesByHostel(schoolCode, hostelId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  Get Hostel Fees by Hostel ID and Room Type
    @GetMapping("/hostel/fees/{schoolCode}/{hostelId}/{roomType}")
    public ResponseEntity<Object> getHostelFeesByHostelAndRoomType(
            @PathVariable String schoolCode,
            @PathVariable int hostelId,
            @PathVariable String roomType
    ) throws Exception {
        try {
            HostelFeesDetails result = hostelService.getHostelFeesByHostelAndRoomType(schoolCode, hostelId, roomType);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Fees not found for this hostel and room type", HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}