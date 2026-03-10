package com.sms.dao.impl;

import com.sms.dao.ExamMeetingDao;
import com.sms.model.ExamMeetingDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
public class ExamMeetingDaoImpl implements ExamMeetingDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public ExamMeetingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public ExamMeetingDetails addExamMeeting(ExamMeetingDetails examMeetingDetails, String schoolCode) throws Exception {
        String sql = "insert into exam_meeting (school_id, session_id, title, meeting_date, start_time, end_time, updated_by, update_date_time) values (?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        DateTimeFormatter timeFormatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, examMeetingDetails.getSchoolId());
                ps.setInt(2, examMeetingDetails.getSessionId());
                ps.setString(3, examMeetingDetails.getTitle());
                ps.setDate(4,examMeetingDetails.getMeetingDate());
                ps.setTime(5, java.sql.Time.valueOf(DateUtil.parseTime(examMeetingDetails.getStartTime()).format(timeFormatterWithSeconds)));
                ps.setTime(6, java.sql.Time.valueOf(DateUtil.parseTime(examMeetingDetails.getEndTime()).format(timeFormatterWithSeconds)));
                ps.setInt(7, examMeetingDetails.getUpdatedBy());
                ps.setDate(8, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("em_id")) {
                int generatedKey = ((Number) keys.get("em_id")).intValue();
                examMeetingDetails.setEmId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examMeetingDetails;
    }
    @Override
    public ExamMeetingDetails getExamMeetingById(int emId,String schoolCode) throws Exception {
        String sql = "select distinct em_id, title, meeting_date, start_time, end_time from exam_meeting where deleted is not true and em_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ExamMeetingDetails examMeetingDetails = null;
        try{
            examMeetingDetails = jdbcTemplate.queryForObject(sql, new Object[]{emId}, new RowMapper<ExamMeetingDetails>() {
                @Override
                public ExamMeetingDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamMeetingDetails emd=new ExamMeetingDetails();
                    emd.setEmId(rs.getInt("em_id"));
                    emd.setTitle(rs.getString("title"));
                    emd.setMeetingDate(rs.getDate("meeting_date"));
                    emd.setStartTime(rs.getString("start_time"));
                    emd.setEndTime(rs.getString("end_time"));
                    return emd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examMeetingDetails;
    }
    @Override
    public List<ExamMeetingDetails> getAllExamMeeting(String schoolCode) throws Exception {
        String sql = "select distinct em_id, title, meeting_date, start_time, end_time from exam_meeting where deleted is not true order by em_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<ExamMeetingDetails> examMeetingDetails = null;
       try{
           examMeetingDetails = jdbcTemplate.query(sql, new RowMapper<ExamMeetingDetails>() {
               @Override
               public ExamMeetingDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ExamMeetingDetails emd=new ExamMeetingDetails();
                   emd.setEmId(rs.getInt("em_id"));
                   emd.setTitle(rs.getString("title"));
                   emd.setMeetingDate(rs.getDate("meeting_date"));
                   emd.setStartTime(rs.getString("start_time"));
                   emd.setEndTime(rs.getString("end_time"));
                   return emd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
        return examMeetingDetails;
    }
    @Override
    public ExamMeetingDetails updateExamMeeting(ExamMeetingDetails examMeetingDetails, int emId, String schoolCode) throws Exception {
        String sql = "UPDATE exam_meeting SET title = ?, meeting_date = ?, start_time = ?, end_time = ?, updated_by = ?, update_date_time = ? WHERE em_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        DateTimeFormatter timeFormatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            int rowsAffected = jdbcTemplate.update(sql,
                    examMeetingDetails.getTitle(),
                    examMeetingDetails.getMeetingDate(),
                    java.sql.Time.valueOf(DateUtil.parseTime(examMeetingDetails.getStartTime()).format(timeFormatterWithSeconds)),
                    java.sql.Time.valueOf(DateUtil.parseTime(examMeetingDetails.getEndTime()).format(timeFormatterWithSeconds)),
                    examMeetingDetails.getUpdatedBy(),
                    java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()),
                    emId);
            if (rowsAffected > 0) {
                return examMeetingDetails;
            } else {
                throw new Exception("No rows affected during update. Exam meeting ID: " + emId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error updating exam meeting: " + e.getMessage(), e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public boolean softDeleteExamMeeting(int emId, String schoolCode) throws DataAccessException {
        final String sql = "UPDATE exam_meeting SET deleted = true WHERE em_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int update = jdbcTemplate.update(sql, emId);
            return update > 0;
        } catch (DataAccessException e) {
            System.err.println("Error executing softDeleteExamMeeting: " + e.getMessage());
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<ExamMeetingDetails> getExamMeetingDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "select title, meeting_date, start_time, end_time from exam_meeting where CONCAT_WS(' ', title, meeting_date, start_time, end_time) ILIKE ? AND deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ExamMeetingDetails> examMeetingDetails = null;
        try{
            examMeetingDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<ExamMeetingDetails>() {
                @Override
                public ExamMeetingDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExamMeetingDetails emd = new ExamMeetingDetails();
                    emd.setTitle(rs.getString("title"));
                    emd.setMeetingDate(rs.getDate("meeting_date"));
                    emd.setStartTime(rs.getString("start_time"));
                    emd.setEndTime(rs.getString("end_time"));
                    return emd;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return examMeetingDetails;
    }

}

