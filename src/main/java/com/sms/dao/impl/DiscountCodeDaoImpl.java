package com.sms.dao.impl;

import com.sms.dao.DiscountCodeDao;
import com.sms.model.DiscountCodeDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.DuplicateKeyException;
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
public class DiscountCodeDaoImpl implements DiscountCodeDao {
    private final JdbcTemplate jdbcTemplate;

    public DiscountCodeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DiscountCodeDetails addDiscount(DiscountCodeDetails discountCodeDetails, String schoolCode) throws Exception {
        String sql = "insert into discount_code (dc_description, dc_rate, dc_rate_type, additional_info) values(?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, discountCodeDetails.getDcDescription());
                ps.setInt(2, discountCodeDetails.getDcRate());
                ps.setString(3, discountCodeDetails.getDcRateType());
                ps.setString(4, discountCodeDetails.getAdditionalInfo());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("dc_id")){
                int generatedKey = ((Number) keys.get("dc_id")).intValue();
                discountCodeDetails.setDcId(generatedKey);
            }
        }catch (DuplicateKeyException e){
            throw new Exception("Discount code already exists!");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return discountCodeDetails;
    }

    @Override
    public DiscountCodeDetails getDiscountById(int dcId, String schoolCode) throws Exception {
        String sql = "select distinct dc_id, dc_description, dc_rate, dc_rate_type, additional_info from discount_code where dc_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        DiscountCodeDetails discountCodeDetails = null;
        try{
            discountCodeDetails = jdbcTemplate.queryForObject(sql, new Object[]{dcId}, new RowMapper<DiscountCodeDetails>() {
                @Override
                public DiscountCodeDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DiscountCodeDetails dcd = new DiscountCodeDetails();
                    dcd.setDcId(rs.getInt("dc_id"));
                    dcd.setDcDescription(rs.getString("dc_description"));
                    dcd.setDcRate(rs.getInt("dc_rate"));
                    dcd.setDcRateType(rs.getString("dc_rate_type"));
                    dcd.setAdditionalInfo(rs.getString("additional_info"));
                    return dcd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return discountCodeDetails;
    }

    @Override
    public List<DiscountCodeDetails> getAllDiscount(String schoolCode) throws Exception {
        String sql = "select distinct dc_id, dc_description, dc_rate, dc_rate_type, additional_info from discount_code order by dc_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<DiscountCodeDetails> discountCodeDetails = null;
        try{
            discountCodeDetails = jdbcTemplate.query(sql, new RowMapper<DiscountCodeDetails>() {
                @Override
                public DiscountCodeDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DiscountCodeDetails dcd = new DiscountCodeDetails();
                    dcd.setDcId(rs.getInt("dc_id"));
                    dcd.setDcDescription(rs.getString("dc_description"));
                    dcd.setDcRate(rs.getInt("dc_rate"));
                    dcd.setDcRateType(rs.getString("dc_rate_type"));
                    dcd.setAdditionalInfo(rs.getString("additional_info"));
                    return dcd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return discountCodeDetails;
    }
    @Override
    public DiscountCodeDetails updateDiscountById(DiscountCodeDetails discountCodeDetails, int dcId, String schoolCode) throws Exception {
        String sql = "update discount_code set dc_description = ?, dc_rate = ?, dc_rate_type = ?, additional_info = ? where dc_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    discountCodeDetails.getDcDescription(),
                    discountCodeDetails.getDcRate(),
                    discountCodeDetails.getDcRateType(),
                    discountCodeDetails.getAdditionalInfo(),
                    discountCodeDetails.getDcId());
            if(rowAffected > 0){
                return discountCodeDetails;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }
    @Override
    public boolean deleteDiscount(int dcId, String schoolCode) throws Exception {
        String sql = "delete from discount_code where dc_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{dcId});
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
