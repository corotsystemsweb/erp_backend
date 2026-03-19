package com.sms.model;

import lombok.Data;

@Data
public class StudentFeeStatus {
    private int studentId;
    private String studentName;
    private String gender;
    private String studentType;
    private boolean excluded;
}

