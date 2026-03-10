/*
package com.sms.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.FeeReportDAO;
import com.sms.model.FeeSummary;
import com.sms.model.MonthWiseFeeDueDetails;
import com.sms.model.PendingFee;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FeeReportDAOImpl implements FeeReportDAO {
    private final JdbcTemplate jdbcTemplate;

    public FeeReportDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FeeSummary> getFeeSummary(String reportType, LocalDate startDate, LocalDate endDate, String schoolCode) {
        JdbcTemplate schoolJdbcTemplate = null;  // Local variable for school-specific template
        try {
            String sql = buildQuery(reportType);
            schoolJdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);  // Get school-specific template
            return schoolJdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(FeeSummary.class),
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();  // Better to return empty list than null
        } finally {
            // Close only the school-specific template, not the injected one
            if (schoolJdbcTemplate != null) {
                DatabaseUtil.closeDataSource(schoolJdbcTemplate);  // Close using school code
            }
        }
    }

    private String buildQuery(String reportType) {
        return switch (reportType.toUpperCase()) {
            case "DAILY" -> """
                    SELECT DATE(fd.system_date_time) AS period,
                           pm.payment_type AS payment_mode,
                           SUM(fd.total_amount_paid) AS total_collected
                    FROM fee_deposit fd
                    JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                    WHERE fd.system_date_time BETWEEN ? AND ?
                    GROUP BY DATE(fd.system_date_time), pm.payment_type
                    ORDER BY period DESC
                    """;

            case "MONTHLY" -> """
                    SELECT TO_CHAR(fd.system_date_time, 'YYYY-MM') AS period,
                           pm.payment_type AS payment_mode,
                           SUM(fd.total_amount_paid) AS total_collected
                    FROM fee_deposit fd
                    JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                    WHERE fd.system_date_time BETWEEN ? AND ?
                    GROUP BY TO_CHAR(fd.system_date_time, 'YYYY-MM'), pm.payment_type
                    ORDER BY period DESC
                    """;

            case "YEARLY" -> """
                    SELECT s.academic_session AS period,
                           pm.payment_type AS payment_mode,
                           SUM(fd.total_amount_paid) AS total_collected
                    FROM fee_deposit fd
                    JOIN session s ON fd.session_id = s.session_id
                    JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                    WHERE fd.system_date_time BETWEEN ? AND ?
                    GROUP BY s.academic_session, pm.payment_type
                    ORDER BY s.academic_session DESC
                    """;

            default -> throw new IllegalArgumentException("Invalid report type: " + reportType);
        };
    }

    @Override
    public List<Map<String, Object>> getClassPendingFees(Long classId, Long sessionId, String schoolCode) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = """
                WITH FeeTotals AS (
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
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND fdd.due_date <= '2025-07-14'
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
                    ft.total_fee_assigned,
                    (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                    ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0)
                        - COALESCE(ad.total_additional_discount, 0)
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
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                WHERE
                    sad.session_id = ?
                    AND sad.student_class_id = ?
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                    mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                    ad.total_additional_discount
                ORDER BY spd.student_id;
                """;
        return schoolJdbc.queryForList(sql,sessionId,classId,sessionId,sessionId,sessionId,sessionId,classId);
    }

    @Override
    public List<Map<String, Object>> getSectionPendingFees(Long classId, Long sectionId, Long sessionId, String schoolCode) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = """
                 WITH FeeTotals AS (
                    SELECT
                        spd.student_id,
                        SUM(fdd.fee_amount) AS total_fee_assigned,
                        SUM(fdd.discount_amount) AS total_discount_amount
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN student_academic_details sad
                        ON (
                            fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0)
                        )
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
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
                    ft.total_fee_assigned,
                    (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                    ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0)
                        - COALESCE(ad.total_additional_discount, 0)
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
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                WHERE
                    sad.session_id = ?
                    AND sad.student_class_id = ?
                    AND (sad.student_section_id = ? OR ? IS NULL)  -- Optional section filter
                   -- AND (spd.student_id = ? OR ? IS NULL)           -- Optional student filter
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                    mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                    ad.total_additional_discount
                ORDER BY spd.student_id
                """;
        return schoolJdbc.queryForList(sql, sessionId,classId, sessionId,sessionId,sessionId,sessionId,classId,sectionId,sectionId);
    }

    @Override
    public List<Map<String, Object>> getStudentPendingFees(Long classId, Long sectionId,Long studentId, Long sessionId, String schoolCode) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = """
                        WITH FeeTotals AS (
                            SELECT
                                spd.student_id,
                                SUM(fdd.fee_amount) AS total_fee_assigned,
                                SUM(fdd.discount_amount) AS total_discount_amount
                            FROM
                                fee_assignment fa
                            JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                            JOIN student_academic_details sad
                                ON (
                                    fa.student_id = sad.student_id
                                    OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id = 0)
                                    OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0)
                                )
                            JOIN student_personal_details spd ON sad.student_id = spd.student_id
                            WHERE
                                sad.session_id = ?
                                AND sad.student_class_id = ?
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
                            ft.total_fee_assigned,
                            (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                            ft.total_fee_assigned
                                - ft.total_discount_amount
                                + COALESCE(pd.total_penalty, 0)
                                - COALESCE(ad.total_additional_discount, 0)
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
                        LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                        LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                        LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                        LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                        WHERE
                            sad.session_id = ?
                            AND sad.student_class_id = ?
                            AND (sad.student_section_id = ? OR ? IS NULL)  -- Optional section filter
                           AND (spd.student_id = ? OR ? IS NULL)           -- Optional student filter
                            AND spd.deleted IS NOT TRUE
                        GROUP BY
                            sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                            sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                            spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                            mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                            ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                            ad.total_additional_discount
                        ORDER BY spd.student_id
                """;
        return schoolJdbc.queryForList(sql, sessionId, classId,sessionId,sessionId,sessionId,sessionId,classId,sectionId,sectionId,studentId,studentId);
    }
//////changed here
    @Override
    public List<Map<String, Object>> getDemandSlips(
            Long classId, Long sectionId, Long studentId,
            Long sessionId, String schoolCode, LocalDate cutoffDate
    ) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(schoolJdbc);

        String baseSql = """
    SELECT
        s.student_id,
        s.first_name || ' ' || s.last_name AS student_name,
        c.class_name,
        sec.section_name,
        sess.academic_session,
        fdd.due_date,
        sf.fee_type,
        fdd.fee_amount,
        fdd.discount_amount,
        COALESCE(fdp.total_paid, 0) AS paid_amount,
        COALESCE(fdp.total_additional_discount, 0) AS additional_discount,
        (
            fdd.fee_amount 
            - fdd.discount_amount 
            - COALESCE(fdp.total_additional_discount, 0)
            - COALESCE(fdp.total_paid, 0)
        ) AS pending_amount
    FROM student_personal_details s
    JOIN student_academic_details acad ON s.student_id = acad.student_id
    JOIN mst_class c ON acad.student_class_id = c.class_id
    JOIN mst_section sec ON acad.student_section_id = sec.section_id
    JOIN session sess ON acad.session_id = sess.session_id
    JOIN fee_assignment fa ON (
        fa.session_id = acad.session_id
        AND (
            fa.student_id = s.student_id
            OR (fa.student_id = 0 AND fa.section_id = acad.student_section_id)
            OR (fa.student_id = 0 AND fa.section_id = 0 AND fa.class_id = acad.student_class_id)
        )
    )
    JOIN school_fees sf ON fa.fee_id = sf.fee_id
    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
    LEFT JOIN (
        SELECT
            fdd.fddt_id,
            fd.student_id,
            SUM(fdd.amount_paid) AS total_paid,
            SUM(fdd.additional_discount) AS total_additional_discount
        FROM fee_deposit_details fdd
        JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
        GROUP BY fdd.fddt_id, fd.student_id
    ) fdp ON fdd.fddt_id = fdp.fddt_id AND s.student_id = fdp.student_id
""";


        List<String> whereClauses = new ArrayList<>();
        whereClauses.add("acad.session_id = :sessionId");

        if (classId != null) whereClauses.add("c.class_id = :classId");
        if (sectionId != null) whereClauses.add("sec.section_id = :sectionId");
        if (studentId != null) whereClauses.add("s.student_id = :studentId");
        if (cutoffDate != null) whereClauses.add("fdd.due_date <= :cutoffDate");

        // Pending amount check
        whereClauses.add("(fdd.fee_amount - fdd.discount_amount - COALESCE(fdp.total_paid, 0)) > 0");

        String whereClause = String.join(" AND ", whereClauses);
        String sql = baseSql + " WHERE " + whereClause + " ORDER BY s.student_id, fdd.due_date";

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", sessionId);
        if (classId != null) params.put("classId", classId);
        if (sectionId != null) params.put("sectionId", sectionId);
        if (studentId != null) params.put("studentId", studentId);
        if (cutoffDate != null) params.put("cutoffDate", cutoffDate);

        return namedJdbc.queryForList(sql, params);
    }

    @Override
    public List<FeeSummary> getFeeSummaryByClass(String schoolCode) throws Exception {
        String sql = """
                SELECT
                    mc.class_name,
                    ms.section_name,
                    SUM(fd.total_amount_paid) AS total_deposit,
                   COUNT(fd.student_id) AS total_students
                  --  STRING_AGG(DISTINCT pm.payment_type, ', ') AS payment_methods_used
                FROM fee_deposit fd
                JOIN mst_class mc\s
                    ON fd.class_id = mc.class_id\s
                  --  AND fd.school_id = mc.school_id
                JOIN mst_section ms\s
                    ON fd.section_id = ms.section_id\s
                  --  AND fd.school_id = ms.school_id
                WHERE DATE(fd.payment_date) = CURRENT_DATE
                GROUP BY\s
                    mc.class_name,
                    ms.section_name,
                    mc.class_id,
                    ms.section_id,
                    fd.school_id
                ORDER BY total_deposit DESC;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeSummary> classWiseFee = null;
        try{
            classWiseFee = jdbcTemplate.query(sql, new RowMapper<FeeSummary>() {
                @Override
                public FeeSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeSummary sd = new FeeSummary();
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setTodayCollection(rs.getDouble("total_deposit"));
                    sd.setStudentCount(rs.getDouble("total_students"));
                    return sd;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classWiseFee;
    }

    @Override
    public List<MonthWiseFeeDueDetails> fetchMonthlyDueDetails(int sessionId, int classId,
                                                               Integer sectionId, Integer studentId,
                                                               String monthFilter, String schoolCode) {
        String sql = """
    WITH StudentList AS (
        SELECT 
            spd.student_id,
            CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
            mc.class_name,
            ms.section_name,
            ms.section_id,
            ses.academic_session,
            spd.school_id,
            sd.school_name
        FROM student_personal_details spd
        JOIN student_academic_details sad ON spd.student_id = sad.student_id
        JOIN mst_class mc ON sad.student_class_id = mc.class_id
        JOIN mst_section ms ON sad.student_section_id = ms.section_id
        JOIN session ses ON sad.session_id = ses.session_id
        JOIN school_details sd ON spd.school_id = sd.school_id
        WHERE sad.session_id = ?
          AND sad.student_class_id = ?
          AND (?::int IS NULL OR sad.student_section_id = ?)
          AND (?::int IS NULL OR spd.student_id = ?)
          AND spd.deleted IS NOT TRUE
    ),
    AllMonths AS (
        SELECT 
            TO_CHAR(due_date, 'Mon YYYY') AS month_display,
            DATE_TRUNC('month', due_date) AS month_start
        FROM (
            SELECT GENERATE_SERIES(
                DATE_TRUNC('month', MIN(due_date)),
                DATE_TRUNC('month', MAX(due_date)),
                INTERVAL '1 month'
            ) AS due_date
            FROM fee_due_date
            WHERE school_id = (SELECT school_id FROM StudentList LIMIT 1)
        ) dates
        WHERE ?::text IS NULL OR TO_CHAR(due_date, 'Mon YYYY') = ?
    ),
    StudentMonths AS (
        SELECT 
            sl.student_id,
            am.month_display,
            am.month_start
        FROM StudentList sl
        CROSS JOIN AllMonths am
    ),
    FeeDetails AS (
        SELECT
            spd.student_id,
            TO_CHAR(fdd.due_date, 'Mon YYYY') AS month_display,
            DATE_TRUNC('month', fdd.due_date) AS month_start,
            SUM(fdd.fee_amount) AS assigned_fees,
            SUM(fdd.discount_amount) AS discount
        FROM fee_assignment fa
        JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
        JOIN student_academic_details sad ON (
            fa.student_id = sad.student_id
            OR (fa.class_id = sad.student_class_id AND fa.section_id = COALESCE(?::int, sad.student_section_id) AND fa.student_id = 0)
            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0)
        )
        JOIN student_personal_details spd ON sad.student_id = spd.student_id
        WHERE sad.session_id = ?
          AND sad.student_class_id = ?
          AND (?::int IS NULL OR sad.student_section_id = ?)
          AND (?::int IS NULL OR spd.student_id = ?)
          AND spd.deleted IS NOT TRUE
          AND (?::text IS NULL OR TO_CHAR(fdd.due_date, 'Mon YYYY') = ?)
        GROUP BY spd.student_id, month_display, month_start
    ),
    PaymentDetails AS (
        SELECT
            fd.student_id,
            TO_CHAR(fddt.due_date, 'Mon YYYY') AS month_display,
            DATE_TRUNC('month', fddt.due_date) AS month_start,
            SUM(fdd.amount_paid) AS amount_paid,
            SUM(COALESCE(fdd.additional_discount, 0)) AS additional_discount,
            SUM(COALESCE(fdd.penalty_amount, 0)) AS penalty
        FROM fee_deposit fd
        JOIN fee_deposit_details fdd ON fd.fd_id = fdd.fd_id
        JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
        WHERE fd.session_id = ? 
          AND fd.class_id = ?
          AND (?::int IS NULL OR fd.section_id = ?)
          AND (?::int IS NULL OR fd.student_id = ?)
          AND (?::text IS NULL OR TO_CHAR(fddt.due_date, 'Mon YYYY') = ?)
        GROUP BY fd.student_id, month_display, month_start
    ),
    MonthlyBalance AS (
        SELECT
            sm.student_id,
            sm.month_display,
            sm.month_start,
            COALESCE(fd.assigned_fees, 0) AS assigned_fees,
            COALESCE(fd.discount, 0) AS discount,
            COALESCE(pd.additional_discount, 0) AS additional_discount,
            COALESCE(pd.penalty, 0) AS penalty,
            (COALESCE(fd.assigned_fees, 0) - COALESCE(fd.discount, 0) - COALESCE(pd.additional_discount, 0) + COALESCE(pd.penalty, 0)) AS net_due,
            COALESCE(pd.amount_paid, 0) AS amount_paid,
            GREATEST(
                (COALESCE(fd.assigned_fees, 0) - COALESCE(fd.discount, 0) - COALESCE(pd.additional_discount, 0) + COALESCE(pd.penalty, 0)) - 
                COALESCE(pd.amount_paid, 0),
                0
            ) AS balance
        FROM StudentMonths sm
        LEFT JOIN FeeDetails fd ON 
            sm.student_id = fd.student_id AND 
            sm.month_start = fd.month_start
        LEFT JOIN PaymentDetails pd ON 
            sm.student_id = pd.student_id AND 
            sm.month_start = pd.month_start
    )
    SELECT
        sl.student_id,
        sl.student_name,
        sl.class_name,
        sl.section_name,
        sl.section_id,
        sl.academic_session,
        sl.school_id,
        sl.school_name,
        COALESCE(
            JSONB_AGG(
                JSONB_BUILD_OBJECT(
                    'month', mb.month_display,
                    'assigned_fees', mb.assigned_fees,
                    'discount', mb.discount,
                    'additional_discount', mb.additional_discount,
                    'penalty', mb.penalty,
                    'net_due', mb.net_due,
                    'amount_paid', mb.amount_paid,
                    'balance', mb.balance
                ) ORDER BY mb.month_start
            ) FILTER (WHERE mb.month_display IS NOT NULL),
            '[]'::jsonb
        ) AS monthly_balance_details,
        COALESCE(SUM(mb.assigned_fees), 0) AS total_assigned_fees,
        COALESCE(SUM(mb.discount), 0) AS total_discount,
        COALESCE(SUM(mb.additional_discount), 0) AS total_additional_discount,
        COALESCE(SUM(mb.penalty), 0) AS total_penalty,
        COALESCE(SUM(mb.net_due), 0) AS total_net_due,
        COALESCE(SUM(mb.amount_paid), 0) AS total_paid,
        COALESCE(SUM(mb.balance), 0) AS total_balance
    FROM StudentList sl
    LEFT JOIN MonthlyBalance mb ON sl.student_id = mb.student_id
    GROUP BY 
        sl.student_id, 
        sl.student_name,
        sl.class_name,
        sl.section_name,
        sl.section_id,
        sl.academic_session,
        sl.school_id,
        sl.school_name
    ORDER BY sl.student_id;
    """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        Object[] params = {
                // Parameters for StudentList (5)
                sessionId, classId,
                sectionId, sectionId,
                studentId, studentId,

                // Parameters for AllMonths (2)
                monthFilter, monthFilter,

                // Parameters for FeeDetails (COALESCE section_id + 8)
                sectionId,
                sessionId, classId,
                sectionId, sectionId,
                studentId, studentId,
                monthFilter, monthFilter,

                // Parameters for PaymentDetails (8)
                sessionId, classId,
                sectionId, sectionId,
                studentId, studentId,
                monthFilter, monthFilter
        };

        return jdbcTemplate.query(sql, params, (ResultSet rs) -> {
            List<MonthWiseFeeDueDetails> list = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            while (rs.next()) {
                MonthWiseFeeDueDetails model = new MonthWiseFeeDueDetails();
                model.setStudentId(rs.getInt("student_id"));
                model.setStudentName(rs.getString("student_name"));
                model.setStudentClass(rs.getString("class_name"));
                model.setStudentSection(rs.getString("section_name"));
              //  model.setSectionId(rs.getInt("section_id"));
                model.setAcademicSession(rs.getString("academic_session"));
            //    model.setSchoolId(rs.getInt("school_id"));
             //   model.setSchoolName(rs.getString("school_name"));

                // Set totals
                model.setTotalAssignedFees(rs.getDouble("total_assigned_fees"));
                model.setTotalDiscount(rs.getDouble("total_discount"));
                model.setTotalAdditionalDiscount(rs.getDouble("total_additional_discount"));
                model.setTotalPenalty(rs.getDouble("total_penalty"));
                model.setTotalNetDue(rs.getDouble("total_net_due"));
                model.setTotalPaid(rs.getDouble("total_paid"));
                model.setTotalBalance(rs.getDouble("total_balance"));

                String monthlyJson = rs.getString("monthly_balance_details");
                List<MonthWiseFeeDueDetails.MonthlyDue> monthlyDues = new ArrayList<>();

                try {
                    JsonNode monthlyArray = mapper.readTree(monthlyJson);
                    if (monthlyArray != null && monthlyArray.isArray()) {
                        for (JsonNode obj : monthlyArray) {
                            MonthWiseFeeDueDetails.MonthlyDue due = new MonthWiseFeeDueDetails.MonthlyDue();
                            due.setMonth(obj.path("month").asText());
                            due.setAssigned_fees(obj.path("assigned_fees").asDouble(0.0));
                            due.setDiscount(obj.path("discount").asDouble(0.0));
                            due.setAdditional_discount(obj.path("additional_discount").asDouble(0.0));
                            due.setPenalty(obj.path("penalty").asDouble(0.0));
                            due.setNet_due(obj.path("net_due").asDouble(0.0));
                            due.setAmount_paid(obj.path("amount_paid").asDouble(0.0));
                            due.setBalance(obj.path("balance").asDouble(0.0));
                            monthlyDues.add(due);
                        }
                    }
                } catch (Exception e) {
                    throw new SQLException("Failed to parse monthly_balance_details JSON: " + monthlyJson, e);
                }

                model.setMonthlyDueDetails(monthlyDues);
                list.add(model);
            }

            return list;
        });
    }
}
*/

