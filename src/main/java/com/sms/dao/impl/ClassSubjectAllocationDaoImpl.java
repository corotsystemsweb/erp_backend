package com.sms.dao.impl;

import com.sms.dao.ClassSubjectAllocationDao;
import com.sms.model.ClassDetails;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.model.StudentAttendanceDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.sms.dao.impl.DynamicStudentInsertDaoImpl.logger;
import static org.springframework.boot.autoconfigure.quartz.JobStoreType.JDBC;

@Repository
public class ClassSubjectAllocationDaoImpl implements ClassSubjectAllocationDao {
    private final JdbcTemplate jdbcTemplate;
@Autowired
    public ClassSubjectAllocationDaoImpl(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
    }
   @Override
   public List<ClassSubjectAllocationDetails> addAllocateSubject(List<ClassSubjectAllocationDetails> classSubjectAllocationDetailsList, String schoolCode) throws Exception {
       String sql = "INSERT INTO class_subject_allocation (school_id,session_id, class_id, section_id, subject_id) VALUES (?, ?, ?, ?, ?)";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<ClassSubjectAllocationDetails> insertedSubjectList = new ArrayList<>();
       try {
           for (ClassSubjectAllocationDetails classSubjectAllocationDetails : classSubjectAllocationDetailsList) {
               KeyHolder keyHolder = new GeneratedKeyHolder();
               jdbcTemplate.update(connection -> {
                   PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                   ps.setInt(1, classSubjectAllocationDetails.getSchoolId());
                   ps.setInt(2,classSubjectAllocationDetails.getSessionId());
                   ps.setInt(3, classSubjectAllocationDetails.getClassId());
                   ps.setInt(4, classSubjectAllocationDetails.getSectionId());
                   ps.setInt(5, classSubjectAllocationDetails.getSubjectId());
                   return ps;
               }, keyHolder);
               Map<String, Object> keys = keyHolder.getKeys();
               if (keys != null && keys.containsKey("csa_id")) {
                   int generatedId = ((Number) keys.get("csa_id")).intValue();
                   classSubjectAllocationDetails.setCsaId(generatedId);
                   insertedSubjectList.add(classSubjectAllocationDetails);

               } else {
                   throw new SQLException("Generated key 'csa_id' not found.");
               }
           }
       } catch (Exception e) {
           // Handle exception appropriately, e.g., logging
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return insertedSubjectList;
   }
    @Override
    public List<ClassSubjectAllocationDetails> getAllAlocatedSubject(String schoolCode) {
        // String sql = "SELECT csa.csa_id,csa.class_id,csa.section_id,csa.subject_id, c.class_name, s.section_name, sub.subject_name FROM class_subject_allocation csa INNER JOIN mst_class c ON csa.class_id = c.class_id INNER JOIN mst_section s ON csa.section_id = s.section_id INNER JOIN mst_subject sub ON csa.subject_id = sub.subject_id";
        String sql="SELECT csa.csa_id,csa.school_id ,se.session_id,  csa.class_id, csa.section_id, csa.subject_id,se.academic_session, c.class_name, s.section_name, sub.subject_name FROM class_subject_allocation csa INNER JOIN mst_class c ON csa.class_id = c.class_id INNER JOIN mst_section s ON csa.section_id = s.section_id INNER JOIN mst_subject sub ON csa.subject_id = sub.subject_id INNER JOIN session se ON csa.session_id = se.session_id ORDER BY csa.csa_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassSubjectAllocationDetails> classSubjectAllocationDetails = null;
        try{
            classSubjectAllocationDetails = jdbcTemplate.query(sql, new RowMapper<ClassSubjectAllocationDetails>() {
                @Override
                public ClassSubjectAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassSubjectAllocationDetails csa = new ClassSubjectAllocationDetails();
                    csa.setCsaId(rs.getInt("csa_id"));
                    csa.setSchoolId(rs.getInt("school_id"));
                    csa.setSessionId(rs.getInt("session_id"));
                    csa.setClassId(rs.getInt("class_id"));
                    csa.setSectionId(rs.getInt("section_id"));
                    csa.setSubjectId(rs.getInt("subject_id"));
                    csa.setAcademicSession(rs.getString("academic_session"));
                    csa.setClassName(rs.getString("class_name"));
                    csa.setSectionName(rs.getString("section_name"));
                    csa.setSubjectName(rs.getString("subject_name"));
                    return csa;
                }
            });
        }catch (Exception e){
            throw new RuntimeException("Error fetching class subject allocation details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classSubjectAllocationDetails;
    }
   @Override
   public ClassSubjectAllocationDetails getAllocatedClassSubjectById(int csaId, String schoolCode) throws Exception {
       String sql="SELECT csa.csa_id,csa.school_id ,se.session_id,  csa.class_id, csa.section_id, csa.subject_id,se.academic_session, c.class_name, s.section_name, sub.subject_name FROM class_subject_allocation csa INNER JOIN mst_class c ON csa.class_id = c.class_id INNER JOIN mst_section s ON csa.section_id = s.section_id INNER JOIN mst_subject sub ON csa.subject_id = sub.subject_id INNER JOIN session se ON csa.session_id = se.session_id where csa_id=?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       ClassSubjectAllocationDetails classSubjectAllocationDetails = null;
       try {
           classSubjectAllocationDetails = jdbcTemplate.queryForObject(sql, new Object[]{csaId}, new RowMapper<ClassSubjectAllocationDetails>() {
               @Override
               public ClassSubjectAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ClassSubjectAllocationDetails csa = new ClassSubjectAllocationDetails();
                   csa.setCsaId(rs.getInt("csa_id"));
                   csa.setSchoolId(rs.getInt("school_id"));
                   csa.setSessionId(rs.getInt("session_id"));
                   csa.setClassId(rs.getInt("class_id"));
                   csa.setSectionId(rs.getInt("section_id"));
                   csa.setSubjectId(rs.getInt("subject_id"));
                   csa.setAcademicSession(rs.getString("academic_session"));
                   csa.setClassName(rs.getString("class_name"));
                   csa.setSectionName(rs.getString("section_name"));
                   csa.setSubjectName(rs.getString("subject_name"));
                   return csa;
               }
           });
       } catch (EmptyResultDataAccessException e) {
           throw new ChangeSetPersister.NotFoundException();
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return classSubjectAllocationDetails;
   }

    @Override
    public ClassSubjectAllocationDetails updateAllocatedSubject(ClassSubjectAllocationDetails classSubjectAllocationDetails, int csaId, String schoolCode) throws Exception {
    String sql="UPDATE class_subject_allocation SET school_id=?, session_id=?,class_id=?, section_id=?, subject_id=? where csa_id=?";
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            int rowsAffected = jdbcTemplate.update(sql,
                    classSubjectAllocationDetails.getSchoolId(),
                    classSubjectAllocationDetails.getSessionId(),
                    classSubjectAllocationDetails.getClassId(),
                    classSubjectAllocationDetails.getSectionId(),
                    classSubjectAllocationDetails.getSubjectId(),
                    csaId);
            if (rowsAffected > 0) {
                classSubjectAllocationDetails.setCsaId(csaId);
                return classSubjectAllocationDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean deleteAllocatedSubject(int csaId, String schoolCode) throws Exception {

        String sql = "delete from class_subject_allocation where csa_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{csaId});
            if(rowAffected > 0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
   @Override
   public List<ClassSubjectAllocationDetails> findSubject(int classId, int sectionId ,int sessionId ,String schoolCode) throws Exception {
      // String sql = "SELECT csa.subject_id,ms.subject_name FROM class_subject_allocation csa INNER JOIN mst_subject ms ON csa.subject_id = ms.subject_id WHERE csa.session_id=? AND csa.class_id = ? AND csa.section_id = ?";
       String sql="SELECT csa.subject_id,ms.subject_name FROM class_subject_allocation csa INNER JOIN mst_subject ms ON csa.subject_id = ms.subject_id WHERE  csa.class_id = ? AND csa.section_id = ? and csa.session_id=?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<ClassSubjectAllocationDetails> classSubjectAllocationDetails = null;
       try{
           classSubjectAllocationDetails = jdbcTemplate.query(sql,new Object[]{classId, sectionId,sessionId}, new RowMapper<ClassSubjectAllocationDetails>() {
               @Override
               public ClassSubjectAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ClassSubjectAllocationDetails csa = new ClassSubjectAllocationDetails();
                   csa.setSubjectId(rs.getInt("subject_id"));
                   csa.setSubjectName(rs.getString("subject_name"));
                   return csa;
               }
           });
       }catch (Exception e){
           throw new Exception("Error retrieving class subject allocations", e);
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return classSubjectAllocationDetails;
   }


    @Override
    public void deleteByClassSection(int schoolId, int sessionId, int classId, int sectionId, String schoolCode) {
        String sql = "DELETE FROM class_subject_allocation " +
                "WHERE school_id=? AND session_id=? AND class_id=? AND section_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        jdbcTemplate.update(sql, schoolId, sessionId, classId, sectionId);
    }

    @Override
    public void insertSubjects(int schoolId, int sessionId, int classId, int sectionId, List<Integer> subjectIds, String schoolCode) {
        String sql = "INSERT INTO class_subject_allocation " +
                "(school_id, session_id, class_id, section_id, subject_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        JdbcTemplate jdbcTemplate = null;
        try {
            // Get JDBC template
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            // Insert each subject
            for (Integer subjectId : subjectIds) {
                try {
                    jdbcTemplate.update(sql, schoolId, sessionId, classId, sectionId, subjectId);
                } catch (Exception e) {
                    // Log detailed error for failed insert
                    String errorMsg = String.format("Failed to insert subject allocation: " +
                                    "School ID: %d, Class ID: %d, Section ID: %d, Subject ID: %d",
                            schoolId, classId, sectionId, subjectId);
                    logger.error(errorMsg, e);
                    // Re-throw as custom exception or handle as needed
                  //  throw new e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any unexpected exceptions
            String errorMsg = "Error in subject allocation process: " + e.getMessage();
            logger.error(errorMsg, e);
          //  throw new DatabaseOperationException(errorMsg, e);
        } finally {
            // Add any necessary cleanup code here
           DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}



