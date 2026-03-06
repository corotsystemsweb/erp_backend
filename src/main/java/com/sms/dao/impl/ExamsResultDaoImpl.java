package com.sms.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.ExamsResultDao;
import com.sms.model.ExamResultDetails;
import com.sms.model.ExpenseType;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class ExamsResultDaoImpl implements ExamsResultDao {
    private final JdbcTemplate jdbcTemplate;

    public ExamsResultDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ExamResultDetails> findAllStudentExamResults(Integer sessionId,Integer classId,Integer sectionId,String schoolCode) {
        StringBuilder sql = new StringBuilder( """
                    WITH student_exam_summary AS (
                                       SELECT
                                           er.student_id,
                                           e.exam_id,
                                           e.class_id,
                                           e.section_id,
                                           e.session_id,
                                           SUM(er.total_marks) AS obtained_marks,
                                           COUNT(CASE WHEN er.total_marks < es.passing_marks THEN 1 END) AS failed_subjects
                                       FROM exam_results er
                                       JOIN exam_subjects es ON er.exam_subject_id = es.exam_subject_id
                                       JOIN exams e ON es.exam_id = e.exam_id
                                       GROUP BY er.student_id, e.exam_id, e.class_id, e.section_id, e.session_id
                                   ),
                
                                   exam_max_marks AS (
                                       SELECT
                                           exam_id,
                                           SUM(
                                               COALESCE(theory_max_marks, 0) +
                                               COALESCE(practical_max_marks, 0) +
                                               COALESCE(viva_max_marks, 0)
                                           ) AS total_max_marks
                                       FROM exam_subjects
                                       GROUP BY exam_id
                                   )
                
                                   SELECT
                                       s.academic_session,  -- Fetch session name instead of ID
                                       spd.first_name AS student_name,
                                       spd.student_id,
                                       mc.class_name,
                                       mc.class_id,
                                       ms.section_id,
                                       ms.section_name,
                                       ex.exam_type_id,
                                       et.name,
                                       ses.obtained_marks,
                                       emm.total_max_marks,
                                       ROUND((ses.obtained_marks::DECIMAL / emm.total_max_marks) * 100, 2) AS percentage,
                
                                       -- Grade Calculation
                                       (
                                           SELECT grade_name
                                           FROM mst_grade
                                           WHERE ROUND((ses.obtained_marks::DECIMAL / emm.total_max_marks) * 100, 2)
                                               BETWEEN min_percentage AND max_percentage
                                           LIMIT 1
                                       ) AS grade,
                
                                       CASE
                                           WHEN ses.failed_subjects = 0 THEN 'Pass'
                                           ELSE 'Fail'
                                       END AS result_status,
                
                                       RANK() OVER (
                                           PARTITION BY ses.class_id, ses.section_id
                                           ORDER BY ses.obtained_marks DESC
                                       ) AS class_position
                
                                   FROM student_exam_summary ses
                                   JOIN exam_max_marks emm ON ses.exam_id = emm.exam_id
                                   JOIN student_personal_details spd ON ses.student_id = spd.student_id
                                   JOIN mst_class mc ON ses.class_id = mc.class_id
                                   LEFT JOIN mst_section ms ON ses.section_id = ms.section_id
                                   JOIN exams ex ON ses.exam_id = ex.exam_id
                                   JOIN exam_type et ON ex.exam_type_id = et.exam_type_id
                                   JOIN session s ON ex.session_id = s.session_id
                                   WHERE
                """);
        List<Object> params = new ArrayList<>();
        if (sessionId != null) {
            sql.append("ses.session_id = ?");
            params.add(sessionId);
        }
        if (classId != null) {
            sql.append(" AND ses.class_id = ?");
            params.add(classId);
        }
        if (sectionId != null) {
            sql.append(" AND ses.section_id = ?");
            params.add(sectionId);
        }

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql.toString(), params.toArray(), new BeanPropertyRowMapper<>(ExamResultDetails.class));
        } catch (DataAccessException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Returning an empty list to avoid breaking the application
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<ExamResultDetails> findStudentResultWithSubject(Integer classId, Integer sectionId, String schoolCode) {
        String sql = """
         WITH
student_subject_scores AS (
    SELECT
        er.student_id,
        e.exam_id,
        e.class_id,
        e.section_id,
        subj.subject_id,
        subj.subject_name,
        er.theory_marks,
        er.practical_marks,
        er.viva_marks,
        er.total_marks AS subject_obtained_marks,
        es.passing_marks AS subject_passing_marks,
        (es.theory_max_marks + es.practical_max_marks + es.viva_max_marks) AS subject_max_marks,
        (er.total_marks::DECIMAL / (es.theory_max_marks + es.practical_max_marks + es.viva_max_marks) * 100) AS subject_percentage
    FROM exam_results er
    JOIN exam_subjects es ON er.exam_subject_id = es.exam_subject_id
    JOIN mst_subject subj ON es.subject_id = subj.subject_id
    JOIN exams e ON es.exam_id = e.exam_id
),

class_summary AS (
    SELECT
        student_id,
        exam_id,
        class_id,
        section_id,
        SUM(subject_obtained_marks) AS total_obtained_marks,
        COUNT(CASE WHEN subject_obtained_marks < subject_passing_marks THEN 1 END) AS failed_subjects,
        RANK() OVER (
            PARTITION BY class_id, section_id
            ORDER BY SUM(subject_obtained_marks) DESC
        ) AS class_position
    FROM student_subject_scores
    GROUP BY student_id, exam_id, class_id, section_id
),

exam_total_marks AS (
    SELECT
        exam_id,
        SUM(
            COALESCE(theory_max_marks, 0) +
            COALESCE(practical_max_marks, 0) +
            COALESCE(viva_max_marks, 0)
        ) AS total_max_marks
    FROM exam_subjects
    GROUP BY exam_id
),

student_overall_grades AS (
    SELECT
        cs.*,
        etm.total_max_marks,
        (cs.total_obtained_marks::DECIMAL / etm.total_max_marks * 100) AS total_percentage
    FROM class_summary cs
    JOIN exam_total_marks etm ON cs.exam_id = etm.exam_id
)

SELECT\s
    spd.student_id,
    spd.first_name AS student_name,
    mc.class_name,
    ms.section_name,
    et.name AS exam_type_name,
    sog.total_obtained_marks,
    sog.total_max_marks,
    ROUND(sog.total_percentage, 2) AS total_percentage,
    og.grade_name AS overall_grade,
    CASE\s
        WHEN sog.failed_subjects = 0 THEN 'Pass'\s
        ELSE 'Fail'\s
    END AS overall_result,
    sog.class_position,
    jsonb_agg(jsonb_build_object(
        'subject_name', ss.subject_name,
        'theory_marks', ss.theory_marks,
        'practical_marks', ss.practical_marks,
        'viva_marks', ss.viva_marks,
        'obtained_marks', ss.subject_obtained_marks,
        'passing_marks', ss.subject_passing_marks,
        'subject_grade', sg.grade_name
    )) AS subjects
FROM student_subject_scores ss
JOIN student_overall_grades sog USING (student_id, exam_id, class_id, section_id)
JOIN student_personal_details spd ON ss.student_id = spd.student_id
JOIN mst_class mc ON ss.class_id = mc.class_id
LEFT JOIN mst_section ms ON ss.section_id = ms.section_id
JOIN exams ex ON ss.exam_id = ex.exam_id
JOIN exam_type et ON ex.exam_type_id = et.exam_type_id
JOIN mst_grade sg ON ss.subject_percentage BETWEEN sg.min_percentage AND sg.max_percentage
JOIN mst_grade og ON sog.total_percentage BETWEEN og.min_percentage AND og.max_percentage
WHERE
    mc.class_id = ?
    AND (ms.section_id = ? OR ? IS NULL)
GROUP BY\s
    spd.student_id,
    spd.first_name,
    mc.class_name,
    ms.section_name,
    et.name,
    sog.total_obtained_marks,
    sog.total_max_marks,
    sog.total_percentage,
    og.grade_name,
    sog.failed_subjects,
    sog.class_position
ORDER BY
    sog.class_position,
    student_name;
   \s""";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, new Object[]{classId, sectionId, sectionId}, new RowMapper<ExamResultDetails>() {
                @Override
                public ExamResultDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamResultDetails et = new ExamResultDetails();
                    et.setStudentName(rs.getString("student_name"));
                    et.setClassName(rs.getString("class_name"));
                    et.setSectionName(rs.getString("section_name"));
                    et.setName(rs.getString("exam_type_name"));

                    et.setTotalObtainedMarks(rs.getInt("total_obtained_marks"));
                    et.setTotalMaxMarks(rs.getInt("total_max_marks"));
                    et.setTotalPercentage(rs.getDouble("total_percentage"));
                    et.setOverAllGrade(rs.getString("overall_grade"));
                    et.setOverResult(rs.getString("overall_result"));
                    et.setClassPosition(rs.getInt("class_position"));

                    // Parsing JSON subjects array
                    // Parsing JSON subjects array
                    String subjectsJson = rs.getString("subjects");
                    if (subjectsJson != null && !subjectsJson.isEmpty()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            List<SubjectDetails> subjects = objectMapper.readValue(subjectsJson, new TypeReference<List<SubjectDetails>>() {});
                            et.setSubjects(subjects);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error parsing subjects JSON", e);
                        }
                    }
                    return et;
                }
            });
        } catch (DataAccessException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public ExamResultDetails resultForParticularStudent(Integer classId, Integer sectionId, Integer studentId, Integer examTypeId, String schoolCode) {
        String sql = """
        WITH
                              student_subject_scores AS (
                                  SELECT
                                      er.student_id,
                                      e.exam_id,
                                      e.class_id,
                                      e.section_id,
                                      subj.subject_id,
                                      subj.subject_name,
                                      er.theory_marks,
                                      er.practical_marks,
                                      er.viva_marks,
                                      er.total_marks AS subject_obtained_marks,
                                      es.passing_marks AS subject_passing_marks,
                                      (es.theory_max_marks + es.practical_max_marks + es.viva_max_marks) AS subject_max_marks,
                                      (er.total_marks::DECIMAL / (es.theory_max_marks + es.practical_max_marks + es.viva_max_marks) * 100) AS subject_percentage
                                  FROM exam_results er
                                  JOIN exam_subjects es ON er.exam_subject_id = es.exam_subject_id
                                  JOIN mst_subject subj ON es.subject_id = subj.subject_id
                                  JOIN exams e ON es.exam_id = e.exam_id
                              ),
                
                              class_summary AS (
                                  SELECT
                                      student_id,
                                      exam_id,
                                      class_id,
                                      section_id,
                                      SUM(subject_obtained_marks) AS total_obtained_marks,
                                      COUNT(CASE WHEN subject_obtained_marks < subject_passing_marks THEN 1 END) AS failed_subjects,
                                      RANK() OVER (
                                          PARTITION BY class_id, section_id
                                          ORDER BY SUM(subject_obtained_marks) DESC
                                      ) AS class_position
                                  FROM student_subject_scores
                                  GROUP BY student_id, exam_id, class_id, section_id
                              ),
                
                              exam_total_marks AS (
                                  SELECT
                                      exam_id,
                                      SUM(
                                          COALESCE(theory_max_marks, 0) +
                                          COALESCE(practical_max_marks, 0) +
                                          COALESCE(viva_max_marks, 0)
                                      ) AS total_max_marks
                                  FROM exam_subjects
                                  GROUP BY exam_id
                              ),
                
                              student_overall_grades AS (
                                  SELECT
                                      cs.*,
                                      etm.total_max_marks,
                                      (cs.total_obtained_marks::DECIMAL / etm.total_max_marks * 100) AS total_percentage
                                  FROM class_summary cs
                                  JOIN exam_total_marks etm ON cs.exam_id = etm.exam_id
                              )
                
                              SELECT
                                  spd.student_id,
                                  spd.first_name AS student_name,
                                  mc.class_name,
                                  ms.section_name,
                                  et.name AS exam_type_name,
                                  sog.total_obtained_marks,
                                  sog.total_max_marks,
                                  ROUND(sog.total_percentage, 2) AS total_percentage,
                                  og.grade_name AS overall_grade,
                                  CASE
                                      WHEN sog.failed_subjects = 0 THEN 'Pass'
                                      ELSE 'Fail'
                                  END AS overall_result,
                                  sog.class_position,
                                  jsonb_agg(jsonb_build_object(
                                      'subject_name', ss.subject_name,
                                      'theory_marks', ss.theory_marks,
                                      'practical_marks', ss.practical_marks,
                                      'viva_marks', ss.viva_marks,
                                      'obtained_marks', ss.subject_obtained_marks,
                                      'passing_marks', ss.subject_passing_marks,
                                      'subject_grade', sg.grade_name
                                  )) AS subjects
                              FROM student_subject_scores ss
                              JOIN student_overall_grades sog USING (student_id, exam_id, class_id, section_id)
                              JOIN student_personal_details spd ON ss.student_id = spd.student_id
                              JOIN mst_class mc ON ss.class_id = mc.class_id
                              LEFT JOIN mst_section ms ON ss.section_id = ms.section_id
                              JOIN exams ex ON ss.exam_id = ex.exam_id
                              JOIN exam_type et ON ex.exam_type_id = et.exam_type_id
                              JOIN mst_grade sg ON ss.subject_percentage BETWEEN sg.min_percentage AND sg.max_percentage
                              JOIN mst_grade og ON sog.total_percentage BETWEEN og.min_percentage AND og.max_percentage
                              WHERE
                                  mc.class_id = ?
                                  AND (ms.section_id = ? OR ? IS NULL)
                                  and spd.student_id =?
                                  and ex.exam_type_id=?
                              GROUP BY
                                  spd.student_id,
                                  spd.first_name,
                                  mc.class_name,
                                  ms.section_name,
                                  et.name,
                                  sog.total_obtained_marks,
                                  sog.total_max_marks,
                                  sog.total_percentage,
                                  og.grade_name,
                                  sog.failed_subjects,
                                  sog.class_position
                              ORDER BY
                                  sog.class_position,
                                  student_name
                                  """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{classId, sectionId, sectionId,studentId,examTypeId}, new RowMapper<ExamResultDetails>() {
                @Override
                public ExamResultDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamResultDetails et = new ExamResultDetails();
                    et.setStudentName(rs.getString("student_name"));
                    et.setClassName(rs.getString("class_name"));
                    et.setSectionName(rs.getString("section_name"));
                    et.setName(rs.getString("exam_type_name"));
                    et.setTotalObtainedMarks(rs.getInt("total_obtained_marks"));
                    et.setTotalMaxMarks(rs.getInt("total_max_marks"));
                    et.setTotalPercentage(rs.getDouble("total_percentage"));
                    et.setOverAllGrade(rs.getString("overall_grade"));
                    et.setOverResult(rs.getString("overall_result"));
                    et.setClassPosition(rs.getInt("class_position"));

                    // Parsing JSON subjects array
                    // Parsing JSON subjects array
                    String subjectsJson = rs.getString("subjects");
                    if (subjectsJson != null && !subjectsJson.isEmpty()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            List<SubjectDetails> subjects = objectMapper.readValue(subjectsJson, new TypeReference<List<SubjectDetails>>() {});
                            et.setSubjects(subjects);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error parsing subjects JSON", e);
                        }
                    }
                    return et;
                }
            });
        } catch (DataAccessException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}
