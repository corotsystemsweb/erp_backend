package com.sms.model;

import lombok.Data;
import java.util.List;

@Data
public class MonthWiseFeeDueDetails {
    private Integer studentId;  // Added to store student ID
    private String studentName;
    private String studentClass;
    private String studentSection;
    private String academicSession;
    private Double allDue;  // Total due across all months
    private Double totalAssignedFees;  // Added for summary
    private Double totalDiscount;  // Added for summary
    private Double totalAdditionalDiscount;  // Added for summary
    private Double totalPenalty;  // Added for summary
    private Double totalNetDue;  // Added for summary
    private Double totalPaid;  // Added for summary
    private Double totalBalance;  // Added for summary
    private List<MonthlyDue> monthlyDueDetails;

    @Data
    public static class MonthlyDue {
        private String month;
        private Double assigned_fees;  // Changed from fee_amount to match query
        private Double discount;  // Changed from discount_amount to match query
        private Double additional_discount;  // New field
        private Double penalty;  // New field
        private Double net_due;
        private Double amount_paid;  // New field
        private Double balance;  // New field
    }
}