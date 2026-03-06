package com.sms.dao.impl;

import com.sms.dao.StaffDepartmentDao;
import com.sms.model.StaffDepartment;
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
public class StaffDepartmentDaoImpl implements StaffDepartmentDao {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public StaffDepartmentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public StaffDepartment addStaffDepartment(StaffDepartment staffDepartment, String schoolCode) throws Exception {
        String sql="insert into staff_department(school_id, department,description,status) values(?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,staffDepartment.getSchoolId());
                ps.setString(2,staffDepartment.getDepartment());
                ps.setString(3,staffDepartment.getDescription());
                ps.setString(4, staffDepartment.getStatus());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("stdp_id")){
                int generatedId=((Number) keys.get("stdp_id")).intValue();
                staffDepartment.setStDpId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffDepartment ;
    }

    @Override
    public StaffDepartment getStaffDepartmentById(int stDpId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    d.stdp_id,
                    d.school_id,
                    d.department,
                    d.description,
                    d.status,
                    COUNT(s.staff_id) AS staff_count
                FROM staff_department d
                LEFT JOIN staff s
                    ON d.stdp_id = s.department_id
                    AND COALESCE(s.deleted, false) = false
                WHERE d.stdp_id = ?
                GROUP BY d.stdp_id, d.school_id, d.department, d.description, d.status;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        StaffDepartment staffDepartment = null;
        try {
            staffDepartment = jdbcTemplate.queryForObject(sql, new Object[]{stDpId}, new RowMapper<StaffDepartment>() {
                @Override
                public StaffDepartment mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDepartment td = new StaffDepartment();
                    td.setStDpId(rs.getInt("stdp_id"));
                    td.setSchoolId(rs.getInt("school_id"));
                    td.setDepartment(rs.getString("department"));
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
        return staffDepartment;
    }

    @Override
    public List<StaffDepartment> getAllStaffDepartment(String schoolCode) throws Exception {
        String sql = """
                SELECT
                    d.stdp_id,
                    d.school_id,
                    d.department,
                    d.description,
                    d.status,
                    COUNT(s.staff_id) AS staff_count
                FROM staff_department d
                LEFT JOIN staff s
                    ON d.stdp_id = s.department_id
                    AND (s.deleted = false OR s.deleted IS NULL)
                GROUP BY d.stdp_id, d.school_id, d.department, d.description, d.status
                ORDER BY d.stdp_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffDepartment> staffDepartment = null;
        try {
            staffDepartment = jdbcTemplate.query(sql, new RowMapper<StaffDepartment>() {
                @Override
                public StaffDepartment mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDepartment td = new StaffDepartment();
                    td.setStDpId(rs.getInt("stdp_id"));
                    td.setSchoolId(rs.getInt("school_id"));
                    td.setDepartment(rs.getString("department"));
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
        return staffDepartment;
    }

    @Override
    public StaffDepartment updateStaffDepartment(StaffDepartment staffDepartment, int sdId, String schoolCode) throws Exception {
        String sql = "UPDATE staff_department SET school_id=?,department=?,description=?, status=? WHERE stdp_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected=jdbcTemplate.update(sql,staffDepartment.getSchoolId(),staffDepartment.getDepartment(),staffDepartment.getDescription(),staffDepartment.getStatus(),sdId);
            if(rowAffected >0){
                return staffDepartment;
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
    public boolean deleteStaffDepartment(int stDpId, String schoolCode) throws Exception {
        String sql="DELETE FROM staff_department WHERE stdp_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowsAffected=jdbcTemplate.update(sql,new Object[]{stDpId});
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
