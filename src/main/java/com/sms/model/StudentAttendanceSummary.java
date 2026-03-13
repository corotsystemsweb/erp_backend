package com.sms.model;

import java.util.List;

public class StudentAttendanceSummary {
    private Integer studentId;
    private String firstName;
    private String lastName;
    private String className;
    private String sectionName;
    private String dailyDetails; // JSON string
    private String subjectDetails; // JSON string
    private Integer totalAbsentDays;
    private Integer totalPresentDays;
    private Double attendancePercentage;
    private Double totalDays;
    private Integer year;
    private Integer month;
    private Integer period;
    private List<DailyAttendanceDetail> dateWiseAttendance;

    public String getFormattedDailyDetails() {
        if (dateWiseAttendance == null || dateWiseAttendance.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (DailyAttendanceDetail detail : dateWiseAttendance) {
            sb.append(detail.toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // Remove last comma
        sb.append("]");

        return sb.toString();
    }
    public List<DailyAttendanceDetail> getDateWiseAttendance() {
        return dateWiseAttendance;
    }

    public void setDateWiseAttendance(List<DailyAttendanceDetail> dateWiseAttendance) {
        this.dateWiseAttendance = dateWiseAttendance;
    }

    // Getters and Setters
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    public String getDailyDetails() { return dailyDetails; }
    public void setDailyDetails(String dailyDetails) { this.dailyDetails = dailyDetails; }
    public String getSubjectDetails() { return subjectDetails; }
    public void setSubjectDetails(String subjectDetails) { this.subjectDetails = subjectDetails; }
    public Integer getTotalAbsentDays() { return totalAbsentDays; }
    public void setTotalAbsentDays(Integer totalAbsentDays) { this.totalAbsentDays = totalAbsentDays; }
    public Integer getTotalPresentDays() { return totalPresentDays; }
    public void setTotalPresentDays(Integer totalPresentDays) { this.totalPresentDays = totalPresentDays; }
    public Double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Double totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
}