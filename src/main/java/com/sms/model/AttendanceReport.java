package com.sms.model;

import lombok.Data;

import java.sql.Date;

@Data
public class AttendanceReport {
    private int studentId;
    private String studentName;
    private String className;
    private String sectionName;
    private String academicSession;
    private Long totalDays;
    private Long absentDays;
    private Long presentDays;
    private Double attendancePercentage;
    private Long attendanceSessionId;
    private Long sessionId;
    private Long classId;
    private Long sectionId;
    private Long subjectId;
    private Long teacherId;
    private Date sessionDate;
    private String sessionType;
    private Date markedAt;

    public AttendanceReport() {
    }

    public AttendanceReport(int studentId, String studentName, String className, String sectionName, String academicSession, Long totalDays, Long absentDays, Long presentDays, Double attendancePercentage) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.sectionName = sectionName;
        this.academicSession = academicSession;
        this.totalDays = totalDays;
        this.absentDays = absentDays;
        this.presentDays = presentDays;
        this.attendancePercentage = attendancePercentage;

    }
}
