package com.sms.dao.impl;

import com.sms.dao.TimetableMasterDao;
import com.sms.model.TimetableMaster;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Objects;

@Repository
public class TimetableMasterDaoImpl implements TimetableMasterDao {
    @Override
    public int createTimeTable(TimetableMaster master, String schoolCode) {

        String sql = "INSERT INTO timetable_master " +
                "(school_id, session_id, class_id, section_id, day_of_week, timetable_date, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING timetable_master_id";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{
                    master.getSchoolId(),
                    master.getSessionId(),
                    master.getClassId(),
                    master.getSectionId(),
                    master.getDayOfWeek().toUpperCase(),
                    master.getTimetableDate() != null
                            ? new java.sql.Date(master.getTimetableDate().getTime())
                            : null,
                    master.getUpdatedBy()
            }, Integer.class);

        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
