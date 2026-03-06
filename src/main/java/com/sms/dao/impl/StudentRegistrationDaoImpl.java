package com.sms.dao.impl;

import com.sms.dao.StudentRegistrationDao;
import com.sms.model.StudentRegistration;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StudentRegistrationDaoImpl implements StudentRegistrationDao {

    @Override
    public StudentRegistration insertStudent(StudentRegistration s, String schoolCode) {


        String sql = "INSERT INTO medhapro_test.student_registration (" +
                "school_id, first_name, last_name, father_name, mother_name, dob, blood_group, religion, " +
                "aadhaar_number, student_pen_no, contact_no, alternate_number, email, address, city, state, pin_code, " +
                "session_id, qualification, enrolled_class, last_institute_name, transfer_certificate_no, " +
                "reference_name, reference_type, enquiry_status, registration_date, comment, status" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                int index = 1;
                ps.setInt(index++, s.getSchoolId());
                ps.setString(index++, s.getFirstName());
                ps.setString(index++, s.getLastName());
                ps.setString(index++, s.getFatherName());
                ps.setString(index++, s.getMotherName());
                ps.setDate(index++, new java.sql.Date(s.getDob().getTime()));
                ps.setString(index++, s.getBloodGroup());
                ps.setString(index++, s.getReligion());
                ps.setString(index++, s.getAadhaarNumber());
                ps.setString(index++, s.getStudentPenNo());
                ps.setString(index++, s.getContactNo());
                ps.setString(index++, s.getAlternateNumber());
                ps.setString(index++, s.getEmail());
                ps.setString(index++, s.getAddress());
                ps.setString(index++, s.getCity());
                ps.setString(index++, s.getState());
                ps.setString(index++, s.getPinCode());
                ps.setInt(index++, s.getSessionId());
                ps.setString(index++, s.getQualification());
                ps.setString(index++, s.getEnrolledClass());
                ps.setString(index++, s.getLastInstituteName());
                ps.setString(index++, s.getTransferCertificateNo());
                ps.setString(index++, s.getReferenceName());
                ps.setString(index++, s.getReferenceType());
                ps.setString(index++, s.getEnquiryStatus());
                ps.setDate(index++, new java.sql.Date(s.getRegistrationDate().getTime()));
                ps.setString(index++, s.getComment());
                ps.setString(index++, s.getStatus());

                return ps;
            }, keyHolder);

            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("std_registration_id")) {
                s.setStdRegistrationId(((Number) keyHolder.getKeys().get("std_registration_id")).intValue());
            }

            return s;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean deleteRegistration(int stdRegistrationId, String status, String schoolCode) {
        String sql = "UPDATE student_registration SET status = ? WHERE std_registration_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            int rowsAffected = jdbcTemplate.update(sql, status, stdRegistrationId);
            return rowsAffected > 0;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<StudentRegistration> getAllRegistration(int sessionId, String schoolCode) {


        String sql = "SELECT std_registration_id, school_id, first_name, last_name, father_name, mother_name, dob, blood_group, religion, " +
                "aadhaar_number, student_pen_no, contact_no, alternate_number, email, address, city, state, pin_code, " +
                "session_id, qualification, enrolled_class, last_institute_name, transfer_certificate_no, " +
                "reference_name, reference_type, enquiry_status, registration_date, comment, status " +
                "FROM medhapro_test.student_registration " +
                "WHERE session_id = ? ORDER BY registration_date DESC";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            return jdbcTemplate.query(sql, new Object[]{sessionId}, (rs, rowNum) -> {
                StudentRegistration student = new StudentRegistration();

                student.setStdRegistrationId(rs.getInt("std_registration_id"));
                student.setSchoolId(rs.getInt("school_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setFatherName(rs.getString("father_name"));
                student.setMotherName(rs.getString("mother_name"));
                student.setDob(rs.getDate("dob"));
                student.setBloodGroup(rs.getString("blood_group"));
                student.setReligion(rs.getString("religion"));
                student.setAadhaarNumber(rs.getString("aadhaar_number"));
                student.setStudentPenNo(rs.getString("student_pen_no"));
                student.setContactNo(rs.getString("contact_no"));
                student.setAlternateNumber(rs.getString("alternate_number"));
                student.setEmail(rs.getString("email"));
                student.setAddress(rs.getString("address"));
                student.setCity(rs.getString("city"));
                student.setState(rs.getString("state"));
                student.setPinCode(rs.getString("pin_code"));
                student.setSessionId(rs.getInt("session_id"));
                student.setQualification(rs.getString("qualification"));
                student.setEnrolledClass(rs.getString("enrolled_class"));
                student.setLastInstituteName(rs.getString("last_institute_name"));
                student.setTransferCertificateNo(rs.getString("transfer_certificate_no"));
                student.setReferenceName(rs.getString("reference_name"));
                student.setReferenceType(rs.getString("reference_type"));
                student.setEnquiryStatus(rs.getString("enquiry_status"));
                student.setRegistrationDate(rs.getDate("registration_date"));
                student.setComment(rs.getString("comment"));
                student.setStatus(rs.getString("status"));

                return student;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
