package com.sms.dao.impl;

import com.sms.appenum.Message;
import com.sms.dao.FeeAssignmentDao;
import com.sms.model.FeeAssignmentDetails;
import com.sms.model.SchoolDetails;
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
import java.util.*;

@Repository
public class FeeAssignmentDaoImpl implements FeeAssignmentDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeeAssignmentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(fa_id), 0) + 1 AS fa_id FROM fee_assignment";
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
  /* @Override
   public FeeAssignmentDetails addFeeAssignment(FeeAssignmentDetails feeAssignmentDetails, String schoolCode) throws Exception {
       // Set the current server date and time
       feeAssignmentDetails.setUpdateDateTime(new Date());
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       // Check for existing records that would violate what was previously unique constraints
       if (existsFeeAssignment(feeAssignmentDetails, schoolCode)) {
           throw new Exception("Fee assignment conflict detected.");
       }

       // SQL Insert Statement
       String sql = "INSERT INTO fee_assignment (school_id, session_id, class_id, section_id, student_id, fee_id, dc_id, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
       try {
           jdbcTemplate.update(sql,
                   feeAssignmentDetails.getSchoolId(),
                   feeAssignmentDetails.getSessionId(),
                   feeAssignmentDetails.getClassId(),
                   feeAssignmentDetails.getSectionId(),
                   feeAssignmentDetails.getStudentId(),
                   feeAssignmentDetails.getFeeId(),
                   feeAssignmentDetails.getDcId(),
                   feeAssignmentDetails.getUpdatedBy(),
                   feeAssignmentDetails.getUpdateDateTime());
       } catch(Exception e) {
           throw new Exception("Database error: " + e.getMessage(), e);
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return feeAssignmentDetails;
   }*/
  @Override
  public FeeAssignmentDetails addFeeAssignment(FeeAssignmentDetails feeAssignmentDetails, String schoolCode) throws Exception {
      // Set the current server date and time
      feeAssignmentDetails.setUpdateDateTime(new Date());
      JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
      try{
        // Check for existing records that would violate what was previously unique constraints
        if (existsFeeAssignment(feeAssignmentDetails, schoolCode)) {
           throw new Exception("Fee assignment conflict detected.");
      }

      // SQL Insert Statement
      String sql = "INSERT INTO fee_assignment (school_id, session_id, class_id, section_id, student_id, fee_id, dc_id, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
          jdbcTemplate.update(sql,
                  feeAssignmentDetails.getSchoolId(),
                  feeAssignmentDetails.getSessionId(),
                  feeAssignmentDetails.getClassId(),
                  feeAssignmentDetails.getSectionId(),
                  feeAssignmentDetails.getStudentId(),
                  feeAssignmentDetails.getFeeId(),
                  feeAssignmentDetails.getDcId(),
                  feeAssignmentDetails.getUpdatedBy(),
                  feeAssignmentDetails.getUpdateDateTime());
      } catch(Exception e) {
          throw new Exception("Database error: " + e.getMessage(), e);
      }finally {
          DatabaseUtil.closeDataSource(jdbcTemplate);
      }
      return feeAssignmentDetails;
  }

   private boolean existsFeeAssignment(FeeAssignmentDetails feeAssignmentDetails, String schoolCode) {
       // Initialize JdbcTemplate
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           return existsWithSessionClassFee(feeAssignmentDetails, jdbcTemplate) ||
                   existsWithSessionClassSectionFee(feeAssignmentDetails, jdbcTemplate) ||
                   existsWithSessionClassSectionStudentFee(feeAssignmentDetails, jdbcTemplate);
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }
    private boolean existsWithSessionClassFee(FeeAssignmentDetails feeAssignmentDetails, JdbcTemplate jdbcTemplate) {
        String sql = "SELECT EXISTS(SELECT 1 FROM fee_assignment WHERE session_id = ? AND class_id = ? AND fee_id = ? AND section_id = 0 AND student_id = 0)";
            Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                    feeAssignmentDetails.getSessionId(),
                    feeAssignmentDetails.getClassId(),
                    feeAssignmentDetails.getFeeId());
            return Boolean.TRUE.equals(exists); // Safe unboxing that handles null
    }

    private boolean existsWithSessionClassSectionFee(FeeAssignmentDetails feeAssignmentDetails, JdbcTemplate jdbcTemplate) {
        if (feeAssignmentDetails.getSectionId() == 0) return false; // Skip if no section
        String sql = "SELECT EXISTS(SELECT 1 FROM fee_assignment WHERE session_id = ? AND class_id = ? AND section_id = ? AND fee_id = ? AND student_id = 0)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                feeAssignmentDetails.getSessionId(),
                feeAssignmentDetails.getClassId(),
                feeAssignmentDetails.getSectionId(),
                feeAssignmentDetails.getFeeId());
        return Boolean.TRUE.equals(exists); // Safe unboxing that handles null
    }

    private boolean existsWithSessionClassSectionStudentFee(FeeAssignmentDetails feeAssignmentDetails, JdbcTemplate jdbcTemplate) {
        if (feeAssignmentDetails.getStudentId() == 0) return false; // Skip if no student
        String sql = "SELECT EXISTS(SELECT 1 FROM fee_assignment WHERE session_id = ? AND class_id = ? AND section_id = ? AND student_id = ? AND fee_id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                feeAssignmentDetails.getSessionId(),
                feeAssignmentDetails.getClassId(),
                feeAssignmentDetails.getSectionId(),
                feeAssignmentDetails.getStudentId(),
                feeAssignmentDetails.getFeeId());
        return Boolean.TRUE.equals(exists);
    }
 /*  @Override
   public List<FeeAssignmentDetails> getFeeAssignments(int classId, Integer sectionId, int sessionId, Integer studentId, String schoolCode) throws Exception {
       JdbcTemplate jdbcTemplate = null;
       try{
           if (sectionId == null || sectionId == 0) {
               // Use first query when sectionId is null or 0
               String sql = "SELECT fa.fa_id, sf.fee_id, sf.fee_type, sf.frequency_id, mf.frequency_type, mc.class_name, ms.section_name " +
                       "FROM fee_assignment AS fa " +
                       "JOIN school_fees AS sf ON fa.fee_id = sf.fee_id " +
                       "JOIN mst_frequency AS mf ON sf.frequency_id = mf.frequency_id " +
                       "JOIN mst_class AS mc ON fa.class_id = mc.class_id " +
                       "LEFT JOIN mst_section AS ms ON fa.section_id = ms.section_id " +
                       "WHERE fa.school_id = 1 AND fa.session_id = ? AND fa.class_id = ? AND fa.section_id = 0 AND fa.student_id = 0";
               jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
               return jdbcTemplate.query(sql, new Object[]{sessionId, classId}, new RowMapper<FeeAssignmentDetails>() {
                   @Override
                   public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                       FeeAssignmentDetails feeAssignment = new FeeAssignmentDetails();
                       feeAssignment.setFaId(rs.getInt("fa_id"));
                       feeAssignment.setFeeId(rs.getInt("fee_id"));
                       feeAssignment.setFeeType(rs.getString("fee_type"));
                       feeAssignment.setFrequencyId(rs.getInt("frequency_id"));
                       feeAssignment.setFrequencyType(rs.getString("frequency_type"));
                       feeAssignment.setClassName(rs.getString("class_name"));
                       feeAssignment.setSectionName(rs.getString("section_name"));
                       return feeAssignment;
                   }
               });
           }else if (studentId == null || studentId == 0) {
               // Second query with UNION and NOT IN
               String sql = "SELECT fa1.fa_id, sf1.fee_id, sf1.fee_type, sf1.frequency_id, mf1.frequency_type, mc1.class_name, ms1.section_name " +
                       "FROM fee_assignment AS fa1 " +
                       "JOIN school_fees AS sf1 ON fa1.fee_id = sf1.fee_id " +
                       "JOIN mst_frequency AS mf1 ON sf1.frequency_id = mf1.frequency_id " +
                       "JOIN  mst_class AS mc1 ON fa1.class_id = mc1.class_id "+
                       "LEFT JOIN mst_section AS ms1 ON fa1.section_id = ms1.section_id "+
                       "WHERE fa1.school_id = 1 AND fa1.session_id = ? AND fa1.class_id = ? AND fa1.section_id = 0 AND fa1.student_id = 0 " +
                       "AND fa1.fee_id NOT IN (" +
                       "    SELECT DISTINCT fa3.fee_id " +
                       "    FROM fee_assignment AS fa3 " +
                       "    WHERE fa3.school_id = 1 AND fa3.session_id = ? AND fa3.class_id = ? AND fa3.section_id = ? AND fa3.student_id = 0) " +
                       "UNION " +
                       "SELECT fa2.fa_id, sf2.fee_id, sf2.fee_type, sf2.frequency_id, mf2.frequency_type, mc2.class_name, ms2.section_name " +
                       "FROM fee_assignment AS fa2 " +
                       "JOIN school_fees AS sf2 ON fa2.fee_id = sf2.fee_id " +
                       "JOIN mst_frequency AS mf2 ON sf2.frequency_id = mf2.frequency_id " +
                       "JOIN  mst_class AS mc2 ON fa2.class_id = mc2.class_id "+
                       "LEFT JOIN mst_section AS ms2 ON fa2.section_id = ms2.section_id " +
                       "WHERE fa2.school_id = 1 AND fa2.session_id = ? AND fa2.class_id = ? AND fa2.section_id = ? AND fa2.student_id = 0";
               jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

               return jdbcTemplate.query(sql, new Object[]{sessionId, classId, sessionId, classId, sectionId, sessionId, classId, sessionId}, new RowMapper<FeeAssignmentDetails>() {
                   @Override
                   public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                       FeeAssignmentDetails feeAssignment = new FeeAssignmentDetails();
                       feeAssignment.setFaId(rs.getInt("fa_id"));
                       feeAssignment.setFeeId(rs.getInt("fee_id"));
                       feeAssignment.setFeeType(rs.getString("fee_type"));
                       feeAssignment.setFrequencyId(rs.getInt("frequency_id"));
                       feeAssignment.setFrequencyType(rs.getString("frequency_type"));
                       feeAssignment.setClassName(rs.getString("class_name"));
                       feeAssignment.setSectionName(rs.getString("section_name"));
                       return feeAssignment;
                   }
               });
           }else{
               String sql = "SELECT fa1.fa_id, sf1.fee_id, sf1.fee_type, sf1.frequency_id, mf1.frequency_type, mc1.class_name, ms1.section_name,  CONCAT(spd1.first_name, ' ', spd1.last_name) AS student_name " +
                       "FROM fee_assignment AS fa1 " +
                       "JOIN school_fees AS sf1 ON fa1.fee_id = sf1.fee_id " +
                       "JOIN mst_frequency AS mf1 ON sf1.frequency_id = mf1.frequency_id " +
                       "JOIN mst_class AS mc1 ON fa1.class_id = mc1.class_id " +
                       "LEFT JOIN mst_section AS ms1 ON fa1.section_id = ms1.section_id " +
                       "LEFT JOIN student_personal_details AS spd1 ON fa1.student_id = spd1.student_id " +
                       "WHERE fa1.school_id = 1 " +
                       "  AND fa1.session_id = ? " +
                       "  AND fa1.class_id = ? " +
                       "  AND fa1.section_id = 0 " +
                       "  AND fa1.student_id = 0 " +
                       "  AND fa1.fee_id NOT IN ( " +
                       "    SELECT DISTINCT fa3.fee_id  " +
                       "    FROM fee_assignment AS fa3 " +
                       "    WHERE fa3.school_id = 1 " +
                       "      AND fa3.session_id = ? " +
                       "      AND fa3.class_id = ? " +
                       "      AND fa3.section_id = ? " +
                       "      AND fa3.student_id = 0 " +
                       "  ) " +
                       " " +
                       "UNION " +
                       " " +
                       "SELECT fa2.fa_id, sf2.fee_id, sf2.fee_type, sf2.frequency_id, mf2.frequency_type, mc2.class_name, ms2.section_name, CONCAT(spd2.first_name, ' ', spd2.last_name) AS student_name " +
                       "FROM fee_assignment AS fa2 " +
                       "JOIN school_fees AS sf2 ON fa2.fee_id = sf2.fee_id " +
                       "JOIN mst_frequency AS mf2 ON sf2.frequency_id = mf2.frequency_id " +
                       "JOIN  mst_class AS mc2 ON fa2.class_id = mc2.class_id " +
                       "LEFT JOIN mst_section AS ms2 ON fa2.section_id = ms2.section_id " +
                       "LEFT JOIN student_personal_details AS spd2 ON fa2.student_id = spd2.student_id " +
                       "WHERE fa2.school_id = 1 " +
                       "  AND fa2.session_id = ? " +
                       "  AND fa2.class_id = ? " +
                       "  AND fa2.section_id = ? " +
                       "  AND fa2.student_id = 0 " +
                       "  AND fa2.fee_id NOT IN ( " +
                       "    SELECT DISTINCT fa6.fee_id  " +
                       "    FROM fee_assignment AS fa6 " +
                       "    WHERE fa6.school_id = 1 " +
                       "      AND fa6.session_id = ? " +
                       "      AND fa6.class_id = ? " +
                       "      AND fa6.section_id = ? " +
                       "      AND fa6.student_id = ? " +
                       "  ) " +
                       " " +
                       "UNION " +
                       " " +
                       "SELECT fa4.fa_id, sf4.fee_id, sf4.fee_type, sf4.frequency_id, mf4.frequency_type, mc4.class_name, ms4.section_name,  CONCAT(spd4.first_name, ' ', spd4.last_name) AS student_name " +
                       "FROM fee_assignment AS fa4 " +
                       "JOIN school_fees AS sf4 ON fa4.fee_id = sf4.fee_id " +
                       "JOIN mst_frequency AS mf4 ON sf4.frequency_id = mf4.frequency_id " +
                       "JOIN  mst_class AS mc4 ON fa4.class_id = mc4.class_id " +
                       "LEFT JOIN mst_section AS ms4 ON fa4.section_id = ms4.section_id " +
                       "LEFT JOIN student_personal_details AS spd4 ON fa4.student_id = spd4.student_id " +
                       "WHERE fa4.school_id = 1 " +
                       "  AND fa4.session_id = ? " +
                       "  AND fa4.class_id = ? " +
                       "  AND fa4.section_id = ? " +
                       "  AND fa4.student_id = ?";
               jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
               return jdbcTemplate.query(sql, new Object[]{sessionId, classId, sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId, studentId, sessionId, classId, sectionId, studentId}, new RowMapper<FeeAssignmentDetails>() {
                   @Override
                   public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                       FeeAssignmentDetails feeAssignment = new FeeAssignmentDetails();
                       feeAssignment.setFaId(rs.getInt("fa_id"));
                       feeAssignment.setFeeId(rs.getInt("fee_id"));
                       feeAssignment.setFeeType(rs.getString("fee_type"));
                       feeAssignment.setFrequencyId(rs.getInt("frequency_id"));
                       feeAssignment.setFrequencyType(rs.getString("frequency_type"));
                       feeAssignment.setClassName(rs.getString("class_name"));
                       feeAssignment.setSectionName(rs.getString("section_name"));
                       feeAssignment.setStudentName(rs.getString("student_name"));
                       return feeAssignment;
                   }
               });
           }
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }*/
 @Override
 public List<FeeAssignmentDetails> getFeeAssignments(int classId, Integer sectionId, int sessionId, Integer studentId, String schoolCode) throws Exception {
     JdbcTemplate jdbcTemplate = null;
     try {
         jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
         RowMapper<FeeAssignmentDetails> rowMapper = getRowMapper();

         if (sectionId == null || sectionId == 0) {
             return handleClassLevel(jdbcTemplate, sessionId, classId, rowMapper);
         } else if (studentId == null || studentId == 0) {
             return handleSectionLevel(jdbcTemplate, sessionId, classId, sectionId, rowMapper);
         } else {
             return handleStudentLevel(jdbcTemplate, sessionId, classId, sectionId, studentId, rowMapper);
         }
     } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("Error fetching fee assignments", e);
     } finally {
         DatabaseUtil.closeDataSource(jdbcTemplate);
     }
 }

    private RowMapper<FeeAssignmentDetails> getRowMapper() {
        return (rs, rowNum) -> {
            FeeAssignmentDetails details = new FeeAssignmentDetails();
            details.setFaId(rs.getInt("fa_id"));
            details.setFeeId(rs.getInt("fee_id"));
            details.setFeeType(rs.getString("fee_type"));
            details.setFrequencyId(rs.getInt("frequency_id"));
            details.setFrequencyType(rs.getString("frequency_type"));
            details.setClassName(rs.getString("class_name"));
            details.setSectionName(rs.getString("section_name"));
            try {
                details.setStudentName(rs.getString("student_name"));
            } catch (SQLException ignored) {
                // Column not present, ignore
            }
            return details;
        };
    }

    private List<FeeAssignmentDetails> handleClassLevel(JdbcTemplate jdbcTemplate, int sessionId, int classId, RowMapper<FeeAssignmentDetails> rowMapper) {
        String sql = "SELECT fa.fa_id, sf.fee_id, sf.fee_type, sf.frequency_id, mf.frequency_type, mc.class_name, ms.section_name " +
                "FROM fee_assignment fa " +
                "JOIN school_fees sf ON fa.fee_id = sf.fee_id " +
                "JOIN mst_frequency mf ON sf.frequency_id = mf.frequency_id " +
                "JOIN mst_class mc ON fa.class_id = mc.class_id " +
                "LEFT JOIN mst_section ms ON fa.section_id = ms.section_id " +
                "WHERE fa.school_id = 1 AND fa.session_id = ? AND fa.class_id = ? " +
                "AND fa.section_id = 0 AND fa.student_id = 0";
        return jdbcTemplate.query(sql, new Object[]{sessionId, classId}, rowMapper);
    }

    private List<FeeAssignmentDetails> handleSectionLevel(JdbcTemplate jdbcTemplate, int sessionId, int classId, int sectionId, RowMapper<FeeAssignmentDetails> rowMapper) {
        String sql = "SELECT fa1.fa_id, sf1.fee_id, sf1.fee_type, sf1.frequency_id, mf1.frequency_type, mc1.class_name, ms1.section_name " +
                "FROM fee_assignment fa1 " +
                getCommonJoins("fa1", "sf1", "mf1", "mc1", "ms1") +
                "WHERE fa1.school_id = 1 AND fa1.session_id = ? AND fa1.class_id = ? AND fa1.section_id = 0 AND fa1.student_id = 0 " +
                "AND fa1.fee_id NOT IN (SELECT fa3.fee_id FROM fee_assignment fa3 WHERE fa3.school_id = 1 AND fa3.session_id = ? AND fa3.class_id = ? AND fa3.section_id = ? AND fa3.student_id = 0) " +
                "UNION " +
                "SELECT fa2.fa_id, sf2.fee_id, sf2.fee_type, sf2.frequency_id, mf2.frequency_type, mc2.class_name, ms2.section_name " +
                "FROM fee_assignment fa2 " +
                getCommonJoins("fa2", "sf2", "mf2", "mc2", "ms2") +
                "WHERE fa2.school_id = 1 AND fa2.session_id = ? AND fa2.class_id = ? AND fa2.section_id = ? AND fa2.student_id = 0";

        List<Object> params = Arrays.asList(
                sessionId, classId,
                sessionId, classId, sectionId,
                sessionId, classId, sectionId
        );
        return jdbcTemplate.query(sql, params.toArray(), rowMapper);
    }

    private List<FeeAssignmentDetails> handleStudentLevel(JdbcTemplate jdbcTemplate, int sessionId, int classId, int sectionId, int studentId, RowMapper<FeeAssignmentDetails> rowMapper) {
        String sql = "SELECT fa1.fa_id, sf1.fee_id, sf1.fee_type, sf1.frequency_id, mf1.frequency_type, mc1.class_name, ms1.section_name, CONCAT(spd1.first_name, ' ', spd1.last_name) AS student_name " +
                "FROM fee_assignment fa1 " +
                getCommonJoins("fa1", "sf1", "mf1", "mc1", "ms1") +
                "LEFT JOIN student_personal_details spd1 ON fa1.student_id = spd1.student_id " +
                "WHERE fa1.school_id = 1 AND fa1.session_id = ? AND fa1.class_id = ? AND fa1.section_id = 0 AND fa1.student_id = 0 " +
                "AND fa1.fee_id NOT IN (SELECT fa3.fee_id FROM fee_assignment fa3 WHERE fa3.school_id = 1 AND fa3.session_id = ? AND fa3.class_id = ? AND fa3.section_id = ? AND fa3.student_id = 0) " +
                "UNION " +
                "SELECT fa2.fa_id, sf2.fee_id, sf2.fee_type, sf2.frequency_id, mf2.frequency_type, mc2.class_name, ms2.section_name, CONCAT(spd2.first_name, ' ', spd2.last_name) AS student_name " +
                "FROM fee_assignment fa2 " +
                getCommonJoins("fa2", "sf2", "mf2", "mc2", "ms2") +
                "LEFT JOIN student_personal_details spd2 ON fa2.student_id = spd2.student_id " +
                "WHERE fa2.school_id = 1 AND fa2.session_id = ? AND fa2.class_id = ? AND fa2.section_id = ? AND fa2.student_id = 0 " +
                "AND fa2.fee_id NOT IN (SELECT fa6.fee_id FROM fee_assignment fa6 WHERE fa6.school_id = 1 AND fa6.session_id = ? AND fa6.class_id = ? AND fa6.section_id = ? AND fa6.student_id = ?) " +
                "UNION " +
                "SELECT fa4.fa_id, sf4.fee_id, sf4.fee_type, sf4.frequency_id, mf4.frequency_type, mc4.class_name, ms4.section_name, CONCAT(spd4.first_name, ' ', spd4.last_name) AS student_name " +
                "FROM fee_assignment fa4 " +
                getCommonJoins("fa4", "sf4", "mf4", "mc4", "ms4") +
                "LEFT JOIN student_personal_details spd4 ON fa4.student_id = spd4.student_id " +
                "WHERE fa4.school_id = 1 AND fa4.session_id = ? AND fa4.class_id = ? AND fa4.section_id = ? AND fa4.student_id = ?";

        List<Object> params = Arrays.asList(
                sessionId, classId, sessionId, classId, sectionId,
                sessionId, classId, sectionId, sessionId, classId, sectionId, studentId,
                sessionId, classId, sectionId, studentId
        );
        return jdbcTemplate.query(sql, params.toArray(), rowMapper);
    }

    private String getCommonJoins(String faAlias, String sfAlias, String mfAlias, String mcAlias, String msAlias) {
        return String.format(
                "JOIN school_fees %s ON %s.fee_id = %s.fee_id " +
                        "JOIN mst_frequency %s ON %s.frequency_id = %s.frequency_id " +
                        "JOIN mst_class %s ON %s.class_id = %s.class_id " +
                        "LEFT JOIN mst_section %s ON %s.section_id = %s.section_id ",
                sfAlias, faAlias, sfAlias,
                mfAlias, sfAlias, mfAlias,
                mcAlias, faAlias, mcAlias,
                msAlias, faAlias, msAlias
        );
    }
    @Override
    public FeeAssignmentDetails getFeeAssignmentById(int faId, String schoolCode) throws Exception {
        String sql = "SELECT " +
                "    fa.fa_id, " +
                "    sd.school_name, " +
                "    sess.academic_session, " +
                "    mc.class_name, " +
                "    ms.section_name, " +
                "    spd.first_name || ' ' || spd.last_name AS student_name, " +
                "    sf.fee_type, " +
                "    dc.dc_description, " +
                "    dc.dc_rate, " +
                "    dc.dc_rate_type  " +
                "FROM fee_assignment fa " +
                "LEFT JOIN school_details sd ON fa.school_id = sd.school_id " +
                "LEFT JOIN session sess ON fa.session_id = sess.session_id " +
                "LEFT JOIN mst_class mc ON fa.class_id = mc.class_id " +
                "LEFT JOIN mst_section ms ON fa.section_id = ms.section_id AND fa.section_id <> 0  " +
                "LEFT JOIN student_personal_details spd ON fa.student_id = spd.student_id AND fa.student_id <> 0  " +
                "LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id " +
                "LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id " +
                "WHERE fa.fa_id = ? " +
                "AND spd.deleted is not true ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        FeeAssignmentDetails feeAssignmentDetails = null;
        try{
            feeAssignmentDetails = jdbcTemplate.queryForObject(sql, new Object[]{faId}, new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails fad = new FeeAssignmentDetails();
                    fad.setFaId(rs.getInt("fa_id"));
                    fad.setSchoolName(rs.getString("school_name"));
                    fad.setAcademicSession(rs.getString("academic_session"));
                    fad.setClassName(rs.getString("class_name"));
                    fad.setSectionName(rs.getString("section_name"));
                    fad.setStudentName(rs.getString("student_name"));
                    fad.setFeeType(rs.getString("fee_type"));
                    fad.setDcDescription(rs.getString("dc_description"));
                    fad.setDcRate(rs.getInt("dc_rate"));
                    fad.setDcRateType(rs.getString("dc_rate_type"));
                    return fad;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return feeAssignmentDetails;
    }
    @Override
    public List<FeeAssignmentDetails> getAllFeeAssignment(String schoolCode) throws Exception {
        String sql = "SELECT " +
                "    fa.fa_id, " +
                "    sd.school_name, " +
                "    sess.academic_session, " +
                "    mc.class_name, " +
                "    ms.section_name, " +
                "    spd.first_name || ' ' || spd.last_name AS student_name, " +
                "    sf.fee_type, " +
                "    dc.dc_description, " +
                "    dc.dc_rate, " +
                "    dc.dc_rate_type,  " +
                "    CASE WHEN EXISTS ( SELECT 1 FROM fee_due_date fdd WHERE fdd.fa_id = fa.fa_id) THEN true ELSE false END As is_due_date_assigned " +
                "FROM fee_assignment fa " +
                "LEFT JOIN school_details sd ON fa.school_id = sd.school_id " +
                "LEFT JOIN session sess ON fa.session_id = sess.session_id " +
                "LEFT JOIN mst_class mc ON fa.class_id = mc.class_id " +
                "LEFT JOIN mst_section ms ON fa.section_id = ms.section_id AND fa.section_id <> 0  " +
                "LEFT JOIN student_personal_details spd ON fa.student_id = spd.student_id AND fa.student_id <> 0  " +
                "LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id " +
                "LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id " +
                "WHERE spd.deleted is not true AND session_id=1";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeAssignmentDetails> feeAssignmentDetails = null;
        try{
            feeAssignmentDetails = jdbcTemplate.query(sql, new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails fad = new FeeAssignmentDetails();
                    fad.setFaId(rs.getInt("fa_id"));
                    fad.setSchoolName(rs.getString("school_name"));
                    fad.setAcademicSession(rs.getString("academic_session"));
                    fad.setClassName(rs.getString("class_name"));
                    fad.setSectionName(rs.getString("section_name"));
                    fad.setStudentName(rs.getString("student_name"));
                    fad.setFeeType(rs.getString("fee_type"));
                    fad.setDcDescription(rs.getString("dc_description"));
                    fad.setDcRate(rs.getInt("dc_rate"));
                    fad.setDcRateType(rs.getString("dc_rate_type"));
                    fad.setIsDueDateAssigned(rs.getBoolean("is_due_date_assigned"));
                    return fad;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeAssignmentDetails;
    }
    @Override
    public FeeAssignmentDetails getDiscountDetails(int faId, double amount, String schoolCode) throws Exception {
        String sql = "SELECT fa.fa_id, sf.frequency_id, mf.frequency_type, " +
                "CASE WHEN dc.dc_rate_type = 'Percentage' THEN ? * dc.dc_rate / 100 " +
                "WHEN dc.dc_rate_type = 'Fixed' THEN dc.dc_rate ELSE 0 END AS discount_amount " +
                "FROM fee_assignment fa " +
                "INNER JOIN school_fees sf ON fa.fee_id = sf.fee_id " +
                "INNER JOIN mst_frequency mf ON sf.frequency_id = mf.frequency_id " +
                "LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id " +
                "WHERE fa.fa_id = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{amount, faId}, new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails feeAssignmentDetails = new FeeAssignmentDetails();
                    feeAssignmentDetails.setFaId(rs.getInt("fa_id"));
                    feeAssignmentDetails.setFrequencyId(rs.getInt("frequency_id"));
                    feeAssignmentDetails.setFrequencyType(rs.getString("frequency_type"));

                    double discountAmount = rs.getDouble("discount_amount");
                    String frequencyType = rs.getString("frequency_type");

                    // Adjust discount amount based on frequency type
                    switch (frequencyType) {
                        case "Monthly":
                            discountAmount /= 12;
                            break;
                        case "Weekly":
                            discountAmount /= 52;
                            break;
                        case "Half Yearly":
                            discountAmount /= 2;
                            break;
                        case "Yearly":
                            // discountAmount /= 1; // This line is redundant
                            break;
                        default:
                            // Handle any unexpected frequency types if necessary
                            break;
                    }

                    feeAssignmentDetails.setDiscountAmount(discountAmount);
                    return feeAssignmentDetails;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

   /* @Override
    public List<FeeAssignmentDetails> getFeeAssignmentDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "SELECT " +
                "    fa.fa_id, " +
                "    sess.academic_session, " +
                "    mc.class_name, " +
                "    ms.section_name, " +
                "    spd.first_name || ' ' || spd.last_name AS student_name, " +
                "    sf.fee_type " +
                "FROM fee_assignment fa " +
                "LEFT JOIN school_details sd ON fa.school_id = sd.school_id " +
                "LEFT JOIN session sess ON fa.session_id = sess.session_id " +
                "LEFT JOIN mst_class mc ON fa.class_id = mc.class_id " +
                "LEFT JOIN mst_section ms ON fa.section_id = ms.section_id AND fa.section_id <> 0  " +
                "LEFT JOIN student_personal_details spd ON fa.student_id = spd.student_id AND fa.student_id <> 0  " +
                "LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id " +
                "LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id " +
                "WHERE " +
                "CONCAT_WS(' ', fa.fa_id, sess.academic_session, mc.class_name, ms.section_name, spd.first_name, spd.last_name, sf.fee_type) ILIKE ? " +
                "AND spd.deleted is not true ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeAssignmentDetails> feeAssignmentDetails = null;
        try{
            feeAssignmentDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails fad = new FeeAssignmentDetails();
                    fad.setFaId(rs.getInt("fa_id"));
                    fad.setAcademicSession(rs.getString("academic_session"));
                    fad.setClassName(rs.getString("class_name"));
                    fad.setSectionName(rs.getString("section_name"));
                    fad.setStudentName(rs.getString("student_name"));
                    fad.setFeeType(rs.getString("fee_type"));
                    return fad;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeAssignmentDetails;
    }*/

   /* @Override
    public List<FeeAssignmentDetails> getFeeAssignmentDetailsByFilters(String schoolCode, Integer classId, Integer sectionId, Integer studentId, Integer fatherId, String registrationNumber) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("    fa.fa_id, ")
                .append("    sess.academic_session, ")
                .append("    mc.class_name, ")
                .append("    ms.section_name, ")
                .append("    spd.first_name || ' ' || spd.last_name AS student_name, ")
                .append("    sf.fee_type ")
                .append("FROM fee_assignment fa ")
                .append("LEFT JOIN school_details sd ON fa.school_id = sd.school_id ")
                .append("LEFT JOIN session sess ON fa.session_id = sess.session_id ")
                .append("LEFT JOIN mst_class mc ON fa.class_id = mc.class_id ")
                .append("LEFT JOIN mst_section ms ON fa.section_id = ms.section_id AND fa.section_id <> 0 ")
                .append("LEFT JOIN student_personal_details spd ON fa.student_id = spd.student_id AND fa.student_id <> 0 ")
                .append("LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id ")
                .append("LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id ")
                .append("WHERE spd.deleted is not true ");

        // Adding dynamic conditions based on non-null parameters
        List<Object> params = new ArrayList<>();
        if (classId != null) {
            sql.append("AND fa.class_id = ? ");
            params.add(classId);
        }
        if (sectionId != null) {
            sql.append("AND fa.section_id = ? ");
            params.add(sectionId);
        }
        if (studentId != null) {
            sql.append("AND fa.student_id = ? ");
            params.add(studentId);
        }
        if (fatherId != null) {
            sql.append("AND spd.father_id = ? ");
            params.add(fatherId);
        }
        if (registrationNumber != null && !registrationNumber.trim().isEmpty()) {
            sql.append("AND spd.registration_number = ? ");
            params.add(registrationNumber);
        }

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeAssignmentDetails> feeAssignmentDetails = null;
        try {
            feeAssignmentDetails = jdbcTemplate.query(sql.toString(), params.toArray(), new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails fad = new FeeAssignmentDetails();
                    fad.setFaId(rs.getInt("fa_id"));
                    fad.setAcademicSession(rs.getString("academic_session"));
                    fad.setClassName(rs.getString("class_name"));
                    fad.setSectionName(rs.getString("section_name"));
                    fad.setStudentName(rs.getString("student_name"));
                    fad.setFeeType(rs.getString("fee_type"));
                    return fad;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return feeAssignmentDetails;
    }*/
   /*@Override
   public List<FeeAssignmentDetails> getFeeAssignmentDetailsByFilters(
           String schoolCode, Integer classId, Integer sectionId, Integer studentId,
           String searchText) throws Exception {

       StringBuilder sql = new StringBuilder();
       sql.append("SELECT ")
               .append("    fa.fa_id, ")
               .append("    sess.academic_session, ")
               .append("    mc.class_name, ")
               .append("    ms.section_name, ")
               .append("    spd.first_name || ' ' || spd.last_name AS student_name, ")
               .append("    sf.fee_type, ")
               .append("    sad.registration_number, ")
               .append("    sad.admission_no ")
               .append("FROM fee_assignment fa ")
               .append("LEFT JOIN school_details sd ON fa.school_id = sd.school_id ")
               .append("LEFT JOIN session sess ON fa.session_id = sess.session_id ")
               .append("LEFT JOIN mst_class mc ON fa.class_id = mc.class_id ")
               .append("LEFT JOIN mst_section ms ON fa.section_id = ms.section_id AND fa.section_id <> 0 ")
               .append("LEFT JOIN student_personal_details spd ON fa.student_id = spd.student_id AND fa.student_id <> 0 ")
               .append("LEFT JOIN student_academic_details sad ON fa.student_id = sad.student_id AND fa.student_id <> 0 ")
               .append("LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id ")
               .append("LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id ")
               .append("WHERE spd.deleted IS NOT TRUE ");

       // Adding dynamic conditions based on non-null parameters
       List<Object> params = new ArrayList<>();
       if (classId != null) {
           sql.append("AND fa.class_id = ? ");
           params.add(classId);
       }
       if (sectionId != null) {
           sql.append("AND fa.section_id = ? ");
           params.add(sectionId);
       }
       if (studentId != null) {
           sql.append("AND fa.student_id = ? ");
           params.add(studentId);
       }

       // Adding searchText condition to the query for multiple fields
       if (searchText != null && !searchText.trim().isEmpty()) {
           sql.append("AND CONCAT_WS(' ',spd.first_name, spd.last_name, sf.fee_type, sad.registration_number, sad.admission_no) ILIKE ? ");
           String searchPattern = "%" + searchText + "%";
           params.add(searchPattern);
       }

       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<FeeAssignmentDetails> feeAssignmentDetails = null;
       try {
           feeAssignmentDetails = jdbcTemplate.query(sql.toString(), params.toArray(), new RowMapper<FeeAssignmentDetails>() {
               @Override
               public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   FeeAssignmentDetails fad = new FeeAssignmentDetails();
                   fad.setFaId(rs.getInt("fa_id"));
                   fad.setAcademicSession(rs.getString("academic_session"));
                   fad.setClassName(rs.getString("class_name"));
                   fad.setSectionName(rs.getString("section_name"));
                   fad.setStudentName(rs.getString("student_name"));
                   fad.setFeeType(rs.getString("fee_type"));
                   return fad;
               }
           });
       } catch (EmptyResultDataAccessException e) {
           return null;
       } catch (Exception e) {
           e.printStackTrace();
           throw e;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return feeAssignmentDetails;
   }*/

    @Override
    public List<FeeAssignmentDetails> getFeeAssignmentDetailsByFilters(
            String schoolCode, Integer sessionId,Integer classId, Integer sectionId, Integer studentId,
            String searchText) throws Exception {
        if (schoolCode == null || schoolCode.trim().isEmpty()) {
            throw new IllegalArgumentException("schoolCode must not be null or empty");
        }
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId must not be null");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("    fa.fa_id, ")
                .append("    sd.school_name, ")
                .append("    sess.academic_session, ")
                .append("    mc.class_name, ")
                .append("    ms.section_name, ")
                .append("    spd.first_name || ' ' || spd.last_name AS student_name, ")
                .append("    sf.fee_type, ")
                .append("    dc.dc_description, ")
                .append("    dc.dc_rate, ")
                .append("    dc.dc_rate_type, ")
                .append("    CASE ")
                .append("        WHEN EXISTS (SELECT 1 FROM fee_due_date fdd WHERE fdd.fa_id = fa.fa_id) ")
                .append("        THEN true ")
                .append("        ELSE false ")
                .append("    END AS is_due_date_assigned ")
                .append("FROM fee_assignment fa ")
                .append("LEFT JOIN school_details sd ON fa.school_id = sd.school_id ")
                .append("LEFT JOIN session sess ON fa.session_id = sess.session_id ")
                .append("LEFT JOIN mst_class mc ON fa.class_id = mc.class_id ")
                .append("LEFT JOIN mst_section ms ON fa.section_id = ms.section_id AND fa.section_id <> 0 ")
                .append("LEFT JOIN student_personal_details spd ON fa.student_id = spd.student_id AND fa.student_id <> 0 ")
                .append("LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id ")
                .append("LEFT JOIN discount_code dc ON fa.dc_id = dc.dc_id ")
                .append("WHERE spd.deleted IS NOT TRUE AND fa.session_id=? ");


        // Adding dynamic conditions based on non-null parameters
        List<Object> params = new ArrayList<>();
        params.add(sessionId);
        if (classId != null) {
            sql.append("AND fa.class_id = ? ");
            params.add(classId);
        }
        if (sectionId != null) {
            sql.append("AND fa.section_id = ? ");
            params.add(sectionId);
        }
        if (studentId != null) {
            sql.append("AND fa.student_id = ? ");
            params.add(studentId);
        }

        // Adding searchText condition to the query for multiple fields
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append("AND CONCAT_WS(' ',spd.first_name, spd.last_name, sf.fee_type, dc.dc_description, dc.dc_rate, dc.dc_rate_type) ILIKE ? ");
            String searchPattern = "%" + searchText + "%";
            params.add(searchPattern);
        }
        // Adding ORDER BY clause for class_name and section_name
        sql.append("ORDER BY mc.class_name, ms.section_name ");

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeAssignmentDetails> feeAssignmentDetails = null;
        try {
            feeAssignmentDetails = jdbcTemplate.query(sql.toString(), params.toArray(), new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails fad = new FeeAssignmentDetails();
                    fad.setFaId(rs.getInt("fa_id"));
                    fad.setSchoolName(rs.getString("school_name"));
                    fad.setAcademicSession(rs.getString("academic_session"));
                    fad.setClassName(rs.getString("class_name"));
                    fad.setSectionName(rs.getString("section_name"));
                    fad.setStudentName(rs.getString("student_name"));
                    fad.setFeeType(rs.getString("fee_type"));
                    fad.setDcDescription(rs.getString("dc_description"));
                    fad.setDcRate(rs.getInt("dc_rate"));
                    fad.setDcRateType(rs.getString("dc_rate_type"));
                    fad.setIsDueDateAssigned(rs.getBoolean("is_due_date_assigned"));
                    return fad;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return feeAssignmentDetails;
    }



    @Override
    public List<FeeAssignmentDetails> getFeeAssignmentByFaIdAndSession(int faId, int sessionId, String schoolCode) throws Exception {
        String sql = """
                SELECT\s
                    fdd.fddt_id,\s
                    fdd.school_id,\s
                    fdd.fa_id,\s
                    fdd.due_date,\s
                    fdd.fee_amount,\s
                    fdd.discount_amount,
                    sf.fee_type,
                    mf.frequency_id,
                    mf.frequency_type,
                    COALESCE(mc.class_name, '') AS class_name,
                    COALESCE(ms.section_name, '') AS section_name,
                    COALESCE(spd.first_name || ' ' || spd.last_name, '') AS student_name
                FROM\s
                    fee_due_date fdd
                JOIN\s
                    fee_assignment fa ON fdd.fa_id = fa.fa_id
                JOIN\s
                    session s ON fa.session_id = s.session_id
                LEFT JOIN\s
                    mst_class mc ON fa.class_id = mc.class_id AND mc.class_id <> 0
                LEFT JOIN\s
                    mst_section ms ON fa.section_id = ms.section_id AND ms.section_id <> 0
                LEFT JOIN\s
                    student_personal_details spd ON fa.student_id = spd.student_id AND spd.student_id <> 0
                LEFT JOIN\s
                    school_fees sf ON fa.fee_id = sf.fee_id   \s
                LEFT JOIN\s
                    mst_frequency mf ON sf.frequency_id = mf.frequency_id
                WHERE\s
                    s.session_id = ?
                    AND fdd.fa_id = ?
                    AND spd.deleted IS NOT TRUE
                ORDER BY\s
                    fdd.fddt_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeAssignmentDetails> feeAssignmentDetails = null;
        try{
            feeAssignmentDetails = jdbcTemplate.query(sql, new Object[]{sessionId, faId}, new RowMapper<FeeAssignmentDetails>() {
                @Override
                public FeeAssignmentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeAssignmentDetails fad = new FeeAssignmentDetails();
                    fad.setFddtId(rs.getInt("fddt_id"));
                    fad.setSchoolId(rs.getInt("school_id"));
                    fad.setFaId(rs.getInt("fa_id"));
                    fad.setDueDate(rs.getDate("due_date"));
                    fad.setFeeAmount(rs.getDouble("fee_amount"));
                    fad.setDiscountAmount(rs.getDouble("discount_amount"));
                    fad.setFeeType(rs.getString("fee_type"));
                    fad.setFrequencyId(rs.getInt("frequency_id"));
                    fad.setFrequencyType(rs.getString("frequency_type"));
                    fad.setClassName(rs.getString("class_name"));
                    fad.setSectionName(rs.getString("section_name"));
                    fad.setStudentName(rs.getString("student_name"));
                    return fad;
                }
            });
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeAssignmentDetails;
    }

}
