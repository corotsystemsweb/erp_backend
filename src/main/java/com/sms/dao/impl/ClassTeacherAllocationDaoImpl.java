package com.sms.dao.impl;

import com.sms.dao.ClassTeacherAllocationDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.model.ClassTeacherAllocationDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
public class ClassTeacherAllocationDaoImpl implements ClassTeacherAllocationDao {
    private final JdbcTemplate jdbcTemplate;

    public ClassTeacherAllocationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   /* @Override
    public ClassTeacherAllocationDetails addClassTeacherAllocation(ClassTeacherAllocationDetails classTeacherAllocationDetails, String schoolCode) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM class_teacher_allocation WHERE class_id = ? AND section_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, classTeacherAllocationDetails.getClassId(), classTeacherAllocationDetails.getSectionId());
            if (count != null && count > 0) {
                // ClassId and SectionId combination already exists, return null
                return null;
            }
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }

        // If no conflict, proceed with the insertion
        String sql = "INSERT INTO class_teacher_allocation (school_id, session_id, class_id, section_id, staff_id, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, classTeacherAllocationDetails.getSchoolId());
                ps.setInt(2, classTeacherAllocationDetails.getSessionId());
                ps.setInt(3, classTeacherAllocationDetails.getClassId());
                ps.setInt(4, classTeacherAllocationDetails.getSectionId());
                ps.setInt(5, classTeacherAllocationDetails.getStaffId());
                ps.setInt(6, classTeacherAllocationDetails.getUpdatedBy());
                ps.setDate(7, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                return ps;
            }, keyHolder);

            // Retrieve the generated key
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("cta_id")) {
                int generatedKey = ((Number) keys.get("cta_id")).intValue();
                classTeacherAllocationDetails.setCtaId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return classTeacherAllocationDetails;
    }*/
   /*@Override
   public ClassTeacherAllocationDetails addClassTeacherAllocation(ClassTeacherAllocationDetails classTeacherAllocationDetails, String schoolCode) throws Exception {
       String checkSql = "SELECT COUNT(*) FROM class_teacher_allocation WHERE class_id = ? AND section_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try {
           Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, classTeacherAllocationDetails.getClassId(), classTeacherAllocationDetails.getSectionId());
           if (count != null && count > 0) {
               // ClassId and SectionId combination already exists, return null
               return null;
           }
       } catch (EmptyResultDataAccessException e) {
           e.printStackTrace();
           return null;
       }

       // If no conflict, proceed with the insertion
       String sql = "INSERT INTO class_teacher_allocation (school_id, session_id, class_id, section_id, staff_id, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

       try {
           KeyHolder keyHolder = new GeneratedKeyHolder();
           jdbcTemplate.update(connection -> {
               PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
               ps.setInt(1, classTeacherAllocationDetails.getSchoolId());
               ps.setInt(2, classTeacherAllocationDetails.getSessionId());
               ps.setInt(3, classTeacherAllocationDetails.getClassId());
               ps.setInt(4, classTeacherAllocationDetails.getSectionId());
               ps.setInt(5, classTeacherAllocationDetails.getStaffId());
               ps.setInt(6, classTeacherAllocationDetails.getUpdatedBy());
               ps.setDate(7, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
               return ps;
           }, keyHolder);

           // Retrieve the generated key
           Map<String, Object> keys = keyHolder.getKeys();
           if (keys != null && keys.containsKey("cta_id")) {
               int generatedKey = ((Number) keys.get("cta_id")).intValue();
               classTeacherAllocationDetails.setCtaId(generatedKey);
           }
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return classTeacherAllocationDetails;
   }*/
   public boolean isClassTeacherAllocationExists(ClassTeacherAllocationDetails classTeacherAllocationDetails, String schoolCode) {
       String checkSql = "SELECT COUNT(*) FROM class_teacher_allocation WHERE class_id = ? AND section_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

       try {
           Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, classTeacherAllocationDetails.getClassId(), classTeacherAllocationDetails.getSectionId());
           return count != null && count > 0;
       } catch (EmptyResultDataAccessException e) {
           e.printStackTrace();
           return false;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }
    @Override
    public ClassTeacherAllocationDetails addClassTeacherAllocation(ClassTeacherAllocationDetails classTeacherAllocationDetails, String schoolCode) throws Exception {
        // Check if the allocation already exists
        if (isClassTeacherAllocationExists(classTeacherAllocationDetails, schoolCode)) {
            // ClassId and SectionId combination already exists, return null
            return null;
        }

        // If no conflict, proceed with the insertion
        String sql = "INSERT INTO class_teacher_allocation (school_id, session_id, class_id, section_id, staff_id, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, classTeacherAllocationDetails.getSchoolId());
                ps.setInt(2, classTeacherAllocationDetails.getSessionId());
                ps.setInt(3, classTeacherAllocationDetails.getClassId());
                ps.setInt(4, classTeacherAllocationDetails.getSectionId());
                ps.setInt(5, classTeacherAllocationDetails.getStaffId());
                ps.setInt(6, classTeacherAllocationDetails.getUpdatedBy());
                ps.setDate(7, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                return ps;
            }, keyHolder);

