/*
package com.sms.dao.impl;

import com.sms.dao.FeeAsWithDDateDao;
import com.sms.model.FeeAssignmentDetailsNew;
import org.springframework.jdbc.core.JdbcTemplate;

public class FeeAsWithDDateDaoImpl implements FeeAsWithDDateDao {

    private final JdbcTemplate jdbcTemplate;

    public FeeAsWithDDateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean assignFeeWithDueDates(FeeAssignmentDetailsNew feeAssignmentDetailsNew) {
        Connection conn = null;
        PreparedStatement pstmtFA = null;
        PreparedStatement pstmtFDDT = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // Insert into fee_assignment
            String faSQL = "INSERT INTO fee_assignment (school_id, session_id, class_id, section_id, student_id, fee_id, dc_id, valid_from, valid_to, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING fa_id";
            pstmtFA = conn.prepareStatement(faSQL);
            pstmtFA.setInt(1, details.getSchoolId());
            pstmtFA.setInt(2, details.getSessionId());
            pstmtFA.setObject(3, details.getClassId());
            pstmtFA.setObject(4, details.getSectionId());
            pstmtFA.setObject(5, details.getStudentId());
            pstmtFA.setInt(6, details.getFeeId());
            pstmtFA.setObject(7, details.getDcId());
            pstmtFA.setDate(8, Date.valueOf(details.getValidFrom()));
            pstmtFA.setObject(9, details.getValidTo() != null ? Date.valueOf(details.getValidTo()) : null);
            pstmtFA.setInt(10, details.getUpdatedBy());

            rs = pstmtFA.executeQuery();
            int faId = -1;
            if (rs.next()) {
                faId = rs.getInt(1);
            }

            // Insert due dates
            String fddtSQL = "INSERT INTO fee_due_date (school_id, fa_id, due_date, fee_amount, discount_amount, updated_by, update_date_time) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
            pstmtFDDT = conn.prepareStatement(fddtSQL);

            List<FeeDueDateDetails> dueDates = details.getDueDates();
            for (FeeDueDateDetails due : dueDates) {
                pstmtFDDT.setInt(1, details.getSchoolId());
                pstmtFDDT.setInt(2, faId);
                pstmtFDDT.setDate(3, Date.valueOf(due.getDueDate()));
                pstmtFDDT.setInt(4, due.getFeeAmount());
                pstmtFDDT.setInt(5, due.getDiscountAmount());
                pstmtFDDT.setInt(6, details.getUpdatedBy());
                pstmtFDDT.addBatch();
            }
            pstmtFDDT.executeBatch();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmtFA != null) pstmtFA.close(); } catch (SQLException e) {}
            try { if (pstmtFDDT != null) pstmtFDDT.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
}
*/
/*
package com.sms.dao.impl;
import com.sms.dao.FeeAsWithDDateDao;
import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.model.FeeDueDateDetailsNew;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
@Repository
public class FeeAsWithDDateDaoImpl implements FeeAsWithDDateDao {

    @Override
    public boolean assignFeeWithDueDates(FeeAssignmentDetailsNew details, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            // Step 1: Insert into fee_assignment and get generated fa_id
            String faSQL = "INSERT INTO fee_assignment (school_id, session_id, class_id, section_id, student_id, fee_id, dc_id, valid_from, valid_to, updated_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING fa_id";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(faSQL, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, details.getSessionId());
                ps.setObject(3, details.getClassId());
                ps.setObject(4, details.getSectionId());
                ps.setObject(5, details.getStudentId());
                ps.setInt(6, details.getFeeId());
                ps.setObject(7, details.getDcId());
                ps.setDate(8, Date.valueOf(details.getValidFrom()));
                ps.setObject(9, details.getValidTo() != null ? Date.valueOf(details.getValidTo()) : null);
                ps.setInt(10, details.getUpdatedBy());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key == null) {
                return false;
            }
            int faId = key.intValue();

            // Step 2: Insert due dates in batch
            String fddtSQL = "INSERT INTO fee_due_date (school_id, fa_id, due_date, fee_amount, discount_amount, updated_by, update_date_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            List<FeeDueDateDetailsNew> dueDates = details.getDueDates();
            jdbcTemplate.batchUpdate(fddtSQL, dueDates, dueDates.size(), (ps, due) -> {
                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, faId);
                ps.setDate(3, Date.valueOf(due.getDueDate()));
                ps.setInt(4, due.getFeeAmount());
                ps.setInt(5, due.getDiscountAmount());
                ps.setInt(6, details.getUpdatedBy());
            });

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jdbcTemplate != null) {
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
        }
    }
}
*/
/*
package com.sms.dao.impl;

import com.sms.dao.FeeAsWithDDateDao;
import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.model.FeeDueDateDetailsNew;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FeeAsWithDDateDaoImpl implements FeeAsWithDDateDao {

    @Override
    public boolean assignFeeWithDueDates(FeeAssignmentDetailsNew details, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            // Insert fee assignment
            String faSQL = "INSERT INTO fee_assignment (school_id, session_id, class_id, section_id, student_id, fee_id, dc_id, valid_from, valid_to, updated_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(faSQL, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, details.getSessionId());
                ps.setObject(3, details.getClassId());
                ps.setObject(4, details.getSectionId());
                ps.setObject(5, details.getStudentId());
                ps.setInt(6, details.getFeeId());
                ps.setObject(7, details.getDcId());
                ps.setDate(8, Date.valueOf(details.getValidFrom()));
                ps.setObject(9, details.getValidTo() != null ? Date.valueOf(details.getValidTo()) : null);
                ps.setInt(10, details.getUpdatedBy());
                return ps;
            }, keyHolder);

            if (rowsAffected == 0 || keyHolder.getKey() == null) {
                return false;
            }
           // int faId = keyHolder.getKey().intValue();

            Number faIdNumber = (Number) keyHolder.getKeys().get("fa_id");
            if (faIdNumber == null) {
                throw new IllegalStateException("fa_id not returned from insert.");
            }
            int faId = faIdNumber.intValue();


            // Insert due dates
            String fddtSQL = "INSERT INTO fee_due_date (school_id, fa_id, due_date, fee_amount, discount_amount, updated_by, update_date_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            List<FeeDueDateDetailsNew> dueDates = details.getDueDates();
            jdbcTemplate.batchUpdate(fddtSQL, dueDates, dueDates.size(), (ps, due) -> {
                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, faId);
                ps.setDate(3, Date.valueOf(due.getDueDate()));
                ps.setInt(4, due.getFeeAmount());
                ps.setInt(5, due.getDiscountAmount());
                ps.setInt(6, details.getUpdatedBy());
            });

            return true;

        } catch (DuplicateKeyException e) {
            throw new IllegalStateException("Duplicate fee assignment detected", e);
        } catch (DataAccessException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error: " + e.getMessage(), e);
        } finally {
            if (jdbcTemplate != null) {
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
        }
    }

    @Override
    public boolean existsClassAssignment(String schoolCode, int schoolId, int sessionId, int classId, int feeId, String validFrom, String validTo) {
        return checkAssignmentExists(schoolCode, schoolId, sessionId, classId, null, null, feeId, validFrom, validTo);
    }

    @Override
    public boolean existsSectionAssignment(String schoolCode, int schoolId, int sessionId, int classId, int sectionId, int feeId, String validFrom, String validTo) {
        return checkAssignmentExists(schoolCode, schoolId, sessionId, classId, sectionId, null, feeId, validFrom, validTo);
    }

    @Override
    public boolean existsStudentAssignment(String schoolCode, int schoolId, int sessionId, int studentId, int feeId, String validFrom, String validTo) {
        return checkAssignmentExists(schoolCode, schoolId, sessionId, null, null, studentId, feeId, validFrom, validTo);
    }

    private boolean checkAssignmentExists(String schoolCode, int schoolId, int sessionId,
                                          Integer classId, Integer sectionId, Integer studentId,
                                          int feeId, String validFrom, String validTo) {
        try {
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            String sql = "SELECT COUNT(*) FROM fee_assignment " +
                    "WHERE school_id = ? " +
                    "AND session_id = ? " +
                    "AND fee_id = ? " +
                    "AND (class_id = ? OR (CAST(? AS INTEGER) IS NULL AND class_id IS NULL)) " +
                    "AND (section_id = ? OR (CAST(? AS INTEGER) IS NULL AND section_id IS NULL)) " +
                    "AND (student_id = ? OR (CAST(? AS INTEGER) IS NULL AND student_id IS NULL)) " +
                    "AND (valid_from <= COALESCE(?::date, '9999-12-31') " +
                    "AND COALESCE(valid_to, '9999-12-31') >= ?::date)";
            Integer count = jdbcTemplate.queryForObject(
                    sql,
                    Integer.class,
                    schoolId,
                    sessionId,
                    feeId,
                    classId, classId,
                    sectionId, sectionId,
                    studentId, studentId,
                    validTo != null ? validTo : "9999-12-31",
                    validFrom
            );

            return count != null && count > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("Database error during duplicate check", e);
        }
    }

}*/

