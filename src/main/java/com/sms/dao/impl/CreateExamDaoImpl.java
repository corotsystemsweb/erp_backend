package com.sms.dao.impl;
// ExamDaoImpl.java
import com.sms.dao.CreateExamDao;
import com.sms.model.CreateExamDetails;
import com.sms.model.ExamSubjectsDetails;
import com.sms.model.PaySalaryDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.sms.dao.impl.DynamicStudentInsertDaoImpl.logger;

@Repository
public class CreateExamDaoImpl implements CreateExamDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int createExam(CreateExamDetails exam,String schoolCode) {
        String sql = "INSERT INTO exams (exam_type_id, session_id, class_id, section_id, " +
                "start_date, end_date, status, created_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        KeyHolder keyHolder = new GeneratedKeyHolder();
  try{
      jdbcTemplate.update(connection -> {
          // Specify the column name(s) we want to return
          PreparedStatement ps = connection.prepareStatement(
                  sql,
                  new String[]{"exam_id"} // Explicitly request only exam_id
          );
          ps.setInt(1, exam.getExamTypeId());
          ps.setInt(2, exam.getSessionId());
          ps.setInt(3, exam.getClassId());
          ps.setObject(4, exam.getSectionId());
          ps.setDate(5, exam.getStartDate());
          ps.setDate(6, exam.getEndDate());
          ps.setString(7, exam.getStatus());
          ps.setTimestamp(8, exam.getCreatedDate());
          return ps;
      }, keyHolder);
  }catch (Exception e){
      e.printStackTrace();
      throw e; // Re-throw the exception for higher-level handling
  } finally {
      DatabaseUtil.closeDataSource(jdbcTemplate);
  }
        return keyHolder.getKey().intValue();
    }

    @Override
    public void createExamSubject(ExamSubjectsDetails subject, String schoolCode) {
        String sql = "INSERT INTO exam_subjects (exam_id, subject_id, theory_max_marks, " +
                "practical_max_marks, viva_max_marks, passing_marks, exam_date, " +
                "start_time, end_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.update(sql,
                    subject.getExamId(),
                    subject.getSubjectId(),
                    subject.getTheoryMaxMarks(),
                    subject.getPracticalMaxMarks(),
                    subject.getVivaMaxMarks(),
                    subject.getPassingMarks(),
                    subject.getExamDate(),
                    subject.getStartTime(),
                    subject.getEndTime());
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<CreateExamDetails> getAllExamDetails(int sessionId, String schoolCode,
                                                     Integer classId, Integer sectionId,
                                                     String examName) {
        // Base SQL with mandatory conditions
        String baseSql = """
    SELECT
        e.exam_id,
        e.exam_type_id,
        et.name,
        s.academic_session,
        mc.class_name,
        ms.section_name,
        e.class_id,
        e.section_id,
        e.start_date,
        e.end_date,
        e.status
    FROM exams AS e
    JOIN session s ON e.session_id::INTEGER = s.session_id::INTEGER
    JOIN mst_class mc ON e.class_id::INTEGER = mc.class_id::INTEGER
    JOIN mst_section ms ON e.section_id::INTEGER = ms.section_id::INTEGER
    JOIN exam_type et ON e.exam_type_id = et.exam_type_id
    WHERE e.deleted IS NOT TRUE
    AND e.session_id::INTEGER = ?
    """;

        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        List<Object> params = new ArrayList<>();
        params.add(sessionId);

        if (classId != null) {
            sqlBuilder.append(" AND e.class_id::INTEGER = ?"); // Cast to INTEGER
            params.add(classId);
        }

        if (sectionId != null) {
            sqlBuilder.append(" AND e.section_id::INTEGER = ?"); // Cast to INTEGER
            params.add(sectionId);
        }

        if (StringUtils.hasText(examName)) {
            sqlBuilder.append(" AND et.name ILIKE ?"); // Use exam type's name
            params.add("%" + examName.trim() + "%");
        }

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<CreateExamDetails> examDetailsList = new ArrayList<>();

        try {
            examDetailsList = jdbcTemplate.query(
                    sqlBuilder.toString(),
                    params.toArray(),
                    new RowMapper<CreateExamDetails>() {
                        @Override
                        public CreateExamDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                            CreateExamDetails details = new CreateExamDetails();
                            details.setExamId(rs.getInt("exam_id"));
                            details.setExamTypeId(rs.getInt("exam_type_id"));
                            details.setName(rs.getString("name"));
                            details.setAcademicSession(rs.getString("academic_session"));
                            details.setClassName(rs.getString("class_name"));
                            details.setSectionName(rs.getString("section_name"));
                            details.setClassId(rs.getInt("class_id"));
                            details.setSectionId(rs.getInt("section_id"));
                            details.setStartDate(rs.getDate("start_date"));
                            details.setEndDate(rs.getDate("end_date"));
                            details.setStatus(rs.getString("status"));
                            return details;
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("Error fetching exams: ", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examDetailsList;
    }

    @Override
    public List<ExamSubjectsDetails> getExamTimeTable(int examId, String schoolCode) {
        String sql= """
                SELECT
                    es.exam_subject_id,
                    es.exam_date,
                    ms.subject_name,
                    es.subject_id,
                    es.start_time,
                    es.end_time,
                    e.class_id,
                    mc.class_name,
                    e.section_id,
                    msct.section_name,
                    e.exam_type_id,
                    et.name
                FROM
                    exam_subjects es
                JOIN
                    mst_subject ms ON es.subject_id = ms.subject_id
                JOIN
                    exams e ON es.exam_id = e.exam_id
                JOIN
                    mst_class mc ON e.class_id = mc.class_id
                JOIN
                    mst_section msct ON e.section_id = msct.section_id
                JOIN
                    exam_type et ON e.exam_type_id=et.exam_type_id
                WHERE
                    es.exam_id = ?
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ExamSubjectsDetails> examSubjectsDetails=null;
        try{
            examSubjectsDetails = jdbcTemplate.query(sql, new Object[]{examId},new RowMapper<ExamSubjectsDetails>() {
                @Override
                public ExamSubjectsDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamSubjectsDetails esd = new ExamSubjectsDetails();
                    esd.setExamSubjectId(rs.getInt("exam_subject_id"));
                    esd.setExamDate(rs.getDate("exam_date"));
                    esd.setSubjectName(rs.getString("subject_name"));
                    esd.setSubjectId(rs.getInt("subject_id"));
                    esd.setStartTime(rs.getTime("start_time"));
                    esd.setEndTime(rs.getTime("end_time"));
                    esd.setClassName(rs.getString("class_name"));
                    esd.setSectionName(rs.getString("section_name"));
                    esd.setName(rs.getString("name"));
                    esd.setExamTypeId(rs.getInt("exam_type_id"));
                    return esd;
                }
            });
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examSubjectsDetails;
    }
}