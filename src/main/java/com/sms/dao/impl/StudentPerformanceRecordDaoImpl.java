package com.sms.dao.impl;

import com.sms.dao.StudentPerformanceRecordDao;
import com.sms.model.StudentPerformanceRecordDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentPerformanceRecordDaoImpl implements StudentPerformanceRecordDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentPerformanceRecordDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<StudentPerformanceRecordDetails> getStudentPerformanceRecord(int classId, int sectionId, int sessionId, int studentId, String examType, String schoolCode) throws Exception {
        String sql = "SELECT eme.eme_id, eme.subject_id, ms.subject_name, eme.student_id, CONCAT(spd.first_name, ' ', spd.last_name) AS student_name, eme.maximum_marks, eme.minimum_marks, eme.obtain, eme.status FROM exam_marks_entry eme join mst_subject ms ON eme.subject_id = ms.subject_id join student_personal_details spd ON eme.student_id = spd.student_id WHERE eme.class_id = ? AND eme.section_id = ? AND eme.session_id = ? AND eme.student_id = ? AND eme.exam_type = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentPerformanceRecordDetails> studentPerformanceRecordDetails = null;
        try{
            studentPerformanceRecordDetails = jdbcTemplate.query(sql, new Object[]{classId, sectionId, sessionId, studentId, examType}, new RowMapper<StudentPerformanceRecordDetails>() {
                @Override
                public StudentPerformanceRecordDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentPerformanceRecordDetails sprd = new StudentPerformanceRecordDetails();
                    sprd.setEmeId(rs.getInt("eme_id"));
                    sprd.setSubjectId(rs.getInt("subject_id"));
                    sprd.setSubjectName(rs.getString("subject_name"));
                    sprd.setStudentId(rs.getInt("student_id"));
                    sprd.setStudentName(rs.getString("student_name"));
                    sprd.setMaximumMarks(rs.getDouble("maximum_marks"));
                    sprd.setMinimumMarks(rs.getDouble("minimum_marks"));
                    sprd.setObtain(rs.getDouble("obtain"));
                    sprd.setStatus(rs.getString("status"));
                    return sprd;
                }
            });
            //calculate the total_maximum_marks and total_obtain_marks
            Double totalMaximumMarks = 0.0;
            Double totalObtainMarks = 0.0;
            for(StudentPerformanceRecordDetails record : studentPerformanceRecordDetails){
                totalMaximumMarks += record.getMaximumMarks();
                totalObtainMarks += record.getObtain();
            }
            //Assign total marks to each record
            for(StudentPerformanceRecordDetails record : studentPerformanceRecordDetails){
                record.setTotalMaximumMarks(totalMaximumMarks);
                record.setTotalObtainMarks(totalObtainMarks);
                double percentage = calculatePercentage(totalObtainMarks, totalMaximumMarks);
                record.setPercentage(percentage);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentPerformanceRecordDetails;
    }
    private double calculatePercentage(double totalObtainMarks, double totalMaximumMarks) {
        if (totalMaximumMarks == 0) {
            return 0.0; // To avoid division by zero error
        } else {
            return (totalObtainMarks / totalMaximumMarks) * 100;
        }
    }
}
