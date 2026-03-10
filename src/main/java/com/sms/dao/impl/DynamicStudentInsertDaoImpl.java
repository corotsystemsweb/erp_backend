package com.sms.dao.impl;

import com.sms.dao.DynamicStudentInsertDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;



@Repository
public class DynamicStudentInsertDaoImpl implements DynamicStudentInsertDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public static final Logger logger = LoggerFactory.getLogger(DynamicStudentInsertDaoImpl.class);

    @Override
    public String executeDdlForStudentInsert(String schoolCode) throws Exception {
        schoolCode = schoolCode.toLowerCase();
        String sql = "SELECT medhapro.insert_student_details(?)";
        try{
            jdbcTemplate.queryForObject(sql, String.class, schoolCode);
            return String.format("Student details are inserted! %s", schoolCode);
        }catch (Exception e){
            e.printStackTrace();
            return "Oops! Student details are not created: " +e.getMessage();
        }
    }
}
