package com.sms.dao.impl;

import com.sms.dao.StudentAttendanceDao;
import com.sms.model.StudentAttendanceDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StudentAttendanceDaoImpl implements StudentAttendanceDao{
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public StudentAttendanceDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findLastAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(ca_id), 0)  AS ca_id FROM class_attendance";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int lastAvailableId = 0;

        try {
            // Execute the SQL query using jdbcTemplate
            lastAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return lastAvailableId;
    }
    @Override
    public List<StudentAttendanceDetails> addAttendance(List<StudentAttendanceDetails> studentAttendanceDetailsList, String schoolCode) throws Exception {
        int lastAvailableId = findLastAvailableId(schoolCode);
        String sql = "INSERT INTO absent_details (ca_id,student_id,absent,attendance_date) VALUES (?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentAttendanceDetails> insertedAttendanceList = new ArrayList<>();
        try {
            for(StudentAttendanceDetails studentAttendanceDetails : studentAttendanceDetailsList) {
                studentAttendanceDetails.setCaId(lastAvailableId); // Set caId here
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection ->{
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, studentAttendanceDetails.getCaId());
                    ps.setInt(2, studentAttendanceDetails.getStudentId());
                    ps.setString(3, studentAttendanceDetails.getAbsent());
                    ps.setDate(4, studentAttendanceDetails.getAttendanceDate() != null ?
                            new java.sql.Date(studentAttendanceDetails.getAttendanceDate().getTime()) : null);
                    return ps;
                }, keyHolder);
                Map<String, Object> keys = keyHolder.getKeys();
                if (keys != null && keys.containsKey("ad_id")) {
                    int generatedId = ((Number) keys.get("ad_id")).intValue();
                    studentAttendanceDetails.setAdId(generatedId);
                    insertedAttendanceList.add(studentAttendanceDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return insertedAttendanceList;
    }
    @Override
    public List<StudentAttendanceDetails> getTotalAttendance(Integer studentId, Integer subjectId, Date dateFrom, Date dateTo, String schoolCode) {
      /* String query = "SELECT " +
                "saptc.student_id, " +
                "saptc.first_name, " +
                "saptc.last_name, " +
                "saptc.father_name, " +
                "COALESCE(sa.total_absent_count, 0) AS total_absent_count, " +
                "saptc.total_class_count, " +
                "(saptc.total_class_count - COALESCE(sa.total_absent_count, 0)) AS total_present_count, " +
                "mst_class.class_name, " +
                "mst_section.section_name " +
                "FROM " +
                "(SELECT " +
                "sapd.student_id, " +
                "sapd.first_name, " +
                "sapd.last_name, " +
                "sapd.father_name, " +
                "sapd.student_class_id, " +
                "sapd.student_section_id, " +
                "COALESCE(ctc.total_class_count,0) AS total_class_count " +
                "FROM " +
                "(SELECT " +
                "sad.student_id, " +
                "spd.first_name, " +
                "spd.last_name, " +
                "spd.father_name, " +
                "sad.student_class_id, " +
                "sad.student_section_id " +
                "FROM " +
                "student_academic_details AS sad " +
                "JOIN " +
                "student_personal_details AS spd ON sad.student_id = spd.student_id " +
                "WHERE " +
                "(?:: int IS NULL OR sad.student_id = ?) " +
                "AND spd.deleted is not true " +
                "AND spd.validity_end_date >= NOW() " +///
                "AND sad.validity_end_date >= NOW()) AS sapd " +///
                "LEFT JOIN " +
                "(SELECT class_id, COALESCE(count(ca.present_date),0) AS total_class_count " +
                "FROM " +
                "class_attendance AS ca " +
                "WHERE " +
                "(?:: int IS NULL OR ca.subject_id = ?) " +
                "AND (?:: date IS NULL OR ca.present_date >= ?) " +
                "AND (?:: date IS NULL OR ca.present_date <= ?) " +
                "GROUP BY class_id) AS ctc " +
                "ON ctc.class_id = sapd.student_class_id) AS saptc " +
                "LEFT JOIN " +
                "mst_class ON saptc.student_class_id = mst_class.class_id " +
                "LEFT JOIN " +
                "mst_section ON saptc.student_section_id = mst_section.section_id " +
                "LEFT JOIN " +
                "(SELECT " +
                "ad.student_id AS student_id, " +
                "COUNT(DISTINCT ad.attendance_date) AS total_absent_count " +
                "FROM " +
                "absent_details AS ad " +
                "WHERE " +
                "ad.ca_id IN " +
                "(SELECT " +
                "ca1.ca_id " +
                "FROM " +
                "class_attendance AS ca1 " +
                "WHERE " +
                "(?::int IS NULL OR ca1.subject_id = ?) " +
                "AND (?:: date IS NULL OR ca1.present_date >= ?) " +
                "AND (?:: date IS NULL OR ca1.present_date <= ?)) " +
                "GROUP BY " +
                "ad.student_id) AS sa ON saptc.student_id = sa.student_id " +
                "ORDER BY " +
                "saptc.student_id ASC ";*/
        String query = "SELECT\n" +
                "saptc.student_id,\n" +
                "saptc.first_name,\n" +
                "saptc.last_name,\n" +
                "saptc.father_name,\n" +
                "COALESCE(sa.total_absent_count, 0) AS total_absent_count,\n" +
                "saptc.total_class_count,\n" +
                "(saptc.total_class_count - COALESCE(sa.total_absent_count, 0)) AS total_present_count,\n" +
                "mst_class.class_name,\n" +
                "mst_section.section_name\n" +
                "FROM\n" +
                "(\n" +
                "SELECT\n" +
                "sapd.student_id,\n" +
                "sapd.first_name,\n" +
                "sapd.last_name,\n" +
                "sapd.father_name,\n" +
                "sapd.student_class_id,\n" +
                "sapd.student_section_id,\n" +
                "COALESCE(ctc.total_class_count, 0) AS total_class_count\n" +
                "FROM\n" +
                "(\n" +
                "SELECT\n" +
                "sad.student_id,\n" +
                "spd.first_name,\n" +
                "spd.last_name,\n" +
                "spd.father_name,\n" +
                "sad.student_class_id,\n" +
                "sad.student_section_id\n" +
                "FROM\n" +
                "student_academic_details AS sad\n" +
                "JOIN\n" +
                "student_personal_details AS spd ON sad.student_id = spd.student_id\n" +
                "WHERE\n" +
                "(?::int IS NULL OR sad.student_id = ?)\n" +
                "AND spd.deleted IS NOT TRUE\n" +
                "AND spd.validity_end_date >= NOW()\n" +
                "AND sad.validity_end_date >= NOW()\n" +
                ") AS sapd\n" +
                "LEFT JOIN\n" +
                "(\n" +
                "SELECT\n" +
                "ca.class_id,\n" +
                "ca.section_id,\n" +
                "COALESCE(COUNT(ca.present_date), 0) AS total_class_count\n" +
                "FROM\n" +
                "class_attendance AS ca\n" +
                "WHERE\n" +
                "(?::int IS NULL OR ca.subject_id = ?)\n" +
                "AND (?::date IS NULL OR ca.present_date >= ?)\n" +
                "AND (?::date IS NULL OR ca.present_date <= ?)\n" +
                "GROUP BY\n" +
                "ca.class_id, ca.section_id\n" +
                ") AS ctc ON ctc.class_id = sapd.student_class_id AND ctc.section_id = sapd.student_section_id\n" +
                ") AS saptc\n" +
                "LEFT JOIN\n" +
                "mst_class ON saptc.student_class_id = mst_class.class_id\n" +
                "LEFT JOIN\n" +
                "mst_section ON saptc.student_section_id = mst_section.section_id\n" +
                "LEFT JOIN\n" +
                "(\n" +
                "SELECT\n" +
                "ad.student_id,\n" +
                "ca1.section_id,\n" +
                "COUNT(DISTINCT ad.attendance_date) AS total_absent_count\n" +
                "FROM\n" +
                "absent_details AS ad\n" +
                "JOIN\n" +
                "class_attendance AS ca1 ON ad.ca_id = ca1.ca_id\n" +
                "WHERE\n" +
                "(?::int IS NULL OR ca1.subject_id = ?)\n" +
                "AND (?::date IS NULL OR ca1.present_date >= ?)\n" +
                "AND (?::date IS NULL OR ca1.present_date <= ?)\n" +
                "GROUP BY\n" +
                "ad.student_id, ca1.section_id\n" +
                ") AS sa ON saptc.student_id = sa.student_id AND saptc.student_section_id = sa.section_id\n" +
                "ORDER BY\n" +
                "saptc.student_id ASC\n";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(query,
                    new Object[]{studentId, studentId, subjectId, subjectId, dateFrom, dateFrom, dateTo, dateTo, subjectId, subjectId, dateFrom, dateFrom, dateTo, dateTo},
                    (rs, rowNum) -> {
                        StudentAttendanceDetails student = new StudentAttendanceDetails();
                        student.setStudentId(rs.getInt("student_id"));
                        student.setFirstName(rs.getString("first_name"));
                        student.setLastName(rs.getString("last_name"));
                        student.setFatherName(rs.getString("father_name"));
                        student.setTotalAbsent(rs.getInt("total_absent_count"));
                        student.setTotalClass(rs.getInt("total_class_count"));
                        student.setTotalPresent(rs.getInt("total_present_count"));
                        student.setClassName(rs.getString("class_name"));
                        student.setSectionName(rs.getString("section_name"));
                        return student;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}







