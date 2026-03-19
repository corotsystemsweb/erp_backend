package com.sms.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PendingFee {
    private Long studentId;
    private String studentName;
    private String className;
    private String sectionName;
    private String academicSession; // Added field
    private BigDecimal pendingAmount;
    private List<DemandSlip> feeDetails;
    private SchoolDetails schoolDetails;

    public PendingFee(String academicSession, String className, List<DemandSlip> feeDetails, BigDecimal pendingAmount, String sectionName, Long studentId, String studentName) {
        this.academicSession = academicSession;
        this.className = className;
        this.feeDetails = feeDetails;
        this.pendingAmount = pendingAmount;
        this.sectionName = sectionName;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public PendingFee() {
        this.feeDetails = new ArrayList<>();
    }


}
