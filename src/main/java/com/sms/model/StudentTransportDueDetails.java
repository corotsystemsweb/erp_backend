package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentTransportDueDetails {

    private int tfdueId;

    // Default to BigDecimal.ZERO to avoid null issues
    private BigDecimal feeAmount = BigDecimal.ZERO;
    private Date dueMonth;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal penalty = BigDecimal.ZERO;
    private BigDecimal dueAmount = BigDecimal.ZERO;
    private BigDecimal paidAmount = BigDecimal.ZERO;

}
