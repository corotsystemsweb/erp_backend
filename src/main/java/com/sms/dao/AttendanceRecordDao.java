package com.sms.dao;

import com.sms.model.AttendanceRecord;
import java.util.List;

public interface AttendanceRecordDao {
    void saveAbsentStudents(List<AttendanceRecord> records, String schoolCode);
}