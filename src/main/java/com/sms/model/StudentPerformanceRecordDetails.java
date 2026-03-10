package com.sms.model;

public class StudentPerformanceRecordDetails {
    private int emeId;
    private int studentId;
    private String studentName;
    private int subjectId;
    private String subjectName;
    private double maximumMarks;
    private double minimumMarks;
    private double obtain;
    private String status;
    private double totalMaximumMarks;
    private double totalObtainMarks;
    private double percentage;

    public int getEmeId() {
        return emeId;
    }

    public void setEmeId(int emeId) {
        this.emeId = emeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

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

    public double getMaximumMarks() {
        return maximumMarks;
    }

    public void setMaximumMarks(double maximumMarks) {
        this.maximumMarks = maximumMarks;
    }

    public double getMinimumMarks() {
        return minimumMarks;
    }

    public void setMinimumMarks(double minimumMarks) {
        this.minimumMarks = minimumMarks;
    }

    public double getObtain() {
        return obtain;
    }

    public void setObtain(double obtain) {
        this.obtain = obtain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalMaximumMarks() {
        return totalMaximumMarks;
    }

    public void setTotalMaximumMarks(double totalMaximumMarks) {
        this.totalMaximumMarks = totalMaximumMarks;
    }

    public double getTotalObtainMarks() {
        return totalObtainMarks;
    }

    public void setTotalObtainMarks(double totalObtainMarks) {
        this.totalObtainMarks = totalObtainMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
