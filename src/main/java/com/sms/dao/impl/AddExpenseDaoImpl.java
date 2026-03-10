package com.sms.dao.impl;

import com.sms.dao.AddExpenseDao;
import com.sms.model.AddExpenseDetails;
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
public class AddExpenseDaoImpl implements AddExpenseDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddExpenseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AddExpenseDetails addExpenseDetails(AddExpenseDetails addExpenseDetails, String schoolCode) throws Exception {
        String sql = "insert into add_expense (school_id, session_id, expense_type_id, particular, expense_date, expense_amount, updated_by, update_date_time) values(?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, addExpenseDetails.getSchoolId());
                ps.setInt(2, addExpenseDetails.getSessionId());
                ps.setInt(3, addExpenseDetails.getExpenseTypeId());
                ps.setString(4, addExpenseDetails.getParticular());
                ps.setDate(5, new java.sql.Date(addExpenseDetails.getExpenseDate().getTime()));
                ps.setDouble(6, addExpenseDetails.getExpenseAmount());
                ps.setInt(7, addExpenseDetails.getUpdatedBy());
                ps.setDate(8, new java.sql.Date(addExpenseDetails.getUpdateDateTime().getTime()));
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys !=null && keys.containsKey("add_expense_id")){
                int generatedKey = ((Number)keys.get("add_expense_id")).intValue();
                addExpenseDetails.setAddExpenseId(generatedKey);
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addExpenseDetails;
    }

    @Override
    public AddExpenseDetails getAddExpenseDetailsById(int addExpenseId, String schoolCode) throws Exception {
        String sql = "SELECT ae.add_expense_id, ae.school_id, ae.session_id, ae.expense_type_id, et.expense_title, ae.particular, ae.expense_date, ae.expense_amount FROM add_expense ae INNER JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.deleted IS NOT TRUE AND et.deleted IS NOT TRUE AND ae.add_expense_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        AddExpenseDetails addExpenseDetails = null;
        try{
            addExpenseDetails = jdbcTemplate.queryForObject(sql, new Object[]{addExpenseId}, new RowMapper<AddExpenseDetails>() {
                @Override
                public AddExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddExpenseDetails ae = new AddExpenseDetails();
                    ae.setAddExpenseId(rs.getInt("add_expense_id"));
                    ae.setSchoolId(rs.getInt("school_id"));
                    ae.setSessionId(rs.getInt("session_id"));
                    ae.setExpenseTypeId(rs.getInt("expense_type_id"));
                    ae.setExpenseTitle(rs.getString("expense_title"));
                    ae.setParticular(rs.getString("particular"));
                    ae.setExpenseDate(rs.getDate("expense_date"));
                    ae.setExpenseAmount(rs.getDouble("expense_amount"));
                    return ae;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addExpenseDetails;
    }

    @Override
    public List<AddExpenseDetails> getAllAddExpenseDetails(String schoolCode) throws Exception {
        String sql = "SELECT ae.add_expense_id, ae.school_id, ae.session_id, ae.expense_type_id, et.expense_title, ae.particular, ae.expense_date, ae.expense_amount FROM add_expense ae INNER JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.deleted IS NOT TRUE AND et.deleted IS NOT TRUE ORDER BY add_expense_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddExpenseDetails> addExpenseDetails = null;
        try{
            addExpenseDetails = jdbcTemplate.query(sql, new RowMapper<AddExpenseDetails>() {
                @Override
                public AddExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddExpenseDetails ae = new AddExpenseDetails();
                    ae.setAddExpenseId(rs.getInt("add_expense_id"));
                    ae.setSchoolId(rs.getInt("school_id"));
                    ae.setSessionId(rs.getInt("session_id"));
                    ae.setExpenseTypeId(rs.getInt("expense_type_id"));
                    ae.setExpenseTitle(rs.getString("expense_title"));
                    ae.setParticular(rs.getString("particular"));
                    ae.setExpenseDate(rs.getDate("expense_date"));
                    ae.setExpenseAmount(rs.getDouble("expense_amount"));
                    return ae;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addExpenseDetails;
    }

    @Override
    public AddExpenseDetails updateAddExpenseDetails(AddExpenseDetails addExpenseDetails, int addExpenseId, String schoolCode) throws Exception {
        String sql = "UPDATE add_expense set expense_type_id = ?, particular = ?, expense_date = ?, expense_amount = ?, updated_by = ?, update_date_time = ? where add_expense_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    addExpenseDetails.getExpenseTypeId(),
                    addExpenseDetails.getParticular(),
                    addExpenseDetails.getExpenseDate(),
                    addExpenseDetails.getExpenseAmount(),
                    addExpenseDetails.getUpdatedBy(),
                    addExpenseDetails.getUpdateDateTime(),
                    addExpenseId);
            if(rowAffected > 0){
                return addExpenseDetails;
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
    public boolean softDeleteAddExpenseDetails(int addExpenseId, String schoolCode) throws Exception {
        String sql = "UPDATE add_expense SET deleted = TRUE WHERE add_expense_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, addExpenseId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<AddExpenseDetails> getExpenseReport(String expenseTitle, String reportType, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = null;
        try{
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            String sql = "";
            switch (reportType.toLowerCase()){
                case "daily report":
                    sql = "SELECT ae.expense_date, et.expense_title, ae.particular, ae.expense_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner WHERE ae_inner.expense_date = ae.expense_date AND ae_inner.deleted IS NOT TRUE) AS total_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner JOIN expense_type et_inner ON ae_inner.expense_type_id = et_inner.expense_type_id WHERE ae_inner.expense_date = ae.expense_date AND ae_inner.deleted IS NOT TRUE AND et_inner.expense_title = ?) AS total_expense_amount FROM add_expense ae JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.expense_date = CURRENT_DATE AND ae.deleted IS NOT TRUE AND et.expense_title = ? ORDER BY ae.expense_date";
                    break;
                case "weekly report":
                    sql = "SELECT ae.expense_date, et.expense_title, ae.particular, ae.expense_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner WHERE ae_inner.expense_date >= DATE_TRUNC('week', CURRENT_DATE) AND ae_inner.expense_date <= CURRENT_DATE AND ae_inner.deleted IS NOT TRUE) AS total_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner JOIN expense_type et_inner ON ae_inner.expense_type_id = et_inner.expense_type_id WHERE ae_inner.expense_date >= DATE_TRUNC('week', CURRENT_DATE) AND ae_inner.expense_date <= CURRENT_DATE AND ae_inner.deleted IS NOT TRUE AND et_inner.expense_title = ?) AS total_expense_amount FROM add_expense ae JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.expense_date >= DATE_TRUNC('week', CURRENT_DATE) AND ae.expense_date <= CURRENT_DATE AND ae.deleted IS NOT TRUE AND et.expense_title = ? ORDER BY ae.expense_date";
                    break;
                case "current month report":
                    sql = "SELECT ae.expense_date, et.expense_title, ae.particular, ae.expense_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner WHERE ae_inner.expense_date >= DATE_TRUNC('month', CURRENT_DATE) AND ae_inner.expense_date < DATE_TRUNC('month', CURRENT_DATE + INTERVAL '1 month') AND ae_inner.deleted IS NOT TRUE) AS total_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner JOIN expense_type et_inner ON ae_inner.expense_type_id = et_inner.expense_type_id WHERE ae_inner.expense_date >= DATE_TRUNC('month', CURRENT_DATE) AND ae_inner.expense_date < DATE_TRUNC('month', CURRENT_DATE + INTERVAL '1 month') AND ae_inner.deleted IS NOT TRUE AND et_inner.expense_title = ?) AS total_expense_amount FROM   add_expense ae JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.expense_date >= DATE_TRUNC('month', CURRENT_DATE) AND ae.expense_date < DATE_TRUNC('month', CURRENT_DATE + INTERVAL '1 month') AND ae.deleted IS NOT TRUE AND et.expense_title = ? ORDER BY ae.expense_date";
                    break;
                case "last month report":
                    sql = "SELECT ae.expense_date, et.expense_title, ae.particular, ae.expense_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner WHERE ae_inner.expense_date >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND ae_inner.expense_date < DATE_TRUNC('month', CURRENT_DATE) AND ae_inner.deleted IS NOT TRUE) AS total_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner JOIN expense_type et_inner ON ae_inner.expense_type_id = et_inner.expense_type_id WHERE ae_inner.expense_date >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND ae_inner.expense_date < DATE_TRUNC('month', CURRENT_DATE) AND ae_inner.deleted IS NOT TRUE AND et_inner.expense_title = ?) AS total_expense_amount FROM  add_expense ae JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.expense_date >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND ae.expense_date < DATE_TRUNC('month', CURRENT_DATE) AND ae.deleted IS NOT TRUE AND et.expense_title = ? ORDER BY ae.expense_date";
                    break;
                case "yearly report":
                    sql = " SELECT  ae.expense_date, et.expense_title, ae.particular, ae.expense_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner WHERE ae_inner.expense_date >= DATE_TRUNC('year', CURRENT_DATE) AND ae_inner.expense_date < DATE_TRUNC('year', CURRENT_DATE + INTERVAL '1 year') AND ae_inner.deleted IS NOT TRUE) AS total_amount, (SELECT SUM(ae_inner.expense_amount) FROM add_expense ae_inner JOIN expense_type et_inner ON ae_inner.expense_type_id = et_inner.expense_type_id WHERE ae_inner.expense_date >= DATE_TRUNC('year', CURRENT_DATE) AND ae_inner.expense_date < DATE_TRUNC('year', CURRENT_DATE + INTERVAL '1 year') AND ae_inner.deleted IS NOT TRUE AND et_inner.expense_title = ?) AS total_expense_amount FROM add_expense ae JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE ae.expense_date >= DATE_TRUNC('year', CURRENT_DATE) AND ae.expense_date < DATE_TRUNC('year', CURRENT_DATE + INTERVAL '1 year') AND ae.deleted IS NOT TRUE AND et.expense_title = ? ORDER BY ae.expense_date";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid report type: " +reportType);
            }
            return jdbcTemplate.query(sql, new Object[]{expenseTitle, expenseTitle}, new RowMapper<AddExpenseDetails>() {
                @Override
                public AddExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddExpenseDetails addExpenseDetails = new AddExpenseDetails();
                    addExpenseDetails.setExpenseDate(rs.getDate("expense_date"));
                    addExpenseDetails.setExpenseTitle(rs.getString("expense_title"));
                    addExpenseDetails.setParticular(rs.getString("particular"));
                    addExpenseDetails.setExpenseAmount(rs.getDouble("expense_amount"));
                    addExpenseDetails.setTotalAmount(rs.getDouble("total_amount"));
                    addExpenseDetails.setTotalExpenseAmount(rs.getDouble("total_expense_amount"));
                    return addExpenseDetails;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<AddExpenseDetails> getExpenseDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "SELECT ae.add_expense_id, et.expense_title, ae.particular, ae.expense_date, ae.expense_amount FROM add_expense ae INNER JOIN expense_type et ON ae.expense_type_id = et.expense_type_id WHERE CONCAT_WS(' ', ae.add_expense_id, et.expense_title, ae.particular, ae.expense_date, ae.expense_amount) ILIKE ? AND ae.deleted IS NOT TRUE AND et.deleted IS NOT TRUE";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddExpenseDetails> addExpenseDetails = null;
        try{
            addExpenseDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<AddExpenseDetails>() {
                @Override
                public AddExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddExpenseDetails aed = new AddExpenseDetails();
                    aed.setAddExpenseId(rs.getInt("add_expense_id"));
                    aed.setExpenseTitle(rs.getString("expense_title"));
                    aed.setParticular(rs.getString("particular"));
                    aed.setExpenseDate(rs.getDate("expense_date"));
                    aed.setExpenseAmount(rs.getDouble("expense_amount"));
                    return aed;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addExpenseDetails;
    }
}