package com.sms.dao.impl;

import com.sms.dao.FeeAsWithDDateDao;
import com.sms.exception.FeeAssignmentConflictException;
import com.sms.model.FeeAssignmentDetailsNew;
import com.sms.model.FeeDueDateDetailsNew;
import com.sms.model.StudentFeeStatus;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FeeAsWithDDateDaoImpl implements FeeAsWithDDateDao {
    private final JdbcTemplate jdbcTemplate;
    LocalDate today = LocalDate.now();

    public FeeAsWithDDateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean assignFeeWithDueDates(FeeAssignmentDetailsNew details, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            if (existsFeeAssignment(details, schoolCode)) {
                throw new FeeAssignmentConflictException("Fee assignment already exists for this session/class/section/student/fee");
            }

            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            // Insert fee assignment
            String faSQL = "INSERT INTO fee_assignment (school_id, session_id, class_id, section_id, student_id, fee_id, dc_id, valid_from, valid_to, updated_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(faSQL, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, details.getSessionId());
                ps.setObject(3, details.getClassId());
                ps.setObject(4, details.getSectionId());
                ps.setObject(5, details.getStudentId());
                ps.setInt(6, details.getFeeId());
                ps.setObject(7, details.getDcId());
                ps.setDate(8, Date.valueOf(details.getValidFrom()));
                ps.setObject(9, details.getValidTo() != null ? Date.valueOf(details.getValidTo()) : null);
                ps.setInt(10, details.getUpdatedBy());
                return ps;
            }, keyHolder);

            // Only check rows affected - don't use keyHolder.getKey()
            if (rowsAffected == 0) {
                return false;
            }

            // Get the map of all generated keys
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys == null || keys.isEmpty()) {
                throw new IllegalStateException("No generated keys returned from insert.");
            }

            // Verify fa_id exists in the key map
            if (!keys.containsKey("fa_id")) {
                throw new IllegalStateException("fa_id not found in generated keys.");
            }

            // Extract and validate fa_id value
            Number faIdNumber = (Number) keys.get("fa_id");
            if (faIdNumber == null) {
                throw new IllegalStateException("fa_id value is null in generated keys.");
            }
            int faId = faIdNumber.intValue();

            // Insert due dates
            String fddtSQL = "INSERT INTO fee_due_date (school_id, fa_id, due_date, fee_amount, discount_amount, updated_by, update_date_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            List<FeeDueDateDetailsNew> dueDates = details.getDueDates();
            jdbcTemplate.batchUpdate(fddtSQL, dueDates, dueDates.size(), (ps, due) -> {
                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, faId);
                ps.setDate(3, Date.valueOf(due.getDueDate()));
                ps.setInt(4, due.getFeeAmount());
                ps.setInt(5, due.getDiscountAmount());
                ps.setInt(6, details.getUpdatedBy());
            });

            return true;

        } catch (DuplicateKeyException e) {
            // Let controller send 409
            throw new FeeAssignmentConflictException("Duplicate fee assignment detected", e);

        } catch (DataAccessException e) {
            // Only wrap DB-related issues
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        } finally {
            if (jdbcTemplate != null) {
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
        }
    }

    private boolean existsFeeAssignment(FeeAssignmentDetailsNew feeAssignmentDetails, String schoolCode) {
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
    private boolean existsWithSessionClassFee(FeeAssignmentDetailsNew feeAssignmentDetails, JdbcTemplate jdbcTemplate) {
        String sql = "SELECT EXISTS(SELECT 1 FROM fee_assignment WHERE session_id = ? AND class_id = ? AND fee_id = ? AND section_id IS NULL AND student_id IS NULL)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                feeAssignmentDetails.getSessionId(),
                feeAssignmentDetails.getClassId(),
                feeAssignmentDetails.getFeeId());
        return Boolean.TRUE.equals(exists); // Safe unboxing that handles null
    }

    private boolean existsWithSessionClassSectionFee(FeeAssignmentDetailsNew feeAssignmentDetails, JdbcTemplate jdbcTemplate) {
        if (feeAssignmentDetails.getSectionId() == null) return false; // Skip if no section
        String sql = "SELECT EXISTS(SELECT 1 FROM fee_assignment WHERE session_id = ? AND class_id = ? AND section_id = ? AND fee_id = ? AND student_id IS NULL)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                feeAssignmentDetails.getSessionId(),
                feeAssignmentDetails.getClassId(),
                feeAssignmentDetails.getSectionId(),
                feeAssignmentDetails.getFeeId());
        return Boolean.TRUE.equals(exists); // Safe unboxing that handles null
    }

    private boolean existsWithSessionClassSectionStudentFee(FeeAssignmentDetailsNew feeAssignmentDetails, JdbcTemplate jdbcTemplate) {
        if (feeAssignmentDetails.getStudentId() == null) return false; // Skip if no student
        String sql = "SELECT EXISTS(SELECT 1 FROM fee_assignment WHERE session_id = ? AND class_id = ? AND section_id = ? AND student_id = ? AND fee_id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class,
                feeAssignmentDetails.getSessionId(),
                feeAssignmentDetails.getClassId(),
                feeAssignmentDetails.getSectionId(),
                feeAssignmentDetails.getStudentId(),
                feeAssignmentDetails.getFeeId());
        return Boolean.TRUE.equals(exists);
    }


