package com.sms.model;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private Integer recordId;
    private Integer attendanceSessionId;
    private Integer studentId;
    private String status;
    private LocalDateTime markedAt;

    public Integer getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Integer attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
}
