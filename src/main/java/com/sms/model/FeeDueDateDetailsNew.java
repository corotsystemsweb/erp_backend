package com.sms.model;

import lombok.Data;

@Data
public class FeeDueDateDetailsNew {
    private String dueDate;
    private int feeAmount;
    private int discountAmount;
    private Long fddtId;
    private boolean deposited;
}
