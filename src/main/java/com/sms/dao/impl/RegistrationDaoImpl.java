package com.sms.dao.impl;

import com.sms.dao.RegistrationDao;
import com.sms.model.RegistrationDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class RegistrationDaoImpl implements RegistrationDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RegistrationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM school_registration WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        if(count != null && count > 0){
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        return count != null ? count : 0;
    }
    @Override
    public RegistrationDetails registration(RegistrationDetails registrationDetails) throws Exception{
        String sql = "INSERT INTO school_registration(first_name, last_name, phone_number, email, password, school_code, role_id, created_date, is_active) VALUES(?,?,?,?,?,?,?,?,?)";
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, registrationDetails.getFirstName());
                ps.setString(2, registrationDetails.getLastName());
                ps.setString(3, registrationDetails.getPhoneNumber());
                ps.setString(4, registrationDetails.getEmail());
                ps.setString(5, registrationDetails.getPassword());
                ps.setString(6, registrationDetails.getSchoolCode().toLowerCase());
                //set role_id as an SQL array
                Integer[] roleIds = registrationDetails.getRoleId();
                if(roleIds != null && roleIds.length > 0){
                    java.sql.Array sqlArray = connection.createArrayOf("INTEGER", roleIds);
                    ps.setArray(7, sqlArray);
                } else {
                    ps.setNull(7, java.sql.Types.ARRAY);
                }

                ps.setDate(8, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setBoolean(9, true);

                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("user_id")){
                int generatedId = ((Number)keys.get("user_id")).intValue();
                registrationDetails.setUserId(generatedId);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Registration failed", e);
        }
        return registrationDetails;
    }
    @Override
    public RegistrationDetails login(String email, String password) {
        String encryptedPassword = CipherUtils.encrypt(password);
        String hasEmail = "SELECT COUNT(*) FROM school_registration WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(hasEmail, Integer.class, email);
        if(count == null && count == 0){
            throw new IllegalArgumentException("Invalid username ");
        }
        //String sql = "select r.role, sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code FROM school_registration sr , role r WHERE sr.email = ? AND sr.password = ? and r.id = sr.role_id";
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
                    sr.email = ?
                    AND sr.password = ?
                    and is_active = true
                GROUP BY
                    sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code, is_active
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email, encryptedPassword}, (resultSet, i) -> {
                RegistrationDetails registrationDetails = new RegistrationDetails();
                registrationDetails.setUserId(resultSet.getInt("user_id"));
                registrationDetails.setFirstName(resultSet.getString("first_name"));
                registrationDetails.setLastName(resultSet.getString("last_name"));
                registrationDetails.setPhoneNumber(CipherUtils.decrypt(resultSet.getString("phone_number")));
                registrationDetails.setEmail(resultSet.getString("email"));
                //loginDetails.setPassword(resultSet.getString("password"));
                registrationDetails.setSchoolCode(resultSet.getString("school_code").toLowerCase());
                registrationDetails.setRoleId((Integer[])resultSet.getArray("role_ids").getArray());
                Array rolesArray = resultSet.getArray("roles");
                String[] roles = (String[]) rolesArray.getArray();
                List<String> lowerCaseRoles = Arrays.stream(roles)
                        .map(String::toLowerCase)
                        .toList();
                registrationDetails.setRole(lowerCaseRoles);
                registrationDetails.setActive(resultSet.getBoolean("is_active"));
                return registrationDetails;
            });
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public RegistrationDetails getUserByEmail(String email){
        String sql = "select r.role, sr.user_id, sr.first_name, sr.last_name, sr.phone_number, sr.email, sr.school_code FROM school_registration sr , role r WHERE sr.email = ? and r.id = sr.role_id";
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, (resultSet, i) -> {
                RegistrationDetails user = new RegistrationDetails();
                user.setUserId(resultSet.getInt("user_id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setPhoneNumber(CipherUtils.decrypt(resultSet.getString("phone_number")));
                user.setEmail(resultSet.getString("email"));
                user.setSchoolCode(resultSet.getString("school_code").toLowerCase());
                user.setRole(List.of((String[])resultSet.getArray("role").getArray()));
                return user;
            });
        }catch(Exception e){
            return null;
        }
    }
}
