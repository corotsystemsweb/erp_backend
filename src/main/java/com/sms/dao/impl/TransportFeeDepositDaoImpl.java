package com.sms.dao.impl;

import com.sms.dao.TransportFeeDepositDao;
import com.sms.model.TransportFeeDepositDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Repository
public class TransportFeeDepositDaoImpl implements TransportFeeDepositDao {
    private final JdbcTemplate jdbcTemplate;

    public TransportFeeDepositDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TransportFeeDepositDetails addTransportFeeDeposit(TransportFeeDepositDetails transportFeeDepositDetails, String schoolCode) throws Exception {
        String sql = "insert into transport_fee_deposit (school_id, session_id, class_id, section_id, student_id, route_id, payment_mode, total_amount_paid, payment_received_by, payment_date, transaction_id, payment_description, status) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, transportFeeDepositDetails.getSchoolId());
                ps.setInt(2, transportFeeDepositDetails.getSessionId());
                ps.setInt(3, transportFeeDepositDetails.getClassId());
                ps.setInt(4, transportFeeDepositDetails.getSectionId());
                ps.setInt(5, transportFeeDepositDetails.getStudentId());
                ps.setInt(6, transportFeeDepositDetails.getRouteId());
                ps.setInt(7, transportFeeDepositDetails.getPaymentMode());
                ps.setDouble(8, transportFeeDepositDetails.getTotalAmountPaid());
                ps.setInt(9, transportFeeDepositDetails.getPaymentReceivedBy());
                ps.setTimestamp(10, java.sql.Timestamp.valueOf(transportFeeDepositDetails.getPaymentDate()));
                ps.setString(11, transportFeeDepositDetails.getTransactionId());
                ps.setString(12, transportFeeDepositDetails.getPaymentDescription());
                ps.setString(13, transportFeeDepositDetails.getStatus());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("tfd_id")){
                int generatedId = ((Number) keys.get("tfd_id")).intValue();
                transportFeeDepositDetails.setTfdId(generatedId);
            }
        } catch (Exception e){
            throw new RuntimeException("Error inserting transport_fee_deposit", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return transportFeeDepositDetails;
    }
}
