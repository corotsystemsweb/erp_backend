package com.sms.dao.impl;

import com.sms.dao.StaffAndStudentDao;
import com.sms.model.StaffAndStudentDetails;
import com.sms.util.CipherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StaffAndStudentDaoImpl implements StaffAndStudentDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StaffAndStudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StaffAndStudentDetails> getUsersByPhoneNumberAndStatus(String phoneNumber) throws Exception {
        // Encrypting phone number
        String encryptedPhoneNumber = CipherUtils.encrypt(phoneNumber);
        String sql = "SELECT student_id AS user_id, first_name, last_name, dob, gender, father_name, phone_number, email_address, student_photo as user_photo, 'Student' AS designation " +
                "FROM student_personal_details " +
                "WHERE phone_number = ? AND current_status = 'active' AND deleted IS NOT TRUE " +
                "UNION " +
                "SELECT staff_id AS user_id, first_name, last_name, dob, gender, father_name, phone_number, email_address, staff_photo as user_photo, designation " +
                "FROM staff " +
                "WHERE phone_number = ? AND current_status = 'active' AND deleted IS NOT TRUE";
        List<StaffAndStudentDetails> staffAndStudentDetails = null;
        staffAndStudentDetails = jdbcTemplate.query(sql, new Object[]{encryptedPhoneNumber, encryptedPhoneNumber}, new RowMapper<StaffAndStudentDetails>() {
            @Override
            public StaffAndStudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                StaffAndStudentDetails sasd = new StaffAndStudentDetails();
                sasd.setUserId(rs.getInt("user_id"));
                sasd.setFirstName(rs.getString("first_name"));
                sasd.setLastName(rs.getString("last_name"));
                sasd.setDob(rs.getDate("dob"));
                sasd.setGender(rs.getString("gender"));
                sasd.setFatherName(rs.getString("father_name"));
                //sasd.setPhoneNumber(rs.getString("phone_number"));
                sasd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                sasd.setEmailAddress(rs.getString("email_address"));
                sasd.setUserPhoto(rs.getString("user_photo"));
                sasd.setDesignation(rs.getString("designation"));
                return sasd;
            }
        });
        return staffAndStudentDetails;
    }
}
