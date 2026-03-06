package com.sms.dao.impl;

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

    public ExamTypeManagerDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExamTypeManager addExamType(ExamTypeManager examTypeManager, String schoolCode) throws Exception {
        String sql="Insert into exam_type (name,session_id,description) values(?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, examTypeManager.getName());
                ps.setInt(2, examTypeManager.getSessionId());
                ps.setString(3, examTypeManager.getDescription());
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
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examTypeManager;
    }

    @Override
    public List<ExamTypeManager> getAllExamType(int sessionId, String schoolCode) throws Exception {
        String sql="select exam_type_id,name,description from exam_type where session_id=?";
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
        String sql="select exam_type_id,name,description from exam_type where exam_type_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ExamTypeManager examTypeManagers=null;
        try{
            examTypeManagers=jdbcTemplate.queryForObject(sql, new Object[]{examTypeId},new RowMapper<ExamTypeManager>() {
                @Override
                public ExamTypeManager mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamTypeManager etm=new ExamTypeManager();
                    etm.setExamTypeId(rs.getInt("exam_type_id"));
                    etm.setName(rs.getString("name"));
                    etm.setDescription(rs.getString("description"));
                    return etm;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);

        }
        return examTypeManagers;
    }

    @Override
    public ExamTypeManager updateById(ExamTypeManager examTypeManager, int examTypeId, String schoolCode) throws Exception {
        String sql = "update exam_type set name = ?, description = ? where exam_type_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    examTypeManager.getName(),
                    examTypeManager.getDescription(),
                    examTypeId);
            if(rowAffected > 0){
                return examTypeManager;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
