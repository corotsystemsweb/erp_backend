package com.sms.model;

import java.sql.Time;
import java.util.Date;

public class TimetableDetails {
    private int timetableId;
    private int schoolId;
    private int sessionId;
    private int classId;
    private int sectionId;
    private int subjectId;
    private int teacherId;
    private String dayOfWeek;
    private int periodNumber;
    private Time startTime;
    private Time endTime;
    private String roomNumber;
    private String academicSession;
    private String className;
    private String sectionName;
    private String subjectName;
    private String teacherName;
    private int staffId;
    private String classTeacher;
    private int totalStudents;
    private int totalPeriods;
    private int totalPeriodsPerDay;
    private int timeTableMasterId;
    private String periodName;
    private boolean isBreak;
    private int updatedBy;
    private Date timeTableDate;
    private Date weekStart;
    private Date weekEnd;
    private String periodTeacherName;
    private int weeklyTotalPeriods;

    public TimetableDetails(int timetableId, int schoolId, int sessionId, int classId, int sectionId, int subjectId, int teacherId, String dayOfWeek, int periodNumber, Time startTime, Time endTime, String roomNumber, String academicSession, String className, String sectionName, String subjectName, String teacherName, int staffId, String classTeacher, int totalStudents, int totalPeriods, int totalPeriodsPerDay, int timeTableMasterId, String periodName, boolean isBreak, int updatedBy, Date timeTableDate, Date weekStart, Date weekEnd, String periodTeacherName, int weeklyTotalPeriods) {
        this.timetableId = timetableId;
        this.schoolId = schoolId;
        this.sessionId = sessionId;
        this.classId = classId;
        this.sectionId = sectionId;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.dayOfWeek = dayOfWeek;
        this.periodNumber = periodNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.academicSession = academicSession;
        this.className = className;
        this.sectionName = sectionName;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.staffId = staffId;
        this.classTeacher = classTeacher;
        this.totalStudents = totalStudents;
        this.totalPeriods = totalPeriods;
        this.totalPeriodsPerDay = totalPeriodsPerDay;
        this.timeTableMasterId = timeTableMasterId;
        this.periodName = periodName;
        this.isBreak = isBreak;
        this.updatedBy = updatedBy;
        this.timeTableDate = timeTableDate;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.periodTeacherName = periodTeacherName;
        this.weeklyTotalPeriods = weeklyTotalPeriods;
    }

    public TimetableDetails() {

    }

    // Getters and Setters

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
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

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(int periodNumber) {
        this.periodNumber = periodNumber;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getAcademicSession() {
        return academicSession;
    }

    public void setAcademicSession(String academicSession) {
        this.academicSession = academicSession;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalPeriods() {
        return totalPeriods;
    }

    public void setTotalPeriods(int totalPeriods) {
        this.totalPeriods = totalPeriods;
    }

    public int getTotalPeriodsPerDay() {
        return totalPeriodsPerDay;
    }

    public void setTotalPeriodsPerDay(int totalPeriodsPerDay) {
        this.totalPeriodsPerDay = totalPeriodsPerDay;
    }

    public int getTimeTableMasterId() {
        return timeTableMasterId;
    }

    public void setTimeTableMasterId(int timeTableMasterId) {
        this.timeTableMasterId = timeTableMasterId;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getTimeTableDate() {
        return timeTableDate;
    }

    public void setTimeTableDate(Date timeTableDate) {
        this.timeTableDate = timeTableDate;
    }

    public Date getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(Date weekStart) {
        this.weekStart = weekStart;
    }

    public Date getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(Date weekEnd) {
        this.weekEnd = weekEnd;
    }

    public String getPeriodTeacherName() {
        return periodTeacherName;
    }

    public void setPeriodTeacherName(String periodTeacherName) {
        this.periodTeacherName = periodTeacherName;
    }

    public int getWeeklyTotalPeriods() {
        return weeklyTotalPeriods;
    }

    public void setWeeklyTotalPeriods(int weeklyTotalPeriods) {
        this.weeklyTotalPeriods = weeklyTotalPeriods;
    }


    // toString

    @Override
    public String toString() {
        return "TimetableDetails{" +
                "timetableId=" + timetableId +
                ", schoolId=" + schoolId +
                ", sessionId=" + sessionId +
                ", classId=" + classId +
                ", sectionId=" + sectionId +
                ", subjectId=" + subjectId +
                ", teacherId=" + teacherId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", periodNumber=" + periodNumber +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", roomNumber='" + roomNumber + '\'' +
                ", academicSession='" + academicSession + '\'' +
                ", className='" + className + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", staffId=" + staffId +
                ", classTeacher='" + classTeacher + '\'' +
                ", totalStudents=" + totalStudents +
                ", totalPeriods=" + totalPeriods +
                ", totalPeriodsPerDay=" + totalPeriodsPerDay +
                ", timeTableMasterId=" + timeTableMasterId +
                ", periodName='" + periodName + '\'' +
                ", isBreak=" + isBreak +
                ", updatedBy=" + updatedBy +
                ", timeTableDate=" + timeTableDate +
                ", weekStart=" + weekStart +
                ", weekEnd=" + weekEnd +
                ", periodTeacherName='" + periodTeacherName + '\'' +
                ", weeklyTotalPeriods=" + weeklyTotalPeriods +
                '}';
    }
}
