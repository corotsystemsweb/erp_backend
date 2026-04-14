package com.sms.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sms.dao.ExamTypeManagerDao;
import com.sms.model.ExamTypeManager;
import com.sms.util.DatabaseUtil;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class ExamTypeManagerDaoImpl implements ExamTypeManagerDao {
    private final JdbcTemplate jdbcTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public ExamTypeManagerDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExamTypeManager addExamType(ExamTypeManager examTypeManager, String schoolCode) throws Exception {

        String sql = "INSERT INTO exam_type " +
                "(school_id, session_id, name, description, category, " +
                "weightage_percent, passing_marks_percent, grading_system, exam_options, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ObjectMapper objectMapper = new ObjectMapper(); // Make sure this is initialized

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            // Store JSON string outside lambda to handle exception
            final String examOptionsJson;
            try {
                examOptionsJson = examTypeManager.getExamOptions() != null
                        ? objectMapper.writeValueAsString(examTypeManager.getExamOptions())
                        : null;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize exam options to JSON", e);
            }

            final String category = examTypeManager.getCategory() != null
                    ? examTypeManager.getCategory() : "major";
            final String gradingSystem = examTypeManager.getGradingSystem() != null
                    ? examTypeManager.getGradingSystem() : "CBSE Standard Grade";
            final String status = examTypeManager.getStatus() != null
                    ? examTypeManager.getStatus() : "ACTIVE";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, examTypeManager.getSchoolId());
                ps.setInt(2, examTypeManager.getSessionId());
                ps.setString(3, examTypeManager.getName());
                ps.setString(4, examTypeManager.getDescription());
                ps.setString(5, category);

                // weightagePercent — optional
                if (examTypeManager.getWeightagePercent() != null) {
                    ps.setDouble(6, examTypeManager.getWeightagePercent());
                } else {
                    ps.setNull(6, java.sql.Types.DECIMAL);
                }

                // passingMarksPercent — optional
                if (examTypeManager.getPassingMarksPercent() != null) {
                    ps.setDouble(7, examTypeManager.getPassingMarksPercent());
                } else {
                    ps.setNull(7, java.sql.Types.DECIMAL);
                }

                ps.setString(8, gradingSystem);

                // examOptions JSONB — optional
                if (examOptionsJson != null) {
                    ps.setString(9, examOptionsJson);
                } else {
                    ps.setNull(9, java.sql.Types.OTHER);
                }

                ps.setString(10, status);

                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("exam_type_id")) {
                int generatedKey = ((Number) keys.get("exam_type_id")).intValue();
                examTypeManager.setExamTypeId(generatedKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examTypeManager;
    }

    //    public ExamTypeManager addExamType(ExamTypeManager examTypeManager, String schoolCode) throws Exception {
//        String sql="Insert into exam_type (name,session_id,description) values(?,?,?)";
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        try {
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            jdbcTemplate.update(connection -> {
//                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//                ps.setString(1, examTypeManager.getName());
//                ps.setInt(2, examTypeManager.getSessionId());
//                ps.setString(3, examTypeManager.getDescription());
//                return ps;
//            }, keyHolder);
//            Map<String, Object> keys = keyHolder.getKeys();
//            if (keys != null && keys.containsKey("exam_type_id")) {
//                int generatedKey = ((Number) keys.get("exam_type_id")).intValue();
//                examTypeManager.setExamTypeId(generatedKey);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//        return examTypeManager;
//    }

    @Override
    public List<ExamTypeManager> getAllExamType(int sessionId, String schoolCode) throws Exception {
        String sql="select exam_type_id,name,description,category,weightage_percent from exam_type where session_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        System.out.println(sessionId);
        List<ExamTypeManager> examTypeManagers=null;
        try{
            examTypeManagers=jdbcTemplate.query(sql, new Object[]{sessionId},new RowMapper<ExamTypeManager>() {
                @Override
                public ExamTypeManager mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamTypeManager etm=new ExamTypeManager();
                    etm.setExamTypeId(rs.getInt("exam_type_id"));
                    etm.setName(rs.getString("name"));
                    etm.setDescription(rs.getString("description"));
                    etm.setCategory(rs.getString("category"));
                    etm.setWeightagePercent(rs.getDouble("weightage_percent"));
                    return etm;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);

        }
        return examTypeManagers;
    }

    @Override
    public ExamTypeManager getExamTypeById(int examTypeId, String schoolCode) throws Exception {
        String sql = "SELECT exam_type_id, school_id, session_id, name, description, " +
                "category, weightage_percent, passing_marks_percent, grading_system, " +
                "exam_options, status " +
                "FROM exam_type WHERE exam_type_id = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ExamTypeManager examTypeManager = null;

        try {
            examTypeManager = jdbcTemplate.queryForObject(sql, new Object[]{examTypeId}, new ExamTypeRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return examTypeManager;
    }

    // Inner class RowMapper
    private static class ExamTypeRowMapper implements RowMapper<ExamTypeManager> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public ExamTypeManager mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExamTypeManager etm = new ExamTypeManager();

            etm.setExamTypeId(rs.getInt("exam_type_id"));
            etm.setSchoolId(rs.getInt("school_id"));
            etm.setSessionId(rs.getInt("session_id"));
            etm.setName(rs.getString("name"));
            etm.setDescription(rs.getString("description"));
            etm.setCategory(rs.getString("category"));

            // Handle weightage_percent
            double weightagePercent = rs.getDouble("weightage_percent");
            etm.setWeightagePercent(rs.wasNull() ? null : weightagePercent);

            // Handle passing_marks_percent
            double passingMarksPercent = rs.getDouble("passing_marks_percent");
            etm.setPassingMarksPercent(rs.wasNull() ? null : passingMarksPercent);

            etm.setGradingSystem(rs.getString("grading_system"));

            // Handle exam_options JSONB
            String examOptionsJson = rs.getString("exam_options");
            if (examOptionsJson != null && !examOptionsJson.isEmpty()) {
                try {
                    JsonNode examOptions = objectMapper.readTree(examOptionsJson);
                    etm.setExamOptions(examOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    etm.setExamOptions(null);
                }
            } else {
                etm.setExamOptions(null);
            }

            etm.setStatus(rs.getString("status"));

            return etm;
        }
    }
    @Override
    public ExamTypeManager updateById(ExamTypeManager examTypeManager, int examTypeId, String schoolCode) throws Exception {

        // examOptions ko pehle hi convert kar lo lambda ke bahar
        String examOptionsJson = null;
        if (examTypeManager.getExamOptions() != null) {
            examOptionsJson = objectMapper.writeValueAsString(examTypeManager.getExamOptions());
        }
        final String finalExamOptionsJson = examOptionsJson;

        String sql = "UPDATE exam_type SET " +
                "name = ?, " +
                "description = ?, " +
                "category = ?, " +
                "weightage_percent = ?, " +
                "passing_marks_percent = ?, " +
                "grading_system = ?, " +
                "exam_options = ?::jsonb, " +
                "status = ?, " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE exam_type_id = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rowAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, examTypeManager.getName());
                ps.setString(2, examTypeManager.getDescription());

                ps.setString(3, examTypeManager.getCategory() != null
                        ? examTypeManager.getCategory() : "major");

                if (examTypeManager.getWeightagePercent() != null) {
                    ps.setDouble(4, examTypeManager.getWeightagePercent());
                } else {
                    ps.setNull(4, java.sql.Types.DECIMAL);
                }

                if (examTypeManager.getPassingMarksPercent() != null) {
                    ps.setDouble(5, examTypeManager.getPassingMarksPercent());
                } else {
                    ps.setNull(5, java.sql.Types.DECIMAL);
                }

                ps.setString(6, examTypeManager.getGradingSystem() != null
                        ? examTypeManager.getGradingSystem() : "CBSE Standard Grade");

                // ab lambda ke andar koi exception nahi
                if (finalExamOptionsJson != null) {
                    ps.setString(7, finalExamOptionsJson);
                } else {
                    ps.setNull(7, java.sql.Types.OTHER);
                }

                ps.setString(8, examTypeManager.getStatus() != null
                        ? examTypeManager.getStatus() : "ACTIVE");

                ps.setInt(9, examTypeId);

                return ps;
            });

            if (rowAffected > 0) {
                examTypeManager.setExamTypeId(examTypeId);
                return examTypeManager;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
