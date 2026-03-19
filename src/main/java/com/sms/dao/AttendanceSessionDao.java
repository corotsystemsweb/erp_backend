package com.sms.dao;

import com.sms.model.AttendanceSession;

public interface AttendanceSessionDao {
    int createSession(AttendanceSession session, String schoolCode);
}