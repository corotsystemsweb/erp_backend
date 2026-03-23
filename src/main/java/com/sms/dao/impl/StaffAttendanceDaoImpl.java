package com.sms.dao.impl;

import com.sms.dao.StaffAttendanceDao;
import com.sms.model.StaffAttendanceDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class StaffAttendanceDaoImpl implements StaffAttendanceDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception {
        String sql = "INSERT INTO staff_attendance " +
                "(school_id, session_id, staff_id, staff_type, designation_id, department_id, " +
                "attendance_date, attendance_status, check_in_time, check_out_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffAttendanceDetails> insertStaff = new ArrayList<>();
        try{
            for(StaffAttendanceDetails staffAttendance : staffAttendanceDetails){
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, staffAttendance.getSchoolId());
                    ps.setInt(2, staffAttendance.getSessionId());
                    ps.setInt(3, staffAttendance.getStaffId());
                    ps.setString(4, staffAttendance.getStaffType());
                    ps.setInt(5, staffAttendance.getDesignationId());
                    ps.setInt(6, staffAttendance.getDepartmentId());
                    ps.setDate(7, new java.sql.Date(staffAttendance.getAttendanceDate().getTime()));
                    ps.setString(8, staffAttendance.getAttendanceStatus());
                    if (staffAttendance.getCheckInTime() != null) {
                        ps.setTime(9, staffAttendance.getCheckInTime());
                    } else {
                        ps.setNull(9, java.sql.Types.TIME);
                    }

                    if (staffAttendance.getCheckOutTime() != null) {
                        ps.setTime(10, staffAttendance.getCheckOutTime());
                    } else {
                        ps.setNull(10, java.sql.Types.TIME);
                    }
                    return ps;
                }, keyHolder);
                Map<String, Object> keys = keyHolder.getKeys();
                if(keys != null && keys.containsKey("sa_id")) {
                    int generatedKey = ((Number) keys.get("sa_id")).intValue();
                    staffAttendance.setSaId(generatedKey);
                    insertStaff.add(staffAttendance);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return insertStaff;
    }

    @Override
    public List<StaffAttendanceDetails> getStaffAttendance(Integer staffId, Integer departmentId, Integer designationId, Date date, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        String baseQuery =
                "SELECT " +
                        "   sa.sa_id, " +
                        "   sa.school_id, " +
                        "   sa.session_id, " +
                        "   s.academic_session, " +
                        "   sa.staff_id, " +
                        "   sa.staff_type, " +
                        "   CASE " +
                        "       WHEN sa.staff_type = 'STAFF'  THEN CONCAT(st.first_name, ' ', st.last_name) " +
                        "       WHEN sa.staff_type = 'DRIVER' THEN CONCAT(dr.first_name, ' ', dr.last_name) " +
                        "   END AS staff_name, " +
                        "   sa.department_id, " +
                        "   sd.department AS department_name, " +
                        "   sa.designation_id, " +
                        "   sdes.designation AS designation_name, " +
                        "   sa.attendance_date, " +
                        "   sa.attendance_status, " +
                        "   sa.check_in_time, " +
                        "   sa.check_out_time " +
                        "FROM staff_attendance sa " +
                        "JOIN session s " +
                        "   ON s.session_id = sa.session_id " +
                        "JOIN staff_department sd " +
                        "   ON sd.stdp_id = sa.department_id " +
                        "JOIN staff_designation sdes " +
                        "   ON sdes.sd_id = sa.designation_id " +
                        "LEFT JOIN staff st " +
                        "   ON st.staff_id = sa.staff_id AND sa.staff_type = 'STAFF' " +
                        "LEFT JOIN add_driver dr " +
                        "   ON dr.driver_id = sa.staff_id AND sa.staff_type = 'DRIVER' ";

        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (date == null) {
            whereClause.append("AND sa.attendance_date = CURRENT_DATE ");
        } else {
            whereClause.append("AND sa.attendance_date = ? ");
            params.add(new java.sql.Date(date.getTime()));
        }

        if (staffId != null) {
            whereClause.append("AND sa.staff_id = ? ");
            params.add(staffId);
        }

        if (departmentId != null) {
            whereClause.append("AND sa.department_id = ? ");
            params.add(departmentId);
        }

        if (designationId != null) {
            whereClause.append("AND sa.designation_id = ? ");
            params.add(designationId);
        }

        String orderBy =
                "ORDER BY sa.attendance_date DESC, " +
                "CASE " +
                "   WHEN sa.staff_type = 'STAFF'  THEN CONCAT(st.first_name, ' ', st.last_name) " +
                "   WHEN sa.staff_type = 'DRIVER' THEN CONCAT(dr.first_name, ' ', dr.last_name) " +
                "END ASC ";

        String finalQuery = baseQuery + whereClause + orderBy;

        try {
            List<StaffAttendanceDetails> result = jdbcTemplate.query(finalQuery, params.toArray(), (rs, rowNum) -> {
                StaffAttendanceDetails details = new StaffAttendanceDetails();
                details.setSaId(rs.getInt("sa_id"));
                details.setSchoolId(rs.getInt("school_id"));
                details.setSessionId(rs.getInt("session_id"));
                details.setAcademicSession(rs.getString("academic_session"));
                details.setStaffId(rs.getInt("staff_id"));
                details.setStaffType(rs.getString("staff_type"));
                details.setStaffName(rs.getString("staff_name"));
                details.setDepartmentId(rs.getInt("department_id"));
                details.setDepartmentName(rs.getString("department_name"));
                details.setDesignationId(rs.getInt("designation_id"));
                details.setDesignation(rs.getString("designation_name"));
                details.setAttendanceDate(rs.getDate("attendance_date"));
                details.setAttendanceStatus(rs.getString("attendance_status"));
                details.setCheckInTime(rs.getTime("check_in_time"));
                details.setCheckOutTime(rs.getTime("check_out_time"));
                return details;
            });

            if (result == null || result.isEmpty()) {
                return null;
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
