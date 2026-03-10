package com.sms.dao.impl;

import com.sms.dao.PaySalaryDao;
import com.sms.model.PaySalaryDetails;
import com.sms.model.SessionDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class PaySalaryDaoImpl implements PaySalaryDao {
    private  final JdbcTemplate jdbcTemplate;
  @Autowired
    public PaySalaryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PaySalaryDetails addCalculatedSalary(PaySalaryDetails paySalaryDetails, String schoolCode) throws Exception {
        String sql = "insert into pay_salary(school_id,session_id,designation_id, staff_id, salary_amount, count_leave, total_salary, pay_salary_month, pay_salary_year, payment_date, updated_by, update_date_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,paySalaryDetails.getSchoolId());
                ps.setInt(2,paySalaryDetails.getSessionId());
                ps.setInt(3, paySalaryDetails.getDesignationId());
                ps.setInt(4,paySalaryDetails.getStaffId());
                ps.setString(5, paySalaryDetails.getSalaryAmount());
                ps.setString(6,paySalaryDetails.getLeaveCount());
                ps.setString(7,paySalaryDetails.getTotalSalary());
                ps.setString(8,paySalaryDetails.getPaySalaryMonth());
                ps.setString(9,paySalaryDetails.getPaySalaryYear());
                ps.setDate(10,paySalaryDetails.getPaymentDate());
                ps.setInt(11,paySalaryDetails.getUpdatedBy());
                ps.setDate(12,paySalaryDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("ps_id")){
                int generatedKey = ((Number) keys.get("ps_id")).intValue();
                paySalaryDetails.setPsId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return paySalaryDetails;
    }

    @Override
    public List<PaySalaryDetails> findSalaryDetailsByMonth(String paySalaryMonth, String paySalaryYear,String schoolCode) throws Exception {
       /* String sql = "SELECT " +
                "    sd.designation AS designation, " +
                "    CONCAT(s.first_name, ' ', s.last_name) AS staff_name, " +
                "    ps.count_leave AS count_leave, " +
                "    ps.salary_amount as salary_amount, " +
                "    ps.total_salary AS payable_salary, " +
                "    ps.payment_date AS payment_date, " +
                "    ps.pay_salary_month AS pay_salary_month " +
                "FROM " +
                "    pay_salary ps " +
                "    JOIN staff s ON ps.staff_id = s.staff_id " +
                "    JOIN staff_designation sd ON ps.designation_id = sd.sd_id " +
                "WHERE " +
                "    ps.pay_salary_month = ? " +
                "    AND ps.pay_salary_year = ? ";*/
        String sql = "SELECT \n" +
                "    ps.count_leave,\n" +
                "    ps.total_salary,\n" +
                "    ps.payment_date,\n" +
                "    ps.pay_salary_month, \n" +
                "    sd.designation,\n" +
                "    CASE \n" +
                "        WHEN s.staff_id IS NOT NULL AND sd.designation ILIKE 'driver' THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name)\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        ELSE 'Unknown'\n" +
                "    END AS staff_name,\n" +
                "    CASE\n" +
                "        WHEN s.staff_id IS NOT NULL THEN s.staff_id\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN ad.driver_id\n" +
                "        ELSE NULL\n" +
                "    END AS staff_id,\n" +
                "    ps.salary_amount\n" +
                "FROM \n" +
                "    pay_salary ps\n" +
                "LEFT JOIN \n" +
                "    staff_designation sd ON ps.designation_id = sd.sd_id\n" +
                "LEFT JOIN \n" +
                "    staff s ON ps.staff_id = s.staff_id AND sd.designation NOT ILIKE 'driver' AND COALESCE(s.deleted, false) IS NOT TRUE \n" +
                "LEFT JOIN \n" +
                "    add_driver ad ON ps.staff_id = ad.driver_id AND sd.designation ILIKE 'driver' \n" +
                "WHERE \n" +
                "    ps.pay_salary_month = ? and ps.pay_salary_year = ?\n" +
                "    AND COALESCE(ps.deleted, false) IS NOT TRUE\n" +
                "ORDER BY \n" +
                "    ps.ps_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<PaySalaryDetails> paySalaryDetails = null;
        try{
             paySalaryDetails = jdbcTemplate.query(sql, new Object[]{paySalaryMonth,paySalaryYear},new RowMapper<PaySalaryDetails>() {
                @Override
                public PaySalaryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    PaySalaryDetails psd = new PaySalaryDetails();
                    psd.setLeaveCount(rs.getString("count_leave"));
                    psd.setTotalSalary(rs.getString("total_salary"));
                    psd.setPaymentDate(rs.getDate("payment_date"));
                    psd.setPaySalaryMonth(rs.getString("pay_salary_month"));
                    psd.setDesignation(rs.getString("designation"));
                    psd.setStaffName(rs.getString("staff_name"));
                    psd.setStaffId(rs.getInt("staff_id"));
                    psd.setSalaryAmount(rs.getString("salary_amount"));
                    return psd;
                }
            });
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return paySalaryDetails;
    }

    @Override
    public PaySalaryDetails findSalaryDetailsByStaffId(int staffId, String paySalaryMonth, String paySalaryYear, int designationId, String schoolCode) throws Exception {
        /*String sql = "SELECT " +
                "    sd.designation AS designation, " +
                "    CONCAT(s.first_name, ' ', s.last_name) AS staff_name, " +
                "    ps.count_leave AS count_leave, " +
                "    ps.salary_amount as salary_amount, " +
                "    ps.total_salary AS payable_salary, " +
                "    ps.payment_date AS payment_date, " +
                "    ps.pay_salary_month AS pay_salary_month " +
                "FROM " +
                "    pay_salary ps " +
                "    JOIN staff s ON ps.staff_id = s.staff_id " +
                "    JOIN staff_designation sd ON ps.designation_id = sd.sd_id " +
                "WHERE " +
                "    s.staff_id= ? " +
                "    AND ps.pay_salary_month = ? " +
                "    AND ps.pay_salary_year = ?; ";*/
        String sql = "SELECT \n" +
                "    ps.count_leave,\n" +
                "    ps.total_salary,\n" +
                "    ps.payment_date,\n" +
                "    ps.pay_salary_month, \n" +
                "    sd.designation,\n" +
                "    CASE \n" +
                "        WHEN s.staff_id IS NOT NULL AND sd.designation ILIKE 'driver' THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name)\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        ELSE 'Unknown'\n" +
                "    END AS staff_name,\n" +
                "    CASE\n" +
                "        WHEN s.staff_id IS NOT NULL THEN s.staff_id\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN ad.driver_id\n" +
                "        ELSE NULL\n" +
                "    END AS staff_id,\n" +
                "    ps.salary_amount\n" +
                "FROM \n" +
                "    pay_salary ps\n" +
                "LEFT JOIN \n" +
                "    staff_designation sd ON ps.designation_id = sd.sd_id\n" +
                "LEFT JOIN \n" +
                "    staff s ON ps.staff_id = s.staff_id AND sd.designation NOT ILIKE 'driver' AND COALESCE(s.deleted, false) IS NOT TRUE\n" +
                "LEFT JOIN \n" +
                "    add_driver ad ON ps.staff_id = ad.driver_id AND sd.designation ILIKE 'driver'\n" +
                "WHERE \n" +
                "    ps.staff_id = ?\n" +
                "    and ps.pay_salary_month = ? \n" +
                "    and ps.pay_salary_year = ?\n" +
                "    and ps.designation_id = ?\n" +
                "    AND COALESCE(ps.deleted, false) IS NOT TRUE\n" +
                "ORDER BY \n" +
                "    ps.ps_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        PaySalaryDetails paySalaryDetails=null;
        try{
             paySalaryDetails = jdbcTemplate.queryForObject(sql, new Object[]{staffId,paySalaryMonth,paySalaryYear,designationId},new RowMapper<PaySalaryDetails>() {
                @Override
                public PaySalaryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    PaySalaryDetails psd = new PaySalaryDetails();
                    psd.setStaffId(rs.getInt("staff_id"));
                    psd.setDesignation(rs.getString("designation"));
                    psd.setStaffName(rs.getString("staff_name"));
                    psd.setLeaveCount(rs.getString("count_leave"));
                    psd.setSalaryAmount(rs.getString("salary_amount"));
                    psd.setTotalSalary(rs.getString("total_salary"));
                    psd.setPaymentDate(rs.getDate("payment_date"));
                    psd.setPaySalaryMonth(rs.getString("pay_salary_month"));
                    return psd;
                }
            });
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return paySalaryDetails;
    }
}
