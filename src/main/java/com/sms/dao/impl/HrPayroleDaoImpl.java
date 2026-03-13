package com.sms.dao.impl;

import com.sms.dao.HrPayroleDao;
import com.sms.model.BankDetails;
import com.sms.model.HrPayroleDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
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
public class HrPayroleDaoImpl implements HrPayroleDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public HrPayroleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public HrPayroleDetails addSalary(HrPayroleDetails hrPayroleDetails, String schoolCode) throws Exception {
        String sql = "insert into staff_salary (school_id,session_id, staff_id, department_id, designation_id, salary_amount) values (?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,hrPayroleDetails.getSchoolId());
                ps.setInt(2,hrPayroleDetails.getSessionId());
                ps.setInt(3,hrPayroleDetails.getStaffId());
                ps.setInt(4, hrPayroleDetails.getDepartmentId());
                ps.setInt(5, hrPayroleDetails.getDesignationId());
                ps.setString(6, hrPayroleDetails.getSalaryAmount());
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("ss_id")){
                int generatedKey = ((Number) keys.get("ss_id")).intValue();
                hrPayroleDetails.setSsId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return hrPayroleDetails;
    }

    @Override
    public HrPayroleDetails getAllSalaryById(int ssId, String schoolCode) throws Exception {
      //  String sql = "SELECT ss.ss_id,sd.sd_id,sd.designation AS designation, s.staff_id, CONCAT(s.first_name, ' ', s.last_name) AS staff_name ,ss.salary_amount FROM staff_salary ss INNER JOIN staff s ON ss.staff_id = s.staff_id INNER JOIN staff_designation sd ON ss.designation_id = sd.sd_id WHERE ss.deleted is not true and ss_id=?";
        String sql = "SELECT \n" +
                "    ss.ss_id,\n" +
                "    sd.designation,\n" +
                "\tss.staff_id,\n" +
                "\tss.designation_id,\n" +
                "    CASE \n" +
                "        WHEN s.staff_id IS NOT NULL AND sd.designation ILIKE 'driver' THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name)\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        ELSE 'Unknown'\n" +
                "    END AS staff_name,\n" +
                "    ss.salary_amount\n" +
                "FROM \n" +
                "    staff_salary ss\n" +
                "LEFT JOIN \n" +
                "    staff_designation sd ON ss.designation_id = sd.sd_id\n" +
                "LEFT JOIN \n" +
                "    staff s ON ss.staff_id = s.staff_id AND sd.designation NOT ILIKE 'driver' AND COALESCE(s.deleted, false) IS NOT TRUE\n" +
                "LEFT JOIN \n" +
                "    add_driver ad ON ss.staff_id = ad.driver_id AND sd.designation ILIKE 'driver'\n" +
                "WHERE \n" +
                "    ss.ss_id = ? \n" +
                "    AND COALESCE(ss.deleted, false) IS NOT TRUE\n" +
                "ORDER BY \n" +
                "    ss.ss_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        HrPayroleDetails hrPayroleDetails=null;
        try{
            hrPayroleDetails=jdbcTemplate.queryForObject(sql, new Object[]{ssId},new RowMapper<HrPayroleDetails>() {
                @Override
                public HrPayroleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HrPayroleDetails ss=new HrPayroleDetails();
                    ss.setSsId(rs.getInt("ss_id"));
                    ss.setDesignation(rs.getString("designation"));
                    ss.setStaffId(rs.getInt("staff_id"));
                    ss.setDesignationId(rs.getInt("designation_id"));
                    ss.setStaffName(rs.getString("staff_name"));
                    ss.setSalaryAmount(rs.getString("salary_amount"));
                    return ss;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return hrPayroleDetails;
    }

    @Override
    public List<HrPayroleDetails> getAllSalary(String schoolCode) throws Exception {
       // String sql = "SELECT ss.ss_id,sd.designation AS designation, CONCAT(s.first_name, ' ', s.last_name) AS staff_name ,ss.salary_amount FROM staff_salary ss INNER JOIN staff s ON ss.staff_id = s.staff_id INNER JOIN staff_designation sd ON ss.designation_id = sd.sd_id WHERE ss.deleted is not true";
        String sql="SELECT\n" +
                "    ss.ss_id,\n" +
                "    sd.designation,\n" +
                "\tss.staff_id,\n" +
                "\tss.designation_id,\n" +
                "    CASE\n" +
                "        WHEN s.staff_id IS NOT NULL AND sd.designation ILIKE 'driver' THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name)\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        ELSE 'Unknown'\n" +
                "    END AS staff_name,\n" +
                "    ss.salary_amount\n" +
                "FROM\n" +
                "    staff_salary ss\n" +
                "LEFT JOIN\n" +
                "    staff_designation sd ON ss.designation_id = sd.sd_id\n" +
                "LEFT JOIN\n" +
                "    staff s ON ss.staff_id = s.staff_id AND sd.designation NOT ILIKE 'driver' AND COALESCE(s.deleted, false) IS NOT TRUE\n" +
                "LEFT JOIN\n" +
                "    add_driver ad ON ss.staff_id = ad.driver_id AND sd.designation ILIKE 'driver'\n" +
                "WHERE\n" +
                "    COALESCE(ss.deleted, false) IS NOT TRUE\n" +
                "ORDER BY\n" +
                "    ss.ss_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<HrPayroleDetails>hrPayroleDetails=null;
        try{
            hrPayroleDetails=jdbcTemplate.query(sql, new RowMapper<HrPayroleDetails>() {
                @Override
                public HrPayroleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HrPayroleDetails ss=new HrPayroleDetails();
                    ss.setSsId(rs.getInt("ss_id"));
                    ss.setDesignation(rs.getString("designation"));
                    ss.setStaffId(rs.getInt("staff_id"));
                    ss.setDesignationId(rs.getInt("designation_id"));
                    ss.setStaffName(rs.getString("staff_name"));
                    ss.setSalaryAmount(rs.getString("salary_amount"));
                    return ss;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return hrPayroleDetails;
    }

    @Override
    public HrPayroleDetails updateSalaryDetails(HrPayroleDetails hrPayroleDetails, int ssId, String schoolCode) throws Exception {
        String sql = "UPDATE staff_salary SET staff_id=?, school_id=?, session_id=?, department_id = ?, designation_id=?, salary_amount=? WHERE ss_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    hrPayroleDetails.getStaffId(),
                    hrPayroleDetails.getSchoolId(),
                    hrPayroleDetails.getSessionId(),
                    hrPayroleDetails.getDepartmentId(),
                    hrPayroleDetails.getDesignationId(),
                    hrPayroleDetails.getSalaryAmount(),
                    ssId);
            if (rowAffected > 0) {
                return hrPayroleDetails;
            } else {
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
    public boolean softDeletdSalary(int ssId, String schoolCode) throws Exception {
        String sql = "UPDATE staff_salary SET deleted = TRUE WHERE ss_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, ssId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<HrPayroleDetails> getSalaryDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "SELECT\n" +
                "    sd.designation,\n" +
                "    CASE\n" +
                "        WHEN s.staff_id IS NOT NULL AND sd.designation ILIKE 'driver' THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name)\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        ELSE 'Unknown'\n" +
                "    END AS staff_name,\n" +
                "    ss.salary_amount\n" +
                "FROM\n" +
                "    staff_salary ss\n" +
                "LEFT JOIN\n" +
                "    staff_designation sd ON ss.designation_id = sd.sd_id\n" +
                "LEFT JOIN\n" +
                "    staff s ON ss.staff_id = s.staff_id AND sd.designation NOT ILIKE 'driver' AND COALESCE(s.deleted, false) IS NOT TRUE\n" +
                "LEFT JOIN\n" +
                "    add_driver ad ON ss.staff_id = ad.driver_id AND sd.designation ILIKE 'driver'\n" +
                "WHERE\n" +
                "    COALESCE(ss.deleted, false) IS NOT TRUE\n" +
                "    AND CONCAT_WS(' ', sd.designation, s.first_name, s.last_name, ad.first_name, ad.last_name, ss.salary_amount::TEXT) ILIKE ?\n" +
                "ORDER BY\n" +
                "    ss.ss_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<HrPayroleDetails> hrPayroleDetails = null;
        try{
            hrPayroleDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<HrPayroleDetails>() {
                @Override
                public HrPayroleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HrPayroleDetails hrpd = new HrPayroleDetails();
                    hrpd.setDesignation(rs.getString("designation"));
                    hrpd.setStaffName(rs.getString("staff_name"));
                    hrpd.setSalaryAmount(rs.getString("salary_amount"));
                    return hrpd;
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
        return hrPayroleDetails;
    }

    @Override
    public HrPayroleDetails getSalaryByStaffId(int staffId, String schoolCode) throws Exception {
        String sql = "SELECT ss_id, salary_amount FROM staff_salary WHERE staff_id = ? AND COALESCE(deleted, false) IS NOT TRUE ORDER BY ss_id DESC LIMIT 1";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        HrPayroleDetails hrPayroleDetails = null;
        try{
            hrPayroleDetails = jdbcTemplate.queryForObject(sql, new Object[]{staffId}, new RowMapper<HrPayroleDetails>() {
                @Override
                public HrPayroleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HrPayroleDetails hrpd = new HrPayroleDetails();
                    hrpd.setSsId(rs.getInt("ss_id"));
                    hrpd.setSalaryAmount(rs.getString("salary_amount"));
                    return hrpd;
                }
            });
        } catch (Exception e){
           return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return hrPayroleDetails;
    }

}
