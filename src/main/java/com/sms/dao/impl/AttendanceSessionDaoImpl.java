package com.sms.dao.impl;

import com.sms.dao.AttendanceSessionDao;
import com.sms.model.AttendanceSession;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class AttendanceSessionDaoImpl implements AttendanceSessionDao {

    @Override
    public int createSession(AttendanceSession session, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO attendance_session (" +
                                "session_id, class_id, section_id, subject_id, " +
                                "teacher_id, session_date, session_type" +
                                ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new String[] {"attendance_session_id"}
                );
                ps.setInt(1, session.getSessionId());
                ps.setInt(2, session.getClassId());
                ps.setInt(3, session.getSectionId());
                ps.setObject(4, session.getSubjectId());
                ps.setInt(5, session.getTeacherId());
                ps.setDate(6, java.sql.Date.valueOf(session.getSessionDate()));
                ps.setString(7, session.getSessionType());
                return ps;
            }, keyHolder);

            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to create session: " + e.getMessage(), e) {};
        } finally {
            if (jdbcTemplate != null) {
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
        }
    }
}