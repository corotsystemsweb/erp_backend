package com.sms.dao.impl;

import com.sms.dao.UserRegistrationDao;
import com.sms.model.UserRegistrationDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class UserRegistrationDaoImpl implements UserRegistrationDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRegistrationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM school_registration  WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        if(count !=null && count > 0){
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        return count !=null ? count : 0;
    }

    @Override
    public UserRegistrationDetails adduser(UserRegistrationDetails userRegistrationDetails) throws Exception {
        String sql = "insert into school_registration(first_name, last_name, phone_number, email, password, school_code, role_id, created_date, is_active) values(?,?,?,?,?,?,?,?,?)";
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, userRegistrationDetails.getFirstName());
                ps.setString(2, userRegistrationDetails.getLastName());
                ps.setString(3, CipherUtils.encrypt(userRegistrationDetails.getPhoneNumber()));
                ps.setString(4, userRegistrationDetails.getEmail());
                ps.setString(5, CipherUtils.encrypt(userRegistrationDetails.getPassword()));
                ps.setString(6,userRegistrationDetails.getSchoolCode());
                //set role_id as an SQL ARRAY
                Integer[] roleIds = userRegistrationDetails.getRoleId();
                if(roleIds != null && roleIds.length > 0){
                    java.sql.Array sqlArray = connection.createArrayOf("INTEGER", roleIds);
                    ps.setArray(7, sqlArray);
                }else {
                    ps.setNull(7, java.sql.Types.ARRAY);
                }
                ps.setDate(8, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setBoolean(9, true);
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("user_id")){
               int generatedId =  ((Number)keys.get("user_id")).intValue();
               userRegistrationDetails.setUserId(generatedId);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("User registration failed " + e);
        }
        return userRegistrationDetails;
    }

    @Override
    public List<UserRegistrationDetails> getAllUserDetails(String schoolCode) throws Exception {
        //String sql = "select sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code, sr.role_id, r.role from school_registration as sr INNER JOIN role as r ON sr.role_id = r.id where sr.school_code = ? order by sr.user_id asc";
        String sql = """
                select
                    sr.user_id,
                    sr.first_name,
                    sr.last_name,
                    sr.phone_number,
                    sr.email,
                    sr.school_code ,
                    array_agg(r.id) as role_ids,
                    array_agg(r.role) AS roles,
                    sr.is_active
                FROM
                    school_registration sr
                JOIN
                    role r ON r.id = ANY(sr.role_id)
                WHERE
                    sr.school_code = ?
                GROUP BY
                    sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code, is_active
                ORDER BY sr.user_id ASC
                """;
        List<UserRegistrationDetails> userRegistrationDetails = null;
        try{
            userRegistrationDetails = jdbcTemplate.query(sql, new Object[]{schoolCode}, new RowMapper<UserRegistrationDetails>() {
                @Override
                public UserRegistrationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    UserRegistrationDetails urd = new UserRegistrationDetails();
                    urd.setUserId(rs.getInt("user_id"));
                    urd.setFirstName(rs.getString("first_name"));
                    urd.setLastName(rs.getString("last_name"));
                    urd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    urd.setEmail(rs.getString("email"));
                    urd.setSchoolCode(rs.getString("school_code"));
                    urd.setRoleId((Integer[]) rs.getArray("role_ids").getArray());
                    Array rolesArray = rs.getArray("roles");
                    String[] roles = (String[])rolesArray.getArray();
                    List<String> lowerCaseRoles = Arrays.stream(roles).map(String::toLowerCase).toList();
                    urd.setRole(lowerCaseRoles);
                    urd.setActive(rs.getBoolean("is_active"));
                    return urd;
                }
            });
            return userRegistrationDetails;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public UserRegistrationDetails getUserDetailsById(int userId, String schoolCode) throws Exception {
        //String sql = "select sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code, sr.role_id, r.role from school_registration as sr INNER JOIN role as r ON sr.role_id = r.id where sr.user_id = ? and sr.school_code = ?";
        String sql = """
                select
                      sr.user_id,
                      sr.first_name,
                      sr.last_name,
                      sr.phone_number,
                      sr.email,
                      sr.school_code,
                      array_agg(r.id) as role_ids,
                      array_agg(r.role) AS roles,
                      sr.is_active
                FROM
                    school_registration sr
                JOIN
                    role r ON r.id = ANY(sr.role_id)
                where
                     sr.user_id = ?
                     AND sr.school_code = ?
                GROUP BY
                    sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code, is_active
                """;
        UserRegistrationDetails userRegistrationDetails = null;
        try{
            userRegistrationDetails = jdbcTemplate.queryForObject(sql, new Object[]{userId, schoolCode}, new RowMapper<UserRegistrationDetails>() {
                @Override
                public UserRegistrationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    UserRegistrationDetails urd = new UserRegistrationDetails();
                    urd.setUserId(rs.getInt("user_id"));
                    urd.setFirstName(rs.getString("first_name"));
                    urd.setLastName(rs.getString("last_name"));
                    urd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    urd.setEmail(rs.getString("email"));
                    urd.setSchoolCode(rs.getString("school_code"));
                    urd.setRoleId((Integer[]) rs.getArray("role_ids").getArray());
                    Array rolesArray = rs.getArray("roles");
                    String[] roles = (String[]) rolesArray.getArray();
                    List<String> lowerCaseRoles = Arrays.stream(roles).map(String::toLowerCase).toList();
                    urd.setRole(lowerCaseRoles);
                    urd.setActive(rs.getBoolean("is_active"));
                    return urd;
                }
            });
            return userRegistrationDetails;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public String updateUserDetailsById(UserRegistrationDetails userRegistrationDetails, int userId, String schoolCode) throws Exception {
        String sql = "update school_registration set first_name = ?, last_name = ?, phone_number = ?, email = ?, password = ?, role_id = ? where user_id = ? and school_code = ?";
        try{
            int rowAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, userRegistrationDetails.getFirstName());
                ps.setString(2, userRegistrationDetails.getLastName());
                ps.setString(3, CipherUtils.encrypt(userRegistrationDetails.getPhoneNumber()));
                ps.setString(4, userRegistrationDetails.getEmail());
                ps.setString(5, CipherUtils.encrypt(userRegistrationDetails.getPassword()));
                //set role_id as an sql array
                Integer[] roleIds = userRegistrationDetails.getRoleId();
                if(roleIds != null && roleIds.length > 0){
                    java.sql.Array sqlArray = connection.createArrayOf("INTEGER", roleIds);
                    ps.setArray(6, sqlArray);
                }else{
                    ps.setNull(6, java.sql.Types.ARRAY);
                }
                ps.setInt(7, userId);
                ps.setString(8, schoolCode);
                return ps;
            });
            return rowAffected > 0 ? "Registration details updated" : "Registration details not updated";
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Update failed", e);
        }
    }

    @Override
    public boolean deleteUser(int userId, String schoolCode) throws Exception {
        String sql = "delete from school_registration where user_id = ? and school_code = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, userId, schoolCode);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to delete user: " + e.getMessage());
        }
    }

    @Override
    public List<UserRegistrationDetails> getAllRole() throws Exception {
        String sql = "SELECT id, role FROM role WHERE role NOT ILIKE 'STUDENT' AND role NOT ILIKE 'Admin' AND role NOT ILIKE 'Super Admin' ORDER BY id ASC";
        List<UserRegistrationDetails> userRegistrationDetails;
        try{
            userRegistrationDetails = jdbcTemplate.query(sql, new RowMapper<UserRegistrationDetails>() {
                @Override
                public UserRegistrationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    UserRegistrationDetails urd = new UserRegistrationDetails();
                    urd.setAllRoleId(rs.getInt("id"));
                    urd.setAllRole(rs.getString("role"));
                    return urd;
                }
            });
            return userRegistrationDetails;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
