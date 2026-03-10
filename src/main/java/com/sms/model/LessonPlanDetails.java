package com.sms.model;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class LessonPlanDetails {
    private Integer lessonPlanId;
    private Integer schoolId;
    private Integer sessionId;
    private Integer cstaId;
    private String planTitle;
    private String planDescription;
    private Integer createdBy;
    private Timestamp createdAt;
    private Integer updatedBy;
    private Timestamp updatedAt;
    private String status;
}