////with exclusion logic

package com.sms.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.FeeReportDAO;
import com.sms.model.FeeSummary;
import com.sms.model.MonthWiseFeeDueDetails;
import com.sms.model.PendingFee;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FeeReportDAOImpl implements FeeReportDAO {
    private final JdbcTemplate jdbcTemplate;

    public FeeReportDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FeeSummary> getFeeSummary(String reportType, LocalDate startDate, LocalDate endDate, String schoolCode) {
        JdbcTemplate schoolJdbcTemplate = null;  // Local variable for school-specific template
        try {
            String sql = buildQuery(reportType);
            schoolJdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);  // Get school-specific template
            return schoolJdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(FeeSummary.class),
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();  // Better to return empty list than null
        } finally {
            // Close only the school-specific template, not the injected one
            if (schoolJdbcTemplate != null) {
                DatabaseUtil.closeDataSource(schoolJdbcTemplate);  // Close using school code
            }
        }
    }

    private String buildQuery(String reportType) {
        return switch (reportType.toUpperCase()) {
            case "DAILY" -> """
                    SELECT DATE(fd.system_date_time) AS period,
                           pm.payment_type AS payment_mode,
                           SUM(fd.total_amount_paid) AS total_collected
                    FROM fee_deposit fd
                    JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                    WHERE fd.system_date_time BETWEEN ? AND ?
                    GROUP BY DATE(fd.system_date_time), pm.payment_type
                    ORDER BY period DESC
                    """;

            case "MONTHLY" -> """
                    SELECT TO_CHAR(fd.system_date_time, 'YYYY-MM') AS period,
                           pm.payment_type AS payment_mode,
                           SUM(fd.total_amount_paid) AS total_collected
                    FROM fee_deposit fd
                    JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                    WHERE fd.system_date_time BETWEEN ? AND ?
                    GROUP BY TO_CHAR(fd.system_date_time, 'YYYY-MM'), pm.payment_type
                    ORDER BY period DESC
                    """;

            case "YEARLY" -> """
                    SELECT s.academic_session AS period,
                           pm.payment_type AS payment_mode,
                           SUM(fd.total_amount_paid) AS total_collected
                    FROM fee_deposit fd
                    JOIN session s ON fd.session_id = s.session_id
                    JOIN payment_mode pm ON fd.payment_mode = pm.pm_id
                    WHERE fd.system_date_time BETWEEN ? AND ?
                    GROUP BY s.academic_session, pm.payment_type
                    ORDER BY s.academic_session DESC
                    """;

            default -> throw new IllegalArgumentException("Invalid report type: " + reportType);
        };
    }

    @Override
    public List<Map<String, Object>> getClassPendingFees(Long classId, Long sessionId, String schoolCode) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = """
                WITH FeeTotals AS (
                    SELECT
                        spd.student_id,
                        SUM(fdd.fee_amount) AS total_fee_assigned,
                        SUM(fdd.discount_amount) AS total_discount_amount
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN student_academic_details sad
                        ON (fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id is null)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id is null AND fa.student_id is null))
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND NOT EXISTS (
                                    SELECT 1 FROM fee_assignment_exclusion excl\s
                                    WHERE excl.fa_id = fa.fa_id AND excl.student_id = spd.student_id
                                )
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
                    ft.total_fee_assigned,
                    (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                    ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0)
                        - COALESCE(ad.total_additional_discount, 0)
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
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                WHERE
                    sad.session_id = ?
                    AND sad.student_class_id = ?
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                    mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                    ad.total_additional_discount
                ORDER BY spd.student_id;
                """;
        return schoolJdbc.queryForList(sql,sessionId,classId,sessionId,sessionId,sessionId,sessionId,classId);
    }

    @Override
    public List<Map<String, Object>> getSectionPendingFees(Long classId, Long sectionId, Long sessionId, String schoolCode) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = """
                 WITH FeeTotals AS (
                    SELECT
                        spd.student_id,
                        SUM(fdd.fee_amount) AS total_fee_assigned,
                        SUM(fdd.discount_amount) AS total_discount_amount
                    FROM
                        fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN student_academic_details sad
                        ON (
                            fa.student_id = sad.student_id
                            OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id is null)
                            OR (fa.class_id = sad.student_class_id AND fa.section_id is null AND fa.student_id is null)
                        )
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    WHERE
                        sad.session_id = ?
                        AND sad.student_class_id = ?
                        AND spd.deleted IS NOT TRUE
                        AND NOT EXISTS (
                                    SELECT 1 FROM fee_assignment_exclusion excl\s
                                    WHERE excl.fa_id = fa.fa_id AND excl.student_id = spd.student_id
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
                    ft.total_fee_assigned,
                    (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                    ft.total_fee_assigned
                        - ft.total_discount_amount
                        + COALESCE(pd.total_penalty, 0)
                        - COALESCE(ad.total_additional_discount, 0)
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
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                WHERE
                    sad.session_id = ?
                    AND sad.student_class_id = ?
                    AND (sad.student_section_id = ? OR ? IS NULL)  -- Optional section filter
                   -- AND (spd.student_id = ? OR ? IS NULL)           -- Optional student filter
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                    sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                    spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                    mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                    ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                    ad.total_additional_discount
                ORDER BY spd.student_id
                """;
        return schoolJdbc.queryForList(sql, sessionId,classId, sessionId,sessionId,sessionId,sessionId,classId,sectionId,sectionId);
    }

    @Override
    public List<Map<String, Object>> getStudentPendingFees(Long classId, Long sectionId,Long studentId, Long sessionId, String schoolCode) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = """
                        WITH FeeTotals AS (
                            SELECT
                                spd.student_id,
                                SUM(fdd.fee_amount) AS total_fee_assigned,
                                SUM(fdd.discount_amount) AS total_discount_amount
                            FROM
                                fee_assignment fa
                            JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                            JOIN student_academic_details sad
                                ON (
                                    fa.student_id = sad.student_id
                                    OR (fa.class_id = sad.student_class_id AND fa.section_id = sad.student_section_id AND fa.student_id is null)
                                    OR (fa.class_id = sad.student_class_id AND fa.section_id is null AND fa.student_id is null)
                                )
                            JOIN student_personal_details spd ON sad.student_id = spd.student_id
                            WHERE
                                sad.session_id = ?
                                AND sad.student_class_id = ?
                                AND NOT EXISTS (
                                            SELECT 1 FROM fee_assignment_exclusion excl
                                            WHERE excl.fa_id = fa.fa_id AND excl.student_id = spd.student_id
                                        )
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
                            ft.total_fee_assigned,
                            (ft.total_discount_amount + COALESCE(ad.total_additional_discount, 0)) AS total_discount,
                            ft.total_fee_assigned
                                - ft.total_discount_amount
                                + COALESCE(pd.total_penalty, 0)
                                - COALESCE(ad.total_additional_discount, 0)
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
                        LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                        LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                        LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                        LEFT JOIN AdditionalDiscounts ad ON spd.student_id = ad.student_id
                        WHERE
                            sad.session_id = ?
                            AND sad.student_class_id = ?
                            AND (sad.student_section_id = ? OR ? IS NULL)  -- Optional section filter
                           AND (spd.student_id = ? OR ? IS NULL)           -- Optional student filter
                            AND spd.deleted IS NOT TRUE
                        GROUP BY
                            sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address,
                            sd.phone_number, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode,
                            spd.student_id, spd.first_name, spd.last_name, sad.admission_no, sad.registration_number,
                            mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session,
                            ft.total_fee_assigned, ft.total_discount_amount, pd.total_penalty, pdp.total_paid,
                            ad.total_additional_discount
                        ORDER BY spd.student_id
                """;
        return schoolJdbc.queryForList(sql, sessionId, classId,sessionId,sessionId,sessionId,sessionId,classId,sectionId,sectionId,studentId,studentId);
    }