//    @Override
//    public FeeAssignmentDetailsNew getFeeAssignmentDetailsWithStudents(long faId, String schoolCode) {
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//
//        // 1. Get main fee assignment
//        String sqlFeeDetails = """
//                SELECT
//                    fa.fa_id,
//                    fa.school_id,
//                    fa.session_id,
//                    s.academic_session,
//                    fa.class_id,
//                    mc.class_name,
//                    fa.section_id,
//                    ms.section_name,
//                    fa.student_id,
//                    CASE
//                                        WHEN fa.student_id IS NOT NULL
//                                        THEN CONCAT(spd.first_name, ' ', spd.last_name)
//                                        ELSE NULL
//                                    END AS student_name,
//                    fa.fee_id,
//                    sf.fee_type,
//                    fa.dc_id,
//                    dc.dc_description,
//                    TO_CHAR(fa.valid_from, 'YYYY-MM-DD') AS valid_from,
//                    TO_CHAR(fa.valid_to, 'YYYY-MM-DD') AS valid_to,
//                    CASE
//                        WHEN EXISTS (
//                            SELECT 1
//                            FROM fee_deposit_details fdd
//                            WHERE fdd.fa_id = fa.fa_id
//                        ) THEN TRUE
//                        ELSE FALSE
//                    END AS has_deposit
//                FROM
//                    fee_assignment fa
//                LEFT JOIN session s
//                    ON fa.session_id = s.session_id
//                LEFT JOIN mst_class mc
//                    ON fa.class_id = mc.class_id
//                LEFT JOIN mst_section ms
//                    ON fa.section_id = ms.section_id
//                LEFT JOIN school_fees sf
//                    ON fa.fee_id = sf.fee_id
//                LEFT JOIN discount_code dc
//                    ON fa.dc_id = dc.dc_id
//                LEFT JOIN student_personal_details spd
//                    ON spd.student_id = fa.student_id
//                WHERE
//                CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, CURRENT_DATE)
//                AND
//                    fa.fa_id = ?;
//    """;
//
//        FeeAssignmentDetailsNew details = jdbcTemplate.queryForObject(
//                sqlFeeDetails,
//                (rs, rowNum) -> mapFeeAssignment(rs),
//                faId
//        );
//
//        String sqlDueDates = """
//SELECT fddt.fddt_id,
//       TO_CHAR(fddt.due_date, 'YYYY-MM-DD') AS due_date,
//       fddt.fee_amount,
//       fddt.discount_amount,
//       CASE
//           WHEN EXISTS (
//               SELECT 1
//               FROM fee_deposit_details fdd
//               WHERE fdd.fa_id = fddt.fa_id
//                 AND fdd.fddt_id = fddt.fddt_id
//           ) THEN TRUE ELSE FALSE
//       END AS is_deposited
//FROM fee_due_date fddt
//WHERE fddt.fa_id = ?
//  AND fddt.due_date BETWEEN
//      (SELECT valid_from FROM fee_assignment WHERE fa_id = ?)
//      AND
//      COALESCE((SELECT valid_to FROM fee_assignment WHERE fa_id = ?), fddt.due_date)
//""";
//
//// Then update query call
//        List<FeeDueDateDetailsNew> dueDates = jdbcTemplate.query(
//                sqlDueDates,
//                (rs, rowNum) -> mapDueDate(rs),
//                faId, faId, faId  // Pass faId three times
//        );
//        details.setDueDates(dueDates);
//
//        // 3. Get student list based on assignment type
//        if (details.getStudentId() != null) {
//            // Case 1: Fee assigned to specific student
//            String sqlSingleStudent = """
//           SELECT s.student_id,
//                              CONCAT(s.first_name, ' ', s.last_name) AS student_name,
//                              CASE WHEN EXISTS (
//                                  SELECT 1
//                                  FROM fee_assignment_exclusion fae
//                                  WHERE fae.fa_id = ?
//                                    AND fae.student_id = s.student_id
//                                    AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
//                              ) THEN 1 ELSE 0 END AS excluded
//                       FROM student_personal_details s
//                       WHERE s.student_id = ?
//        """;
//
//            List<StudentFeeStatus> students = jdbcTemplate.query(
//                    sqlSingleStudent,
//                    (rs, rowNum) -> mapStudent(rs),
//                    faId,
//                    details.getStudentId()
//            );
//            details.setStudents(students);
//        } else if (details.getClassId() != null && details.getSectionId() != null) {
//            // Case 2: Fee assigned to specific class and section
//            String sqlClassSectionStudents = """
//SELECT s.student_id,
//       CONCAT(s.first_name, ' ', s.last_name) AS student_name,
//       CASE WHEN EXISTS (
//           SELECT 1
//           FROM fee_assignment_exclusion fae
//           WHERE fae.fa_id = ?
//             AND fae.student_id = sad.student_id
//             AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
//       ) THEN 1 ELSE 0 END AS excluded
//FROM student_academic_details sad
//JOIN student_personal_details s ON s.student_id = sad.student_id
//WHERE sad.session_id = ?
//  AND sad.student_class_id = ?
//  AND sad.student_section_id = ?
//""";
//
//            List<StudentFeeStatus> students = jdbcTemplate.query(
//                    sqlClassSectionStudents,
//                    (rs, rowNum) -> mapStudent(rs),
//                    faId,
//                    details.getSessionId(),
//                    details.getClassId(),
//                    details.getSectionId()
//            );
//            details.setStudents(students);
//        } else if (details.getClassId() != null) {
//            // Case 3: Fee assigned to entire class
//            String sqlClassStudents = """
//SELECT s.student_id,
//       CONCAT(s.first_name, ' ', s.last_name) AS student_name,
//       CASE WHEN EXISTS (
//           SELECT 1
//           FROM fee_assignment_exclusion fae
//           WHERE fae.fa_id = ?
//             AND fae.student_id = sad.student_id
//             AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
//       ) THEN 1 ELSE 0 END AS excluded
//FROM student_academic_details sad
//JOIN student_personal_details s ON s.student_id = sad.student_id
//WHERE sad.session_id = ?
//  AND sad.student_class_id = ?
//""";
//
//            List<StudentFeeStatus> students = jdbcTemplate.query(
//                    sqlClassStudents,
//                    (rs, rowNum) -> mapStudent(rs),
//                    faId,
//                    details.getSessionId(),
//                    details.getClassId()
//            );
//            details.setStudents(students);
//        } else {
//            // Case 4: Shouldn't happen as fee must be assigned to either student, class, or class+section
//            details.setStudents(new ArrayList<>());
//        }
//
//        return details;
//    }

