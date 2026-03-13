package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentTransportDetails {
    private Integer studentTransportId;
    private Integer schoolId;
    private Integer sessionId;
    private Integer studentId;
    private Integer routeId;
    private BigDecimal fee;
    private String status;
    private Date startDate;
    private Date endDate;
    private String firstName;
    private String lastName;
    private String boardingPoint;
    private String destination;
    private String academicSession;
//    private String vehicleId;
//    added for student transport details
//    private BigDecimal feeAmount;
//    private Date dueMonth;
//    private BigDecimal discount;
//    private BigDecimal penalty;
//    private BigDecimal dueAmount;
    //Determines behaviour: CHANGE or CLOSE

    private List<StudentTransportDueDetails> dues = new ArrayList<>();
    private String actionType;



}
