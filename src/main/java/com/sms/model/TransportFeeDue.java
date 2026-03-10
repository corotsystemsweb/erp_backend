package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransportFeeDue {
    private Integer tfDueId;
    private int schoolId;
    private int studentTransportId;
    private Date dueMonth;
    private BigDecimal feeAmount;
    private BigDecimal discountAmount;
    private BigDecimal penaltyAmount;
    private int studentId;
}
