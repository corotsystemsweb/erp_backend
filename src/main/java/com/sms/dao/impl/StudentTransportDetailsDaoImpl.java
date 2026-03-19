package com.sms.dao.impl;

import com.sms.dao.StudentTransportDetailsDao;
import com.sms.model.StudentTransportDetails;
import com.sms.model.StudentTransportDueDetails;
import com.sms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class StudentTransportDetailsDaoImpl implements StudentTransportDetailsDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StudentTransportDetailsDaoImpl.class);
    @Override
    public StudentTransportDetails addStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception {
        String sql = "INSERT INTO student_transport_details (school_id, session_id, student_id, route_id, fee, status, start_date, end_date) VALUES (?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, 1);
                ps.setInt(2, details.getSessionId());
                ps.setInt(3, details.getStudentId());
                ps.setInt(4, details.getRouteId());
                ps.setBigDecimal(5, details.getFee());
                ps.setString(6, "ACTIVE");
                ps.setDate(7, new java.sql.Date(details.getStartDate().getTime()));
                ps.setDate(8, new java.sql.Date(details.getEndDate().getTime()));
                return ps;
            }, keyHolder);
            if(keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("student_transport_id")){
                details.setStudentTransportId(((Number)keyHolder.getKeys().get("student_transport_id")).intValue());
            }
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return details;
    }

