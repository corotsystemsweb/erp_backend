package com.sms.dao.impl;

import com.sms.dao.LessonPlanDao;
import com.sms.model.LessonPlanDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
@Repository
public class LessonPlanDaoImpl implements LessonPlanDao {

    // School-specific connection will be used via DatabaseUtil
  //  public LessonPlanDaoImpl() {}  // Updated constructor

    @Override
    public LessonPlanDetails save(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception {
        final String sql = "INSERT INTO lesson_plan ("
                + "school_id, session_id, csta_id, plan_title, "
                + "plan_description, created_by, status"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";  // 7 parameters

        try {
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, lessonPlanDetails.getSchoolId());
                ps.setInt(2, lessonPlanDetails.getSessionId());
                ps.setInt(3, lessonPlanDetails.getCstaId());
                ps.setString(4, lessonPlanDetails.getPlanTitle());
                ps.setString(5, lessonPlanDetails.getPlanDescription());
                 ps.setInt(6, lessonPlanDetails.getCreatedBy());
                ps.setString(7, lessonPlanDetails.getStatus());
                return ps;
            }, keyHolder);

            // Set generated ID
            if (keyHolder.getKey() != null) {
                lessonPlanDetails.setLessonPlanId(keyHolder.getKey().intValue());
            }
            return lessonPlanDetails;

        } catch (Exception e) {
            throw new Exception("Failed to save lesson plan: " + e.getMessage());
        }
    }

    @Override
    public LessonPlanDetails getById(int lessonPlanId, String schoolCode) throws Exception {
        final String sql = "SELECT "
                + "lesson_plan_id, school_id, session_id, csta_id, plan_title, "
                + "plan_description, created_by, created_at, updated_by, updated_at, status "
                + "FROM lesson_plan WHERE lesson_plan_id = ?";

        try {
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            return jdbcTemplate.queryForObject(sql, new Object[]{lessonPlanId}, new RowMapper<LessonPlanDetails>() {
                @Override
                public LessonPlanDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LessonPlanDetails details = new LessonPlanDetails();
                    details.setLessonPlanId(rs.getInt("lesson_plan_id"));
                    details.setSchoolId(rs.getInt("school_id"));
                    details.setSessionId(rs.getInt("session_id"));
                    details.setCstaId(rs.getInt("csta_id"));
                    details.setPlanTitle(rs.getString("plan_title"));
                    details.setPlanDescription(rs.getString("plan_description"));
                    details.setCreatedBy(rs.getInt("created_by"));
                    details.setStatus(rs.getString("status"));
                    details.setCreatedAt(rs.getTimestamp("created_at"));
                    details.setUpdatedAt(rs.getTimestamp("updated_at"));
                    details.setUpdatedBy(rs.getInt("updated_by"));
                    return details;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            throw new Exception("Failed to fetch lesson plan: " + e.getMessage());
        }
    }

    @Override
    public List<LessonPlanDetails> getAllLessionPlan(String schoolCode) throws Exception {
        final String sql = "SELECT "
                + "lesson_plan_id, school_id, session_id, csta_id, plan_title, "
                + "plan_description, created_by, created_at, updated_by, updated_at, status "
                + "FROM lesson_plan WHERE lesson_plan_id = ?";

        try {
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            return jdbcTemplate.query(sql, new RowMapper<LessonPlanDetails>() {
                @Override
                public LessonPlanDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LessonPlanDetails details = new LessonPlanDetails();
                    details.setLessonPlanId(rs.getInt("lesson_plan_id"));
                    details.setSchoolId(rs.getInt("school_id"));
                    details.setSessionId(rs.getInt("session_id"));
                    details.setCstaId(rs.getInt("csta_id"));
                    details.setPlanTitle(rs.getString("plan_title"));
                    details.setPlanDescription(rs.getString("plan_description"));
                    details.setCreatedBy(rs.getInt("created_by"));
                    details.setStatus(rs.getString("status"));
                    details.setCreatedAt(rs.getTimestamp("created_at"));
                    details.setUpdatedAt(rs.getTimestamp("updated_at"));
                    details.setUpdatedBy(rs.getInt("updated_by"));
                    return details;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            throw new Exception("Failed to fetch lesson plan: " + e.getMessage());
        }
    }

    @Override
    public int update(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception {
        final String sql = "UPDATE lesson_plan SET "
                + "school_id = ?, session_id = ?, csta_id = ?, "
                + "plan_title = ?, plan_description = ?, status = ?, "
                + "updated_by = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE lesson_plan_id = ?";
        try {
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            return jdbcTemplate.update(sql,
                    lessonPlanDetails.getSchoolId(),
                    lessonPlanDetails.getSessionId(),
                    lessonPlanDetails.getCstaId(),
                    lessonPlanDetails.getPlanTitle(),
                    lessonPlanDetails.getPlanDescription(),
                    lessonPlanDetails.getStatus(),
                    lessonPlanDetails.getUpdatedBy(),
                    lessonPlanDetails.getLessonPlanId());
        } catch (Exception e) {
            throw new Exception("Failed to update lesson plan: " + e.getMessage());
        }
    }
}