@Override
public FeeAssignmentDetailsNew getFeeAssignmentDetailsWithStudents(long faId, String schoolCode) {
    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

    // 1. Get main fee assignment
    String sqlFeeDetails = """
            SELECT
                fa.fa_id,
                fa.school_id,
                fa.session_id,
                s.academic_session,
                fa.class_id,
                mc.class_name,
                fa.section_id,
                ms.section_name,
                fa.student_id,
                CASE
                                    WHEN fa.student_id IS NOT NULL
                                    THEN CONCAT(spd.first_name, ' ', spd.last_name)
                                    ELSE NULL
                                END AS student_name,
                fa.fee_id,
                sf.fee_type,
                fa.dc_id,
                dc.dc_description,
                TO_CHAR(fa.valid_from, 'YYYY-MM-DD') AS valid_from,
                TO_CHAR(fa.valid_to, 'YYYY-MM-DD') AS valid_to,
                CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM fee_deposit_details fdd
                        WHERE fdd.fa_id = fa.fa_id
                    ) THEN TRUE
                    ELSE FALSE
                END AS has_deposit
            FROM
                fee_assignment fa
            LEFT JOIN session s
                ON fa.session_id = s.session_id
            LEFT JOIN mst_class mc
                ON fa.class_id = mc.class_id
            LEFT JOIN mst_section ms
                ON fa.section_id = ms.section_id
            LEFT JOIN school_fees sf
                ON fa.fee_id = sf.fee_id
            LEFT JOIN discount_code dc
                ON fa.dc_id = dc.dc_id
            LEFT JOIN student_personal_details spd
                ON spd.student_id = fa.student_id
            WHERE
            CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, CURRENT_DATE)
            AND
                fa.fa_id = ?;
""";

    FeeAssignmentDetailsNew details = jdbcTemplate.queryForObject(
            sqlFeeDetails,
            (rs, rowNum) -> mapFeeAssignment(rs),
            faId
    );

    String sqlDueDates = """
SELECT fddt.fddt_id,
       TO_CHAR(fddt.due_date, 'YYYY-MM-DD') AS due_date,
       fddt.fee_amount,
       fddt.discount_amount,
       CASE 
           WHEN EXISTS (
               SELECT 1
               FROM fee_deposit_details fdd 
               WHERE fdd.fa_id = fddt.fa_id
                 AND fdd.fddt_id = fddt.fddt_id
           ) THEN TRUE ELSE FALSE
       END AS is_deposited
FROM fee_due_date fddt
WHERE fddt.fa_id = ?
  AND fddt.due_date BETWEEN 
      (SELECT valid_from FROM fee_assignment WHERE fa_id = ?) 
      AND 
      COALESCE((SELECT valid_to FROM fee_assignment WHERE fa_id = ?), fddt.due_date)
""";

    List<FeeDueDateDetailsNew> dueDates = jdbcTemplate.query(
            sqlDueDates,
            (rs, rowNum) -> mapDueDate(rs),
            faId, faId, faId  // Pass faId three times
    );
    details.setDueDates(dueDates);

    // 3. Get student list based on assignment type
    if (details.getStudentId() != null) {
        // Case 1: Fee assigned to specific student
        String sqlSingleStudent = """
       SELECT s.student_id,
                          CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                          s.gender,
                          sad.student_type,
                          CASE WHEN EXISTS (
                              SELECT 1
                              FROM fee_assignment_exclusion fae
                              WHERE fae.fa_id = ?
                                AND fae.student_id = s.student_id
                                AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
                          ) THEN 1 ELSE 0 END AS excluded
                   FROM student_personal_details s
                   JOIN student_academic_details sad ON s.student_id = sad.student_id
                   WHERE s.student_id = ?
                   AND sad.session_id = ?
                   -- Check if fee type matches student's attributes
                   AND EXISTS (
                       SELECT 1 FROM school_fees sf2 
                       JOIN fee_assignment fa2 ON sf2.fee_id = fa2.fee_id
                       WHERE fa2.fa_id = ?
                       AND (
                           -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
                           (UPPER(sf2.fee_type) LIKE '% - OLD' AND UPPER(sad.student_type) = 'OLD')
                           -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
                           OR (UPPER(sf2.fee_type) LIKE '% - NEW' AND UPPER(sad.student_type) = 'NEW')
                           -- Case 3: Fee type has "- Male" suffix, check gender = 'Male'
                           OR (UPPER(sf2.fee_type) LIKE '% - MALE' AND UPPER(s.gender) = 'MALE')
                           -- Case 4: Fee type has "- Female" suffix, check gender = 'Female'
                           OR (UPPER(sf2.fee_type) LIKE '% - FEMALE' AND UPPER(s.gender) = 'FEMALE')
                           -- Case 5: Fee type doesn't have any gender/student_type suffix
                           OR (
                               UPPER(sf2.fee_type) NOT LIKE '% - OLD' 
                               AND UPPER(sf2.fee_type) NOT LIKE '% - NEW'
                               AND UPPER(sf2.fee_type) NOT LIKE '% - MALE'
                               AND UPPER(sf2.fee_type) NOT LIKE '% - FEMALE'
                           )
                       )
                   )
    """;

        List<StudentFeeStatus> students = jdbcTemplate.query(
                sqlSingleStudent,
                (rs, rowNum) -> mapStudent(rs),
                faId,
                details.getStudentId(),
                details.getSessionId(),
                faId
        );
        details.setStudents(students);

        // If no student found (fee doesn't match student attributes), return empty list
        if (students.isEmpty()) {
            details.setStudents(new ArrayList<>());
        }

    } else if (details.getClassId() != null && details.getSectionId() != null) {
        // Case 2: Fee assigned to specific class and section
        String sqlClassSectionStudents = """
SELECT s.student_id, 
       CONCAT(s.first_name, ' ', s.last_name) AS student_name,
       s.gender,
       sad.student_type,
       CASE WHEN EXISTS (
           SELECT 1 
           FROM fee_assignment_exclusion fae
           WHERE fae.fa_id = ? 
             AND fae.student_id = sad.student_id
             AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
       ) THEN 1 ELSE 0 END AS excluded
FROM student_academic_details sad
JOIN student_personal_details s ON s.student_id = sad.student_id
WHERE sad.session_id = ?
  AND sad.student_class_id = ?
  AND sad.student_section_id = ?
  -- Check if fee type matches student's attributes
  AND EXISTS (
      SELECT 1 FROM school_fees sf2 
      JOIN fee_assignment fa2 ON sf2.fee_id = fa2.fee_id
      WHERE fa2.fa_id = ?
      AND (
          -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
          (UPPER(sf2.fee_type) LIKE '% - OLD' AND UPPER(sad.student_type) = 'OLD')
          -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
          OR (UPPER(sf2.fee_type) LIKE '% - NEW' AND UPPER(sad.student_type) = 'NEW')
          -- Case 3: Fee type has "- Male" suffix, check gender = 'Male'
          OR (UPPER(sf2.fee_type) LIKE '% - MALE' AND UPPER(s.gender) = 'MALE')
          -- Case 4: Fee type has "- Female" suffix, check gender = 'Female'
          OR (UPPER(sf2.fee_type) LIKE '% - FEMALE' AND UPPER(s.gender) = 'FEMALE')
          -- Case 5: Fee type doesn't have any gender/student_type suffix
          OR (
              UPPER(sf2.fee_type) NOT LIKE '% - OLD' 
              AND UPPER(sf2.fee_type) NOT LIKE '% - NEW'
              AND UPPER(sf2.fee_type) NOT LIKE '% - MALE'
              AND UPPER(sf2.fee_type) NOT LIKE '% - FEMALE'
          )
      )
  )
""";

        List<StudentFeeStatus> students = jdbcTemplate.query(
                sqlClassSectionStudents,
                (rs, rowNum) -> mapStudent(rs),
                faId,
                details.getSessionId(),
                details.getClassId(),
                details.getSectionId(),
                faId
        );
        details.setStudents(students);

    } else if (details.getClassId() != null) {
        // Case 3: Fee assigned to entire class
        String sqlClassStudents = """
SELECT s.student_id, 
       CONCAT(s.first_name, ' ', s.last_name) AS student_name,
       s.gender,
       sad.student_type,
       CASE WHEN EXISTS (
           SELECT 1 
           FROM fee_assignment_exclusion fae
           WHERE fae.fa_id = ? 
             AND fae.student_id = sad.student_id
             AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
       ) THEN 1 ELSE 0 END AS excluded
FROM student_academic_details sad
JOIN student_personal_details s ON s.student_id = sad.student_id
WHERE sad.session_id = ?
  AND sad.student_class_id = ?
  -- Check if fee type matches student's attributes
  AND EXISTS (
      SELECT 1 FROM school_fees sf2 
      JOIN fee_assignment fa2 ON sf2.fee_id = fa2.fee_id
      WHERE fa2.fa_id = ?
      AND (
          -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
          (UPPER(sf2.fee_type) LIKE '% - OLD' AND UPPER(sad.student_type) = 'OLD')
          -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
          OR (UPPER(sf2.fee_type) LIKE '% - NEW' AND UPPER(sad.student_type) = 'NEW')
          -- Case 3: Fee type has "- Male" suffix, check gender = 'Male'
          OR (UPPER(sf2.fee_type) LIKE '% - MALE' AND UPPER(s.gender) = 'MALE')
          -- Case 4: Fee type has "- Female" suffix, check gender = 'Female'
          OR (UPPER(sf2.fee_type) LIKE '% - FEMALE' AND UPPER(s.gender) = 'FEMALE')
          -- Case 5: Fee type doesn't have any gender/student_type suffix
          OR (
              UPPER(sf2.fee_type) NOT LIKE '% - OLD' 
              AND UPPER(sf2.fee_type) NOT LIKE '% - NEW'
              AND UPPER(sf2.fee_type) NOT LIKE '% - MALE'
              AND UPPER(sf2.fee_type) NOT LIKE '% - FEMALE'
          )
      )
  )
""";

        List<StudentFeeStatus> students = jdbcTemplate.query(
                sqlClassStudents,
                (rs, rowNum) -> mapStudent(rs),
                faId,
                details.getSessionId(),
                details.getClassId(),
                faId
        );
        details.setStudents(students);

    } else {
        // Case 4: Shouldn't happen as fee must be assigned to either student, class, or class+section
        details.setStudents(new ArrayList<>());
    }

    return details;
}

