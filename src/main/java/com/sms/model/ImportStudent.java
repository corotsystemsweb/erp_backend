package com.sms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class ImportStudent {
    @Setter
    @Getter
    private Integer studentId;
    @Getter
    @Setter
    private String studentName;
    @Setter
    @Getter
    private Integer classId;
    @Setter
    @Getter
    private String className;
    @Setter
    @Getter
    private Integer sectionId;
    @Setter
    @Getter
    private String sectionName;
    @Setter
    @Getter
    private Integer sessionId;
    @Setter
    @Getter
    private String academicSession;
    @Setter
    @Getter
    private String examStatus;
    @Setter
    @Getter
    private double attendancePercentage;
    @Getter
    @Setter
    private String eligibiltyStatus;
    @Setter
    @Getter
    private List<Integer> studentIds;
    @Setter
    @Getter
    private Integer nextSessionId;
    @Setter
    @Getter
    private Integer nextClassId;
    @Setter
    @Getter
    private Integer nextSectionId;
    @Setter
    @Getter
    private double dueAmount;
    private boolean isEligibleForGraduation;
    private  boolean isFeeCleared;

    public boolean isEligibleForGraduation() {
        return isEligibleForGraduation;
    }

    public void setEligibleForGraduation(boolean eligibleForGraduation) {
        isEligibleForGraduation = eligibleForGraduation;
    }

    public boolean isFeeCleared() {
        return isFeeCleared;
    }

    public void setFeeCleared(boolean feeCleared) {
        isFeeCleared = feeCleared;
    }

}
