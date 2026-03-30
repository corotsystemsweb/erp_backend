package com.sms.dao.impl;

import com.sms.model.FeeDetail;

import com.sms.dao.TransportFeeDepositDetailsDao;
import com.sms.model.TransportFeeDepositDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TransportFeeDepositDetailsDaoImpl implements TransportFeeDepositDetailsDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<TransportFeeDepositDetails> addTransportFeeDepositDetails(List<TransportFeeDepositDetails> transportFeeDepositDetails, String schoolCode) throws Exception {
        String sql = "insert into transport_fee_deposit_details (tfd_id, student_transport_id, due_month, fee_amount, discount_amount, penalty_amount, amount_paid, balance, status, system_date_time) values (?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    TransportFeeDepositDetails tfdd = transportFeeDepositDetails.get(i);
                    ps.setInt(1, tfdd.getTfdId());
                    ps.setInt(2, tfdd.getStudentTransportId());
                    ps.setDate(3, new java.sql.Date(tfdd.getDueMonth().getTime()));
                    ps.setDouble(4, tfdd.getFeeAmount());
                    ps.setDouble(5, tfdd.getDiscountAmount());
                    ps.setDouble(6, tfdd.getPenaltyAmount());
                    ps.setDouble(7, tfdd.getAmountPaid());
                    ps.setDouble(8, tfdd.getBalance());
                    ps.setString(9, tfdd.getStatus());
                    ps.setTimestamp(10, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                }

                @Override
                public int getBatchSize() {
                    return transportFeeDepositDetails.size();
                }
            });
        } catch (Exception e){
            throw new Exception("Error while adding transport fee deposit details ", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return transportFeeDepositDetails;
    }



    @Override
    public List<FeeDetail> findTransportFeeDetailsByTransactionId(String transactionId, int schoolId, String schoolCode) throws Exception {

        String sql = """
            SELECT
                tfdd.due_month       AS due_date,
                tfdd.fee_amount      AS original_amount,
                tfdd.discount_amount AS discount_amount,
                tfdd.penalty_amount  AS penalty_amount,
                tfdd.amount_paid     AS amount_paid,
                tfdd.balance         AS balance
            FROM transport_fee_deposit tfd
            JOIN transport_fee_deposit_details tfdd ON tfd.tfd_id = tfdd.tfd_id
            WHERE tfd.transaction_id = ?
              AND tfd.school_id = ?
            """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, new Object[]{transactionId, schoolId},
                    (rs, rowNum) -> {
                        FeeDetail fd = new FeeDetail();
                        fd.setFeeType("Transport Fee");
                        fd.setDueDate(rs.getDate("due_date"));
                        fd.setOriginalAmount(rs.getDouble("original_amount"));
                        fd.setDiscountDescription("No Discount");
                        fd.setDiscountRate("0 Rs");
                        fd.setDiscountAmount(rs.getDouble("discount_amount"));
                        fd.setPenaltyAmount(rs.getDouble("penalty_amount"));
                        fd.setAmountPaid(rs.getDouble("amount_paid"));
                        fd.setBalance(rs.getDouble("balance"));
                        return fd;
                    });
        } catch (Exception e) {
            throw new Exception("Error fetching transport fee details for transactionId: "
                    + transactionId, e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
