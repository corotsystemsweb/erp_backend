package com.sms.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.StudentSearchTextDao;
import com.sms.model.ParentDetails;
import com.sms.model.StudentSearchTextDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class StudentSearchTextDaoImpl implements StudentSearchTextDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
   @Override
   public List<StudentSearchTextDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception {
       String sql = """
               SELECT
                   spd.student_id,
                   spd.first_name,
                   spd.last_name,
                   mc.class_name,
                   ms.section_name,
                   spd.father_name,
                   spd.phone_number,
                   sad.registration_number,
                   COALESCE(
                       JSON_AGG(
                           JSON_BUILD_OBJECT(
                               'parentId', pd.parent_id,
                               'firstName', pd.first_name,
                               'lastName', pd.last_name
                           )
                       ) FILTER (WHERE pd.parent_id IS NOT NULL AND pd.deleted IS NOT TRUE), '[]'
                   ) AS parent_details
               FROM
                   student_personal_details AS spd
               INNER JOIN
                   student_academic_details AS sad ON spd.student_id = sad.student_id
               INNER JOIN
                   mst_class AS mc ON sad.student_class_id = mc.class_id
               INNER JOIN
                   mst_section AS ms ON sad.student_section_id = ms.section_id
               INNER JOIN
                   session AS ses ON sad.session_id = ses.session_id
               INNER JOIN
                   school_details AS sd ON spd.school_id = sd.school_id
               LEFT JOIN
                   parent_details AS pd ON pd.parent_id = ANY(spd.parent_id) AND pd.deleted IS NOT TRUE
               WHERE
                   spd.school_id = 1
                   AND sad.school_id = 1
                   AND spd.validity_end_date >= CURRENT_DATE
                   AND sad.validity_end_date >= CURRENT_DATE
                   AND spd.deleted IS NOT true
                   AND pd.deleted IS NOT true
               GROUP BY
                   spd.student_id, spd.first_name, spd.last_name, mc.class_name, ms.section_name, spd.father_name, spd.phone_number, sad.registration_number;
               """;
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<StudentSearchTextDetails> studentSearchTexDetails = null;

       try{
           studentSearchTexDetails = jdbcTemplate.query(sql, new RowMapper<StudentSearchTextDetails>() {
               @Override
               public StudentSearchTextDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   StudentSearchTextDetails sst = new StudentSearchTextDetails();
                   sst.setStudentId(rs.getInt("student_id"));
                   sst.setFirstName(rs.getString("first_name"));
                   sst.setLastName(rs.getString("last_name"));
                   sst.setClassName(rs.getString("class_name"));
                   sst.setSectionName(rs.getString("section_name"));
                   sst.setFatherName(rs.getString("father_name"));
                   sst.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                   sst.setRegistrationNumber(rs.getString("registration_number"));
                   String parentDetailsJson = rs.getString("parent_details");
                   if(parentDetailsJson != null){
                       try {
                           ObjectMapper objectMapper = new ObjectMapper();
                           List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});
                           sst.setParentDetails(parentDetailsList);
                       } catch (JsonProcessingException e) {
                           e.printStackTrace();
                           sst.setParentDetails(null);
                       }
                   }

                   // Create a combined string for first and last name (no spaces)
                   String combinedData = sst.getStudentId() + " " + sst.getFirstName() + " " + sst.getLastName() + " " + sst.getFatherName() + " " + sst.getPhoneNumber() + " " + sst.getRegistrationNumber() + " " + sst.getParentDetails();
                   combinedData = combinedData.toLowerCase();

                   // Convert the search text to lowercase
                   String searchPattern = searchText.toLowerCase();

                   // Check if the searchText exists as a substring in combinedData
                   if (combinedData.contains(searchPattern)) {
                       return sst;
                   } else {
                       return null;
                   }
               }
           }).stream().filter(Objects::nonNull).collect(Collectors.toList());
       } catch (EmptyResultDataAccessException e) {
           return null;
       } catch (Exception e) {
           e.printStackTrace();
           throw e;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return studentSearchTexDetails;
   }

    @Override
    public List<StudentSearchTextDetails> getStudentFeeDetailsBasedOnClassSectionSessionForSearchText(int classId, int sectionId, int sessionId, String searchText, String schoolCode) throws Exception {
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
                    OR (fa.class_id = sad.student_class_id AND
                    (fa.section_id = sad.student_section_id OR fa.section_id IS NULL) AND
                     fa.student_id IS NULL)
                     OR (fa.class_id = sad.student_class_id AND
                    fa.section_id IS NULL AND
                    fa.student_id IS NULL))
                    JOIN student_personal_details spd 
                    ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND sad.student_section_id = ?
                        AND spd.deleted IS NOT TRUE
                        AND NOT EXISTS (
                                    SELECT 1 FROM fee_assignment_exclusion fe
                                    WHERE fe.fa_id = fa.fa_id\s
                                      AND fe.student_id = spd.student_id
                                      AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
                                )
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
                            OR (fa.class_id = sad.student_class_id AND
                                (fa.section_id = sad.student_section_id OR fa.section_id IS NULL) AND
                                fa.student_id IS NULL)
                            OR (fa.class_id = sad.student_class_id AND
                                fa.section_id IS NULL AND
                                fa.student_id IS NULL))
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND sad.student_section_id = ?
                        AND spd.deleted IS NOT TRUE
                        AND NOT EXISTS (
                                    SELECT 1 FROM fee_assignment_exclusion fe
                                    WHERE fe.fa_id = fa.fa_id
                                      AND fe.student_id = spd.student_id
                                      AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
                                )
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
                                'lastName', pds.last_name
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
                    spd.student_id, spd.first_name, spd.last_name, mc.class_id, mc.class_name, ms.section_id,
                    ms.section_name, ses.session_id, ses.academic_session, fd.fee_details,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid
                ORDER BY spd.student_id
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<StudentSearchTextDetails> studentSearchTexDetailsList = null;

        try{
            studentSearchTexDetailsList = jdbcTemplate.query(sql, new Object[]{sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId, sessionId, classId, sectionId}, new RowMapper<StudentSearchTextDetails>() {
                @Override
                public StudentSearchTextDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentSearchTextDetails sst = new StudentSearchTextDetails();
                    sst.setSchoolId(rs.getInt("school_id"));
                    sst.setSchoolName(rs.getString("school_name"));
                    sst.setSchoolBuilding(rs.getString("school_building"));
                    sst.setSchoolAddress(rs.getString("school_address"));
                    sst.setSchoolEmailAddress(rs.getString("email_address"));
                    sst.setSchoolPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sst.setSchoolCity(rs.getString("school_city"));
                    sst.setSchoolState(rs.getString("school_state"));
                    sst.setSchoolCountry(rs.getString("school_country"));
                    sst.setSchoolZipCode(rs.getString("school_zipcode"));
                    sst.setStudentId(rs.getInt("student_id"));
                    sst.setStudentName(rs.getString("student_name"));
                    sst.setClassId(rs.getInt("class_id"));
                    sst.setClassName(rs.getString("class_name"));
                    sst.setSectionId(rs.getInt("section_id"));
                    sst.setSectionName(rs.getString("section_name"));
                    sst.setSessionId(rs.getInt("session_id"));
                    sst.setSessionName(rs.getString("academic_session"));

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

                            sst.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sst.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        sst.setParentDetails(Collections.emptyList());
                    }

                    Array feeDetailsArray = rs.getArray("fee_details");
                    if (feeDetailsArray != null) {
                        sst.setFeeDetails(Arrays.asList((String[]) rs.getArray("fee_details").getArray()));
                    } else {
                        sst.setFeeDetails(Collections.emptyList());
                    }

                    sst.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                    sst.setTotalDiscount(rs.getDouble("total_discount"));
                    sst.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                    sst.setTotalPenalty(rs.getDouble("total_penalty"));
                    sst.setTotalPaid(rs.getDouble("total_paid"));
                    sst.setTotalDue(rs.getDouble("total_due"));

                    // Build combined data for search
                    StringBuilder combinedDataBuilder = new StringBuilder();
                    combinedDataBuilder.append(sst.getStudentName()).append(" ");
                    // Add parent details to combined data if available
                    if (sst.getParentDetails() != null) {
                        for (ParentDetails parent : sst.getParentDetails()) {
                            combinedDataBuilder.append(parent.getFirstName()).append(" ").append(parent.getLastName()).append(" ");
                        }
                    }

                    String combinedData = combinedDataBuilder.toString().toLowerCase();
                    String searchPattern = searchText.toLowerCase();

                    // Check if the search text is a substring of the combined data
                    if (combinedData.contains(searchPattern)) {
                        return sst;
                    } else {
                        return null;
                    }
                }

            }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentSearchTexDetailsList;
    }

    @Override
    public List<StudentSearchTextDetails> getFeeDatilsBasedOnSession(int sessionId, String searchText, String schoolCode) throws Exception {
        String sql = """
                WITH FeeDetailsIntermediate AS (
                    SELECT
                        spd.student_id,
                        spd.gender,
                        sad.student_type,
                        fs.fee_type,
                        fdd.fee_amount,
                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS fee_count,
                        SUM(fdd.discount_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS total_discount_amount,
                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) * fdd.fee_amount AS total_fee_for_type
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN school_fees fs ON fa.fee_id = fs.fee_id
                    JOIN student_academic_details sad\s
                        ON sad.session_id = fa.session_id\s
                        AND sad.student_class_id = fa.class_id
                        -- Handle all three assignment levels
                        AND (
                            -- Student-level: fa.student_id matches specific student
                            (fa.student_id IS NOT NULL AND fa.student_id = sad.student_id)
                            -- Section-level: fa.student_id is NULL but fa.section_id matches
                            OR (fa.student_id IS NULL AND fa.section_id IS NOT NULL AND fa.section_id = sad.student_section_id)
                            -- Class-level: both student_id and section_id are NULL
                            OR (fa.student_id IS NULL AND fa.section_id IS NULL)
                        )
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND spd.deleted IS NOT TRUE
                        AND NOT EXISTS (
                            SELECT 1 FROM fee_assignment_exclusion fe
                            WHERE fe.fa_id = fa.fa_id
                                AND fe.student_id = spd.student_id
                                AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
                        )
                        -- Filter fees based on student_type and gender (CASE-INSENSITIVE)
                        AND (
                            -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
                            (UPPER(fs.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
                            -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
                            OR (UPPER(fs.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
                            -- Case 3: Fee type has "- Male" suffix, check gender = 'Male' (case-insensitive)
                            OR (UPPER(fs.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
                            -- Case 4: Fee type has "- Female" suffix, check gender = 'Female' (case-insensitive)
                            OR (UPPER(fs.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
                            -- Case 5: Fee type doesn't have any gender/student_type suffix
                            OR (UPPER(fs.fee_type) NOT LIKE '% - OLD'\s
                                AND UPPER(fs.fee_type) NOT LIKE '% - NEW'
                                AND UPPER(fs.fee_type) NOT LIKE '% - MALE'
                                AND UPPER(fs.fee_type) NOT LIKE '% - FEMALE')
                        )
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
                    JOIN school_fees fs ON fa.fee_id = fs.fee_id
                    JOIN student_academic_details sad\s
                        ON sad.session_id = fa.session_id\s
                        AND sad.student_class_id = fa.class_id
                        -- Handle all three assignment levels
                        AND (
                            -- Student-level: fa.student_id matches specific student
                            (fa.student_id IS NOT NULL AND fa.student_id = sad.student_id)
                            -- Section-level: fa.student_id is NULL but fa.section_id matches
                            OR (fa.student_id IS NULL AND fa.section_id IS NOT NULL AND fa.section_id = sad.student_section_id)
                            -- Class-level: both student_id and section_id are NULL
                            OR (fa.student_id IS NULL AND fa.section_id IS NULL)
                        )
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND spd.deleted IS NOT TRUE
                        AND NOT EXISTS (
                            SELECT 1 FROM fee_assignment_exclusion fe
                            WHERE fe.fa_id = fa.fa_id
                                AND fe.student_id = spd.student_id
                                AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
                        )
                        -- Filter fees based on student_type and gender (CASE-INSENSITIVE - same as above)
                        AND (
                            (UPPER(fs.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
                            OR (UPPER(fs.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
                            OR (UPPER(fs.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
                            OR (UPPER(fs.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
                            OR (UPPER(fs.fee_type) NOT LIKE '% - OLD'\s
                                AND UPPER(fs.fee_type) NOT LIKE '% - NEW'
                                AND UPPER(fs.fee_type) NOT LIKE '% - MALE'
                                AND UPPER(fs.fee_type) NOT LIKE '% - FEMALE')
                        )
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
                ),
                AdditionalDiscounts AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fdd.additional_discount), 0.0) AS total_additional_discount
                    FROM
                        fee_deposit_details fdd
                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
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
                    sad.admission_no,
                    sad.registration_number,
                    mc.class_id,
                    mc.class_name,
                    ms.section_id,
                    ms.section_name,
                    ses.session_id,
                    ses.academic_session,
                    spd.gender,
                    sad.student_type,
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
                    fd.fee_details AS fee_details,
                    ft.total_fee_assigned AS total_fee_assigned,
                    (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                    ft.total_fee_assigned
                        - ft.total_discount_amount  -- Original discounts
                        + COALESCE(pd.total_penalty, 0)  -- Add penalties
                        - COALESCE(ad.total_additional_discount, 0)  -- Subtract additional discounts
                        AS gross_student_fee,
                    COALESCE(pd.total_penalty, 0.0) AS total_penalty,
                    COALESCE(pdp.total_paid, 0.0) AS total_paid,
                    (ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0)
                        - COALESCE(ad.total_additional_discount, 0))
                        - COALESCE(pdp.total_paid, 0.0) AS total_due
                FROM
                    student_personal_details spd
                JOIN student_academic_details sad ON spd.student_id = sad.student_id
                JOIN mst_class mc ON sad.student_class_id = mc.class_id
                JOIN mst_section ms ON sad.student_section_id = ms.section_id
                JOIN school_details sd ON spd.school_id = sd.school_id
                JOIN session ses ON sad.session_id = ses.session_id
                LEFT JOIN FeeDetails fd ON spd.student_id = fd.student_id
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                LEFT JOIN parent_details pds ON pds.parent_id = ANY(spd.parent_id)
                WHERE
                    sad.session_id = ?
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                    mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                    spd.gender, sad.student_type,
                    fd.fee_details, ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                    ad.total_additional_discount
                ORDER BY spd.student_id
                """;
//        String sql = """
//                WITH FeeDetailsIntermediate AS (
//                    SELECT
//                        spd.student_id,
//                        spd.gender,
//                        sad.student_type,
//                        fs.fee_type,
//                        fdd.fee_amount,
//                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS fee_count,
//                        SUM(fdd.discount_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) AS total_discount_amount,
//                        COUNT(fdd.fee_amount) OVER (PARTITION BY spd.student_id, fs.fee_type) * fdd.fee_amount AS total_fee_for_type
//                    FROM
//                        fee_assignment fa
//                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
//                    JOIN school_fees fs ON fa.fee_id = fs.fee_id
//                    JOIN student_academic_details sad
//                        ON sad.session_id = fa.session_id
//                        AND sad.student_class_id = fa.class_id
//                        -- Handle all three assignment levels
//                        AND (
//                            -- Student-level: fa.student_id matches specific student
//                            (fa.student_id IS NOT NULL AND fa.student_id = sad.student_id)
//                            -- Section-level: fa.student_id is NULL but fa.section_id matches
//                            OR (fa.student_id IS NULL AND fa.section_id IS NOT NULL AND fa.section_id = sad.student_section_id)
//                            -- Class-level: both student_id and section_id are NULL
//                            OR (fa.student_id IS NULL AND fa.section_id IS NULL)
//                        )
//                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
//                    WHERE
//                        sad.session_id = ?
//                        AND spd.deleted IS NOT TRUE
//                        AND NOT EXISTS (
//                            SELECT 1 FROM fee_assignment_exclusion fe
//                            WHERE fe.fa_id = fa.fa_id
//                                AND fe.student_id = spd.student_id
//                                AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
//                        )
//                        -- Filter fees based on student_type and gender (CASE-INSENSITIVE)
//                        AND (
//                            -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
//                            (UPPER(fs.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
//                            -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
//                            OR (UPPER(fs.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
//                            -- Case 3: Fee type has "- Male" suffix, check gender = 'Male' (case-insensitive)
//                            OR (UPPER(fs.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
//                            -- Case 4: Fee type has "- Female" suffix, check gender = 'Female' (case-insensitive)
//                            OR (UPPER(fs.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
//                            -- Case 5: Fee type doesn't have any gender/student_type suffix
//                            OR (UPPER(fs.fee_type) NOT LIKE '% - OLD'
//                                AND UPPER(fs.fee_type) NOT LIKE '% - NEW'
//                                AND UPPER(fs.fee_type) NOT LIKE '% - MALE'
//                                AND UPPER(fs.fee_type) NOT LIKE '% - FEMALE')
//                        )
//                ),
//                TransportFeeDetails AS (
//                    SELECT
//                        std.student_id,
//                        -- Calculate number of months between start_date and end_date
//                        EXTRACT(YEAR FROM AGE(std.end_date, std.start_date)) * 12\s
//                        + EXTRACT(MONTH FROM AGE(std.end_date, std.start_date)) + 1 AS total_months,
//                        -- Calculate total transport fee (monthly fee * number of months)
//                        std.fee * (
//                            EXTRACT(YEAR FROM AGE(std.end_date, std.start_date)) * 12\s
//                            + EXTRACT(MONTH FROM AGE(std.end_date, std.start_date)) + 1
//                        ) AS total_transport_fee,
//                        -- Format transport fee details similar to fee_details format
//                        CONCAT(
//                            'Fee Type: Transport Fee',
//                            ', Fee Amount: ',
//                            std.fee::text, ' * ',\s
//                            (EXTRACT(YEAR FROM AGE(std.end_date, std.start_date)) * 12\s
//                             + EXTRACT(MONTH FROM AGE(std.end_date, std.start_date)) + 1)::text,
//                            ' months = ',
//                            (std.fee * (
//                                EXTRACT(YEAR FROM AGE(std.end_date, std.start_date)) * 12\s
//                                + EXTRACT(MONTH FROM AGE(std.end_date, std.start_date)) + 1
//                            ))::text,
//                            ', Total Discount Amount: 0'
//                        ) AS transport_fee_detail
//                    FROM student_transport_details std
//                    WHERE std.session_id = ?
//                        AND std.status = 'ACTIVE'
//                        -- Ensure dates are valid
//                        AND std.start_date IS NOT NULL
//                        AND std.end_date IS NOT NULL
//                        AND std.start_date <= std.end_date
//                ),
//                FeeDetails AS (
//                    SELECT
//                        COALESCE(fdi.student_id, tfd.student_id) AS student_id,
//                        -- Combine regular fee details with transport fee details
//                        ARRAY_AGG(
//                            DISTINCT CONCAT(
//                                'Fee Type: ', fdi.fee_type,
//                                ', Fee Amount: ',
//                                CASE
//                                    WHEN fdi.fee_count > 1 THEN CONCAT(fdi.fee_count, '*', fdi.fee_amount::text, ' = ', fdi.total_fee_for_type::text)
//                                    ELSE fdi.total_fee_for_type::text
//                                END,
//                                ', Total Discount Amount: ', fdi.total_discount_amount::text
//                            )
//                        ) FILTER (WHERE fdi.student_id IS NOT NULL)
//                        ||\s
//                        COALESCE(
//                            ARRAY_AGG(
//                                DISTINCT tfd.transport_fee_detail
//                            ) FILTER (WHERE tfd.student_id IS NOT NULL),\s
//                            '{}'::text[]
//                        ) AS fee_details,
//                        COALESCE(SUM(fdi.total_fee_for_type), 0) AS total_fee_amount,
//                        COALESCE(SUM(fdi.total_discount_amount), 0) AS total_discount_amount
//                    FROM FeeDetailsIntermediate fdi
//                    FULL OUTER JOIN TransportFeeDetails tfd ON fdi.student_id = tfd.student_id
//                    GROUP BY COALESCE(fdi.student_id, tfd.student_id)
//                ),
//                FeeTotals AS (
//                    SELECT
//                        COALESCE(ft.student_id, tfd.student_id) AS student_id,
//                        COALESCE(ft.regular_fee_assigned, 0) + COALESCE(tfd.total_transport_fee, 0) AS total_fee_assigned,
//                        COALESCE(ft.total_discount_amount, 0) AS total_discount_amount
//                    FROM (
//                        SELECT
//                            spd.student_id,
//                            SUM(fdd.fee_amount) AS regular_fee_assigned,
//                            SUM(fdd.discount_amount) AS total_discount_amount
//                        FROM
//                            fee_assignment fa
//                        JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
//                        JOIN school_fees fs ON fa.fee_id = fs.fee_id
//                        JOIN student_academic_details sad
//                            ON sad.session_id = fa.session_id
//                            AND sad.student_class_id = fa.class_id
//                            -- Handle all three assignment levels
//                            AND (
//                                -- Student-level: fa.student_id matches specific student
//                                (fa.student_id IS NOT NULL AND fa.student_id = sad.student_id)
//                                -- Section-level: fa.student_id is NULL but fa.section_id matches
//                                OR (fa.student_id IS NULL AND fa.section_id IS NOT NULL AND fa.section_id = sad.student_section_id)
//                                -- Class-level: both student_id and section_id are NULL
//                                OR (fa.student_id IS NULL AND fa.section_id IS NULL)
//                            )
//                        JOIN student_personal_details spd ON sad.student_id = spd.student_id
//                        WHERE
//                            sad.session_id = ?
//                            AND spd.deleted IS NOT TRUE
//                            AND NOT EXISTS (
//                                SELECT 1 FROM fee_assignment_exclusion fe
//                                WHERE fe.fa_id = fa.fa_id
//                                    AND fe.student_id = spd.student_id
//                                    AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
//                            )
//                            -- Filter fees based on student_type and gender (CASE-INSENSITIVE - same as above)
//                            AND (
//                                (UPPER(fs.fee_type) LIKE '% - OLD' AND sad.student_type = 'Old')
//                                OR (UPPER(fs.fee_type) LIKE '% - NEW' AND sad.student_type = 'New')
//                                OR (UPPER(fs.fee_type) LIKE '% - MALE' AND UPPER(spd.gender) = 'MALE')
//                                OR (UPPER(fs.fee_type) LIKE '% - FEMALE' AND UPPER(spd.gender) = 'FEMALE')
//                                OR (UPPER(fs.fee_type) NOT LIKE '% - OLD'
//                                    AND UPPER(fs.fee_type) NOT LIKE '% - NEW'
//                                    AND UPPER(fs.fee_type) NOT LIKE '% - MALE'
//                                    AND UPPER(fs.fee_type) NOT LIKE '% - FEMALE')
//                            )
//                        GROUP BY spd.student_id
//                    ) ft
//                    FULL OUTER JOIN (
//                        SELECT\s
//                            student_id,
//                            SUM(total_transport_fee) AS total_transport_fee
//                        FROM TransportFeeDetails
//                        GROUP BY student_id
//                    ) tfd ON ft.student_id = tfd.student_id
//                ),
//                PenaltyDetails AS (
//                    SELECT
//                        fd.student_id,
//                        COALESCE(SUM(fdd.penalty_amount), 0.0) AS total_penalty
//                    FROM
//                        fee_deposit_details fdd
//                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
//                    WHERE fd.session_id = ?
//                    GROUP BY fd.student_id
//                ),
//                PaidDetails AS (
//                    SELECT
//                        fd.student_id,
//                        COALESCE(SUM(fd.total_amount_paid), 0.0) AS total_paid
//                    FROM fee_deposit fd
//                    WHERE fd.session_id = ?
//                    GROUP BY fd.student_id
//                ),
//                AdditionalDiscounts AS (
//                    SELECT
//                        fd.student_id,
//                        COALESCE(SUM(fdd.additional_discount), 0.0) AS total_additional_discount
//                    FROM
//                        fee_deposit_details fdd
//                    JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
//                    WHERE fd.session_id = ?
//                    GROUP BY fd.student_id
//                )
//                SELECT
//                    sd.school_id,
//                    sd.school_name,
//                    sd.school_building,
//                    sd.school_address,
//                    sd.email_address,
//                    sd.phone_number,
//                    sd.school_city,
//                    sd.school_state,
//                    sd.school_country,
//                    sd.school_zipcode,
//                    spd.student_id,
//                    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
//                    sad.admission_no,
//                    sad.registration_number,
//                    mc.class_id,
//                    mc.class_name,
//                    ms.section_id,
//                    ms.section_name,
//                    ses.session_id,
//                    ses.academic_session,
//                    spd.gender,
//                    sad.student_type,
//                    COALESCE(
//                        JSON_AGG(
//                            JSON_BUILD_OBJECT(
//                                'parentId', pds.parent_id,
//                                'firstName', pds.first_name,
//                                'lastName', pds.last_name,
//                                'phoneNumber', pds.phone_number,
//                                'emergencyPhoneNumber', pds.emergency_phone_number,
//                                'whatsappNumber', pds.whatsapp_no,
//                                'parentType', pds.parent_type
//                            )
//                        ) FILTER (WHERE pds.parent_id IS NOT NULL AND pds.deleted IS NOT TRUE), '[]'
//                    ) AS parent_details,
//                    COALESCE(fd.fee_details, '{}'::text[]) AS fee_details,
//                    COALESCE(ft.total_fee_assigned, 0) AS total_fee_assigned,
//                    COALESCE(ft.total_discount_amount, 0) + COALESCE(ad.total_additional_discount, 0) AS total_discount,
//                    COALESCE(ft.total_fee_assigned, 0)
//                        - COALESCE(ft.total_discount_amount, 0)  -- Original discounts
//                        + COALESCE(pd.total_penalty, 0)  -- Add penalties
//                        - COALESCE(ad.total_additional_discount, 0)  -- Subtract additional discounts
//                        AS gross_student_fee,
//                    COALESCE(pd.total_penalty, 0.0) AS total_penalty,
//                    COALESCE(pdp.total_paid, 0.0) AS total_paid,
//                    (COALESCE(ft.total_fee_assigned, 0)
//                        - COALESCE(ft.total_discount_amount, 0)
//                        + COALESCE(pd.total_penalty, 0)
//                        - COALESCE(ad.total_additional_discount, 0))
//                        - COALESCE(pdp.total_paid, 0.0) AS total_due
//                FROM
//                    student_personal_details spd
//                JOIN student_academic_details sad ON spd.student_id = sad.student_id
//                JOIN mst_class mc ON sad.student_class_id = mc.class_id
//                JOIN mst_section ms ON sad.student_section_id = ms.section_id
//                JOIN school_details sd ON spd.school_id = sd.school_id
//                JOIN session ses ON sad.session_id = ses.session_id
//                LEFT JOIN FeeDetails fd ON spd.student_id = fd.student_id
//                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
//                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
//                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
//                LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
//                LEFT JOIN parent_details pds ON pds.parent_id = ANY(spd.parent_id)
//                WHERE
//                    sad.session_id = ?
//                    AND spd.deleted IS NOT TRUE
//                    -- Include students who have either regular fees OR transport fees
//                    AND (ft.total_fee_assigned > 0 OR EXISTS (
//                        SELECT 1 FROM TransportFeeDetails tfd\s
//                        WHERE tfd.student_id = spd.student_id
//                    ))
//                GROUP BY
//                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
//                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
//                    spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
//                    mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
//                    spd.gender, sad.student_type,
//                    fd.fee_details, ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
//                    ad.total_additional_discount
//                ORDER BY spd.student_id;
//                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<StudentSearchTextDetails> studentSearchTexDetailsList = null;

        try{
            studentSearchTexDetailsList = jdbcTemplate.query(sql, new Object[]{sessionId,sessionId, sessionId, sessionId, sessionId, sessionId}, new RowMapper<StudentSearchTextDetails>() {
            //studentSearchTexDetailsList = jdbcTemplate.query(sql, new Object[]{sessionId,sessionId, sessionId, sessionId, sessionId, sessionId, sessionId}, new RowMapper<StudentSearchTextDetails>() {
                @Override
                public StudentSearchTextDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentSearchTextDetails sst = new StudentSearchTextDetails();
                    sst.setSchoolId(rs.getInt("school_id"));
                    sst.setSchoolName(rs.getString("school_name"));
                    sst.setSchoolBuilding(rs.getString("school_building"));
                    sst.setSchoolAddress(rs.getString("school_address"));
                    sst.setSchoolEmailAddress(rs.getString("email_address"));
                    sst.setSchoolPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sst.setSchoolCity(rs.getString("school_city"));
                    sst.setSchoolState(rs.getString("school_state"));
                    sst.setSchoolCountry(rs.getString("school_country"));
                    sst.setSchoolZipCode(rs.getString("school_zipcode"));
                    sst.setStudentId(rs.getInt("student_id"));
                    sst.setStudentName(rs.getString("student_name"));
                    sst.setAdmissionNumber(rs.getString("admission_no"));
                    sst.setRegistrationNumber(rs.getString("registration_number"));
                    sst.setClassId(rs.getInt("class_id"));
                    sst.setClassName(rs.getString("class_name"));
                    sst.setSectionId(rs.getInt("section_id"));
                    sst.setSectionName(rs.getString("section_name"));
                    sst.setSessionId(rs.getInt("session_id"));
                    sst.setSessionName(rs.getString("academic_session"));
                    sst.setGender(rs.getString("gender"));
                    sst.setStudentType(rs.getString("student_type"));

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
                                if (parent.getEmergencyPhoneNumber() != null) {
                                    parent.setEmergencyPhoneNumber(CipherUtils.decrypt(parent.getEmergencyPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                            }

                            sst.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sst.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        sst.setParentDetails(Collections.emptyList());
                    }

                    Array feeDetailsArray = rs.getArray("fee_details");
                    if (feeDetailsArray != null) {
                        sst.setFeeDetails(Arrays.asList((String[]) rs.getArray("fee_details").getArray()));
                    } else {
                        sst.setFeeDetails(Collections.emptyList());
                    }

                    sst.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                    sst.setTotalDiscount(rs.getDouble("total_discount"));
                    sst.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                    sst.setTotalPenalty(rs.getDouble("total_penalty"));
                    sst.setTotalPaid(rs.getDouble("total_paid"));
                    sst.setTotalDue(rs.getDouble("total_due"));

                    // Build combined data for search
                    StringBuilder combinedDataBuilder = new StringBuilder();
                    if(sst.getRegistrationNumber() != null){
                        combinedDataBuilder.append(sst.getRegistrationNumber()).append(" ");
                    }
                    combinedDataBuilder.append(sst.getStudentName()).append(" ");

                    // Add parent details to combined data if available
                    if (sst.getParentDetails() != null) {
                        for (ParentDetails parent : sst.getParentDetails()) {
                            combinedDataBuilder.append(parent.getFirstName()).append(" ").append(parent.getLastName()).append(" ");
                        }
                    }

                    String combinedData = combinedDataBuilder.toString().toLowerCase();
                    String searchPattern = searchText.toLowerCase();

                    // Check if the search text is a substring of the combined data
                    if (combinedData.contains(searchPattern)) {
                        return sst;
                    } else {
                        return null;
                    }
                }

            }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentSearchTexDetailsList;
    }
}
