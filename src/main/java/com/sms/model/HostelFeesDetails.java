// com/sms/model/HostelFeesDetails.java
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
public class HostelFeesDetails {
    private int feeId;
    private int schoolId;
    private int sessionId;
    private int hostelId;
    private String hostelName;  // Denormalized for response
    private String roomType;    // Single, Double, Triple, Quad
    private double monthlyFee;
    private double securityDeposit;
    private double admissionFee;
    private Date createdDate;
    private Date updatedDate;
    private Boolean deleted;
}