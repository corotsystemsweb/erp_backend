package com.sms.model;


import java.sql.Date;
import java.sql.Timestamp;

public class ClassAttendanceDetails {
    private int caId;
    private int classId;

    private int sectionId;

    private int subjectId;
    private int teacherId;
    private Date presentDate;
    private Timestamp attendanceMarkTime;


    public int getCaId() {
        return caId;
    }

    public void setCaId(int caId) {
        this.caId = caId;
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

    public Date getPresentDate() {
        return presentDate;
    }

    public void setPresentDate(Date presentDate) {
        this.presentDate = presentDate;
    }

    public Timestamp getAttendanceMarkTime() {
        return attendanceMarkTime;
    }

    public void setAttendanceMarkTime(Timestamp attendanceMarkTime) {
        this.attendanceMarkTime = attendanceMarkTime;
    }
}
