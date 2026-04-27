// com.sms/model/BedDetails.java
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
public class BedDetails {
    private int bedId;
    private int roomId;
    private String bedNumber;
    private String bedLabel;  // A, B, C, D
    private String status;  // Available, Occupied, Reserved, Maintenance
    private Integer studentId;
    private Date allocatedDate;
    private Date createdDate;
    private Date updatedDate;
}