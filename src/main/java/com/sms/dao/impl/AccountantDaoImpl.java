package com.sms.dao.impl;

import com.sms.dao.AccountantDao;
import com.sms.model.AccountantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AccountantDaoImpl implements AccountantDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AccountantDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*

    */

    @Override
    public boolean addAccount(AccountantDetails accountantDetails) throws Exception {
        String sql = " insert into accountant (firstName,lastName,email,phoneNumber) values(?,?,?,?)";
        try{
            jdbcTemplate.update(sql , new Object[]{accountantDetails.getFirstName(),accountantDetails.getLastName(),
                    accountantDetails.getEmail(),accountantDetails.getPhoneNumber()});
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public AccountantDetails getAccountantDetailsById(int id) throws Exception {
        String sql = "select distinct id, firstName, lastName, email, phoneNumber from accountant where id = ?";
        AccountantDetails accountantDetails = null;
        accountantDetails = jdbcTemplate.queryForObject(sql, new Object[]{id},new RowMapper<AccountantDetails>(){

            @Override
            public AccountantDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                AccountantDetails accd = new AccountantDetails();
                accd.setId(rs.getInt("id"));
                accd.setFirstName(rs.getString("firstName"));
                accd.setLastName(rs.getString("lastName"));
                accd.setEmail(rs.getString("email"));
                accd.setPhoneNumber(rs.getString("phoneNumber"));
                return accd;
            }
        });
        return accountantDetails;
    }

    @Override
    public List<AccountantDetails> getAllAccountantDetails() throws Exception {
        String sql = "select distinct id, firstName, lastName, email, phoneNumber from accountant";
        List<AccountantDetails> accountantDetails = null;
        accountantDetails = jdbcTemplate.query(sql, new RowMapper<AccountantDetails>() {
            @Override
            public AccountantDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                AccountantDetails accd = new AccountantDetails();
                accd.setId(rs.getInt("id"));
                accd.setFirstName(rs.getString("firstName"));
                accd.setLastName(rs.getString("lastName"));
                accd.setEmail(rs.getString("email"));
                accd.setPhoneNumber(rs.getString("phoneNumber"));
                return accd;
            }
        });
        return accountantDetails;
    }
}
