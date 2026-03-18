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

import java.sql.*;
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
    public List<TimetableDetails> createTimetableBulkWithMaster(List<TimetableDetails> timeTables, int masterId, String schoolCode) throws Exception {
        String sql = "INSERT INTO timetable (timetable_master_id, period_number, period_name, start_time, end_time, subject_id, teacher_id, room_number, is_break, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.batchUpdate(sql, timeTables, timeTables.size(), (ps,t)->{
                ps.setInt(1, masterId);
                ps.setInt(2, t.getPeriodNumber());
                ps.setString(3, t.getPeriodName());
                ps.setTime(4, t.getStartTime());
                ps.setTime(5, t.getEndTime());
                ps.setInt(6, t.getSubjectId());
                ps.setInt(7, t.getTeacherId());
                ps.setString(8, t.getRoomNumber());
                ps.setBoolean(9, t.isBreak());
                ps.setInt(10, t.getUpdatedBy());
            });
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return timeTables;
    }


//    @Override
//    public List<TimetableDetails> getAllTimeTableBasedClassSection(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
//        String sql = """
//                SELECT
//                    t.timetable_id,
//                    t.school_id,
//                    t.session_id,
//                    s.academic_session, -- Added from session table
//                    t.class_id,
//                    c.class_name, -- Assuming mst_class table exists
//                    t.section_id,
//                    sec.section_name, -- Assuming mst_section table exists
//                    t.subject_id,
//                    sub.subject_name, -- Already included from mst_subject
//                    t.teacher_id,
//                    CONCAT(st.first_name, ' ', st.last_name) AS teacher_name,
//                    t.day_of_week,
//                    t.period_number,
//                    t.start_time,
//                    t.end_time,
//                    t.room_number
//                FROM
//                    timetable t
//                JOIN
//                    session s ON t.session_id = s.session_id -- Join with session table
//                JOIN
//                    mst_subject sub ON t.subject_id = sub.subject_id -- Join with subject table
//                JOIN
//                    staff st ON t.teacher_id = st.staff_id -- Join with staff table
//                JOIN
//                    mst_class c ON t.class_id = c.class_id -- Join with class table
//                JOIN
//                    mst_section sec ON t.section_id = sec.section_id -- Join with section table
//                WHERE
//                    t.school_id = 1 -- Replace with the desired school_id
//                    AND t.session_id = ? -- Replace with the desired session_id
//                    AND t.class_id = ? -- Replace with the desired class_id
//                    AND t.section_id = ? -- Replace with the desired section_id
//                ORDER BY
//                    t.day_of_week,
//                    t.period_number;
//                """;
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        List<TimetableDetails> timetableDetails = null;
//        try{
//            timetableDetails = jdbcTemplate.query(sql, new Object[]{sessionId,classId,sectionId}, new RowMapper<TimetableDetails>() {
//                @Override
//                public TimetableDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
//                    TimetableDetails td = new TimetableDetails();
//                    td.setTimetableId(rs.getInt("timetable_id"));
//                    td.setSchoolId(rs.getInt("school_id"));
//                    td.setSessionId(rs.getInt("session_id"));
//                    td.setAcademicSession(rs.getString("academic_session"));
//                    td.setClassId(rs.getInt("class_id"));
//                    td.setClassName(rs.getString("class_name"));
//                    td.setSectionId(rs.getInt("section_id"));
//                    td.setSectionName(rs.getString("section_name"));
//                    td.setSubjectId(rs.getInt("subject_id"));
//                    td.setSubjectName(rs.getString("subject_name"));
//                    td.setTeacherId(rs.getInt("teacher_id"));
//                    td.setTeacherName(rs.getString("teacher_name"));
//                    td.setDayOfWeek(rs.getString("day_of_week"));
//                    td.setPeriodNumber(rs.getInt("period_number"));
//                    td.setStartTime(Time.valueOf(rs.getTime("start_time").toLocalTime()));
//                    td.setEndTime(Time.valueOf(rs.getTime("end_time").toLocalTime()));
//                    td.setRoomNumber(rs.getString("room_number"));
//                    return td;
//                }
//            });
//        }catch (Exception e){
//            throw new Exception("Error fetching subject details", e);
//        }finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//        return timetableDetails;
//    }

    @Override
    public List<TimetableDetails> getAllTimeTableBasedClassSection(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    t.timetable_id,
                    tm.school_id,
                    tm.session_id,
                    s.academic_session,
                    tm.class_id,
                    c.class_name,
                    tm.section_id,
                    sec.section_name,
                    t.subject_id,
                    sub.subject_name,
                    t.teacher_id,
                    CONCAT(st.first_name, ' ', st.last_name) AS teacher_name,
                    tm.day_of_week,
                    tm.timetable_date,
                    t.period_number,
                    t.period_name,
                    t.start_time,
                    t.end_time,
                    t.room_number,
                    t.is_break
                FROM
                    timetable t
                JOIN
                    timetable_master tm ON t.timetable_master_id = tm.timetable_master_id
                JOIN
                    session s ON tm.session_id = s.session_id
                JOIN
                    mst_subject sub ON t.subject_id = sub.subject_id
                JOIN
                    staff st ON t.teacher_id = st.staff_id
                JOIN
                    mst_class c ON tm.class_id = c.class_id
                JOIN
                    mst_section sec ON tm.section_id = sec.section_id
                WHERE
                    tm.school_id = 1
                    AND tm.session_id = ?
                    AND tm.class_id = ?
                    AND tm.section_id = ?
                    AND tm.is_deleted = FALSE
                    AND t.is_deleted = FALSE
                    AND tm.timetable_date BETWEEN (
                        SELECT DATE_TRUNC('week', MAX(timetable_date))
                        FROM timetable_master
                        WHERE school_id = tm.school_id
                          AND session_id = tm.session_id
                          AND class_id = tm.class_id
                          AND section_id = tm.section_id
                          AND is_deleted = FALSE
                    )
                    AND (
                        SELECT DATE_TRUNC('week', MAX(timetable_date)) + INTERVAL '5 days'
                        FROM timetable_master
                        WHERE school_id = tm.school_id
                          AND session_id = tm.session_id
                          AND class_id = tm.class_id
                          AND section_id = tm.section_id
                          AND is_deleted = FALSE
                    )
                ORDER BY
                    CASE tm.day_of_week
                        WHEN 'MONDAY'    THEN 1
                        WHEN 'TUESDAY'   THEN 2
                        WHEN 'WEDNESDAY' THEN 3
                        WHEN 'THURSDAY'  THEN 4
                        WHEN 'FRIDAY'    THEN 5
                        WHEN 'SATURDAY'  THEN 6
                    END,
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
                    td.setTimeTableDate(rs.getDate("timetable_date"));
                    td.setPeriodNumber(rs.getInt("period_number"));
                    td.setPeriodName(rs.getString("period_name"));
                    td.setStartTime(Time.valueOf(rs.getTime("start_time").toLocalTime()));
                    td.setEndTime(Time.valueOf(rs.getTime("end_time").toLocalTime()));
                    td.setRoomNumber(rs.getString("room_number"));
                    td.setBreak(rs.getBoolean("is_break"));
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

    @Override
    public List<TimetableDetails> getAllTimeTableBasedOnStaffId(int sessionId, Integer staffId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    t.timetable_id,
                    tm.school_id,
                    tm.session_id,
                    s.academic_session,
                    tm.class_id,
                    c.class_name,
                    tm.section_id,
                    sec.section_name,
                    t.subject_id,
                    sub.subject_name,
                    t.teacher_id,
                    CONCAT(st.first_name, ' ', st.last_name) AS teacher_name,
                    tm.day_of_week,
                    tm.timetable_date,
                    t.period_number,
                    t.period_name,
                    t.start_time,
                    t.end_time,
                    t.room_number,
                    t.is_break
                FROM
                    timetable t
                JOIN
                    timetable_master tm ON t.timetable_master_id = tm.timetable_master_id
                JOIN
                    session s ON tm.session_id = s.session_id
                JOIN
                    mst_subject sub ON t.subject_id = sub.subject_id
                JOIN
                    staff st ON t.teacher_id = st.staff_id
                JOIN
                    mst_class c ON tm.class_id = c.class_id
                JOIN
                    mst_section sec ON tm.section_id = sec.section_id
                WHERE
                    tm.session_id = ?
                    AND t.teacher_id = ?
                    AND tm.is_deleted = FALSE
                    AND t.is_deleted = FALSE
                    AND tm.timetable_date BETWEEN (
                        SELECT DATE_TRUNC('week', MAX(timetable_date))
                        FROM timetable_master
                        WHERE session_id = tm.session_id
                          AND is_deleted = FALSE
                    )
                    AND (
                        SELECT DATE_TRUNC('week', MAX(timetable_date)) + INTERVAL '5 days'
                        FROM timetable_master
                        WHERE session_id = tm.session_id
                          AND is_deleted = FALSE
                    )
                ORDER BY
                    CASE tm.day_of_week
                        WHEN 'MONDAY'    THEN 1
                        WHEN 'TUESDAY'   THEN 2
                        WHEN 'WEDNESDAY' THEN 3
                        WHEN 'THURSDAY'  THEN 4
                        WHEN 'FRIDAY'    THEN 5
                        WHEN 'SATURDAY'  THEN 6
                    END,
                    t.period_number;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<TimetableDetails> timetableDetails = null;
        try{
            timetableDetails = jdbcTemplate.query(sql, new Object[]{sessionId, staffId}, new RowMapper<TimetableDetails>() {
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
                    td.setTimeTableDate(rs.getDate("timetable_date"));
                    td.setPeriodNumber(rs.getInt("period_number"));
                    td.setPeriodName(rs.getString("period_name"));
                    td.setStartTime(Time.valueOf(rs.getTime("start_time").toLocalTime()));
                    td.setEndTime(Time.valueOf(rs.getTime("end_time").toLocalTime()));
                    td.setRoomNumber(rs.getString("room_number"));
                    td.setBreak(rs.getBoolean("is_break"));
                    return td;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return timetableDetails;
    }

    @Override
    public List<TimetableDetails> getAllTimeTableSchedule(int sessionId, String schoolCode) throws Exception {
        String sql = """
                WITH weekly_period_counts AS (
                    SELECT
                        tm.class_id,
                        tm.section_id,
                        tm.session_id,
                        DATE_TRUNC('week', tm.timetable_date)::DATE AS week_start,
                        COUNT(DISTINCT CASE WHEN t.is_break = FALSE AND t.period_number > 0
                                            THEN t.timetable_id END) AS weekly_total_periods
                    FROM timetable_master tm
                    LEFT JOIN timetable t
                           ON t.timetable_master_id = tm.timetable_master_id
                          AND t.is_deleted = FALSE
                    WHERE tm.is_deleted = FALSE
                    GROUP BY
                        tm.class_id,
                        tm.section_id,
                        tm.session_id,
                        DATE_TRUNC('week', tm.timetable_date)
                ),
                total_period_counts AS (
                    SELECT
                        class_id,
                        section_id,
                        session_id,
                        SUM(weekly_total_periods) AS total_periods
                    FROM weekly_period_counts
                    GROUP BY class_id, section_id, session_id
                ),
                -- Per day period count (excluding deleted and breaks)
                daily_period_counts AS (
                    SELECT
                        tm.timetable_master_id,
                        COUNT(t.timetable_id) AS total_periods_per_day
                    FROM timetable_master tm
                    LEFT JOIN timetable t
                           ON t.timetable_master_id = tm.timetable_master_id
                          AND t.is_deleted = FALSE
                          AND t.is_break = FALSE
                          AND t.period_number > 0
                    WHERE tm.is_deleted = FALSE
                    GROUP BY tm.timetable_master_id
                )
                SELECT
                    c.class_id,
                    s.section_id,
                    sess.session_id,
                    sess.academic_session,
                
                    c.class_name,
                    COALESCE(s.section_name, 'No Section') AS section_name,
                
                    cta.staff_id,
                
                    CASE
                        WHEN cta.staff_id IS NULL
                        THEN 'Class Teacher Not Assigned'
                        ELSE CONCAT(st.first_name, ' ', st.last_name)
                    END AS class_teacher,
                
                    DATE_TRUNC('week', tm.timetable_date)::DATE AS week_start,
                    (DATE_TRUNC('week', tm.timetable_date) + INTERVAL '5 days')::DATE AS week_end,
                
                    tm.day_of_week,
                    tm.timetable_date,
                    t.timetable_id,
                    t.period_number,
                    t.period_name,
                    t.start_time,
                    t.end_time,
                    t.subject_id,
                    sub.subject_name,
                    t.teacher_id,
                    CONCAT(tst.first_name, ' ', tst.last_name) AS period_teacher_name,
                    t.room_number,
                    t.is_break,
                
                    wpc.weekly_total_periods,
                    -- Per day count comes from daily_period_counts CTE (per timetable_master_id = per day)
                    dpc.total_periods_per_day,
                    tpc.total_periods,
                
                    (
                        SELECT COUNT(DISTINCT spd.student_id)
                        FROM student_academic_details sad
                        JOIN student_personal_details spd
                            ON spd.student_id = sad.student_id
                           AND spd.validity_end_date >= NOW()
                           AND spd.deleted IS NOT TRUE
                        WHERE sad.student_class_id = cs.class_id
                          AND sad.student_section_id = cs.section_id
                          AND sad.session_id = sess.session_id
                    ) AS total_students
                
                FROM mst_class c
                
                LEFT JOIN class_and_section cs
                       ON cs.class_id = c.class_id
                      AND cs.school_id = c.school_id
                
                LEFT JOIN mst_section s
                       ON s.section_id = cs.section_id
                      AND s.school_id = cs.school_id
                
                LEFT JOIN session sess
                       ON sess.school_id = c.school_id
                      AND sess.session_id = ?
                
                LEFT JOIN class_teacher_allocation cta
                       ON cta.class_id = cs.class_id
                      AND cta.section_id = cs.section_id
                      AND cta.session_id = sess.session_id
                
                LEFT JOIN staff st
                       ON st.staff_id = cta.staff_id
                
                INNER JOIN timetable_master tm
                        ON tm.class_id = cs.class_id
                       AND tm.section_id = cs.section_id
                       AND tm.session_id = sess.session_id
                       AND tm.is_deleted = FALSE
                
                INNER JOIN timetable t
                        ON t.timetable_master_id = tm.timetable_master_id
                       AND t.is_deleted = FALSE
                
                LEFT JOIN mst_subject sub
                       ON sub.subject_id = t.subject_id
                
                LEFT JOIN staff tst
                       ON tst.staff_id = t.teacher_id
                
                LEFT JOIN weekly_period_counts wpc
                       ON wpc.class_id = cs.class_id
                      AND wpc.section_id = cs.section_id
                      AND wpc.session_id = sess.session_id
                      AND wpc.week_start = DATE_TRUNC('week', tm.timetable_date)::DATE
                
                LEFT JOIN total_period_counts tpc
                       ON tpc.class_id = cs.class_id
                      AND tpc.section_id = cs.section_id
                      AND tpc.session_id = sess.session_id
                
                -- Join daily period counts per timetable_master_id (per day)
                LEFT JOIN daily_period_counts dpc
                       ON dpc.timetable_master_id = tm.timetable_master_id
                
                WHERE c.school_id = 1
                
                ORDER BY
                    c.class_id,
                    s.section_id,
                    week_start,
                    CASE tm.day_of_week
                        WHEN 'MONDAY'    THEN 1
                        WHEN 'TUESDAY'   THEN 2
                        WHEN 'WEDNESDAY' THEN 3
                        WHEN 'THURSDAY'  THEN 4
                        WHEN 'FRIDAY'    THEN 5
                        WHEN 'SATURDAY'  THEN 6
                    END,
                    t.period_number;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<TimetableDetails> timetableDetails = null;
        try{
            timetableDetails = jdbcTemplate.query(sql, new Object[]{sessionId}, new RowMapper<TimetableDetails>() {
                @Override
                public TimetableDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TimetableDetails td = new TimetableDetails();
                    td.setClassId(rs.getInt("class_id"));
                    td.setSectionId(rs.getInt("section_id"));
                    td.setSessionId(rs.getInt("session_id"));
                    td.setAcademicSession(rs.getString("academic_session"));
                    td.setClassName(rs.getString("class_name"));
                    td.setSectionName(rs.getString("section_name"));
                    td.setStaffId(rs.getInt("staff_id"));
                    td.setClassTeacher(rs.getString("class_teacher"));
                    td.setWeekStart(rs.getDate("week_start"));
                    td.setWeekEnd(rs.getDate("week_end"));
                    td.setDayOfWeek(rs.getString("day_of_week"));
                    td.setTimeTableDate(rs.getDate("timetable_date"));
                    td.setTimetableId(rs.getInt("timetable_id"));
                    td.setPeriodNumber(rs.getInt("period_number"));
                    td.setPeriodName(rs.getString("period_name"));
                    td.setStartTime(rs.getTime("start_time"));
                    td.setEndTime(rs.getTime("end_time"));
                    td.setSubjectId(rs.getInt("subject_id"));
                    td.setSubjectName(rs.getString("subject_name"));
                    // Period Teacher Info
                    td.setTeacherId(rs.getInt("teacher_id"));
                    td.setPeriodTeacherName(rs.getString("period_teacher_name"));
                    td.setRoomNumber(rs.getString("room_number"));
                    td.setBreak(rs.getBoolean("is_break"));
                    td.setWeeklyTotalPeriods(rs.getInt("weekly_total_periods"));
                    td.setTotalPeriodsPerDay(rs.getInt("total_periods_per_day"));
                    td.setTotalPeriods(rs.getInt("total_periods"));
                    td.setTotalStudents(rs.getInt("total_students"));
                    return td;
                }
            });
        } catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return timetableDetails;
    }


}
