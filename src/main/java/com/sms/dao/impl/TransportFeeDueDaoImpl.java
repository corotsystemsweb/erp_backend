package com.sms.dao.impl;

import com.sms.dao.TransportFeeDueDao;
import com.sms.model.TransportFeeDue;
import com.sms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
public class TransportFeeDueDaoImpl implements TransportFeeDueDao {
    private final JdbcTemplate jdbcTemplate;

    public TransportFeeDueDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private static final Logger logger = LoggerFactory.getLogger(TransportFeeDueDaoImpl.class);

    @Override
    public String addBulkTransportFeeDue(List<TransportFeeDue> transportFeeDuesList, String schoolCode) throws Exception {
        String sql = "insert into transport_fee_due (school_id, student_transport_id, due_month, fee_amount, discount_amount, penalty_amount) values (?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int[] batchResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    TransportFeeDue tfd = transportFeeDuesList.get(i);
                    ps.setInt(1, tfd.getSchoolId());
                    ps.setInt(2, tfd.getStudentTransportId());
                    ps.setDate(3, new java.sql.Date(tfd.getDueMonth().getTime()));
                    ps.setBigDecimal(4, tfd.getFeeAmount());
                    ps.setBigDecimal(5, tfd.getDiscountAmount());
                    ps.setBigDecimal(6, tfd.getPenaltyAmount());
                }

                @Override
                public int getBatchSize() {
                    return transportFeeDuesList.size();
                }
            });
            if(Arrays.stream(batchResult).allMatch(result -> result >= 0)){
                return "Transport Fee details are added successfully";
            } else{
                throw new RuntimeException("Error occurred while inserting records!");
            }
        } catch(Exception e){
            logger.error("Database error while inserting Transport Fee for the school code: {}", schoolCode, e);
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public String updateBulkTransportFeeDue(List<TransportFeeDue> list, String schoolCode) throws Exception {
        String sql = "UPDATE transport_fee_due SET  school_id = ?, student_transport_id = ?, due_month = ?, fee_amount = ?, discount_amount = ?, penalty_amount = ? WHERE tfdue_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    TransportFeeDue t = list.get(i);

                    ps.setInt(1, t.getSchoolId());
                    ps.setInt(2, t.getStudentTransportId());
                    ps.setDate(3, new java.sql.Date(t.getDueMonth().getTime()));
                    ps.setBigDecimal(4, t.getFeeAmount());
                    ps.setBigDecimal(5, t.getDiscountAmount());
                    ps.setBigDecimal(6, t.getPenaltyAmount());
                    ps.setInt(7, t.getTfDueId());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
            return "Updated";
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public String closeTransportFeeMonths(List<Integer> tfDueIds, String schoolCode) throws Exception {
        String sql = """
                UPDATE transport_fee_due tfd SET fee_amount = 0, discount_amount = 0, penalty_amount = 0
                WHERE tfdue_id = ?
                AND NOT EXISTS (SELECT 1 FROM transport_fee_deposit_details tfdd WHERE tfdd.student_transport_id = tfd.student_transport_id AND tfdd.due_month = tfd.due_month)
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, tfDueIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return tfDueIds.size();
                }
            });
            return "Closed Successfully";
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

//    @Override
//    public int countRemainingActiveMonths(int studentTransportId, String schoolCode) throws Exception {
//        String sql = """
//                SELECT COUNT(*) FROM transport_fee_due tfd
//                            WHERE tfd.student_transport_id = ?
//                            AND tfd.fee_amount > 0
//                            AND NOT EXISTS (
//                              SELECT 1 FROM transport_fee_deposit_details tfdd
//                              WHERE tfdd.student_transport_id = tfd.student_transport_id
//                               AND tfdd.due_month = tfd.due_month
//                            )
//                """;
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        try{
//            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentTransportId);
//            return count == null ? 0 : count;
//        } finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//    }

}
