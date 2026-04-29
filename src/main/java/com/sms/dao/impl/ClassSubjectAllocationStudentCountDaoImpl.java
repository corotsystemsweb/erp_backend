package com.sms.dao.impl;

import com.sms.dao.ClassSubjectAllocationStudentCountDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClassSubjectAllocationStudentCountDaoImpl implements ClassSubjectAllocationStudentCountDao {

    @Override
    public List<ClassSubjectAllocationDetails> getSubjectAllocationsWithStudentCount(
            int sessionId, Integer classId, Integer sectionId, Integer subjectId, String schoolCode)
            throws Exception {

        List<ClassSubjectAllocationDetails> list = new ArrayList<>();
        JdbcTemplate jdbcTemplate = null;

        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            // Base query
            StringBuilder query = new StringBuilder();
            query.append("SELECT ");
            query.append("    csa.subject_id, ");
            query.append("    sub.subject_name, ");
            query.append("    csa.class_id, ");
            query.append("    mc.class_name, ");
            query.append("    csa.section_id, ");
            query.append("    ms.section_name, ");
            query.append("    COUNT(DISTINCT sad.student_id) AS student_count ");
            query.append("FROM class_subject_allocation csa ");
            query.append("JOIN mst_subject sub ON csa.subject_id = sub.subject_id ");
            query.append("JOIN mst_class mc ON csa.class_id = mc.class_id ");
            query.append("JOIN mst_section ms ON csa.section_id = ms.section_id ");
            query.append("LEFT JOIN student_academic_details sad ");
            query.append("    ON sad.student_class_id = csa.class_id ");
            query.append("    AND sad.student_section_id = csa.section_id ");
            query.append("    AND sad.session_id = csa.session_id ");
            query.append("    AND (sad.is_dropout = FALSE OR sad.is_dropout IS NULL) ");
            query.append("WHERE csa.session_id = ? ");

            // Build parameters list
            List<Object> parameters = new ArrayList<>();
            parameters.add(sessionId);

            if (classId != null) {
                query.append("AND csa.class_id = ? ");
                parameters.add(classId);
            }

            if (sectionId != null) {
                query.append("AND csa.section_id = ? ");
                parameters.add(sectionId);
            }

            if (subjectId != null) {
                query.append("AND csa.subject_id = ? ");
                parameters.add(subjectId);
            }

            query.append("GROUP BY csa.subject_id, sub.subject_name, csa.class_id, mc.class_name, ");
            query.append("         csa.section_id, ms.section_name ");
            query.append("ORDER BY mc.class_name, ms.section_name, sub.subject_name");

            // Execute query
            list = jdbcTemplate.query(query.toString(), parameters.toArray(), new RowMapper<ClassSubjectAllocationDetails>() {
                @Override
                public ClassSubjectAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassSubjectAllocationDetails details = new ClassSubjectAllocationDetails();
                    details.setSubjectId(rs.getInt("subject_id"));
                    details.setSubjectName(rs.getString("subject_name"));
                    details.setClassId(rs.getInt("class_id"));
                    details.setClassName(rs.getString("class_name"));
                    details.setSectionId(rs.getInt("section_id"));
                    details.setSectionName(rs.getString("section_name"));
                    details.setStudentCount(rs.getInt("student_count"));
                    return details;
                }
            });

        } catch (Exception e) {
            throw new Exception("Error getting subject allocations with student count: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return list;
    }

    @Override
    public List<ClassSubjectAllocationDetails> getAllSubjectAllocationsWithStudentCount(
            int sessionId, String schoolCode) throws Exception {
        // Call the flexible method with null for all optional parameters
        return getSubjectAllocationsWithStudentCount(sessionId, null, null, null, schoolCode);
    }

    @Override
    public Integer getStudentCountForAllocation(
            int subjectId, int classId, int sectionId, int sessionId, String schoolCode)
            throws Exception {

        Integer studentCount = 0;
        JdbcTemplate jdbcTemplate = null;

        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            String query = "SELECT COUNT(DISTINCT sad.student_id) AS student_count " +
                    "FROM class_subject_allocation csa " +
                    "LEFT JOIN student_academic_details sad " +
                    "    ON sad.student_class_id = csa.class_id " +
                    "    AND sad.student_section_id = csa.section_id " +
                    "    AND sad.session_id = csa.session_id " +
                    "    AND (sad.is_dropout = FALSE OR sad.is_dropout IS NULL) " +
                    "WHERE csa.session_id = ? " +
                    "    AND csa.subject_id = ? " +
                    "    AND csa.class_id = ? " +
                    "    AND csa.section_id = ?";

            try {
                studentCount = jdbcTemplate.queryForObject(query, new Object[]{sessionId, subjectId, classId, sectionId}, Integer.class);
                if (studentCount == null) {
                    studentCount = 0;
                }
            } catch (EmptyResultDataAccessException e) {
                studentCount = 0;
            }

        } catch (Exception e) {
            throw new Exception("Error getting student count for specific allocation: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return studentCount;
    }
}