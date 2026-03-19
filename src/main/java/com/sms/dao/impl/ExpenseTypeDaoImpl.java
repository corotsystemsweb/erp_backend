package com.sms.dao.impl;

import com.sms.dao.ExpenseTypeDao;
import com.sms.model.ExpenseType;
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
public class ExpenseTypeDaoImpl implements ExpenseTypeDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExpenseTypeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExpenseType addExpenseType(ExpenseType expenseType, String schoolCode) throws Exception {
        String sql = "insert into expense_type (school_id, session_id, expense_title, description, updated_by, update_date_time) values(?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, expenseType.getSchoolId());
                ps.setInt(2, expenseType.getSessionId());
                ps.setString(3, expenseType.getExpenseTitle());
                ps.setString(4, expenseType.getDescription());
                ps.setInt(5, expenseType.getUpdatedBy());
                ps.setDate(6, new java.sql.Date(expenseType.getUpdateDateTime().getTime()));
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys !=null && keys.containsKey("expense_type_id")){
                int generatedKey = ((Number)keys.get("expense_type_id")).intValue();
                expenseType.setExpenseTypeId(generatedKey);
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return expenseType;
    }

    @Override
    public ExpenseType getExpenseTypeById(int expenseTypeId, String schoolCode) throws Exception {
        String sql = "select expense_type_id, school_id, session_id, expense_title, description, updated_by, update_date_time from expense_type where expense_type_id = ? and deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ExpenseType expenseType = null;
        try{
            expenseType = jdbcTemplate.queryForObject(sql, new Object[]{expenseTypeId}, new RowMapper<ExpenseType>() {
                @Override
                public ExpenseType mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExpenseType et = new ExpenseType();
                    et.setExpenseTypeId(rs.getInt("expense_type_id"));
                    et.setSchoolId(rs.getInt("school_id"));
                    et.setSessionId(rs.getInt("session_id"));
                    et.setExpenseTitle(rs.getString("expense_title"));
                    et.setDescription(rs.getString("description"));
                    et.setUpdatedBy(rs.getInt("updated_by"));
                    et.setUpdateDateTime(rs.getDate("update_date_time"));
                    return et;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return expenseType;
    }

    @Override
    public List<ExpenseType> getAllExpenseType(String schoolCode) throws Exception {
        String sql = "select expense_type_id, school_id, session_id, expense_title, description, updated_by, update_date_time from expense_type where deleted is not true order by expense_type_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ExpenseType> expenseType = null;
        try{
            expenseType = jdbcTemplate.query(sql, new RowMapper<ExpenseType>() {
                @Override
                public ExpenseType mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExpenseType et = new ExpenseType();
                    et.setExpenseTypeId(rs.getInt("expense_type_id"));
                    et.setSchoolId(rs.getInt("school_id"));
                    et.setSessionId(rs.getInt("session_id"));
                    et.setExpenseTitle(rs.getString("expense_title"));
                    et.setDescription(rs.getString("description"));
                    et.setUpdatedBy(rs.getInt("updated_by"));
                    et.setUpdateDateTime(rs.getDate("update_date_time"));
                    return et;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return expenseType;
    }

    @Override
    public ExpenseType updateExpenseType(ExpenseType expenseType, int expenseTypeId, String schoolCode) throws Exception {
        String sql = "update expense_type set expense_title = ?, description = ?, updated_by = ?, update_date_time = ? where expense_type_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    expenseType.getExpenseTitle(),
                    expenseType.getDescription(),
                    expenseType.getUpdatedBy(),
                    expenseType.getUpdateDateTime(),
                    expenseTypeId);
            if(rowAffected > 0){
                return expenseType;
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
    public boolean softDeleteExpenseType(int expenseTypeId, String schoolCode) throws Exception {
        String sql = "UPDATE expense_type SET deleted = TRUE WHERE expense_type_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, expenseTypeId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<ExpenseType> getExpenseTypeBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "select expense_title, description from expense_type where concat_ws(' ', expense_title, description) ILIKE ? AND deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ExpenseType> expenseTypes = null;
        try{
            expenseTypes = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<ExpenseType>() {
                @Override
                public ExpenseType mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExpenseType et = new ExpenseType();
                    et.setExpenseTitle(rs.getString("expense_title"));
                    et.setDescription(rs.getString("description"));
                    return et;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return expenseTypes;
    }
}
