package com.sms.dao.impl;

import com.sms.dao.ExamMarksEntryDao;
import com.sms.model.ExamMarksEntryDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ExamMarksEntryDaoImpl implements ExamMarksEntryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExamMarksEntryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExamMarksEntryDetails addExamMarks(ExamMarksEntryDetails examMarksEntryDetails, String schoolCode) throws Exception {
        String sql = "INSERT INTO exam_marks_entry(school_id, class_id, section_id, student_id, session_id, exam_type, subject_id, maximum_marks, minimum_marks, obtain, grade, status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, examMarksEntryDetails.getSchoolId());
                    ps.setInt(2, examMarksEntryDetails.getClassId());
                    ps.setInt(3, examMarksEntryDetails.getSectionId());
                    ps.setInt(4, examMarksEntryDetails.getStudentId());
                    ps.setInt(5, examMarksEntryDetails.getSessionId());
                    ps.setString(6, examMarksEntryDetails.getExamType());
                    ps.setInt(7, examMarksEntryDetails.getSubjectId());
                    ps.setDouble(8, examMarksEntryDetails.getMaximumMarks());
                    ps.setDouble(9, examMarksEntryDetails.getMinimumMarks());
                    ps.setDouble(10, examMarksEntryDetails.getObtain());
                    ps.setString(11, examMarksEntryDetails.getGrade());
                    ps.setString(12, examMarksEntryDetails.getStatus());
                    return ps;
                }, keyHolder);
                Map<String, Object> keys = keyHolder.getKeys();
                if (keys != null && keys.containsKey("eme_id")) {
                    int generatedId = ((Number) keys.get("eme_id")).intValue();
                    examMarksEntryDetails.setEmeId(generatedId);
                }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examMarksEntryDetails;
    }

    @Override
    public ExamMarksEntryDetails getExamMarksById(int emeId, String schoolCode) throws Exception {
        String sql = "SELECT eme.eme_id, eme.school_id, sd.school_name, eme.class_id, mc.class_name, eme.section_id, ms.section_name, eme.session_id, s.academic_session AS session_name, eme.student_id, CONCAT(spd.first_name, ' ', spd.last_name) AS student_name, eme.exam_type, eme.subject_id, msj.subject_name, eme.maximum_marks, eme.minimum_marks, eme.obtain, eme.grade, eme.status FROM exam_marks_entry eme JOIN school_details sd ON eme.school_id = sd.school_id JOIN mst_class mc ON eme.class_id = mc.class_id JOIN mst_section ms ON eme.section_id = ms.section_id JOIN session s ON eme.session_id = s.session_id JOIN student_personal_details spd ON eme.student_id = spd.student_id JOIN mst_subject msj ON eme.subject_id = msj.subject_id where eme.eme_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ExamMarksEntryDetails examMarksEntryDetails = null;
        try{
            examMarksEntryDetails = jdbcTemplate.queryForObject(sql, new Object[]{emeId}, new RowMapper<ExamMarksEntryDetails>() {
                @Override
                public ExamMarksEntryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamMarksEntryDetails emed = new ExamMarksEntryDetails();
                    emed.setEmeId(rs.getInt("eme_id"));
                    emed.setSchoolId(rs.getInt("school_id"));
                    emed.setSchoolName(rs.getString("school_name"));
                    emed.setClassId(rs.getInt("class_id"));
                    emed.setClassName(rs.getString("class_name"));
                    emed.setSectionId(rs.getInt("section_id"));
                    emed.setSectionName(rs.getString("section_name"));
                    emed.setSessionId(rs.getInt("session_id"));
                    emed.setSessionName(rs.getString("session_name"));
                    emed.setStudentId(rs.getInt("student_id"));
                    emed.setStudentName(rs.getString("student_name"));
                    emed.setExamType(rs.getString("exam_type"));
                    emed.setSubjectId(rs.getInt("subject_id"));
                    emed.setSubjectName(rs.getString("subject_name"));
                    emed.setMaximumMarks(rs.getDouble("maximum_marks"));
                    emed.setMinimumMarks(rs.getDouble("minimum_marks"));
                    emed.setObtain(rs.getDouble("obtain"));
                    emed.setGrade(rs.getString("grade"));
                    emed.setStatus(rs.getString("status"));
                    return emed;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examMarksEntryDetails;
    }

    @Override
    public List<ExamMarksEntryDetails> getAllExamMarks(String schoolCode) throws Exception {
        String sql = "SELECT eme.eme_id, eme.school_id, sd.school_name, eme.class_id, mc.class_name, eme.section_id, ms.section_name, eme.session_id, s.academic_session AS session_name, eme.student_id, CONCAT(spd.first_name, ' ', spd.last_name) AS student_name, eme.exam_type, eme.subject_id, msj.subject_name, eme.maximum_marks, eme.minimum_marks, eme.obtain, eme.grade, eme.status FROM exam_marks_entry eme JOIN school_details sd ON eme.school_id = sd.school_id JOIN mst_class mc ON eme.class_id = mc.class_id JOIN mst_section ms ON eme.section_id = ms.section_id JOIN session s ON eme.session_id = s.session_id JOIN student_personal_details spd ON eme.student_id = spd.student_id JOIN mst_subject msj ON eme.subject_id = msj.subject_id order by eme.eme_id asc ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ExamMarksEntryDetails> examMarksEntryDetails = null;
        try{
            examMarksEntryDetails = jdbcTemplate.query(sql, new RowMapper<ExamMarksEntryDetails>() {
                @Override
                public ExamMarksEntryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamMarksEntryDetails emed = new ExamMarksEntryDetails();
                    emed.setEmeId(rs.getInt("eme_id"));
                    emed.setSchoolId(rs.getInt("school_id"));
                    emed.setSchoolName(rs.getString("school_name"));
                    emed.setClassId(rs.getInt("class_id"));
                    emed.setClassName(rs.getString("class_name"));
                    emed.setSectionId(rs.getInt("section_id"));
                    emed.setSectionName(rs.getString("section_name"));
                    emed.setSessionId(rs.getInt("session_id"));
                    emed.setSessionName(rs.getString("session_name"));
                    emed.setStudentId(rs.getInt("student_id"));
                    emed.setStudentName(rs.getString("student_name"));
                    emed.setExamType(rs.getString("exam_type"));
                    emed.setSubjectId(rs.getInt("subject_id"));
                    emed.setSubjectName(rs.getString("subject_name"));
                    emed.setMaximumMarks(rs.getDouble("maximum_marks"));
                    emed.setMinimumMarks(rs.getDouble("minimum_marks"));
                    emed.setObtain(rs.getDouble("obtain"));
                    emed.setGrade(rs.getString("grade"));
                    emed.setStatus(rs.getString("status"));
                    return emed;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examMarksEntryDetails;
    }

    @Override
    public ExamMarksEntryDetails updateExamMarks(ExamMarksEntryDetails examMarksEntryDetails, int emeId, String schoolCode) throws Exception {
        String sql = "update exam_marks_entry set class_id = ?, section_id = ?, student_id = ?, session_id = ?, exam_type = ?, subject_id = ?, maximum_marks = ?, minimum_marks = ?, obtain = ?, grade = ?, status = ? where eme_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    examMarksEntryDetails.getClassId(),
                    examMarksEntryDetails.getSectionId(),
                    examMarksEntryDetails.getStudentId(),
                    examMarksEntryDetails.getSessionId(),
                    examMarksEntryDetails.getExamType(),
                    examMarksEntryDetails.getSubjectId(),
                    examMarksEntryDetails.getMaximumMarks(),
                    examMarksEntryDetails.getMinimumMarks(),
                    examMarksEntryDetails.getObtain(),
                    examMarksEntryDetails.getGrade(),
                    examMarksEntryDetails.getStatus(),
                    emeId
            );
            if(rowAffected > 0 ){
                return examMarksEntryDetails;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean deleteExamMarks(int emeId, String schoolCode) throws Exception {
        String sql = "delete from exam_marks_entry where eme_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{emeId});
            if(rowAffected > 0 ){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
