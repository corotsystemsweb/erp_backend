package com.sms.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.StudentEnquiryFormDao;
import com.sms.exception.ImageSizeLimitExceededException;
import com.sms.model.StudentDetails;
import com.sms.model.StudentEnquiryFormDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

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
                "last_class_result, transfer_certificate_number, tc_date_of_issue," +
                "siblings, subjects," +
                "declaration_text, declaration_date, place, parent_signature, relationship_with_candidate, principal_signature," +
                "register_page_no, register_entry_date" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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

                // ===== JSONB =====
                ps.setObject(48, siblingsJson, Types.OTHER);
                ps.setObject(49, subjectsJson, Types.OTHER);

                // ===== DECLARATION =====
                ps.setString(50, studentEnquiryFormDetails.getDeclarationText());
                // Declaration Date
                if (studentEnquiryFormDetails.getDeclarationDate() != null)
                    ps.setDate(51, java.sql.Date.valueOf(studentEnquiryFormDetails.getDeclarationDate()));
                else
                    ps.setNull(51, Types.DATE);
                ps.setString(52, studentEnquiryFormDetails.getPlace());
                ps.setString(53, studentEnquiryFormDetails.getParentSignature());
                ps.setString(54, studentEnquiryFormDetails.getRelationshipWithCandidate());
                ps.setString(55, studentEnquiryFormDetails.getPrincipalSignature());

                // ===== OFFICE =====
                ps.setString(56, studentEnquiryFormDetails.getRegisterPageNo());
                // Register Entry Date
                if (studentEnquiryFormDetails.getRegisterEntryDate() != null)
                    ps.setDate(57, java.sql.Date.valueOf(studentEnquiryFormDetails.getRegisterEntryDate()));
                else
                    ps.setNull(57, Types.DATE);

                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                studentEnquiryFormDetails.setId(((Number) keys.get("id")).intValue());
            }

        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Error saving student enquiry", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentEnquiryFormDetails;
    }
}
