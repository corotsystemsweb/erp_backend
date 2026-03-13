package com.sms.service.impl;

import com.sms.dao.ImportStudentDao;
import com.sms.exception.DuplicatePromotionException;
import com.sms.exception.InvalidPromotionException;
import com.sms.model.ImportStudent;
import com.sms.service.ImportStudentService;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ImportStudentServiceImpl implements ImportStudentService {
    @Autowired
    private ImportStudentDao importStudentDao;
    @Override
    public List<ImportStudent> getStudentDetails(Integer sessionId, Integer classId, Integer sectionId, String schoolCode) throws Exception {
        return importStudentDao.getStudentDetails(sessionId,classId,sectionId,schoolCode);
    }

    @Transactional
    @Override
    public void promoteStudents(ImportStudent importStudent, Integer currentSessionId,String schoolCode) {
        System.out.println("hello"+importStudent.getNextClassId());
        // Validate target session/class/section
        importStudentDao.validateSessionClassSection(
                importStudent.getNextSessionId(),
                importStudent.getNextClassId(),
                importStudent.getNextSectionId(),
                schoolCode
        );

        // Check for existing promotions
        for (Integer studentId : importStudent.getStudentIds()) {
            if (importStudentDao.existsInNextSession(studentId, importStudent.getNextSessionId(),schoolCode)) {
                throw new DuplicatePromotionException(
                        "Student ID " + studentId + " already exists in target session");
            }
        }

        // Perform promotion
        importStudentDao.promoteStudents(
                importStudent.getStudentIds(),
                currentSessionId,
                importStudent.getNextSessionId(),
                importStudent.getNextClassId(),
                importStudent.getNextSectionId(),
                schoolCode
        );
    }

    @Override
    public List<ImportStudent> getGraduationEligibleStudents(Integer sessionId, Integer classId, Integer sectionId, String schoolCode) {
        return importStudentDao.getGraduationEligibleStudents(sessionId,classId,sectionId,schoolCode);
    }

    @Transactional
    @Override
    public void graduateStudents(ImportStudent importStudent, Integer currentSessionId, String schoolCode) {
        // Validate current class is highest
       /* if (!importStudentDao.isHighestClass(importStudent.getClassId(), schoolCode)) {
            throw new InvalidPromotionException("Students are not in the highest class for graduation");
        }*/

        // Check if students are already graduated
        for (Integer studentId : importStudent.getStudentIds()) {
            if (isAlreadyGraduated(studentId, schoolCode)) {
                throw new DuplicatePromotionException("Student ID " + studentId + " is already graduated");
            }
        }

        // Perform graduation
        importStudentDao.graduateStudents(
                importStudent.getStudentIds(),
                currentSessionId,
                schoolCode
        );
    }

    private boolean isAlreadyGraduated(Integer studentId, String schoolCode) {
        String sql = "SELECT COUNT(*) FROM student_personal_details " +
                "WHERE student_id = ? AND current_status = 'Graduated'";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, studentId) > 0;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
