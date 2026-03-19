package com.sms.dao.impl;

import com.sms.dao.DynamicTablesGeneratorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DynamicTablesGeneratorDaoImpl implements DynamicTablesGeneratorDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String executeDdl(String schoolCode) throws Exception {
        // Ensure schoolCode is in lowercase
        schoolCode = schoolCode.toLowerCase();
        String sql = "SELECT medhapro.exec_ddl(?)";
        try{
            jdbcTemplate.queryForObject(sql, new Object[]{schoolCode}, String.class);
            return "Tables and sequences are created: " + schoolCode;
        }catch (Exception e){
            e.printStackTrace();
            return "Tables are not created: " +e.getMessage();
        }
    }
}
