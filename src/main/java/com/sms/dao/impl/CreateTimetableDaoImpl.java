package com.sms.dao.impl;

import com.sms.dao.CreateTimetableDao;
import com.sms.model.SubjectDetails;
import com.sms.model.TimetableDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Map;


@Repository
public class CreateTimetableDaoImpl implements CreateTimetableDao {
    @Override
    public TimetableDetails createTimetable(TimetableDetails timetable,String schoolCode) throws SQLException {
        String sql = "INSERT INTO timetable (school_id, session_id, class_id, section_id, subject_id, teacher_id, day_of_week, period_number, start_time, end_time, room_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, timetable.getSchoolId());
                ps.setInt(2, timetable.getSessionId());
                ps.setInt(3, timetable.getClassId());
                ps.setInt(4, timetable.getSectionId());
                ps.setInt(5, timetable.getSubjectId());
                ps.setInt(6, timetable.getTeacherId());
                ps.setString(7, timetable.getDayOfWeek());
                ps.setInt(8, timetable.getPeriodNumber());
                ps.setTime(9, timetable.getStartTime());
                ps.setTime(10, timetable.getEndTime());
                ps.setString(11, timetable.getRoomNumber());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("timetable_id")){
                int generatedId=((Number) keys.get("timetable_id")).intValue();
                timetable.setTimetableId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return timetable;
    }
    @Override
    public List<TimetableDetails> createTimetableBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException {
        String sql = "INSERT INTO timetable (school_id, session_id, class_id, section_id, subject_id, teacher_id, day_of_week, period_number, start_time, end_time, room_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            for (TimetableDetails timetable : timetables) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, timetable.getSchoolId());
                    ps.setInt(2, timetable.getSessionId());
                    ps.setInt(3, timetable.getClassId());
                    ps.setInt(4, timetable.getSectionId());
                    ps.setInt(5, timetable.getSubjectId());
                    ps.setInt(6, timetable.getTeacherId());
                    ps.setString(7, timetable.getDayOfWeek());
                    ps.setInt(8, timetable.getPeriodNumber());
                    ps.setTime(9, timetable.getStartTime());
                    ps.setTime(10, timetable.getEndTime());
                    ps.setString(11, timetable.getRoomNumber());
                    return ps;
                }, keyHolder);
                Map<String, Object> keys = keyHolder.getKeys();
                if (keys != null && keys.containsKey("timetable_id")) {
                    int generatedId = ((Number) keys.get("timetable_id")).intValue();
                    timetable.setTimetableId(generatedId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return timetables;
    }

    @Override
    public List<TimetableDetails> getAllTimeTableBasedClassSection(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    t.timetable_id,
                    t.school_id,
                    t.session_id,
                    s.academic_session, -- Added from session table
                    t.class_id,
                    c.class_name, -- Assuming mst_class table exists
                    t.section_id,
                    sec.section_name, -- Assuming mst_section table exists
                    t.subject_id,
                    sub.subject_name, -- Already included from mst_subject
                    t.teacher_id,
                    CONCAT(st.first_name, ' ', st.last_name) AS teacher_name,
                    t.day_of_week,
                    t.period_number,
                    t.start_time,
                    t.end_time,
                    t.room_number
                FROM
                    timetable t
                JOIN
                    session s ON t.session_id = s.session_id -- Join with session table
                JOIN
                    mst_subject sub ON t.subject_id = sub.subject_id -- Join with subject table
                JOIN
                    staff st ON t.teacher_id = st.staff_id -- Join with staff table
                JOIN
                    mst_class c ON t.class_id = c.class_id -- Join with class table
                JOIN
                    mst_section sec ON t.section_id = sec.section_id -- Join with section table
                WHERE
                    t.school_id = 1 -- Replace with the desired school_id
                    AND t.session_id = ? -- Replace with the desired session_id
                    AND t.class_id = ? -- Replace with the desired class_id
                    AND t.section_id = ? -- Replace with the desired section_id
                ORDER BY
                    t.day_of_week,
                    t.period_number;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<TimetableDetails> timetableDetails = null;
        try{
            timetableDetails = jdbcTemplate.query(sql, new Object[]{sessionId,classId,sectionId}, new RowMapper<TimetableDetails>() {
                @Override
                public TimetableDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimetableDetails td = new TimetableDetails();
                    td.setTimetableId(rs.getInt("timetable_id"));
                    td.setSchoolId(rs.getInt("school_id"));
                    td.setSessionId(rs.getInt("session_id"));
                    td.setAcademicSession(rs.getString("academic_session"));
                    td.setClassId(rs.getInt("class_id"));
                    td.setClassName(rs.getString("class_name"));
                    td.setSectionId(rs.getInt("section_id"));
                    td.setSectionName(rs.getString("section_name"));
                    td.setSubjectId(rs.getInt("subject_id"));
                    td.setSubjectName(rs.getString("subject_name"));
                    td.setTeacherId(rs.getInt("teacher_id"));
                    td.setTeacherName(rs.getString("teacher_name"));
                    td.setDayOfWeek(rs.getString("day_of_week"));
                    td.setPeriodNumber(rs.getInt("period_number"));
                    td.setStartTime(Time.valueOf(rs.getTime("start_time").toLocalTime()));
                    td.setEndTime(Time.valueOf(rs.getTime("end_time").toLocalTime()));
                    td.setRoomNumber(rs.getString("room_number"));
                    return td;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return timetableDetails;
    }

    @Override
    public int[] updateTimetableBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException {
            String sql = "UPDATE timetable SET school_id=?, session_id=?, class_id=?, section_id=?, subject_id=?, teacher_id=?, day_of_week=?, period_number=?, start_time=?, end_time=?, room_number=? WHERE timetable_id=?";
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            try {
                return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        TimetableDetails t = timetables.get(i);
                        ps.setInt(1, t.getSchoolId());
                        ps.setInt(2, t.getSessionId());
                        ps.setInt(3, t.getClassId());
                        ps.setInt(4, t.getSectionId());
                        ps.setInt(5, t.getSubjectId());
                        ps.setInt(6, t.getTeacherId());
                        ps.setString(7, t.getDayOfWeek());
                        ps.setInt(8, t.getPeriodNumber());
                        ps.setTime(9, t.getStartTime());
                        ps.setTime(10, t.getEndTime());
                        ps.setString(11, t.getRoomNumber());
                        ps.setInt(12, t.getTimetableId());
                    }
                    @Override
                    public int getBatchSize() {
                        return timetables.size();
                    }
                });
            } finally {
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
    }

    @Override
    public int[] deleteTimetableBulk(List<Integer> timetableIds, String schoolCode) throws SQLException {
        String sql = "DELETE FROM timetable WHERE timetable_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, timetableIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return timetableIds.size();
                }
            });
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


}
