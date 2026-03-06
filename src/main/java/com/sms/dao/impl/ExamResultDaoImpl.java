package com.sms.dao.impl;

import com.sms.dao.ExamResultDao;
import com.sms.model.ExamResult;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
public class ExamResultDaoImpl implements ExamResultDao {
    private final JdbcTemplate jdbcTemplate;

    public ExamResultDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveMarks(List<ExamResult> results, String schoolCode) {
        String sql = "INSERT INTO exam_results (student_id, exam_subject_id, theory_marks, practical_marks, viva_marks) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (student_id, exam_subject_id) DO UPDATE SET " +
                "theory_marks = EXCLUDED.theory_marks, " +
                "practical_marks = EXCLUDED.practical_marks, " +
                "viva_marks = EXCLUDED.viva_marks";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ExamResult result = results.get(i);
                    ps.setLong(1, result.getStudentId());
                    ps.setLong(2, result.getExamSubjectId());
                    ps.setObject(3, result.getTheoryMarks(), Types.INTEGER);
                    ps.setObject(4, result.getPracticalMarks(), Types.INTEGER);
                    ps.setObject(5, result.getVivaMarks(), Types.INTEGER);
                }

                @Override
                public int getBatchSize() {
                    return results.size();
                }

            });
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
