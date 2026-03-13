package com.sms.dao.impl;

import com.sms.dao.ClassAndSectionDao;
import com.sms.model.ClassAndSectionDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ClassAndSectionDaoImpl implements ClassAndSectionDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClassAndSectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(class_section_id), 0) + 1 AS class_section_id FROM class_and_section";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int nextAvailableId = 0;
        try {
            nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableId;
    }
    /*
    @method addClassAndSection
    @param classAndSectionDetails
    @throws Exception
    @description add the ClassAndSectionDetails details in class_and_section table
    @developer Sukhendu Bhowmik
    */
    @Override
    public ClassAndSectionDetails addClassAndSection(ClassAndSectionDetails classAndSectionDetails, String schoolCode) throws Exception {
        int nextAvailableId = findNextAvailableId(schoolCode);
        classAndSectionDetails.setClassSectionId(nextAvailableId);
        String sql = "insert into class_and_section (class_section_id, school_id, class_id, section_id) values(?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
           jdbcTemplate.update(sql,
                   classAndSectionDetails.getClassSectionId(),
                   classAndSectionDetails.getSchoolId(),
                   classAndSectionDetails.getClassId(),
                   classAndSectionDetails.getSectionId()
           );
        }catch(Exception e){
           // e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classAndSectionDetails;
    }
    /*
    @method getClassAndSectionById
    @param classSectionId
    @throws Exception
    @description get the ClassAndSectionDetails details by id from class_and_section table
    @developer Sukhendu Bhowmik
    */

    @Override
    public ClassAndSectionDetails getClassAndSectionById(int classSectionId, String schoolCode) throws Exception {
        String sql = "select distinct class_section_id, school_id, class_id, section_id from class_and_section where class_section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ClassAndSectionDetails classAndSectionDetails = null;
       try{
           classAndSectionDetails = jdbcTemplate.queryForObject(sql, new Object[]{classSectionId}, new RowMapper<ClassAndSectionDetails>() {
               @Override
               public ClassAndSectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ClassAndSectionDetails csd = new ClassAndSectionDetails();
                   csd.setClassSectionId(rs.getInt("class_section_id"));
                   csd.setSchoolId(rs.getInt("school_id"));
                   csd.setClassId(rs.getInt("class_id"));
                   csd.setSectionId(rs.getInt("section_id"));
                   return csd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
        return classAndSectionDetails;
    }
    /*
    @method getAllClassAndSection
    @throws Exception
    @description get all ClassAndSectionDetails details  from class_and_section table
    @developer Sukhendu Bhowmik
    */

    @Override
    public List<ClassAndSectionDetails> getAllClassAndSection(String schoolCode) throws Exception {
        String sql = "select distinct class_section_id, school_id, class_id, section_id from class_and_section order by class_section_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<ClassAndSectionDetails> classAndSectionDetails = null;
       try {
           classAndSectionDetails = jdbcTemplate.query(sql, new RowMapper<ClassAndSectionDetails>() {
               @Override
               public ClassAndSectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ClassAndSectionDetails csd = new ClassAndSectionDetails();
                   csd.setClassSectionId(rs.getInt("class_section_id"));
                   csd.setSchoolId(rs.getInt("school_id"));
                   csd.setClassId(rs.getInt("class_id"));
                   csd.setSectionId(rs.getInt("section_id"));
                   return csd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
        return classAndSectionDetails;
    }
     /*
    @method updateClassAndSection
    @param classAndSectionDetails
    @throws Exception
    @description update ClassAndSectionDetails in class_and_section table by id
    @developer Sukhendu Bhowmik
    */

    @Override
    public ClassAndSectionDetails updateClassAndSection(ClassAndSectionDetails classAndSectionDetails, int classSectionId, String schoolCode) throws Exception {
        String sql = "update class_and_section SET school_id = ?, class_id = ?, section_id = ? where class_section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try{
            int isRowEffected = jdbcTemplate.update(sql ,
                    classAndSectionDetails.getSchoolId(),
                    classAndSectionDetails.getClassId(),
                    classAndSectionDetails.getSectionId(),
                    classAndSectionDetails.getClassSectionId());
            if(isRowEffected > 0){
                return classAndSectionDetails;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    /*
    @method deleteClassAndSection
    @param id
    @throws Exception
    @description delete ClassAndSectionDetails in class_and_section table by id
    @developer Sukhendu Bhowmik
    */

    @Override
    public boolean deleteClassAndSection(int classSectionId, String schoolCode) throws Exception {
        String sql = "delete from class_and_section where class_section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int isRowEffected = jdbcTemplate.update(sql, new Object[]{classSectionId});
            if(isRowEffected > 0){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

   /* @Override
    public List<ClassAndSectionDetails> getClassRelatedSection() throws Exception {
        //String sql = "SELECT c.id, mc.class_name,mc.id as class_id,ms.section_name,ms.id as section_id FROM class_and_section c JOIN mst_class mc ON c.class_id = mc.id JOIN mst_section ms ON c.section_id = ms.id";
        String sql = "SELECT cas.id, sd.id as school_id,sd.school_name, mc.id as class_id,mc.class_name, ms.id as section_id,ms.section_name FROM class_and_section cas JOIN school_details sd ON cas.school_id = sd.id JOIN mst_class mc ON cas.class_id = mc.id JOIN mst_section ms ON cas.section_id = ms.id";
        List<ClassAndSectionDetails> classAndSectionDetails = null;
        classAndSectionDetails = jdbcTemplate.query(sql, new RowMapper<ClassAndSectionDetails>() {
            @Override
            public ClassAndSectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassAndSectionDetails csd = new ClassAndSectionDetails();
                csd.setId(rs.getInt("id"));
                csd.setSchoolId(rs.getInt("school_id"));
                csd.setSchoolName(rs.getString("school_name"));
                csd.setClassId(rs.getInt("class_id"));
                csd.setSectionId(rs.getInt("section_id"));
                csd.setStudentClass(rs.getString("class_name"));
                csd.setStudentSection(rs.getString("section_name"));
                return csd;
            }
        });
        return classAndSectionDetails;
    }*/
   @Override
   public List<ClassAndSectionDetails> getClassRelatedSection(String schoolCode) throws Exception {
       String sql = "SELECT cas.class_section_id, cas.school_id, sd.school_name, mc.class_id, mc.class_name, ms.section_id, ms.section_name FROM class_and_section cas JOIN mst_class mc ON cas.class_id = mc.class_id JOIN mst_section ms ON cas.section_id = ms.section_id JOIN school_details sd ON cas.school_id = sd.school_id WHERE cas.school_id = 1 ORDER BY cas.class_section_id ASC";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<ClassAndSectionDetails> classAndSectionDetails=null;
       try{
       classAndSectionDetails = jdbcTemplate.query(sql, new RowMapper<ClassAndSectionDetails>() {
           @Override
           public ClassAndSectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
               ClassAndSectionDetails csd = new ClassAndSectionDetails();
               csd.setClassSectionId(rs.getInt("class_section_id"));
               csd.setSchoolId(rs.getInt("school_id"));
               csd.setSchoolName(rs.getString("school_name"));
               csd.setClassId(rs.getInt("class_id"));
               csd.setStudentClass(rs.getString("class_name"));
               csd.setSectionId(rs.getInt("section_id"));
               csd.setStudentSection(rs.getString("section_name"));
               return csd;
           }
       });
   } catch (Exception e){
           e.printStackTrace();
           return null;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return classAndSectionDetails;
       }
}
