package com.sms.dao.impl;

import com.sms.dao.ClassSubjectTeacherDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.model.ClassSubjectTeacherDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
public class ClassSubjectTeacherDaoImpl implements ClassSubjectTeacherDao {
    private final JdbcTemplate jdbcTemplate;

    public ClassSubjectTeacherDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public ClassSubjectTeacherDetails assignSubjectClassTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails, String schoolCode) throws Exception {
        String sql = "insert into class_subject_teacher_allocation (school_id, session_id, class_id, section_id, subject_id, teacher_id, allocation_role, allocation_date, status) values(?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, classSubjectTeacherDetails.getSchoolId());
                ps.setInt(2, classSubjectTeacherDetails.getSessionId());
                ps.setInt(3, classSubjectTeacherDetails.getClassId());
                ps.setInt(4, classSubjectTeacherDetails.getSectionId());
                ps.setInt(5, classSubjectTeacherDetails.getSubjectId());
                ps.setInt(6, classSubjectTeacherDetails.getTeacherId());
                ps.setString(7, classSubjectTeacherDetails.getAllocationRole());
                if (classSubjectTeacherDetails.getAllocationDate() != null) {
                    ps.setDate(8, new java.sql.Date(classSubjectTeacherDetails.getAllocationDate().getTime()));
                } else {
                    ps.setNull(8, java.sql.Types.DATE);
                }
                ps.setString(9, classSubjectTeacherDetails.getStatus());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("csta_id")) {
                int generatedKey = ((Number) keys.get("csta_id")).intValue();
                classSubjectTeacherDetails.setCstaId(generatedKey);
            }
        } catch (DuplicateKeyException e){
            throw e;
        }catch (Exception e) {
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classSubjectTeacherDetails;
    }

    @Override
    public List<ClassSubjectTeacherDetails> ClassSubjectTeacher(String schoolCode) throws Exception {
        String sql = "SELECT " +
                "csta_id, " +
                "csta.session_id, " +
                "csta.school_id, " +
                "csta.class_id, " +
                "csta.section_id, " +
                "csta.subject_id, " +
                "csta.teacher_id, " +
                "csta.allocation_role, " +
                "csta.allocation_date, " +
                "csta.status, " +
                "s.academic_session, " +
                "cls.class_name, " +
                "sec.section_name, " +
                "sub.subject_name, " +
                "CONCAT(staff.first_name, ' ', staff.last_name) AS teacher_name " +
                "FROM class_subject_teacher_allocation csta " +
                "INNER JOIN session s ON csta.session_id = s.session_id " +
                "INNER JOIN mst_class cls ON csta.class_id = cls.class_id " +
                "INNER JOIN mst_section sec ON csta.section_id = sec.section_id " +
                "INNER JOIN mst_subject sub ON csta.subject_id = sub.subject_id " +
                "INNER JOIN staff ON csta.teacher_id = staff.staff_id";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassSubjectTeacherDetails> classSubjectTeacherDetails = null;
        try{
            classSubjectTeacherDetails = jdbcTemplate.query(sql, new RowMapper<ClassSubjectTeacherDetails>() {

                @Override
                public ClassSubjectTeacherDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassSubjectTeacherDetails csta = new ClassSubjectTeacherDetails();
                    csta.setCstaId(rs.getInt("csta_id"));
                    csta.setSessionId(rs.getInt("session_id"));
                    csta.setSchoolId(rs.getInt("school_id"));
                    csta.setClassId(rs.getInt("class_id"));
                    csta.setSectionId(rs.getInt("section_id"));
                    csta.setSubjectId(rs.getInt("subject_id"));
                    csta.setTeacherId(rs.getInt("teacher_id"));
                    csta.setAllocationRole(rs.getString("allocation_role"));
                    csta.setAllocationDate(rs.getDate("allocation_date"));
                    csta.setStatus(rs.getString("status"));
                    csta.setAcademicSession(rs.getString("academic_session"));
                    csta.setClassName(rs.getString("class_name"));
                    csta.setSectionName(rs.getString("section_name"));
                    csta.setSubjectName(rs.getString("subject_name"));
                    csta.setTeacherName(rs.getString("teacher_name"));
                    return csta;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return classSubjectTeacherDetails;
    }
    @Override
    public ClassSubjectTeacherDetails getClassSubjectTeacherById(int cstaId,String schoolCode) throws Exception {
        String sql = "SELECT " +
                "csta_id, " +
                "csta.session_id, " +
                "csta.school_id, " +
                "csta.class_id, " +
                "csta.section_id, " +
                "csta.subject_id, " +
                "csta.teacher_id, " +
                "csta.allocation_role, " +
                "csta.allocation_date, " +
                "csta.status, " +
                "s.academic_session, " +
                "cls.class_name, " +
                "sec.section_name, " +
                "sub.subject_name, " +
                "CONCAT(staff.first_name, ' ', staff.last_name) AS teacher_name " +
                "FROM class_subject_teacher_allocation csta " +
                "INNER JOIN session s ON csta.session_id = s.session_id " +
                "INNER JOIN mst_class cls ON csta.class_id = cls.class_id " +
                "INNER JOIN mst_section sec ON csta.section_id = sec.section_id " +
                "INNER JOIN mst_subject sub ON csta.subject_id = sub.subject_id " +
                "INNER JOIN staff ON csta.teacher_id = staff.staff_id " +
                "WHERE csta_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        ClassSubjectTeacherDetails classSubjectTeacherDetails = null;
        try {
            classSubjectTeacherDetails = jdbcTemplate.queryForObject(sql, new Object[]{cstaId}, new RowMapper<ClassSubjectTeacherDetails>() {
                @Override
                public ClassSubjectTeacherDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassSubjectTeacherDetails csta = new ClassSubjectTeacherDetails();
                    csta.setCstaId(rs.getInt("csta_id"));
                    csta.setSessionId(rs.getInt("session_id"));
                    csta.setSchoolId(rs.getInt("school_id"));
                    csta.setClassId(rs.getInt("class_id"));
                    csta.setSectionId(rs.getInt("section_id"));
                    csta.setSubjectId(rs.getInt("subject_id"));
                    csta.setTeacherId(rs.getInt("teacher_id"));
                    csta.setAllocationRole(rs.getString("allocation_role"));
                    csta.setAllocationDate(rs.getDate("allocation_date"));
                    csta.setStatus(rs.getString("status"));
                    csta.setAcademicSession(rs.getString("academic_session"));
                    csta.setClassName(rs.getString("class_name"));
                    csta.setSectionName(rs.getString("section_name"));
                    csta.setSubjectName(rs.getString("subject_name"));
                    csta.setTeacherName(rs.getString("teacher_name"));
                    return csta;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            throw new ChangeSetPersister.NotFoundException();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classSubjectTeacherDetails;
    }

   @Override
   public ClassSubjectTeacherDetails updateClassSubjectTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails, int cstaId, String schoolCode) throws Exception {
       String sql = "UPDATE class_subject_teacher_allocation " +
               "SET school_id = ?, session_id = ?, class_id = ?, section_id = ?, subject_id = ?, teacher_id = ?, allocation_role = ?, allocation_date = ?, status = ? " +
               "WHERE csta_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowEffected = jdbcTemplate.update(sql,
                   classSubjectTeacherDetails.getSchoolId(),
                   classSubjectTeacherDetails.getSessionId(),
                   classSubjectTeacherDetails.getClassId(),
                   classSubjectTeacherDetails.getSectionId(),
                   classSubjectTeacherDetails.getSubjectId(),
                   classSubjectTeacherDetails.getTeacherId(),
                   classSubjectTeacherDetails.getAllocationRole(),
                   classSubjectTeacherDetails.getAllocationDate(),
                   classSubjectTeacherDetails.getStatus(),
                   cstaId);

           if (rowEffected > 0) {
               return classSubjectTeacherDetails;
           } else {
               return null;
           }
       } catch (DuplicateKeyException e){
           throw e;
       }catch (Exception e){
           throw e;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

   }

   @Override
   public boolean deleteClassSubjectTeacher(int cstaId, String schoolCode) throws Exception {
       String sql = "delete from class_subject_teacher_allocation where csta_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql, new Object[]{cstaId});
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
    public void bulkInsert(List<ClassSubjectTeacherDetails> allocations,String schoolCode) {
        String sql = "INSERT INTO class_subject_teacher_allocation " +
                "(school_id, session_id, class_id, section_id, subject_id, teacher_id) " +
                "SELECT ?, ?, ?, ?, ?, ? " +
                "WHERE EXISTS (" +
                "  SELECT 1 FROM class_subject_allocation " +
                "  WHERE school_id = ? AND session_id = ? AND class_id = ? AND section_id = ? AND subject_id = ?" +
                ") AND EXISTS (" +
                "  SELECT 1 FROM staff WHERE staff_id = ? AND school_id = ?" +
                ")";
        JdbcTemplate jdbcTemplate = null;
 try {
     jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
     jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
         @Override
         public void setValues(PreparedStatement ps, int i) throws SQLException {
             ClassSubjectTeacherDetails allocation = allocations.get(i);
             ps.setInt(1, allocation.getSchoolId());
             ps.setInt(2, allocation.getSessionId());
             ps.setInt(3, allocation.getClassId());
             ps.setInt(4, allocation.getSectionId());
             ps.setInt(5, allocation.getSubjectId());
             ps.setInt(6, allocation.getTeacherId());
             // Check for valid subject allocation
             ps.setInt(7, allocation.getSchoolId());
             ps.setInt(8, allocation.getSessionId());
             ps.setInt(9, allocation.getClassId());
             ps.setInt(10, allocation.getSectionId());
             ps.setInt(11, allocation.getSubjectId());
             // Check for valid teacher
             ps.setInt(12, allocation.getTeacherId());
             ps.setInt(13, allocation.getSchoolId());
         }

         @Override
         public int getBatchSize() {
             return allocations.size();
         }
     });
 }catch (Exception e){
     e.printStackTrace();
 }finally {
     DatabaseUtil.closeDataSource(jdbcTemplate);
 }
    }

    @Override
    public List<ClassSubjectTeacherDetails> findAllocatedTeacher(int sessionId,int classId,int sectionId,int subjectId,String schoolCode) throws Exception {
        String sql = """
                   SELECT\s
                     csta.teacher_id,\s
                     s.first_name || ' ' || s.last_name AS teacher_name
                   FROM class_subject_teacher_allocation csta
                   INNER JOIN staff s ON csta.teacher_id = s.staff_id
                   WHERE\s
                     csta.session_id = ?
                     AND csta.class_id = ?
                     AND csta.section_id = ?
                     AND csta.subject_id = ?
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassSubjectTeacherDetails> classSubjectTeacherDetails = null;
        try{
            classSubjectTeacherDetails = jdbcTemplate.query(sql, new Object[]{sessionId,classId,sectionId,subjectId}, new RowMapper<ClassSubjectTeacherDetails>() {

                @Override
                public ClassSubjectTeacherDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassSubjectTeacherDetails csta = new ClassSubjectTeacherDetails();
                   // csta.setCstaId(rs.getInt("csta_id"));

                  //  csta.setSubjectId(rs.getInt("subject_id"));
                    csta.setTeacherId(rs.getInt("teacher_id"));
                    csta.setTeacherName(rs.getString("teacher_name"));
                    return csta;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return classSubjectTeacherDetails;
    }
}
