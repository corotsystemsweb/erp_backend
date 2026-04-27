// com/sms/model/AddHostelFeesRequest.java
package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AddHostelFeesRequest {
    private int schoolId;
    private int sessionId;
    private int hostelId;
    private String roomType;    // Single, Double, Triple, Quad
    private double monthlyFee;
    private double securityDeposit;
    private double admissionFee;
}