package com.sms.dao.impl;
import com.sms.dao.ExamScheduleDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.model.ExamScheduleDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Repository
public class ExamScheduleDaoImpl implements ExamScheduleDao {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public ExamScheduleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
   @Override
   public List<ExamScheduleDetails> addExamScheduleDetails(List<ExamScheduleDetails> examScheduleDetails, String schoolCode) throws Exception {
       String sql = "INSERT INTO exam_schedule (school_id,session_id, class_id, section_id, subject_id, start_month, end_month, exam_type, exam_date, exam_day, exam_timing, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<ExamScheduleDetails> examScheduleDetailsList = new ArrayList<>();
       try{
           for (ExamScheduleDetails scheduleDetails : examScheduleDetails) {
               KeyHolder keyHolder = new GeneratedKeyHolder();
               jdbcTemplate.update(connection -> {
                   PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                   ps.setInt(1, scheduleDetails.getSchoolId());
                   ps.setInt(2, scheduleDetails.getSessionId());
                   ps.setInt(3, scheduleDetails.getClassId());
                   ps.setInt(4, scheduleDetails.getSectionId());
                   ps.setInt(5, scheduleDetails.getSubjectId());
                   ps.setString(6, scheduleDetails.getStartMonth());
                   ps.setString(7, scheduleDetails.getEndMonth());
                   ps.setString(8, scheduleDetails.getExamType());
                   ps.setDate(9, scheduleDetails.getExamDate());
                   ps.setString(10, scheduleDetails.getExamDay());
                   ps.setString(11, scheduleDetails.getExamTiming());
                   ps.setInt(12, scheduleDetails.getUpdatedBy());
                   ps.setDate(13, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                   return ps;
               }, keyHolder);
               Map<String, Object> keys = keyHolder.getKeys();
               if (keys != null && keys.containsKey("es_id")) {
                   int generatedId = ((Number) keys.get("es_id")).intValue();
                   scheduleDetails.setEsId(generatedId);
                   examScheduleDetailsList.add(scheduleDetails);
               }
           }
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return examScheduleDetailsList;
   }

    @Override
    public ExamScheduleDetails addExam(ExamScheduleDetails examScheduleDetails, String schoolCode) throws Exception {
        String sql = "INSERT INTO exam_schedule (school_id,session_id, class_id, section_id, subject_id, start_month, end_month, exam_type, exam_date, exam_day, exam_timing, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, examScheduleDetails.getSchoolId());
                ps.setInt(2, examScheduleDetails.getSessionId());
                ps.setInt(2, examScheduleDetails.getSessionId());
                ps.setInt(3, examScheduleDetails.getClassId());
                ps.setInt(4, examScheduleDetails.getSectionId());
                ps.setInt(5, examScheduleDetails.getSubjectId());
                ps.setString(6, examScheduleDetails.getStartMonth());
                ps.setString(7, examScheduleDetails.getEndMonth());
                ps.setString(8, examScheduleDetails.getExamType());
                ps.setDate(9, examScheduleDetails.getExamDate());
                ps.setString(10, examScheduleDetails.getExamDay());
                ps.setString(11, examScheduleDetails.getExamTiming());
                ps.setInt(12, examScheduleDetails.getUpdatedBy());
                ps.setDate(13, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("es_id")) {
                int generatedId = ((Number) keys.get("es_id")).intValue();
                examScheduleDetails.setEsId(generatedId);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examScheduleDetails;
    }
    @Override
    public List<ExamScheduleDetails> getExamScheduleDetails(int sessionId, int classId, int sectionId, String examType, String schoolCode) throws Exception {
        String sql = "SELECT " +
                "es.es_id, " +
                "mc.class_name, " +
                "ms.section_name, " +
                "es.start_month, " +
                "es.end_month, " +
                "s.academic_session, " +
                "msj.subject_name, " +
                "es.exam_date, " +
                "es.exam_day, " +
                "es.exam_timing " +
                "FROM " +
                "exam_schedule es " +
                "JOIN " +
                "mst_class mc ON es.class_id = mc.class_id " +
                "JOIN " +
                "mst_section ms ON es.section_id = ms.section_id " +
                "JOIN " +
                "session s ON es.session_id = s.session_id " +
                "JOIN " +
                "mst_subject msj ON es.subject_id = msj.subject_id " +
                "WHERE " +
                "es.session_id = ? " +
                "AND es.class_id = ? " +
                "AND es.section_id = ? " +
                "AND es.exam_type = ? " +
                "AND es.deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            List<ExamScheduleDetails> examScheduleDetails = jdbcTemplate.query(sql, new Object[]{sessionId, classId, sectionId, examType}, new RowMapper<ExamScheduleDetails>() {
                @Override
                public ExamScheduleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamScheduleDetails es = new ExamScheduleDetails();
                    es.setEsId(rs.getInt("es_id"));
                    es.setClassName(rs.getString("class_name"));
                    es.setSectionName(rs.getString("section_name"));
                    es.setStartMonth(rs.getString("start_month"));
                    es.setEndMonth(rs.getString("end_month"));
                    es.setAcademicSession(rs.getString("academic_session"));
                    es.setSubjectName(rs.getString("subject_name"));
                    es.setExamDate(rs.getDate("exam_date"));
                    es.setExamDay(rs.getString("exam_day"));
                    es.setExamTiming(rs.getString("exam_timing"));
                    return es;
                }
            });
            return examScheduleDetails;
        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for debugging
            throw new Exception();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<ExamScheduleDetails> getAllExamScheduleDetails(int sessionId, String schoolCode) throws Exception {
        return List.of();
    }

    @Override
    public boolean softDeleteExamSchedule(int esId, String schoolCode) throws Exception {
        String sql = "UPDATE exam_schedule SET deleted = TRUE WHERE es_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, esId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