private FeeAssignmentDetailsNew mapFeeAssignment(ResultSet rs) throws SQLException {
    FeeAssignmentDetailsNew fa = new FeeAssignmentDetailsNew();
    fa.setFaId(rs.getLong("fa_id"));
    fa.setSchoolId(rs.getInt("school_id"));
    fa.setSessionId(rs.getInt("session_id"));

    // Convert 0 to null for class/section/student IDs
    fa.setClassId(rs.getInt("class_id"));
    fa.setSectionId((Integer) rs.getObject("section_id"));
    fa.setStudentId(convertZeroToNull(rs.getObject("student_id")));

    fa.setFeeId(rs.getInt("fee_id"));
    fa.setDcId((Integer) rs.getObject("dc_id"));
    fa.setAcademicSession(rs.getString("academic_session"));
    fa.setClassName(rs.getString("class_name"));
    fa.setSectionName(rs.getString("section_name"));
    fa.setStudentName(rs.getString("student_name"));  // Will be null for class assignments
    fa.setFeeType(rs.getString("fee_type"));
    fa.setDcDescription(rs.getString("dc_description"));
    fa.setValidFrom(rs.getString("valid_from"));
    fa.setValidTo(rs.getString("valid_to"));
    fa.setHasDeposit(rs.getBoolean("has_deposit"));
    return fa;
}

    // Helper method to convert 0 to null
    private Integer convertZeroToNull(Object value) throws SQLException {
        if (value == null) return null;
        int intValue = (Integer) value;
        return intValue == 0 ? null : intValue;
    }

    private FeeDueDateDetailsNew mapDueDate(ResultSet rs) throws SQLException {
        FeeDueDateDetailsNew dd = new FeeDueDateDetailsNew();
        dd.setDueDate(rs.getString("due_date"));
        dd.setFddtId(rs.getLong("fddt_id"));
        dd.setFeeAmount(rs.getInt("fee_amount"));
        dd.setDiscountAmount(rs.getInt("discount_amount"));
        dd.setDeposited(rs.getBoolean("is_deposited"));
        return dd;
    }

    private StudentFeeStatus mapStudent(ResultSet rs) throws SQLException {
        StudentFeeStatus s = new StudentFeeStatus();
        s.setStudentId(rs.getInt("student_id"));
        s.setStudentName(rs.getString("student_name"));
        s.setGender(rs.getString("gender"));
        s.setStudentType(rs.getString("student_type"));
        s.setExcluded(rs.getInt("excluded") == 1);
        return s;
    }

    @Override
  /*  public List<FeeAssignmentDetailsNew> getAllFeeAssignments(
            String schoolCode,
            Integer classId,
            Integer sectionId,
            Integer studentId
    ) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        String sqlFeeDetails = """
                SELECT
                    fa.fa_id,
                    fa.school_id,
                    fa.session_id,
                    s.academic_session,
                    fa.class_id,
                    c.class_name,
                    fa.section_id,
                    sec.section_name,
                    fa.student_id,
                    CASE
                           WHEN fa.student_id IS NOT NULL
                           THEN CONCAT(spd.first_name, ' ', spd.last_name)
                           ELSE NULL
                        END AS student_name,
                    fa.fee_id,
                    sf.fee_type,
                    fa.dc_id,
                    dc.dc_description,
                    TO_CHAR(fa.valid_from, 'YYYY-MM-DD') AS valid_from,
                    TO_CHAR(fa.valid_to, 'YYYY-MM-DD') AS valid_to,
                    CASE
                        WHEN EXISTS (
                            SELECT 1
                            FROM fee_deposit_details fdd
                            WHERE fdd.fa_id = fa.fa_id
                        ) THEN TRUE ELSE FALSE
                    END AS has_deposit
                FROM fee_assignment fa
                LEFT JOIN session s
                    ON s.session_id = fa.session_id
                LEFT JOIN mst_class c
                    ON c.class_id = fa.class_id
                LEFT JOIN mst_section sec
                    ON sec.section_id = fa.section_id
                LEFT JOIN student_personal_details spd
                    ON spd.student_id = fa.student_id
                LEFT JOIN school_fees sf
                    ON sf.fee_id = fa.fee_id
                LEFT JOIN discount_code dc
                    ON dc.dc_id = fa.dc_id
                WHERE fa.class_id = COALESCE(?, fa.class_id)
                  AND fa.section_id = COALESCE(?, fa.section_id)
                  AND fa.student_id = COALESCE(?, fa.student_id)
                  AND CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, CURRENT_DATE)
                """;
        List<FeeAssignmentDetailsNew> assignments = jdbcTemplate.query(
                sqlFeeDetails,
                (rs, rowNum) -> mapFeeAssignment(rs),
                classId,
                sectionId,
                studentId
        );

        String sqlDueDates = """
        SELECT fddt.fddt_id,
               TO_CHAR(fddt.due_date, 'YYYY-MM-DD') AS due_date,
               fddt.fee_amount,
               fddt.discount_amount,
               CASE 
                   WHEN EXISTS (
                       SELECT 1
                       FROM fee_deposit_details fdd 
                       WHERE fdd.fa_id = fddt.fa_id
                         AND fdd.fddt_id = fddt.fddt_id
                   ) THEN TRUE ELSE FALSE
               END AS is_deposited
        FROM fee_due_date fddt
        WHERE fddt.fa_id = ?
    """;

        for (FeeAssignmentDetailsNew fa : assignments) {
            List<FeeDueDateDetailsNew> dueDates = jdbcTemplate.query(
                    sqlDueDates,
                    (rs, rowNum) -> mapDueDate(rs),
                    fa.getFaId()
            );
            fa.setDueDates(dueDates);
        }

        return assignments;
    }*/

   /* public List<FeeAssignmentDetailsNew> getAllFeeAssignments(
            String schoolCode,
            Integer classId,
            Integer sectionId,
            Integer studentId
    ) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        // Base SQL with StringBuilder
        StringBuilder sqlFeeDetails = new StringBuilder("""
            SELECT
                fa.fa_id,
                fa.school_id,
                fa.session_id,
                s.academic_session,
                fa.class_id,
                c.class_name,
                fa.section_id,
                sec.section_name,
                fa.student_id,
                CASE
                       WHEN fa.student_id IS NOT NULL
                       THEN CONCAT(spd.first_name, ' ', spd.last_name)
                       ELSE NULL
                    END AS student_name,
                fa.fee_id,
                sf.fee_type,
                fa.dc_id,
                dc.dc_description,
                TO_CHAR(fa.valid_from, 'YYYY-MM-DD') AS valid_from,
                TO_CHAR(fa.valid_to, 'YYYY-MM-DD') AS valid_to,
                CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM fee_deposit_details fdd
                        WHERE fdd.fa_id = fa.fa_id
                    ) THEN TRUE ELSE FALSE
                END AS has_deposit
            FROM fee_assignment fa
            LEFT JOIN session s
                ON s.session_id = fa.session_id
            LEFT JOIN mst_class c
                ON c.class_id = fa.class_id
            LEFT JOIN mst_section sec
                ON sec.section_id = fa.section_id
            LEFT JOIN student_personal_details spd
                ON spd.student_id = fa.student_id
            LEFT JOIN school_fees sf
                ON sf.fee_id = fa.fee_id
            LEFT JOIN discount_code dc
             ON dc.dc_id = fa.dc_id
            LEFT JOIN fee_assignment_exclusion fae
                        ON fae.fa_id = fa.fa_id
                        AND fae.student_id = fa.student_id
                        AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)

            AND fae.exclusion_id IS NULL
            """);

        // Dynamic conditions and parameters
        List<Object> params = new ArrayList<>();

        if (classId != null) {
            sqlFeeDetails.append(" AND fa.class_id = ?");
            params.add(classId);
        }

        if (sectionId != null) {
            sqlFeeDetails.append(" AND (fa.section_id = ? OR fa.section_id IS NULL)");
            params.add(sectionId);
        }

        if (studentId != null) {
            sqlFeeDetails.append(" AND (fa.student_id = ? OR fa.student_id IS NULL)");
            params.add(studentId);
        }

        // Execute query with dynamic parameters
        List<FeeAssignmentDetailsNew> assignments = jdbcTemplate.query(
                sqlFeeDetails.toString(),
                (rs, rowNum) -> mapFeeAssignment(rs),
                params.toArray()
        );

        // Due dates query (unchanged)
        String sqlDueDates = """
        SELECT fddt.fddt_id,
               TO_CHAR(fddt.due_date, 'YYYY-MM-DD') AS due_date,
               fddt.fee_amount,
               fddt.discount_amount,
               CASE 
                   WHEN EXISTS (
                       SELECT 1
                       FROM fee_deposit_details fdd 
                       WHERE fdd.fa_id = fddt.fa_id
                         AND fdd.fddt_id = fddt.fddt_id
                   ) THEN TRUE ELSE FALSE
               END AS is_deposited
        FROM fee_due_date fddt
        WHERE fddt.fa_id = ?
    """;

        for (FeeAssignmentDetailsNew fa : assignments) {
            List<FeeDueDateDetailsNew> dueDates = jdbcTemplate.query(
                    sqlDueDates,
                    (rs, rowNum) -> mapDueDate(rs),
                    fa.getFaId()
            );
            fa.setDueDates(dueDates);
        }

        return assignments;
    }*/

