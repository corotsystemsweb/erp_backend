//package com.sms.model;
//
//public class ExamTypeManager {
//    private int examTypeId;
//    private String name;
//    private int sessionId;
//    private String description;
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public int getExamTypeId() {
//        return examTypeId;
//    }
//
//    public void setExamTypeId(int examTypeId) {
//        this.examTypeId = examTypeId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getSessionId() {
//        return sessionId;
//    }
//
//    public void setSessionId(int sessionId) {
//        this.sessionId = sessionId;
//    }
//}

// ExamTypeManager.java

package com.sms.model;

import com.fasterxml.jackson.databind.JsonNode;

public class ExamTypeManager {

    private int examTypeId;
    private int schoolId;
    private int sessionId;
    private String name;
    private String description;
    private String category;
    private Double weightagePercent;
    private Double passingMarksPercent;
    private String gradingSystem;
    private JsonNode examOptions;
    private String status;

    public int getExamTypeId() {
        return examTypeId;
    }
    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public int getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getSessionId() {
        return sessionId;
    }
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public Double getWeightagePercent() {
        return weightagePercent;
    }
    public void setWeightagePercent(Double weightagePercent) {
        this.weightagePercent = weightagePercent;
    }

    public Double getPassingMarksPercent() {
        return passingMarksPercent;
    }
    public void setPassingMarksPercent(Double passingMarksPercent) {
        this.passingMarksPercent = passingMarksPercent;
    }

    public String getGradingSystem() {
        return gradingSystem;
    }
    public void setGradingSystem(String gradingSystem) {
        this.gradingSystem = gradingSystem;
    }

    public JsonNode getExamOptions() {
        return examOptions;
    }
    public void setExamOptions(JsonNode examOptions) {
        this.examOptions = examOptions;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
