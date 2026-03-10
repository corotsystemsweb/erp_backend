package com.sms.dao.impl;

import com.sms.dao.StaffDesigDao;
import com.sms.model.StaffDesig;
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
import java.util.List;
import java.util.Map;

@Repository
public class StaffDesigDaoImpl implements StaffDesigDao {
    private final JdbcTemplate jdbcTemplate;
@Autowired
    public StaffDesigDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public StaffDesig addStaffDesig(StaffDesig staffDesig, String schoolCode) throws Exception {
        String sql="insert into staff_designation(school_id, designation,description, status, department_id) values(?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,staffDesig.getSchoolId());
                ps.setString(2,staffDesig.getDesignation());
                ps.setString(3,staffDesig.getDescription());
                ps.setString(4, staffDesig.getStatus());
                ps.setInt(5, staffDesig.getDepartmentId());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("sd_id")){
                int generatedId=((Number) keys.get("sd_id")).intValue();
                staffDesig.setSdId(generatedId);
            }
        }catch(Exception e) {
        e.printStackTrace();
        return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffDesig ;
    }

@Override
    public StaffDesig getStaffDesignById(int sdId, String schoolCode) throws Exception {
    String sql = """
            SELECT
                sd.sd_id,
                sd.school_id,
                d.department,
                sd.designation,
                sd.description,
                sd.status,
                COUNT(s.staff_id) AS staff_count
            FROM staff_designation sd
            LEFT JOIN staff_department d
                ON sd.department_id = d.stdp_id
            LEFT JOIN staff s
                ON sd.sd_id = s.designation_id
                AND COALESCE(s.deleted, false) = false
            WHERE sd.sd_id = ?
            GROUP BY sd.sd_id, sd.school_id, d.department, sd.designation, sd.description, sd.status;
            """;
    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
    StaffDesig staffDesig = null;
 try {
     staffDesig = jdbcTemplate.queryForObject(sql, new Object[]{sdId}, new RowMapper<StaffDesig>() {
         @Override
         public StaffDesig mapRow(ResultSet rs, int rowNum) throws SQLException {
             StaffDesig td = new StaffDesig();
             td.setSdId(rs.getInt("sd_id"));
             td.setSchoolId(rs.getInt("school_id"));
             td.setDepartment(rs.getString("department"));
             td.setDesignation(rs.getString("designation"));
             td.setDescription(rs.getString("description"));
             td.setStatus(rs.getString("status"));
             td.setStaffCount(rs.getInt("staff_count"));
             return td;
         }
     });
 }catch (Exception e){
     e.printStackTrace();
     return null;
 }finally {
     DatabaseUtil.closeDataSource(jdbcTemplate);
 }
    return staffDesig;
    }

    @Override
    public List<StaffDesig> getAllStaffDesig(String schoolCode) throws Exception {
    String sql = """
            SELECT
                sd.sd_id,
                sd.school_id,
                d.department,
                sd.designation,
                sd.description,
                sd.status,
                COUNT(s.staff_id) AS staff_count
            FROM staff_designation sd
            LEFT JOIN staff_department d
                ON sd.department_id = d.stdp_id
            LEFT JOIN staff s
                ON sd.sd_id = s.designation_id
                AND COALESCE(s.deleted, false) = false
            GROUP BY sd.sd_id, sd.school_id, d.department, sd.designation, sd.description, sd.status
            ORDER BY sd.sd_id;
            """;
    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
    List<StaffDesig> staffDesigs=null;
    try {
        staffDesigs = jdbcTemplate.query(sql, new RowMapper<StaffDesig>() {

            @Override
            public StaffDesig mapRow(ResultSet rs, int rowNum) throws SQLException {
                StaffDesig td = new StaffDesig();
                td.setSdId(rs.getInt("sd_id"));
                td.setSchoolId(rs.getInt("school_id"));
                td.setDepartment(rs.getString("department"));
                td.setDesignation(rs.getString("designation"));
                td.setDescription(rs.getString("description"));
                td.setStatus(rs.getString("status"));
                td.setStaffCount(rs.getInt("staff_count"));
                return td;
            }
        });
    } catch (Exception e){
        e.printStackTrace();
        return null;
    } finally {
        DatabaseUtil.closeDataSource(jdbcTemplate);
    }
        return staffDesigs;
    }

    @Override
    public StaffDesig updateStaffDesig(StaffDesig staffDesig, int sdId, String schoolCode) throws Exception {
 //String sql="UPDATE staff_designation SET designationName=?,description=? WHERE id=?";
        String sql = "UPDATE staff_designation SET designation=?,description=?, status=?, department_id=? WHERE sd_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        int rowAffected=jdbcTemplate.update(sql,staffDesig.getDesignation(),staffDesig.getDescription(),staffDesig.getStatus(),staffDesig.getDepartmentId(),sdId);
        if(rowAffected >0){
            return staffDesig;
        }else
        {
            return null;
        }
    } catch(Exception e){
           e.printStackTrace();
           return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }

    @Override
    public boolean deleteStaffDesig(int sdId, String schoolCode) throws Exception {
    String sql="DELETE FROM staff_designation WHERE sd_id=?";
    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
    try{
    int rowsAffected=jdbcTemplate.update(sql,new Object[]{sdId});
    if(rowsAffected>0)
    {
        return true;
    }else {
        return false;
    }
    } catch (Exception e){
        e.printStackTrace();
        return false;
    } finally {
        DatabaseUtil.closeDataSource(jdbcTemplate);
    }
    }

}
