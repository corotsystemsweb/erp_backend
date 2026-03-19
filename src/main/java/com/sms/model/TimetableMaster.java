package com.sms.model;

import java.util.Date;

public class TimetableMaster {
    private int timetableMasterId;
    private int schoolId;
    private int sessionId;
    private int classId;
    private int sectionId;
    private String dayOfWeek;
    private Date timetableDate;
    private int updatedBy;

    public int getTimetableMasterId() {
        return timetableMasterId;
    }

    public void setTimetableMasterId(int timetableMasterId) {
        this.timetableMasterId = timetableMasterId;
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getTimetableDate() {
        return timetableDate;
    }

    public void setTimetableDate(Date timetableDate) {
        this.timetableDate = timetableDate;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }
}
