package com.sms.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DemandSlip {
    private String name;
    private String className;
    private String sectionName;
    private BigDecimal totalPending;
    private LocalDate dueDate;
    private String feeType;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal discountAmount;
}
