package com.sms.dao.impl;

import com.sms.dao.MarkSheetDao;
import com.sms.model.MarkSheetDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MarkSheetDaoImpl implements MarkSheetDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MarkSheetDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
   @Override
   public List<MarkSheetDetails> searchAllStudentMarks(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
       String sql = "select\n" +
               "    sd.school_name,\n" +
               "    spd.student_id,\n" +
               "    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,\n" +
               "    mc.class_name,\n" +
               "    mst.section_name,\n" +
               "    s.academic_session as session_name,\n" +
               "    eme.subject_id,\n" +
               "    ms.subject_name,\n" +
               "    eme.exam_type, \n" +
               "    eme.maximum_marks,\n" +
               "    eme.minimum_marks,\n" +
               "    eme.obtain,\n" +
               "    eme.grade,\n" +
               "    eme.status,\n" +
               "    SUM(eme.maximum_marks) OVER (PARTITION BY spd.student_id) AS total_maximum_marks,\n" +
               "    SUM(eme.obtain) OVER (PARTITION BY spd.student_id) AS total_obtain_marks,\n" +
               "    ROUND(\n" +
               "        (SUM(eme.obtain) OVER (PARTITION BY spd.student_id)::numeric / SUM(eme.maximum_marks) OVER (PARTITION BY spd.student_id)::numeric) * 100,\n" +
               "        2\n" +
               "    ) AS percentage\n" +
               "FROM\n" +
               "    exam_marks_entry eme\n" +
               "JOIN\n" +
               "    student_personal_details spd ON eme.student_id = spd.student_id \n" +
               "JOIN\n" +
               "    student_academic_details sad ON eme.student_id = sad.student_id \n" +
               "JOIN\n" +
               "    mst_subject ms ON eme.subject_id = ms.subject_id \n" +
               "JOIN\n" +
               "    school_details sd ON eme.school_id = sd.school_id \n" +
               "JOIN\n" +
               "    mst_class mc  ON eme.class_id = mc.class_id  \n" +
               "JOIN\n" +
               "    mst_section mst  ON eme.section_id = mst.section_id  \n" +
               "JOIN\n" +
               "    session s  ON eme.session_id = s.session_id       \n" +
               "WHERE\n" +
               "    eme.class_id = ?\n" +
               "    AND eme.section_id = ?\n" +
               "    AND eme.session_id = ?\n" +
               "ORDER BY\n" +
               "    spd.student_id, eme.subject_id";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<MarkSheetDetails> markSheetDetails = null;
       try{
           markSheetDetails = jdbcTemplate.query(sql, new Object[]{classId, sectionId, sessionId}, new RowMapper<MarkSheetDetails>() {
               @Override
               public MarkSheetDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   MarkSheetDetails msd = new MarkSheetDetails();
                   msd.setSchoolName(rs.getString("school_name"));
                   msd.setStudentId(rs.getInt("student_id"));
                   msd.setStudentName(rs.getString("student_name"));
                   msd.setClassName(rs.getString("class_name"));
                   msd.setSectionName(rs.getString("section_name"));
                   msd.setSessionName(rs.getString("session_name"));
                   msd.setSubjectId(rs.getInt("subject_id"));
                   msd.setSubjectName(rs.getString("subject_name"));
                   msd.setExamType(rs.getString("exam_type"));
                   msd.setMaximumMarks(rs.getDouble("maximum_marks"));
                   msd.setMinimumMarks(rs.getDouble("minimum_marks"));
                   msd.setObtain(rs.getDouble("obtain"));
                   msd.setGrade(rs.getString("grade"));
                   msd.setStatus(rs.getString("status"));
                   msd.setTotalMaximumMarks(rs.getDouble("total_maximum_marks"));
                   msd.setTotalObtainMarks(rs.getDouble("total_obtain_marks"));
                   msd.setPercentage(rs.getDouble("percentage"));
                   return msd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return markSheetDetails;
   }
}
