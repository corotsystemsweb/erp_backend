package com.sms.dao.impl;

import com.sms.dao.SectionDao;
import com.sms.model.SectionDetails;
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
import java.util.List;
import java.util.Map;

@Repository
public class SectionDaoImpl implements SectionDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(section_id), 0) + 1 AS section_id FROM mst_section";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int nextAvailableId = 0;
        try {
            nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableId;
    }
    /*
    @method addSection
    @param sectionDetails
    @throws Exception
    @description adding Section details in mst_section table
    @developer Sukhendu Bhowmik
    */
    @Override
    public SectionDetails addSection(SectionDetails sectionDetails, String schoolCode) throws Exception {
        int nextAvailableId = findNextAvailableId(schoolCode);
        sectionDetails.setSectionId(nextAvailableId);
        String sql = "insert into mst_section (section_id, school_id, section_name) values(?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
           jdbcTemplate.update(sql,
                   sectionDetails.getSectionId(),
                   sectionDetails.getSchoolId(),
                   sectionDetails.getStudentSection());
        }catch(DuplicateKeyException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return sectionDetails;
    }
    /*
  @method getSectionDetailsById
  @param sectionId
  @throws Exception
  @description getting section details by id from mst_class table
  @developer Sukhendu Bhowmik
  */
    @Override
    public SectionDetails getSectionDetailsById(int sectionId,String schoolCode) throws Exception {
        String sql = "select distinct section_id, school_id, section_name from mst_section where section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SectionDetails sectionDetails = null;
       try{
           sectionDetails = jdbcTemplate.queryForObject(sql, new Object[]{sectionId}, new RowMapper<SectionDetails>() {
               @Override
               public SectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   SectionDetails sd = new SectionDetails();
                   sd.setSectionId(rs.getInt("section_id"));
                   sd.setSchoolId(rs.getInt("school_id"));
                   sd.setStudentSection(rs.getString("section_name"));
                   return sd;
               }
           });
           return sectionDetails;
       } catch(Exception e){
           e.printStackTrace();
           throw e;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
    }
    /*
    @method getAllSectionDetails
    @throws Exception
    @description getting all section details from mst_section table
    @developer Sukhendu Bhowmik
    */
    @Override
    public List<SectionDetails> getAllSectionDetails(String schoolCode) throws Exception {
        String sql = "select distinct section_id, school_id, section_name from mst_section order by section_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SectionDetails> sectionDetails=null;
      try{
          sectionDetails = jdbcTemplate.query(sql, new RowMapper<SectionDetails>() {
              @Override
              public SectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                  SectionDetails sd = new SectionDetails();
                  sd.setSectionId(rs.getInt("section_id"));
                  sd.setSchoolId(rs.getInt("school_id"));
                  sd.setStudentSection(rs.getString("section_name"));
                  return sd;
              }
          });
      }catch (Exception e)
      {
          e.printStackTrace();
          throw e;
      } finally {
          DatabaseUtil.closeDataSource(jdbcTemplate);
      }
        return sectionDetails;
    }
    /*
   @method updateSectionDetailsById
   @param sectionDetails, sectionId
   @throws Exception
   @description update section details by sectionId
   @developer Sukhendu Bhowmik
   */
    @Override
    public SectionDetails updateSectionDetailsById(SectionDetails sectionDetails, int sectionId, String schoolCode) throws Exception {
        String sql = "UPDATE mst_section SET school_id = ?, section_name = ? WHERE section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowEffected = jdbcTemplate.update(sql,
                   sectionDetails.getSchoolId(),
                   sectionDetails.getStudentSection(),
                   sectionDetails.getSectionId());
           if(rowEffected > 0){
               return sectionDetails;
           }else{
               return null;
           }
       } catch(Exception e){
           e.printStackTrace();
           throw e;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
    }
    /*
   @method deleteSection
   @param sectionId
   @throws Exception
   @description delete section details by id
   @developer Sukhendu Bhowmik
   */
    @Override
    public boolean deleteSection(int sectionId,String schoolCode) throws Exception {
        String sql = "delete from mst_section where section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rowAffected = jdbcTemplate.update(sql, new Object[]{sectionId});
            if (rowAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
