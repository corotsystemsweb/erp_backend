package com.sms.dao.impl;

import com.sms.dao.AdminDao;
import com.sms.model.AdminDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDaoImpl implements AdminDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AdminDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addAdmin(AdminDetails adminDetails) throws Exception {
        String sql = " insert into admin (firstName,lastName,sex,email,dob,address,mobileNo) values(?,?,?,?,?,?,?)";
        try{
            jdbcTemplate.update(sql, new Object[]{adminDetails.getFirstName(),adminDetails.getLastName(),adminDetails.getSex(),
                                adminDetails.getEmail(),adminDetails.getDob(),adminDetails.getAddress(),adminDetails.getMobileNo()});
            return true;
        }catch(Exception e){
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    public AdminDetails getAdminDetailsById(int id) throws Exception {
        String sql = "select distinct id, firstName, lastName, sex,email, dob, address, mobileNo from admin where id = ?";
            AdminDetails adminDetails = jdbcTemplate.queryForObject(sql, new Object[]{id}, new RowMapper<AdminDetails>(){

                @Override
                public AdminDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AdminDetails ad = new AdminDetails();
                    ad.setId(rs.getInt("id"));
                    ad.setFirstName(rs.getString("firstName"));
                    ad.setLastName(rs.getString("lastName"));
                    ad.setSex(rs.getString("sex"));
                    ad.setEmail(rs.getString("email"));
                    ad.setDob(rs.getDate("dob"));
                    ad.setAddress(rs.getString("address"));
                    ad.setMobileNo(rs.getString("mobileNo"));
                    return ad;
                }
            });
            return adminDetails;
    }

    @Override
    public List<AdminDetails> getAllAdminDetails() throws Exception {
        String sql = "select distinct id, firstName, lastName, sex,email, dob, address, mobileNo from admin";
        List<AdminDetails> adminDetailsList = jdbcTemplate.query(sql, new RowMapper<AdminDetails>() {
            @Override
            public AdminDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                AdminDetails ad = new AdminDetails();
                ad.setId(rs.getInt("id"));
                ad.setFirstName(rs.getString("firstName"));
                ad.setLastName(rs.getString("lastName"));
                ad.setSex(rs.getString("sex"));
                ad.setEmail(rs.getString("email"));
                ad.setDob(rs.getDate("dob"));
                ad.setAddress(rs.getString("address"));
                ad.setMobileNo(rs.getString("mobileNo"));
                return ad;
            }
        });
        return adminDetailsList;
    }
}