//    @Override
//    public StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception {
//        String sql = "UPDATE student_transport_details set student_id = ?, route_id = ?, fee = ?, status = ?, start_date = ?, end_date = ? WHERE student_transport_id = ?";
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        try{
//            int rowAffected = jdbcTemplate.update(sql,
//                    details.getStudentId(),
//                    details.getRouteId(),
//                    details.getFee(),
//                    details.getStatus(),
//                    new java.sql.Date(details.getStartDate().getTime()),
//                    new java.sql.Date(details.getEndDate().getTime()),
//                    details.getStudentTransportId()
//            );
//            if(rowAffected > 0){
//                return details;
//            }
//            return null;
//        } catch (Exception e){
//            e.printStackTrace();
//            return null;
//        } finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//    }

    //CHANGE: deactivate old + insert new
    @Override
    public StudentTransportDetails updateStudentTransportWithHistory(StudentTransportDetails details, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String deactivateSql = "UPDATE student_transport_details SET status = 'INACTIVE' WHERE student_transport_id = ? AND status = 'ACTIVE'";

        try {
            int rows = jdbcTemplate.update(deactivateSql, details.getStudentTransportId());

            if (rows == 0) return null;

            return addStudentTransportDetails(details, schoolCode);

        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    //CLOSE: only deactivate
    @Override
    public StudentTransportDetails deactivateStudentTransport(int studentTransportId, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        String sql = "UPDATE student_transport_details SET status = 'INACTIVE' WHERE student_transport_id = ? AND status = 'ACTIVE'";
        try {
            int rows = jdbcTemplate.update(sql, studentTransportId);
            return rows > 0 ? new StudentTransportDetails() : null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public StudentTransportDetails updateStudentTransportDetails(StudentTransportDetails details, String schoolCode) throws Exception {
        String sql = "UPDATE student_transport_details SET school_id = ?, session_id = ?, student_id = ?,  route_id = ?, fee = ?, status = ?, start_date = ?, end_date = ? WHERE student_transport_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rows = jdbcTemplate.update(sql,
                    details.getSchoolId(),
                    details.getSessionId(),
                    details.getStudentId(),
                    details.getRouteId(),
                    details.getFee(),
                    details.getStatus(),
                    new java.sql.Date(details.getStartDate().getTime()),
                    new java.sql.Date(details.getEndDate().getTime()),
                    details.getStudentTransportId()
            );
            return rows > 0 ? details : null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public StudentTransportDetails getStudentTransportDetails(
            int sessionId, Integer studentId, String schoolCode) throws Exception {

        String sql = """
    SELECT
        std.student_id,
        std.student_transport_id,
        std.session_id,
        std.school_id,
        std.route_id,
        std.fee,
        std.status,
        std.start_date,
        std.end_date,

        tfdue.fee_amount,
        tfdue.tfdue_id,
        tfdue.due_month,
        tfdue.discount_amount,
        tfdue.penalty_amount,

        COALESCE(tfdd.amount_paid, 0) AS paid_amount,

        COALESCE(
            tfdue.fee_amount
            - tfdue.discount_amount
            + tfdue.penalty_amount
            - COALESCE(tfdd.amount_paid, 0),
        0) AS due_amount

    FROM student_transport_details std

    INNER JOIN transport_fee_due tfdue
        ON tfdue.student_transport_id = std.student_transport_id

    LEFT JOIN (
        SELECT
            student_transport_id,
            due_month,
            SUM(amount_paid) AS amount_paid
        FROM transport_fee_deposit_details
        GROUP BY student_transport_id, due_month
    ) tfdd
        ON tfdd.student_transport_id = tfdue.student_transport_id
       AND tfdd.due_month = tfdue.due_month

    WHERE std.status = 'ACTIVE'
      AND std.session_id = ?
      AND std.student_id = ?

    ORDER BY tfdue.due_month
""";

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        final StudentTransportDetails[] result = { null };

        jdbcTemplate.query(sql, new Object[]{sessionId, studentId}, rs -> {

            if (result[0] == null) {
                StudentTransportDetails student = new StudentTransportDetails();
                student.setStudentTransportId(rs.getInt("student_transport_id"));
                student.setStudentId(rs.getInt("student_id"));
                student.setSessionId(rs.getInt("session_id"));
                student.setSchoolId(rs.getInt("school_id"));
                student.setRouteId(rs.getInt("route_id"));
                student.setFee(rs.getBigDecimal("fee"));
                student.setStatus(rs.getString("status"));
                student.setStartDate(rs.getDate("start_date"));
                student.setEndDate(rs.getDate("end_date"));
                student.setActionType("NONE");

                result[0] = student;
            }

            StudentTransportDueDetails due = new StudentTransportDueDetails();
            due.setFeeAmount(rs.getBigDecimal("fee_amount"));
            due.setTfdueId(rs.getInt("tfdue_id"));
            due.setDueMonth(rs.getDate("due_month"));
            due.setDiscount(rs.getBigDecimal("discount_amount"));
            due.setPenalty(rs.getBigDecimal("penalty_amount"));
            due.setPaidAmount(rs.getBigDecimal("paid_amount"));
            due.setDueAmount(rs.getBigDecimal("due_amount"));

            result[0].getDues().add(due);
        });

        return result[0]; // null if student not found
    }




    @Override
    public List<StudentTransportDetails> getStudentTransport(int sessionId, String schoolCode) throws Exception {

        String sql = """
    SELECT
        std.student_id,
        std.student_transport_id,
        std.session_id,
        std.school_id,
        std.route_id,
        std.fee,
        std.status,
        std.start_date,
        std.end_date,

        tfdue.fee_amount,
        tfdue.due_month,
        tfdue.discount_amount,
        tfdue.penalty_amount,

        COALESCE(tfdd.amount_paid, 0) AS paid_amount,

        COALESCE(
            tfdue.fee_amount
            - tfdue.discount_amount
            + tfdue.penalty_amount
            - COALESCE(tfdd.amount_paid, 0),
        0) AS due_amount

    FROM student_transport_details std

    INNER JOIN transport_fee_due tfdue
        ON tfdue.student_transport_id = std.student_transport_id

    LEFT JOIN (
        SELECT
            student_transport_id,
            due_month,
            SUM(amount_paid) AS amount_paid
        FROM transport_fee_deposit_details
        GROUP BY student_transport_id, due_month
    ) tfdd
        ON tfdd.student_transport_id = tfdue.student_transport_id
       AND tfdd.due_month = tfdue.due_month

    WHERE std.status = 'ACTIVE'
      AND std.session_id = ?

    ORDER BY std.student_id, tfdue.due_month
""";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        Map<Integer, StudentTransportDetails> map = new LinkedHashMap<>();

        jdbcTemplate.query(sql, new Object[]{sessionId}, rs -> {

            int transportId = rs.getInt("student_transport_id");

            StudentTransportDetails student = map.get(transportId);
            if (student == null) {
                student = new StudentTransportDetails();
                student.setStudentTransportId(transportId);
                student.setStudentId(rs.getInt("student_id"));
                student.setSessionId(rs.getInt("session_id"));
                student.setSchoolId(rs.getInt("school_id"));
                student.setRouteId(rs.getInt("route_id"));
                student.setFee(rs.getBigDecimal("fee"));
                student.setStatus(rs.getString("status"));
                student.setStartDate(rs.getDate("start_date"));
                student.setEndDate(rs.getDate("end_date"));


                student.setActionType("NONE");

                map.put(transportId, student);
            }

            StudentTransportDueDetails due = new StudentTransportDueDetails();
            due.setFeeAmount(rs.getBigDecimal("fee_amount"));
            due.setDueMonth(rs.getDate("due_month"));
            due.setDiscount(rs.getBigDecimal("discount_amount"));
            due.setPenalty(rs.getBigDecimal("penalty_amount"));
            due.setPaidAmount(rs.getBigDecimal("paid_amount"));
            due.setDueAmount(rs.getBigDecimal("due_amount"));

            student.getDues().add(due);
        });

        return new ArrayList<>(map.values());
    }



    @Override
    public StudentTransportDetails getActiveStudentTransportDetails(
            int sessionId,
            Integer studentId,
            String status,
            String schoolCode) throws Exception {

        String sql = """
        SELECT
            std.student_transport_id,
            std.student_id,
            std.session_id,
            s.academic_session,
            std.school_id,
            std.route_id,
            std.fee,
            std.status,
            std.start_date,
            std.end_date,
            spd.first_name,
            spd.last_name,
            ar.boarding_point,
            ar.destination,

            tfdue.tfdue_id,
            tfdue.fee_amount,
            tfdue.due_month,
            tfdue.discount_amount,
            tfdue.penalty_amount,

            COALESCE(tfdd.amount_paid, 0) AS paid_amount,

            COALESCE(
                tfdue.fee_amount
                - tfdue.discount_amount
                + tfdue.penalty_amount
                - COALESCE(tfdd.amount_paid, 0)
            , 0) AS due_amount

        FROM student_transport_details std

        INNER JOIN student_personal_details spd
            ON spd.student_id = std.student_id
           AND spd.school_id = std.school_id

        INNER JOIN add_route ar
            ON ar.route_id = std.route_id
           AND ar.school_id = std.school_id

        INNER JOIN transport_fee_due tfdue
            ON tfdue.student_transport_id = std.student_transport_id

        LEFT JOIN (
            SELECT
                student_transport_id,
                due_month,
                SUM(amount_paid) AS amount_paid
            FROM transport_fee_deposit_details
            GROUP BY student_transport_id, due_month
        ) tfdd
            ON tfdd.student_transport_id = tfdue.student_transport_id
           AND tfdd.due_month = tfdue.due_month

        INNER JOIN session s
            ON s.session_id = std.session_id
           AND s.school_id = std.school_id

        WHERE std.status = ?
          AND std.session_id = ?
          AND std.student_id = ?
          AND tfdue.fee_amount > 0

        ORDER BY tfdue.due_month
    """;

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        final StudentTransportDetails[] result = { null };

        try {

            jdbcTemplate.query(sql,
                    new Object[]{status.toUpperCase(), sessionId, studentId},
                    rs -> {

                        if (result[0] == null) {

                            StudentTransportDetails student = new StudentTransportDetails();
                            student.setStudentTransportId(rs.getInt("student_transport_id"));
                            student.setStudentId(rs.getInt("student_id"));
                            student.setSessionId(rs.getInt("session_id"));
                            student.setAcademicSession(rs.getString("academic_session"));
                            student.setSchoolId(rs.getInt("school_id"));
                            student.setRouteId(rs.getInt("route_id"));
                            student.setFee(rs.getBigDecimal("fee"));
                            student.setStatus(rs.getString("status"));
                            student.setStartDate(rs.getDate("start_date"));
                            student.setEndDate(rs.getDate("end_date"));

                            student.setFirstName(rs.getString("first_name"));
                            student.setLastName(rs.getString("last_name"));
                            student.setBoardingPoint(rs.getString("boarding_point"));
                            student.setDestination(rs.getString("destination"));

                            // ✅ VERY IMPORTANT
                            student.setDues(new ArrayList<>());

                            result[0] = student;
                        }

                        StudentTransportDueDetails due = new StudentTransportDueDetails();
                        due.setTfdueId(rs.getInt("tfdue_id"));
                        due.setFeeAmount(rs.getBigDecimal("fee_amount"));
                        due.setDueMonth(rs.getDate("due_month"));
                        due.setDiscount(rs.getBigDecimal("discount_amount"));
                        due.setPenalty(rs.getBigDecimal("penalty_amount"));
                        due.setPaidAmount(rs.getBigDecimal("paid_amount"));
                        due.setDueAmount(rs.getBigDecimal("due_amount"));

                        result[0].getDues().add(due);
                    });

        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return result[0];
    }

    @Override
    public List<StudentTransportDetails> getAllStudentsTransportDetails(int sessionId, String status, Integer routeId, Date dueMonth, String schoolCode) throws Exception {
        StringBuilder sql = new StringBuilder(
                """
                        SELECT
                            std.student_transport_id,
                            std.student_id,
                            std.session_id,
                            s.academic_session,
                            std.school_id,
                            std.route_id,
                            std.fee,
                            std.status,
                            std.start_date,
                            std.end_date,
                            spd.first_name,
                            spd.last_name,
                            ar.boarding_point,
                            ar.destination,
                            tfdue.tfdue_id,
                            tfdue.fee_amount,
                            tfdue.due_month,
                            tfdue.discount_amount,
                            tfdue.penalty_amount,
                            COALESCE(tfdd.amount_paid, 0) AS paid_amount,
                            COALESCE(
                                tfdue.fee_amount
                                - tfdue.discount_amount
                                + tfdue.penalty_amount
                                - COALESCE(tfdd.amount_paid, 0)
                            , 0) AS due_amount,

                            -- Class and section info
                            sad.student_class_id AS class_id,
                            c.class_name,
                            sad.student_section_id AS section_id,
                            sec.section_name,
                
                            -- Last paid date
                            tfdd.last_paid_date
                
                        FROM student_transport_details std
                        INNER JOIN student_personal_details spd
                            ON spd.student_id = std.student_id
                           AND spd.school_id = std.school_id
                        INNER JOIN add_route ar
                            ON ar.route_id = std.route_id
                           AND ar.school_id = std.school_id
                        INNER JOIN transport_fee_due tfdue
                            ON tfdue.student_transport_id = std.student_transport_id
                        LEFT JOIN (
                            SELECT
                                student_transport_id,
                                due_month,
                                SUM(amount_paid) AS amount_paid,
                                MAX(system_date_time) AS last_paid_date
                            FROM transport_fee_deposit_details
                            GROUP BY student_transport_id, due_month
                        ) tfdd
                            ON tfdd.student_transport_id = tfdue.student_transport_id
                           AND tfdd.due_month = tfdue.due_month
                        INNER JOIN session s
                            ON s.session_id = std.session_id
                           AND s.school_id = std.school_id
                        LEFT JOIN student_academic_details sad
                            ON sad.student_id = std.student_id
                           AND sad.session_id = std.session_id
                        LEFT JOIN mst_class c
                            ON c.class_id = sad.student_class_id
                        LEFT JOIN mst_section sec
                            ON sec.section_id = sad.student_section_id
                        WHERE tfdue.fee_amount > 0
                          AND std.session_id  = ?
                """);
        List<Object> params = new ArrayList<>();

        params.add(sessionId);

        //Dynamic filters
        if (status != null && !status.isEmpty()) {
            sql.append(" AND std.status = ? ");
            params.add(status.toUpperCase());
        }

        if (routeId != null) {
            sql.append(" AND std.route_id = ? ");
            params.add(routeId);
        }

        if (dueMonth != null) {
            sql.append(" AND tfdue.due_month = ? ");
            params.add(new java.sql.Date(dueMonth.getTime()));
        }

        sql.append(" ORDER BY tfdue.due_month ");

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        Map<Integer, StudentTransportDetails> studentMap = new LinkedHashMap<>();

        try{
            jdbcTemplate.query(sql.toString(), params.toArray(), rs -> {
                int transportId = rs.getInt("student_transport_id");
                StudentTransportDetails student = studentMap.get(transportId);
                if(student == null){
                    student = new StudentTransportDetails();
                    student.setStudentTransportId(transportId);
                    student.setStudentId(rs.getInt("student_id"));
                    student.setSessionId(rs.getInt("session_id"));
                    student.setAcademicSession(rs.getString("academic_session"));
                    student.setSchoolId(rs.getInt("school_id"));
                    student.setRouteId(rs.getInt("route_id"));
                    student.setFee(rs.getBigDecimal("fee"));
                    student.setStatus(rs.getString("status"));
                    student.setStartDate(rs.getDate("start_date"));
                    student.setEndDate(rs.getDate("end_date"));

                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));
                    student.setBoardingPoint(rs.getString("boarding_point"));
                    student.setDestination(rs.getString("destination"));

                    student.setClassId(rs.getInt("class_id"));
                    student.setClassName(rs.getString("class_name"));
                    student.setSectionId(rs.getInt("section_id"));
                    student.setSectionName(rs.getString("section_name"));

                    student.setDues(new ArrayList<>());

                    studentMap.put(transportId, student);
                }

                //  Due mapping
                StudentTransportDueDetails due = new StudentTransportDueDetails();

                due.setTfdueId(rs.getInt("tfdue_id"));
                due.setFeeAmount(rs.getBigDecimal("fee_amount"));
                due.setDueMonth(rs.getDate("due_month"));
                due.setDiscount(rs.getBigDecimal("discount_amount"));
                due.setPenalty(rs.getBigDecimal("penalty_amount"));
                due.setPaidAmount(rs.getBigDecimal("paid_amount"));
                due.setDueAmount(rs.getBigDecimal("due_amount"));
                due.setLastPaidDate(rs.getTimestamp("last_paid_date"));

                student.getDues().add(due);
            });
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return new ArrayList<>(studentMap.values());
    }


}

