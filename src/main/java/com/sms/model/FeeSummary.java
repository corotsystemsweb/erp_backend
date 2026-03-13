package com.sms.model;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class FeeSummary {
    private String period;       // Date (YYYY-MM-DD), Month (YYYY-MM), or Academic Session
    private String paymentMode;
    private BigDecimal totalCollected;
    private String className;
    private String sectionName;
    private double studentCount;
    private double todayCollection;
    // Getters & Setters
}