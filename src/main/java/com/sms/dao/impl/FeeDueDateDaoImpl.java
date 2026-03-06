package com.sms.dao.impl;

import com.sms.dao.FeeDueDateDao;
import com.sms.model.FeeDueDateDetails;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FeeDueDateDaoImpl implements FeeDueDateDao {
    private final JdbcTemplate jdbcTemplate;

    public FeeDueDateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FeeDueDateDetails addFeeDueDetails(FeeDueDateDetails feeDueDateDetails, String schoolCode) throws Exception {
        String sql = "insert into fee_due_date (school_id, fa_id, due_date, fee_amount, discount_amount, updated_by, update_date_time) values(?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, feeDueDateDetails.getSchoolId());
                ps.setInt(2, feeDueDateDetails.getFaId());
                ps.setDate(3, new java.sql.Date(feeDueDateDetails.getDueDate().getTime()));
                ps.setInt(4, feeDueDateDetails.getFeeAmount());
                //get the round figure
                int roundedDiscountAmount = (int) Math.round(feeDueDateDetails.getDiscountAmount());
                ps.setDouble(5, roundedDiscountAmount);
                ps.setInt(6, feeDueDateDetails.getUpdatedBy());
                ps.setDate(7, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                return  ps;
            }, keyHolder);
            Map<String,Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("fddt_id")){
                int generatedId = ((Number) keys.get("fddt_id")).intValue();
                feeDueDateDetails.setFddtId(generatedId);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDueDateDetails;
    }
   @Override
   public List<FeeDueDateDetails> addFeeDueDetailsList(List<FeeDueDateDetails> feeDueDateDetailsList, String schoolCode) throws Exception {
       String sql = "insert into fee_due_date (school_id, fa_id, due_date, fee_amount, discount_amount, updated_by, update_date_time) values(?,?,?,?,?,?,?)";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<FeeDueDateDetails> updatedList  = new ArrayList<>();
       try{
           for(FeeDueDateDetails feeDueDateDetails : feeDueDateDetailsList){
               KeyHolder keyHolder = new GeneratedKeyHolder();
               try{
                   jdbcTemplate.update(connection -> {
                       PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                       ps.setInt(1, feeDueDateDetails.getSchoolId());
                       ps.setInt(2, feeDueDateDetails.getFaId());
                       ps.setDate(3, new java.sql.Date(feeDueDateDetails.getDueDate().getTime()));
                       ps.setInt(4, feeDueDateDetails.getFeeAmount());
                       ps.setDouble(5, feeDueDateDetails.getDiscountAmount());
                       ps.setInt(6, feeDueDateDetails.getUpdatedBy());
                       ps.setDate(7, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                       return  ps;
                   }, keyHolder);
                   Map<String,Object> keys = keyHolder.getKeys();
                   if(keys != null && keys.containsKey("fddt_id")){
                       int generatedId = ((Number) keys.get("fddt_id")).intValue();
                       feeDueDateDetails.setFddtId(generatedId);
                   }
                   updatedList.add(feeDueDateDetails);
               }catch(Exception e){
                   e.printStackTrace();
               }
           }
       }catch (Exception e){
           // Handle exception
           e.printStackTrace();
           //throw new Exception("Error adding fee due details: " + e.getMessage(), e);
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return updatedList;
   }
    @Override
    public List<FeeDueDateDetails> getExactFee(int sessionId, String schoolCode) throws Exception {
        /*String sql = "WITH fee_details AS (\n" +
                "    SELECT\n" +
                "        s.academic_session AS session,\n" +
                "        COALESCE(fdd.fee_amount, 0) AS fee_amount,\n" +
                "        COALESCE(fdd.discount_amount, 0) AS discount\n" +
                "    FROM\n" +
                "        student_personal_details spd\n" +
                "    JOIN\n" +
                "        student_academic_details sad ON spd.student_id = sad.student_id AND spd.school_id = sad.school_id\n" +
                "    JOIN\n" +
                "        mst_class mc ON sad.student_class_id = mc.class_id\n" +
                "    JOIN\n" +
                "        mst_section ms ON sad.student_section_id = ms.section_id\n" +
                "    JOIN\n" +
                "        school_details sd ON spd.school_id = sd.school_id\n" +
                "    JOIN\n" +
                "        fee_assignment fa ON fa.school_id = spd.school_id\n" +
                "        AND (\n" +
                "            (fa.class_id = mc.class_id AND fa.section_id = 0 AND fa.student_id = 0)\n" +
                "            OR (fa.class_id = mc.class_id AND fa.section_id = ms.section_id AND fa.student_id = 0)\n" +
                "            OR (fa.student_id = spd.student_id)\n" +
                "        )\n" +
                "    JOIN\n" +
                "        fee_due_date fdd ON fa.fa_id = fdd.fa_id\n" +
                "    JOIN\n" +
                "        session s ON fa.session_id = s.session_id\n" +
                "    WHERE\n" +
                "        spd.deleted IS NOT TRUE\n" +
                "        AND fa.session_id = sad.session_id -- Ensure the session matches\n" +
                "),\n" +
                "session_gross_fee AS (\n" +
                "    SELECT\n" +
                "        session,\n" +
                "        SUM(fee_amount) AS total_fee,\n" +
                "        SUM(discount) AS total_discount,\n" +
                "        SUM(fee_amount) - SUM(discount) AS exact_fee\n" +
                "    FROM\n" +
                "        fee_details\n" +
                "    GROUP BY\n" +
                "        session\n" +
                ")\n" +
                "SELECT\n" +
                "    sgf.session,\n" +
                "    sgf.total_fee,\n" +
                "    sgf.total_discount,\n" +
                "    sgf.exact_fee\n" +
                "FROM\n" +
                "    session_gross_fee sgf\n" +
                "ORDER BY\n" +
                "    sgf.session";*/
        String sql = """
                             WITH fee_deposit_aggregation AS (
                                SELECT
                                    SUM(penalty_amount) AS total_penalty
                                FROM fee_deposit_details
                            ),
                            fee_details AS (
                                SELECT
                                    s.academic_session AS session,
                                    COALESCE(fddt.fee_amount, 0) AS fee_amount,
                                    COALESCE(fddt.discount_amount, 0) + COALESCE(
                                        (
                                            SELECT SUM(additional_discount)
                                            FROM fee_deposit_details fdd
                                            WHERE fdd.fa_id = fa.fa_id AND fdd.fddt_id = fddt.fddt_id
                                        ), 0
                                    ) AS discount
                                FROM student_personal_details spd
                                JOIN student_academic_details sad\s
                                    ON spd.student_id = sad.student_id\s
                                    AND spd.school_id = sad.school_id
                                JOIN mst_class mc\s
                                    ON sad.student_class_id = mc.class_id
                                JOIN mst_section ms\s
                                    ON sad.student_section_id = ms.section_id
                                JOIN fee_assignment fa\s
                                    ON fa.school_id = spd.school_id
                                    AND (
                                        (fa.class_id = mc.class_id AND fa.section_id IS NULL AND fa.student_id IS NULL)
                                        OR (fa.class_id = mc.class_id AND fa.section_id = ms.section_id AND fa.student_id IS NULL)
                                        OR (fa.student_id = spd.student_id)
                                    )
                                    AND NOT EXISTS (
                                                SELECT 1
                                                FROM fee_assignment_exclusion fae
                                                WHERE fae.fa_id = fa.fa_id
                                                  AND fae.student_id = spd.student_id
                                                  AND (fae.valid_to IS NULL OR fae.valid_to >= CURRENT_DATE)
                                                  AND fae.valid_from <= CURRENT_DATE
                                           )
                                JOIN fee_due_date fddt\s
                                    ON fa.fa_id = fddt.fa_id
                                JOIN session s\s
                                    ON fa.session_id = s.session_id
                                WHERE
                                    spd.deleted IS NOT TRUE
                                    AND fa.session_id = sad.session_id
                                    AND fa.session_id = ?
                            ),
                            session_summary AS (
                                SELECT
                                    session,
                                    SUM(fee_amount) AS total_fee,
                                    SUM(discount) AS total_discount,
                                    SUM(fee_amount) - SUM(discount) AS fee_after_discount
                                FROM fee_details
                                GROUP BY session
                            ),
                            total_deposit AS (
                                SELECT
                                    SUM(total_amount_paid) AS total_amount_deposited
                                FROM fee_deposit
                                WHERE session_id = ?
                            ),
                            today_collection AS (
                                SELECT
                                    SUM(total_amount_paid) AS today_collection
                                FROM fee_deposit
                                WHERE DATE(system_date_time) = CURRENT_DATE
                                  AND session_id = ?
                            )
                            SELECT
                                ss.session,
                                ss.total_fee,
                                ss.total_discount,
                                fda.total_penalty,
                                ss.fee_after_discount,
                                ss.fee_after_discount + fda.total_penalty AS exact_fee_after_discount_and_penalty,
                                ss.total_fee + fda.total_penalty - ss.total_discount - COALESCE(td.total_amount_deposited, 0) AS total_due,
                                COALESCE(tc.today_collection, 0) AS today_collection
                            FROM session_summary ss
                            CROSS JOIN fee_deposit_aggregation fda
                            CROSS JOIN total_deposit td
                            CROSS JOIN today_collection tc
                            ORDER BY ss.session;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeDueDateDetails> feeDueDateDetails = null;
        try{
            feeDueDateDetails = jdbcTemplate.query(sql, new Object[]{sessionId,sessionId,sessionId}, new RowMapper<FeeDueDateDetails>() {
                @Override
                public FeeDueDateDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDueDateDetails fddd = new FeeDueDateDetails();
                    // fddd.setYear(rs.getString("session"));
                    fddd.setYear(rs.getString("session"));
                    fddd.setTotalFee(rs.getDouble("total_fee"));
                    fddd.setTotalDiscount(rs.getDouble("total_discount"));
                    fddd.setTotalPenalty(rs.getDouble("total_penalty"));
                    fddd.setFeeAfterDiscount(rs.getDouble("fee_after_discount"));
                    fddd.setExactFeeAfterDiscountAndPenalty(rs.getDouble("exact_fee_after_discount_and_penalty"));
                    fddd.setTodayCollection(rs.getDouble("today_collection"));
                    return fddd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDueDateDetails;
    }
    public List<Integer> getFddtIdsForFaId(int faId, String schoolCode) throws Exception {
        String sql = " SELECT fddt_id FROM fee_due_date WHERE fa_id = ? ORDER BY fddt_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForList(sql, Integer.class, faId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error fetching fddt_ids: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
   @Override
   public void updateFeeDueDate(List<FeeDueDateDetails> feeDueDateDetailsList, int faId, String schoolCode) throws Exception {
       // Fetch unique identifiers (e.g., fddtId)
       List<Integer> fddtIds = getFddtIdsForFaId(faId, schoolCode);

       // Simple check to ensure that we have enough identifiers
       if (fddtIds.size() != feeDueDateDetailsList.size()) {
           throw new Exception("Number of identifiers does not match the number of records to update.");
       }

       // Update records
       String sql = "UPDATE fee_due_date SET fee_amount = ?, discount_amount = ? WHERE fddt_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try {
           for (int i = 0; i < fddtIds.size(); i++) {
               Integer fddtId = fddtIds.get(i);
               FeeDueDateDetails details = feeDueDateDetailsList.get(i);
               jdbcTemplate.update(sql,
                       details.getFeeAmount(),
                       details.getDiscountAmount(),
                       fddtId
               );
           }
       } catch (Exception e) {
           e.printStackTrace();
           throw new Exception("Error updating fee details: " + e.getMessage(), e);
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }
}
