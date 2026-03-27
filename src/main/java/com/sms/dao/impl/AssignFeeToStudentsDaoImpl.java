package com.sms.dao.impl;

import com.sms.dao.AssignFeeToStudentsDao;
import com.sms.model.AssignFeeToStudentsDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AssignFeeToStudentsDaoImpl implements AssignFeeToStudentsDao {

    @Override
    public List<AssignFeeToStudentsDetails> getAssignFeeToStudentDetails(int sessionId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    fa.fa_id                                                AS assignment_id,
                    mc.class_id,
                    mc.class_name,
                    CASE
                        WHEN fa.section_id IS NOT NULL THEN ARRAY[ms.section_id]
                        ELSE ARRAY_AGG(DISTINCT cs.section_id)
                    END                                                     AS section_ids,
                    CASE
                        WHEN fa.section_id IS NOT NULL THEN ARRAY[ms.section_name]
                        ELSE ARRAY_AGG(DISTINCT ms2.section_name)
                    END                                                     AS section_names,
                    fs.fee_type,
                    fdd.fee_amount                                          AS amount,
                    mf.frequency_type                                       AS frequency,
                    fa.valid_from                                           AS start_date,
                    CASE
                        WHEN UPPER(mf.frequency_type) = 'MONTHLY'          THEN '12 months'
                        WHEN UPPER(mf.frequency_type) = 'YEARLY'           THEN '1 year'
                        WHEN UPPER(mf.frequency_type) = 'HALF YEARLY'      THEN '2 half years'
                        WHEN UPPER(mf.frequency_type) = 'QUARTERLY'        THEN '4 quarters'
                        ELSE mf.frequency_type
                    END                                                     AS duration,
                    COUNT(DISTINCT sad.student_id)                          AS students,
                    COALESCE(
                        CONCAT(
                            dc.dc_description, ' (',
                            dc.dc_rate,
                            CASE
                                WHEN UPPER(dc.dc_rate_type) = 'PERCENTAGE' THEN '%'
                                ELSE ' ' || dc.dc_rate_type
                            END,
                            ')'
                        ),
                        'No Discount'
                    )                                                       AS discount,
                    CASE
                        WHEN fs.deleted = TRUE THEN 'Inactive'
                        ELSE 'Active'
                    END                                                     AS status
                FROM fee_assignment fa
                JOIN school_fees fs              ON fa.fee_id = fs.fee_id
                JOIN mst_frequency mf            ON fs.frequency_id = mf.frequency_id
                JOIN mst_class mc                ON fa.class_id = mc.class_id
                LEFT JOIN mst_section ms         ON fa.section_id = ms.section_id
                LEFT JOIN class_and_section cs
                    ON fa.section_id IS NULL
                    AND cs.class_id = fa.class_id
                    AND cs.school_id = fa.school_id
                LEFT JOIN mst_section ms2
                    ON fa.section_id IS NULL
                    AND ms2.section_id = cs.section_id
                JOIN fee_due_date fdd            ON fa.fa_id = fdd.fa_id
                JOIN student_academic_details sad
                    ON sad.session_id = fa.session_id
                    AND sad.student_class_id = fa.class_id
                    AND (
                        (fa.student_id IS NOT NULL AND fa.student_id = sad.student_id)
                        OR (fa.student_id IS NULL AND fa.section_id IS NOT NULL AND fa.section_id = sad.student_section_id)
                        OR (fa.student_id IS NULL AND fa.section_id IS NULL)
                    )
                JOIN student_personal_details spd ON sad.student_id = spd.student_id
                LEFT JOIN discount_code dc       ON fa.dc_id = dc.dc_id
                WHERE
                    fa.session_id = ?
                    AND spd.deleted IS NOT TRUE
                    AND NOT EXISTS (
                        SELECT 1 FROM fee_assignment_exclusion fe
                        WHERE fe.fa_id = fa.fa_id
                          AND fe.student_id = sad.student_id
                          AND (fe.valid_to IS NULL OR fe.valid_to >= CURRENT_DATE)
                    )
                    AND (
                        (UPPER(fs.fee_type) LIKE '% - OLD'      AND sad.student_type = 'Old')
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
                GROUP BY
                    fa.fa_id, fa.valid_from, fa.dc_id, fa.section_id,
                    mc.class_id, mc.class_name,
                    ms.section_id, ms.section_name,
                    fs.fee_type, fs.deleted,
                    fdd.fee_amount,
                    mf.frequency_type,
                    dc.dc_description, dc.dc_rate, dc.dc_rate_type
                ORDER BY fa.fa_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AssignFeeToStudentsDetails> assignFeeToStudentsDetails = null;
        try{
            assignFeeToStudentsDetails = jdbcTemplate.query(sql, new Object[]{sessionId}, (rs, rowNum) -> {
                AssignFeeToStudentsDetails obj = new AssignFeeToStudentsDetails();

                obj.setAssignmentId(rs.getInt("assignment_id"));
                obj.setClassId(rs.getInt("class_id"));
                obj.setClassName(rs.getString("class_name"));

                // Handle PostgreSQL ARRAY → List<Integer>
                java.sql.Array sectionIdsArray = rs.getArray("section_ids");
                if (sectionIdsArray != null) {
                    Integer[] sectionIds = (Integer[]) sectionIdsArray.getArray();
                    obj.setSectionIds(List.of(sectionIds));
                }

                //Handle PostgreSQL ARRAY → List<String>
                java.sql.Array sectionNamesArray = rs.getArray("section_names");
                if (sectionNamesArray != null) {
                    String[] sectionNames = (String[]) sectionNamesArray.getArray();
                    obj.setSectionNames(List.of(sectionNames));
                }

                obj.setFeeType(rs.getString("fee_type"));
                obj.setAmount(rs.getDouble("amount"));
                obj.setFrequency(rs.getString("frequency"));

                //Date mapping
                obj.setStartDate(rs.getDate("start_date"));

                obj.setDuration(rs.getString("duration"));
                obj.setStudentsCount(rs.getInt("students"));
                obj.setDiscount(rs.getString("discount"));
                obj.setStatus(rs.getString("status"));

                return obj;
            });
        }catch (Exception e){
            e.printStackTrace();
            return List.of();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return assignFeeToStudentsDetails;
    }
}
