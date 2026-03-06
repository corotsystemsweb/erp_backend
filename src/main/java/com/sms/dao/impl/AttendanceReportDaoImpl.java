package com.sms.dao.impl;

import com.sms.dao.AttendanceReportDao;
import com.sms.model.DailyAttendanceDetail;
import com.sms.model.StudentAttendanceSummary;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class AttendanceReportDaoImpl implements AttendanceReportDao {

    private final RowMapper<StudentAttendanceSummary> rowMapper = new RowMapper<>() {
        @Override
        public StudentAttendanceSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudentAttendanceSummary summary = new StudentAttendanceSummary();
            summary.setStudentId(rs.getInt("student_id"));
            summary.setFirstName(rs.getString("first_name"));
            summary.setLastName(rs.getString("last_name"));
            summary.setClassName(rs.getString("class_name"));
            summary.setSectionName(rs.getString("section_name"));
            summary.setDailyDetails(rs.getString("daily_details"));
            summary.setSubjectDetails(rs.getString("subject_details"));
            summary.setTotalAbsentDays(rs.getInt("total_absent_days"));
            summary.setTotalPresentDays(rs.getInt("total_present_days"));
            summary.setAttendancePercentage(rs.getDouble("attendance_percentage"));
            return summary;
        }
    };

    @Override
    public List<StudentAttendanceSummary> getMonthlyAttendanceSummary(
            int classId, int sectionId, int sessionId,
            String startDate, String endDate, String schoolCode
    ) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            String sql =
                    "WITH parameters AS (" +
                            "  SELECT ? AS input_class_id, ? AS input_section_id, " +
                            "  ? AS input_session_id, ?::DATE AS start_date, ?::DATE AS end_date" +
                            "), " +
                            "date_range AS (" +
                            "  SELECT generate_series(start_date, end_date, '1 day'::INTERVAL)::DATE AS attendance_date " +
                            "  FROM parameters" +
                            "), " +
                            "students AS (" +
                            "  SELECT spd.student_id, spd.first_name, spd.last_name, c.class_name, s.section_name " +
                            "  FROM student_personal_details spd " +
                            "  JOIN student_academic_details sad ON spd.student_id = sad.student_id " +
                            "  JOIN mst_class c ON sad.student_class_id = c.class_id " +
                            "  JOIN mst_section s ON sad.student_section_id = s.section_id " +
                            "  WHERE c.class_id = (SELECT input_class_id FROM parameters) " +
                            "    AND s.section_id = (SELECT input_section_id FROM parameters) " +
                            "    AND sad.session_id = (SELECT input_session_id FROM parameters)" +
                            "), " +
                            "daily_attendance AS (" +
                            "  SELECT st.student_id, dr.attendance_date, " +
                            "    CASE WHEN ass.attendance_session_id IS NULL THEN 'Not Marked' " +
                            "         WHEN ar.student_id IS NULL THEN 'Present' ELSE 'Absent' END AS status " +
                            "  FROM students st " +
                            "  CROSS JOIN date_range dr " +
                            "  LEFT JOIN attendance_session ass " +
                            "    ON ass.class_id = (SELECT input_class_id FROM parameters) " +
                            "    AND ass.section_id = (SELECT input_section_id FROM parameters) " +
                            "    AND ass.session_type = 'DAILY' " +
                            "    AND ass.session_date = dr.attendance_date " +
                            "  LEFT JOIN attendance_record ar " +
                            "    ON ass.attendance_session_id = ar.attendance_session_id " +
                            "    AND st.student_id = ar.student_id" +
                            "), " +
                            "aggregated_daily AS (" +
                            "  SELECT student_id, " +
                            "    jsonb_agg(jsonb_build_object('date', attendance_date, 'status', status)) AS daily_details, " +
                            "    COUNT(*) FILTER (WHERE status = 'Absent') AS total_absent_days, " +
                            "    COUNT(*) FILTER (WHERE status = 'Present') AS total_present_days, " +
                            "    COUNT(*) FILTER (WHERE status IN ('Present', 'Absent')) AS total_marked_days " +
                            "  FROM daily_attendance " +
                            "  GROUP BY student_id" +
                            "), " +
                            "subject_attendance AS (" +
                            "  SELECT st.student_id, sub.subject_name, " +
                            "    COUNT(*) FILTER (WHERE ar.student_id IS NULL) AS present_classes, " +
                            "    COUNT(*) FILTER (WHERE ar.student_id IS NOT NULL) AS absent_classes, " +
                            "    COUNT(*) AS total_classes " +
                            "  FROM students st " +
                            "  JOIN attendance_session ass " +
                            "    ON ass.class_id = (SELECT input_class_id FROM parameters) " +
                            "    AND ass.section_id = (SELECT input_section_id FROM parameters) " +
                            "    AND ass.session_type = 'SUBJECT' " +
                            "    AND ass.session_date BETWEEN (SELECT start_date FROM parameters) AND (SELECT end_date FROM parameters) " +
                            "  JOIN mst_subject sub ON ass.subject_id = sub.subject_id " +
                            "  LEFT JOIN attendance_record ar ON ass.attendance_session_id = ar.attendance_session_id AND st.student_id = ar.student_id " +
                            "  GROUP BY st.student_id, sub.subject_name" +
                            "), " +
                            "aggregated_subjects AS (" +
                            "  SELECT student_id, " +
                            "    jsonb_agg(jsonb_build_object('subject', subject_name, 'present', present_classes, 'absent', absent_classes, 'total', total_classes)) AS subject_details " +
                            "  FROM subject_attendance " +
                            "  GROUP BY student_id" +
                            "), " +
                            "final_output AS (" +
                            "  SELECT s.student_id, s.first_name, s.last_name, s.class_name, s.section_name, " +
                            "    ad.daily_details::text AS daily_details, " +
                            "    asb.subject_details::text AS subject_details, " +
                            "    ad.total_absent_days, ad.total_present_days, " +
                            "    CASE WHEN ad.total_marked_days > 0 THEN ROUND((ad.total_present_days::DECIMAL / ad.total_marked_days) * 100, 2) ELSE 0 END AS attendance_percentage " +
                            "  FROM students s " +
                            "  JOIN aggregated_daily ad ON s.student_id = ad.student_id " +
                            "  LEFT JOIN aggregated_subjects asb ON s.student_id = asb.student_id" +
                            ") " +
                            "SELECT * FROM final_output ORDER BY student_id";

            return jdbcTemplate.query(sql, rowMapper, classId, sectionId, sessionId, startDate, endDate);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<StudentAttendanceSummary> getDateRangeAttendanceSummary(
            int classId, int sectionId,
            String startDate, String endDate, String schoolCode
    ) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            String sql =
                    "WITH parameters AS (" +
                            "  SELECT ? AS input_class_id, ? AS input_section_id, " +
                            "  ?::DATE AS start_date, ?::DATE AS end_date" +
                            "), " +
                            "students AS (" +
                            "  SELECT spd.student_id, spd.first_name, spd.last_name, sad.student_class_id, sad.student_section_id " +
                            "  FROM student_personal_details spd " +
                            "  JOIN student_academic_details sad ON spd.student_id = sad.student_id " +
                            "  WHERE sad.student_class_id = (SELECT input_class_id FROM parameters) " +
                            "    AND sad.student_section_id = (SELECT input_section_id FROM parameters) " +
                            "    AND sad.session_id = (SELECT MAX(session_id) FROM session)" +
                            "), " +
                            "daily_attendance AS (" +
                            "  SELECT s.student_id, " +
                            "    COUNT(ass.attendance_session_id) FILTER (WHERE ar.student_id IS NULL) AS present_days, " +
                            "    COUNT(ar.student_id) AS absent_days " +
                            "  FROM students s " +
                            "  LEFT JOIN attendance_session ass " +
                            "    ON ass.class_id = s.student_class_id " +
                            "    AND ass.section_id = s.student_section_id " +
                            "    AND ass.session_type = 'DAILY' " +
                            "    AND ass.session_date BETWEEN (SELECT start_date FROM parameters) AND (SELECT end_date FROM parameters) " +
                            "  LEFT JOIN attendance_record ar " +
                            "    ON ass.attendance_session_id = ar.attendance_session_id " +
                            "    AND s.student_id = ar.student_id " +
                            "  GROUP BY s.student_id" +
                            "), " +
                            "subject_attendance AS (" +
                            "  SELECT s.student_id, sub.subject_name, " +
                            "    COUNT(ass.attendance_session_id) FILTER (WHERE ar.student_id IS NULL) AS present_days, " +
                            "    COUNT(ar.student_id) AS absent_days, " +
                            "    COUNT(ass.attendance_session_id) AS total_classes " +
                            "  FROM students s " +
                            "  CROSS JOIN mst_subject sub " +
                            "  LEFT JOIN attendance_session ass " +
                            "    ON ass.class_id = s.student_class_id " +
                            "    AND ass.section_id = s.student_section_id " +
                            "    AND ass.subject_id = sub.subject_id " +
                            "    AND ass.session_type = 'SUBJECT' " +
                            "    AND ass.session_date BETWEEN (SELECT start_date FROM parameters) AND (SELECT end_date FROM parameters) " +
                            "  LEFT JOIN attendance_record ar " +
                            "    ON ass.attendance_session_id = ar.attendance_session_id " +
                            "    AND s.student_id = ar.student_id " +
                            "  GROUP BY s.student_id, sub.subject_name" +
                            "), " +
                            "subject_aggregation AS (" +
                            "  SELECT student_id, " +
                            "    jsonb_agg(jsonb_build_object('subject', subject_name, 'present_days', present_days, 'absent_days', absent_days, 'total_classes', total_classes)) AS subject_wise " +
                            "  FROM subject_attendance " +
                            "  GROUP BY student_id" +
                            ") " +
                            "SELECT s.student_id, s.first_name, s.last_name, " +
                            "  da.present_days AS total_present_days, " +
                            "  da.absent_days AS total_absent_days, " +
                            "  sa.subject_wise::text AS subject_details, " +
                            "  c.class_name, " +
                            "  sec.section_name, " +
                            "  '[]'::text AS daily_details, " +  // Empty array for daily_details
                            "  0.0 AS attendance_percentage " +  // Dummy value for percentage
                            "FROM students s " +
                            "JOIN daily_attendance da ON s.student_id = da.student_id " +
                            "LEFT JOIN subject_aggregation sa ON s.student_id = sa.student_id " +
                            "JOIN mst_class c ON s.student_class_id = c.class_id " +
                            "JOIN mst_section sec ON s.student_section_id = sec.section_id " +
                            "ORDER BY s.student_id";

            return jdbcTemplate.query(sql, rowMapper, classId, sectionId, startDate, endDate);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<Map<String, Object>> getMonthlyAttendanceSummaryforDashboard(Long classId, Long sectionId, int month, int year, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            String sql = """
                        SELECT\s
                                                    a.session_date AS date,
                                                    (total_students - COUNT(r.student_id)) AS present_students,
                                                    COUNT(r.student_id) AS absent_students,
                                                    total_students
                                                FROM attendance_session a
                                                LEFT JOIN attendance_record r ON a.attendance_session_id = r.attendance_session_id
                                                CROSS JOIN (
                                                    SELECT COUNT(*) AS total_students\s
                                                    FROM student_academic_details\s
                                                    WHERE student_class_id = ?\s
                                                    AND student_section_id = ?
                                                ) AS total
                                                WHERE a.class_id = ?
                                                  AND a.section_id = ?
                                                  AND a.session_type = 'DAILY' -- Only daily attendance
                                                  AND EXTRACT(MONTH FROM a.session_date) = ?
                                                  AND EXTRACT(YEAR FROM a.session_date) = ?
                                                GROUP BY a.session_date, total.total_students;
                    """;
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            return jdbcTemplate.queryForList(sql, classId, sectionId, classId, sectionId, month, year);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<Map<String, Object>> getClassSectionSummary(String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            String sql = """
                    SELECT 
                        c.class_name,
                        s.section_name,
                        (total.total_students - COUNT(DISTINCT r.student_id)) AS present_students,
                        COUNT(DISTINCT r.student_id) AS absent_students,
                        total.total_students
                    FROM attendance_session a
                    JOIN mst_class c ON a.class_id = c.class_id
                    JOIN mst_section s ON a.section_id = s.section_id
                    CROSS JOIN (
                        SELECT COUNT(*) AS total_students 
                        FROM student_academic_details 
                        WHERE student_class_id = c.class_id 
                        AND student_section_id = s.section_id
                    ) AS total
                    LEFT JOIN attendance_record r ON a.attendance_session_id = r.attendance_session_id
                    WHERE a.session_type = 'DAILY'
                    GROUP BY c.class_name, s.section_name, total.total_students
                    """;
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<Map<String, Object>> getStudentAttendanceDetails(Long studentId, Long classId, Long sectionId, Date startDate, Date endDate, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            String sql = """
                    SELECT 
                        a.session_date,
                        a.session_type,
                        CASE 
                            WHEN a.session_type = 'SUBJECT' THEN sub.subject_name
                            ELSE 'Daily'
                        END AS attendance_type,
                        CASE 
                            WHEN r.student_id IS NULL THEN 'Present' 
                            ELSE 'Absent' 
                        END AS status
                    FROM attendance_session a
                    LEFT JOIN attendance_record r 
                        ON a.attendance_session_id = r.attendance_session_id 
                        AND r.student_id = ?
                    LEFT JOIN mst_subject sub
                        ON a.subject_id = sub.subject_id
                    WHERE a.class_id = ?
                      AND a.section_id = ?
                      AND a.session_date BETWEEN ? AND ?
                    ORDER BY a.session_date, a.session_type
                    """;
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            return jdbcTemplate.queryForList(sql,
                    studentId,
                    classId, sectionId,
                    new java.sql.Date(startDate.getTime()),
                    new java.sql.Date(endDate.getTime())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<StudentAttendanceSummary> getAttendanceSummaryByClass(String schoolCode) throws Exception {
        String sql = """
                WITH distinct_students AS (
                    SELECT DISTINCT\s
                        student_class_id,\s
                        student_section_id,\s
                        session_id,\s
                        student_id
                    FROM student_academic_details
                )
                SELECT\s
                    mc.class_name,  -- Replace class_id with class name
                    ms.section_name, -- Replace section_id with section name
                    COUNT(DISTINCT ds.student_id) AS total_students,
                    COUNT(ar.student_id) AS absent_count,
                    COUNT(DISTINCT ds.student_id) - COUNT(ar.student_id) AS present_count
                FROM attendance_session a
                INNER JOIN distinct_students ds
                    ON a.class_id = ds.student_class_id
                    AND a.section_id = ds.student_section_id
                    AND a.session_id = ds.session_id
                LEFT JOIN attendance_record ar
                    ON a.attendance_session_id = ar.attendance_session_id
                    AND ds.student_id = ar.student_id
                INNER JOIN mst_class mc\s
                    ON a.class_id = mc.class_id  -- Join to get class name
                INNER JOIN mst_section ms\s
                    ON a.section_id = ms.section_id  -- Join to get section name
                WHERE a.session_type = 'DAILY'
                    AND a.session_date = CURRENT_DATE
                GROUP BY mc.class_name, ms.section_name\s
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentAttendanceSummary> attendanceClasswise = null;
        try {
            attendanceClasswise = jdbcTemplate.query(sql, new RowMapper<StudentAttendanceSummary>() {
                @Override
                public StudentAttendanceSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentAttendanceSummary sd = new StudentAttendanceSummary();
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setTotalAbsentDays(rs.getInt("absent_count"));
                    sd.setTotalPresentDays(rs.getInt("present_count"));
                    return sd;
                }
            });
        } catch (Exception e) {
            throw new Exception("Error fetching attendance details", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return attendanceClasswise;
    }

   /* @Override
    public List<StudentAttendanceSummary> getAttendanceDetails(Long studentId, String intervalType, Integer year, String schoolCode) throws Exception {
        String sql = """
            SELECT
                student_id,
                year,
                period,
                COUNT(attendance_session_id) AS total_days,
                COUNT(absent_student_id) AS absent_days,
                (COUNT(attendance_session_id) - COUNT(absent_student_id)) AS present_days,
                ROUND(
                    ((COUNT(attendance_session_id) - COUNT(absent_student_id)) / 
                    GREATEST(COUNT(attendance_session_id), 1)::numeric * 100), 
                2) AS attendance_percentage
            FROM (
                SELECT
                    sa.student_id,
                    EXTRACT(YEAR FROM a.session_date) AS year,
                    CASE 
                        WHEN ? = 'week' THEN EXTRACT(WEEK FROM a.session_date)
                        WHEN ? = 'month' THEN EXTRACT(MONTH FROM a.session_date)
                        ELSE NULL 
                    END AS period,
                    a.attendance_session_id,
                    ar.student_id AS absent_student_id
                FROM student_academic_details sa
                INNER JOIN attendance_session a 
                    ON sa.session_id = a.session_id
                    AND sa.student_class_id = a.class_id
                    AND sa.student_section_id = a.section_id
                    AND a.session_type = 'DAILY'
                    AND a.session_date >= sa.admission_date
                LEFT JOIN attendance_record ar 
                    ON a.attendance_session_id = ar.attendance_session_id
                    AND sa.student_id = ar.student_id
                WHERE sa.student_id = ?
                  AND (?::integer IS NULL OR EXTRACT(YEAR FROM a.session_date) = ?::integer)
            ) AS subquery
            GROUP BY student_id, year, period
            ORDER BY student_id, year, period NULLS FIRST
    """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentAttendanceSummary> studentAttendance = null;
        try {
            studentAttendance = jdbcTemplate.query(sql, new Object[]{intervalType, intervalType, studentId, year, year}, new RowMapper<StudentAttendanceSummary>() {
                @Override
                public StudentAttendanceSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentAttendanceSummary details = new StudentAttendanceSummary();
                    details.setStudentId(rs.getInt("student_id"));
                    details.setYear(rs.getInt("year"));
                    // Corrected column name from 'month' to 'period' if necessary
                    details.setPeriod(rs.getObject("period") != null ? rs.getInt("period") : null);
                    details.setTotalDays(rs.getDouble("total_days"));
                    details.setTotalAbsentDays(rs.getInt("absent_days"));
                    details.setTotalPresentDays(rs.getInt("present_days"));
                    details.setAttendancePercentage(rs.getDouble("attendance_percentage"));
                    return details;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentAttendance;
    }*/

    @Override
    public List<StudentAttendanceSummary> getAttendanceDetails(
            Long studentId, String intervalType, Integer year,
            Integer classId, Integer sectionId, String schoolCode) throws Exception {

        String sql = """
        WITH attendance_details AS (
            SELECT
                sa.student_id,
                sa.student_class_id AS class_id,
                sa.student_section_id AS section_id,
                a.session_date,
                EXTRACT(YEAR FROM a.session_date) AS date_part_year,
                CASE 
                    WHEN ? = 'week' THEN EXTRACT(WEEK FROM a.session_date)
                    WHEN ? = 'month' THEN EXTRACT(MONTH FROM a.session_date)
                    ELSE NULL 
                END AS period,
                ar.student_id IS NOT NULL AS is_absent
            FROM student_academic_details sa
            INNER JOIN attendance_session a 
                ON sa.session_id = a.session_id
                AND sa.student_class_id = a.class_id
                AND sa.student_section_id = a.section_id
                AND a.session_type = 'DAILY'
                AND a.session_date >= sa.admission_date
            LEFT JOIN attendance_record ar 
                ON a.attendance_session_id = ar.attendance_session_id
                AND sa.student_id = ar.student_id
            WHERE sa.student_id = ?
              AND (?::integer IS NULL OR EXTRACT(YEAR FROM a.session_date) = ?::integer)
              AND (?::integer IS NULL OR sa.student_class_id = ?::integer)
              AND (?::integer IS NULL OR sa.student_section_id = ?::integer)
        ),
        summary AS (
            SELECT
                student_id,
                class_id,
                section_id,
                date_part_year AS year,
                period,
                COUNT(DISTINCT session_date) AS total_days,
                COUNT(DISTINCT CASE WHEN is_absent THEN session_date END) AS absent_days,
                COUNT(DISTINCT session_date) - COUNT(DISTINCT CASE WHEN is_absent THEN session_date END) AS present_days,
                ROUND(
                    ((COUNT(DISTINCT session_date) - COUNT(DISTINCT CASE WHEN is_absent THEN session_date END)) / 
                    GREATEST(COUNT(DISTINCT session_date), 1)::numeric * 100), 
                2) AS attendance_percentage
            FROM attendance_details
            GROUP BY 
                student_id,
                class_id,
                section_id,
                date_part_year,
                period
        ),
        date_details AS (
            SELECT
                student_id,
                to_char(session_date, 'YYYY-MM-DD') AS date_str,
                is_absent
            FROM attendance_details
        )
        SELECT 
            s.student_id,
            s.class_id,
            s.section_id,
            s.year,
            s.period,
            s.total_days,
            s.absent_days,
            s.present_days,
            s.attendance_percentage,
            json_agg(
                json_build_object(
                    'date', d.date_str,
                    'status', CASE WHEN d.is_absent THEN 'Absent' ELSE 'Present' END
                )
            ) AS daily_details
        FROM summary s
        JOIN date_details d ON s.student_id = d.student_id
        GROUP BY 
            s.student_id,
            s.class_id,
            s.section_id,
            s.year,
            s.period,
            s.total_days,
            s.absent_days,
            s.present_days,
            s.attendance_percentage
        ORDER BY 
            s.student_id,
            s.class_id,
            s.section_id,
            s.year, 
            s.period NULLS FIRST
        """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, new Object[]{
                    intervalType, intervalType,
                    studentId,
                    year, year,
                    classId, classId,
                    sectionId, sectionId
            }, (rs, rowNum) -> {
                StudentAttendanceSummary summary = new StudentAttendanceSummary();
                summary.setStudentId(rs.getInt("student_id"));
               // summary.setClassId(rs.getInt("class_id"));
             //   summary.setSectionId(rs.getInt("section_id"));
                summary.setYear(rs.getInt("year"));
                summary.setPeriod(rs.getObject("period") != null ? rs.getInt("period") : null);
                summary.setTotalDays(rs.getDouble("total_days"));
                summary.setTotalAbsentDays(rs.getInt("absent_days"));
                summary.setTotalPresentDays(rs.getInt("present_days"));
                summary.setAttendancePercentage(rs.getDouble("attendance_percentage"));

                // Handle daily details
                String dailyDetailsJson = rs.getString("daily_details");
                if (dailyDetailsJson != null) {
                    summary.setDailyDetails(parseDailyDetails(dailyDetailsJson));
                }

                return summary;
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error fetching attendance details", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    private String parseDailyDetails(String json) {
        List<DailyAttendanceDetail> details = new ArrayList<>();

        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return details.toString();
        }

        // Remove outer brackets and whitespace
        json = json.trim().substring(1, json.length() - 1).trim();

        // Split into individual entries
        String[] entries = json.split("\\s*,\\s*(?=\\{)");

        for (String entry : entries) {
            try {
                // Clean up the entry
                entry = entry.trim();
                if (entry.startsWith("{")) {
                    entry = entry.substring(1);
                }
                if (entry.endsWith("}")) {
                    entry = entry.substring(0, entry.length() - 1);
                }

                String date = extractJsonValue(entry, "date");
                String status = extractJsonValue(entry, "status");
                details.add(new DailyAttendanceDetail(date, status));
            } catch (Exception e) {
                System.err.println("Error parsing attendance entry: " + entry);
                e.printStackTrace();
                // Continue with next entry
            }
        }

        return details.toString();
    }

    private String extractJsonValue(String json, String key) {
        // Handle cases where key might be wrapped in quotes with spaces
        String keyPattern1 = "\"" + key + "\"\\s*:";
        String keyPattern2 = "'" + key + "'\\s*:";
        String keyPattern3 = key + "\\s*:";

        int keyPos = -1;
        String usedPattern = "";

        // Check for different key patterns
        if (json.contains("\"" + key + "\"")) {
            keyPos = json.indexOf("\"" + key + "\"");
            usedPattern = "\"" + key + "\"";
        } else if (json.contains("'" + key + "'")) {
            keyPos = json.indexOf("'" + key + "'");
            usedPattern = "'" + key + "'";
        } else if (json.contains(key + ":")) {
            keyPos = json.indexOf(key + ":");
            usedPattern = key + ":";
        }

        if (keyPos < 0) {
            throw new IllegalArgumentException("Key '" + key + "' not found in JSON: " + json);
        }

        // Find the value after the key
        int valueStart = json.indexOf(':', keyPos + usedPattern.length());
        if (valueStart < 0) {
            throw new IllegalArgumentException("Invalid JSON format after key '" + key + "'");
        }

        // Skip whitespace after colon
        valueStart++;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        // Determine if value is quoted
        boolean isQuoted = valueStart < json.length() &&
                (json.charAt(valueStart) == '"' || json.charAt(valueStart) == '\'');

        char quoteChar = isQuoted ? json.charAt(valueStart) : '\0';
        int valueStartPos = isQuoted ? valueStart + 1 : valueStart;

        // Find end of value
        int valueEnd;
        if (isQuoted) {
            valueEnd = json.indexOf(quoteChar, valueStartPos);
        } else {
            // Find next comma or end of string
            valueEnd = json.length();
            int commaPos = json.indexOf(',', valueStartPos);
            if (commaPos > valueStartPos && commaPos < valueEnd) {
                valueEnd = commaPos;
            }
            // Trim whitespace
            while (valueEnd > valueStartPos && Character.isWhitespace(json.charAt(valueEnd - 1))) {
                valueEnd--;
            }
        }

        if (valueEnd < valueStartPos) {
            throw new IllegalArgumentException("Invalid JSON value for key '" + key + "'");
        }

        return json.substring(valueStartPos, valueEnd);
    }
}