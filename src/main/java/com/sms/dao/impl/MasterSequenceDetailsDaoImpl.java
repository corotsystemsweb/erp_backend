package com.sms.dao.impl;

import com.sms.dao.MasterSequenceDetailsDao;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MasterSequenceDetailsDaoImpl implements MasterSequenceDetailsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MasterSequenceDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public int findNextAvailableSeqCode(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(seq_code), 0) + 1 AS seq_code FROM master_sequence_controller";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        // Initialize id to 0 in case there are no records
        int nextAvailableSeqCode = 0;
        try {
            nextAvailableSeqCode = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableSeqCode;
    }
    public int findNextAvailableCurrentValue(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(current_value), 0) + 1 AS current_value FROM master_sequence_controller";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        // Initialize id to 0 in case there are no records
        int nextAvailableCurrentValue = 0;

        try {
            // Execute the SQL query using jdbcTemplate
            nextAvailableCurrentValue = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableCurrentValue;
    }
    @Override
    public void addSeqCodeAndCurrentValue(String schoolCode) throws Exception{
        int nextAvailableSeqCode = findNextAvailableSeqCode(schoolCode);
        int nextAvailableCurrentValue = findNextAvailableCurrentValue(schoolCode);

        String sql = "INSERT INTO master_sequence_controller(school_id, seq_code, current_value) VALUES (?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            jdbcTemplate.update(sql, 1, nextAvailableSeqCode, nextAvailableCurrentValue);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    //////////////////////////////////////////////////
   /* public void updateSeqCodeAndCurrentValue(int nextSeqCode, int nextCurrentValue, String schoolCode) throws Exception {
        String sql = "UPDATE master_sequence_controller SET seq_code = ?, current_value = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            jdbcTemplate.update(sql, nextSeqCode, nextCurrentValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }*/
    public void updateSeqCodeAndCurrentValue(int nextSeqCode, int nextCurrentValue, String schoolCode) throws Exception {
        String sql = "UPDATE master_sequence_controller SET seq_code = ?, current_value = ?"; // No WHERE clause
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);  // Get the JdbcTemplate for the specific school schema

        try {
            jdbcTemplate.update(sql, nextSeqCode, nextCurrentValue);  // Execute update without WHERE clause
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


}
