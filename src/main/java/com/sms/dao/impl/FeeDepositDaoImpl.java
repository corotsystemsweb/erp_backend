package com.sms.dao.impl;

import com.sms.dao.FeeDepositDao;
import com.sms.model.DiscountCodeDetails;
import com.sms.model.FeeDepositDetails;
import com.sms.model.FeeDueDateDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class FeeDepositDaoImpl implements FeeDepositDao {
    private final JdbcTemplate jdbcTemplate;

    public FeeDepositDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FeeDepositDetails addFeeDeposit(FeeDepositDetails feeDepositDetails, String schoolCode) throws Exception {
        String sql = "insert into fee_deposit (school_id, session_id, class_id, section_id, student_id, payment_mode, total_amount_paid, payment_received_by, system_date_time, payment_description, transaction_id, comment, fdd_status, payment_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, feeDepositDetails.getSchoolId());
                ps.setInt(2, feeDepositDetails.getSessionId());
                ps.setInt(3, feeDepositDetails.getClassId());
                ps.setInt(4, feeDepositDetails.getSectionId());
                ps.setInt(5, feeDepositDetails.getStudentId());
                ps.setInt(6, feeDepositDetails.getPaymentMode());
                ps.setInt(7, feeDepositDetails.getTotalAmountPaid());
                ps.setInt(8, feeDepositDetails.getPaymentReceivedBy());
                ps.setDate(9, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setString(10, feeDepositDetails.getPaymentDescription());
                ps.setString(11, feeDepositDetails.getTransactionId());
                ps.setString(12, feeDepositDetails.getComment());
                ps.setString(13, feeDepositDetails.getFddStatus());
              //  ps.setDate(13, new java.sql.Date(feeDepositDetails.getPaymentDate().getTime()));
                java.util.Date paymentDate = feeDepositDetails.getPaymentDate();
                if (paymentDate == null) {
                    throw new IllegalArgumentException("Payment date cannot be null");
                }
                ps.setDate(14, new java.sql.Date(paymentDate.getTime()));
              //  ps.setDouble(14,feeDepositDetails.getAdditionalDiscount());
              //  ps.setString(15,feeDepositDetails.getAdditionalDiscountReason());
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("fd_id")) {
                int generatedId = ((Number) keys.get("fd_id")).intValue();
                feeDepositDetails.setFdId(generatedId);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetails;
    }
   @Override
   public FeeDepositDetails getFeeDepositById(int fdId, String schoolCode) throws Exception {
       String sql = "SELECT  fd.fd_id, fd.school_id, sd.school_name, fd.class_id, mc.class_name, fd.section_id, ms.section_name, fd.student_id, CONCAT(sp.first_name, ' ', sp.last_name) AS student_name, pm.pm_id, pm.payment_type, fd.total_amount_paid, fd.payment_received_by, fd.system_date_time, fd.payment_description, fd.transaction_id, fd.comment FROM  fee_deposit AS fd INNER JOIN school_details AS sd ON fd.school_id = sd.school_id INNER JOIN mst_class AS mc ON fd.class_id = mc.class_id INNER JOIN  mst_section AS ms ON fd.section_id = ms.section_id INNER JOIN student_personal_details AS sp ON fd.student_id = sp.student_id INNER JOIN payment_mode AS pm ON fd.payment_mode = pm.pm_id WHERE fd_id = ? AND sp.deleted is not true";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       FeeDepositDetails feeDepositDetails = null;
       try{
           feeDepositDetails = jdbcTemplate.queryForObject(sql, new Object[]{fdId}, new RowMapper<FeeDepositDetails>() {
               @Override
               public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   FeeDepositDetails fdd = new FeeDepositDetails();
                   fdd.setFdId(rs.getInt("fd_id"));
                   fdd.setSchoolId(rs.getInt("school_id"));
                   fdd.setSchoolName(rs.getString("school_name"));
                   fdd.setClassId(rs.getInt("class_id"));
                   fdd.setClassName(rs.getString("class_name"));
                   fdd.setSectionId(rs.getInt("section_id"));
                   fdd.setSectionName(rs.getString("section_name"));
                   fdd.setStudentId(rs.getInt("student_id"));
                   fdd.setStudentName(rs.getString("student_name"));
                   fdd.setPmId(rs.getInt("pm_id"));
                   fdd.setPaymentType(rs.getString("payment_type"));
                   fdd.setTotalAmountPaid(rs.getInt("total_amount_paid"));
                   fdd.setPaymentReceivedBy(rs.getInt("payment_received_by"));
                   fdd.setSystemDateTime(rs.getDate("system_date_time"));
                   fdd.setPaymentDescription(rs.getString("payment_description"));
                   fdd.setTransactionId(rs.getString("transaction_id"));
                   fdd.setComment(rs.getString("comment"));
                   return fdd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return feeDepositDetails;
   }
   @Override
   public List<FeeDepositDetails> getAllFeeDeposit(String schoolCode) throws Exception {
       String sql = "SELECT  fd.fd_id, fd.school_id, sd.school_name, fd.class_id, mc.class_name, fd.section_id, ms.section_name, fd.student_id, CONCAT(sp.first_name, ' ', sp.last_name) AS student_name, pm.pm_id, pm.payment_type, fd.total_amount_paid, fd.payment_received_by, fd.system_date_time, fd.payment_description, fd.transaction_id, fd.comment FROM  fee_deposit AS fd INNER JOIN school_details AS sd ON fd.school_id = sd.school_id INNER JOIN mst_class AS mc ON fd.class_id = mc.class_id INNER JOIN  mst_section AS ms ON fd.section_id = ms.section_id INNER JOIN student_personal_details AS sp ON fd.student_id = sp.student_id INNER JOIN payment_mode AS pm ON fd.payment_mode = pm.pm_id where sp.deleted is not true order by fd_id asc ";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<FeeDepositDetails> feeDepositDetails = null;
       try{
           feeDepositDetails = jdbcTemplate.query(sql, new RowMapper<FeeDepositDetails>() {
               @Override
               public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   FeeDepositDetails fdd = new FeeDepositDetails();
                   fdd.setFdId(rs.getInt("fd_id"));
                   fdd.setSchoolId(rs.getInt("school_id"));
                   fdd.setSchoolName(rs.getString("school_name"));
                   fdd.setClassId(rs.getInt("class_id"));
                   fdd.setClassName(rs.getString("class_name"));
                   fdd.setSectionId(rs.getInt("section_id"));
                   fdd.setSectionName(rs.getString("section_name"));
                   fdd.setStudentId(rs.getInt("student_id"));
                   fdd.setStudentName(rs.getString("student_name"));
                   fdd.setPmId(rs.getInt("pm_id"));
                   fdd.setPaymentType(rs.getString("payment_type"));
                   fdd.setTotalAmountPaid(rs.getInt("total_amount_paid"));
                   fdd.setPaymentReceivedBy(rs.getInt("payment_received_by"));
                   fdd.setSystemDateTime(rs.getDate("system_date_time"));
                   fdd.setPaymentDescription(rs.getString("payment_description"));
                   fdd.setTransactionId(rs.getString("transaction_id"));
                   fdd.setComment(rs.getString("comment"));
                   return fdd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return feeDepositDetails;
   }
   @Override
   public FeeDepositDetails updateFeeDepositById(FeeDepositDetails feeDepositDetails, int fdId, String schoolCode) throws Exception {
       String sql = "update fee_deposit set session_id = ?, class_id = ?, section_id = ?, student_id = ?, payment_mode = ?, total_amount_paid = ?, payment_received_by = ?, system_date_time = ?, payment_description = ?, transaction_id = ?, comment = ? where fd_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql,
                   feeDepositDetails.getSessionId(),
                   feeDepositDetails.getClassId(),
                   feeDepositDetails.getSectionId(),
                   feeDepositDetails.getStudentId(),
                   feeDepositDetails.getPaymentMode(),
                   feeDepositDetails.getTotalAmountPaid(),
                   feeDepositDetails.getPaymentReceivedBy(),
                   feeDepositDetails.getSystemDateTime(),
                   feeDepositDetails.getPaymentDescription(),
                   feeDepositDetails.getTransactionId(),
                   feeDepositDetails.getComment(),
                   feeDepositDetails.getFdId());
           if(rowAffected > 0){
               return feeDepositDetails;
           }
           return null;
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }
    @Override
    public double getTotalFeeDeposit(String schoolCode) throws Exception {
        String sql = "SELECT SUM(total_amount_paid) AS total_amount_paid_sum FROM fee_deposit";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            Double result = jdbcTemplate.queryForObject(sql, Double.class);
            if(result == null){
                return 0.0;
            }
            return result;
        }catch (Exception e){
            throw new Exception("Error fetching total fee deposit: " + e.getMessage(), e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }
    @Override
    public List<FeeDepositDetails> getTotalAmountPaidByStudents(String schoolCode) throws Exception {
        String sql = "select\n" +
                "    spd.student_id,\n" +
                "    spd.first_name || ' ' || spd.last_name AS student_name,\n" +
                "    mc.class_id,\n" +
                "    mc.class_name,\n" +
                "    ms.section_id,\n" +
                "    ms.section_name,\n" +
                "    SUM(fd.total_amount_paid) AS total_amount_paid_by_student\n" +
                "FROM\n" +
                "    fee_deposit fd\n" +
                "INNER JOIN\n" +
                "    student_personal_details spd ON fd.student_id = spd.student_id\n" +
                "INNER JOIN\n" +
                "    student_academic_details sad ON fd.student_id = sad.student_id\n" +
                "INNER JOIN\n" +
                "    mst_class mc ON sad.student_class_id = mc.class_id\n" +
                "INNER JOIN\n" +
                "    mst_section ms ON sad.student_section_id = ms.section_id\n" +
                "WHERE spd.deleted is not true\n" +
                "GROUP by\n" +
                "    spd.student_id ,\n" +
                "    spd.first_name,\n" +
                "    spd.last_name,\n" +
                "    sad.registration_number,\n" +
                "    mc.class_id,\n" +
                "    mc.class_name,\n" +
                "    ms.section_id,\n" +
                "    ms.section_name";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeDepositDetails> feeDepositDetails = null;
        try{
            feeDepositDetails = jdbcTemplate.query(sql, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fdd = new FeeDepositDetails();
                    fdd.setStudentId(rs.getInt("student_id"));
                    fdd.setStudentName(rs.getString("student_name"));
                    fdd.setClassId(rs.getInt("class_id"));
                    fdd.setClassName(rs.getString("class_name"));
                    fdd.setSectionId(rs.getInt("section_id"));
                    fdd.setSectionName(rs.getString("section_name"));
                    fdd.setTotalAmountPaidByStudent(rs.getDouble("total_amount_paid_by_student"));
                    return fdd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetails;
    }
   @Override
   public FeeDepositDetails getTotalAmountPaidByParticularStudent(int studentId, String schoolCode) {
      /* String sql = "select\n" +
               "    spd.student_id,\n" +
               "    spd.first_name || ' ' || spd.last_name AS student_name,\n" +
               "    mc.class_id,\n" +
               "    mc.class_name,\n" +
               "    ms.section_id,\n" +
               "    ms.section_name,\n" +
               "    SUM(fd.total_amount_paid) AS total_amount_paid_by_student\n" +
               "FROM\n" +
               "    fee_deposit fd\n" +
               "INNER JOIN\n" +
               "    student_personal_details spd ON fd.student_id = spd.student_id\n" +
               "INNER JOIN\n" +
               "    student_academic_details sad ON fd.student_id = sad.student_id\n" +
               "INNER JOIN\n" +
               "    mst_class mc ON sad.student_class_id = mc.class_id\n" +
               "INNER JOIN\n" +
               "    mst_section ms ON sad.student_section_id = ms.section_id\n" +
               "WHERE spd.deleted is not true\n" +
               "AND spd.student_id = ?\n" +
               "GROUP by\n" +
               "    spd.student_id ,\n" +
               "    spd.first_name,\n" +
               "    spd.last_name,\n" +
               "    sad.registration_number,\n" +
               "    mc.class_id,\n" +
               "    mc.class_name,\n" +
               "    ms.section_id,\n" +
               "    ms.section_name";*/
       String sql = "SELECT\n" +
               "    spd.student_id,\n" +
               "    spd.first_name || ' ' || spd.last_name AS student_name,\n" +
               "    mc.class_id,\n" +
               "    mc.class_name,\n" +
               "    ms.section_id,\n" +
               "    ms.section_name,\n" +
               "    SUM(fd.total_amount_paid) AS total_amount_paid_by_student,\n" +
               "    array_agg('Deposit Amount Details : ' || to_char(fd.system_date_time, 'YYYY-MM-DD') || ' : ' || fd.total_amount_paid || ' : ' || fd.comment ORDER BY fd.fd_id) AS total_amount_paid_list -- Aggregate as array\n" +
               "FROM\n" +
               "    fee_deposit fd\n" +
               "INNER JOIN\n" +
               "    student_personal_details spd ON fd.student_id = spd.student_id\n" +
               "INNER JOIN\n" +
               "    student_academic_details sad ON fd.student_id = sad.student_id\n" +
               "INNER JOIN\n" +
               "    mst_class mc ON sad.student_class_id = mc.class_id\n" +
               "INNER JOIN\n" +
               "    mst_section ms ON sad.student_section_id = ms.section_id\n" +
               "WHERE\n" +
               "    spd.deleted IS NOT TRUE\n" +
               "    AND spd.student_id = ?\n" +
               "GROUP BY\n" +
               "    spd.student_id,\n" +
               "    spd.first_name,\n" +
               "    spd.last_name,\n" +
               "    sad.registration_number,\n" +
               "    mc.class_id,\n" +
               "    mc.class_name,\n" +
               "    ms.section_id,\n" +
               "    ms.section_name";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try {
           return jdbcTemplate.queryForObject(sql, new Object[]{studentId}, new RowMapper<FeeDepositDetails>() {
               @Override
               public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   FeeDepositDetails fdd = new FeeDepositDetails();
                   fdd.setStudentId(rs.getInt("student_id"));
                   fdd.setStudentName(rs.getString("student_name"));
                   fdd.setClassId(rs.getInt("class_id"));
                   fdd.setClassName(rs.getString("class_name"));
                   fdd.setSectionId(rs.getInt("section_id"));
                   fdd.setSectionName(rs.getString("section_name"));
                   fdd.setTotalAmountPaidByStudent(rs.getDouble("total_amount_paid_by_student"));
                   fdd.setTotalAmountPaidList(Arrays.asList((String[])rs.getArray("total_amount_paid_list").getArray()));
                   return fdd;
               }
           });
       } catch (EmptyResultDataAccessException e) {
           return null; // No data found
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }
   @Override
   public List<FeeDepositDetails> getYearlyTotalDeposit(int sessionId, String schoolCode) throws Exception {
       /*String sql = "SELECT\n" +
               "    s.academic_session AS session,\n" +
               "    SUM(fd.total_amount_paid) AS session_wise_amount_deposit\n" +
               "FROM\n" +
               "    fee_deposit fd\n" +
               "JOIN\n" +
               "    session s ON fd.session_id = s.session_id\n" +
               "JOIN\n" +
               "    student_personal_details spd ON fd.student_id = spd.student_id\n" +
               "WHERE\n" +
               "    spd.deleted IS NOT TRUE\n" +
               "GROUP BY\n" +
               "    s.academic_session\n" +
               "ORDER BY\n" +
               "    s.academic_session";*/
       String sql = """
               SELECT
                   s.academic_session AS session,
                   SUM(fd.total_amount_paid) AS session_wise_amount_deposit
                       FROM
                           fee_deposit fd
                       JOIN
                           session s ON fd.session_id = s.session_id
                       JOIN
                           student_personal_details spd ON fd.student_id = spd.student_id
                       WHERE
                           spd.deleted IS NOT TRUE
                           AND s.session_id = ?
                       GROUP BY
                           s.academic_session
                       ORDER BY
                           s.academic_session
               """;
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<FeeDepositDetails> feeDepositDetails = null;
       try{
           feeDepositDetails = jdbcTemplate.query(sql, new Object[]{sessionId}, new RowMapper<FeeDepositDetails>() {
               @Override
               public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   FeeDepositDetails fdd = new FeeDepositDetails();
                   fdd.setYear(rs.getString("session"));
                   fdd.setTotalYearlyAmountDeposit(rs.getDouble("session_wise_amount_deposit"));
                   return fdd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return feeDepositDetails;
   }

    @Override
    public List<FeeDepositDetails> getUnsettledFeesByClassSectionSessionAndStudentId(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    fd.fd_id,
                    fd.school_id,
                    fd.session_id,
                    s.academic_session,
                    fd.class_id,
                    mc.class_name,
                    fd.section_id,
                    ms.section_name,
                    fd.student_id,
                    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
                    fd.payment_mode,
                    pm.payment_type,
                    fd.total_amount_paid,
                    fd.system_date_time,
                    fd.fdd_status
                FROM
                    fee_deposit fd
                JOIN
                    student_personal_details spd ON spd.student_id = fd.student_id
                JOIN
                    mst_class mc ON mc.class_id = fd.class_id
                JOIN
                    mst_section ms ON ms.section_id = fd.section_id
                JOIN
                    session s ON s.session_id = fd.session_id
                JOIN
                    payment_mode pm ON pm.pm_id = fd.payment_mode
                WHERE
                    fd.session_id = ?
                    AND fd.class_id = ?
                    AND fd.section_id = ?
                    AND fd.student_id = ?
                    AND (fd.fdd_status IS NULL OR fd.fdd_status NOT ILIKE 'settled')
                    AND spd.deleted IS NOT true
                                                   
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeDepositDetails> feeDepositDetails = null;

        try{
            feeDepositDetails = jdbcTemplate.query(sql, new Object[]{sessionId, classId, sectionId, studentId}, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fd = new FeeDepositDetails();
                    fd.setFdId(rs.getInt("fd_id"));
                    fd.setSchoolId(rs.getInt("school_id"));
                    fd.setSessionId(rs.getInt("session_id"));
                    fd.setSessionName(rs.getString("academic_session"));
                    fd.setClassId(rs.getInt("class_id"));
                    fd.setClassName(rs.getString("class_name"));
                    fd.setSectionId(rs.getInt("section_id"));
                    fd.setSectionName(rs.getString("section_name"));
                    fd.setStudentId(rs.getInt("student_id"));
                    fd.setStudentName(rs.getString("student_name"));
                    fd.setPaymentMode(rs.getInt("payment_mode"));
                    fd.setPaymentType(rs.getString("payment_type"));
                    fd.setTotalAmountPaid(rs.getInt("total_amount_paid"));
                    fd.setSystemDateTime(rs.getDate("system_date_time"));
                    fd.setFddStatus(rs.getString("fdd_status"));
                    return fd;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetails;
    }

    @Override
    public FeeDepositDetails updateStatus(FeeDepositDetails feeDepositDetails, int fdId, String schoolCode) throws Exception {
        String sql = "UPDATE fee_deposit SET fdd_status = ? WHERE fd_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    feeDepositDetails.getFddStatus(),
                    feeDepositDetails.getFdId());
            if(rowAffected > 0){
                return feeDepositDetails;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
