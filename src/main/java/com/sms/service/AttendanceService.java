package com.sms.service;

import com.sms.model.AttendanceSession;
import java.util.List;

public interface AttendanceService {
    void markAttendance(AttendanceSession session, List<Integer> absentStudentIds, String schoolCode);
}