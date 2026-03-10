package com.sms.service.impl;

import com.sms.dao.AttendanceRecordDao;
import com.sms.dao.AttendanceSessionDao;
import com.sms.model.AttendanceRecord;
import com.sms.model.AttendanceSession;
import com.sms.service.AttendanceService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceSessionDao attendanceSessionDao;
    private final AttendanceRecordDao attendanceRecordDao;

    public AttendanceServiceImpl(
            AttendanceSessionDao attendanceSessionDao,
            AttendanceRecordDao attendanceRecordDao) {
        this.attendanceSessionDao = attendanceSessionDao;
        this.attendanceRecordDao = attendanceRecordDao;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void markAttendance(AttendanceSession session, List<Integer> absentStudentIds, String schoolCode) {
        validateSession(session);

        int sessionId = attendanceSessionDao.createSession(session, schoolCode);
        List<AttendanceRecord> records = createAttendanceRecords(sessionId, absentStudentIds);
        attendanceRecordDao.saveAbsentStudents(records, schoolCode);
    }

    private void validateSession(AttendanceSession session) {
        if ("SUBJECT".equalsIgnoreCase(session.getSessionType())){
            if (session.getSubjectId() == null) {
                throw new IllegalArgumentException("Subject ID required for subject attendance");
            }
        }
        if (session.getSessionDate() == null) {
            throw new IllegalArgumentException("Attendance date is required");
        }
    }

    private List<AttendanceRecord> createAttendanceRecords(int sessionId, List<Integer> studentIds) {
        return studentIds.stream()
                .map(studentId -> {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setAttendanceSessionId(sessionId);
                    record.setStudentId(studentId);
                    return record;
                })
                .collect(Collectors.toList());
    }
}