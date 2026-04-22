package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HostelDetails {
    private int hostelId;
    private String name;
    private String type;  // Boys, Girls, Mixed
    private int totalRooms;
    private int totalCapacity;
    private String wardenName;
    private String contactNumber;
    private String address;
    private String status;  // Active, Inactive, Maintenance
    private Date createdDate;
    private Date updatedDate;
    private Boolean deleted;
    private String remarks;
}