// com.sms/model/RoomDetails.java
package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RoomDetails {
    private int roomId;
    private int hostelId;
    private String hostelName;
    private String roomNumber;
    private String floorNumber;
    private String roomType;  // Single, Double, Triple, Quad
    private int capacity;
    private int currentOccupancy;
    private int totalBeds;
    private String status;  // Available, Occupied, Maintenance, Reserved
    private double rentAmount;
    private boolean isActive;
    private String remarks;
    private Date createdDate;
    private Date updatedDate;
    private List<BedDetails> beds;  // List of beds in this room
}