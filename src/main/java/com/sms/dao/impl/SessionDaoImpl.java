package com.sms.dao.impl;

import com.sms.dao.SessionDao;
import com.sms.model.SessionDetails;
import com.sms.util.AcademicSessionUtil;
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
import java.util.List;
import java.util.Map;

@Repository
public class SessionDaoImpl implements SessionDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*
    @method addSession
    @param sessionDetails
    @throws Exception
    @description Adding session details in session table
    @developer Sukhendu Bhowmik
    */
/*    @Override
    public SessionDetails addSession(SessionDetails sessionDetails, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        if(isSessionExist(sessionDetails, schoolCode)){
            throw new Exception("Session is already exist");
        }
        String sql = "insert into session (school_id, academic_session) values (?, ?)";
        //JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, sessionDetails.getSchoolId());
                ps.setString(2, sessionDetails.getAcademicSession());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("session_id")){
                int generatedKey = ((Number)keys.get("session_id")).intValue();
                sessionDetails.setSessionId(generatedKey);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return sessionDetails;
    }

    private boolean isSessionExist(SessionDetails sessionDetails, String schoolCode){
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = "SELECT EXISTS(SELECT 1 FROM session WHERE academic_session = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                sessionDetails.getAcademicSession());
        return Boolean.TRUE.equals(exists);
    }*/

    @Override
    public SessionDetails addSession(SessionDetails sessionDetails, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            if (isSessionExist(sessionDetails, schoolCode)) {
                throw new Exception("Session is already exist");
            }

            String sql = "INSERT INTO session (school_id, academic_session,start_date, end_date) VALUES (?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, sessionDetails.getSchoolId());
                ps.setString(2, sessionDetails.getAcademicSession());
                ps.setObject(3, sessionDetails.getStartDate()); // LocalDate or null
                ps.setObject(4, sessionDetails.getEndDate());
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("session_id")) {
                int generatedKey = ((Number) keys.get("session_id")).intValue();
                sessionDetails.setSessionId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throwing the exception for handling higher up
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return sessionDetails;
    }

    private boolean isSessionExist(SessionDetails sessionDetails, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            String sql = "SELECT EXISTS(SELECT 1 FROM session WHERE academic_session = ?)";
            Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, sessionDetails.getAcademicSession());
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    /*
    @method getSessionDetailsById
    @param id
    @throws Exception
    @description getting session details by its id from session table
    @developer Sukhendu Bhowmik
    */

    /*@Override
    public SessionDetails getSessionById(int sessionId, String schoolCode) throws Exception {
        String sql = "select distinct session_id, school_id, academic_session from session where session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SessionDetails sessionDetails = null;
        sessionDetails = jdbcTemplate.queryForObject(sql, new Object[]{sessionId}, new RowMapper<SessionDetails>() {
            @Override
            public SessionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                SessionDetails sd = new SessionDetails();
                sd.setSessionId(rs.getInt("session_id"));
                sd.setSchoolId(rs.getInt("school_id"));
                sd.setAcademicSession(rs.getString("academic_session"));
                return sd;
            }
        });
        return sessionDetails;
    }*/

    public SessionDetails getSessionById(int sessionId, String schoolCode) throws Exception {
        String sql = "select distinct session_id, school_id, academic_session from session where session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SessionDetails sessionDetails = null;
        try {
            sessionDetails = jdbcTemplate.queryForObject(sql, new Object[]{sessionId}, new RowMapper<SessionDetails>() {
                @Override
                public SessionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SessionDetails sd = new SessionDetails();
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setAcademicSession(rs.getString("academic_session"));
                    return sd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return sessionDetails;
    }
    /*
    @method getAllSessionDetails
    @throws Exception
    @description getting all session details from session table
    @developer Sukhendu Bhowmik
    */

   /* @Override
    public List<SessionDetails> getAllSessionDetails(String schoolCode) throws Exception {
        String sql = "select distinct session_id, school_id, academic_session from session order by session_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SessionDetails> sessionDetails = jdbcTemplate.query(sql, new RowMapper<SessionDetails>() {
            @Override
            public SessionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                SessionDetails sd = new SessionDetails();
                sd.setSessionId(rs.getInt("session_id"));
                sd.setSchoolId(rs.getInt("school_id"));
                sd.setAcademicSession(rs.getString("academic_session"));
                return sd;
            }
        });
        return sessionDetails;
    }*/

    @Override
    public List<SessionDetails> getAllSessionDetails(String schoolCode) throws Exception {
        String sql = "select distinct session_id, school_id, academic_session from session order by session_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SessionDetails> sessionDetails=null;
       try{
            sessionDetails = jdbcTemplate.query(sql, new RowMapper<SessionDetails>() {
               @Override
               public SessionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   SessionDetails sd = new SessionDetails();
                   sd.setSessionId(rs.getInt("session_id"));
                   sd.setSchoolId(rs.getInt("school_id"));
                   sd.setAcademicSession(rs.getString("academic_session"));
                   return sd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
        return sessionDetails;
    }
    /*
    @method updateSessionDetailsById
    @param sessionDetails, id
    @throws Exception
    @description updating  session details by id
    @developer Sukhendu Bhowmik
    */

    /*@Override
    public SessionDetails updateSessionDetailsById(SessionDetails sessionDetails, int sessionId, String schoolCode) throws Exception {
        String sql = "update session set school_id = ?, academic_session = ? where session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int rowEffected = jdbcTemplate.update(sql,
                sessionDetails.getSchoolId(),
                sessionDetails.getAcademicSession(),
                sessionDetails.getSessionId());
        if(rowEffected > 0){
            return sessionDetails;
        }else{
            return null;
        }
    }*/

    @Override
    public SessionDetails updateSessionDetailsById(SessionDetails sessionDetails, int sessionId, String schoolCode) throws Exception {
        String sql = "UPDATE session SET school_id = ?, academic_session = ? WHERE session_id = ?";
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            int rowsAffected = jdbcTemplate.update(sql,
                    sessionDetails.getSchoolId(),
                    sessionDetails.getAcademicSession(),
                    sessionId);
            if (rowsAffected > 0) {
                return sessionDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throwing the exception for handling higher up
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    /*
    @method deleteSessionDetailsById
    @param id
    @throws Exception
    @description deleting  session details by id
    @developer Sukhendu Bhowmik
    */

    @Override
    public boolean deleteSessionDetailsById(int sessionId, String schoolCode) throws Exception {
        String sql = "DELETE from session where session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rowAffected = jdbcTemplate.update(sql, new Object[]{sessionId});
            if (rowAffected > 0) {
                return true;
            } else {
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
