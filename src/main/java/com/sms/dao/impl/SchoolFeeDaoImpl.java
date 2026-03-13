package com.sms.dao.impl;

import com.sms.dao.SchoolFeeDao;
import com.sms.model.SchoolFeeDetails;
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
public class SchoolFeeDaoImpl implements SchoolFeeDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SchoolFeeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(fee_id), 0) + 1 AS fee_id FROM school_fees";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        // Initialize id to 0 in case there are no records
        int nextAvailableId = 0;

        try {
            // Execute the SQL query using jdbcTemplate
            nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableId;
    }

    @Override
    public SchoolFeeDetails addSchoolFee(SchoolFeeDetails schoolFeeDetails, String schoolCode) throws Exception {
        int nextAvailableId = findNextAvailableId(schoolCode);
        schoolFeeDetails.setFeeId(nextAvailableId);
        String sql = "insert into school_fees (fee_id, school_id, fee_type, frequency_id) values (?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.update(sql,
                    schoolFeeDetails.getFeeId(),
                    schoolFeeDetails.getSchoolId(),
                    schoolFeeDetails.getFeeType(),
                    schoolFeeDetails.getFrequencyId());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return schoolFeeDetails;
    }

    @Override
    public SchoolFeeDetails getSchoolFeeById(int feeId, String schoolCode) throws Exception {
        String sql = "SELECT DISTINCT sf.fee_id, sf.school_id, sf.fee_type, sf.frequency_id, mf.frequency_type " +
                "FROM school_fees sf " +
                "JOIN mst_frequency mf ON sf.frequency_id = mf.frequency_id " +
                "WHERE sf.fee_id = ? AND sf.deleted IS NOT TRUE";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SchoolFeeDetails schoolFeeDetails = null;
        try{
            schoolFeeDetails = jdbcTemplate.queryForObject(sql, new Object[]{feeId}, new RowMapper<SchoolFeeDetails>() {
                @Override
                public SchoolFeeDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SchoolFeeDetails sfd = new SchoolFeeDetails();
                    sfd.setFeeId(rs.getInt("fee_id"));
                    sfd.setSchoolId(rs.getInt("school_id"));
                    sfd.setFeeType(rs.getString("fee_type"));
                    sfd.setFrequencyId(rs.getInt("frequency_id"));
                    sfd.setFrequencyType(rs.getString("frequency_type"));
                    return sfd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return schoolFeeDetails;
    }
    @Override
    public List<SchoolFeeDetails> getAllSchoolFee(String schoolCode) throws Exception {
        //String sql = "select distinct fee_id,school_id, fee_type, frequency_id from school_fees where deleted is not true order by fee_id asc";
        String sql = "select sf.fee_id, sf.school_id, sf.fee_type, sf.frequency_id, mf.frequency_type from school_fees sf join mst_frequency mf on sf.frequency_id = mf.frequency_id where sf.deleted is not true order by sf.fee_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SchoolFeeDetails> schoolFeeDetails = null;
        try{
            schoolFeeDetails = jdbcTemplate.query(sql, new RowMapper<SchoolFeeDetails>() {
                @Override
                public SchoolFeeDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SchoolFeeDetails sfd = new SchoolFeeDetails();
                    sfd.setFeeId(rs.getInt("fee_id"));
                    sfd.setSchoolId(rs.getInt("school_id"));
                    sfd.setFeeType(rs.getString("fee_type"));
                    sfd.setFrequencyId(rs.getInt("frequency_id"));
                    sfd.setFrequencyType(rs.getString("frequency_type"));
                    return sfd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return schoolFeeDetails;
    }

    @Override
    public SchoolFeeDetails updateSchoolFeeById(SchoolFeeDetails schoolFeeDetails, int feeId, String schoolCode) throws Exception {
        String sql = "update school_fees set school_id = ?, fee_type = ?, frequency_id = ? where fee_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    schoolFeeDetails.getSchoolId(),
                    schoolFeeDetails.getFeeType(),
                    schoolFeeDetails.getFrequencyId(),
                    feeId);
            if(rowEffected > 0){
                System.out.println(schoolFeeDetails);
                System.out.println(rowEffected);
                return schoolFeeDetails;
            }else {
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
    public boolean deleteSchoolFee(int feeId, String schoolCode) throws Exception {
        String sql = "DELETE from school_fees where fee_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{feeId});
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