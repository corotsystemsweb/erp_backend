package com.sms.dao.impl;

import com.sms.dao.StudentExcelDao;
import com.sms.model.StudentExcelDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Repository
public class StudentExcelDaoImpl implements StudentExcelDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public StudentExcelDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public StudentExcelDetails addData(StudentExcelDetails studentExcelDetails,String schoolCode) throws Exception {
        //String sql = "insert into student_details (academic_session, student_class, student_section, registration_number, roll_number, first_name, last_name, gender, dob, blood_group, father_name, mother_name, phone_number, aadhar_number, religion, nationality, current_address, current_city, current_state, current_zipcode, father_occupation, mother_occupation, emergency_phone_number, email_address, current_status, current_status_comment) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql = "INSERT INTO student_excel (uu_id,apaar_id, session, Student_First_Name, Student_Last_Name, student_class, student_section,roll_no, medium, dob, religion, nationality, gender, mobile_number, email, address, city, state, country, pincode, type, isRteStudent,student_mother_Name, student_father_name, guardian_name, admission_date, enrolled_session, enrolled_year, enrolled_class, pen_no, student_type, admission_no, registration_no, enrollment_no, stream, whatsapp,alternate_number, blood_group, aadhar_no, caste, category, rte_application_no, attended_school, attended_class, school_affiliated, last_session, mother_qualification, father_qualification, guardian_qualification, mother_occupation,father_occupation,guardian_occupation,mother_residential_address, father_residential_address, guardian_residential_address, mother_income, father_income, guardian_income, mother_email, father_email, guardian_email, mother_mobile, father_mobile, guardian_mobile,  tc_no, tc_date, scholarship_id, scholarship_password, domicile_application_no, income_application_no, caste_application_no, dob_application_no,mother_aadhar_no, father_aadhar_no, guardian_aadhar_no, height,weight, bank_name, branch_name, bank_account_no, bank_ifsc, pan_no, reference, govt_student_id, govt_family_id, dropout, dropout_reason, dropout_date, previous_qualification,previous_pass_year, previous_roll_no,previous_obt_marks, previous_percentage, previous_subjects, previous_school_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, studentExcelDetails.getUuid());
                ps.setString(2,studentExcelDetails.getApaarId());
                ps.setString(3,studentExcelDetails.getSession());
                ps.setString(4, studentExcelDetails.getStudentFirstName());
                ps.setString(5, studentExcelDetails.getStudentLastName());
                ps.setString(6, studentExcelDetails.getStudentClass());
                ps.setString(7, studentExcelDetails.getStudentSection());
                ps.setInt(8, studentExcelDetails.getRollNo());
                ps.setString(9, studentExcelDetails.getMedium());
                ps.setDate(10, new java.sql.Date(studentExcelDetails.getDob().getTime()));
                ps.setString(11, studentExcelDetails.getReligion());
                ps.setString(12, studentExcelDetails.getNationality());
                ps.setString(13, studentExcelDetails.getGender());
                ps.setString(14, studentExcelDetails.getMobileNumber() != null ? CipherUtils.encrypt(studentExcelDetails.getMobileNumber()) : null);
                ps.setString(15, studentExcelDetails.getEmail());
                ps.setString(16, studentExcelDetails.getAddress());
                ps.setString(17, studentExcelDetails.getCity());
                ps.setString(18, studentExcelDetails.getState());
                ps.setString(19, studentExcelDetails.getCountry());
                ps.setString(20, studentExcelDetails.getPincode());
                ps.setString(21, studentExcelDetails.getType());
                ps.setBoolean(22, studentExcelDetails.isRteStudent());
                ps.setString(23, studentExcelDetails.getStudentMotherName());
                ps.setString(24, studentExcelDetails.getStudentFatherName());
                ps.setString(25, studentExcelDetails.getGuardianName());
                ps.setDate(26, new java.sql.Date(studentExcelDetails.getAdmissionDate().getTime()));
                ps.setString(27, studentExcelDetails.getEnrolledSession());
                ps.setString(28, studentExcelDetails.getEnrolledYear());
                ps.setString(29, studentExcelDetails.getEnrolledClass());
                ps.setString(30, studentExcelDetails.getPenNo());
                ps.setString(31, studentExcelDetails.getStudentType());
                ps.setString(32, studentExcelDetails.getAdmissionNo());
                ps.setString(33, studentExcelDetails.getRegistrationNo());
                ps.setString(34, studentExcelDetails.getEnrollmentNo());
                ps.setString(35, studentExcelDetails.getStream());
                ps.setString(36, studentExcelDetails.getWhatsapp() != null ? CipherUtils.encrypt(studentExcelDetails.getWhatsapp()) : null);
                ps.setString(37, studentExcelDetails.getAlternateNumber() != null ? CipherUtils.encrypt(studentExcelDetails.getAlternateNumber()) : null);
                ps.setString(38, studentExcelDetails.getBloodGroup());
                ps.setString(39, studentExcelDetails.getAadharNo() != null ? CipherUtils.encrypt(studentExcelDetails.getAadharNo()) : null);
                ps.setString(40, studentExcelDetails.getCaste());
                ps.setString(41, studentExcelDetails.getCategory());
                ps.setString(42, studentExcelDetails.getRteApplicationNo());
                ps.setString(43, studentExcelDetails.getAttendedSchool());
                ps.setString(44, studentExcelDetails.getAttendedClass());
                ps.setString(45, studentExcelDetails.getSchoolAffiliated());
                ps.setString(46, studentExcelDetails.getLastSession());
                ps.setString(47, studentExcelDetails.getMotherQualification());
                ps.setString(48, studentExcelDetails.getFatherQualification());
                ps.setString(49, studentExcelDetails.getGuardianQualification());
                ps.setString(50, studentExcelDetails.getMotherOccupation());
                ps.setString(51, studentExcelDetails.getFatherOccupation());
                ps.setString(52, studentExcelDetails.getGuardianOccupation());
                ps.setString(53, studentExcelDetails.getMotherResidentialAddress());
                ps.setString(54, studentExcelDetails.getFatherResidentialAddress());
                ps.setString(55, studentExcelDetails.getGuardianResidentialAddress());
                ps.setString(56, studentExcelDetails.getMotherIncome());
                ps.setString(57, studentExcelDetails.getFatherIncome());
                ps.setString(58, studentExcelDetails.getGuardianIncome());
                ps.setString(59, studentExcelDetails.getMotherEmail());
                ps.setString(60, studentExcelDetails.getFatherEmail());
                ps.setString(61, studentExcelDetails.getGuardianEmail());
                ps.setString(62, studentExcelDetails.getMobileNumber() != null ? CipherUtils.encrypt(studentExcelDetails.getMotherMobile()) : null);
                ps.setString(63, studentExcelDetails.getFatherMobile() != null ? CipherUtils.encrypt(studentExcelDetails.getFatherMobile()) : null);
                ps.setString(64, studentExcelDetails.getGuardianMobile() != null ? CipherUtils.encrypt(studentExcelDetails.getGuardianMobile()) : null);
                ps.setString(65, studentExcelDetails.getTcNo());
                ps.setString(66, studentExcelDetails.getTcDate());
                ps.setString(67, studentExcelDetails.getScholarshipId());
                ps.setString(68, studentExcelDetails.getScholarshipPassword());
                ps.setString(69, studentExcelDetails.getDomicileApplicationNo());
                ps.setString(70, studentExcelDetails.getIncomeApplicationNo());
                ps.setString(71, studentExcelDetails.getCasteApplicationNo());
                ps.setString(72, studentExcelDetails.getDobApplicationNo());
                ps.setString(73, studentExcelDetails.getMotherAadharNo() != null ? CipherUtils.encrypt(studentExcelDetails.getMotherAadharNo()) : null);
                ps.setString(74, studentExcelDetails.getFatherAadharNo() != null ? CipherUtils.encrypt(studentExcelDetails.getFatherAadharNo()) : null);
                ps.setString(75, studentExcelDetails.getGuardianAadharNo() != null ? CipherUtils.encrypt(studentExcelDetails.getGuardianAadharNo()) : null);
                ps.setString(76, studentExcelDetails.getHeight());
                ps.setString(77, studentExcelDetails.getWeight());
                ps.setString(78, studentExcelDetails.getBankName());
                ps.setString(79, studentExcelDetails.getBranchName());
                ps.setString(80, studentExcelDetails.getBankAccountNo());
                ps.setString(81, studentExcelDetails.getBankIfsc());
                ps.setString(82, studentExcelDetails.getPanNo());
                ps.setString(83, studentExcelDetails.getReference());
                ps.setString(84, studentExcelDetails.getGovtStudentId());
                ps.setString(85, studentExcelDetails.getGovtFamilyId());
                ps.setBoolean(86, studentExcelDetails.isDropout());
                ps.setString(87, studentExcelDetails.getDropoutReason());
                ps.setDate(88, studentExcelDetails.getDropoutDate() != null ? new java.sql.Date(studentExcelDetails.getDropoutDate().getTime()) : null);
                ps.setString(89, studentExcelDetails.getPreviousQualification());
                ps.setString(90, studentExcelDetails.getPreviousPassYear());
                ps.setString(91, studentExcelDetails.getPreviousRollNo());
                ps.setString(92, studentExcelDetails.getPreviousObtMarks());
                ps.setString(93, studentExcelDetails.getPreviousPercentage());
                ps.setString(94, studentExcelDetails.getPreviousSubjects());
                ps.setString(95, studentExcelDetails.getPreviousSchoolName());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                int generatedId = ((Number) keys.get("id")).intValue();
                studentExcelDetails.setStudentId(generatedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentExcelDetails;
    }

    public void truncateStudentExcelTable(String schoolCode) {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            String truncateSql = "TRUNCATE TABLE student_excel";
            jdbcTemplate.execute(truncateSql);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}