            // Retrieve the generated key
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("cta_id")) {
                int generatedKey = ((Number) keys.get("cta_id")).intValue();
                classTeacherAllocationDetails.setCtaId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return classTeacherAllocationDetails;
    }

    /* @Override
    public ClassTeacherAllocationDetails getClassTeacherAllocationById(int ctaId,String schoolCode) throws Exception {
        String sql = "SELECT distinct cta.cta_id, sd.school_name,ses.session_id, ses.academic_session, mc.class_id, mc.class_name, ms.section_id,cta.staff_id, ms.section_name, CONCAT(st.first_name, ' ', st.last_name) AS teacher_name FROM  class_teacher_allocation AS cta JOIN school_details AS sd ON sd.school_id = cta.school_id JOIN  session AS ses ON ses.session_id = cta.session_id JOIN  mst_class AS mc ON mc.class_id = cta.class_id JOIN mst_section AS ms ON ms.section_id = cta.section_id JOIN staff AS st ON st.staff_id = cta.staff_id WHERE cta.cta_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ClassTeacherAllocationDetails classTeacherAllocationDetails = null;
        classTeacherAllocationDetails = jdbcTemplate.queryForObject(sql, new Object[]{ctaId}, new RowMapper<ClassTeacherAllocationDetails>() {
            @Override
            public ClassTeacherAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassTeacherAllocationDetails ctad = new ClassTeacherAllocationDetails();
                ctad.setCtaId(rs.getInt("cta_id"));
                ctad.setSchoolName(rs.getString("school_name"));
                ctad.setSessionId(rs.getInt("session_id"));
                ctad.setAccademicSession(rs.getString("academic_session"));
                ctad.setClassId(rs.getInt("class_id"));
                ctad.setClassName(rs.getString("class_name"));
                ctad.setSectionId(rs.getInt("section_id"));
                ctad.setSectionName(rs.getString("section_name"));
                ctad.setStaffId(rs.getInt("staff_id"));
                ctad.setTeacherName(rs.getString("teacher_name"));
                return ctad;
            }
        });
        return classTeacherAllocationDetails;
    }*/
   @Override
   public ClassTeacherAllocationDetails getClassTeacherAllocationById(int ctaId,String schoolCode) throws Exception {
       String sql = "SELECT distinct cta.cta_id, sd.school_name,ses.session_id, ses.academic_session, mc.class_id, mc.class_name, ms.section_id,cta.staff_id, ms.section_name, CONCAT(st.first_name, ' ', st.last_name) AS teacher_name FROM  class_teacher_allocation AS cta JOIN school_details AS sd ON sd.school_id = cta.school_id JOIN  session AS ses ON ses.session_id = cta.session_id JOIN  mst_class AS mc ON mc.class_id = cta.class_id JOIN mst_section AS ms ON ms.section_id = cta.section_id JOIN staff AS st ON st.staff_id = cta.staff_id WHERE cta.cta_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       ClassTeacherAllocationDetails classTeacherAllocationDetails = null;
       try{
           classTeacherAllocationDetails = jdbcTemplate.queryForObject(sql, new Object[]{ctaId}, new RowMapper<ClassTeacherAllocationDetails>() {
               @Override
               public ClassTeacherAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ClassTeacherAllocationDetails ctad = new ClassTeacherAllocationDetails();
                   ctad.setCtaId(rs.getInt("cta_id"));
                   ctad.setSchoolName(rs.getString("school_name"));
                   ctad.setSessionId(rs.getInt("session_id"));
                   ctad.setAccademicSession(rs.getString("academic_session"));
                   ctad.setClassId(rs.getInt("class_id"));
                   ctad.setClassName(rs.getString("class_name"));
                   ctad.setSectionId(rs.getInt("section_id"));
                   ctad.setSectionName(rs.getString("section_name"));
                   ctad.setStaffId(rs.getInt("staff_id"));
                   ctad.setTeacherName(rs.getString("teacher_name"));
                   return ctad;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return classTeacherAllocationDetails;
   }
    /*@Override
    public List<ClassTeacherAllocationDetails> getAllClassTeacherAllocation(String schoolCode) throws Exception {
        String sql = "SELECT distinct cta.cta_id, sd.school_name, ses.academic_session, mc.class_name,  ms.section_name, CONCAT(st.first_name, ' ', st.last_name) AS teacher_name FROM  class_teacher_allocation AS cta JOIN school_details AS sd ON sd.school_id = cta.school_id JOIN  session AS ses ON ses.session_id = cta.session_id JOIN  mst_class AS mc ON mc.class_id = cta.class_id JOIN mst_section AS ms ON ms.section_id = cta.section_id JOIN staff AS st ON st.staff_id = cta.staff_id";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassTeacherAllocationDetails> classTeacherAllocationDetails = null;
        classTeacherAllocationDetails = jdbcTemplate.query(sql, new RowMapper<ClassTeacherAllocationDetails>() {
            @Override
            public ClassTeacherAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassTeacherAllocationDetails ctad = new ClassTeacherAllocationDetails();
                ctad.setCtaId(rs.getInt("cta_id"));
                ctad.setSchoolName(rs.getString("school_name"));
                ctad.setAccademicSession(rs.getString("academic_session"));
                ctad.setClassName(rs.getString("class_name"));
                ctad.setSectionName(rs.getString("section_name"));
                ctad.setTeacherName(rs.getString("teacher_name"));
                return ctad;
            }
        });
        return classTeacherAllocationDetails;
    }*/
    @Override
    public List<ClassTeacherAllocationDetails> getAllClassTeacherAllocation(String schoolCode) throws Exception {
        String sql = "SELECT distinct cta.cta_id, sd.school_name, ses.academic_session, mc.class_name,  ms.section_name, CONCAT(st.first_name, ' ', st.last_name) AS teacher_name FROM  class_teacher_allocation AS cta JOIN school_details AS sd ON sd.school_id = cta.school_id JOIN  session AS ses ON ses.session_id = cta.session_id JOIN  mst_class AS mc ON mc.class_id = cta.class_id JOIN mst_section AS ms ON ms.section_id = cta.section_id JOIN staff AS st ON st.staff_id = cta.staff_id";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassTeacherAllocationDetails> classTeacherAllocationDetails = null;
        try{
            classTeacherAllocationDetails = jdbcTemplate.query(sql, new RowMapper<ClassTeacherAllocationDetails>() {
                @Override
                public ClassTeacherAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassTeacherAllocationDetails ctad = new ClassTeacherAllocationDetails();
                    ctad.setCtaId(rs.getInt("cta_id"));
                    ctad.setSchoolName(rs.getString("school_name"));
                    ctad.setAccademicSession(rs.getString("academic_session"));
                    ctad.setClassName(rs.getString("class_name"));
                    ctad.setSectionName(rs.getString("section_name"));
                    ctad.setTeacherName(rs.getString("teacher_name"));
                    return ctad;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classTeacherAllocationDetails;
    }

    /*@Override
    public ClassTeacherAllocationDetails updateClassTeacherAllocationById(ClassTeacherAllocationDetails classTeacherAllocationDetails, int ctaId, String schoolCode) throws Exception {
        String sql = "update class_teacher_allocation set class_id = ?, section_id = ?, staff_id = ?, updated_by = ?, update_date_time = ? where cta_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int rowEffected = jdbcTemplate.update(sql,
                classTeacherAllocationDetails.getClassId(),
                classTeacherAllocationDetails.getSectionId(),
                classTeacherAllocationDetails.getStaffId(),
                classTeacherAllocationDetails.getUpdatedBy(),
                classTeacherAllocationDetails.getUpdateDateTime(),
                ctaId);
        if(rowEffected > 0){
            return classTeacherAllocationDetails;
        }else{
            return null;
        }
    }*/
    @Override
    public ClassTeacherAllocationDetails updateClassTeacherAllocationById(ClassTeacherAllocationDetails classTeacherAllocationDetails, int ctaId, String schoolCode) throws Exception {
        String sql = "update class_teacher_allocation set class_id = ?, section_id = ?, staff_id = ?, updated_by = ?, update_date_time = ? where cta_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    classTeacherAllocationDetails.getClassId(),
                    classTeacherAllocationDetails.getSectionId(),
                    classTeacherAllocationDetails.getStaffId(),
                    classTeacherAllocationDetails.getUpdatedBy(),
                    classTeacherAllocationDetails.getUpdateDateTime(),
                    ctaId);
            if(rowEffected > 0){
                return classTeacherAllocationDetails;
            }else{
                return null;
            }
        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
   /* @Override
    public boolean deleteClassTeacherAllocationById(int ctaId,String schoolCode) throws Exception {
        String sql = "delete from class_teacher_allocation where cta_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int rowAffected = jdbcTemplate.update(sql, new Object[]{ctaId});
        if(rowAffected > 0){
            return true;
        }else{
            return false;
        }
    }*/
   @Override
   public boolean deleteClassTeacherAllocationById(int ctaId,String schoolCode) throws Exception {
       String sql = "delete from class_teacher_allocation where cta_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql, new Object[]{ctaId});
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

    /*@Override
    public List<ClassTeacherAllocationDetails> findAllTeacherByClassAndSection(int sessionId, int classId, int sectionId, String schoolCode) throws Exception {
        String sql = "SELECT cta.staff_id, CONCAT(s.first_name, ' ', s.last_name) AS teacher_name\n" +
                "FROM class_teacher_allocation cta\n" +
                "JOIN staff s ON cta.staff_id = s.staff_id\n" +
                "WHERE " +
                "cta.session_id = ? AND " +
                "cta.class_id = ? AND " +
                "cta.section_id = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassTeacherAllocationDetails> classTeacherAllocationDetails = jdbcTemplate.query(sql,
                new Object[]{sessionId, classId, sectionId},
                new RowMapper<ClassTeacherAllocationDetails>() {
                    @Override
                    public ClassTeacherAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ClassTeacherAllocationDetails cta = new ClassTeacherAllocationDetails();
                        cta.setStaffId(rs.getInt("staff_id"));
                        cta.setTeacherName(rs.getString("teacher_name"));
                        return cta;
                    }
                });

        return classTeacherAllocationDetails;
    }*/
    @Override
    public List<ClassTeacherAllocationDetails> findAllTeacherByClassAndSection(int sessionId, int classId, int sectionId, String schoolCode) throws Exception {
        String sql = "SELECT cta.staff_id, CONCAT(s.first_name, ' ', s.last_name) AS teacher_name\n" +
                "FROM class_teacher_allocation cta\n" +
                "JOIN staff s ON cta.staff_id = s.staff_id\n" +
                "WHERE " +
                "cta.session_id = ? AND " +
                "cta.class_id = ? AND " +
                "cta.section_id = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassTeacherAllocationDetails> classTeacherAllocationDetails = null;
        try{
            classTeacherAllocationDetails = jdbcTemplate.query(sql,
                    new Object[]{sessionId, classId, sectionId},
                    new RowMapper<ClassTeacherAllocationDetails>() {
                        @Override
                        public ClassTeacherAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                            ClassTeacherAllocationDetails cta = new ClassTeacherAllocationDetails();
                            cta.setStaffId(rs.getInt("staff_id"));
                            cta.setTeacherName(rs.getString("teacher_name"));
                            return cta;
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classTeacherAllocationDetails;
    }

}
