package com.sms.model;

import lombok.Data;

import java.sql.Date;
@Data
public class LessonPlanTitleDetails {
    private Integer detailId;
    private Integer lessonPlanId;
    private Integer schoolId;
    private Integer sessionId;
    private String topicName;
    private String subTopicName;
    private Date lessonDate;
    private String resources;
    private String teachingMethod;
}
