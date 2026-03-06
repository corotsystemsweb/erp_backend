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
    public List<StaffAttendanceDetails> getAllStaffDetails(String schoolCode) throws Exception {
        String sql = """
                SELECT
                    COALESCE(s.staff_id, d.driver_id) AS staff_id,
                    CASE
                        WHEN s.staff_id IS NOT NULL THEN 'STAFF'
                        ELSE 'DRIVER'
                    END AS staff_type,
                    CONCAT(COALESCE(s.first_name, d.first_name), ' ', COALESCE(s.last_name, d.last_name)) AS staff_name,
                    COALESCE(s.phone_number, d.contact_number) AS phone_number,
                    COALESCE(s.designation_id, d.sd_id) AS designation_id,
                    sd.designation AS designation
                FROM
                    staff s
                FULL OUTER JOIN
                    add_driver d ON s.designation_id = d.sd_id
                JOIN
                    staff_designation sd ON sd.sd_id = COALESCE(s.designation_id, d.sd_id)
                WHERE
                    (s.deleted IS NOT TRUE)
                                
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffAttendanceDetails> staffAttendanceDetails = null;
        try{
            staffAttendanceDetails = jdbcTemplate.query(sql, new RowMapper<StaffAttendanceDetails>() {
                @Override
                public StaffAttendanceDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffAttendanceDetails sad = new StaffAttendanceDetails();
                    sad.setStaffId(rs.getInt("staff_id"));
                    sad.setStaffType(rs.getString("staff_type"));
                    sad.setStaffName(rs.getString("staff_name"));
                    sad.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    sad.setDesignationId(rs.getInt("designation_id"));
                    sad.setDesignation(rs.getString("designation"));
                    return sad;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffAttendanceDetails;
    }

    @Override
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception {
        String sql = "insert into staff_attendance (staff_id, staff_type, designation_id, attendance_date, attendance_status) values (?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffAttendanceDetails> insertStaff = new ArrayList<>();
        try{
            for(StaffAttendanceDetails staffAttendance : staffAttendanceDetails){
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, staffAttendance.getStaffId());
                    ps.setString(2, staffAttendance.getStaffType());
                    ps.setInt(3, staffAttendance.getDesignationId());
                    ps.setDate(4, new java.sql.Date(staffAttendance.getAttendanceDate().getTime()));
                    ps.setString(5, staffAttendance.getAttendanceStatus());
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

//    @Override
//    public List<StaffAttendanceDetails> getAllStaffAttendanceDetails(Integer staffId, String staffName, Integer designationId, Date dateFrom, Date dateTo, String schoolCode) throws Exception {
//        String sql = /*"""
//                WITH TotalDays AS (
//                    SELECT
//                        COUNT(DISTINCT attendance_date) AS total_days
//                    FROM
//                        staff_attendance
//                    WHERE
//                        (? :: date IS NULL OR attendance_date >= ?)
//                        AND (? :: date IS NULL OR attendance_date <= ?)
//                ),
//                AttendanceSummary AS (
//                    SELECT
//                        sa.staff_id,
//                        sa.staff_type,
//                        COUNT(CASE WHEN LOWER(sa.attendance_status) = 'present' THEN 1 END) AS total_present
//                    FROM
//                        staff_attendance sa
//                    WHERE
//                        (? :: date IS NULL OR sa.attendance_date >= ?)
//                        AND (? :: date IS NULL OR sa.attendance_date <= ?)
//                    GROUP BY
//                        sa.staff_id, sa.staff_type
//                )
//                SELECT
//                    DISTINCT sa.staff_id,
//                    CONCAT(
//                        COALESCE(s.first_name, d.first_name), ' ',
//                        COALESCE(s.last_name, d.last_name)
//                    ) AS staff_name,
//                    COALESCE(s.phone_number, d.contact_number) AS phone_number,
//                    td.total_days,
//                    ats.total_present,
//                    (td.total_days - ats.total_present) AS total_absent,
//                    sd.designation AS designation,
//                    sa.staff_type
//                FROM
//                    staff_attendance sa
//                CROSS JOIN
//                    TotalDays td
//                LEFT JOIN
//                    AttendanceSummary ats ON sa.staff_id = ats.staff_id AND sa.staff_type = ats.staff_type
//                LEFT JOIN
//                    staff s ON sa.staff_id = s.staff_id AND sa.staff_type = 'STAFF'
//                LEFT JOIN
//                    add_driver d ON sa.staff_id = d.driver_id AND sa.staff_type = 'DRIVER'
//                LEFT JOIN
//                    staff_designation sd ON sa.designation_id = sd.sd_id
//                WHERE
//                    sa.staff_type IN ('STAFF', 'DRIVER')
//                    AND (? :: int IS NULL OR sa.staff_id = ?)
//                    AND (
//                        ? :: TEXT IS NULL OR CONCAT(
//                            COALESCE(s.first_name, d.first_name), ' ',
//                            COALESCE(s.last_name, d.last_name)
//                        ) = ?
//                    )
//                    AND (? :: int IS NULL OR sd.sd_id = ?)
//                ORDER BY
//                    sa.staff_id
//                """*/
//                """
//                        WITH TotalDays AS (
//                                                                                        SELECT COUNT(DISTINCT attendance_date::date) AS total_days
//                                                                                        FROM staff_attendance
//                                                                                        WHERE (?::date IS NULL OR attendance_date::date >= ?::date)
//                                                                                            AND (?::date IS NULL OR attendance_date::date <= ?::date)
//                                                                                    ),
//                                                                                    StaffList AS (
//                                                                                        SELECT DISTINCT staff_id, staff_type
//                                                                                        FROM staff_attendance
//                                                                                    ),
//                                                                                    AttendanceSummary AS (
//                                                                                        SELECT
//                                                                                            sa.staff_id,
//                                                                                            sa.staff_type,
//                                                                                            COUNT(DISTINCT CASE\s
//                                                                                                             WHEN LOWER(sa.attendance_status) = 'present'\s
//                                                                                                             THEN sa.attendance_date::date\s
//                                                                                                           END) AS total_present
//                                                                                        FROM staff_attendance sa
//                                                                                        WHERE (?::date IS NULL OR sa.attendance_date::date >= ?::date)
//                                                                                            AND (?::date IS NULL OR sa.attendance_date::date <= ?::date)
//                                                                                        GROUP BY sa.staff_id, sa.staff_type
//                                                                                    )
//                                                                                    SELECT
//                                                                                        sl.staff_id,
//                                                                                        CONCAT(
//                                                                                            COALESCE(s.first_name, d.first_name), ' ',
//                                                                                            COALESCE(s.last_name, d.last_name)
//                                                                                        ) AS staff_name,
//                                                                                        COALESCE(s.phone_number, d.contact_number) AS phone_number,
//                                                                                        td.total_days,
//                                                                                        COALESCE(ats.total_present, 0) AS total_present,
//                                                                                        (td.total_days - COALESCE(ats.total_present, 0)) AS total_absent,
//                                                                                        COALESCE(sd.designation, 'N/A') AS designation,
//                                                                                        sl.staff_type
//                                                                                    FROM StaffList sl
//                                                                                    CROSS JOIN TotalDays td
//                                                                                    LEFT JOIN AttendanceSummary ats\s
//                                                                                        ON sl.staff_id = ats.staff_id AND sl.staff_type = ats.staff_type
//                                                                                    LEFT JOIN staff s\s
//                                                                                        ON sl.staff_id = s.staff_id AND sl.staff_type = 'STAFF'
//                                                                                    LEFT JOIN add_driver d\s
//                                                                                        ON sl.staff_id = d.driver_id AND sl.staff_type = 'DRIVER'
//                                                                                    LEFT JOIN staff_designation sd\s
//                                                                                        ON s.designation_id = sd.sd_id
//                                                                                    WHERE
//                                                                                        sl.staff_type IN ('STAFF', 'DRIVER')
//                                                                                        AND (?::int IS NULL OR sl.staff_id = ?)
//                                                                                        AND (
//                                                                                            ?::TEXT IS NULL OR\s
//                                                                                            CONCAT(COALESCE(s.first_name, d.first_name), ' ', COALESCE(s.last_name, d.last_name))\s
//                                                                                            ILIKE '%' || ? || '%'
//                                                                                        )
//                                                                                        AND (?::int IS NULL OR sd.sd_id = ?)
//                                                                                    ORDER BY sl.staff_id;
//                        """;
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        List<StaffAttendanceDetails> staffAttendanceDetails = null;
//        try{
//            staffAttendanceDetails = jdbcTemplate.query(
//                    sql,
//                    new Object[]{
//                            // TotalDays CTE (4 params)
//                            dateFrom, dateFrom, dateTo, dateTo,
//
//                            // AttendanceSummary CTE (4 params)
//                            dateFrom, dateFrom, dateTo, dateTo,
//
//                            // Main query filters (6 params)
//                            staffId, staffId,
//                            staffName, staffName,
//                            designationId, designationId
//                    },
//                    new RowMapper<StaffAttendanceDetails>(){
//
//                @Override
//                public StaffAttendanceDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
//                    StaffAttendanceDetails sad = new StaffAttendanceDetails();
//                    sad.setStaffId(rs.getInt("staff_id"));
//                    sad.setStaffName(rs.getString("staff_name"));
//                    sad.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
//                    sad.setTotalDays(rs.getDouble("total_days"));
//                    sad.setTotalPresent(rs.getDouble("total_present"));
//                    sad.setTotalAbsent(rs.getDouble("total_absent"));
//                    sad.setDesignation(rs.getString("designation"));
//                    sad.setStaffType(rs.getString("staff_type"));
//                    return sad;
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//        return staffAttendanceDetails;
//    }

    @Override
    public List<StaffAttendanceDetails> getAllStaffAttendanceDetails(
            Integer staffId,
            String staffName,
            Integer designationId,
            Date dateFrom,
            Date dateTo,
            String schoolCode) {

        String sql = """
        WITH DateRange AS (
            SELECT DISTINCT attendance_date
            FROM staff_attendance
            WHERE
                (?::date IS NULL OR attendance_date >= ?)
                AND
                (?::date IS NULL OR attendance_date <= ?)
        ),

        StaffList AS (
            SELECT
                st.staff_id,
                'STAFF' AS staff_type,
                st.first_name,
                st.last_name,
                st.phone_number,
                st.designation_id
            FROM staff st
            WHERE (st.deleted IS NULL OR st.deleted = false)
              AND st.current_status = 'active'

            UNION ALL

            SELECT
                d.driver_id,
                'DRIVER',
                d.first_name,
                d.last_name,
                d.contact_number,
                d.sd_id
            FROM add_driver d
        ),

        DailyAttendance AS (
            SELECT
                sl.staff_id,
                sl.staff_type,
                dr.attendance_date,
                sa.attendance_status AS status
            FROM StaffList sl
            JOIN DateRange dr ON true
            LEFT JOIN staff_attendance sa
                ON sa.staff_id = sl.staff_id
                AND sa.staff_type = sl.staff_type
                AND sa.attendance_date = dr.attendance_date
        ),

        Summary AS (
            SELECT
                staff_id,
                staff_type,
                COUNT(*) FILTER (WHERE LOWER(status)='present') AS total_present,
                COUNT(*) FILTER (WHERE LOWER(status)='absent') AS total_absent,
                COUNT(status) AS total_days
            FROM DailyAttendance
            GROUP BY staff_id, staff_type
        )

        SELECT
            sl.staff_id,
            CONCAT(sl.first_name,' ',sl.last_name) AS staff_name,
            sl.phone_number,
            sl.staff_type,
            COALESCE(sd.designation,'N/A') AS designation,

            COALESCE(sm.total_days,0) AS total_days,
            COALESCE(sm.total_present,0) AS total_present,
            COALESCE(sm.total_absent,0) AS total_absent,

            ROUND(
                CASE
                    WHEN COALESCE(sm.total_days,0) = 0 THEN 0
                    ELSE (sm.total_present*100.0/sm.total_days)
                END,2
            ) AS attendance_percentage,

            json_agg(
                json_build_object(
                    'date', da.attendance_date,
                    'status', COALESCE(da.status,'Not Marked')
                )
                ORDER BY da.attendance_date
            ) AS daily_details

        FROM StaffList sl
        LEFT JOIN staff_designation sd
            ON sl.designation_id = sd.sd_id
        LEFT JOIN Summary sm
            ON sl.staff_id = sm.staff_id
            AND sl.staff_type = sm.staff_type
        LEFT JOIN DailyAttendance da
            ON sl.staff_id = da.staff_id
            AND sl.staff_type = da.staff_type

        WHERE
            (?::int IS NULL OR sl.staff_id = ?)
            AND (
                ?::TEXT IS NULL OR
                CONCAT(sl.first_name,' ',sl.last_name) ILIKE '%'||?||'%'
            )
            AND (?::int IS NULL OR sl.designation_id = ?)

        GROUP BY
            sl.staff_id,
            sl.first_name,
            sl.last_name,
            sl.phone_number,
            sl.staff_type,
            sd.designation,
            sm.total_days,
            sm.total_present,
            sm.total_absent

        ORDER BY sl.staff_id;
        """;

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {

            return jdbcTemplate.query(
                    sql,
                    new Object[]{
                            // DateRange params (IMPORTANT ORDER)
                            dateFrom, dateFrom,
                            dateTo, dateTo,

                            // Filters
                            staffId, staffId,
                            staffName, staffName,
                            designationId, designationId
                    },
                    (rs, rowNum) -> {

                        StaffAttendanceDetails sad =
                                new StaffAttendanceDetails();

                        sad.setStaffId(rs.getInt("staff_id"));
                        sad.setStaffName(rs.getString("staff_name"));
                        sad.setStaffType(rs.getString("staff_type"));

                        String phone = rs.getString("phone_number");
                        sad.setPhoneNumber(
                                phone != null ? CipherUtils.decrypt(phone) : null
                        );

                        sad.setDesignation(rs.getString("designation"));
                        sad.setTotalDays(rs.getDouble("total_days"));
                        sad.setTotalPresent(rs.getDouble("total_present"));
                        sad.setTotalAbsent(rs.getDouble("total_absent"));
                        sad.setAttendancePercentage(
                                rs.getDouble("attendance_percentage")
                        );

                        sad.setDailyDetails(rs.getString("daily_details"));

                        return sad;
                    }
            );

        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}
