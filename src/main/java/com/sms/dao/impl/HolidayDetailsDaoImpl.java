package com.sms.dao.impl;

import com.sms.dao.HolidayDetailsDao;
import com.sms.model.HolidayDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class HolidayDetailsDaoImpl implements HolidayDetailsDao {
    private final JdbcTemplate jdbcTemplate;
    public HolidayDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*@Override
    public HolidayDetails addHolidayDetails(HolidayDetails holidayDetails, String schoolCode) throws Exception {
        String sql = "INSERT into holiday(school_id,holiday_title,holiday_date, holiday_description) values(?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection ->{
                PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,holidayDetails.getSchoolId());
                ps.setString(2,holidayDetails.getHolidayTitle());
                ps.setDate(3,new java.sql.Date(holidayDetails.getHolidayDate().getTime()));
                ps.setString(4,holidayDetails.getHolidayDescription());
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")){
                int generatedId = ((Number) keys.get("id")).intValue();
                holidayDetails.setId(generatedId);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return holidayDetails;
    }*/
    @Override
    public HolidayDetails addHolidayDetails(HolidayDetails holidayDetails, String schoolCode) throws Exception {
        String sql = "INSERT into holiday(school_id,session_id,holiday_title,holiday_date, holiday_description) values(?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection ->{
                PreparedStatement ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,holidayDetails.getSchoolId());
                ps.setInt(2,holidayDetails.getSessionId());
                ps.setString(3,holidayDetails.getHolidayTitle());
                ps.setDate(4,new java.sql.Date(holidayDetails.getHolidayDate().getTime()));
                ps.setString(5,holidayDetails.getHolidayDescription());
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")){
                int generatedId = ((Number) keys.get("id")).intValue();
                holidayDetails.setId(generatedId);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return holidayDetails;
    }
   /* @Override
    public List<HolidayDetails> getAllHolidayDetails(String schoolCode) throws Exception {
     String sql="SELECT DISTINCT id,holiday_title, holiday_date, holiday_description FROM holiday ORDER BY holiday_date ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<HolidayDetails> holidayDetailsList=jdbcTemplate.query(sql, new RowMapper<HolidayDetails>() {
            @Override
            public HolidayDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                HolidayDetails hd=new HolidayDetails();
                hd.setId(rs.getInt("id"));
                hd.setHolidayTitle(rs.getString("holiday_title"));
                hd.setHolidayDescription(rs.getString("holiday_description"));
                hd.setHolidayDate(rs.getDate("holiday_date"));
                return hd;
            }
        });
        return holidayDetailsList;
    }*/
   @Override
   public List<HolidayDetails> getAllHolidayDetails(int sessionId,String schoolCode) throws Exception {
       String sql="SELECT DISTINCT id,holiday_title, holiday_date, holiday_description FROM holiday where session_id=? ORDER BY holiday_date ASC";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<HolidayDetails> holidayDetailsList = null;
       try{
           holidayDetailsList=jdbcTemplate.query(sql, new Object[]{sessionId}, new RowMapper<HolidayDetails>() {
               @Override
               public HolidayDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   HolidayDetails hd=new HolidayDetails();
                   hd.setId(rs.getInt("id"));
                   hd.setHolidayTitle(rs.getString("holiday_title"));
                   hd.setHolidayDescription(rs.getString("holiday_description"));
                   hd.setHolidayDate(rs.getDate("holiday_date"));
                   return hd;
               }
           });
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return holidayDetailsList;
   }

    @Override
    public List<HolidayDetails> getPastOrTodayHolidays(int sessionId, String schoolCode) throws Exception {
        String sql = """
                SELECT DISTINCT\s
                    id,\s
                    holiday_title,\s
                    holiday_date,\s
                    holiday_description\s
                FROM holiday\s
                WHERE session_id = ?\s
                    AND EXTRACT(MONTH FROM holiday_date) = EXTRACT(MONTH FROM CURRENT_DATE)\s
                    AND EXTRACT(YEAR FROM holiday_date) = EXTRACT(YEAR FROM CURRENT_DATE)\s
                ORDER BY holiday_date ASC;
                """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<HolidayDetails> holidayDetailsList = null;

        try {
            holidayDetailsList = jdbcTemplate.query(sql, new Object[]{sessionId}, new RowMapper<HolidayDetails>() {
                @Override
                public HolidayDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HolidayDetails hd = new HolidayDetails();
                    hd.setId(rs.getInt("id"));
                    hd.setHolidayTitle(rs.getString("holiday_title"));
                    hd.setHolidayDescription(rs.getString("holiday_description"));
                    hd.setHolidayDate(rs.getDate("holiday_date"));
                    return hd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return holidayDetailsList;
    }


    /*@Override
    public HolidayDetails getHolidayDetailsById(int id, String schoolCode){
        String sql = "SELECT id,holiday_title, holiday_date, holiday_description FROM holiday WHERE id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    mapHolidayDetailsFromResultSet(rs)
            );
        } catch (EmptyResultDataAccessException ex) {
            return null; // or throw a custom exception, log a message, etc.
        }
    }*/
    @Override
    public HolidayDetails getHolidayDetailsById(int id, String schoolCode) {
        String sql = "SELECT id,holiday_title, holiday_date, holiday_description FROM holiday WHERE id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        HolidayDetails holidayDetailsList = null;
        try{
            holidayDetailsList=jdbcTemplate.queryForObject(sql, new Object[]{id}, new RowMapper<HolidayDetails>() {
                @Override
                public HolidayDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HolidayDetails hd=new HolidayDetails();
                    hd.setId(rs.getInt("id"));
                    hd.setHolidayTitle(rs.getString("holiday_title"));
                    hd.setHolidayDate(rs.getDate("holiday_date"));
                    hd.setHolidayDescription(rs.getString("holiday_description"));
                    return hd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return holidayDetailsList;
    }
   /* @Override
    public HolidayDetails updateHolidayDetailsById(HolidayDetails holidayDetails, int id, String schoolCode) throws Exception {
        try {
            String sql = "update holiday SET school_id=?,holiday_title=?, holiday_date=?, holiday_description=? where id=?";
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            int rowsAffected = jdbcTemplate.update(sql,
                    holidayDetails.getSchoolId(),
                    holidayDetails.getHolidayTitle(),
                    holidayDetails.getHolidayDate(),
                    holidayDetails.getHolidayDescription(),
                    id);
            if (rowsAffected > 0) {
                return holidayDetails;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Error updating holiday details", e);
        }
    }*/
   @Override
   public HolidayDetails updateHolidayDetailsById(HolidayDetails holidayDetails, int id, String schoolCode) throws Exception {
       String sql = "update holiday SET school_id=?,holiday_title=?, holiday_date=?, holiday_description=? where id=?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try {
           int rowsAffected = jdbcTemplate.update(sql,
                   holidayDetails.getSchoolId(),
                   holidayDetails.getHolidayTitle(),
                   holidayDetails.getHolidayDate(),
                   holidayDetails.getHolidayDescription(),
                   id);
           if (rowsAffected > 0) {
               return holidayDetails;
           } else {
               return null;
           }
       } catch (DataAccessException e) {
           throw new RuntimeException("Error updating holiday details", e);
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

    /*@Override
    public boolean deleteHolidayDetails(int id, String schoolCode) throws Exception {
        String sql = "delete from holiday where id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int rowAffected = jdbcTemplate.update(sql, new Object[]{id});
        if(rowAffected > 0){
            return true;
        }else{
            return false;
        }
    }*/
    @Override
    public boolean deleteHolidayDetails(int id, String schoolCode) throws Exception {
        String sql = "delete from holiday where id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{id});
            if(rowAffected > 0){
                return true;
            }else{
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

