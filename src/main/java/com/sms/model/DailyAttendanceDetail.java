package com.sms.model;

public class DailyAttendanceDetail {
    private String date;
    private String status;
    private String dayOfWeek;

    public DailyAttendanceDetail(String date, String status) {
        this.date = date;
        this.status = status;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getDayOfWeek() { return dayOfWeek; }

    @Override
    public String toString() {
        return String.format("{\"date\":\"%s\",\"status\":\"%s\",\"dayOfWeek\":\"%s\"}",
                date, status, dayOfWeek);
    }
}