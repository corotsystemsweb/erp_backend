package com.sms.dao.impl;

import com.sms.dao.DdlForAddStudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DdlForAddStudentDaoImpl implements DdlForAddStudentDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public DdlForAddStudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String addDdlForStudentDetails(String schoolCode) throws Exception {
        String sql = "SELECT medhapro.insert_student_personal_details(?)";
        try {
            jdbcTemplate.queryForObject(sql, new Object[]{schoolCode}, String.class);
            return "DDL executed successfully for schema: " + schoolCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error executing DDL: " + e.getMessage();
        }
    }
}
