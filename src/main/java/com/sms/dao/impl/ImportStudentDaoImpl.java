package com.sms.dao.impl;

import com.sms.dao.ImportStudentDao;
import com.sms.exception.InvalidPromotionException;
import com.sms.model.AddBookDetails;
import com.sms.model.ImportStudent;
import com.sms.model.StaffDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ImportStudentDaoImpl implements ImportStudentDao {
    private final JdbcTemplate jdbcTemplate;
@Autowired
    public ImportStudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ImportStudent> getStudentDetails(Integer sessionId, Integer classId, Integer sectionId, String schoolCode) throws Exception {
        StringBuilder sql = new StringBuilder("""
            WITH AttendanceSummary AS (
                SELECT
                    sa.student_id,
                    sa.session_id,
                    sa.student_class_id AS class_id,
                    sa.student_section_id AS section_id,
                    COUNT(CASE WHEN ad.absent IS NULL THEN 1 END) * 100.0 / COUNT(*) AS attendance_percentage
                FROM student_academic_details sa
                JOIN class_attendance ca ON sa.student_class_id = ca.class_id
                                          AND sa.student_section_id = ca.section_id
                LEFT JOIN absent_details ad ON ca.ca_id = ad.ca_id AND sa.student_id = ad.student_id
                JOIN student_personal_details spd ON sa.student_id = spd.student_id
                WHERE spd.deleted IS NOT TRUE
                  AND spd.validity_end_date <= '9999-12-31'
                GROUP BY sa.student_id, sa.session_id, sa.student_class_id, sa.student_section_id
            ),
            ExamResults AS (
                SELECT
                    es.student_id,
                    e.session_id,
                    e.class_id,
                    e.section_id,
                    CASE
                        WHEN SUM(CASE WHEN es.total_marks < exs.passing_marks THEN 1 ELSE 0 END) > 0
                        THEN 'Failed' ELSE 'Passed'
                    END AS exam_status
                FROM exam_results es
                JOIN exam_subjects exs ON es.exam_subject_id = exs.exam_subject_id
                JOIN exams e ON exs.exam_id = e.exam_id
                GROUP BY es.student_id, e.session_id, e.class_id, e.section_id
            )
            SELECT
                a.student_id,
                spd.first_name,
                mc.class_id,
                mc.class_name,
                ms.section_id,
                ms.section_name,
                a.session_id,
                s.academic_session,
                e.exam_status,
                a.attendance_percentage,
                CASE
                    WHEN a.attendance_percentage >= 75 AND e.exam_status = 'Passed' THEN 'Eligible'
                    ELSE 'Not Eligible'
                END AS eligibility_status
            FROM AttendanceSummary a
            JOIN ExamResults e ON a.student_id = e.student_id
                               AND a.session_id = e.session_id
                               AND a.class_id = e.class_id
                               AND a.section_id = e.section_id
            JOIN mst_class mc ON a.class_id = mc.class_id
            JOIN mst_section ms ON a.section_id = ms.section_id
            JOIN session s ON a.session_id = s.session_id
            JOIN student_personal_details spd ON a.student_id = spd.student_id
            """);

        List<Object> params = new ArrayList<>();
        if (classId != null) {
            sql.append(" WHERE a.class_id = ?");
            params.add(classId);
        }
        if (sectionId != null) {
            sql.append(classId != null ? " AND" : " WHERE").append(" a.section_id = ?");
            params.add(sectionId);
        }
        if (sessionId != null) {
            sql.append((classId != null || sectionId != null) ? " AND" : " WHERE").append(" a.session_id = ?");
            params.add(sessionId);
        }
        sql.append(" ORDER BY a.student_id");

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ImportStudent> importStudents;

        try {
            importStudents = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                ImportStudent imp = new ImportStudent();
                imp.setStudentId(rs.getInt("student_id"));
                imp.setStudentName(rs.getString("first_name"));
                imp.setClassId(rs.getInt("class_id"));
                imp.setClassName(rs.getString("class_name"));
                imp.setSectionId(rs.getInt("section_id"));
                imp.setSectionName(rs.getString("section_name"));
                imp.setSessionId(rs.getInt("session_id"));
                imp.setAcademicSession(rs.getString("academic_session"));
                imp.setExamStatus(rs.getString("exam_status"));
                imp.setAttendancePercentage(rs.getDouble("attendance_percentage"));
                imp.setEligibiltyStatus(rs.getString("eligibility_status"));
                return imp;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return importStudents;
    }



    @Override
    public boolean existsInNextSession(Integer studentId, Integer nextSessionId,String schoolCode) {
        String sql = "SELECT COUNT(*) FROM student_academic_details " +
                "WHERE student_id = ? AND session_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, studentId, nextSessionId) > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }




   @Override
   public void validateSessionClassSection(Integer sessionId, Integer classId,
                                           Integer sectionId, String schoolCode) {
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       System.out.println("tt"+sessionId+classId+sectionId);
       try {
           String sessionSql = "SELECT COUNT(*) FROM session WHERE session_id = ?";
           int sessionCount = jdbcTemplate.queryForObject(sessionSql, Integer.class, sessionId);

           String classSql = "SELECT COUNT(*) FROM mst_class WHERE class_id = ?";
           int classCount = jdbcTemplate.queryForObject(classSql, Integer.class, classId);

           String sectionSql = "SELECT COUNT(*) FROM mst_section WHERE section_id = ?";
           int sectionCount = jdbcTemplate.queryForObject(sectionSql, Integer.class, sectionId);
           System.out.println("hhh"+sessionCount+classCount+sectionCount);
           if (sessionCount == 0 || classCount == 0 || sectionCount == 0) {
               throw new InvalidPromotionException("Invalid session, class, or section for the school");
           }
       } catch (Exception e) {
           e.printStackTrace();
           throw new InvalidPromotionException("Error validating session, class, or section");
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

    @Override
    public void promoteStudents(List<Integer> studentIds, Integer currentSessionId,
                                Integer nextSessionId, Integer nextClassId,
                                Integer nextSectionId, String schoolCode) {
        String sql = """
        INSERT INTO student_academic_details (
            student_id, school_id, apaar_id, pen_no, admission_no, admission_date, registration_number, roll_number, 
            session_id, student_class_id, student_section_id, stream, education_medium, referred_by, is_rte_student, 
            rte_application_no, enrolled_session, enrolled_class, enrolled_year, transfer_cirti_no, date_of_issue, 
            scholarship_id, scholarship_password, lst_school_name, lst_school_addrs, lst_attended_class, lst_scl_aff_to, 
            lst_session, is_dropout, dropout_date, dropout_reason, student_addmission_type, session_status, 
            session_status_comment, previous_qualifications, updated_by, create_date, validity_start_date, validity_end_date
        )
        SELECT 
            sad.student_id, sad.school_id, sad.apaar_id, sad.pen_no, sad.admission_no, sad.admission_date, 
            sad.registration_number, sad.roll_number, ?, ?, ?, sad.stream, sad.education_medium, sad.referred_by, 
            sad.is_rte_student, sad.rte_application_no, sad.enrolled_session, sad.enrolled_class, sad.enrolled_year, 
            sad.transfer_cirti_no, sad.date_of_issue, sad.scholarship_id, sad.scholarship_password, sad.lst_school_name, 
            sad.lst_school_addrs, sad.lst_attended_class, sad.lst_scl_aff_to, sad.lst_session, sad.is_dropout, 
            sad.dropout_date, sad.dropout_reason, sad.student_addmission_type, 'Active', 'Promoted to next session', 
            sad.previous_qualifications, '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '9999-12-31'
        FROM student_academic_details sad
        WHERE sad.student_id = ?
        AND sad.session_id = ?
        AND NOT EXISTS (
            SELECT 1 FROM student_academic_details 
            WHERE student_id = sad.student_id 
            AND session_id = ?
        );
    """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            for (Integer studentId : studentIds) {
                jdbcTemplate.update(sql, nextSessionId, nextClassId, nextSectionId, studentId, currentSessionId, nextSessionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<ImportStudent> getGraduationEligibleStudents(Integer sessionId, Integer classId, Integer sectionId, String schoolCode) {
    String sql= /*"""
            WITH FeeTotals AS (
                SELECT
                    spd.student_id,
                    sad.student_class_id AS class_id,
                    sad.student_section_id AS section_id,
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
                WHERE sad.session_id = ? -- Filter by session_id
                    AND spd.deleted IS NOT TRUE
                GROUP BY spd.student_id, sad.student_class_id, sad.student_section_id
            ),
            PenaltyDetails AS (
                SELECT
                    fd.student_id,
                    COALESCE(SUM(fddt.penalty_amount), 0.0) AS total_penalty
                FROM
                    fee_deposit_details fddt
                JOIN fee_deposit fd ON fddt.fd_id = fd.fd_id
                WHERE fd.session_id = ? -- Filter by session_id
                GROUP BY fd.student_id
            ),
            PaidDetails AS (
                SELECT
                    fd.student_id,
                    COALESCE(SUM(fd.total_amount_paid), 0.0) AS total_paid
                FROM fee_deposit fd
                WHERE fd.session_id = ? -- Filter by session_id
                GROUP BY fd.student_id
            )
            SELECT
                spd.student_id,
                CONCAT(spd.first_name, ' ', spd.last_name) AS student_name, -- Full name as student_name
                mc.class_name ,
                ms.section_name ,
                ft.class_id,
                ft.section_id,
                (ft.total_fee_assigned - ft.total_discount_amount + COALESCE(pd.total_penalty, 0))
                    - COALESCE(pdp.total_paid, 0) AS due_amount,
                CASE
                    WHEN ((ft.total_fee_assigned - ft.total_discount_amount + COALESCE(pd.total_penalty, 0))
                        - COALESCE(pdp.total_paid, 0)) = 0 THEN TRUE
                    ELSE FALSE
                END AS is_eligible_for_graduation
            FROM
                student_personal_details spd
            JOIN student_academic_details sad ON spd.student_id = sad.student_id
            JOIN mst_class mc ON sad.student_class_id = mc.class_id  -- Join with mst_class to get class_name
            JOIN mst_section ms ON sad.student_section_id = ms.section_id  -- Join with mst_section to get section_name
            LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
            LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
            LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
            WHERE
                sad.session_id = ? -- Filter by session_id
                AND sad.student_class_id = ? -- Filter by class_id
                AND sad.student_section_id = ?-- Filter by section_id
                AND spd.deleted IS NOT TRUE
            ORDER BY spd.student_id
            """*/
        """
                WITH FeeTotals AS (
                    SELECT
                        spd.student_id,
                        sad.student_class_id AS class_id,
                        sad.student_section_id AS section_id,
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
                    WHERE sad.session_id = ? -- Filter by session_id
                        AND spd.deleted IS NOT TRUE
                    GROUP BY spd.student_id, sad.student_class_id, sad.student_section_id
                ),
                PenaltyDetails AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fddt.penalty_amount), 0.0) AS total_penalty
                    FROM
                        fee_deposit_details fddt
                    JOIN fee_deposit fd ON fddt.fd_id = fd.fd_id
                    WHERE fd.session_id = ? -- Filter by session_id
                    GROUP BY fd.student_id
                ),
                PaidDetails AS (
                    SELECT
                        fd.student_id,
                        COALESCE(SUM(fd.total_amount_paid), 0.0) AS total_paid
                    FROM fee_deposit fd
                    WHERE fd.session_id = ? -- Filter by session_id
                    GROUP BY fd.student_id
                ),
                ExamResults AS (
                    SELECT
                        er.student_id,
                        COUNT(er.exam_subject_id) AS total_exams,
                        COUNT(CASE WHEN er.total_marks >= es.passing_marks THEN 1 END) AS passed_exams
                    FROM exam_results er
                    JOIN exam_subjects es ON er.exam_subject_id = es.exam_subject_id
                    WHERE es.exam_date <= CURRENT_DATE  -- Only consider completed exams
                    GROUP BY er.student_id
                )
                SELECT
                    spd.student_id,
                    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name, -- Full name as student_name
                    mc.class_name,
                    ms.section_name,
                    ft.class_id,
                    ft.section_id,
                    (ft.total_fee_assigned - ft.total_discount_amount + COALESCE(pd.total_penalty, 0))
                        - COALESCE(pdp.total_paid, 0) AS due_amount,
                    CASE\s
                        WHEN ((ft.total_fee_assigned - ft.total_discount_amount + COALESCE(pd.total_penalty, 0))
                            - COALESCE(pdp.total_paid, 0)) = 0 THEN TRUE
                        ELSE FALSE
                    END AS is_fee_cleared,
                    CASE\s
                        WHEN er.total_exams = er.passed_exams THEN 'Passed'
                        ELSE 'Failed'
                    END AS exam_status,
                    CASE\s
                        WHEN ((ft.total_fee_assigned - ft.total_discount_amount + COALESCE(pd.total_penalty, 0))
                            - COALESCE(pdp.total_paid, 0)) = 0\s
                            AND er.total_exams = er.passed_exams THEN TRUE
                        ELSE FALSE
                    END AS is_eligible_for_graduation
                FROM
                    student_personal_details spd
                JOIN student_academic_details sad ON spd.student_id = sad.student_id
                JOIN mst_class mc ON sad.student_class_id = mc.class_id  -- Join with mst_class to get class_name
                JOIN mst_section ms ON sad.student_section_id = ms.section_id  -- Join with mst_section to get section_name
                LEFT JOIN FeeTotals ft ON spd.student_id = ft.student_id
                LEFT JOIN PenaltyDetails pd ON spd.student_id = pd.student_id
                LEFT JOIN PaidDetails pdp ON spd.student_id = pdp.student_id
                LEFT JOIN ExamResults er ON spd.student_id = er.student_id
                WHERE
                    sad.session_id = ? -- Filter by session_id
                    AND sad.student_class_id = ? -- Filter by class_id
                    AND sad.student_section_id = ? -- Filter by section_id
                    AND spd.deleted IS NOT TRUE
                ORDER BY spd.student_id;
                
                """;

    JdbcTemplate jdbcTemplate=DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, new Object[]{sessionId, sessionId, sessionId, sessionId, classId, sectionId}, new RowMapper<ImportStudent>() {
                @Override
                public ImportStudent mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ImportStudent imps=new ImportStudent();
                    imps.setStudentId(rs.getInt("student_id"));
                    imps.setStudentName(rs.getString("student_name"));
                    imps.setClassId(rs.getInt("class_id"));
                    imps.setClassName(rs.getString("class_name"));
                    imps.setSectionId(rs.getInt("section_id"));
                    imps.setStudentName(rs.getString("section_name"));
                    imps.setDueAmount(rs.getDouble("due_amount"));
                    imps.setFeeCleared(rs.getBoolean("is_fee_cleared"));
                    imps.setExamStatus(rs.getString("exam_status"));
                    imps.setEligibleForGraduation(rs.getBoolean("is_eligible_for_graduation"));
                    return imps;
                }
            });

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return List.of();
    }

    /*@Override
    public boolean isHighestClass(Integer classId, String schoolCode) {
        String sql = "SELECT is_highest_class FROM mst_class WHERE class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, Boolean.class, classId);
        } catch (Exception e) {
            return false;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }*/

    @Override
    public void graduateStudents(List<Integer> studentIds, Integer currentSessionId, String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        // Update academic details
        String academicUpdateSql = """
            UPDATE student_academic_details
            SET validity_end_date = CURRENT_DATE,
                session_status = 'Graduated',
                session_status_comment = 'Student graduated'
            WHERE student_id = ?
            AND session_id = ?
            AND validity_end_date = '9999-12-31'
            """;

        // Update personal details
        String personalUpdateSql = """
            UPDATE student_personal_details
            SET current_status = 'Graduated',
                validity_end_date = CURRENT_DATE
            WHERE student_id = ?
            AND validity_end_date = '9999-12-31'
            """;

        try {
            for (Integer studentId : studentIds) {
                // Update academic record
                jdbcTemplate.update(academicUpdateSql, studentId, currentSessionId);
                System.out.println("current"+studentId+currentSessionId);
                // Update personal details
                jdbcTemplate.update(personalUpdateSql, studentId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Graduation failed: " + e.getMessage());
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}
