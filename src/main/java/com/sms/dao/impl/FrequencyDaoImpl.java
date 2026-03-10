package com.sms.dao.impl;

import com.sms.dao.FrequencyDao;
import com.sms.model.FrequencyDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class FrequencyDaoImpl implements FrequencyDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public FrequencyDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(frequency_id), 0) + 1 AS frequency_id FROM mst_frequency";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int nextAvailableId = 0;
        try {
            nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableId;
    }

    @Override
    public FrequencyDetails addFrequency(FrequencyDetails frequencyDetails, String schoolCode) throws Exception {
        int nextAvailableId = findNextAvailableId(schoolCode);
        frequencyDetails.setFrequencyId(nextAvailableId);
        String sql = "insert into mst_frequency (frequency_id, school_id, frequency_type) values (?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.update(sql,
                    frequencyDetails.getFrequencyId(),
                    frequencyDetails.getSchoolId(),
                    frequencyDetails.getFrequencyType());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return frequencyDetails;
    }
    @Override
    public FrequencyDetails getFrequencyDetailsById(int frequencyId, String schoolCode) throws Exception {
        String sql = "select distinct frequency_id,school_id, frequency_type from mst_frequency where frequency_id = ? and deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        FrequencyDetails frequencyDetails = null;
        try{
            frequencyDetails = jdbcTemplate.queryForObject(sql, new Object[]{frequencyId}, new RowMapper<FrequencyDetails>() {
                @Override
                public FrequencyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FrequencyDetails fd = new FrequencyDetails();
                    fd.setFrequencyId(rs.getInt("frequency_id"));
                    fd.setSchoolId(rs.getInt("school_id"));
                    fd.setFrequencyType(rs.getString("frequency_type"));
                    return fd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return frequencyDetails;
    }
    @Override
    public List<FrequencyDetails> getAllFrequencyDetails(String schoolCode) throws Exception {
        String sql = "select distinct frequency_id,school_id, frequency_type from mst_frequency where deleted is not true order by frequency_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FrequencyDetails> frequencyDetails = null;
        try{
            frequencyDetails = jdbcTemplate.query(sql, new RowMapper<FrequencyDetails>() {
                @Override
                public FrequencyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FrequencyDetails fd = new FrequencyDetails();
                    fd.setFrequencyId(rs.getInt("frequency_id"));
                    fd.setSchoolId(rs.getInt("school_id"));
                    fd.setFrequencyType(rs.getString("frequency_type"));
                    return fd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return frequencyDetails;
    }
    @Override
    public FrequencyDetails updateFrequencyDetailsById(FrequencyDetails frequencyDetails, int frequencyId, String schoolCode) throws Exception {
        String sql = "update mst_frequency set school_id = ?, frequency_type = ? where frequency_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    frequencyDetails.getSchoolId(),
                    frequencyDetails.getFrequencyType(),
                    frequencyId);
            if(rowEffected > 0){
                return frequencyDetails;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }
    @Override
    public boolean deleteFrequency(int frequencyId, String schoolCode) throws Exception {
        String sql = "update mst_frequency set deleted = TRUE WHERE frequency_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, frequencyId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }
}
