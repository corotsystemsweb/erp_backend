package com.sms.service.impl;

import com.sms.dao.ExamResultDao;
import com.sms.model.ExamResult;
import com.sms.service.ExamResultService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExamResultServiceImpl implements ExamResultService {
    private final ExamResultDao examResultDao;
    private final JdbcTemplate jdbcTemplate;

    public ExamResultServiceImpl(ExamResultDao examResultDao, JdbcTemplate jdbcTemplate) {
        this.examResultDao = examResultDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveMarks(List<ExamResult> results ,String schoolCode) throws Exception {
        examResultDao.saveMarks(results,schoolCode);
    }
   /* private void validateMarks(ExamResult result) {
        String sql = "SELECT theory_max_marks, practical_max_marks, viva_max_marks " +
                "FROM exam_subjects WHERE exam_subject_id = ?";

        Map<String, Object> subject = jdbcTemplate.queryForMap(sql, result.getExamSubjectId());

        validateComponentMarks(result.getTheoryMarks(), subject.get("theory_max_marks"), "Theory");
        validateComponentMarks(result.getPracticalMarks(), subject.get("practical_max_marks"), "Practical");
        validateComponentMarks(result.getVivaMarks(), subject.get("viva_max_marks"), "Viva");
    }

    private void validateComponentMarks(Integer marks, Object maxMarks, String componentName) {
        if (marks != null && maxMarks != null) {
            int max = (Integer) maxMarks;
            if (marks > max) {
                throw new RuntimeException(componentName + " marks exceed maximum allowed (" + max + ")");
            }
            if (marks < 0) {
                throw new RuntimeException(componentName + " marks cannot be negative");
            }
        }
    }*/
}
