package com.sms.dao.impl;

import com.sms.dao.PaymentModeDao;
import com.sms.model.PaymentDetails;
import com.sms.model.SessionDetails;
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
public class PaymentModeDaoImpl implements PaymentModeDao {
    private final JdbcTemplate jdbcTemplate;
@Autowired
    public PaymentModeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public PaymentDetails addPaymentMode(PaymentDetails paymentDetails, String schoolCode) throws Exception {
        String sql = "insert into payment_mode (school_id,payment_type) values(?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, paymentDetails.getSchoolId());
                ps.setString(2, paymentDetails.getPaymentType());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("pm_id")){
                int generatedKey = ((Number) keys.get("pm_id")).intValue();
                paymentDetails.setPmId(generatedKey);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return  paymentDetails;
    }

    @Override
    public PaymentDetails getPaymentModeById(int pmId, String schoolCode) throws Exception {
        String sql = "select distinct pm_id, school_id, payment_type from payment_mode where pm_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        PaymentDetails paymentDetails = null;
        try{
            paymentDetails = jdbcTemplate.queryForObject(sql, new Object[]{pmId}, new RowMapper<PaymentDetails>() {
                @Override
                public PaymentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    PaymentDetails pd = new PaymentDetails();
                    pd.setPmId(rs.getInt("pm_id"));
                    pd.setSchoolId(rs.getInt("school_id"));
                    pd.setPaymentType(rs.getString("payment_type"));
                    return pd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return paymentDetails;
    }

   @Override
   public List<PaymentDetails> getAllPaymentMode(String schoolCode) throws Exception {
       String sql = "select distinct pm_id, school_id, payment_type from payment_mode order by pm_id asc";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<PaymentDetails> paymentDetails = null;
       try{
           paymentDetails = jdbcTemplate.query(sql, new RowMapper<PaymentDetails>() {
               @Override
               public PaymentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   PaymentDetails pd = new PaymentDetails();
                   pd.setPmId(rs.getInt("pm_id"));
                   pd.setSchoolId(rs.getInt("school_id"));
                   pd.setPaymentType(rs.getString("payment_type"));
                   return pd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return paymentDetails;
   }
}

