package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectDetails {
    private int subjectId;
    private String subjectName;
    private String subjectCode;
    private String subjectCategory;
    private Double weeklyHours;
    private String subjectDescription;
    private boolean hasPracticalExam;
    private boolean electiveCourse;
    private boolean allowLastEnrollment;
    private boolean autoGrading;
    private Map<String, Integer> gradeWeightage;
    private String syllabus;
    private String syllabusFileName;
    private int facultyCount;
    private int classCount;

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

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectCategory() {
        return subjectCategory;
    }

    public void setSubjectCategory(String subjectCategory) {
        this.subjectCategory = subjectCategory;
    }

    public Double getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(Double weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public boolean isHasPracticalExam() {
        return hasPracticalExam;
    }

    public void setHasPracticalExam(boolean hasPracticalExam) {
        this.hasPracticalExam = hasPracticalExam;
    }

    public boolean isElectiveCourse() {
        return electiveCourse;
    }

    public void setElectiveCourse(boolean electiveCourse) {
        this.electiveCourse = electiveCourse;
    }

    public boolean isAllowLastEnrollment() {
        return allowLastEnrollment;
    }

    public void setAllowLastEnrollment(boolean allowLastEnrollment) {
        this.allowLastEnrollment = allowLastEnrollment;
    }

    public boolean isAutoGrading() {
        return autoGrading;
    }

    public void setAutoGrading(boolean autoGrading) {
        this.autoGrading = autoGrading;
    }

    public Map<String, Integer> getGradeWeightage() {
        return gradeWeightage;
    }

    public void setGradeWeightage(Map<String, Integer> gradeWeightage) {
        this.gradeWeightage = gradeWeightage;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getSyllabusFileName() {
        return syllabusFileName;
    }

    public void setSyllabusFileName(String syllabusFileName) {
        this.syllabusFileName = syllabusFileName;
    }

    public int getFacultyCount() {
        return facultyCount;
    }

    public void setFacultyCount(int facultyCount) {
        this.facultyCount = facultyCount;
    }

    public int getClassCount() {
        return classCount;
    }

    public void setClassCount(int classCount) {
        this.classCount = classCount;
    }
}
