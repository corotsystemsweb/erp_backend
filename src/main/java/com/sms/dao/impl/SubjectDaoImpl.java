package com.sms.dao.impl;

import com.sms.dao.SubjectDao;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SubjectDaoImpl implements SubjectDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SubjectDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
   private int findNextAvailableId(String schoolCode) {
       String dbsql = "SELECT COALESCE(MAX(subject_id), 0) + 1 AS subject_id FROM mst_subject";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       // Initialize id to 0 in case there are no records
       int nextAvailableId = 0;

       try {
           // Execute the SQL query using jdbcTemplate
           nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
       } catch (EmptyResultDataAccessException e) {
           e.printStackTrace();
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return nextAvailableId;
   }
    /*
    @method addSubject
    @param subjectDetails
    @throws Exception
    @description adding subject details in subject table
    @developer Sukhendu Bhowmik
    */

   @Override
   public SubjectDetails addSubject(SubjectDetails subjectDetails, String schoolCode) throws Exception {
       int nextAvailableId = findNextAvailableId(schoolCode);
       subjectDetails.setSubjectId(nextAvailableId);
       String sql = "insert into mst_subject (subject_id,school_id,subject_name) values(?,?,?)";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           jdbcTemplate.update(sql,
                   subjectDetails.getSubjectId(),
                   subjectDetails.getSchoolId(),
                   subjectDetails.getSubjectName());
       }catch(Exception e){
           //e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return subjectDetails;
   }
    /*
    @method getSubjectDetailsById
    @param subjectId
    @throws Exception
    @description getting subject details by id from subject table
    @developer Sukhendu Bhowmik
    */
    @Override
    public SubjectDetails getSubjectDetailsById(int subjectId,String schoolCode) throws Exception {
        String sql = "select distinct subject_id,school_id, subject_name from mst_subject where subject_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SubjectDetails subjectDetails = null;
        try{
            subjectDetails = jdbcTemplate.queryForObject(sql, new Object[]{subjectId}, new RowMapper<SubjectDetails>() {
                @Override
                public SubjectDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SubjectDetails sd = new SubjectDetails();
                    sd.setSubjectId(rs.getInt("subject_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSubjectName(rs.getString("subject_name"));
                    return sd;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return subjectDetails;
    }
    /*
    @method getAllSubjectDetails
    @throws Exception
    @description getting all subject details from subject table
    @developer Sukhendu Bhowmik
    */

    @Override
    public List<SubjectDetails> getAllSubjectDetails(String schoolCode) throws Exception {
        String sql = "select distinct subject_id,school_id, subject_name from mst_subject order by subject_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SubjectDetails> subjectDetails = null;
        try{
            subjectDetails = jdbcTemplate.query(sql, new RowMapper<SubjectDetails>() {
                @Override
                public SubjectDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SubjectDetails sd = new SubjectDetails();
                    sd.setSubjectId(rs.getInt("subject_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSubjectName(rs.getString("subject_name"));
                    return sd;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return subjectDetails;
    }
    /*
    @method updateSubjectDetailsById
    @param subjectDetails, subjectId
    @throws Exception
    @description update subject details by id
    @developer Sukhendu Bhowmik
    */

    //check  the subject name which has already exists or not. If I update English to Hindi but Hindi is already there then it will give false
    public boolean isOtherSubjectHasSameName(SubjectDetails subjectDetails, int subjectId, String schoolCode){
        String checkSql = "SELECT COUNT(*) FROM mst_subject WHERE subject_id != ? AND subject_name = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, subjectId, subjectDetails.getSubjectName());
            return count != null && count > 0;
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public SubjectDetails updateSubjectDetailsById(SubjectDetails subjectDetails, int subjectId, String schoolCode) throws Exception {
        if(isOtherSubjectHasSameName(subjectDetails, subjectId, schoolCode)){
            return null;
        }
        // If no conflict, proceed with the update
        String sql = "UPDATE mst_subject SET school_id = ?, subject_name = ? WHERE subject_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    subjectDetails.getSchoolId(),
                    subjectDetails.getSubjectName(),
                    subjectId);

            if (rowEffected > 0) {
                return subjectDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Error updating subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }


    /*
    @method deleteSubject
    @param subjectId
    @throws Exception
    @description delete subject details by id
    @developer Sukhendu Bhowmik
    */

   @Override
   public boolean deleteSubject(int subjectId, String schoolCode) throws Exception {
       String sql = "delete from mst_subject where subject_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql, new Object[]{subjectId});
           if(rowAffected > 0){
               return true;
           }else{
               return false;
           }
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

}