//    public List<FeeAssignmentDetailsNew> getAllFeeAssignments(
//            String schoolCode,
//            Integer classId,
//            Integer sectionId,
//            Integer studentId
//    ) {
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//
//        // Base SQL
//        StringBuilder sqlFeeDetails = new StringBuilder("""
//        SELECT
//            fa.fa_id,
//            fa.school_id,
//            fa.session_id,
//            s.academic_session,
//            fa.class_id,
//            c.class_name,
//            fa.section_id,
//            sec.section_name,
//            fa.student_id,
//            CASE
//                   WHEN fa.student_id IS NOT NULL
//                   THEN CONCAT(spd.first_name, ' ', spd.last_name)
//                   ELSE NULL
//                END AS student_name,
//            fa.fee_id,
//            sf.fee_type,
//            fa.dc_id,
//            dc.dc_description,
//            TO_CHAR(fa.valid_from, 'YYYY-MM-DD') AS valid_from,
//            TO_CHAR(fa.valid_to, 'YYYY-MM-DD') AS valid_to,
//            CASE
//                WHEN EXISTS (
//                    SELECT 1
//                    FROM fee_deposit_details fdd
//                    WHERE fdd.fa_id = fa.fa_id
//                ) THEN TRUE ELSE FALSE
//            END AS has_deposit
//        FROM fee_assignment fa
//        LEFT JOIN session s
//            ON s.session_id = fa.session_id
//        LEFT JOIN mst_class c
//            ON c.class_id = fa.class_id
//        LEFT JOIN mst_section sec
//            ON sec.section_id = fa.section_id
//        LEFT JOIN student_personal_details spd
//            ON spd.student_id = fa.student_id
//        LEFT JOIN school_fees sf
//            ON sf.fee_id = fa.fee_id
//        LEFT JOIN discount_code dc
//            ON dc.dc_id = fa.dc_id
//        WHERE CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, CURRENT_DATE)
//    """);
//
//        List<Object> params = new ArrayList<>();
//
//        // Dynamic filters
//        if (classId != null) {
//            sqlFeeDetails.append(" AND fa.class_id = ?");
//            params.add(classId);
//        }
//
//        if (sectionId != null) {
//            sqlFeeDetails.append(" AND (fa.section_id = ? OR fa.section_id IS NULL)");
//            params.add(sectionId);
//        }
//
//        if (studentId != null) {
//            sqlFeeDetails.append(" AND (fa.student_id = ? OR fa.student_id IS NULL)");
//            params.add(studentId);
//
//            // Add exclusion check for this student
//            sqlFeeDetails.append("""
//            AND NOT EXISTS (
//                SELECT 1
//                FROM fee_assignment_exclusion fae
//                WHERE fae.fa_id = fa.fa_id
//                  AND fae.student_id = ?
//                  AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
//            )
//        """);
//            params.add(studentId);
//        }
//
//        // Execute the main query
//        List<FeeAssignmentDetailsNew> assignments = jdbcTemplate.query(
//                sqlFeeDetails.toString(),
//                (rs, rowNum) -> mapFeeAssignment(rs),
//                params.toArray()
//        );
//
//        // Query to fetch due dates for each assignment
//        String sqlDueDates = """
//        SELECT fddt.fddt_id,
//               TO_CHAR(fddt.due_date, 'YYYY-MM-DD') AS due_date,
//               fddt.fee_amount,
//               fddt.discount_amount,
//               CASE
//                   WHEN EXISTS (
//                       SELECT 1
//                       FROM fee_deposit_details fdd
//                       WHERE fdd.fa_id = fddt.fa_id
//                         AND fdd.fddt_id = fddt.fddt_id
//                   ) THEN TRUE ELSE FALSE
//               END AS is_deposited
//        FROM fee_due_date fddt
//        WHERE fddt.fa_id = ?
//    """;
//
//        // Fetch due dates for each assignment
//        for (FeeAssignmentDetailsNew fa : assignments) {
//            List<FeeDueDateDetailsNew> dueDates = jdbcTemplate.query(
//                    sqlDueDates,
//                    (rs, rowNum) -> mapDueDate(rs),
//                    fa.getFaId()
//            );
//            fa.setDueDates(dueDates);
//        }
//
//        return assignments;
//    }
    public List<FeeAssignmentDetailsNew> getAllFeeAssignments(
            String schoolCode,
            Integer classId,
            Integer sectionId,
            Integer studentId
    ) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        // Base SQL
        StringBuilder sqlFeeDetails = new StringBuilder("""
        SELECT
            fa.fa_id,
            fa.school_id,
            fa.session_id,
            s.academic_session,
            fa.class_id,
            c.class_name,
            fa.section_id,
            sec.section_name,
            fa.student_id,
            CASE
                WHEN fa.student_id IS NOT NULL
                THEN CONCAT(spd.first_name, ' ', spd.last_name)
                ELSE NULL
            END AS student_name,
            fa.fee_id,
            sf.fee_type,
            fa.dc_id,
            dc.dc_description,
            TO_CHAR(fa.valid_from, 'YYYY-MM-DD') AS valid_from,
            TO_CHAR(fa.valid_to, 'YYYY-MM-DD') AS valid_to,
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM fee_deposit_details fdd
                    WHERE fdd.fa_id = fa.fa_id
                ) THEN TRUE ELSE FALSE
            END AS has_deposit
        FROM fee_assignment fa
        LEFT JOIN session s
            ON s.session_id = fa.session_id
        LEFT JOIN mst_class c
            ON c.class_id = fa.class_id
        LEFT JOIN mst_section sec
            ON sec.section_id = fa.section_id
        LEFT JOIN student_personal_details spd
            ON spd.student_id = fa.student_id
        LEFT JOIN school_fees sf
            ON sf.fee_id = fa.fee_id
        LEFT JOIN discount_code dc
            ON dc.dc_id = fa.dc_id
        WHERE CURRENT_DATE BETWEEN fa.valid_from AND COALESCE(fa.valid_to, CURRENT_DATE)
    """);

        List<Object> params = new ArrayList<>();

        // Dynamic filters
        if (classId != null) {
            sqlFeeDetails.append(" AND fa.class_id = ?");
            params.add(classId);
        }

        if (sectionId != null) {
            sqlFeeDetails.append(" AND (fa.section_id = ? OR fa.section_id IS NULL)");
            params.add(sectionId);
        }

        if (studentId != null) {
            sqlFeeDetails.append(" AND (fa.student_id = ? OR fa.student_id IS NULL)");
            params.add(studentId);

            // Add exclusion check for this student
            sqlFeeDetails.append("""
            AND NOT EXISTS (
                SELECT 1
                FROM fee_assignment_exclusion fae
                WHERE fae.fa_id = fa.fa_id
                  AND fae.student_id = ?
                  AND CURRENT_DATE BETWEEN fae.valid_from AND COALESCE(fae.valid_to, CURRENT_DATE)
            )
        """);
            params.add(studentId);

            // Add Old/New and Male/Female filtering only when studentId is provided
            sqlFeeDetails.append("""
            AND (
                -- Case 1: Fee type has "- Old" suffix, check student_type = 'Old'
                (UPPER(sf.fee_type) LIKE '%% - OLD' AND EXISTS (
                    SELECT 1 FROM student_academic_details sad 
                    WHERE sad.student_id = ? 
                    AND sad.student_type = 'Old'
                ))
                -- Case 2: Fee type has "- New" suffix, check student_type = 'New'
                OR (UPPER(sf.fee_type) LIKE '%% - NEW' AND EXISTS (
                    SELECT 1 FROM student_academic_details sad 
                    WHERE sad.student_id = ? 
                    AND sad.student_type = 'New'
                ))
                -- Case 3: Fee type has "- Male" suffix, check gender = 'Male'
                OR (UPPER(sf.fee_type) LIKE '%% - MALE' AND EXISTS (
                    SELECT 1 FROM student_personal_details spd2 
                    WHERE spd2.student_id = ? 
                    AND UPPER(spd2.gender) = 'MALE'
                ))
                -- Case 4: Fee type has "- Female" suffix, check gender = 'Female'
                OR (UPPER(sf.fee_type) LIKE '%% - FEMALE' AND EXISTS (
                    SELECT 1 FROM student_personal_details spd2 
                    WHERE spd2.student_id = ? 
                    AND UPPER(spd2.gender) = 'FEMALE'
                ))
                -- Case 5: Fee type doesn't have any gender/student_type suffix
                OR (
                    UPPER(sf.fee_type) NOT LIKE '%% - OLD' 
                    AND UPPER(sf.fee_type) NOT LIKE '%% - NEW'
                    AND UPPER(sf.fee_type) NOT LIKE '%% - MALE'
                    AND UPPER(sf.fee_type) NOT LIKE '%% - FEMALE'
                )
            )
        """);
            params.add(studentId);
            params.add(studentId);
            params.add(studentId);
            params.add(studentId);
        }

        // Execute the main query
        List<FeeAssignmentDetailsNew> assignments = jdbcTemplate.query(
                sqlFeeDetails.toString(),
                (rs, rowNum) -> mapFeeAssignment(rs),
                params.toArray()
        );

        // Query to fetch due dates for each assignment
        String sqlDueDates = """
        SELECT fddt.fddt_id,
               TO_CHAR(fddt.due_date, 'YYYY-MM-DD') AS due_date,
               fddt.fee_amount,
               fddt.discount_amount,
               CASE
                   WHEN EXISTS (
                       SELECT 1
                       FROM fee_deposit_details fdd
                       WHERE fdd.fa_id = fddt.fa_id
                         AND fdd.fddt_id = fddt.fddt_id
                   ) THEN TRUE ELSE FALSE
               END AS is_deposited
        FROM fee_due_date fddt
        WHERE fddt.fa_id = ?
    """;

        // Fetch due dates for each assignment
        for (FeeAssignmentDetailsNew fa : assignments) {
            List<FeeDueDateDetailsNew> dueDates = jdbcTemplate.query(
                    sqlDueDates,
                    (rs, rowNum) -> mapDueDate(rs),
                    fa.getFaId()
            );
            fa.setDueDates(dueDates);
        }

        return assignments;
    }

    @Override
    @Transactional
    public void updateFeeAssignment(FeeAssignmentDetailsNew details, String schoolCode) {
        Date validFrom = Date.valueOf(LocalDate.parse(details.getValidFrom()));
        Date validTo = Date.valueOf(LocalDate.parse(details.getValidTo()));
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        //  1. Update main fee_assignment record
        jdbcTemplate.update("""
        UPDATE fee_assignment
        SET class_id = ?, section_id = ?, student_id = ?, fee_id = ?, dc_id = ?,
            valid_from = ?, valid_to = ?, updated_by = ?
        WHERE fa_id = ?
    """,
                details.getClassId(),
                details.getSectionId(),
                details.getStudentId(),
                details.getFeeId(),
                details.getDcId(),
                validFrom,
                validTo,
                details.getUpdatedBy(),
                details.getFaId());

        // ✅ 2. Handle Due Dates (update or insert)
        List<FeeDueDateDetailsNew> dueDates = details.getDueDates();

        // First, fetch existing fddt_ids from DB for this fa_id
        List<Long> existingIds = jdbcTemplate.queryForList(
                "SELECT fddt_id FROM fee_due_date WHERE fa_id = ?",
                Long.class, details.getFaId()
        );
        Set<Long> sentIds = new HashSet<>();

        for (FeeDueDateDetailsNew dd : dueDates) {
            Date dueDateSql = Date.valueOf(LocalDate.parse(dd.getDueDate())); // ✅ Convert String to java.sql.Date

            if (dd.getFddtId() != null) {
                // Update existing
                jdbcTemplate.update("""
                UPDATE fee_due_date
                SET due_date = ?, fee_amount = ?, discount_amount = ?
                WHERE fddt_id = ? AND fa_id = ?
            """, dueDateSql, dd.getFeeAmount(), dd.getDiscountAmount(),
                        dd.getFddtId(), details.getFaId());
                sentIds.add(dd.getFddtId().longValue());
            } else {
                // Insert new
                jdbcTemplate.update("""
                INSERT INTO fee_due_date (fa_id, due_date, fee_amount, discount_amount)
                VALUES (?, ?, ?, ?)
            """, details.getFaId(), dueDateSql, dd.getFeeAmount(), dd.getDiscountAmount());
            }
        }

        // ✅ 3. Delete removed due dates
        for (Long id : existingIds) {
            if (!sentIds.contains(id)) {
                jdbcTemplate.update("DELETE FROM fee_due_date WHERE fddt_id = ?", id);
            }
        }

        // ✅ 4. Handle Student Exclusions
        jdbcTemplate.update("DELETE FROM fee_assignment_exclusion WHERE fa_id = ?", details.getFaId());
        if (details.getStudents() != null) {
            details.getStudents().stream()
                    .filter(s -> s.isExcluded())
                    .forEach(s -> jdbcTemplate.update("""
                INSERT INTO fee_assignment_exclusion (fa_id, student_id,valid_to,created_by)
                VALUES (?, ?,?,?)
            """, details.getFaId(), s.getStudentId(),validTo,details.getUpdatedBy()));
        }
    }

}


