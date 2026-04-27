// com/sms/model/HostelCapacityStatus.java
package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HostelCapacityStatus {
    private int hostelId;
    private String hostelName;

    // Room statistics
    private int totalRoomsAllowed;      // total_rooms from hostel table
    private int roomsAdded;              // current rooms count
    private int availableRooms;          // totalRoomsAllowed - roomsAdded

    // Bed/Capacity statistics
    private int totalCapacityAllowed;    // total_capacity from hostel table
    private int bedsAdded;               // total beds added so far
    private int availableCapacity;       // totalCapacityAllowed - bedsAdded

    // Percentage filled
    private double roomsFilledPercentage;
    private double capacityFilledPercentage;
}