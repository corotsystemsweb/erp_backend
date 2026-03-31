package com.sms.dao.impl;

import com.sms.dao.ManageFeeTypeDao;
import com.sms.model.ManageFeeTypeDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ManageFeeTypeImpl implements ManageFeeTypeDao {

    @Override
    public List<ManageFeeTypeDetails> getManageFeeType(int sessionId, String schoolCode) throws Exception {
        String sql = """
                WITH FeeAssignedStudents AS (
                    SELECT
                        fs.fee_id,
                        fs.fee_type,
                        COUNT(DISTINCT sad.student_id)  AS total_students,
                        COALESCE(SUM(fdd.fee_amount), 0) AS total_amount
                    FROM school_fees fs
                    LEFT JOIN fee_assignment fa ON fs.fee_id = fa.fee_id
                        AND fa.session_id = ?
                    LEFT JOIN fee_due_date fdd ON fa.fa_id = fdd.fa_id
                    LEFT JOIN student_academic_details sad
                        ON sad.session_id = fa.session_id
                        AND sad.student_class_id = fa.class_id
                        AND (
                            (fa.student_id IS NOT NULL AND fa.student_id = sad.student_id)
                            OR (fa.student_id IS NULL AND fa.section_id IS NOT NULL AND fa.section_id = sad.student_section_id)
                            OR (fa.student_id IS NULL AND fa.section_id IS NULL)
                        )
                    LEFT JOIN student_personal_details spd
                        ON sad.student_id = spd.student_id
                        AND spd.deleted IS NOT TRUE
                    WHERE
                        NOT EXISTS (
                            SELECT 1 FROM fee_assignment_exclusion fe
                            WHERE fe.fa_id = fa.fa_id
                              AND fe.student_id = sad.student_id
                              AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
                        )
                        AND (
                            sad.student_id IS NULL
                            OR (UPPER(fs.fee_type) LIKE '% - OLD'      AND sad.student_type = 'Old')
                            OR (UPPER(fs.fee_type) LIKE '% - NEW'      AND sad.student_type = 'New')
                            OR (UPPER(fs.fee_type) LIKE '% - MALE'     AND UPPER(spd.gender) = 'MALE')
                            OR (UPPER(fs.fee_type) LIKE '% - FEMALE'   AND UPPER(spd.gender) = 'FEMALE')
                            OR (
                                UPPER(fs.fee_type) NOT LIKE '% - OLD'
                                AND UPPER(fs.fee_type) NOT LIKE '% - NEW'
                                AND UPPER(fs.fee_type) NOT LIKE '% - MALE'
                                AND UPPER(fs.fee_type) NOT LIKE '% - FEMALE'
                            )
                        )
                    GROUP BY fs.fee_id, fs.fee_type
                )
                SELECT
                    fas.fee_id,
                    fas.fee_type,
                    fas.total_students,
                    fas.total_amount,
                    CASE WHEN sf.deleted = TRUE THEN 'Inactive' ELSE 'Active' END AS status
                FROM FeeAssignedStudents fas
                JOIN school_fees sf ON fas.fee_id = sf.fee_id
                ORDER BY fas.fee_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ManageFeeTypeDetails> manageFeeTypeDetails = null;
        try{
            manageFeeTypeDetails = jdbcTemplate.query(sql, new Object[]{sessionId}, (rs, rowNum) -> {
                ManageFeeTypeDetails mftd = new ManageFeeTypeDetails();
                mftd.setFeeId(rs.getInt("fee_id"));
                mftd.setFeeType(rs.getString("fee_type"));
                mftd.setTotalStudents(rs.getInt("total_students"));
                mftd.setTotalAmount(rs.getDouble("total_amount"));
                mftd.setStatus(rs.getString("status"));
                return mftd;
            });
        } catch (Exception e){
            e.printStackTrace();
            return List.of();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return manageFeeTypeDetails;
    }
}
