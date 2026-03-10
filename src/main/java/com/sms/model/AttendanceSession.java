package com.sms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceSession {
    private Integer attendanceSessionId;
    private Integer sessionId;  // From mst_session
    private Integer classId;
    private Integer sectionId;
    private Integer subjectId;
    private Integer teacherId;
    private LocalDate sessionDate;
    private String sessionType;
    private LocalDateTime createdAt;

    public Integer getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Integer attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
}
