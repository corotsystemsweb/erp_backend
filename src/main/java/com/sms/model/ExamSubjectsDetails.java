package com.sms.model;

import java.sql.Date;
import java.sql.Time;

public class ExamSubjectsDetails {
    private Integer examSubjectId;  // Ignored in requests
    private Integer examId;

//    @NotNull(message = "Subject ID is required")
    private Integer subjectId;

//    @Min(value = 0, message = "Theory marks must be >= 0")
    private Integer theoryMaxMarks;

//    @Min(value = 0, message = "Practical marks must be >= 0")
    private Integer practicalMaxMarks;

//    @Min(value = 0, message = "Viva marks must be >= 0")
    private Integer vivaMaxMarks;

//    @NotNull(message = "Passing marks are required")
//    @Min(value = 0, message = "Passing marks must be >= 0")
    private Integer passingMarks;

//    @NotNull(message = "Exam date is required")
    private Date examDate;

//    @NotNull(message = "Start time is required")
    private Time startTime;
    private String subjectName;
    private String className;
    private String sectionName;
    private String name;
    private int examTypeId;
//    @NotNull(message = "End time is required")
    private Time endTime;

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public Integer getExamSubjectId() {
        return examSubjectId;
    }

    public void setExamSubjectId(Integer examSubjectId) {
        this.examSubjectId = examSubjectId;
    }

    public Integer getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Integer passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Integer getPracticalMaxMarks() {
        return practicalMaxMarks;
    }

    public void setPracticalMaxMarks(Integer practicalMaxMarks) {
        this.practicalMaxMarks = practicalMaxMarks;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getTheoryMaxMarks() {
        return theoryMaxMarks;
    }

    public void setTheoryMaxMarks(Integer theoryMaxMarks) {
        this.theoryMaxMarks = theoryMaxMarks;
    }

    public Integer getVivaMaxMarks() {
        return vivaMaxMarks;
    }

    public void setVivaMaxMarks(Integer vivaMaxMarks) {
        this.vivaMaxMarks = vivaMaxMarks;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getExamTypeId() {
        return examTypeId;
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
