package com.sms.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubjectDetails {
    private int subjectId;


    private String subjectName;

    @JsonProperty("subject_name")
    private String subject_name;

    private int schoolId;

    @JsonProperty("theory_marks")
    private int theoryMarks;

    @JsonProperty("practical_marks")
    private int practicalMarks;

    @JsonProperty("viva_marks")
    private int vivaMarks;

    @JsonProperty("passing_marks")
    private int passingMarks;

    @JsonProperty("subject_grade")
    private String subjectGrade;

    @JsonProperty("obtained_marks")
    private int obtainedMarks;

    // Getters and Setters
    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public int getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(int passingMarks) {
        this.passingMarks = passingMarks;
    }

    public int getPracticalMarks() {
        return practicalMarks;
    }

    public void setPracticalMarks(int practicalMarks) {
        this.practicalMarks = practicalMarks;
    }

    public String getSubjectGrade() {
        return subjectGrade;
    }

    public void setSubjectGrade(String subjectGrade) {
        this.subjectGrade = subjectGrade;
    }

    public int getTheoryMarks() {
        return theoryMarks;
    }

    public void setTheoryMarks(int theoryMarks) {
        this.theoryMarks = theoryMarks;
    }

    public int getVivaMarks() {
        return vivaMarks;
    }

    public void setVivaMarks(int vivaMarks) {
        this.vivaMarks = vivaMarks;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }
}
