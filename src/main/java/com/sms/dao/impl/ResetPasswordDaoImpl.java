package com.sms.dao.impl;

import com.sms.dao.ResetPasswordDao;
import com.sms.model.ResetPasswordDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ResetPasswordDaoImpl implements ResetPasswordDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ResetPasswordDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int checkEmailExist(String email) throws Exception {
        String sql = "select count(*) from school_registration where email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        if(count == null || count ==0){
            throw new IllegalArgumentException("Email not exists");
        }
        return count;
    }

    @Override
    public void updatePassword(String email, String encodedPassword) throws Exception {
        String sql = "UPDATE school_registration SET password = ? WHERE email = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, encodedPassword, email); // Correct parameter order

            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Failed to reset password");
            }
        } catch (DataAccessException e) {
            // Handle specific data access exceptions
            throw new Exception("Database error occurred while updating password.", e);
        } catch (Exception e) {
            // Handle general exceptions
            throw new Exception("Failed to reset password due to unexpected error.", e);
        }
    }


}
