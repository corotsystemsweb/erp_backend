package com.sms.model;

import java.sql.Time;

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

    // Constructor
    public TimetableDetails(int timetableId, int schoolId, int sessionId, int classId, int sectionId, int subjectId, int teacherId, String dayOfWeek, int periodNumber, Time startTime, Time endTime, String roomNumber) {
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
    }

    public TimetableDetails() {

    }

    // Getters and Setters


    public String getAcademicSession() {
        return academicSession;
    }

    public void setAcademicSession(String academicSession) {
        this.academicSession = academicSession;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(int periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
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

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

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
                '}';
    }
}
