package com.sms.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.StudentEnquiryFormDao;
import com.sms.exception.ImageSizeLimitExceededException;
import com.sms.model.StudentDetails;
import com.sms.model.StudentEnquiryFormDetails;
import com.sms.model.StudentEnquirySiblings;
import com.sms.model.StudentEnquirySubjects;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class StudentEnquiryFormDaoImpl implements StudentEnquiryFormDao {

    @Value("${student.enquiry.image.local.path}")
    private String FOLDER_PATH;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int studentId) throws Exception {
        //check the file size in bytes
        long maxsize = 200 * 1024; // 200kb in bytes
        if(file.getSize() > maxsize){
            throw new ImageSizeLimitExceededException("File size exceeds the maximum limit of 200KB");
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String photoPath = FOLDER_PATH + schoolCode + File.separator + studentId + "." + "png";
        File directory = new File(FOLDER_PATH + schoolCode);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        file.transferTo(new File(photoPath));
        return true;
    }

    @Override
    public StudentEnquiryFormDetails getImage(String schoolCode, int studentId) throws Exception {
        String fileName = studentId + ".png";
        String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        // Check if the file exists
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new IOException("File not found: " + imagePath);
        }

        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        StudentEnquiryFormDetails studentEnquiryFormDetails = new StudentEnquiryFormDetails();
        studentEnquiryFormDetails.setStudentImage(base64Image);
        return studentEnquiryFormDetails;
    }

    @Override
    public StudentEnquiryFormDetails saveStudentEnquiry(StudentEnquiryFormDetails studentEnquiryFormDetails, String schoolCode) throws Exception {
        String sql = "INSERT INTO student_enquiry_form (" +
                "sr_no, admission_date, admission_no, class_sought, session, pen, apaar_id," +
                "student_name, gender, dob, dob_in_words," +
                "mother_name, mother_phone, mother_qualification, mother_email, mother_occupation, mother_local_address, mother_residential_address, mother_annual_income," +
                "father_name, father_phone, father_qualification, father_email, father_occupation, father_local_address, father_residential_address, father_annual_income," +
                "guardian_name, guardian_phone, guardian_qualification, guardian_email, guardian_occupation, guardian_local_address, guardian_residential_address, guardian_annual_income," +
                "is_single_girl_child, is_specially_abled," +
                "category, religion, aadhar_number," +
                "last_school_name, last_school_address, last_class_attended, last_school_board," +
                "last_class_result, transfer_certificate_number, tc_date_of_issue, status," +
                "siblings, subjects," +
                "declaration_text, declaration_date, place, parent_signature, relationship_with_candidate, principal_signature," +
                "register_page_no, register_entry_date" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();

            String siblingsJson = objectMapper.writeValueAsString(studentEnquiryFormDetails.getSiblings() != null ? studentEnquiryFormDetails.getSiblings() : new ArrayList<>());

            String subjectsJson = objectMapper.writeValueAsString(studentEnquiryFormDetails.getSubjects() != null ? studentEnquiryFormDetails.getSubjects() : new ArrayList<>());

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                // ===== BASIC =====
                ps.setString(1, studentEnquiryFormDetails.getSrNo());
                if (studentEnquiryFormDetails.getAdmissionDate() != null)
                    ps.setDate(2, java.sql.Date.valueOf(studentEnquiryFormDetails.getAdmissionDate()));
                else
                    ps.setNull(2, Types.DATE);
                ps.setString(3, studentEnquiryFormDetails.getAdmissionNo());
                ps.setString(4, studentEnquiryFormDetails.getClassSought());
                ps.setString(5, studentEnquiryFormDetails.getSession());
                ps.setString(6, studentEnquiryFormDetails.getPen());
                ps.setString(7, studentEnquiryFormDetails.getApaarId());

                // ===== PERSONAL =====
                ps.setString(8, studentEnquiryFormDetails.getStudentName());
                ps.setString(9, studentEnquiryFormDetails.getGender());
                if (studentEnquiryFormDetails.getDob() != null)
                    ps.setDate(10, java.sql.Date.valueOf(studentEnquiryFormDetails.getDob()));
                else
                    ps.setNull(10, Types.DATE);
                ps.setString(11, studentEnquiryFormDetails.getDobInWords());

                // ===== MOTHER =====
                ps.setString(12, studentEnquiryFormDetails.getMotherName());
                ps.setString(13, studentEnquiryFormDetails.getMotherPhone());
                ps.setString(14, studentEnquiryFormDetails.getMotherQualification());
                ps.setString(15, studentEnquiryFormDetails.getMotherEmail());
                ps.setString(16, studentEnquiryFormDetails.getMotherOccupation());
                ps.setString(17, studentEnquiryFormDetails.getMotherLocalAddress());
                ps.setString(18, studentEnquiryFormDetails.getMotherResidentialAddress());
                if (studentEnquiryFormDetails.getMotherAnnualIncome() != null)
                    ps.setBigDecimal(19, studentEnquiryFormDetails.getMotherAnnualIncome());
                else
                    ps.setNull(19, Types.NUMERIC);

                // ===== FATHER =====
                ps.setString(20, studentEnquiryFormDetails.getFatherName());
                ps.setString(21, studentEnquiryFormDetails.getFatherPhone());
                ps.setString(22, studentEnquiryFormDetails.getFatherQualification());
                ps.setString(23, studentEnquiryFormDetails.getFatherEmail());
                ps.setString(24, studentEnquiryFormDetails.getFatherOccupation());
                ps.setString(25, studentEnquiryFormDetails.getFatherLocalAddress());
                ps.setString(26, studentEnquiryFormDetails.getFatherResidentialAddress());
                if (studentEnquiryFormDetails.getFatherAnnualIncome() != null)
                    ps.setBigDecimal(27, studentEnquiryFormDetails.getFatherAnnualIncome());
                else
                    ps.setNull(27, Types.NUMERIC);

                // ===== GUARDIAN =====
                ps.setString(28, studentEnquiryFormDetails.getGuardianName());
                ps.setString(29, studentEnquiryFormDetails.getGuardianPhone());
                ps.setString(30, studentEnquiryFormDetails.getGuardianQualification());
                ps.setString(31, studentEnquiryFormDetails.getGuardianEmail());
                ps.setString(32, studentEnquiryFormDetails.getGuardianOccupation());
                ps.setString(33, studentEnquiryFormDetails.getGuardianLocalAddress());
                ps.setString(34, studentEnquiryFormDetails.getGuardianResidentialAddress());
                if (studentEnquiryFormDetails.getGuardianAnnualIncome() != null)
                    ps.setBigDecimal(35, studentEnquiryFormDetails.getGuardianAnnualIncome());
                else
                    ps.setNull(35, Types.NUMERIC);

                // ===== STATUS =====
                ps.setBoolean(36, Boolean.TRUE.equals(studentEnquiryFormDetails.getIsSingleGirlChild()));
                ps.setBoolean(37, Boolean.TRUE.equals(studentEnquiryFormDetails.getIsSpeciallyAbled()));

                // ===== CATEGORY =====
                ps.setString(38, studentEnquiryFormDetails.getCategory());
                ps.setString(39, studentEnquiryFormDetails.getReligion());
                ps.setString(40, studentEnquiryFormDetails.getAadharNumber());

                // ===== SCHOOL =====
                ps.setString(41, studentEnquiryFormDetails.getLastSchoolName());
                ps.setString(42, studentEnquiryFormDetails.getLastSchoolAddress());
                ps.setString(43, studentEnquiryFormDetails.getLastClassAttended());
                ps.setString(44, studentEnquiryFormDetails.getLastSchoolBoard());

                // ===== RESULT =====
                ps.setString(45, studentEnquiryFormDetails.getLastClassResult());
                ps.setString(46, studentEnquiryFormDetails.getTransferCertificateNumber());
                // TC Date
                if (studentEnquiryFormDetails.getTcDateOfIssue() != null)
                    ps.setDate(47, java.sql.Date.valueOf(studentEnquiryFormDetails.getTcDateOfIssue()));
                else
                    ps.setNull(47, Types.DATE);

                ps.setString(48, studentEnquiryFormDetails.getStatus());

                // ===== JSONB =====
                ps.setObject(49, siblingsJson, Types.OTHER);
                ps.setObject(50, subjectsJson, Types.OTHER);

                // ===== DECLARATION =====
                ps.setString(51, studentEnquiryFormDetails.getDeclarationText());
                // Declaration Date
                if (studentEnquiryFormDetails.getDeclarationDate() != null)
                    ps.setDate(52, java.sql.Date.valueOf(studentEnquiryFormDetails.getDeclarationDate()));
                else
                    ps.setNull(52, Types.DATE);
                ps.setString(53, studentEnquiryFormDetails.getPlace());
                ps.setString(54, studentEnquiryFormDetails.getParentSignature());
                ps.setString(55, studentEnquiryFormDetails.getRelationshipWithCandidate());
                ps.setString(56, studentEnquiryFormDetails.getPrincipalSignature());

                // ===== OFFICE =====
                ps.setString(57, studentEnquiryFormDetails.getRegisterPageNo());
                // Register Entry Date
                if (studentEnquiryFormDetails.getRegisterEntryDate() != null)
                    ps.setDate(58, java.sql.Date.valueOf(studentEnquiryFormDetails.getRegisterEntryDate()));
                else
                    ps.setNull(58, Types.DATE);

                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("student_enquiry_id")) {
                int generatedId = ((Number) keys.get("student_enquiry_id")).intValue();
                studentEnquiryFormDetails.setStudentEnquiryId(generatedId);

                // Set sr_no same as student_enquiry_id
                String updateSql = "UPDATE student_enquiry_form SET sr_no = ? WHERE student_enquiry_id = ?";
                jdbcTemplate.update(updateSql, String.valueOf(generatedId), generatedId);
            }

        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Error saving student enquiry", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentEnquiryFormDetails;
    }

    @Override
    public List<StudentEnquiryFormDetails> getAllStudentEnquiry(String status, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        String baseQuery = """
                SELECT student_enquiry_id, sr_no, admission_date, admission_no, class_sought, session, pen, apaar_id, student_name, gender,
                dob, dob_in_words, mother_name, mother_phone, mother_qualification, mother_email, mother_occupation, mother_local_address,
                mother_residential_address, mother_annual_income, father_name, father_phone, father_qualification, father_email, father_occupation,
                father_local_address, father_residential_address, father_annual_income, guardian_name, guardian_phone, guardian_qualification,
                guardian_email, guardian_occupation, guardian_local_address, guardian_residential_address, guardian_annual_income,
                is_single_girl_child, is_specially_abled, category, religion, aadhar_number, last_school_name, last_school_address,
                last_class_attended, last_school_board, last_class_result, transfer_certificate_number, tc_date_of_issue, status, siblings, subjects,
                declaration_text, declaration_date, place, parent_signature, relationship_with_candidate, principal_signature, register_page_no,
                register_entry_date, created_at, updated_at FROM student_enquiry_form
                """;
        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if(status != null && !status.trim().isEmpty()){
            String normalizedStatus = status.trim().toUpperCase();

            List<String> allowedStatus = Arrays.asList("PENDING", "VERIFIED", "SHORTLISTED", "REJECTED");

            if(!allowedStatus.contains(normalizedStatus)){
                throw new IllegalArgumentException("Invalid status value: " + status);
            }

            whereClause.append("AND status = ? ");
            params.add(normalizedStatus);

        }

        String orderBy = " ORDER BY student_enquiry_id ASC";

        String finalQuery = baseQuery + whereClause + orderBy;

        try{
            List<StudentEnquiryFormDetails> studentEnquiryFormDetails = jdbcTemplate.query(finalQuery, params.toArray(), (rs, rowNum) -> {

                StudentEnquiryFormDetails sed = new StudentEnquiryFormDetails();
                    // ===== BASIC =====
                    sed.setStudentEnquiryId(rs.getInt("student_enquiry_id"));
                    sed.setSrNo(rs.getString("sr_no"));
                    sed.setAdmissionDate(rs.getString("admission_date"));
                    sed.setAdmissionNo(rs.getString("admission_no"));
                    sed.setClassSought(rs.getString("class_sought"));
                    sed.setSession(rs.getString("session"));
                    sed.setPen(rs.getString("pen"));
                    sed.setApaarId(rs.getString("apaar_id"));

                    // ===== PERSONAL =====
                    sed.setStudentName(rs.getString("student_name"));
                    sed.setGender(rs.getString("gender"));
                    sed.setDob(rs.getString("dob"));
                    sed.setDobInWords(rs.getString("dob_in_words"));

                    // ===== MOTHER =====
                    sed.setMotherName(rs.getString("mother_name"));
                    sed.setMotherPhone(rs.getString("mother_phone"));
                    sed.setMotherQualification(rs.getString("mother_qualification"));
                    sed.setMotherEmail(rs.getString("mother_email"));
                    sed.setMotherOccupation(rs.getString("mother_occupation"));
                    sed.setMotherLocalAddress(rs.getString("mother_local_address"));
                    sed.setMotherResidentialAddress(rs.getString("mother_residential_address"));
                    sed.setMotherAnnualIncome(rs.getBigDecimal("mother_annual_income"));

                    // ===== FATHER =====
                    sed.setFatherName(rs.getString("father_name"));
                    sed.setFatherPhone(rs.getString("father_phone"));
                    sed.setFatherQualification(rs.getString("father_qualification"));
                    sed.setFatherEmail(rs.getString("father_email"));
                    sed.setFatherOccupation(rs.getString("father_occupation"));
                    sed.setFatherLocalAddress(rs.getString("father_local_address"));
                    sed.setFatherResidentialAddress(rs.getString("father_residential_address"));
                    sed.setFatherAnnualIncome(rs.getBigDecimal("father_annual_income"));

                    // ===== GUARDIAN =====
                    sed.setGuardianName(rs.getString("guardian_name"));
                    sed.setGuardianPhone(rs.getString("guardian_phone"));
                    sed.setGuardianQualification(rs.getString("guardian_qualification"));
                    sed.setGuardianEmail(rs.getString("guardian_email"));
                    sed.setGuardianOccupation(rs.getString("guardian_occupation"));
                    sed.setGuardianLocalAddress(rs.getString("guardian_local_address"));
                    sed.setGuardianResidentialAddress(rs.getString("guardian_residential_address"));
                    sed.setGuardianAnnualIncome(rs.getBigDecimal("guardian_annual_income"));

                    // ===== STATUS =====
                    sed.setIsSingleGirlChild(rs.getBoolean("is_single_girl_child"));
                    sed.setIsSpeciallyAbled(rs.getBoolean("is_specially_abled"));

                    // ===== CATEGORY =====
                    sed.setCategory(rs.getString("category"));
                    sed.setReligion(rs.getString("religion"));

                    // ===== AADHAR =====
                    sed.setAadharNumber(rs.getString("aadhar_number"));

                    // ===== LAST SCHOOL =====
                    sed.setLastSchoolName(rs.getString("last_school_name"));
                    sed.setLastSchoolAddress(rs.getString("last_school_address"));
                    sed.setLastClassAttended(rs.getString("last_class_attended"));
                    sed.setLastSchoolBoard(rs.getString("last_school_board"));

                    // ===== RESULT =====
                    sed.setLastClassResult(rs.getString("last_class_result"));

                    // ===== TC =====
                    sed.setTransferCertificateNumber(rs.getString("transfer_certificate_number"));
                    sed.setTcDateOfIssue(rs.getString("tc_date_of_issue"));

                    sed.setStatus(rs.getString("status"));

                    // ===== SIBLINGS =====
                    String siblingsJson = rs.getString("siblings");
                    if (siblingsJson != null && !siblingsJson.isEmpty()) {
                        try {
                            sed.setSiblings(Arrays.asList(objectMapper.readValue(siblingsJson, StudentEnquirySiblings[].class)));
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing siblings JSON", e);
                        }
                    }

                    // ===== SUBJECTS =====
                    String subjectsJson = rs.getString("subjects");
                    if (subjectsJson != null && !subjectsJson.isEmpty()) {
                        try {
                            sed.setSubjects(Arrays.asList(objectMapper.readValue(subjectsJson, StudentEnquirySubjects[].class)));
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing subjects JSON", e);
                        }
                    }

                    // ===== DECLARATION =====
                    sed.setDeclarationText(rs.getString("declaration_text"));
                    sed.setDeclarationDate(rs.getString("declaration_date"));
                    sed.setPlace(rs.getString("place"));
                    sed.setParentSignature(rs.getString("parent_signature"));
                    sed.setRelationshipWithCandidate(rs.getString("relationship_with_candidate"));
                    sed.setPrincipalSignature(rs.getString("principal_signature"));

                    // ===== OFFICE =====
                    sed.setRegisterPageNo(rs.getString("register_page_no"));
                    sed.setRegisterEntryDate(rs.getString("register_entry_date"));

                    // ===== SYSTEM =====
                    sed.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    sed.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                    return sed;
            });

            return studentEnquiryFormDetails;

        } catch (Exception e){
            throw new RuntimeException("Error fetching student enquiry data", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public StudentEnquiryFormDetails getStudentEnquiryById(int studentEnquiryId, String schoolCode) throws Exception {
        String sql = """
                SELECT student_enquiry_id, sr_no, admission_date, admission_no, class_sought, session, pen, apaar_id, student_name, gender,
                dob, dob_in_words, mother_name, mother_phone, mother_qualification, mother_email, mother_occupation, mother_local_address,
                mother_residential_address, mother_annual_income, father_name, father_phone, father_qualification, father_email, father_occupation,
                father_local_address, father_residential_address, father_annual_income, guardian_name, guardian_phone, guardian_qualification,
                guardian_email, guardian_occupation, guardian_local_address, guardian_residential_address, guardian_annual_income,
                is_single_girl_child, is_specially_abled, category, religion, aadhar_number, last_school_name, last_school_address,
                last_class_attended, last_school_board, last_class_result, transfer_certificate_number, tc_date_of_issue, status, siblings, subjects,
                declaration_text, declaration_date, place, parent_signature, relationship_with_candidate, principal_signature, register_page_no,
                register_entry_date, created_at, updated_at FROM student_enquiry_form WHERE student_enquiry_id = ?
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        StudentEnquiryFormDetails studentEnquiryFormDetails = null;

        try{
            studentEnquiryFormDetails = jdbcTemplate.queryForObject(sql, new Object[]{studentEnquiryId}, new RowMapper<StudentEnquiryFormDetails>() {
                @Override
                public StudentEnquiryFormDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentEnquiryFormDetails sed = new StudentEnquiryFormDetails();
                    // ===== BASIC =====
                    sed.setStudentEnquiryId(rs.getInt("student_enquiry_id"));
                    sed.setSrNo(rs.getString("sr_no"));
                    sed.setAdmissionDate(rs.getString("admission_date"));
                    sed.setAdmissionNo(rs.getString("admission_no"));
                    sed.setClassSought(rs.getString("class_sought"));
                    sed.setSession(rs.getString("session"));
                    sed.setPen(rs.getString("pen"));
                    sed.setApaarId(rs.getString("apaar_id"));

                    // ===== PERSONAL =====
                    sed.setStudentName(rs.getString("student_name"));
                    sed.setGender(rs.getString("gender"));
                    sed.setDob(rs.getString("dob"));
                    sed.setDobInWords(rs.getString("dob_in_words"));

                    // ===== MOTHER =====
                    sed.setMotherName(rs.getString("mother_name"));
                    sed.setMotherPhone(rs.getString("mother_phone"));
                    sed.setMotherQualification(rs.getString("mother_qualification"));
                    sed.setMotherEmail(rs.getString("mother_email"));
                    sed.setMotherOccupation(rs.getString("mother_occupation"));
                    sed.setMotherLocalAddress(rs.getString("mother_local_address"));
                    sed.setMotherResidentialAddress(rs.getString("mother_residential_address"));
                    sed.setMotherAnnualIncome(rs.getBigDecimal("mother_annual_income"));

                    // ===== FATHER =====
                    sed.setFatherName(rs.getString("father_name"));
                    sed.setFatherPhone(rs.getString("father_phone"));
                    sed.setFatherQualification(rs.getString("father_qualification"));
                    sed.setFatherEmail(rs.getString("father_email"));
                    sed.setFatherOccupation(rs.getString("father_occupation"));
                    sed.setFatherLocalAddress(rs.getString("father_local_address"));
                    sed.setFatherResidentialAddress(rs.getString("father_residential_address"));
                    sed.setFatherAnnualIncome(rs.getBigDecimal("father_annual_income"));

                    // ===== GUARDIAN =====
                    sed.setGuardianName(rs.getString("guardian_name"));
                    sed.setGuardianPhone(rs.getString("guardian_phone"));
                    sed.setGuardianQualification(rs.getString("guardian_qualification"));
                    sed.setGuardianEmail(rs.getString("guardian_email"));
                    sed.setGuardianOccupation(rs.getString("guardian_occupation"));
                    sed.setGuardianLocalAddress(rs.getString("guardian_local_address"));
                    sed.setGuardianResidentialAddress(rs.getString("guardian_residential_address"));
                    sed.setGuardianAnnualIncome(rs.getBigDecimal("guardian_annual_income"));

                    // ===== STATUS =====
                    sed.setIsSingleGirlChild(rs.getBoolean("is_single_girl_child"));
                    sed.setIsSpeciallyAbled(rs.getBoolean("is_specially_abled"));

                    // ===== CATEGORY =====
                    sed.setCategory(rs.getString("category"));
                    sed.setReligion(rs.getString("religion"));

                    // ===== AADHAR =====
                    sed.setAadharNumber(rs.getString("aadhar_number"));

                    // ===== LAST SCHOOL =====
                    sed.setLastSchoolName(rs.getString("last_school_name"));
                    sed.setLastSchoolAddress(rs.getString("last_school_address"));
                    sed.setLastClassAttended(rs.getString("last_class_attended"));
                    sed.setLastSchoolBoard(rs.getString("last_school_board"));

                    // ===== RESULT =====
                    sed.setLastClassResult(rs.getString("last_class_result"));

                    // ===== TC =====
                    sed.setTransferCertificateNumber(rs.getString("transfer_certificate_number"));
                    sed.setTcDateOfIssue(rs.getString("tc_date_of_issue"));

                    sed.setStatus(rs.getString("status"));

                    // ===== SIBLINGS =====
                    String siblingsJson = rs.getString("siblings");
                    if (siblingsJson != null && !siblingsJson.isEmpty()) {
                        try {
                            sed.setSiblings(Arrays.asList(objectMapper.readValue(siblingsJson, StudentEnquirySiblings[].class)));
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing siblings JSON", e);
                        }
                    }

                    // ===== SUBJECTS =====
                    String subjectsJson = rs.getString("subjects");
                    if (subjectsJson != null && !subjectsJson.isEmpty()) {
                        try {
                            sed.setSubjects(Arrays.asList(objectMapper.readValue(subjectsJson, StudentEnquirySubjects[].class)));
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing subjects JSON", e);
                        }
                    }

                    // ===== DECLARATION =====
                    sed.setDeclarationText(rs.getString("declaration_text"));
                    sed.setDeclarationDate(rs.getString("declaration_date"));
                    sed.setPlace(rs.getString("place"));
                    sed.setParentSignature(rs.getString("parent_signature"));
                    sed.setRelationshipWithCandidate(rs.getString("relationship_with_candidate"));
                    sed.setPrincipalSignature(rs.getString("principal_signature"));

                    // ===== OFFICE =====
                    sed.setRegisterPageNo(rs.getString("register_page_no"));
                    sed.setRegisterEntryDate(rs.getString("register_entry_date"));

                    // ===== SYSTEM =====
                    sed.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    sed.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                    return sed;

                }
            });
        } catch (Exception e){
            throw new Exception("Error fetching student enquiry data: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentEnquiryFormDetails;
    }

    @Override
    public StudentEnquiryFormDetails updateStudentEnquiryById(StudentEnquiryFormDetails studentEnquiryFormDetails, String schoolCode) throws Exception {
        String sql = """
                UPDATE student_enquiry_form SET admission_date = ?, admission_no = ?, class_sought = ?, session = ?, pen = ?, apaar_id = ?, student_name = ?, gender = ?,
                dob = ?, dob_in_words = ?, mother_name = ?, mother_phone = ?, mother_qualification = ?, mother_email = ?, mother_occupation = ?, mother_local_address = ?,
                mother_residential_address = ?, mother_annual_income = ?, father_name = ?, father_phone = ?, father_qualification = ?, father_email = ?, father_occupation = ?,
                father_local_address = ?, father_residential_address = ?, father_annual_income = ?, guardian_name = ?, guardian_phone = ?, guardian_qualification = ?,
                guardian_email = ?, guardian_occupation = ?, guardian_local_address = ?, guardian_residential_address = ?, guardian_annual_income = ?,
                is_single_girl_child = ?, is_specially_abled = ?, category = ?, religion = ?, aadhar_number = ?, last_school_name = ?, last_school_address = ?,
                last_class_attended = ?, last_school_board = ?, last_class_result = ?, transfer_certificate_number = ?, tc_date_of_issue = ?, status = ?, siblings = ?::jsonb, subjects = ?::jsonb,
                declaration_text = ?, declaration_date = ?, place = ?, parent_signature = ?, relationship_with_candidate = ?, principal_signature = ?, register_page_no = ?,
                register_entry_date = ?, updated_at = ? WHERE student_enquiry_id = ?
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            String siblingsJson = objectMapper.writeValueAsString(studentEnquiryFormDetails.getSiblings() != null ? studentEnquiryFormDetails.getSiblings() : new ArrayList<>());

            String subjectsJson = objectMapper.writeValueAsString(studentEnquiryFormDetails.getSubjects() != null ? studentEnquiryFormDetails.getSubjects() : new ArrayList<>());

            int rows = jdbcTemplate.update(sql,
                    studentEnquiryFormDetails.getAdmissionDate() != null ? java.sql.Date.valueOf(studentEnquiryFormDetails.getAdmissionDate()) : null,
                    studentEnquiryFormDetails.getAdmissionNo(),
                    studentEnquiryFormDetails.getClassSought(),
                    studentEnquiryFormDetails.getSession(),
                    studentEnquiryFormDetails.getPen(),
                    studentEnquiryFormDetails.getApaarId(),
                    studentEnquiryFormDetails.getStudentName(),
                    studentEnquiryFormDetails.getGender(),
                    studentEnquiryFormDetails.getDob() != null ? java.sql.Date.valueOf(studentEnquiryFormDetails.getDob()) : null,
                    studentEnquiryFormDetails.getDobInWords(),

                    studentEnquiryFormDetails.getMotherName(),
                    studentEnquiryFormDetails.getMotherPhone(),
                    studentEnquiryFormDetails.getMotherQualification(),
                    studentEnquiryFormDetails.getMotherEmail(),
                    studentEnquiryFormDetails.getMotherOccupation(),
                    studentEnquiryFormDetails.getMotherLocalAddress(),
                    studentEnquiryFormDetails.getMotherResidentialAddress(),
                    studentEnquiryFormDetails.getMotherAnnualIncome(),

                    studentEnquiryFormDetails.getFatherName(),
                    studentEnquiryFormDetails.getFatherPhone(),
                    studentEnquiryFormDetails.getFatherQualification(),
                    studentEnquiryFormDetails.getFatherEmail(),
                    studentEnquiryFormDetails.getFatherOccupation(),
                    studentEnquiryFormDetails.getFatherLocalAddress(),
                    studentEnquiryFormDetails.getFatherResidentialAddress(),
                    studentEnquiryFormDetails.getFatherAnnualIncome(),

                    studentEnquiryFormDetails.getGuardianName(),
                    studentEnquiryFormDetails.getGuardianPhone(),
                    studentEnquiryFormDetails.getGuardianQualification(),
                    studentEnquiryFormDetails.getGuardianEmail(),
                    studentEnquiryFormDetails.getGuardianOccupation(),
                    studentEnquiryFormDetails.getGuardianLocalAddress(),
                    studentEnquiryFormDetails.getGuardianResidentialAddress(),
                    studentEnquiryFormDetails.getGuardianAnnualIncome(),

                    Boolean.TRUE.equals(studentEnquiryFormDetails.getIsSingleGirlChild()),
                    Boolean.TRUE.equals(studentEnquiryFormDetails.getIsSpeciallyAbled()),
                    studentEnquiryFormDetails.getCategory(),
                    studentEnquiryFormDetails.getReligion(),
                    studentEnquiryFormDetails.getAadharNumber(),

                    studentEnquiryFormDetails.getLastSchoolName(),
                    studentEnquiryFormDetails.getLastSchoolAddress(),
                    studentEnquiryFormDetails.getLastClassAttended(),
                    studentEnquiryFormDetails.getLastSchoolBoard(),

                    studentEnquiryFormDetails.getLastClassResult(),
                    studentEnquiryFormDetails.getTransferCertificateNumber(),
                    studentEnquiryFormDetails.getTcDateOfIssue() != null ? java.sql.Date.valueOf(studentEnquiryFormDetails.getTcDateOfIssue()) : null,
                    studentEnquiryFormDetails.getStatus(),

                    siblingsJson,
                    subjectsJson,

                    studentEnquiryFormDetails.getDeclarationText(),
                    studentEnquiryFormDetails.getDeclarationDate() != null ? java.sql.Date.valueOf(studentEnquiryFormDetails.getDeclarationDate()) : null,
                    studentEnquiryFormDetails.getPlace(),
                    studentEnquiryFormDetails.getParentSignature(),
                    studentEnquiryFormDetails.getRelationshipWithCandidate(),
                    studentEnquiryFormDetails.getPrincipalSignature(),

                    studentEnquiryFormDetails.getRegisterPageNo(),
                    studentEnquiryFormDetails.getRegisterEntryDate() != null ? java.sql.Date.valueOf(studentEnquiryFormDetails.getRegisterEntryDate()) : null,
                    LocalDateTime.now(), // updated_at

                    studentEnquiryFormDetails.getStudentEnquiryId()
            );

            if (rows == 0) {
                return null; // not found
            }

            return studentEnquiryFormDetails;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error updating student enquiry", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean deleteStudentEnquiry(int studentEnquiryId, String schoolCode) throws Exception {
        String sql = "DELETE FROM student_enquiry_form WHERE student_enquiry_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{studentEnquiryId});
            return rowAffected > 0;
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


}
