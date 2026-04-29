package com.sms.dao.impl;
import java.util.List;

import com.sms.dao.LibraryMemberDao;
import com.sms.model.LibraryMemberDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class LibraryMemberDaoImpl implements LibraryMemberDao {

    @Override
    public LibraryMemberDetails addMember(LibraryMemberDetails details, String schoolCode) throws Exception {

        String sql = "INSERT INTO library_member " +
                "(school_id, session_id, member_type, student_id, staff_id, class_id, section_id, member_name, email, phone, valid_until, updated_by, update_date_time,class_name,section_name) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, details.getSessionId());
                ps.setString(3, details.getMemberType());

                ps.setObject(4, details.getStudentId()); // nullable
                ps.setObject(5, details.getStaffId());

                ps.setObject(6, details.getClassId());
                ps.setObject(7, details.getSectionId());

                ps.setString(8, details.getMemberName());
                ps.setString(9, details.getEmail());
                ps.setString(10, details.getPhone());

                ps.setDate(11, details.getValidUntil() != null
                        ? new java.sql.Date(details.getValidUntil().getTime())
                        : null);

                ps.setInt(12, details.getUpdatedBy());
                ps.setTimestamp(13, details.getUpdateDateTime());
                ps.setString(14,details.getClassName());
                ps.setString(15,details.getSectionName());


                return ps;
            }, keyHolder);

            return details;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<LibraryMemberDetails> getAllMembers(String schoolCode) throws Exception {

        String sql = "SELECT member_id, school_id, session_id, member_type, student_id, staff_id, class_id, section_id, member_name, email, phone, valid_until, updated_by, update_date_time, class_name, section_name, deleted FROM library_member WHERE deleted IS NOT TRUE";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<LibraryMemberDetails> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            LibraryMemberDetails m = new LibraryMemberDetails();

            m.setMemberId(rs.getInt("member_id"));
            m.setSchoolId(rs.getInt("school_id"));
            m.setSessionId(rs.getInt("session_id"));

            m.setMemberType(rs.getString("member_type"));

            m.setStudentId((Integer) rs.getObject("student_id"));
            m.setStaffId((Integer) rs.getObject("staff_id"));

            m.setClassId((Integer) rs.getObject("class_id"));
            m.setSectionId((Integer) rs.getObject("section_id"));

            m.setMemberName(rs.getString("member_name"));
            m.setEmail(rs.getString("email"));
            m.setPhone(rs.getString("phone"));

            m.setValidUntil(rs.getDate("valid_until"));

            m.setUpdatedBy(rs.getInt("updated_by"));
            m.setUpdateDateTime(rs.getTimestamp("update_date_time"));

            m.setClassName(rs.getString("class_name"));
            m.setSectionName(rs.getString("section_name"));

            m.setDeleted(rs.getBoolean("deleted"));

            return m;
        });

        return list;
    }
}