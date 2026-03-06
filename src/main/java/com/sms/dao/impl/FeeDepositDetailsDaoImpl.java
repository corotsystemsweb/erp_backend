package com.sms.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.FeeDepositDetailsDao;
import com.sms.model.FeeDepositDetails;
import com.sms.model.FeeDetail;
import com.sms.model.ParentDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class FeeDepositDetailsDaoImpl implements FeeDepositDetailsDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
   /* @Override
    public List<FeeDepositDetails> addFeeDepositDetails(List<FeeDepositDetails> feeDepositDetails, String schoolCode) throws Exception {
        String sql = "insert into fee_deposit_details (fd_id, fa_id, fddt_id, amount_paid, dc_id, discount_amount, penalty_id, penalty_amount, balance, approved_by, payment_received_by, system_date_time, status, comment) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    FeeDepositDetails ddd = feeDepositDetails.get(i);
                    ps.setInt(1, ddd.getFdId());
                    ps.setInt(2, ddd.getFaId());
                    ps.setInt(3, ddd.getFddtId());
                    // Round amountPaid
                    int roundedAmountPaid = (int) Math.round(ddd.getAmountPaid());
                    ps.setDouble(4, roundedAmountPaid);
                    ps.setInt(5, ddd.getDcId());
                    // Round discountAmount
                    int roundedDiscountAmount = (int) Math.round(ddd.getDiscountAmount());
                    ps.setDouble(6, roundedDiscountAmount);
                    ps.setInt(7, ddd.getPenaltyId());
                    // Round penaltyAmount
                    int roundedPenaltyAmount = (int) Math.round(ddd.getPenaltyAmount());
                    ps.setDouble(8, roundedPenaltyAmount);
                    // Round balance
                    int roundedBalance = (int) Math.round(ddd.getBalance());
                    ps.setDouble(9, roundedBalance);
                    ps.setInt(10, ddd.getApprovedBy());
                    ps.setInt(11, ddd.getPaymentReceivedBy());
                    ps.setDate(12, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                    ps.setString(13, ddd.getStatus());
                    ps.setString(14, ddd.getComment());
                }

                @Override
                public int getBatchSize() {
                    return feeDepositDetails.size();
                }
            });
        }catch (Exception e){
            throw new Exception("Error while adding fee deposit details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetails;
    }
*/
    @Override
    public List<FeeDepositDetails> addFeeDepositDetails(List<FeeDepositDetails> feeDepositDetails, String schoolCode) throws Exception {
        String sql = "insert into fee_deposit_details (fd_id, fa_id, fddt_id, amount_paid, dc_id, discount_amount, penalty_id, penalty_amount, balance, approved_by, payment_received_by, system_date_time, status, comment,additional_discount,additional_discount_reason) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    FeeDepositDetails ddd = feeDepositDetails.get(i);
                    ps.setInt(1, ddd.getFdId());
                    ps.setInt(2, ddd.getFaId());
                    ps.setInt(3, ddd.getFddtId());
                    ps.setDouble(4, ddd.getAmountPaid());
                    ps.setInt(5, ddd.getDcId());
                    ps.setDouble(6, ddd.getDiscountAmount());
                    ps.setInt(7, ddd.getPenaltyId());
                    ps.setDouble(8, ddd.getPenaltyAmount());
                    ps.setDouble(9, ddd.getBalance());
                    ps.setInt(10, ddd.getApprovedBy());
                    ps.setInt(11, ddd.getPaymentReceivedBy());
                    ps.setDate(12, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                    ps.setString(13, ddd.getStatus());
                    ps.setString(14, ddd.getComment());
                    ps.setDouble(15,ddd.getAdditionalDiscount());
                    ps.setString(16,ddd.getAdditionalDiscountReason());
                }

                @Override
                public int getBatchSize() {
                    return feeDepositDetails.size();
                }
            });
        }catch (Exception e){
            throw new Exception("Error while adding fee deposit details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetails;
    }

    @Override
    public List<FeeDepositDetails> getStudentFeeDetailsBasedOnClassSectionSession(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        String sql = """
                WITH FeeDetailsIntermediate AS (
                    SELECT
                        spd.student_id,
                        fs.fee_type,
                        fdd.fee_amount,
                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS fee_count,
                        SUM(fdd.discount_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS total_discount_amount,
                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) * fdd.fee_amount AS total_fee_for_type
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN school_fees fs ON fa.fee_id = fs.fee_id
                    JOIN student_academic_details sad
                        ON (fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0))
                    JOIN student_personal_details spd
                        ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND sad.student_section_id = ?
                        AND spd.deleted IS NOT TRUE
                ),
                FeeDetails AS (
                    SELECT
                        student_id,
                        ARRAY_AGG(
                            DISTINCT CONCAT(
                                'Fee Type: ', fee_type,
                                ', Fee Amount: ',
                                CASE
                                    WHEN fee_count > 1 THEN CONCAT(fee_count, '*', fee_amount::text, ' = ', total_fee_for_type::text)
                                    ELSE total_fee_for_type::text
                                END,
                                ', Total Discount Amount: ', total_discount_amount::text
                            )
                        ) AS fee_details,
                        SUM(total_fee_for_type) AS total_fee_amount,
                        SUM(total_discount_amount) AS total_discount_amount
                    FROM FeeDetailsIntermediate
                    GROUP BY student_id
                ),
                FeeTotals AS (
                    SELECT
                        spd.student_id,
                        SUM(fdd.fee_amount) AS total_fee_assigned,
                        SUM(fdd.discount_amount) AS total_discount_amount
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN student_academic_details sad
                        ON (fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0))
                    JOIN student_personal_details spd
                        ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND sad.student_section_id = ?
                        AND spd.deleted IS NOT TRUE
                    GROUP BY spd.student_id
                ),
                PenaltyDetails AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fdd.penalty_amount), 0.0) AS total_penalty
                    FROM
                        fee_deposit_details fdd
                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
                    WHERE fd.session_id = ? AND fd.class_id = ? AND fd.section_id = ?
                    GROUP BY fd.student_id
                ),
                PaidDetails AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fd.total_amount_paid), 0.0) AS total_paid
                    FROM fee_deposit fd
                    WHERE fd.session_id = ? AND fd.class_id = ? AND fd.section_id = ?
                    GROUP BY fd.student_id
                )
                SELECT
                    sd.school_id,
                    sd.school_name,
                    sd.school_building,
                    sd.school_address,
                    sd.email_address,
                    sd.phone_number,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.school_zipcode,
                    spd.student_id,
                    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
                    sad.admission_no ,
                    sad.registration_number ,
                    mc.class_id,
                    mc.class_name,
                    ms.section_id,
                    ms.section_name,
                    ses.session_id,
                    ses.academic_session,
                    COALESCE(
                        JSON_AGG(
                            JSON_BUILD_OBJECT(
                                'parentId', pds.parent_id,
                                'firstName', pds.first_name,
                                'lastName', pds.last_name,
                                'phoneNumber', pds.phone_number,
                                'emergencyPhoneNumber', pds.emergency_phone_number,
                                'whatsappNumber', pds.whatsapp_no,
                                'parentType', pds.parent_type
                            )
                        ) FILTER (WHERE pds.parent_id IS NOT NULL AND pds.deleted IS NOT TRUE), '[]'
                    ) AS parent_details,
                    fd.fee_details AS fee_details, -- Using formatted fee_details from CTE
                    ft.total_fee_assigned AS total_fee_assigned, -- Retained original total fee assigned
                    ft.total_discount_amount AS total_discount, -- Retained original total discount
                    ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0) AS gross_student_fee,
                    COALESCE(pd.total_penalty, 0.0) AS total_penalty,
                    COALESCE(pdp.total_paid, 0.0) AS total_paid,
                    (ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0))
                        - COALESCE(pdp.total_paid, 0) AS total_due
                FROM
                    student_personal_details spd
                JOIN student_academic_details sad ON spd.student_id = sad.student_id
                JOIN mst_class mc ON sad.student_class_id = mc.class_id
                JOIN mst_section ms ON sad.student_section_id = ms.section_id
                JOIN school_details sd ON spd.school_id = sd.school_id
                JOIN session ses ON sad.session_id = ses.session_id
                LEFT JOIN FeeDetails fd ON spd.student_id = fd.student_id
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id -- Original totals
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN parent_details pds ON pds.parent_id = ANY(spd.parent_id)
                WHERE
                    sad.session_id = ?
                    AND sad.student_class_id = ?
                    AND sad.student_section_id = ?
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name,sad.admission_no,sad.registration_number, mc.class_id, mc.class_name, ms.section_id,
                    ms.section_name, ses.session_id, ses.academic_session, fd.fee_details,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid
                ORDER BY spd.student_id
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<FeeDepositDetails> feeDepositDetailsList = null;

        try{
            feeDepositDetailsList = jdbcTemplate.query(sql, new Object[]{sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId}, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fdd = new FeeDepositDetails();
                    fdd.setSchoolId(rs.getInt("school_id"));
                    fdd.setSchoolName(rs.getString("school_name"));
                    fdd.setSchoolBuilding(rs.getString("school_building"));
                    fdd.setSchoolAddress(rs.getString("school_address"));
                    fdd.setSchoolEmailAddress(rs.getString("email_address"));
                    fdd.setSchoolPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    fdd.setSchoolCity(rs.getString("school_city"));
                    fdd.setSchoolState(rs.getString("school_state"));
                    fdd.setSchoolCountry(rs.getString("school_country"));
                    fdd.setSchoolZipCode(rs.getString("school_zipcode"));
                    fdd.setStudentId(rs.getInt("student_id"));
                    fdd.setStudentName(rs.getString("student_name"));
                    fdd.setAdmissionNumber(rs.getString("admission_no"));
                    fdd.setRegistrationNumber(rs.getString("registration_number"));
                    fdd.setClassId(rs.getInt("class_id"));
                    fdd.setClassName(rs.getString("class_name"));
                    fdd.setSectionId(rs.getInt("section_id"));
                    fdd.setSectionName(rs.getString("section_name"));
                    fdd.setSessionId(rs.getInt("session_id"));
                    fdd.setSessionName(rs.getString("academic_session"));
                    // Mapping parent_details
                    String parentDetailsJson = rs.getString("parent_details");
                    if (parentDetailsJson != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});

                            // Decrypt sensitive fields
                            for (ParentDetails parent : parentDetailsList) {
                                if (parent.getPhoneNumber() != null) {
                                    parent.setPhoneNumber(CipherUtils.decrypt(parent.getPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                            }

                            fdd.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            fdd.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        fdd.setParentDetails(null);
                    }
                    Array feeDetailsArray = rs.getArray("fee_details");
                    if(feeDetailsArray != null) {
                        fdd.setFeeDetails(Arrays.asList((String[]) rs.getArray("fee_details").getArray()));
                    }else{
                        fdd.setFeeDetails(Collections.emptyList());
                    }
                    fdd.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                    fdd.setTotalDiscount(rs.getDouble("total_discount"));
                    fdd.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                    fdd.setTotalPenalty(rs.getDouble("total_penalty"));
                    fdd.setTotalPaid(rs.getDouble("total_paid"));
                    fdd.setTotalDue(rs.getDouble("total_due"));
                    return fdd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetailsList;
    }

    @Override
    public List<FeeDepositDetails> getAllStudentFeeDetails(int sessionId, String schoolCode) throws Exception {
        String sql = """
                WITH FeeDetailsIntermediate AS (
                    SELECT
                        spd.student_id,
                        fs.fee_type,
                        fdd.fee_amount,
                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS fee_count,
                        SUM(fdd.discount_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS total_discount_amount,
                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) * fdd.fee_amount AS total_fee_for_type
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN school_fees fs ON fa.fee_id = fs.fee_id
                    JOIN student_academic_details sad
                        ON (fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0))
                    JOIN student_personal_details spd
                        ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND spd.deleted IS NOT TRUE
                ),
                FeeDetails AS (
                    SELECT
                        student_id,
                        ARRAY_AGG(
                            DISTINCT CONCAT(
                                'Fee Type: ', fee_type,
                                ', Fee Amount: ',
                                CASE
                                    WHEN fee_count > 1 THEN CONCAT(fee_count, '*', fee_amount::text, ' = ', total_fee_for_type::text)
                                    ELSE total_fee_for_type::text
                                END,
                                ', Total Discount Amount: ', total_discount_amount::text
                            )
                        ) AS fee_details,
                        SUM(total_fee_for_type) AS total_fee_amount,
                        SUM(total_discount_amount) AS total_discount_amount
                    FROM FeeDetailsIntermediate
                    GROUP BY student_id
                ),
                FeeTotals AS (
                    SELECT
                        spd.student_id,
                        SUM(fdd.fee_amount) AS total_fee_assigned,
                        SUM(fdd.discount_amount) AS total_discount_amount
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN student_academic_details sad
                        ON (fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0))
                    JOIN student_personal_details spd
                        ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND spd.deleted IS NOT TRUE
                    GROUP BY spd.student_id
                ),
                PenaltyDetails AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fdd.penalty_amount), 0.0) AS total_penalty
                    FROM
                        fee_deposit_details fdd
                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
                    WHERE fd.session_id = ?
                    GROUP BY fd.student_id
                ),
                PaidDetails AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fd.total_amount_paid), 0.0) AS total_paid
                    FROM fee_deposit fd
                    WHERE fd.session_id = ?
                    GROUP BY fd.student_id
                )
                SELECT
                    sd.school_id,
                    sd.school_name,
                    sd.school_building,
                    sd.school_address,
                    sd.email_address,
                    sd.phone_number,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.school_zipcode,
                    spd.student_id,
                    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
                    sad.admission_no ,
                    sad.registration_number ,
                    mc.class_id,
                    mc.class_name,
                    ms.section_id,
                    ms.section_name,
                    ses.session_id,
                    ses.academic_session,
                    COALESCE(
                        JSON_AGG(
                            JSON_BUILD_OBJECT(
                                'parentId', pds.parent_id,
                                'firstName', pds.first_name,
                                'lastName', pds.last_name,
                                'phoneNumber', pds.phone_number,
                                'emergencyPhoneNumber', pds.emergency_phone_number,
                                'whatsappNumber', pds.whatsapp_no,
                                'parentType', pds.parent_type
                            )
                        ) FILTER (WHERE pds.parent_id IS NOT NULL AND pds.deleted IS NOT TRUE), '[]'
                    ) AS parent_details,
                    fd.fee_details AS fee_details, -- Using formatted fee_details from CTE
                    ft.total_fee_assigned AS total_fee_assigned, -- Retained original total fee assigned
                    ft.total_discount_amount AS total_discount, -- Retained original total discount
                    ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0) AS gross_student_fee,
                    COALESCE(pd.total_penalty, 0.0) AS total_penalty,
                    COALESCE(pdp.total_paid, 0.0) AS total_paid,
                    (ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0))
                        - COALESCE(pdp.total_paid, 0) AS total_due
                FROM
                    student_personal_details spd
                JOIN student_academic_details sad ON spd.student_id = sad.student_id
                JOIN mst_class mc ON sad.student_class_id = mc.class_id
                JOIN mst_section ms ON sad.student_section_id = ms.section_id
                JOIN school_details sd ON spd.school_id = sd.school_id
                JOIN session ses ON sad.session_id = ses.session_id
                LEFT JOIN FeeDetails fd ON spd.student_id = fd.student_id
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id -- Original totals
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN parent_details pds ON pds.parent_id = ANY(spd.parent_id)
                WHERE
                    sad.session_id = ?
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name,sad.admission_no,sad.registration_number, mc.class_id, mc.class_name, ms.section_id,
                    ms.section_name, ses.session_id, ses.academic_session, fd.fee_details,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid
                ORDER BY spd.student_id
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<FeeDepositDetails> feeDepositDetailsList = null;

        try{
            feeDepositDetailsList = jdbcTemplate.query(sql, new Object[]{sessionId,sessionId,sessionId,sessionId, sessionId}, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fdd = new FeeDepositDetails();
                    fdd.setSchoolId(rs.getInt("school_id"));
                    fdd.setSchoolName(rs.getString("school_name"));
                    fdd.setSchoolBuilding(rs.getString("school_building"));
                    fdd.setSchoolAddress(rs.getString("school_address"));
                    fdd.setSchoolEmailAddress(rs.getString("email_address"));
                    fdd.setSchoolPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    fdd.setSchoolCity(rs.getString("school_city"));
                    fdd.setSchoolState(rs.getString("school_state"));
                    fdd.setSchoolCountry(rs.getString("school_country"));
                    fdd.setSchoolZipCode(rs.getString("school_zipcode"));
                    fdd.setStudentId(rs.getInt("student_id"));
                    fdd.setStudentName(rs.getString("student_name"));
                    fdd.setAdmissionNumber(rs.getString("admission_no"));
                    fdd.setRegistrationNumber(rs.getString("registration_number"));
                    fdd.setClassId(rs.getInt("class_id"));
                    fdd.setClassName(rs.getString("class_name"));
                    fdd.setSectionId(rs.getInt("section_id"));
                    fdd.setSectionName(rs.getString("section_name"));
                    fdd.setSessionId(rs.getInt("session_id"));
                    fdd.setSessionName(rs.getString("academic_session"));
                    // Mapping parent_details
                    String parentDetailsJson = rs.getString("parent_details");
                    if (parentDetailsJson != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});

                            // Decrypt sensitive fields
                            for (ParentDetails parent : parentDetailsList) {
                                if (parent.getPhoneNumber() != null) {
                                    parent.setPhoneNumber(CipherUtils.decrypt(parent.getPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                            }

                            fdd.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            fdd.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        fdd.setParentDetails(null);
                    }
                    Array feeDetailsArray = rs.getArray("fee_details");
                    if(feeDetailsArray != null) {
                        fdd.setFeeDetails(Arrays.asList((String[]) rs.getArray("fee_details").getArray()));
                    }else{
                        fdd.setFeeDetails(Collections.emptyList());
                    }
                    fdd.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                    fdd.setTotalDiscount(rs.getDouble("total_discount"));
                    fdd.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                    fdd.setTotalPenalty(rs.getDouble("total_penalty"));
                    fdd.setTotalPaid(rs.getDouble("total_paid"));
                    fdd.setTotalDue(rs.getDouble("total_due"));
                    return fdd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return feeDepositDetailsList;
    }

    @Override
    public List<FeeDepositDetails> getStudentFeeSegregation(int schoolId, int sessionId, int classId, int sectionId, int studentId, String schoolCode) throws Exception {
        String sql = """
                SELECT\s
                                                     fddt1.school_id,\s
                                                     fddt1.school_name,\s
                                                     fddt1.school_building,\s
                                                     fddt1.school_state,\s
                                                     fddt1.school_city,\s
                                                     fddt1.school_country,\s
                                                     fddt1.school_address,\s
                                                     fddt1.school_email,\s
                                                     fddt1.student_id,\s
                                                     fddt1.student_name,\s
                                                     fddt1.student_class_id,\s
                                                     fddt1.class_name,\s
                                                     fddt1.student_section_id,\s
                                                     fddt1.section_name,\s
                                                     fddt1.session_id,\s
                                                     fddt1.session_name,\s
                                                     fddt1.parent_id,\s
                                                     fddt1.parent_name,\s
                                                     fddt1.parent_mobile_no,
                                                     fddls.fd_id,\s
                                                     fddls.system_date_time,\s
                                                     fddt1.fddt_id,
                                                     fddt1.fee_type,\s
                                                     fddt1.fee_id,\s
                                                     fddt1.frequency_id,\s
                                                     fddt1.frequency_type,\s
                                                     fddt1.fa_id,\s
                                                     fddt1.dc_id,
                                                     COALESCE(fddls.penalty_id,0) AS penalty_id,\s
                                                     fddls.penalty_type,\s
                                                     fddt1.due_date,\s
                                                     fddt1.fee_amount,\s
                                                    \s
                                                    
                                                     fddt1.discount_amount AS initial_discount,
                                                     COALESCE(fddls.additional_discount,0) AS additional_discount,
                                                     fddls.additional_discount_reason,
                                                    \s
                                                    
                                                     (fddt1.amount_after_discount - COALESCE(fddls.additional_discount,0)) AS net_amount_after_discount,
                                                     COALESCE(fddls.total_penalty,0) AS total_penalty,
                                                     (fddt1.amount_after_discount - COALESCE(fddls.additional_discount,0) + COALESCE(fddls.total_penalty,0)) AS total_payable_amount,
                                                     COALESCE(fddls.total_paid,0) AS total_paid,\s
                                                     (fddt1.amount_after_discount - COALESCE(fddls.additional_discount,0) + COALESCE(fddls.total_penalty,0) - COALESCE(fddls.total_paid,0)) AS amount_due,
                                                     COALESCE(fddls.status,'Pending') AS status
                                                 FROM (
                                                     SELECT\s
                                                         fddt.fa_id,\s
                                                         fa.fee_id,\s
                                                         fa.dc_id,\s
                                                         sad.school_id,\s
                                                         sd1.school_name,\s
                                                         sd1.school_building,\s
                                                         sd1.school_state,\s
                                                         sd1.school_city,\s
                                                         sd1.school_country,\s
                                                         sd1.school_address,\s
                                                         sd1.school_email,\s
                                                         spd.student_id,\s
                                                         spd.student_name,\s
                                                         sad.student_class_id,\s
                                                         mc.class_name,\s
                                                         sad.student_section_id,\s
                                                         ms.section_name,
                                                         sad.session_id,\s
                                                         sad.session_name,\s
                                                         spd.parent_id,\s
                                                         spd.parent_name,\s
                                                         spd.parent_mobile_no,
                                                         fddt.fddt_id,\s
                                                         sf.fee_type,\s
                                                         sf.frequency_id,\s
                                                         mf.frequency_type,\s
                                                         fddt.due_date,\s
                                                         fddt.fee_amount,\s
                                                         fddt.discount_amount,\s
                                                         (fddt.fee_amount - fddt.discount_amount) AS amount_after_discount
                                                     FROM fee_due_date fddt
                                                     JOIN fee_assignment fa ON fddt.fa_id = fa.fa_id
                                                     JOIN (
                                                         SELECT sd.school_id,sd.school_name, sd.school_building, sd.school_state,\s
                                                                sd.school_city, sd.school_country, sd.school_address,\s
                                                                sd.email_address AS school_email\s
                                                         FROM school_details sd\s
                                                         WHERE sd.school_id = ?
                                                     ) AS sd1 ON sd1.school_id = fa.school_id
                                                     JOIN (
                                                         SELECT s.student_id, s.school_id,s.session_id,s.student_class_id,
                                                                s.student_section_id, s.enrolled_session AS session_name\s
                                                         FROM student_academic_details s\s
                                                         WHERE s.school_id= ?
                                                           AND s.session_id = ?\s
                                                           AND s.student_class_id= ?\s
                                                           AND s.student_section_id= ?\s
                                                           AND s.student_id = ?
                                                     ) AS sad ON (
                                                         fa.student_id = sad.student_id
                                                         OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                                                         OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id =0)
                                                     )
                                                     JOIN (
                                                         SELECT student_id,\s
                                                                CONCAT(first_name,' ',last_name) AS student_name,\s
                                                                parent_id,\s
                                                                COALESCE(father_name,mother_name) AS parent_name,\s
                                                                COALESCE(phone_number,emergency_phone_number) AS parent_mobile_no\s
                                                         FROM student_personal_details\s
                                                         WHERE student_id = ?
                                                     ) AS spd ON spd.student_id=sad.student_id
                                                     JOIN mst_class mc ON mc.school_id = fa.school_id AND mc.class_id = sad.student_class_id
                                                     JOIN mst_section ms ON ms.school_id = fa.school_id AND ms.section_id = sad.student_section_id
                                                     JOIN school_fees sf ON fa.fee_id = sf.fee_id
                                                     JOIN mst_frequency mf ON mf.frequency_id = sf.frequency_id
                                                 ) AS fddt1
                                                 LEFT JOIN (
                                                     SELECT\s
                                                         fddlb.fddt_id,\s
                                                         fddlb.system_date_time,\s
                                                         fddtpa.total_paid,\s
                                                         fddlb.penalty_id,\s
                                                         fddlb.penalty_type,\s
                                                         fddtpa.total_penalty,\s
                                                         fddlb.balance,\s
                                                         fddlb.status,\s
                                                         fddlb.fd_id,
                                
                                                         fddlb.additional_discount,
                                                         fddlb.additional_discount_reason
                                                     FROM (
                                                         SELECT\s
                                                             fdd2.fddt_id,
                                                             SUM(fdd2.penalty_amount) AS total_penalty,\s
                                                             SUM(fdd2.amount_paid) AS total_paid
                                                         FROM fee_deposit_details fdd2
                                                         WHERE fdd2.fd_id IN (
                                                             SELECT fd2.fd_id\s
                                                             FROM fee_deposit fd2 \s
                                                             WHERE fd2.school_id = ?\s
                                                               AND fd2.session_id = ?\s
                                                               AND fd2.class_id = ?\s
                                                               AND fd2.section_id = ?\s
                                                               AND fd2.student_id = ?
                                                         )
                                                         GROUP BY fdd2.fddt_id
                                                     ) AS fddtpa
                                                     JOIN (
                                                         SELECT\s
                                                             fdd.fd_id,\s
                                                             fdd.system_date_time,\s
                                                             fdd.fddt_id,\s
                                                             fdd.penalty_id,\s
                                                             p.penalty_type,\s
                                                             fdd.penalty_amount,\s
                                                             fdd.balance,\s
                                                             fdd.status,
                                                             
                                                             fdd.additional_discount,
                                                             fdd.additional_discount_reason
                                                         FROM fee_deposit_details fdd
                                                         LEFT JOIN penalty p ON fdd.penalty_id = p.penalty_id
                                                         WHERE fdd.fdd_id IN (
                                                             SELECT MAX(fdd1.fdd_id)\s
                                                             FROM fee_deposit_details fdd1
                                                             WHERE fdd1.fd_id IN (
                                                                 SELECT fd1.fd_id\s
                                                                 FROM fee_deposit fd1 \s
                                                                 WHERE fd1.school_id= ?\s
                                                                   AND fd1.session_id = ?
                                                                   AND fd1.class_id= ?\s
                                                                   AND fd1.section_id= ?\s
                                                                   AND fd1.student_id = ?
                                                             )
                                                             GROUP BY fdd1.fddt_id
                                                         )
                                                     ) AS fddlb ON fddtpa.fddt_id = fddlb.fddt_id
                                                 ) AS fddls ON fddt1.fddt_id = fddls.fddt_id
                                                 ORDER BY fddt1.due_date ASC;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<FeeDepositDetails> feeDepositDetails = null;

        try{
            feeDepositDetails = jdbcTemplate.query(sql, new Object[]{schoolId,
                    schoolId,
                    sessionId,
                    classId,
                    sectionId,
                    studentId,
                    studentId,
                    schoolId,
                    sessionId,
                    classId,
                    sectionId,
                    studentId,
                    schoolId,
                    sessionId,
                    classId,
                    sectionId,
                    studentId }, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fdd = new FeeDepositDetails();
                    fdd.setSchoolId(rs.getInt("school_id"));
                    fdd.setSchoolName(rs.getString("school_name"));
                    fdd.setSchoolBuilding(rs.getString("school_building"));
                    fdd.setSchoolState(rs.getString("school_state"));
                    fdd.setSchoolCity(rs.getString("school_city"));
                    fdd.setSchoolCountry(rs.getString("school_country"));
                    fdd.setSchoolAddress(rs.getString("school_address"));
                    fdd.setSchoolEmailAddress(rs.getString("school_email"));
                    fdd.setStudentId(rs.getInt("student_id"));
                    fdd.setStudentName(rs.getString("student_name"));
                    fdd.setClassId(rs.getInt("student_class_id"));
                    fdd.setClassName(rs.getString("class_name"));
                    fdd.setSectionId(rs.getInt("student_section_id"));
                    fdd.setSectionName(rs.getString("section_name"));
                    fdd.setSessionId(rs.getInt("session_id"));
                    fdd.setSessionName(rs.getString("session_name"));
                    // Handling parent_id as an array
                    java.sql.Array parentIdArray = rs.getArray("parent_id");
                    if (parentIdArray != null) {
                        Integer[] parentIds = (Integer[]) parentIdArray.getArray();
                        fdd.setParentId(Arrays.asList(parentIds)); // Convert array to list
                    } else {
                        fdd.setParentId(Collections.emptyList()); // If no parent ID is found, set empty list
                    }
                    fdd.setParentName(rs.getString("parent_name"));
                    fdd.setParentMobileNumber(CipherUtils.decrypt(rs.getString("parent_mobile_no")));
                    fdd.setFdId(rs.getInt("fd_id"));
                    fdd.setSystemDateTime(rs.getDate("system_date_time") != null ? rs.getDate("system_date_time") : null);
                    fdd.setFddtId(rs.getInt("fddt_id"));
                    fdd.setFeeType(rs.getString("fee_type"));
                    fdd.setFeeId(rs.getInt("fee_id"));
                    fdd.setFrequencyId(rs.getInt("frequency_id"));
                    fdd.setFrequencyType(rs.getString("frequency_type"));
                    fdd.setFaId(rs.getInt("fa_id"));
                    fdd.setDcId(rs.getInt("dc_id"));
                    fdd.setPenaltyId(rs.getInt("penalty_id"));
                    fdd.setPenaltyTypes(rs.getString("penalty_type"));
                    fdd.setDueDate(rs.getDate("due_date"));
                    fdd.setFeeAmount(rs.getDouble("fee_amount"));
                    fdd.setDiscountAmount(rs.getDouble("initial_discount"));
                    fdd.setAmountAfterDiscount(rs.getDouble("net_amount_after_discount"));
                    fdd.setTotalPenalty(rs.getDouble("total_penalty"));
                    fdd.setTotalPayableAmount(rs.getDouble("total_payable_amount"));
                    fdd.setTotalPaid(rs.getDouble("total_paid"));
                    fdd.setAmountDue(rs.getDouble("amount_due"));
                    fdd.setStatus(rs.getString("status"));
                    fdd.setAdditionalDiscount(rs.getDouble("additional_discount"));
                    fdd.setAdditionalDiscountReason(rs.getString("additional_discount_reason"));
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
    public FeeDepositDetails getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception {
        String sql = """
                WITH FeeDetailsIntermediate AS (
                                    SELECT
                                        spd.student_id,
                                        fs.fee_type,
                                        fdd.fee_amount,
                                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS fee_count,
                                        SUM(fdd.discount_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS total_discount_amount,
                                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) * fdd.fee_amount AS total_fee_for_type
                                    FROM
                                        fee_assignment fa
                                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                                    JOIN school_fees fs ON fa.fee_id = fs.fee_id
                                    JOIN student_academic_details sad
                                        ON (fa.student_id = sad.student_id
                                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0))
                                    JOIN student_personal_details spd
                                        ON sad.student_id = spd.student_id
                                    WHERE
                                        sad.session_id = ?
                                        AND sad.student_class_id = ?
                                        AND sad.student_section_id = ?
                                        AND sad.student_id = ?
                                       -- AND spd.deleted IS NOT TRUE
                                ),
                                FeeDetails AS (
                                    SELECT
                                        student_id,
                                        ARRAY_AGG(
                                            DISTINCT CONCAT(
                                                'Fee Type: ', fee_type,
                                                ', Fee Amount: ',
                                                CASE
                                                    WHEN fee_count > 1 THEN CONCAT(fee_count, '*', fee_amount::text, ' = ', total_fee_for_type::text)
                                                    ELSE total_fee_for_type::text
                                                END,
                                                ', Total Discount Amount: ', total_discount_amount::text
                                            )
                                        ) AS fee_details,
                                        SUM(total_fee_for_type) AS total_fee_amount,
                                        SUM(total_discount_amount) AS total_discount_amount
                                    FROM FeeDetailsIntermediate
                                    GROUP BY student_id
                                ),
                                FeeTotals AS (
                                    SELECT
                                        spd.student_id,
                                        SUM(fdd.fee_amount) AS total_fee_assigned,
                                        SUM(fdd.discount_amount) AS total_discount_amount
                                    FROM
                                        fee_assignment fa
                                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                                    JOIN student_academic_details sad
                                        ON (fa.student_id = sad.student_id
                                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0))
                                    JOIN student_personal_details spd
                                        ON sad.student_id = spd.student_id
                                    WHERE
                                        sad.session_id = ?
                                        AND sad.student_class_id = ?
                                        AND sad.student_section_id = ?
                                        AND sad.student_id = ?
                                        --AND spd.deleted IS NOT TRUE
                                    GROUP BY spd.student_id
                                ),
                                PenaltyDetails AS (
                                    SELECT
                                        fd.student_id,
                                        COALESCE(SUM(fdd.penalty_amount), 0.0) AS total_penalty
                                    FROM
                                        fee_deposit_details fdd
                                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
                                    WHERE fd.session_id = ? AND fd.class_id = ? AND fd.section_id = ? AND fd.student_id = ?
                                    GROUP BY fd.student_id
                                ),
                                PaidDetails AS (
                                    SELECT
                                        fd.student_id,
                                        COALESCE(SUM(fd.total_amount_paid), 0.0) AS total_paid,
                                        MAX(fd.fd_id) AS latest_fd_id
                                    FROM fee_deposit fd
                                    WHERE fd.session_id = ? AND fd.class_id = ? AND fd.section_id = ? AND fd.student_id = ?
                                    GROUP BY fd.student_id
                                ),
                                LastPaymentMode AS (
                                    select fd.student_id, pm.payment_type AS last_payment_mode
                                    from fee_deposit fd JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                                    where fd.session_id = ? AND fd.class_id = ? AND fd.section_id = ? AND fd.student_id = ? ORDER BY fd.fd_id desc  LIMIT 1
                                )
                                SELECT
                                    sd.school_id,
                                    sd.school_name,
                                    sd.school_building,
                                    sd.school_address,
                                    sd.email_address,
                                    sd.phone_number,
                                    sd.school_city,
                                    sd.school_state,
                                    sd.school_country,
                                    sd.school_zipcode,
                                    spd.student_id,
                                    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
                                    mc.class_id,
                                    mc.class_name,
                                    ms.section_id,
                                    ms.section_name,
                                    ses.session_id,
                                    ses.academic_session,
                                    COALESCE(
                                        JSON_AGG(
                                            JSON_BUILD_OBJECT(
                                                'parentId', pds.parent_id,
                                                'firstName', pds.first_name,
                                                'lastName', pds.last_name,
                                                'phoneNumber', pds.phone_number,
                                                'emergencyPhoneNumber', pds.emergency_phone_number,
                                                'whatsappNumber', pds.whatsapp_no,
                                                'parentType', pds.parent_type
                                            )
                                        ) FILTER (WHERE pds.parent_id IS NOT NULL AND pds.deleted IS NOT TRUE), '[]'
                                    ) AS parent_details,
                                    fd.fee_details AS fee_details, -- Using formatted fee_details from CTE
                                    ft.total_fee_assigned AS total_fee_assigned, -- Retained original total fee assigned
                                    ft.total_discount_amount AS total_discount, -- Retained original total discount
                                    ft.total_fee_assigned
                                        - ft.total_discount_amount
                                        + COALESCE(pd.total_penalty, 0) AS gross_student_fee,
                                    COALESCE(pd.total_penalty, 0.0) AS total_penalty,
                                    COALESCE(pdp.total_paid, 0.0) AS total_paid,
                                    (ft.total_fee_assigned
                                        - ft.total_discount_amount
                                        + COALESCE(pd.total_penalty, 0))
                                        - COALESCE(pdp.total_paid, 0) AS total_due,
                                        pdp.latest_fd_id,
                                        lpm.last_payment_mode
                                FROM
                                    student_personal_details spd
                                JOIN student_academic_details sad ON spd.student_id = sad.student_id
                                JOIN mst_class mc ON sad.student_class_id = mc.class_id
                                JOIN mst_section ms ON sad.student_section_id = ms.section_id
                                JOIN school_details sd ON spd.school_id = sd.school_id
                                JOIN session ses ON sad.session_id = ses.session_id
                                LEFT JOIN FeeDetails fd ON spd.student_id = fd.student_id
                                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id -- Original totals
                                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                                LEFT JOIN parent_details pds ON pds.parent_id = ANY(spd.parent_id)
                                LEFT JOIN LastPaymentMode lpm ON spd.student_id = lpm.student_id
                                WHERE
                                    sad.session_id = ?
                                    AND sad.student_class_id = ?
                                    AND sad.student_section_id = ?
                                    AND sad.student_id = ?
                                    --AND spd.deleted IS NOT TRUE
                                GROUP BY
                                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                                    spd.student_id, spd.first_name, spd.last_name, mc.class_id, mc.class_name, ms.section_id,
                                    ms.section_name, ses.session_id, ses.academic_session, fd.fee_details,
                                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid, pdp.latest_fd_id, lpm.last_payment_mode
                                ORDER BY spd.student_id
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        FeeDepositDetails feeDepositDetails = null;
        try{
            feeDepositDetails = jdbcTemplate.queryForObject(sql, new Object[]{sessionId, classId, sectionId, studentId, sessionId, classId, sectionId, studentId, sessionId, classId, sectionId, studentId, sessionId, classId, sectionId, studentId, sessionId, classId, sectionId, studentId, sessionId, classId, sectionId, studentId}, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fdd = new FeeDepositDetails();
                    fdd.setSchoolId(rs.getInt("school_id"));
                    fdd.setSchoolName(rs.getString("school_name"));
                    fdd.setSchoolBuilding(rs.getString("school_building"));
                    fdd.setSchoolAddress(rs.getString("school_address"));
                    fdd.setSchoolEmailAddress(rs.getString("email_address"));
                    fdd.setSchoolPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    fdd.setSchoolCity(rs.getString("school_city"));
                    fdd.setSchoolState(rs.getString("school_state"));
                    fdd.setSchoolCountry(rs.getString("school_country"));
                    fdd.setSchoolZipCode(rs.getString("school_zipcode"));
                    fdd.setStudentId(rs.getInt("student_id"));
                    fdd.setStudentName(rs.getString("student_name"));
                    fdd.setClassId(rs.getInt("class_id"));
                    fdd.setClassName(rs.getString("class_name"));
                    fdd.setSectionId(rs.getInt("section_id"));
                    fdd.setSectionName(rs.getString("section_name"));
                    fdd.setSessionId(rs.getInt("session_id"));
                    fdd.setSessionName(rs.getString("academic_session"));
                    // Mapping parent_details
                    String parentDetailsJson = rs.getString("parent_details");
                    if (parentDetailsJson != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});

                            // Decrypt sensitive fields
                            for (ParentDetails parent : parentDetailsList) {
                                if (parent.getPhoneNumber() != null) {
                                    parent.setPhoneNumber(CipherUtils.decrypt(parent.getPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                            }

                            fdd.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            fdd.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        fdd.setParentDetails(null);
                    }
                    fdd.setFeeDetails(Arrays.asList((String[])rs.getArray("fee_details").getArray()));
                    fdd.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                    fdd.setTotalDiscount(rs.getDouble("total_discount"));
                    fdd.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                    fdd.setTotalPenalty(rs.getDouble("total_penalty"));
                    fdd.setTotalPaid(rs.getDouble("total_paid"));
                    fdd.setTotalDue(rs.getDouble("total_due"));
                    fdd.setMaxFdIdAsTransactionId(rs.getInt("latest_fd_id"));
                    fdd.setLastPaymentMode(rs.getString("last_payment_mode"));
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

   /* @Override
    public List<FeeDepositDetails> findFeeDepositDetailsById(int fdId, int schoolId, String schoolCode) throws Exception {
        String sql = """
        SELECT 
            spd.first_name || ' ' || spd.last_name AS student_name,
            sad.admission_no,
            mc.class_name,
            ms.section_name,
            s.academic_session,
            fd.transaction_id,
            TO_CHAR(fd.system_date_time, 'DD-MM-YYYY HH24:MI') AS payment_date,
            fd.total_amount_paid AS total_paid,
            pm.payment_type,
            json_agg(
                json_build_object(
                    'fee_type', sf.fee_type,
                    'due_date', TO_CHAR(fddt.due_date, 'DD-MM-YYYY'),
                    'original_amount', fddt.fee_amount,
                    'discount_description', dc.dc_description,
                    'discount_rate', dc.dc_rate || 
                        CASE WHEN dc.dc_rate_type = 'percentage' THEN '%' ELSE ' Rs' END,
                    'discount_amount', fdd.discount_amount,
                    'penalty_amount', fdd.penalty_amount,
                    'amount_paid', fdd.amount_paid,
                    'balance', fdd.balance
                )
            ) AS fee_details
        FROM fee_deposit fd
        LEFT JOIN fee_deposit_details fdd ON fd.fd_id = fdd.fd_id
        LEFT JOIN fee_assignment fa ON fdd.fa_id = fa.fa_id
        LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id
        LEFT JOIN discount_code dc ON fdd.dc_id = dc.dc_id
        LEFT JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
        LEFT JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
        LEFT JOIN student_personal_details spd ON fd.student_id = spd.student_id
        LEFT JOIN student_academic_details sad ON fd.student_id = sad.student_id
        LEFT JOIN mst_class mc ON sad.student_class_id = mc.class_id
        LEFT JOIN mst_section ms ON sad.student_section_id = ms.section_id
        LEFT JOIN session s ON sad.session_id = s.session_id
        WHERE fd.fd_id = ? AND fd.school_id = ?
        GROUP BY 
            spd.first_name, spd.last_name, 
            sad.admission_no, mc.class_name,
            ms.section_name, s.academic_session,
            fd.transaction_id, fd.system_date_time,
            fd.total_amount_paid, pm.payment_type
        """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeDepositDetails> feeDepositDetails = null;

        try {
            feeDepositDetails = jdbcTemplate.query(sql, new Object[]{fdId, schoolId}, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails details = new FeeDepositDetails();

                    // Student Information
                    details.setStudentName(rs.getString("student_name"));
                    details.setAdmissionNumber(rs.getString("admission_no"));
                    details.setClassName(rs.getString("class_name"));
                    details.setSectionName(rs.getString("section_name"));
                    details.setSessionName(rs.getString("academic_session"));

                    // Payment Information
                    details.setTransactionId(rs.getString("transaction_id"));
                    details.setSystemDateTime(rs.getTimestamp("payment_date"));
                    details.setTotalPaid(rs.getDouble("total_paid"));
                    details.setPaymentType(rs.getString("payment_type"));

                    // Fee Details
                    Array jsonArray = rs.getArray("fee_detail");
                    details.setFeeDetails(jsonArray != null ?
                            Arrays.asList((String[]) jsonArray.getArray()) : null);

                    return details;
                }
            });
            return feeDepositDetails;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error fetching fee deposit details", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }*/
   public List<FeeDepositDetails> findFeeDepositDetailsById(int fdId, int schoolId, String schoolCode) throws Exception {
//       String sql= """
//               WITH fee_details_cte AS (
//                   SELECT DISTINCT
//                       fdd.fd_id,
//                       sf.fee_type,
//                       fddt.due_date,
//                       fddt.fee_amount,
//                       dc.dc_description,
//                       dc.dc_rate,
//                       dc.dc_rate_type,
//                       fdd.discount_amount,
//                       fdd.additional_discount,
//                       fdd.penalty_amount,
//                       fdd.amount_paid,
//                       fdd.balance
//                   FROM fee_deposit_details fdd
//                   LEFT JOIN fee_assignment fa ON fdd.fa_id = fa.fa_id
//                   LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id
//                   LEFT JOIN discount_code dc ON fdd.dc_id = dc.dc_id
//                   LEFT JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
//               )
//               SELECT
//                   spd.first_name || ' ' || spd.last_name AS student_name,
//                   sad.admission_no,
//                   mc.class_name,
//                   ms.section_name,
//                   s.academic_session,
//                   fd.transaction_id,
//                   fd.payment_date,
//                   fd.total_amount_paid,
//                   pm.payment_type,
//                   json_agg(
//                      DISTINCT jsonb_build_object(
//                           'fee_type', fdc.fee_type,
//                           'due_date', fdc.due_date,
//                           'original_amount', fdc.fee_amount,
//                           'discount_description', fdc.dc_description,
//                           'discount_rate', fdc.dc_rate ||
//                               CASE WHEN fdc.dc_rate_type = 'percentage' THEN '%' ELSE ' Rs' END,
//                           'discount_amount', fdc.discount_amount + COALESCE(fdc.additional_discount, 0),
//                           'penalty_amount', fdc.penalty_amount,
//                           'amount_paid', fdc.amount_paid,
//                           'balance', fdc.balance
//                       )
//                   ) AS fee_details
//               FROM fee_deposit fd
//               LEFT JOIN fee_details_cte fdc ON fd.fd_id = fdc.fd_id
//               LEFT JOIN student_personal_details spd ON fd.student_id = spd.student_id
//               LEFT JOIN student_academic_details sad ON fd.student_id = sad.student_id
//               LEFT JOIN mst_class mc ON sad.student_class_id = mc.class_id
//               LEFT JOIN mst_section ms ON sad.student_section_id = ms.section_id
//               LEFT JOIN session s ON sad.session_id = s.session_id
//               LEFT JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
//               WHERE fd.fd_id = ? AND fd.school_id = ?
//               GROUP BY
//                   spd.first_name, spd.last_name,
//                   sad.admission_no, mc.class_name,
//                   ms.section_name, s.academic_session,
//                   fd.transaction_id, fd.payment_date,
//                   fd.total_amount_paid, pm.payment_type
//               """;
//       String sql = """
//               WITH fee_details_cte AS (
//                   SELECT DISTINCT
//                       fdd.fd_id,
//                       sf.fee_type,
//                       fddt.due_date,
//                       fddt.fee_amount,
//                       dc.dc_description,
//                       dc.dc_rate,
//                       dc.dc_rate_type,
//                       fdd.discount_amount,
//                       fdd.additional_discount,
//                       fdd.penalty_amount,
//                       fdd.amount_paid,
//                       fdd.balance
//                   FROM fee_deposit_details fdd
//                   LEFT JOIN fee_assignment fa ON fdd.fa_id = fa.fa_id
//                   LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id
//                   LEFT JOIN discount_code dc ON fdd.dc_id = dc.dc_id
//                   LEFT JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
//               )
//               SELECT
//                   spd.first_name || ' ' || spd.last_name AS student_name,
//                   sad.admission_no,
//                   mc.class_name,
//                   ms.section_name,
//                   s.academic_session,
//                   fd.transaction_id,
//                   fd.payment_date,
//                   fd.total_amount_paid,
//                   pm.payment_type,
//                   json_agg(
//                       DISTINCT jsonb_build_object(
//                           'fee_type', fdc.fee_type,
//                           'due_date', fdc.due_date,
//                           'original_amount', fdc.fee_amount,
//                           'discount_description', fdc.dc_description,
//                           'discount_rate', fdc.dc_rate ||
//                               CASE WHEN fdc.dc_rate_type = 'percentage' THEN '%' ELSE ' Rs' END,
//                           'discount_amount', fdc.discount_amount + COALESCE(fdc.additional_discount, 0),
//                           'penalty_amount', fdc.penalty_amount,
//                           'amount_paid', fdc.amount_paid,
//                           'balance', fdc.balance
//                       )
//                   ) AS fee_details
//               FROM fee_deposit fd
//               LEFT JOIN fee_details_cte fdc ON fd.fd_id = fdc.fd_id
//               LEFT JOIN student_personal_details spd ON fd.student_id = spd.student_id
//                   AND fd.school_id = spd.school_id
//               LEFT JOIN student_academic_details sad ON fd.student_id = sad.student_id
//                   AND fd.school_id = sad.school_id
//                   AND fd.session_id = sad.session_id
//               LEFT JOIN mst_class mc ON sad.student_class_id = mc.class_id
//                   AND sad.school_id = mc.school_id
//               LEFT JOIN mst_section ms ON sad.student_section_id = ms.section_id
//                   AND sad.school_id = ms.school_id
//               LEFT JOIN session s ON sad.session_id = s.session_id
//                   AND sad.school_id = s.school_id
//               LEFT JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
//                   AND fd.school_id = pm.school_id
//               WHERE fd.fd_id = ?
//                   AND fd.school_id = ?
//                   -- Apply Male/Female and Old/New filtering based on fee_type
//                   AND (
//                       -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
//                       (UPPER(fdc.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
//                       -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
//                       OR (UPPER(fdc.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
//                       -- Case 3: Fee type has "- Male" suffix, check gender = 'Male' (case-insensitive)
//                       OR (UPPER(fdc.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
//                       -- Case 4: Fee type has "- Female" suffix, check gender = 'Female' (case-insensitive)
//                       OR (UPPER(fdc.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
//                       -- Case 5: Fee type doesn't have any gender/student_type suffix
//                       OR (
//                           fdc.fee_type IS NULL
//                           OR (
//                               UPPER(fdc.fee_type) NOT LIKE '% - OLD'
//                               AND UPPER(fdc.fee_type) NOT LIKE '% - NEW'
//                               AND UPPER(fdc.fee_type) NOT LIKE '% - MALE'
//                               AND UPPER(fdc.fee_type) NOT LIKE '% - FEMALE'
//                           )
//                       )
//                   )
//               GROUP BY
//                   spd.first_name, spd.last_name,
//                   sad.admission_no, mc.class_name,
//                   ms.section_name, s.academic_session,
//                   fd.transaction_id, fd.payment_date,
//                   fd.total_amount_paid, pm.payment_type
//               """;
       String sql = """
               WITH fee_details_cte AS (
                                  SELECT DISTINCT
                                      fdd.fd_id,
                                      sf.fee_type,
                                      fddt.due_date,
                                      fddt.fee_amount,
                                      dc.dc_description,
                                      dc.dc_rate,
                                      dc.dc_rate_type,
                                      fdd.discount_amount,
                                      fdd.additional_discount,
                                      fdd.penalty_amount,
                                      fdd.amount_paid,
                                      fdd.balance
                                  FROM fee_deposit_details fdd
                                  LEFT JOIN fee_assignment fa ON fdd.fa_id = fa.fa_id
                                  LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id
                                  LEFT JOIN discount_code dc ON fdd.dc_id = dc.dc_id
                                  LEFT JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
                              )
                              SELECT
                                  spd.first_name || ' ' || spd.last_name AS student_name,
                                  sad.admission_no,
                                  sad.roll_number,
                                  spd.gender,
                                  mc.class_name,
                                  ms.section_name,
                                  s.academic_session,
                                  fd.transaction_id,
                                  fd.payment_date,
                                  fd.total_amount_paid,
                                  pm.payment_type,
                                  json_agg(
                                      DISTINCT jsonb_build_object(
                                           -- Modified fee_type display logic
                                          'fee_type',\s
                                          CASE\s
                                              WHEN UPPER(fdc.fee_type) LIKE '% - OLD'\s
                                                   OR UPPER(fdc.fee_type) LIKE '% - NEW'
                                                   OR UPPER(fdc.fee_type) LIKE '% - MALE'
                                                   OR UPPER(fdc.fee_type) LIKE '% - FEMALE'
                                              THEN TRIM(SUBSTRING(fdc.fee_type FROM 1 FOR\s
                                                   POSITION(' - ' IN fdc.fee_type) - 1))
                                              ELSE fdc.fee_type\s
                                          END,
                                          -- Add type field for Old/New categorization
                                          'type',
                                          CASE\s
                                              WHEN UPPER(fdc.fee_type) LIKE '% - OLD' THEN 'Old'
                                              WHEN UPPER(fdc.fee_type) LIKE '% - NEW' THEN 'New'
                                              ELSE NULL
                                          END,
                                          'due_date', fdc.due_date,
                                          'original_amount', fdc.fee_amount,
                                          'discount_description', fdc.dc_description,
                                          'discount_rate', fdc.dc_rate ||
                                              CASE WHEN fdc.dc_rate_type = 'percentage' THEN '%' ELSE ' Rs' END,
                                          'discount_amount', fdc.discount_amount + COALESCE(fdc.additional_discount, 0),
                                          'penalty_amount', fdc.penalty_amount,
                                          'amount_paid', fdc.amount_paid,
                                          'balance', fdc.balance
                                      )
                                  ) AS fee_details
                              FROM fee_deposit fd
                              LEFT JOIN fee_details_cte fdc ON fd.fd_id = fdc.fd_id
                              LEFT JOIN student_personal_details spd ON fd.student_id = spd.student_id
                                  AND fd.school_id = spd.school_id
                              LEFT JOIN student_academic_details sad ON fd.student_id = sad.student_id
                                  AND fd.school_id = sad.school_id
                                  AND fd.session_id = sad.session_id
                              LEFT JOIN mst_class mc ON sad.student_class_id = mc.class_id
                                  AND sad.school_id = mc.school_id
                              LEFT JOIN mst_section ms ON sad.student_section_id = ms.section_id
                                  AND sad.school_id = ms.school_id
                              LEFT JOIN session s ON sad.session_id = s.session_id
                                  AND sad.school_id = s.school_id
                              LEFT JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                                  AND fd.school_id = pm.school_id
                              WHERE fd.fd_id = ?
                                  AND fd.school_id = ?
                                  -- Apply Male/Female and Old/New filtering based on fee_type
                                  AND (
                                      -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
                                      (UPPER(fdc.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
                                      -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
                                      OR (UPPER(fdc.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
                                      -- Case 3: Fee type has "- Male" suffix, check gender = 'Male' (case-insensitive)
                                      OR (UPPER(fdc.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
                                      -- Case 4: Fee type has "- Female" suffix, check gender = 'Female' (case-insensitive)
                                      OR (UPPER(fdc.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
                                      -- Case 5: Fee type doesn't have any gender/student_type suffix
                                      OR (
                                          fdc.fee_type IS NULL
                                          OR (
                                              UPPER(fdc.fee_type) NOT LIKE '% - OLD'
                                              AND UPPER(fdc.fee_type) NOT LIKE '% - NEW'
                                              AND UPPER(fdc.fee_type) NOT LIKE '% - MALE'
                                              AND UPPER(fdc.fee_type) NOT LIKE '% - FEMALE'
                                          )
                                      )
                                  )
                              GROUP BY
                                  spd.first_name, spd.last_name,
                                  sad.admission_no,sad.roll_number, spd.gender, mc.class_name,
                                  ms.section_name, s.academic_session,
                                  fd.transaction_id, fd.payment_date,
                                  fd.total_amount_paid, pm.payment_type;
               """;

       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       ObjectMapper objectMapper = new ObjectMapper();
       SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

       try {
           return jdbcTemplate.query(sql, new Object[]{fdId, schoolId}, new RowMapper<FeeDepositDetails>() {
               @Override
               public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   FeeDepositDetails details = new FeeDepositDetails();

                   // Student Information
                   details.setStudentName(rs.getString("student_name"));
                   details.setAdmissionNumber(rs.getString("admission_no"));
                   details.setRollNumber(rs.getString("roll_number"));
                   details.setGender(rs.getString("gender"));
                   details.setClassName(rs.getString("class_name"));
                   details.setSectionName(rs.getString("section_name"));
                   details.setSessionName(rs.getString("academic_session"));

                   // Payment Information
                   details.setTransactionId(rs.getString("transaction_id"));

                   // Handle timestamp directly
                   Date paymentDate = rs.getDate("payment_date");
                   if(paymentDate != null) {
                       details.setSystemDateTime(paymentDate);
                       details.setFormattedPaymentDate(dateFormat.format(paymentDate));
                   }

                   details.setTotalPaid(rs.getDouble("total_amount_paid"));
                   details.setPaymentType(rs.getString("payment_type"));

                   // JSON Handling
                   String json = rs.getString("fee_details");
                   if(json != null && !json.isEmpty()) {
                       try {
                           List<FeeDetail> feeDetail = objectMapper.readValue(
                                   json,
                                   new TypeReference<List<FeeDetail>>() {}
                           );
                           details.setFeeDetail(feeDetail);
                       } catch (Exception e) {
                           details.setFeeDetail(Collections.emptyList());
                       }
                   } else {
                       details.setFeeDetail(Collections.emptyList());
                   }

                   return details;
               }
           });
       } catch (Exception e) {
           throw new Exception("Error fetching fee deposit details for fdId: " + fdId, e);
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

    public List<FeeDepositDetails> findFeeDeposits(Integer studentId, String studentName,
                                                   int schoolId, int sessionId, String schoolCode)
            throws Exception {
        StringBuilder sqlBuilder = new StringBuilder("""
        SELECT
            spd.first_name || ' ' || spd.last_name AS student_name,
            sad.admission_no,
            mc.class_name,
            ms.section_name,
            s.academic_session,
            fd.transaction_id,
            fd.fd_id,
            fd.payment_date AS payment_date,
            fd.total_amount_paid AS total_paid,
            pm.payment_type,
            json_agg(
                json_build_object(
                    'fee_type', sf.fee_type,
                    'due_date', fddt.due_date,
                    'original_amount', fddt.fee_amount,
                    'discount_description', dc.dc_description,
                    'discount_rate', dc.dc_rate || 
                        CASE WHEN dc.dc_rate_type = 'percentage' THEN '%' ELSE ' Rs' END,
                    'discount_amount', fdd.discount_amount + COALESCE(fdd.additional_discount, 0),
                    'penalty_amount', fdd.penalty_amount,
                    'amount_paid', fdd.amount_paid,
                    'balance', fdd.balance
                )
            ) AS fee_details
        FROM fee_deposit fd
        LEFT JOIN fee_deposit_details fdd ON fd.fd_id = fdd.fd_id
        LEFT JOIN fee_assignment fa ON fdd.fa_id = fa.fa_id
        LEFT JOIN school_fees sf ON fa.fee_id = sf.fee_id
        LEFT JOIN discount_code dc ON fdd.dc_id = dc.dc_id
        LEFT JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
        LEFT JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
        LEFT JOIN student_personal_details spd ON fd.student_id = spd.student_id
        LEFT JOIN student_academic_details sad ON fd.student_id = sad.student_id
        LEFT JOIN mst_class mc ON sad.student_class_id = mc.class_id
        LEFT JOIN mst_section ms ON sad.student_section_id = ms.section_id
        LEFT JOIN session s ON sad.session_id = s.session_id
        WHERE fd.school_id = ?
          AND s.session_id = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(schoolId);
        params.add(sessionId);

        if (studentId != null) {
            sqlBuilder.append(" AND fd.student_id = ? ");
            params.add(studentId);
        }

        if (StringUtils.hasText(studentName)) {
            sqlBuilder.append(" AND (COALESCE(spd.first_name, '') || ' ' || COALESCE(spd.last_name, '')) ILIKE ? ");
            params.add("%" + studentName.trim() + "%");
        }

        sqlBuilder.append("""
        GROUP BY
            spd.first_name, spd.last_name, 
            sad.admission_no, mc.class_name,
            ms.section_name, s.academic_session,
            fd.transaction_id, fd.fd_id, fd.payment_date,
            fd.total_amount_paid, pm.payment_type
        ORDER BY fd.payment_date DESC
        """);

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
            return jdbcTemplate.query(sqlBuilder.toString(), params.toArray(), new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails details = new FeeDepositDetails();

                    // Student Information
                    details.setStudentName(rs.getString("student_name"));
                    details.setAdmissionNumber(rs.getString("admission_no"));
                    details.setClassName(rs.getString("class_name"));
                    details.setSectionName(rs.getString("section_name"));
                    details.setSessionName(rs.getString("academic_session"));

                    // Payment Information
                    details.setTransactionId(rs.getString("transaction_id"));
                    details.setFdId(rs.getInt("fd_id"));

                    Timestamp paymentDate = rs.getTimestamp("payment_date");
                    if(paymentDate != null) {
                        details.setSystemDateTime(paymentDate);
                        details.setFormattedPaymentDate(dateFormat.format(paymentDate));
                    }

                    details.setTotalPaid(rs.getDouble("total_paid"));
                    details.setPaymentType(rs.getString("payment_type"));

                    // JSON Handling
                    String json = rs.getString("fee_details");
                    if(json != null && !json.isEmpty()) {
                        try {
                            List<FeeDetail> feeDetail = objectMapper.readValue(
                                    json,
                                    new TypeReference<List<FeeDetail>>() {}
                            );
                            details.setFeeDetail(feeDetail);
                        } catch (Exception e) {
                            details.setFeeDetail(Collections.emptyList());
                        }
                    } else {
                        details.setFeeDetail(Collections.emptyList());
                    }

                    return details;
                }

            });
        } catch (Exception e) {
            throw new Exception("Error fetching fee deposits for school: " + schoolId +
                    ", session: " + sessionId +
                    (studentId != null ? ", studentId: " + studentId : "") +
                    (StringUtils.hasText(studentName) ? ", studentName: " + studentName : ""), e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    public List<FeeDepositDetails> getAcademicFeesScreen(int schoolId, int sessionId, int studentId, String schoolCode) throws Exception {
//        String sql= """
//                WITH payment_data AS (
//                    SELECT
//                        fdd.fddt_id,
//                        SUM(fdd.amount_paid) AS total_paid,
//                        SUM(fdd.penalty_amount) AS total_penalty,
//                        SUM(fdd.additional_discount) AS total_additional_discount, -- Added this line
//                        MAX(fdd.fdd_id) AS latest_fdd_id
//                    FROM fee_deposit_details fdd
//                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
//                    WHERE fd.school_id = ?
//                        AND fd.session_id = ?
//                        AND fd.student_id = ?
//                    GROUP BY fdd.fddt_id
//                ),
//                assignment_data AS (
//                    SELECT
//                        fddt.fa_id,
//                        fa.fee_id,
//                        fa.dc_id,
//                        sad.school_id,
//                        spd.student_id,
//                        COALESCE(spd.father_name, spd.mother_name) AS parent_name,
//                        spd.phone_number AS parent_mobile_no,
//                        spd.first_name || ' ' || spd.last_name AS student_name,
//                        sad.student_class_id,
//                        mc.class_name,
//                        sad.student_section_id,
//                        ms.section_name,
//                        sad.session_id,
//                        s.academic_session AS session_name,
//                        fddt.fddt_id,
//                        sf.fee_type,
//                        sf.frequency_id,
//                        mf.frequency_type,
//                        fddt.due_date,
//                        fddt.fee_amount,
//                        fddt.discount_amount AS initial_discount,
//                        fa.valid_from,
//                        fa.valid_to,
//                        ROW_NUMBER() OVER (
//                            PARTITION BY sf.fee_id, fddt.due_date
//                            ORDER BY
//                                CASE
//                                    WHEN fa.student_id IS NOT NULL THEN 1
//                                    WHEN fa.section_id IS NOT NULL THEN 2
//                                    ELSE 3
//                                END
//                        ) AS priority_rank
//                    FROM fee_due_date fddt
//                    JOIN fee_assignment fa ON fddt.fa_id = fa.fa_id
//                    JOIN student_academic_details sad
//                        ON sad.school_id = ?
//                        AND sad.session_id = ?
//                        AND sad.student_id = ?
//                    JOIN student_personal_details spd
//                        ON spd.student_id = sad.student_id
//                        AND spd.school_id = ?
//                    JOIN session s ON sad.session_id = s.session_id
//                    JOIN mst_class mc
//                        ON mc.class_id = sad.student_class_id
//                        AND mc.school_id = ?
//                    JOIN mst_section ms
//                        ON ms.section_id = sad.student_section_id
//                        AND ms.school_id = ?
//                    JOIN school_fees sf ON fa.fee_id = sf.fee_id
//                    JOIN mst_frequency mf ON mf.frequency_id = sf.frequency_id
//                    LEFT JOIN fee_assignment_exclusion fex
//                        ON fa.fa_id = fex.fa_id
//                        AND fex.student_id = ?
//                        AND CURRENT_DATE BETWEEN fex.valid_from AND COALESCE(fex.valid_to, '9999-12-31')
//                    WHERE fex.exclusion_id IS NULL
//                        AND CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, '9999-12-31')
//                        AND (
//                            (fa.student_id = sad.student_id)
//                            OR (fa.student_id IS NULL AND fa.section_id = sad.student_section_id AND fa.class_id = sad.student_class_id)
//                            OR (fa.student_id IS NULL AND fa.section_id IS NULL AND fa.class_id = sad.student_class_id)
//                         )
//                )
//                SELECT
//                    ad.school_id,
//                    ad.student_id,
//                    ad.student_name,
//                    ad.student_class_id,
//                    ad.class_name,
//                    ad.student_section_id,
//                    ad.section_name,
//                    ad.session_id,
//                    ad.session_name,
//                    ad.parent_name,
//                    ad.parent_mobile_no,
//                    ad.fddt_id,
//                    ad.fee_type,
//                    ad.fee_id,
//                    ad.frequency_id,
//                    ad.frequency_type,
//                    ad.fa_id,
//                    ad.dc_id,
//                    ad.due_date,
//                    ad.fee_amount,
//                    ad.initial_discount,
//                    COALESCE(pd.total_additional_discount, 0) AS additional_discount, -- Changed from fd.additional_discount
//                    fdd.additional_discount_reason, -- Changed from fd.additional_discount_reason
//                    (ad.fee_amount - ad.initial_discount - COALESCE(pd.total_additional_discount, 0)) AS net_amount_after_discount,
//                    COALESCE(pd.total_penalty, 0) AS total_penalty,
//                    (ad.fee_amount - ad.initial_discount - COALESCE(pd.total_additional_discount, 0) + COALESCE(pd.total_penalty, 0)) AS total_payable_amount,
//                    COALESCE(pd.total_paid, 0) AS total_paid,
//                    (ad.fee_amount - ad.initial_discount - COALESCE(pd.total_additional_discount, 0) + COALESCE(pd.total_penalty, 0) - COALESCE(pd.total_paid, 0)) AS amount_due,
//                    COALESCE(fdd.status, 'Pending') AS status,
//                    ad.valid_from,
//                    ad.valid_to
//                FROM assignment_data ad
//                LEFT JOIN payment_data pd ON ad.fddt_id = pd.fddt_id
//                LEFT JOIN fee_deposit_details fdd
//                    ON fdd.fdd_id = pd.latest_fdd_id
//                -- Removed the JOIN to fee_deposit table since we no longer need it for additional_discount
//                WHERE ad.priority_rank = 1
//                ORDER BY ad.due_date ASC
//                """;
        String sql = """
                WITH payment_data AS (
                    SELECT
                        fdd.fddt_id,
                        SUM(fdd.amount_paid) AS total_paid,
                        SUM(fdd.penalty_amount) AS total_penalty,
                        SUM(fdd.additional_discount) AS total_additional_discount,
                        MAX(fdd.fdd_id) AS latest_fdd_id
                    FROM fee_deposit_details fdd
                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
                    WHERE fd.school_id = ?
                        AND fd.session_id = ?
                        AND fd.student_id = ?
                    GROUP BY fdd.fddt_id
                ),
                assignment_data AS (
                    SELECT
                        fddt.fa_id,
                        fa.fee_id,
                        fa.dc_id,
                        sad.school_id,
                        spd.student_id,
                        spd.gender, -- Added for gender filtering
                        sad.student_type, -- Added for student_type filtering
                        COALESCE(spd.father_name, spd.mother_name) AS parent_name,
                        spd.phone_number AS parent_mobile_no,
                        spd.first_name || ' ' || spd.last_name AS student_name,
                        sad.student_class_id,
                        mc.class_name,
                        sad.student_section_id,
                        ms.section_name,
                        sad.session_id,
                        s.academic_session AS session_name,
                        fddt.fddt_id,
                        sf.fee_type,
                        sf.frequency_id,
                        mf.frequency_type,
                        fddt.due_date,
                        fddt.fee_amount,
                        fddt.discount_amount AS initial_discount,
                        fa.valid_from,
                        fa.valid_to,
                        ROW_NUMBER() OVER (
                            PARTITION BY sf.fee_id, fddt.due_date
                            ORDER BY
                                CASE
                                    WHEN fa.student_id IS NOT NULL THEN 1
                                    WHEN fa.section_id IS NOT NULL THEN 2
                                    ELSE 3
                                END
                        ) AS priority_rank
                    FROM fee_due_date fddt
                    JOIN fee_assignment fa ON fddt.fa_id = fa.fa_id
                    JOIN student_academic_details sad
                        ON sad.school_id = ?
                        AND sad.session_id = ?
                        AND sad.student_id = ?
                    JOIN student_personal_details spd
                        ON spd.student_id = sad.student_id
                        AND spd.school_id = ?
                    JOIN session s ON sad.session_id = s.session_id
                    JOIN mst_class mc
                        ON mc.class_id = sad.student_class_id
                        AND mc.school_id = ?
                    JOIN mst_section ms
                        ON ms.section_id = sad.student_section_id
                        AND ms.school_id = ?
                    JOIN school_fees sf ON fa.fee_id = sf.fee_id
                    JOIN mst_frequency mf ON mf.frequency_id = sf.frequency_id
                    LEFT JOIN fee_assignment_exclusion fex
                        ON fa.fa_id = fex.fa_id
                        AND fex.student_id = ?
                        AND CURRENT_DATE BETWEEN fex.valid_from AND COALESCE(fex.valid_to, '9999-12-31')
                    WHERE fex.exclusion_id IS NULL
                        AND CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, '9999-12-31')
                        AND (
                            (fa.student_id = sad.student_id)
                            OR (fa.student_id IS NULL AND fa.section_id = sad.student_section_id AND fa.class_id = sad.student_class_id)
                            OR (fa.student_id IS NULL AND fa.section_id IS NULL AND fa.class_id = sad.student_class_id)
                        )
                        -- Added Old/New and Male/Female filtering (case-insensitive)
                        AND (
                            -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
                            (UPPER(sf.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
                            -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
                            OR (UPPER(sf.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
                            -- Case 3: Fee type has "- Male" suffix, check gender = 'Male' (case-insensitive)
                            OR (UPPER(sf.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
                            -- Case 4: Fee type has "- Female" suffix, check gender = 'Female' (case-insensitive)
                            OR (UPPER(sf.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
                            -- Case 5: Fee type doesn't have any gender/student_type suffix
                            OR (UPPER(sf.fee_type) NOT LIKE '% - OLD'\s
                                AND UPPER(sf.fee_type) NOT LIKE '% - NEW'
                                AND UPPER(sf.fee_type) NOT LIKE '% - MALE'
                                AND UPPER(sf.fee_type) NOT LIKE '% - FEMALE')
                        )
                )
                SELECT
                    ad.school_id,
                    ad.student_id,
                    ad.student_name,
                    ad.student_class_id,
                    ad.class_name,
                    ad.student_section_id,
                    ad.section_name,
                    ad.session_id,
                    ad.session_name,
                    ad.parent_name,
                    ad.parent_mobile_no,
                    ad.fddt_id,
                    ad.fee_type,
                    ad.fee_id,
                    ad.frequency_id,
                    ad.frequency_type,
                    ad.fa_id,
                    ad.dc_id,
                    ad.due_date,
                    ad.fee_amount,
                    ad.initial_discount,
                    COALESCE(pd.total_additional_discount, 0) AS additional_discount,
                    fdd.additional_discount_reason,
                    (ad.fee_amount - ad.initial_discount - COALESCE(pd.total_additional_discount, 0)) AS net_amount_after_discount,
                    COALESCE(pd.total_penalty, 0) AS total_penalty,
                    (ad.fee_amount - ad.initial_discount - COALESCE(pd.total_additional_discount, 0) + COALESCE(pd.total_penalty, 0)) AS total_payable_amount,
                    COALESCE(pd.total_paid, 0) AS total_paid,
                    (ad.fee_amount - ad.initial_discount - COALESCE(pd.total_additional_discount, 0) + COALESCE(pd.total_penalty, 0) - COALESCE(pd.total_paid, 0)) AS amount_due,
                    COALESCE(fdd.status, 'Pending') AS status,
                    ad.valid_from,
                    ad.valid_to
                FROM assignment_data ad
                LEFT JOIN payment_data pd ON ad.fddt_id = pd.fddt_id
                LEFT JOIN fee_deposit_details fdd
                    ON fdd.fdd_id = pd.latest_fdd_id
                WHERE ad.priority_rank = 1
                ORDER BY ad.due_date ASC
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<FeeDepositDetails> feeDepositDetails = null;

        try{
            feeDepositDetails = jdbcTemplate.query(sql, new Object[]{
                    schoolId,
                    sessionId,
                    studentId,
                    schoolId,
                    sessionId,
                    studentId,
                    schoolId,
                    schoolId,
                    schoolId,
                    studentId
                     }, new RowMapper<FeeDepositDetails>() {
                @Override
                public FeeDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeDepositDetails fdd = new FeeDepositDetails();
                    fdd.setSchoolId(rs.getInt("school_id"));
//                    fdd.setSchoolName(rs.getString("school_name"));
                    //fdd.setSchoolBuilding(rs.getString("school_building"));
                   // fdd.setSchoolState(rs.getString("school_state"));
                   // fdd.setSchoolCity(rs.getString("school_city"));
                   // fdd.setSchoolCountry(rs.getString("school_country"));
                   // fdd.setSchoolAddress(rs.getString("school_address"));
                   // fdd.setSchoolEmailAddress(rs.getString("school_email"));
                    fdd.setStudentId(rs.getInt("student_id"));
                    fdd.setStudentName(rs.getString("student_name"));
                    fdd.setClassId(rs.getInt("student_class_id"));
                    fdd.setClassName(rs.getString("class_name"));
                    fdd.setSectionId(rs.getInt("student_section_id"));
                    fdd.setSectionName(rs.getString("section_name"));
                    fdd.setSessionId(rs.getInt("session_id"));
                    fdd.setSessionName(rs.getString("session_name"));
                    // Handling parent_id as an array
                    /*java.sql.Array parentIdArray = rs.getArray("parent_id");
                    if (parentIdArray != null) {
                        Integer[] parentIds = (Integer[]) parentIdArray.getArray();
                        fdd.setParentId(Arrays.asList(parentIds)); // Convert array to list
                    } else {
                        fdd.setParentId(Collections.emptyList()); // If no parent ID is found, set empty list
                    }*/
                    fdd.setParentName(rs.getString("parent_name"));
                    fdd.setParentMobileNumber(CipherUtils.decrypt(rs.getString("parent_mobile_no")));
                   // fdd.setFdId(rs.getInt("fd_id"));
                 //   fdd.setSystemDateTime(rs.getDate("system_date_time") != null ? rs.getDate("system_date_time") : null);
                    fdd.setFddtId(rs.getInt("fddt_id"));
                    fdd.setFeeType(rs.getString("fee_type"));
                    fdd.setFeeId(rs.getInt("fee_id"));
                    fdd.setFrequencyId(rs.getInt("frequency_id"));
                    fdd.setFrequencyType(rs.getString("frequency_type"));
                    fdd.setFaId(rs.getInt("fa_id"));
                    fdd.setDcId(rs.getInt("dc_id"));
                   // fdd.setPenaltyId(rs.getInt("penalty_id"));
                  //  fdd.setPenaltyTypes(rs.getString("penalty_type"));
                    fdd.setDueDate(rs.getDate("due_date"));
                    fdd.setFeeAmount(rs.getDouble("fee_amount"));
                    fdd.setDiscountAmount(rs.getDouble("initial_discount"));
                    fdd.setAmountAfterDiscount(rs.getDouble("net_amount_after_discount"));
                    fdd.setTotalPenalty(rs.getDouble("total_penalty"));
                    fdd.setTotalPayableAmount(rs.getDouble("total_payable_amount"));
                    fdd.setTotalPaid(rs.getDouble("total_paid"));
                    fdd.setAmountDue(rs.getDouble("amount_due"));
                    fdd.setStatus(rs.getString("status"));
                    fdd.setAdditionalDiscount(rs.getDouble("additional_discount"));
                    fdd.setAdditionalDiscountReason(rs.getString("additional_discount_reason"));
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

}
