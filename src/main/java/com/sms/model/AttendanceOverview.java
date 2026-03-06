package com.sms.model;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class AttendanceOverview {
    private List<AttendanceReport> tableData;
    private int totalStudents;
    private int presentToday;
    private int absentToday;
    private int lateStudents;
    private List<TopAttendant> topAttendants;
    private Map<String, Integer> weeklyAbsent;

    @Data
    public static class TopAttendant {
        private String studentName;
        private Double attendancePercentage;
        private Long presentDays;

        public TopAttendant(String studentName, Double attendancePercentage, Long presentDays) {
            this.studentName = studentName;
            this.attendancePercentage = attendancePercentage;
            this.presentDays = presentDays;
        }
    }
}
