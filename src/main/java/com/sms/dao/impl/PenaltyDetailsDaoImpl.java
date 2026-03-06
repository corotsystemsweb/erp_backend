package com.sms.dao.impl;


import com.sms.dao.PenaltyDetailsDao;
import com.sms.model.PenaltyDetails;
import com.sms.model.TransportAllocationDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Repository
public class PenaltyDetailsDaoImpl implements PenaltyDetailsDao {
    private  final JdbcTemplate jdbcTemplate;

    public PenaltyDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public PenaltyDetails addPenalty(PenaltyDetails penaltyDetails,String schoolCode) throws Exception {
        String sql="insert into penalty(penalty_amount,penalty_type,description,system_date_time)values(?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,penaltyDetails.getPenaltyAmount());
                ps.setString(2,penaltyDetails.getPenaltyType());
                ps.setString(3, penaltyDetails.getDescription());
                ps.setTimestamp(4, penaltyDetails.setSystemDateTime(Timestamp.valueOf(LocalDateTime.now())));
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("penalty_id")){
                int generatedKey = ((Number) keys.get("penalty_id")).intValue();
                penaltyDetails.setPenaltyId(generatedKey);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return penaltyDetails;
    }
    @Override
    public List<PenaltyDetails> getAllPenaltyDetails(String schoolCode) throws Exception {
        String sql="select penalty_id,penalty_amount,penalty_type,description from penalty";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
         List<PenaltyDetails> penaltyDetails = null;
        try{
           penaltyDetails = jdbcTemplate.query(sql, new RowMapper<PenaltyDetails>() {
                @Override
                public PenaltyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    PenaltyDetails pd = new PenaltyDetails();
                    pd.setPenaltyId(rs.getInt("penalty_id"));
                    pd.setPenaltyAmount(rs.getInt("penalty_amount"));
                    pd.setPenaltyType(rs.getString("penalty_type"));
                    pd.setDescription(rs.getString("description"));
                    return pd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return penaltyDetails;

    }

    @Override
    public PenaltyDetails getPenalty(int penaltyId, String schoolCode) throws Exception {
        String sql="select penalty_id,penalty_amount,penalty_type,description from penalty where penalty_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        PenaltyDetails penaltyDetails = null;
        try{
            penaltyDetails = jdbcTemplate.queryForObject(sql,new Object[]{penaltyId},new RowMapper<PenaltyDetails>() {
                @Override
                public PenaltyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    PenaltyDetails pd = new PenaltyDetails();
                    pd.setPenaltyId(rs.getInt("penalty_id"));
                    pd.setPenaltyAmount(rs.getInt("penalty_amount"));
                    pd.setPenaltyType(rs.getString("penalty_type"));
                    pd.setDescription(rs.getString("description"));
                    return pd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return penaltyDetails;
    }

    @Override
    public PenaltyDetails updatePenaltyDetails(PenaltyDetails penaltyDetails, int penaltyId, String schoolCode) throws Exception {
        String sql="UPDATE penalty SET penalty_amount=?, penalty_type=?, description=?, system_date_time=? WHERE penalty_id=?";
        JdbcTemplate jdbcTemplate = null;
        try {
            Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            int rowsAffected = jdbcTemplate.update(sql,
                    penaltyDetails.getPenaltyAmount(),
                    penaltyDetails.getPenaltyType(),
                    penaltyDetails.getDescription(),
                    currentTimestamp,
                    penaltyId);
            if (rowsAffected > 0) {
                penaltyDetails.setPenaltyId(penaltyId);
                penaltyDetails.setSystemDateTime(currentTimestamp);
                return penaltyDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
