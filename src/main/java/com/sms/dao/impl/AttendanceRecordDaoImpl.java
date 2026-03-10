package com.sms.dao.impl;

import com.sms.dao.AttendanceRecordDao;
import com.sms.model.AttendanceRecord;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AttendanceRecordDaoImpl implements AttendanceRecordDao {

    @Override
    public void saveAbsentStudents(List<AttendanceRecord> records, String schoolCode) {
        if (records.isEmpty()) return;

        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            jdbcTemplate.batchUpdate(
                    "INSERT INTO attendance_record (attendance_session_id, student_id) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            AttendanceRecord record = records.get(i);
                            ps.setInt(1, record.getAttendanceSessionId());
                            ps.setInt(2, record.getStudentId());
                        }

                        @Override
                        public int getBatchSize() {
                            return records.size();
                        }
                    }
            );
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to save records: " + e.getMessage(), e) {};
        } finally {
            if (jdbcTemplate != null) {
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
        }
    }
}