//////changed here
    @Override
    public List<Map<String, Object>> getDemandSlips(
            Long classId, Long sectionId, Long studentId,
            Long sessionId, String schoolCode, LocalDate cutoffDate
    ) {
        JdbcTemplate schoolJdbc = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(schoolJdbc);

        String baseSql = """
    SELECT
        s.student_id,
        s.first_name || ' ' || s.last_name AS student_name,
        c.class_name,
        sec.section_name,
        sess.academic_session,
        fdd.due_date,
        sf.fee_type,
        fdd.fee_amount,
        fdd.discount_amount,
        COALESCE(fdp.total_paid, 0) AS paid_amount,
        COALESCE(fdp.total_additional_discount, 0) AS additional_discount,
        (
            fdd.fee_amount 
            - fdd.discount_amount 
            - COALESCE(fdp.total_additional_discount, 0)
            - COALESCE(fdp.total_paid, 0)
        ) AS pending_amount
    FROM student_personal_details s
    JOIN student_academic_details acad ON s.student_id = acad.student_id
    JOIN mst_class c ON acad.student_class_id = c.class_id
    JOIN mst_section sec ON acad.student_section_id = sec.section_id
    JOIN session sess ON acad.session_id = sess.session_id
    JOIN fee_assignment fa ON (
        fa.session_id = acad.session_id
        AND (
            fa.student_id = s.student_id
            OR (fa.student_id IS NULL AND fa.section_id = acad.student_section_id)
            OR (fa.student_id IS NULL AND fa.section_id IS NULL AND fa.class_id = acad.student_class_id)
        )
        AND NOT EXISTS (
                                SELECT 1 FROM fee_assignment_exclusion excl\s
                                WHERE excl.fa_id = fa.fa_id AND excl.student_id = s.student_id
                            )
    )
    JOIN school_fees sf ON fa.fee_id = sf.fee_id
    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
    LEFT JOIN (
        SELECT
            fdd.fddt_id,
            fd.student_id,
            SUM(fdd.amount_paid) AS total_paid,
            SUM(fdd.additional_discount) AS total_additional_discount
        FROM fee_deposit_details fdd
        JOIN fee_deposit fd ON fdd.fd_id = fd.fd_id
        GROUP BY fdd.fddt_id, fd.student_id
    ) fdp ON fdd.fddt_id = fdp.fddt_id AND s.student_id = fdp.student_id
""";


        List<String> whereClauses = new ArrayList<>();
        whereClauses.add("acad.session_id = :sessionId");

        if (classId != null) whereClauses.add("c.class_id = :classId");
        if (sectionId != null) whereClauses.add("sec.section_id = :sectionId");
        if (studentId != null) whereClauses.add("s.student_id = :studentId");
        if (cutoffDate != null) whereClauses.add("fdd.due_date <= :cutoffDate");

        // Pending amount check
        whereClauses.add("(fdd.fee_amount - fdd.discount_amount - COALESCE(fdp.total_paid, 0)) > 0");

        String whereClause = String.join(" AND ", whereClauses);
        String sql = baseSql + " WHERE " + whereClause + " ORDER BY s.student_id, fdd.due_date";

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", sessionId);
        if (classId != null) params.put("classId", classId);
        if (sectionId != null) params.put("sectionId", sectionId);
        if (studentId != null) params.put("studentId", studentId);
        if (cutoffDate != null) params.put("cutoffDate", cutoffDate);

        return namedJdbc.queryForList(sql, params);
    }

    @Override
    public List<FeeSummary> getFeeSummaryByClass(String schoolCode) throws Exception {
        String sql = """
                SELECT
                    mc.class_name,
                    ms.section_name,
                    SUM(fd.total_amount_paid) AS total_deposit,
                   COUNT(fd.student_id) AS total_students
                  --  STRING_AGG(DISTINCT pm.payment_type, ', ') AS payment_methods_used
                FROM fee_deposit fd
                JOIN mst_class mc\s
                    ON fd.class_id = mc.class_id\s
                  --  AND fd.school_id = mc.school_id
                JOIN mst_section ms\s
                    ON fd.section_id = ms.section_id\s
                  --  AND fd.school_id = ms.school_id
                WHERE DATE(fd.payment_date) = CURRENT_DATE
                GROUP BY\s
                    mc.class_name,
                    ms.section_name,
                    mc.class_id,
                    ms.section_id,
                    fd.school_id
                ORDER BY total_deposit DESC;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<FeeSummary> classWiseFee = null;
        try{
            classWiseFee = jdbcTemplate.query(sql, new RowMapper<FeeSummary>() {
                @Override
                public FeeSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FeeSummary sd = new FeeSummary();
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setTodayCollection(rs.getDouble("total_deposit"));
                    sd.setStudentCount(rs.getDouble("total_students"));
                    return sd;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classWiseFee;
    }

    @Override
    public List<MonthWiseFeeDueDetails> fetchMonthlyDueDetails(int sessionId, int classId,
                                                               Integer sectionId, Integer studentId,
                                                               String monthFilter, String schoolCode) {
       /* String sql = """
    WITH StudentList AS (
        SELECT 
            spd.student_id,
            CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
            mc.class_name,
            ms.section_name,
            ms.section_id,
            ses.academic_session,
            spd.school_id,
            sd.school_name
        FROM student_personal_details spd
        JOIN student_academic_details sad ON spd.student_id = sad.student_id
        JOIN mst_class mc ON sad.student_class_id = mc.class_id
        JOIN mst_section ms ON sad.student_section_id = ms.section_id
        JOIN session ses ON sad.session_id = ses.session_id
        JOIN school_details sd ON spd.school_id = sd.school_id
        WHERE sad.session_id = ?
          AND sad.student_class_id = ?
          AND (?::int IS NULL OR sad.student_section_id = ?)
          AND (?::int IS NULL OR spd.student_id = ?)
          AND spd.deleted IS NOT TRUE
    ),
    AllMonths AS (
        SELECT 
            TO_CHAR(due_date, 'Mon YYYY') AS month_display,
            DATE_TRUNC('month', due_date) AS month_start
        FROM (
            SELECT GENERATE_SERIES(
                DATE_TRUNC('month', MIN(due_date)),
                DATE_TRUNC('month', MAX(due_date)),
                INTERVAL '1 month'
            ) AS due_date
            FROM fee_due_date
            WHERE school_id = (SELECT school_id FROM StudentList LIMIT 1)
        ) dates
        WHERE ?::text IS NULL OR TO_CHAR(due_date, 'Mon YYYY') = ?
    ),
    StudentMonths AS (
        SELECT 
            sl.student_id,
            am.month_display,
            am.month_start
        FROM StudentList sl
        CROSS JOIN AllMonths am
    ),
    FeeDetails AS (
        SELECT
            spd.student_id,
            TO_CHAR(fdd.due_date, 'Mon YYYY') AS month_display,
            DATE_TRUNC('month', fdd.due_date) AS month_start,
            SUM(fdd.fee_amount) AS assigned_fees,
            SUM(fdd.discount_amount) AS discount
        FROM fee_assignment fa
        JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
        JOIN student_academic_details sad ON (
            fa.student_id = sad.student_id
            OR (fa.class_id = sad.student_class_id AND fa.section_id = COALESCE(?::int, sad.student_section_id) AND fa.student_id = 0)
            OR (fa.class_id = sad.student_class_id AND fa.section_id = 0 AND fa.student_id = 0)
        )
        JOIN student_personal_details spd ON sad.student_id = spd.student_id
        WHERE sad.session_id = ?
          AND sad.student_class_id = ?
          AND (?::int IS NULL OR sad.student_section_id = ?)
          AND (?::int IS NULL OR spd.student_id = ?)
          AND spd.deleted IS NOT TRUE
          AND (?::text IS NULL OR TO_CHAR(fdd.due_date, 'Mon YYYY') = ?)
        GROUP BY spd.student_id, month_display, month_start
    ),
    PaymentDetails AS (
        SELECT
            fd.student_id,
            TO_CHAR(fddt.due_date, 'Mon YYYY') AS month_display,
            DATE_TRUNC('month', fddt.due_date) AS month_start,
            SUM(fdd.amount_paid) AS amount_paid,
            SUM(COALESCE(fdd.additional_discount, 0)) AS additional_discount,
            SUM(COALESCE(fdd.penalty_amount, 0)) AS penalty
        FROM fee_deposit fd
        JOIN fee_deposit_details fdd ON fd.fd_id = fdd.fd_id
        JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
        WHERE fd.session_id = ? 
          AND fd.class_id = ?
          AND (?::int IS NULL OR fd.section_id = ?)
          AND (?::int IS NULL OR fd.student_id = ?)
          AND (?::text IS NULL OR TO_CHAR(fddt.due_date, 'Mon YYYY') = ?)
        GROUP BY fd.student_id, month_display, month_start
    ),
    MonthlyBalance AS (
        SELECT
            sm.student_id,
            sm.month_display,
            sm.month_start,
            COALESCE(fd.assigned_fees, 0) AS assigned_fees,
            COALESCE(fd.discount, 0) AS discount,
            COALESCE(pd.additional_discount, 0) AS additional_discount,
            COALESCE(pd.penalty, 0) AS penalty,
            (COALESCE(fd.assigned_fees, 0) - COALESCE(fd.discount, 0) - COALESCE(pd.additional_discount, 0) + COALESCE(pd.penalty, 0)) AS net_due,
            COALESCE(pd.amount_paid, 0) AS amount_paid,
            GREATEST(
                (COALESCE(fd.assigned_fees, 0) - COALESCE(fd.discount, 0) - COALESCE(pd.additional_discount, 0) + COALESCE(pd.penalty, 0)) - 
                COALESCE(pd.amount_paid, 0),
                0
            ) AS balance
        FROM StudentMonths sm
        LEFT JOIN FeeDetails fd ON 
            sm.student_id = fd.student_id AND 
            sm.month_start = fd.month_start
        LEFT JOIN PaymentDetails pd ON 
            sm.student_id = pd.student_id AND 
            sm.month_start = pd.month_start
    )
    SELECT
        sl.student_id,
        sl.student_name,
        sl.class_name,
        sl.section_name,
        sl.section_id,
        sl.academic_session,
        sl.school_id,
        sl.school_name,
        COALESCE(
            JSONB_AGG(
                JSONB_BUILD_OBJECT(
                    'month', mb.month_display,
                    'assigned_fees', mb.assigned_fees,
                    'discount', mb.discount,
                    'additional_discount', mb.additional_discount,
                    'penalty', mb.penalty,
                    'net_due', mb.net_due,
                    'amount_paid', mb.amount_paid,
                    'balance', mb.balance
                ) ORDER BY mb.month_start
            ) FILTER (WHERE mb.month_display IS NOT NULL),
            '[]'::jsonb
        ) AS monthly_balance_details,
        COALESCE(SUM(mb.assigned_fees), 0) AS total_assigned_fees,
        COALESCE(SUM(mb.discount), 0) AS total_discount,
        COALESCE(SUM(mb.additional_discount), 0) AS total_additional_discount,
        COALESCE(SUM(mb.penalty), 0) AS total_penalty,
        COALESCE(SUM(mb.net_due), 0) AS total_net_due,
        COALESCE(SUM(mb.amount_paid), 0) AS total_paid,
        COALESCE(SUM(mb.balance), 0) AS total_balance
    FROM StudentList sl
    LEFT JOIN MonthlyBalance mb ON sl.student_id = mb.student_id
    GROUP BY 
        sl.student_id, 
        sl.student_name,
        sl.class_name,
        sl.section_name,
        sl.section_id,
        sl.academic_session,
        sl.school_id,
        sl.school_name
    ORDER BY sl.student_id;
    """;*/
        String sql= """
                WITH StudentList AS (
                    SELECT\s
                        spd.student_id,
                        CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,
                        mc.class_name,
                        ms.section_name,
                        ms.section_id,
                        ses.academic_session,
                        spd.school_id,
                        sd.school_name
                    FROM student_personal_details spd
                    JOIN student_academic_details sad ON spd.student_id = sad.student_id
                    JOIN mst_class mc ON sad.student_class_id = mc.class_id
                    JOIN mst_section ms ON sad.student_section_id = ms.section_id
                    JOIN session ses ON sad.session_id = ses.session_id
                    JOIN school_details sd ON spd.school_id = sd.school_id
                    WHERE sad.session_id = ?
                      AND sad.student_class_id = ?
                      AND (?::int IS NULL OR sad.student_section_id = ?)
                      AND (?::int IS NULL OR spd.student_id = ?)
                      AND spd.deleted IS NOT TRUE
                ),
                AllMonths AS (
                    SELECT\s
                        TO_CHAR(due_date, 'Mon YYYY') AS month_display,
                        DATE_TRUNC('month', due_date) AS month_start
                    FROM (
                        SELECT GENERATE_SERIES(
                            DATE_TRUNC('month', MIN(due_date)),
                            DATE_TRUNC('month', MAX(due_date)),
                            INTERVAL '1 month'
                        ) AS due_date
                        FROM fee_due_date
                        WHERE school_id = (SELECT school_id FROM StudentList LIMIT 1)
                    ) dates
                    WHERE ?::text IS NULL OR TO_CHAR(due_date, 'Mon YYYY') = ?
                ),
                StudentMonths AS (
                    SELECT\s
                        sl.student_id,
                        am.month_display,
                        am.month_start
                    FROM StudentList sl
                    CROSS JOIN AllMonths am
                ),
                FeeDetails AS (
                    SELECT
                        spd.student_id,
                        TO_CHAR(fdd.due_date, 'Mon YYYY') AS month_display,
                        DATE_TRUNC('month', fdd.due_date) AS month_start,
                        SUM(fdd.fee_amount) AS assigned_fees,
                        SUM(fdd.discount_amount) AS discount
                    FROM fee_assignment fa
                    JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    JOIN student_academic_details sad ON (
                        fa.student_id = sad.student_id
                        OR (fa.class_id = sad.student_class_id AND fa.section_id = COALESCE(?::int, sad.student_section_id) AND fa.student_id IS NULL)
                        OR (fa.class_id = sad.student_class_id AND fa.section_id IS NULL AND fa.student_id IS NULL)
                    )
                    JOIN student_personal_details spd ON sad.student_id = spd.student_id
                    -- Exclusion logic: Check if student is not excluded from this fee assignment
                    WHERE sad.session_id = ?
                      AND sad.student_class_id = ?
                      AND (?::int IS NULL OR sad.student_section_id = ?)
                      AND (?::int IS NULL OR spd.student_id = ?)
                      AND spd.deleted IS NOT TRUE
                      AND (?::text IS NULL OR TO_CHAR(fdd.due_date, 'Mon YYYY') = ?)
                      AND NOT EXISTS (
                          SELECT 1 FROM fee_assignment_exclusion excl\s
                          WHERE excl.fa_id = fa.fa_id AND excl.student_id = spd.student_id
                      )
                    GROUP BY spd.student_id, month_display, month_start
                ),
                PaymentDetails AS (
                    SELECT
                        fd.student_id,
                        TO_CHAR(fddt.due_date, 'Mon YYYY') AS month_display,
                        DATE_TRUNC('month', fddt.due_date) AS month_start,
                        SUM(fdd.amount_paid) AS amount_paid,
                        SUM(COALESCE(fdd.additional_discount, 0)) AS additional_discount,
                        SUM(COALESCE(fdd.penalty_amount, 0)) AS penalty
                    FROM fee_deposit fd
                    JOIN fee_deposit_details fdd ON fd.fd_id = fdd.fd_id
                    JOIN fee_due_date fddt ON fdd.fddt_id = fddt.fddt_id
                    WHERE fd.session_id = ?\s
                      AND fd.class_id = ?
                      AND (?::int IS NULL OR fd.section_id = ?)
                      AND (?::int IS NULL OR fd.student_id = ?)
                      AND (?::text IS NULL OR TO_CHAR(fddt.due_date, 'Mon YYYY') = ?)
                    GROUP BY fd.student_id, month_display, month_start
                ),
                MonthlyBalance AS (
                    SELECT
                        sm.student_id,
                        sm.month_display,
                        sm.month_start,
                        COALESCE(fd.assigned_fees, 0) AS assigned_fees,
                        COALESCE(fd.discount, 0) AS discount,
                        COALESCE(pd.additional_discount, 0) AS additional_discount,
                        COALESCE(pd.penalty, 0) AS penalty,
                        (COALESCE(fd.assigned_fees, 0) - COALESCE(fd.discount, 0) - COALESCE(pd.additional_discount, 0) + COALESCE(pd.penalty, 0)) AS net_due,
                        COALESCE(pd.amount_paid, 0) AS amount_paid,
                        GREATEST(
                            (COALESCE(fd.assigned_fees, 0) - COALESCE(fd.discount, 0) - COALESCE(pd.additional_discount, 0) + COALESCE(pd.penalty, 0)) -\s
                            COALESCE(pd.amount_paid, 0),
                            0
                        ) AS balance
                    FROM StudentMonths sm
                    LEFT JOIN FeeDetails fd ON\s
                        sm.student_id = fd.student_id AND\s
                        sm.month_start = fd.month_start
                    LEFT JOIN PaymentDetails pd ON\s
                        sm.student_id = pd.student_id AND\s
                        sm.month_start = pd.month_start
                )
                SELECT
                    sl.student_id,
                    sl.student_name,
                    sl.class_name,
                    sl.section_name,
                    sl.section_id,
                    sl.academic_session,
                    sl.school_id,
                    sl.school_name,
                    COALESCE(
                        JSONB_AGG(
                            JSONB_BUILD_OBJECT(
                                'month', mb.month_display,
                                'assigned_fees', mb.assigned_fees,
                                'discount', mb.discount,
                                'additional_discount', mb.additional_discount,
                                'penalty', mb.penalty,
                                'net_due', mb.net_due,
                                'amount_paid', mb.amount_paid,
                                'balance', mb.balance
                            ) ORDER BY mb.month_start
                        ) FILTER (WHERE mb.month_display IS NOT NULL),
                        '[]'::jsonb
                    ) AS monthly_balance_details,
                    COALESCE(SUM(mb.assigned_fees), 0) AS total_assigned_fees,
                    COALESCE(SUM(mb.discount), 0) AS total_discount,
                    COALESCE(SUM(mb.additional_discount), 0) AS total_additional_discount,
                    COALESCE(SUM(mb.penalty), 0) AS total_penalty,
                    COALESCE(SUM(mb.net_due), 0) AS total_net_due,
                    COALESCE(SUM(mb.amount_paid), 0) AS total_paid,
                    COALESCE(SUM(mb.balance), 0) AS total_balance
                FROM StudentList sl
                LEFT JOIN MonthlyBalance mb ON sl.student_id = mb.student_id
                GROUP BY\s
                    sl.student_id,\s
                    sl.student_name,
                    sl.class_name,
                    sl.section_name,
                    sl.section_id,
                    sl.academic_session,
                    sl.school_id,
                    sl.school_name
                ORDER BY sl.student_id
                """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        Object[] params = {
                // Parameters for StudentList (5)
                sessionId, classId,
                sectionId, sectionId,
                studentId, studentId,

                // Parameters for AllMonths (2)
                monthFilter, monthFilter,

                // Parameters for FeeDetails (COALESCE section_id + 8)
                sectionId,
                sessionId, classId,
                sectionId, sectionId,
                studentId, studentId,
                monthFilter, monthFilter,

                // Parameters for PaymentDetails (8)
                sessionId, classId,
                sectionId, sectionId,
                studentId, studentId,
                monthFilter, monthFilter
        };

        return jdbcTemplate.query(sql, params, (ResultSet rs) -> {
            List<MonthWiseFeeDueDetails> list = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            while (rs.next()) {
                MonthWiseFeeDueDetails model = new MonthWiseFeeDueDetails();
                model.setStudentId(rs.getInt("student_id"));
                model.setStudentName(rs.getString("student_name"));
                model.setStudentClass(rs.getString("class_name"));
                model.setStudentSection(rs.getString("section_name"));
              //  model.setSectionId(rs.getInt("section_id"));
                model.setAcademicSession(rs.getString("academic_session"));
            //    model.setSchoolId(rs.getInt("school_id"));
             //   model.setSchoolName(rs.getString("school_name"));

                // Set totals
                model.setTotalAssignedFees(rs.getDouble("total_assigned_fees"));
                model.setTotalDiscount(rs.getDouble("total_discount"));
                model.setTotalAdditionalDiscount(rs.getDouble("total_additional_discount"));
                model.setTotalPenalty(rs.getDouble("total_penalty"));
                model.setTotalNetDue(rs.getDouble("total_net_due"));
                model.setTotalPaid(rs.getDouble("total_paid"));
                model.setTotalBalance(rs.getDouble("total_balance"));

                String monthlyJson = rs.getString("monthly_balance_details");
                List<MonthWiseFeeDueDetails.MonthlyDue> monthlyDues = new ArrayList<>();

                try {
                    JsonNode monthlyArray = mapper.readTree(monthlyJson);
                    if (monthlyArray != null && monthlyArray.isArray()) {
                        for (JsonNode obj : monthlyArray) {
                            MonthWiseFeeDueDetails.MonthlyDue due = new MonthWiseFeeDueDetails.MonthlyDue();
                            due.setMonth(obj.path("month").asText());
                            due.setAssigned_fees(obj.path("assigned_fees").asDouble(0.0));
                            due.setDiscount(obj.path("discount").asDouble(0.0));
                            due.setAdditional_discount(obj.path("additional_discount").asDouble(0.0));
                            due.setPenalty(obj.path("penalty").asDouble(0.0));
                            due.setNet_due(obj.path("net_due").asDouble(0.0));
                            due.setAmount_paid(obj.path("amount_paid").asDouble(0.0));
                            due.setBalance(obj.path("balance").asDouble(0.0));
                            monthlyDues.add(due);
                        }
                    }
                } catch (Exception e) {
                    throw new SQLException("Failed to parse monthly_balance_details JSON: " + monthlyJson, e);
                }

                model.setMonthlyDueDetails(monthlyDues);
                list.add(model);
            }

            return list;
        });
    }
}



