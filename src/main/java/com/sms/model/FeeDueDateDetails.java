package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeeDueDateDetails {
    private int fddtId;
    private int schoolId;
    private int studentId;
    private String studentName;
    private int studentClassId;
    private String className;
    private int studentSectionId;
    private String sectionName;
    private int sessionId;
    private String accademicSession;
    private int faId;
    private Date dueDate;
    private int feeAmount;
    private double discountAmount;
    private double totalAmountAfterDiscount;
    private double effectiveFeeAmount;
    private String feeType;
    private String frequencyType;
    private int dcId;
    private String dcDescription;
    private int dcRate;
    private String dcRateType;
    private int updatedBy;
    private Date updateDateTime;
    private String year;
    private double totalFee;
    private double totalDiscount;
    private double exactFee;
    private double totalPenalty;
    private double feeAfterDiscount;
    private double exactFeeAfterDiscountAndPenalty;
    private double todayCollection;
}
