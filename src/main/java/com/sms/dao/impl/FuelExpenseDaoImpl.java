package com.sms.dao.impl;

import com.sms.dao.FuelExpenseDao;
import com.sms.model.FuelExpenseDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
public class FuelExpenseDaoImpl implements FuelExpenseDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FuelExpenseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FuelExpenseDetails addFuelExpense(FuelExpenseDetails fuelExpenseDetails, String schoolCode) throws Exception {
        String sql = "insert into fuel_expense (school_id, session_id, vehicle_id, fuel_date, fuel_liter, refuel_amount, updated_by, update_date_time) values(?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, fuelExpenseDetails.getSchoolId());
                ps.setInt(2, fuelExpenseDetails.getSessionId());
                ps.setInt(3, fuelExpenseDetails.getVehicleId());
                ps.setDate(4, new java.sql.Date(fuelExpenseDetails.getFuelDate().getTime()));
                ps.setDouble(5, fuelExpenseDetails.getFuelLiter());
                ps.setDouble(6, fuelExpenseDetails.getRefuelAmount());
                ps.setInt(7, fuelExpenseDetails.getUpdatedBy());
                ps.setDate(8, new java.sql.Date(fuelExpenseDetails.getUpdatedDateTime().getTime()));
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("fuel_expense_id")){
                int generatedKey = ((Number) keys.get("fuel_expense_id")).intValue();
                fuelExpenseDetails.setFuelExpenseId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return fuelExpenseDetails;
    }

    @Override
    public FuelExpenseDetails getFuelExpenseById(int fuelExpenseId, String schoolCode) throws Exception {
        String sql = "SELECT fe.fuel_expense_id, fe.school_id, fe.session_id, fe.vehicle_id, av.vehicle_number, fe.fuel_date, fe.fuel_liter, fe.refuel_amount, fe.updated_by, fe.update_date_time FROM fuel_expense fe INNER JOIN add_vehicle av ON fe.vehicle_id = av.vehicle_id WHERE fe.fuel_expense_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        FuelExpenseDetails fuelExpenseDetails = null;
        try{
            fuelExpenseDetails = jdbcTemplate.queryForObject(sql, new Object[]{fuelExpenseId}, new RowMapper<FuelExpenseDetails>() {
                @Override
                public FuelExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FuelExpenseDetails fed = new FuelExpenseDetails();
                    fed.setFuelExpenseId(rs.getInt("fuel_expense_id"));
                    fed.setSchoolId(rs.getInt("school_id"));
                    fed.setSessionId(rs.getInt("session_id"));
                    fed.setVehicleId(rs.getInt("vehicle_id"));
                    fed.setVehicleNumber(rs.getString("vehicle_number"));
                    fed.setFuelDate(rs.getDate("fuel_date"));
                    fed.setFuelLiter(rs.getDouble("fuel_liter"));
                    fed.setRefuelAmount(rs.getDouble("refuel_amount"));
                    fed.setUpdatedBy(rs.getInt("updated_by"));
                    fed.setUpdatedDateTime(rs.getDate("update_date_time"));
                    return fed;
                }
            });
            return fuelExpenseDetails;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<FuelExpenseDetails> getAllFuelExpense(String schoolCode) throws Exception {
        String sql = "SELECT fe.fuel_expense_id, fe.school_id, fe.session_id, fe.vehicle_id, av.vehicle_number, fe.fuel_date, fe.fuel_liter, fe.refuel_amount, fe.updated_by, fe.update_date_time FROM fuel_expense fe INNER JOIN add_vehicle av ON fe.vehicle_id = av.vehicle_id where deleted is not true order by fe.fuel_expense_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FuelExpenseDetails> fuelExpenseDetails = null;
        try{
            fuelExpenseDetails = jdbcTemplate.query(sql, new RowMapper<FuelExpenseDetails>() {
                @Override
                public FuelExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FuelExpenseDetails fed = new FuelExpenseDetails();
                    fed.setFuelExpenseId(rs.getInt("fuel_expense_id"));
                    fed.setSchoolId(rs.getInt("school_id"));
                    fed.setSessionId(rs.getInt("session_id"));
                    fed.setVehicleId(rs.getInt("vehicle_id"));
                    fed.setVehicleNumber(rs.getString("vehicle_number"));
                    fed.setFuelDate(rs.getDate("fuel_date"));
                    fed.setFuelLiter(rs.getDouble("fuel_liter"));
                    fed.setRefuelAmount(rs.getDouble("refuel_amount"));
                    fed.setUpdatedBy(rs.getInt("updated_by"));
                    fed.setUpdatedDateTime(rs.getDate("update_date_time"));
                    return fed;
                }
            });
            return fuelExpenseDetails;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public FuelExpenseDetails updateFuelExpense(FuelExpenseDetails fuelExpenseDetails, int fuelExpenseId, String schoolCode) throws Exception {
        String sql = "update fuel_expense set school_id = ?, session_id = ?, vehicle_id = ?, fuel_date = ?, fuel_liter = ?, refuel_amount = ?, updated_by = ?, update_date_time = ? where fuel_expense_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    fuelExpenseDetails.getSchoolId(),
                    fuelExpenseDetails.getSessionId(),
                    fuelExpenseDetails.getVehicleId(),
                    fuelExpenseDetails.getFuelDate(),
                    fuelExpenseDetails.getFuelLiter(),
                    fuelExpenseDetails.getRefuelAmount(),
                    fuelExpenseDetails.getUpdatedBy(),
                    fuelExpenseDetails.getUpdatedDateTime(),
                    fuelExpenseId);
            if(rowAffected > 0){
                return fuelExpenseDetails;
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
    public boolean deleteFuelExpense(int fuelExpenseId, String schoolCode) throws Exception {
        String sql = "UPDATE fuel_expense SET deleted = TRUE WHERE fuel_expense_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, fuelExpenseId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public List<FuelExpenseDetails> getFuelExpenseReport(String reportType, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            String sql = "";
            switch (reportType.toLowerCase()) {
                case "daily report":
                   sql = "SELECT fuel_date, fuel_liter, refuel_amount, (SELECT SUM(fuel_liter) FROM fuel_expense WHERE fuel_date = CURRENT_DATE AND (deleted IS NOT true)) AS total_fuel_liter, (SELECT SUM(refuel_amount) FROM fuel_expense WHERE fuel_date = CURRENT_DATE AND (deleted IS NOT true)) AS total_refuel_amount FROM fuel_expense WHERE fuel_date = CURRENT_DATE AND (deleted IS NOT true) ORDER BY fuel_date";
                    break;
                case "weekly report":
                    sql = "SELECT fuel_date, fuel_liter, refuel_amount, (SELECT SUM(fuel_liter) FROM fuel_expense WHERE date_trunc('week', fuel_date) = date_trunc('week', CURRENT_DATE) AND (deleted IS NOT true)) AS total_fuel_liter, (SELECT SUM(refuel_amount) FROM fuel_expense WHERE date_trunc('week', fuel_date) = date_trunc('week', CURRENT_DATE) AND (deleted IS NOT true)) AS total_refuel_amount FROM fuel_expense WHERE date_trunc('week', fuel_date) = date_trunc('week', CURRENT_DATE) AND (deleted IS NOT true) ORDER BY fuel_date";
                    break;
                case "current month report":
                    sql = "SELECT fuel_date, fuel_liter, refuel_amount, (SELECT SUM(fuel_liter) FROM fuel_expense WHERE date_trunc('month', fuel_date) = date_trunc('month', CURRENT_DATE) AND (deleted IS NOT true)) AS total_fuel_liter, (SELECT SUM(refuel_amount) FROM fuel_expense WHERE date_trunc('month', fuel_date) = date_trunc('month', CURRENT_DATE) AND (deleted IS NOT true)) AS total_refuel_amount FROM fuel_expense WHERE date_trunc('month', fuel_date) = date_trunc('month', CURRENT_DATE) AND (deleted IS NOT true) ORDER BY fuel_date";
                    break;
                case "last month report":
                    sql = "SELECT fuel_date, fuel_liter, refuel_amount, (SELECT SUM(fuel_liter) FROM fuel_expense WHERE date_trunc('month', fuel_date) = date_trunc('month', CURRENT_DATE) - interval '1 month' AND (deleted IS NOT true)) AS total_fuel_liter, (SELECT SUM(refuel_amount) FROM fuel_expense WHERE date_trunc('month', fuel_date) = date_trunc('month', CURRENT_DATE) - interval '1 month' AND (deleted IS NOT true)) AS total_refuel_amount FROM fuel_expense WHERE date_trunc('month', fuel_date) = date_trunc('month', CURRENT_DATE) - interval '1 month' AND (deleted IS NOT true) ORDER BY fuel_date";
                    break;
                case "yearly report":
                    sql = "SELECT fuel_date, fuel_liter, refuel_amount, (SELECT SUM(fuel_liter) FROM fuel_expense WHERE date_trunc('year', fuel_date) = date_trunc('year', CURRENT_DATE) AND (deleted IS NOT true)) AS total_fuel_liter, (SELECT SUM(refuel_amount) FROM fuel_expense WHERE date_trunc('year', fuel_date) = date_trunc('year', CURRENT_DATE) AND (deleted IS NOT true)) AS total_refuel_amount FROM fuel_expense WHERE date_trunc('year', fuel_date) = date_trunc('year', CURRENT_DATE) AND (deleted IS NOT true) ORDER BY fuel_date";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid report type: " + reportType);
            }
            return jdbcTemplate.query(sql, new RowMapper<FuelExpenseDetails>() {
                @Override
                public FuelExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FuelExpenseDetails fuelExpenseDetails = new FuelExpenseDetails();
                    fuelExpenseDetails.setFuelDate(rs.getDate("fuel_date"));
                    fuelExpenseDetails.setFuelLiter(rs.getDouble("fuel_liter"));
                    fuelExpenseDetails.setRefuelAmount(rs.getDouble("refuel_amount"));
                    fuelExpenseDetails.setTotalFuelLiter(rs.getDouble("total_fuel_liter"));
                    fuelExpenseDetails.setTotalRefuelAmount(rs.getDouble("total_refuel_amount"));
                    return fuelExpenseDetails;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<FuelExpenseDetails> getFuelExpenseReportBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "SELECT fe.fuel_expense_id, av.vehicle_number, fe.fuel_date, fe.fuel_liter, fe.refuel_amount FROM fuel_expense fe INNER JOIN add_vehicle av ON fe.vehicle_id = av.vehicle_id where CONCAT_WS(' ', fe.fuel_expense_id, av.vehicle_number, fe.fuel_date, fe.fuel_liter, fe.refuel_amount) ILIKE ? AND deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FuelExpenseDetails> fuelExpenseDetails = null;
        try{
            fuelExpenseDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<FuelExpenseDetails>() {
                @Override
                public FuelExpenseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FuelExpenseDetails fed = new FuelExpenseDetails();
                    fed.setFuelExpenseId(rs.getInt("fuel_expense_id"));
                    fed.setVehicleNumber(rs.getString("vehicle_number"));
                    fed.setFuelDate(rs.getDate("fuel_date"));
                    fed.setFuelLiter(rs.getDouble("fuel_liter"));
                    fed.setRefuelAmount(rs.getDouble("refuel_amount"));
                    return fed;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return fuelExpenseDetails;
    }
}
