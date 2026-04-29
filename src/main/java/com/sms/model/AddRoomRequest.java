// com.sms/model/AddRoomRequest.java
package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AddRoomRequest {
    private int hostelId;
    private String roomNumber;
    private String floorNumber;
    private String roomType;  // Single, Double, Triple, Quad
    private int capacity;
    private Double rentAmount;
    private String remarks;
    private List<BedRequest> beds;  // List of beds to create
}
