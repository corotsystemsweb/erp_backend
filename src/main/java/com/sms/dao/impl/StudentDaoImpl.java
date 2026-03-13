package com.sms.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.StudentDao;
import com.sms.exception.ImageSizeLimitExceededException;
import com.sms.model.*;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static com.sms.dao.impl.DynamicStudentInsertDaoImpl.logger;

@Repository
public class StudentDaoImpl implements StudentDao {
    @Value("${student.img.local.path}")
    private String FOLDER_PATH;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    private MasterSequenceDetailsDaoImpl masterSequenceDetailsDao;

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
    public StudentDetails getImage(String schoolCode, int studentId) throws Exception {
        String fileName = studentId + ".png";
        String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        // Check if the file exists
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new IOException("File not found: " + imagePath);
        }

        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        StudentDetails studentDetails = new StudentDetails();
        studentDetails.setStudentImage(base64Image);
        return studentDetails;
    }
    @Override
    public boolean checkRegistrationNumberExists(String registrationNumber, String schoolCode){
       String checkSql = "SELECT COUNT(*) FROM student_academic_details WHERE registration_number = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           Integer count = jdbcTemplate.queryForObject(checkSql, new Object[]{registrationNumber}, Integer.class);
           if(count != null && count > 0){
               return true;
           }else{
               return false;
           }
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

    }

    //Changing code according vedmark
    @Override
    public StudentDetails addStudentPersonalDetails(StudentDetails studentDetails, String schoolCode) throws Exception {
        int nextSequence = masterSequenceDetailsDao.findNextAvailableSeqCode(schoolCode);
        int nextCurrentValue = masterSequenceDetailsDao.findNextAvailableCurrentValue(schoolCode);
        studentDetails.setStudentId(nextCurrentValue);
        String sql = """
                insert into student_personal_details(student_id, school_id, uu_id, parent_id, father_name, father_occupation,
                mother_name, mother_occupation, first_name, last_name, blood_group, gender, height, weight, aadhar_number, phone_number, 
                emergency_phone_number, whatsapp_no, email_address, dob, dob_cirtificate_no, income_app_no, caste_app_no, domicile_app_no,
                govt_student_id_on_portal, govt_family_id_on_portal, bank_name, branch_name, ifsc_code, account_no, pan_no, religion, 
                nationality, category, caste, current_address, current_city, current_state, current_zipcode, permanent_address, permanent_city, 
                permanent_state, permanent_zipcode, student_country, current_status, current_status_comment, updated_by, updated_date, 
                create_date, validity_start_date, validity_end_date, student_photo) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, nextCurrentValue);
                ps.setInt(2, studentDetails.getSchoolId());
                ps.setString(3, studentDetails.getUuId());
                // Convert parentId List to SQL Array
                List<Integer> parentIds = studentDetails.getParentId(); // Assuming it's a List<Integer>
                Array parentIdArray = connection.createArrayOf("INTEGER", parentIds.toArray());
                ps.setArray(4, parentIdArray); // parent_id
                ps.setString(5, studentDetails.getFatherName());
                ps.setString(6, studentDetails.getFatherOccupation());
                ps.setString(7, studentDetails.getMotherName());
                ps.setString(8, studentDetails.getMotherOccupation());
                ps.setString(9, studentDetails.getFirstName());
                ps.setString(10, studentDetails.getLastName());
                ps.setString(11, studentDetails.getBloodGroup());
                ps.setString(12, studentDetails.getGender());
                ps.setString(13, studentDetails.getHeight());
                ps.setString(14, studentDetails.getWeight());
                ps.setString(15, CipherUtils.encrypt(studentDetails.getAadharNumber()));
                ps.setString(16, CipherUtils.encrypt(studentDetails.getPhoneNumber()));
                ps.setString(17, CipherUtils.encrypt(studentDetails.getEmergencyPhoneNumber()));
                ps.setString(18, CipherUtils.encrypt(studentDetails.getWhatsAppNumber()));
                ps.setString(19, studentDetails.getEmailAddress());
                ps.setDate(  20,   new java.sql.Date(studentDetails.getDob().getTime()));
                ps.setString(21, studentDetails.getDobCirtificateNo());
                ps.setString(22, studentDetails.getIncomeAppNo());
                ps.setString(23, studentDetails.getCasteAppNo());
                ps.setString(24, studentDetails.getDomicileAppNo());
                ps.setString(25, studentDetails.getGovtStudentIdOnPortal());
                ps.setString(26, studentDetails.getGovtFamilyIdOnPortal());
                ps.setString(27, studentDetails.getBankName());
                ps.setString(28, studentDetails.getBranchName());
                ps.setString(29, studentDetails.getIfscCode());
                ps.setString(30, studentDetails.getAccountNumber());
                ps.setString(31, studentDetails.getPanNo());
                ps.setString(32, studentDetails.getReligion());
                ps.setString(33, studentDetails.getNationality());
                ps.setString(34, studentDetails.getCategory());
                ps.setString(35, studentDetails.getCaste());
                ps.setString(36, studentDetails.getCurrentAddress());
                ps.setString(37, studentDetails.getCurrentCity());
                ps.setString(38, studentDetails.getCurrentState());
                ps.setInt(39, studentDetails.getCurrentZipCode());
                ps.setString(40, studentDetails.getPermanentAddress());
                ps.setString(41, studentDetails.getPermanentCity());
                ps.setString(42, studentDetails.getPermanentState());
                ps.setInt(43, studentDetails.getPermanentZipCode());
                ps.setString(44, studentDetails.getStudentCountry());
                ps.setString(45, studentDetails.getCurrentStatus());
                ps.setString(46, studentDetails.getCurrentStatusComment());
                ps.setInt(47, studentDetails.getUpdatedBy());
                ps.setDate(48, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(49, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(50, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(51, java.sql.Date.valueOf("9999-12-31"));
                ps.setString(52, studentDetails.getStudentPhoto());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                int generatedId = ((Number) keys.get("id")).intValue();
                studentDetails.setId(generatedId);
            }
        } catch(DuplicateKeyException e){
            throw new Exception("Student with id " + nextCurrentValue + " already exists. Duplicate entry is not allowed");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }

    //change student academic details based on vedmark
    @Override
    public StudentDetails addStudentAcademicDetails(StudentDetails studentDetails, String schoolCode) throws Exception {
        /*//check is registration number is already exists or not
        if(checkRegistrationNumberExists(studentDetails.getRegistrationNumber(), schoolCode)){
            throw new Exception("Registration number is already available.");
        }*/

        int nextSequence = masterSequenceDetailsDao.findNextAvailableSeqCode(schoolCode);
        int nextCurrentValue = masterSequenceDetailsDao.findNextAvailableCurrentValue(schoolCode);
        studentDetails.setStudentId(nextCurrentValue);
        //String sql = "insert into student_academic_details (student_id, school_id, registration_number, roll_number, session_id, student_class_id, student_section_id, session_status, session_status_comment, updated_by, updated_date, create_date, validity_start_date, validity_end_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql = """
                insert into student_academic_details (student_id, school_id, uu_id, apaar_id, pen_no, admission_no, admission_date, registration_number, 
                roll_number, session_id, student_class_id, student_section_id, stream, education_medium, referred_by, is_rte_student, 
                rte_application_no, enrolled_session, enrolled_class, enrolled_year, transfer_cirti_no, date_of_issue, scholarship_id, 
                scholarship_password, lst_school_name, lst_school_addrs, lst_attended_class, lst_scl_aff_to, lst_session, is_dropout, 
                dropout_date, dropout_reason, student_addmission_type, session_status, session_status_comment, previous_qualifications,
                updated_by, updated_date, create_date, validity_start_date, validity_end_date, student_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,    nextCurrentValue);
                ps.setInt(2,    studentDetails.getSchoolId());
                ps.setString(3, studentDetails.getUuId());
                ps.setString(4, studentDetails.getApaarId());
                ps.setString(5, studentDetails.getPenNo());
                ps.setString(6, studentDetails.getAdmissionNo());
                ps.setDate(  7,   studentDetails.getAdmissionDate() != null ? new java.sql.Date(studentDetails.getAdmissionDate().getTime()) : null);
                ps.setString(8, studentDetails.getRegistrationNumber());
                ps.setString(9, studentDetails.getRollNumber());
                ps.setInt(10, studentDetails.getSessionId());
                ps.setInt(11, studentDetails.getStudentClassId());
                ps.setInt(12, studentDetails.getStudentSectionId());
                ps.setString(13, studentDetails.getStream());
                ps.setString(14, studentDetails.getEducationMedium());
                ps.setString(15, studentDetails.getReferredBy());
                ps.setBoolean(16, studentDetails.isRteStudent());
                ps.setString(17, studentDetails.getRteApplicationNo());
                ps.setString(18, studentDetails.getEnrolledSession());
                ps.setString(19, studentDetails.getEnrolledClass());
                ps.setString(20, studentDetails.getEnrolledYear());
                ps.setString(21, studentDetails.getTransferCirtiNo());
                ps.setDate(22, studentDetails.getDateOfIssue() != null ? new java.sql.Date(studentDetails.getDateOfIssue().getTime()) : null);
                ps.setString(23, studentDetails.getScholarshipId());
                ps.setString(24, studentDetails.getScholarshipPassword() != null ? CipherUtils.encrypt(studentDetails.getScholarshipPassword()) : null);
                ps.setString(25, studentDetails.getLstSchoolName());
                ps.setString(26, studentDetails.getLstSchoolAddress());
                ps.setString(27, studentDetails.getLstAttendedClass());
                ps.setString(28, studentDetails.getLstSclAffTo());
                ps.setString(29, studentDetails.getLstSession());
                ps.setBoolean(30, studentDetails.isDropOut());
                ps.setDate(31, studentDetails.getDropOutDate() != null ? new java.sql.Date(studentDetails.getDropOutDate().getTime()) : null);
                ps.setString(32, studentDetails.getDropOutReason());
                ps.setString(33, studentDetails.getStudentAdmissionType());
                ps.setString(34, studentDetails.getSessionStatus());
                ps.setString(35, studentDetails.getSessionStatusComment());
                try {
                    String jsonString = new ObjectMapper().writeValueAsString(studentDetails.getPreviousQualificationDetails()); // Convert list to JSON
                    ps.setObject(36, jsonString, java.sql.Types.OTHER); // Set as JSONB
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to convert previous qualifications to JSON", e);
                }

                ps.setInt(37, studentDetails.getUpdatedBy());
                ps.setDate(38, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(39, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(40, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(41, java.sql.Date.valueOf("9999-12-31"));
                ps.setString(42, studentDetails.getStudentType());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                int generatedId = ((Number) keys.get("id")).intValue();
                studentDetails.setId(generatedId);
            }
        } catch(DuplicateKeyException e){
            throw new Exception("Student with id " + nextCurrentValue + " already exists. Duplicate entry is not allowed");
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }

    // change get student details by id based on vedmark
    @Override
    public StudentDetails getStudentDetailsById(int studentId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    spd.id AS student_personal_id,
                    spd.student_id,
                    spd.school_id AS personal_school_id,
                    spd.uu_id AS personal_uu_id,
                    spd.first_name,
                    spd.last_name,
                    spd.blood_group,
                    spd.gender,
                    spd.height,
                    spd.weight,
                    spd.aadhar_number,
                    spd.phone_number,
                    spd.emergency_phone_number,
                    spd.whatsapp_no,
                    spd.email_address,
                    spd.dob,
                    spd.dob_cirtificate_no,
                    spd.income_app_no,
                    spd.caste_app_no,
                    spd.domicile_app_no,
                    spd.govt_student_id_on_portal,
                    spd.govt_family_id_on_portal,
                    spd.bank_name,
                    spd.branch_name,
                    spd.ifsc_code,
                    spd.account_no,
                    spd.pan_no,
                    spd.religion,
                    spd.nationality,
                    spd.category,
                    spd.caste,
                    spd.current_address,
                    spd.current_city,
                    spd.current_state,
                    spd.current_zipcode,
                    spd.permanent_address,
                    spd.permanent_city,
                    spd.permanent_state,
                    spd.permanent_zipcode,
                    spd.student_country,
                    spd.current_status,
                    spd.current_status_comment,
                    spd.updated_by AS personal_updated_by,
                    spd.updated_date AS personal_updated_date,
                    spd.create_date AS personal_create_date,
                    spd.validity_start_date AS personal_validity_start_date,
                    spd.validity_end_date AS personal_validity_end_date,
                    spd.student_photo,
                    sad.apaar_id,
                    sad.pen_no,
                    sad.admission_no,
                    sad.admission_date,
                    sad.registration_number,
                    sad.roll_number,
                    sad.session_id,
                    sad.student_class_id,
                    sad.student_section_id,
                    sad.stream,
                    sad.education_medium,
                    sad.referred_by,
                    sad.is_rte_student,
                    sad.rte_application_no,
                    sad.enrolled_session,
                    sad.enrolled_class,
                    sad.enrolled_year,
                    sad.transfer_cirti_no,
                    sad.date_of_issue,
                    sad.scholarship_id,
                    sad.scholarship_password,
                    sad.lst_school_name,
                    sad.lst_school_addrs,
                    sad.lst_attended_class,
                    sad.lst_scl_aff_to,
                    sad.lst_session,
                    sad.is_dropout,
                    sad.dropout_date,
                    sad.dropout_reason,
                    sad.student_addmission_type,
                    sad.session_status,
                    sad.session_status_comment,
                    sad.previous_qualifications::TEXT AS previous_qualifications,
                    sad.student_type,
                    mc.class_id AS class_id,
                    mc.class_name,
                    ms.section_id AS section_id,
                    ms.section_name,
                    ses.academic_session,
                    sd.school_name,
                    sd.school_address,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.school_zipcode,
                    sd.email_address AS school_email_address,
                    sd.phone_number AS school_phone_number,
                    COALESCE(
                        JSON_AGG(
                            JSON_BUILD_OBJECT(
                                'parentId', pd.parent_id,
                                'firstName', pd.first_name,
                                'lastName', pd.last_name,
                                'dob', pd.dob,
                                'phoneNumber', pd.phone_number,
                                'emergencyPhoneNumber', pd.emergency_phone_number,
                                'whatsappNumber', pd.whatsapp_no,
                                'email', pd.email_address,
                                'gender', pd.gender,
                                'parentType', pd.parent_type,
                                'qualification', pd.qualification,
                                'aadharNumber', pd.aadhar_number,
                                'companyName', pd.company_name,
                                'designation', pd.designation,
                                'companyAddress', pd.company_address,
                                'companyPhone', pd.company_phone,
                                'address', pd.address,
                                'city', pd.city,
                                'state', pd.state,
                                'zipcode', pd.zipcode
                            )
                        ) FILTER (WHERE pd.parent_id IS NOT NULL AND pd.deleted IS NOT TRUE), '[]'
                    ) AS parent_details
                FROM
                    student_personal_details AS spd
                INNER JOIN
                    student_academic_details AS sad ON spd.student_id = sad.student_id
                INNER JOIN
                    mst_class AS mc ON sad.student_class_id = mc.class_id
                INNER JOIN
                    mst_section AS ms ON sad.student_section_id = ms.section_id
                INNER JOIN
                    session AS ses ON sad.session_id = ses.session_id
                INNER JOIN
                    school_details AS sd ON spd.school_id = sd.school_id
                LEFT JOIN
                    parent_details AS pd ON pd.parent_id = ANY(spd.parent_id)
                WHERE
                    spd.student_id = ?
                    AND spd.school_id = 1
                    AND sad.school_id = 1
                    AND spd.validity_end_date >= NOW()
                    AND sad.validity_end_date >= NOW()
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    spd.id, spd.student_id, spd.school_id, spd.uu_id,
                    spd.first_name, spd.last_name, spd.blood_group, spd.gender,
                    spd.height, spd.weight, spd.aadhar_number, spd.phone_number, spd.emergency_phone_number, spd.whatsapp_no,
                    spd.email_address, spd.dob, spd.dob_cirtificate_no, spd.income_app_no, spd.caste_app_no, spd.domicile_app_no,
                    spd.govt_student_id_on_portal, spd.govt_family_id_on_portal, spd.bank_name, spd.branch_name, spd.ifsc_code,
                    spd.account_no, spd.pan_no, spd.religion, spd.nationality, spd.category, spd.caste, spd.current_address,
                    spd.current_city, spd.current_state, spd.current_zipcode, spd.permanent_address, spd.permanent_city,
                    spd.permanent_state, spd.permanent_zipcode, spd.student_country, spd.current_status, spd.current_status_comment,
                    spd.updated_by, spd.updated_date, spd.create_date, spd.validity_start_date, spd.validity_end_date,
                    spd.student_photo, sad.apaar_id, sad.pen_no, sad.admission_no,
                    sad.admission_date, sad.registration_number, sad.roll_number, sad.session_id, sad.student_class_id,
                    sad.student_section_id, sad.stream, sad.education_medium, sad.referred_by, sad.is_rte_student, sad.rte_application_no,
                    sad.enrolled_session, sad.enrolled_class, sad.enrolled_year, sad.transfer_cirti_no, sad.date_of_issue,
                    sad.scholarship_id, sad.scholarship_password, sad.lst_school_name, sad.lst_school_addrs, sad.lst_attended_class,
                    sad.lst_scl_aff_to, sad.lst_session, sad.is_dropout, sad.dropout_date, sad.dropout_reason, sad.student_addmission_type,
                    sad.session_status, sad.session_status_comment, sad.previous_qualifications, sad.student_type, mc.class_id, mc.class_name,
                    ms.section_id, ms.section_name, ses.academic_session, sd.school_name, sd.school_address, sd.school_city, sd.school_state,
                    sd.school_country, sd.school_zipcode, sd.email_address, sd.phone_number            
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        StudentDetails studentDetails = null;
        try{
            studentDetails =  jdbcTemplate.queryForObject(sql, new Object[]{studentId}, new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails sd = new StudentDetails();
                    sd.setId(rs.getInt("student_personal_id"));
                    sd.setStudentId(rs.getInt("student_id"));
                    sd.setSchoolId(rs.getInt("personal_school_id"));
                    sd.setUuId(rs.getString("personal_uu_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setHeight(rs.getString("height"));
                    sd.setWeight(rs.getString("weight"));
                    sd.setAadharNumber(rs.getString("aadhar_number") != null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    sd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setWhatsAppNumber(rs.getString("whatsapp_no") != null ? CipherUtils.decrypt(rs.getString("whatsapp_no")) : null);
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setDobCirtificateNo(rs.getString("dob_cirtificate_no"));
                    sd.setIncomeAppNo(rs.getString("income_app_no"));
                    sd.setCasteAppNo(rs.getString("caste_app_no"));
                    sd.setDomicileAppNo(rs.getString("domicile_app_no"));
                    sd.setGovtStudentIdOnPortal(rs.getString("govt_student_id_on_portal"));
                    sd.setGovtFamilyIdOnPortal(rs.getString("govt_family_id_on_portal"));
                    sd.setBankName(rs.getString("bank_name"));
                    sd.setBranchName(rs.getString("branch_name"));
                    sd.setIfscCode(rs.getString("ifsc_code"));
                    sd.setAccountNumber(rs.getString("account_no"));
                    sd.setPanNo(rs.getString("pan_no"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setNationality(rs.getString("nationality"));
                    sd.setCategory(rs.getString("category"));
                    sd.setCaste(rs.getString("caste"));
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setStudentCountry(rs.getString("student_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setUpdatedBy(rs.getInt("personal_updated_by"));
                    sd.setUpdatedDate(rs.getDate("personal_updated_date"));
                    sd.setCreateDate(rs.getDate("personal_create_date"));
                    sd.setValidityStartDate(rs.getDate("personal_validity_start_date"));
                    sd.setValidityEndDate(rs.getDate("personal_validity_end_date"));
                    sd.setStudentPhoto(rs.getString("student_photo"));
                    // student academic details
                    sd.setApaarId(rs.getString("apaar_id"));
                    sd.setPenNo(rs.getString("pen_no"));
                    sd.setAdmissionNo(rs.getString("admission_no"));
                    sd.setAdmissionDate(rs.getDate("admission_date"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setRollNumber(rs.getString("roll_number"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setStudentClassId(rs.getInt("student_class_id"));
                    sd.setStudentSectionId(rs.getInt("student_section_id"));
                    sd.setStream(rs.getString("stream"));
                    sd.setEducationMedium(rs.getString("education_medium"));
                    sd.setReferredBy(rs.getString("referred_by"));
                    sd.setRteStudent(rs.getBoolean("is_rte_student"));
                    sd.setRteApplicationNo(rs.getString("rte_application_no"));
                    sd.setEnrolledSession(rs.getString("enrolled_session"));
                    sd.setEnrolledClass(rs.getString("enrolled_class"));
                    sd.setEnrolledYear(rs.getString("enrolled_year"));
                    sd.setTransferCirtiNo(rs.getString("transfer_cirti_no"));
                    sd.setDateOfIssue(rs.getDate("date_of_issue"));
                    sd.setScholarshipId(rs.getString("scholarship_id"));
                    sd.setScholarshipPassword(rs.getString("scholarship_password") != null ? CipherUtils.decrypt(rs.getString("scholarship_password")) : null);
                    sd.setLstSchoolName(rs.getString("lst_school_name"));
                    sd.setLstSchoolAddress(rs.getString("lst_school_addrs"));
                    sd.setLstAttendedClass(rs.getString("lst_attended_class"));
                    sd.setLstSclAffTo(rs.getString("lst_scl_aff_to"));
                    sd.setLstSession(rs.getString("lst_session"));
                    sd.setDropOut(rs.getBoolean("is_dropout"));
                    sd.setDropOutDate(rs.getDate("dropout_date"));
                    sd.setDropOutReason(rs.getString("dropout_reason"));
                    sd.setStudentAdmissionType(rs.getString("student_addmission_type"));
                    sd.setSessionStatus(rs.getString("session_status"));
                    sd.setSessionStatusComment(rs.getString("session_status_comment"));
                    // Mapping previous qualification details
                    String previousQualifications = rs.getString("previous_qualifications");
                    if (previousQualifications != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<PreviousQualificationDetails> qualifications = objectMapper.readValue(previousQualifications, new TypeReference<List<PreviousQualificationDetails>>() {});
                            sd.setPreviousQualificationDetails(qualifications);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sd.setPreviousQualificationDetails(null);
                        }
                    } else {
                        sd.setPreviousQualificationDetails(null);
                    }
                    sd.setStudentType(rs.getString("student_type"));
                    sd.setClassId(rs.getInt("class_id"));
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionId(rs.getInt("section_id"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setAcademicSession(rs.getString("academic_session"));
                    sd.setSchoolName(rs.getString("school_name"));
                    sd.setSchoolAddress(rs.getString("school_address"));
                    sd.setSchoolCity(rs.getString("school_city"));
                    sd.setSchoolState(rs.getString("school_state"));
                    sd.setSchoolCountry(rs.getString("school_country"));
                    sd.setSchoolZipCode(rs.getInt("school_zipcode"));
                    sd.setSchoolEmailAddress(rs.getString("school_email_address"));
                    sd.setSchoolPhoneNumber(rs.getString("school_phone_number") != null ? CipherUtils.decrypt(rs.getString("school_phone_number")) : null);
                    // Mapping parent_details
                    String parentDetailsJson = rs.getString("parent_details");

                    if (parentDetailsJson != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});

                            // Decrypt sensitive fields
                            for (ParentDetails parent : parentDetailsList) {
                                if (parent.getPhoneNumber() != null) {
                                    parent.setPhoneNumber(CipherUtils.decrypt(parent.getPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                                if (parent.getAadharNumber() != null) {
                                    parent.setAadharNumber(CipherUtils.decrypt(parent.getAadharNumber()));
                                }
                                if (parent.getEmergencyPhoneNumber() != null) {
                                    parent.setEmergencyPhoneNumber(CipherUtils.decrypt(parent.getEmergencyPhoneNumber()));
                                }
                                if(parent.getCompanyPhone() != null) {
                                    parent.setCompanyPhone(CipherUtils.decrypt(parent.getCompanyPhone()));
                                }
                            }

                            sd.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sd.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        sd.setParentDetails(null);
                    }
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }

    @Override
    public List<StudentDetails> getAllStudentDetails(int sessionId,String schoolCode) throws Exception {
        String sql = """
                SELECT
                    spd.id AS student_personal_id,
                    spd.student_id,
                    spd.school_id AS personal_school_id,
                    spd.uu_id AS personal_uu_id,
                    spd.first_name,
                    spd.last_name,
                    spd.blood_group,
                    spd.gender,
                    spd.height,
                    spd.weight,
                    spd.aadhar_number,
                    spd.phone_number,
                    spd.emergency_phone_number,
                    spd.whatsapp_no,
                    spd.email_address,
                    spd.dob,
                    spd.dob_cirtificate_no,
                    spd.income_app_no,
                    spd.caste_app_no,
                    spd.domicile_app_no,
                    spd.govt_student_id_on_portal,
                    spd.govt_family_id_on_portal,
                    spd.bank_name,
                    spd.branch_name,
                    spd.ifsc_code,
                    spd.account_no,
                    spd.pan_no,
                    spd.religion,
                    spd.nationality,
                    spd.category,
                    spd.caste,
                    spd.current_address,
                    spd.current_city,
                    spd.current_state,
                    spd.current_zipcode,
                    spd.permanent_address,
                    spd.permanent_city,
                    spd.permanent_state,
                    spd.permanent_zipcode,
                    spd.student_country,
                    spd.current_status,
                    spd.current_status_comment,
                    spd.updated_by AS personal_updated_by,
                    spd.updated_date AS personal_updated_date,
                    spd.create_date AS personal_create_date,
                    spd.validity_start_date AS personal_validity_start_date,
                    spd.validity_end_date AS personal_validity_end_date,
                    spd.student_photo,
                    sad.apaar_id,
                    sad.pen_no,
                    sad.admission_no,
                    sad.admission_date,
                    sad.registration_number,
                    sad.roll_number,
                    sad.session_id,
                    sad.student_class_id,
                    sad.student_section_id,
                    sad.stream,
                    sad.education_medium,
                    sad.referred_by,
                    sad.is_rte_student,
                    sad.rte_application_no,
                    sad.enrolled_session,
                    sad.enrolled_class,
                    sad.enrolled_year,
                    sad.transfer_cirti_no,
                    sad.date_of_issue,
                    sad.scholarship_id,
                    sad.scholarship_password,
                    sad.lst_school_name,
                    sad.lst_school_addrs,
                    sad.lst_attended_class,
                    sad.lst_scl_aff_to,
                    sad.lst_session,
                    sad.is_dropout,
                    sad.dropout_date,
                    sad.dropout_reason,
                    sad.student_addmission_type,
                    sad.session_status,
                    sad.session_status_comment,
                    sad.previous_qualifications::TEXT AS previous_qualifications,
                    sad.student_type,
                    mc.class_id AS class_id,
                    mc.class_name,
                    ms.section_id AS section_id,
                    ms.section_name,
                    ses.academic_session,
                    sd.school_name,
                    sd.school_address,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.school_zipcode,
                    sd.email_address AS school_email_address,
                    sd.phone_number AS school_phone_number,
                    COALESCE(
                        JSON_AGG(
                            JSON_BUILD_OBJECT(
                                'parentId', pd.parent_id,
                                'firstName', pd.first_name,
                                'lastName', pd.last_name,
                                'dob', pd.dob,
                                'phoneNumber', pd.phone_number,
                                'emergencyPhoneNumber', pd.emergency_phone_number,
                                'whatsappNumber', pd.whatsapp_no,
                                'email', pd.email_address,
                                'gender', pd.gender,
                                'parentType', pd.parent_type,
                                'qualification', pd.qualification,
                                'aadharNumber', pd.aadhar_number,
                                'companyName', pd.company_name,
                                'designation', pd.designation,
                                'companyAddress', pd.company_address,
                                'companyPhone', pd.company_phone,
                                'address', pd.address,
                                'city', pd.city,
                                'state', pd.state,
                                'zipcode', pd.zipcode
                            )
                        ) FILTER (WHERE pd.parent_id IS NOT NULL AND pd.deleted IS NOT TRUE), '[]'
                    ) AS parent_details
                FROM
                    student_personal_details AS spd
                INNER JOIN
                    student_academic_details AS sad ON spd.student_id = sad.student_id
                INNER JOIN
                    mst_class AS mc ON sad.student_class_id = mc.class_id
                INNER JOIN
                    mst_section AS ms ON sad.student_section_id = ms.section_id
                INNER JOIN
                    session AS ses ON sad.session_id = ses.session_id
                INNER JOIN
                    school_details AS sd ON spd.school_id = sd.school_id
                LEFT JOIN
                    parent_details AS pd ON pd.parent_id = ANY(spd.parent_id)
                WHERE
                    spd.validity_end_date >= NOW()
                    AND sad.validity_end_date >= NOW()
                    AND sad.session_id=?
                    AND spd.deleted IS NOT TRUE
                GROUP BY
                    spd.id, spd.student_id, spd.school_id, spd.uu_id,
                    spd.first_name, spd.last_name, spd.blood_group, spd.gender,
                    spd.height, spd.weight, spd.aadhar_number, spd.phone_number, spd.emergency_phone_number, spd.whatsapp_no,
                    spd.email_address, spd.dob, spd.dob_cirtificate_no, spd.income_app_no, spd.caste_app_no, spd.domicile_app_no,
                    spd.govt_student_id_on_portal, spd.govt_family_id_on_portal, spd.bank_name, spd.branch_name, spd.ifsc_code,
                    spd.account_no, spd.pan_no, spd.religion, spd.nationality, spd.category, spd.caste, spd.current_address,
                    spd.current_city, spd.current_state, spd.current_zipcode, spd.permanent_address, spd.permanent_city,
                    spd.permanent_state, spd.permanent_zipcode, spd.student_country, spd.current_status, spd.current_status_comment,
                    spd.updated_by, spd.updated_date, spd.create_date, spd.validity_start_date, spd.validity_end_date,
                    spd.student_photo, sad.apaar_id, sad.pen_no, sad.admission_no,
                    sad.admission_date, sad.registration_number, sad.roll_number, sad.session_id, sad.student_class_id,
                    sad.student_section_id, sad.stream, sad.education_medium, sad.referred_by, sad.is_rte_student, sad.rte_application_no,
                    sad.enrolled_session, sad.enrolled_class, sad.enrolled_year, sad.transfer_cirti_no, sad.date_of_issue,
                    sad.scholarship_id, sad.scholarship_password, sad.lst_school_name, sad.lst_school_addrs, sad.lst_attended_class,
                    sad.lst_scl_aff_to, sad.lst_session, sad.is_dropout, sad.dropout_date, sad.dropout_reason, sad.student_addmission_type,
                    sad.session_status, sad.session_status_comment, sad.previous_qualifications, sad.student_type, mc.class_id, mc.class_name,
                    ms.section_id, ms.section_name, ses.academic_session, sd.school_name, sd.school_address, sd.school_city, sd.school_state,
                    sd.school_country, sd.school_zipcode, sd.email_address, sd.phone_number
                ORDER BY\s
                    CASE\s
                        WHEN LOWER(class_name) ~ 'playway|play way|play group|pw' THEN 0
                        WHEN LOWER(class_name) ~ 'nursery|nur|nr' THEN 1
                        WHEN LOWER(class_name) ~ 'l\\.?k\\.?g|lower kg' THEN 2
                        WHEN LOWER(class_name) ~ 'u\\.?k\\.?g|upper kg' THEN 3
                        WHEN LOWER(class_name) ~ 'kg|kindergarten' THEN 4
                        WHEN class_name ~ '^[0-9]' THEN
                            CAST(SUBSTRING(class_name FROM '^[0-9]+') AS INTEGER) + 10
                        ELSE 99  
                    END ASC,
                    class_name ASC 
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentDetails> studentDetails = null;
        try{
            studentDetails =  jdbcTemplate.query(sql,new Object[]{sessionId} ,new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails sd = new StudentDetails();
                    sd.setId(rs.getInt("student_personal_id"));
                    sd.setStudentId(rs.getInt("student_id"));
                    sd.setSchoolId(rs.getInt("personal_school_id"));
                    sd.setUuId(rs.getString("personal_uu_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setHeight(rs.getString("height"));
                    sd.setWeight(rs.getString("weight"));
                    sd.setAadharNumber(rs.getString("aadhar_number") != null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    sd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setWhatsAppNumber(rs.getString("whatsapp_no") != null ? CipherUtils.decrypt(rs.getString("whatsapp_no")) : null);
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setDobCirtificateNo(rs.getString("dob_cirtificate_no"));
                    sd.setIncomeAppNo(rs.getString("income_app_no"));
                    sd.setCasteAppNo(rs.getString("caste_app_no"));
                    sd.setDomicileAppNo(rs.getString("domicile_app_no"));
                    sd.setGovtStudentIdOnPortal(rs.getString("govt_student_id_on_portal"));
                    sd.setGovtFamilyIdOnPortal(rs.getString("govt_family_id_on_portal"));
                    sd.setBankName(rs.getString("bank_name"));
                    sd.setBranchName(rs.getString("branch_name"));
                    sd.setIfscCode(rs.getString("ifsc_code"));
                    sd.setAccountNumber(rs.getString("account_no"));
                    sd.setPanNo(rs.getString("pan_no"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setNationality(rs.getString("nationality"));
                    sd.setCategory(rs.getString("category"));
                    sd.setCaste(rs.getString("caste"));
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setStudentCountry(rs.getString("student_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setUpdatedBy(rs.getInt("personal_updated_by"));
                    sd.setUpdatedDate(rs.getDate("personal_updated_date"));
                    sd.setCreateDate(rs.getDate("personal_create_date"));
                    sd.setValidityStartDate(rs.getDate("personal_validity_start_date"));
                    sd.setValidityEndDate(rs.getDate("personal_validity_end_date"));
                    sd.setStudentPhoto(rs.getString("student_photo"));
                    // student academic details
                    sd.setApaarId(rs.getString("apaar_id"));
                    sd.setPenNo(rs.getString("pen_no"));
                    sd.setAdmissionNo(rs.getString("admission_no"));
                    sd.setAdmissionDate(rs.getDate("admission_date"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setRollNumber(rs.getString("roll_number"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setStudentClassId(rs.getInt("student_class_id"));
                    sd.setStudentSectionId(rs.getInt("student_section_id"));
                    sd.setStream(rs.getString("stream"));
                    sd.setEducationMedium(rs.getString("education_medium"));
                    sd.setReferredBy(rs.getString("referred_by"));
                    sd.setRteStudent(rs.getBoolean("is_rte_student"));
                    sd.setRteApplicationNo(rs.getString("rte_application_no"));
                    sd.setEnrolledSession(rs.getString("enrolled_session"));
                    sd.setEnrolledClass(rs.getString("enrolled_class"));
                    sd.setEnrolledYear(rs.getString("enrolled_year"));
                    sd.setTransferCirtiNo(rs.getString("transfer_cirti_no"));
                    sd.setDateOfIssue(rs.getDate("date_of_issue"));
                    sd.setScholarshipId(rs.getString("scholarship_id"));
                    sd.setScholarshipPassword(rs.getString("scholarship_password") != null ? CipherUtils.decrypt(rs.getString("scholarship_password")) : null);
                    sd.setLstSchoolName(rs.getString("lst_school_name"));
                    sd.setLstSchoolAddress(rs.getString("lst_school_addrs"));
                    sd.setLstAttendedClass(rs.getString("lst_attended_class"));
                    sd.setLstSclAffTo(rs.getString("lst_scl_aff_to"));
                    sd.setLstSession(rs.getString("lst_session"));
                    sd.setDropOut(rs.getBoolean("is_dropout"));
                    sd.setDropOutDate(rs.getDate("dropout_date"));
                    sd.setDropOutReason(rs.getString("dropout_reason"));
                    sd.setStudentAdmissionType(rs.getString("student_addmission_type"));
                    sd.setSessionStatus(rs.getString("session_status"));
                    sd.setSessionStatusComment(rs.getString("session_status_comment"));
                    // Mapping previous qualification details
                    String previousQualifications = rs.getString("previous_qualifications");
                    if (previousQualifications != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<PreviousQualificationDetails> qualifications = objectMapper.readValue(previousQualifications, new TypeReference<List<PreviousQualificationDetails>>() {});
                            sd.setPreviousQualificationDetails(qualifications);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sd.setPreviousQualificationDetails(null);
                        }
                    } else {
                        sd.setPreviousQualificationDetails(null);
                    }
                    sd.setStudentType(rs.getString("student_type"));
                    sd.setClassId(rs.getInt("class_id"));
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionId(rs.getInt("section_id"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setAcademicSession(rs.getString("academic_session"));
                    sd.setSchoolName(rs.getString("school_name"));
                    sd.setSchoolAddress(rs.getString("school_address"));
                    sd.setSchoolCity(rs.getString("school_city"));
                    sd.setSchoolState(rs.getString("school_state"));
                    sd.setSchoolCountry(rs.getString("school_country"));
                    sd.setSchoolZipCode(rs.getInt("school_zipcode"));
                    sd.setSchoolEmailAddress(rs.getString("school_email_address"));
                    sd.setSchoolPhoneNumber(rs.getString("school_phone_number") != null ? CipherUtils.decrypt(rs.getString("school_phone_number")) : null);
                    // Mapping parent_details
                    String parentDetailsJson = rs.getString("parent_details");

                    if (parentDetailsJson != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});

                            // Decrypt sensitive fields
                            for (ParentDetails parent : parentDetailsList) {
                                if (parent.getPhoneNumber() != null) {
                                    parent.setPhoneNumber(CipherUtils.decrypt(parent.getPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                                if (parent.getAadharNumber() != null) {
                                    parent.setAadharNumber(CipherUtils.decrypt(parent.getAadharNumber()));
                                }
                                if (parent.getEmergencyPhoneNumber() != null) {
                                    parent.setEmergencyPhoneNumber(CipherUtils.decrypt(parent.getEmergencyPhoneNumber()));
                                }
                                if(parent.getCompanyPhone() != null) {
                                    parent.setCompanyPhone(CipherUtils.decrypt(parent.getCompanyPhone()));
                                }
                            }

                            sd.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sd.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        sd.setParentDetails(null);
                    }
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }

    @Override
    public StudentDetails updateStudentPersonalDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception {
        //Encrypting aadhar number
        String encryptedAadharNumber = CipherUtils.encrypt(studentDetails.getAadharNumber());
        studentDetails.setAadharNumber(encryptedAadharNumber);
        // Encrypting phone number
        String encryptedPhoneNumber = CipherUtils.encrypt(studentDetails.getPhoneNumber());
        studentDetails.setPhoneNumber(encryptedPhoneNumber);
        // Encrypting emergency  phone number
        String encryptedEmergencyPhoneNumber = CipherUtils.encrypt(studentDetails.getEmergencyPhoneNumber());
        studentDetails.setEmergencyPhoneNumber(encryptedEmergencyPhoneNumber);
        // Encrypting emergency  whatsapp number
        String encryptedWhatsappNumber = CipherUtils.encrypt(studentDetails.getWhatsAppNumber());
        studentDetails.setWhatsAppNumber(encryptedWhatsappNumber);
        String sql = """
                update student_personal_details set school_id = ?, uu_id = ?, parent_id = ?, father_name = ?, father_occupation = ?,
                mother_name = ?, mother_occupation = ?, first_name = ?, last_name = ?, blood_group = ?, gender = ?, height = ?, weight = ?, aadhar_number = ?, phone_number = ?, 
                emergency_phone_number = ?, whatsapp_no = ?, email_address = ?, dob = ?, dob_cirtificate_no = ?, income_app_no = ?, caste_app_no = ?, domicile_app_no = ?,
                govt_student_id_on_portal = ?, govt_family_id_on_portal = ?, bank_name = ?, branch_name = ?, ifsc_code = ?, account_no = ?, pan_no = ?, religion = ?, 
                nationality = ?, category = ?, caste = ?, current_address = ?, current_city = ?, current_state = ?, current_zipcode = ?, permanent_address = ?, permanent_city = ?, 
                permanent_state = ?, permanent_zipcode = ?, student_country = ?, current_status = ?, current_status_comment = ?, updated_by = ?, updated_date = CURRENT_DATE, 
                create_date = ?, validity_start_date = ?, validity_end_date = ?, student_photo = ?
                where student_id = ? 
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    studentDetails.getSchoolId(),
                    studentDetails.getUuId(),
                    studentDetails.getParentId() != null && ! studentDetails.getParentId().isEmpty() ? jdbcTemplate.getDataSource().getConnection().createArrayOf("INTEGER", studentDetails.getParentId().toArray()) : null,
                    studentDetails.getFatherName(),
                    studentDetails.getFatherOccupation(),
                    studentDetails.getMotherName(),
                    studentDetails.getMotherOccupation(),
                    studentDetails.getFirstName(),
                    studentDetails.getLastName(),
                    studentDetails.getBloodGroup(),
                    studentDetails.getGender(),
                    studentDetails.getHeight(),
                    studentDetails.getWeight(),
                    studentDetails.getAadharNumber(),
                    studentDetails.getPhoneNumber(),
                    studentDetails.getEmergencyPhoneNumber(),
                    studentDetails.getWhatsAppNumber(),
                    studentDetails.getEmailAddress(),
                    studentDetails.getDob(),
                    studentDetails.getDobCirtificateNo(),
                    studentDetails.getIncomeAppNo(),
                    studentDetails.getCasteAppNo(),
                    studentDetails.getDomicileAppNo(),
                    studentDetails.getGovtStudentIdOnPortal(),
                    studentDetails.getGovtFamilyIdOnPortal(),
                    studentDetails.getBankName(),
                    studentDetails.getBranchName(),
                    studentDetails.getIfscCode(),
                    studentDetails.getAccountNumber(),
                    studentDetails.getPanNo(),
                    studentDetails.getReligion(),
                    studentDetails.getNationality(),
                    studentDetails.getCategory(),
                    studentDetails.getCaste(),
                    studentDetails.getCurrentAddress(),
                    studentDetails.getCurrentCity(),
                    studentDetails.getCurrentState(),
                    studentDetails.getCurrentZipCode(),
                    studentDetails.getPermanentAddress(),
                    studentDetails.getPermanentCity(),
                    studentDetails.getPermanentState(),
                    studentDetails.getPermanentZipCode(),
                    studentDetails.getStudentCountry(),
                    studentDetails.getCurrentStatus(),
                    studentDetails.getCurrentStatusComment(),
                    studentDetails.getUpdatedBy(),
                    studentDetails.getCreateDate(),
                    studentDetails.getValidityStartDate(),
                    studentDetails.getValidityEndDate(),
                    studentDetails.getStudentPhoto(),
                    studentId);
            if(rowAffected > 0 ){
                return studentDetails;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }

   @Override
   public StudentDetails updateStudentAcademicDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception {
       //Encrypting scholarship password
       String encryptedScholarshipPassword = studentDetails.getScholarshipPassword() != null ? CipherUtils.encrypt(studentDetails.getScholarshipPassword()) : null;
       studentDetails.setScholarshipPassword(encryptedScholarshipPassword);
       // Convert the previousQualificationDetails list to JSON
       String previousQualificationsJson = "[]";
       if (studentDetails.getPreviousQualificationDetails() != null) {
           try {
               previousQualificationsJson = new ObjectMapper().writeValueAsString(studentDetails.getPreviousQualificationDetails());
           } catch (JsonProcessingException e) {
               throw new RuntimeException("Failed to convert previous qualifications to JSON", e);
           }
       }
       String sql = """
               update student_academic_details set school_id = ?, uu_id = ?, apaar_id = ?, pen_no = ?, admission_no = ?, admission_date = ?, registration_number = ?, 
               roll_number = ?, session_id = ?, student_class_id = ?, student_section_id = ?, stream = ?, education_medium = ?, referred_by = ?, is_rte_student = ?, 
               rte_application_no = ?, enrolled_session = ?, enrolled_class = ?, enrolled_year = ?, transfer_cirti_no = ?, date_of_issue = ?, scholarship_id = ?, 
               scholarship_password = ?, lst_school_name = ?, lst_school_addrs = ?, lst_attended_class = ?, lst_scl_aff_to = ?, lst_session = ?, is_dropout = ?, 
               dropout_date = ?, dropout_reason = ?, student_addmission_type = ?, session_status = ?, session_status_comment = ?, previous_qualifications = ? :: jsonb,
               student_type = ?, updated_by = ?, updated_date = CURRENT_DATE, create_date = ?, validity_start_date = ?, validity_end_date = ? where student_id = ?
               """;
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql,
                   studentDetails.getSchoolId(),
                   studentDetails.getUuId(),
                   studentDetails.getApaarId(),
                   studentDetails.getPenNo(),
                   studentDetails.getAdmissionNo(),
                   studentDetails.getAdmissionDate(),
                   studentDetails.getRegistrationNumber(),
                   studentDetails.getRollNumber(),
                   studentDetails.getSessionId(),
                   studentDetails.getStudentClassId(),
                   studentDetails.getStudentSectionId(),
                   studentDetails.getStream(),
                   studentDetails.getEducationMedium(),
                   studentDetails.getReferredBy(),
                   studentDetails.isRteStudent(),
                   studentDetails.getRteApplicationNo(),
                   studentDetails.getEnrolledSession(),
                   studentDetails.getEnrolledClass(),
                   studentDetails.getEnrolledYear(),
                   studentDetails.getTransferCirtiNo(),
                   studentDetails.getDateOfIssue(),
                   studentDetails.getScholarshipId(),
                   studentDetails.getScholarshipPassword(),
                   studentDetails.getLstSchoolName(),
                   studentDetails.getLstSchoolAddress(),
                   studentDetails.getLstAttendedClass(),
                   studentDetails.getLstSclAffTo(),
                   studentDetails.getLstSession(),
                   studentDetails.isDropOut(),
                   studentDetails.getDropOutDate(),
                   studentDetails.getDropOutReason(),
                   studentDetails.getStudentAdmissionType(),
                   studentDetails.getSessionStatus(),
                   studentDetails.getSessionStatusComment(),
                   previousQualificationsJson,
                   studentDetails.getStudentType(),
                   studentDetails.getUpdatedBy(),
                   studentDetails.getCreateDate(),
                   studentDetails.getValidityStartDate(),
                   studentDetails.getValidityEndDate(),
                   studentId);
           if(rowAffected > 0 ){
               return studentDetails;
           }
           return null;
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

   }
    @Override
    public boolean softDeleteStudent(int studentId, String schoolCode) throws Exception {
        String sql = "UPDATE student_personal_details SET deleted = TRUE WHERE student_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, studentId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public List<StudentDetails> searchStudentByClassNameAndSection(String studentClass, String studentSection, String schoolCode) throws Exception {
        String sql = "SELECT spd.student_id, spd.school_id, spd.first_name, spd.last_name, spd.father_name, sad.registration_number, sad.roll_number, sad.session_id, sad.student_class_id, sad.student_section_id, mc.class_id, mc.class_name, ms.section_id AS section_id, ms.section_name FROM student_personal_details AS spd INNER JOIN student_academic_details AS sad ON spd.student_id = sad.student_id INNER JOIN mst_class AS mc ON sad.student_class_id = mc.class_id INNER JOIN mst_section AS ms ON sad.student_section_id = ms.section_id WHERE spd.school_id = 1 AND sad.school_id = 1 AND spd.validity_end_date >= NOW() AND sad.validity_end_date >= NOW() AND spd.deleted IS NOT TRUE AND mc.class_name = ? AND ms.section_name = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentDetails> studentDetailsList = null;
        try{
            studentDetailsList = jdbcTemplate.query(sql, new Object[]{studentClass, studentSection}, new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails sd = new StudentDetails();
                    sd.setStudentId(rs.getInt("student_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setRollNumber(rs.getString("roll_number"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setStudentClassId(rs.getInt("student_class_id"));
                    sd.setStudentSectionId(rs.getInt("student_section_id"));
                    sd.setClassId(rs.getInt("class_id"));
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionId(rs.getInt("section_id"));
                    sd.setSectionName(rs.getString("section_name"));
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetailsList;
    }
    @Override
    public List<StudentDetails> searchByClassSectionAndSession(int studentClass, int studentSection, int sessionId, String schoolCode) throws Exception {
//        String sql = """
//                SELECT spd.student_id, spd.first_name, spd.last_name, cas.class_id, mc.class_name, cas.section_id, ms.section_name,spd.father_name\s
//                FROM student_personal_details spd
//                JOIN student_academic_details sad ON spd.student_id = sad.student_id
//                JOIN class_and_section cas ON sad.student_class_id = cas.class_id AND sad.student_section_id = cas.section_id
//                JOIN mst_class mc ON cas.class_id = mc.class_id
//                JOIN mst_section ms ON cas.section_id = ms.section_id
//                JOIN session s ON sad.session_id = s.session_id
//                WHERE spd.validity_end_date >= NOW()
//                  AND spd.deleted IS NOT TRUE
//                  AND cas.class_id = ?
//                  AND cas.section_id = ?
//                  AND sad.session_id = ?
//                ORDER BY spd.student_id ASC
//                """;
        String sql = """
                SELECT spd.student_id,\s
                                                    spd.first_name,\s
                                                    spd.last_name,\s
                                                    cas.class_id,\s
                                                    mc.class_name,\s
                                                    cas.section_id,\s
                                                    ms.section_name,\s
                                                    spd.father_name,
                                                    sad.roll_number
                                             FROM student_personal_details spd
                                             JOIN student_academic_details sad\s
                                                 ON spd.student_id = sad.student_id
                                             JOIN class_and_section cas\s
                                                 ON sad.student_class_id = cas.class_id\s
                                                AND sad.student_section_id = cas.section_id
                                             JOIN mst_class mc\s
                                                 ON cas.class_id = mc.class_id
                                             JOIN mst_section ms\s
                                                 ON cas.section_id = ms.section_id
                                             JOIN session s\s
                                                 ON sad.session_id = s.session_id
                                             WHERE spd.validity_end_date >= NOW()
                                               AND spd.deleted IS NOT TRUE
                                               AND cas.class_id = ?
                                               AND cas.section_id = ?
                                               AND sad.session_id = ?
                                             ORDER BY spd.student_id ASC;
                
                
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentDetails> studentDetails = null;
        try{
            studentDetails = jdbcTemplate.query(sql, new Object[]{studentClass, studentSection, sessionId}, new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails sd = new StudentDetails();
                    sd.setStudentId(rs.getInt("student_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setClassId(rs.getInt("class_id"));
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionId(rs.getInt("section_id"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setRollNumber(rs.getString("roll_number"));
                    sd.setFatherName(rs.getString("father_name"));
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }
    @Override
    public int getTotalStudent(String schoolCode) throws Exception {
        String sql = "SELECT COUNT(*) AS total_students FROM student_personal_details where validity_end_date >= NOW() AND deleted IS not true ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

   @Override
   public List<StudentDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception {
       String sql = "SELECT " +
               "spd.student_id, " +
               "spd.first_name, " +
               "spd.last_name, " +
               "spd.father_name, " +
               "spd.phone_number, " +
               "sad.registration_number, " +
               "mc.class_name, " +
               "ms.section_name " +
               "FROM " +
               "student_personal_details AS spd " +
               "INNER JOIN " +
               "student_academic_details AS sad ON spd.student_id = sad.student_id " +
               "INNER JOIN " +
               "mst_class AS mc ON sad.student_class_id = mc.class_id " +
               "INNER JOIN " +
               "mst_section AS ms ON sad.student_section_id = ms.section_id " +
               "INNER JOIN " +
               "session AS ses ON sad.session_id = ses.session_id " +
               "INNER JOIN " +
               "school_details AS sd ON spd.school_id  = sd.school_id " +
               "WHERE " +
               "spd.school_id = 1 " +
               "AND sad.school_id = 1 " +
               "AND spd.validity_end_date >= NOW() " +
               "AND sad.validity_end_date >= NOW() " +
               "AND spd.deleted is not true ";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<StudentDetails> studentDetails = null;
       try{
           studentDetails = jdbcTemplate.query(sql, new RowMapper<StudentDetails>() {
               @Override
               public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   StudentDetails sd = new StudentDetails();
                   sd.setStudentId(rs.getInt("student_id"));
                   sd.setFirstName(rs.getString("first_name"));
                   sd.setLastName(rs.getString("last_name"));
                   sd.setFatherName(rs.getString("father_name"));
                   sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                   sd.setRegistrationNumber(rs.getString("registration_number"));
                   sd.setClassName(rs.getString("class_name"));
                   sd.setSectionName(rs.getString("section_name"));
                   //create a combined field for all strings
                   String combinedData = sd.getStudentId() + " " + sd.getFirstName() + " " + sd.getLastName() + " " + sd.getFatherName() + " " + sd.getPhoneNumber() + " " + sd.getRegistrationNumber() + " " + sd.getClassName() + " " + sd.getSectionName();
                   // Convert to lowercase for case-insensitive matching
                   combinedData = combinedData.toLowerCase();
                   // Check if the combined string contains the search text
                   if(combinedData.contains(searchText.toLowerCase())){
                       return sd;
                   }else{
                       return null;
                   }
               }
           }).stream().filter(Objects::nonNull).collect(Collectors.toList());
       }catch(EmptyResultDataAccessException e){
           return null;
       }catch(Exception e){
           e.printStackTrace();
           throw e;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return studentDetails;
   }
    @Override
    public StudentDetails addStudentAcademicDetailsForExcel(StudentDetails studentDetails, String schoolCode) throws Exception {
        String sql = "insert into student_academic_details (student_id, school_id, registration_number, roll_number, session_id, student_class_id, student_section_id, session_status, session_status_comment, updated_by, updated_date, create_date, validity_start_date, validity_end_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,    studentDetails.getStudentId());
                ps.setInt(2,    studentDetails.getSchoolId());
                ps.setString(3, studentDetails.getRegistrationNumber());
                ps.setString(4, studentDetails.getRollNumber());
                ps.setInt(5, studentDetails.getSessionId());
                ps.setInt(6, studentDetails.getStudentClassId());
                ps.setInt(7, studentDetails.getStudentSectionId());
                ps.setString(8, studentDetails.getSessionStatus());
                ps.setString(9, studentDetails.getSessionStatusComment());
                ps.setInt(10, studentDetails.getUpdatedBy());
                ps.setDate(11, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(12, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                ps.setDate(13, new java.sql.Date(studentDetails.getValidityStartDate().getTime()));
                ps.setDate(14, new java.sql.Date(studentDetails.getValidityEndDate().getTime()));
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                int generatedId = ((Number) keys.get("id")).intValue();
                studentDetails.setId(generatedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }

    @Override
    public List<StudentDetails> getStudentDetailsByParentId(int parentId, String schoolCode) throws Exception {
        String sql = """
                SELECT spd.student_id, spd.school_id, spd.parent_id, spd.father_name, spd.father_occupation, spd.mother_name, spd.mother_occupation,
                    spd.first_name, spd.last_name, spd.blood_group, spd.gender, spd.height, spd.weight, spd.aadhar_number, spd.phone_number, spd.emergency_phone_number, spd.whatsapp_no,
                    spd.email_address, spd.dob, spd.dob_cirtificate_no, spd.income_app_no, spd.caste_app_no, spd.domicile_app_no, spd.govt_student_id_on_portal, spd.govt_family_id_on_portal,
                    spd.bank_name, spd.branch_name, spd.ifsc_code, spd.account_no, spd.pan_no, spd.religion, spd.nationality, spd.category, spd.caste, spd.current_address, spd.current_city,
                    spd.current_state, spd.current_zipcode, spd.permanent_address, spd.permanent_city, spd.permanent_state, spd.permanent_zipcode, spd.student_country, spd.current_status, spd.current_status_comment,
                    spd.updated_by, spd.updated_date, spd.create_date, spd.validity_start_date, spd.validity_end_date,
                    sad.apaar_id, sad.pen_no, sad.admission_no, sad.admission_date, sad.registration_number, sad.roll_number, sad.session_id, sad.student_class_id, sad.student_section_id, sad.stream, sad.education_medium,
                    sad.referred_by, sad.is_rte_student, sad.rte_application_no, sad.enrolled_session, sad.enrolled_class, sad.enrolled_year, sad.transfer_cirti_no,
                    sad.date_of_issue, sad.scholarship_id, sad.scholarship_password, sad.lst_school_name, sad.lst_school_addrs, sad.lst_attended_class, sad.lst_scl_aff_to, 
                    sad.lst_session, sad.is_dropout, sad.dropout_date, sad.dropout_reason, sad.student_addmission_type, sad.session_status, sad.session_status_comment, sad.previous_qualifications,
                    mc.class_name, ms.section_name, ses.academic_session
                FROM
                    student_personal_details spd
                JOIN
                    student_academic_details sad ON spd.student_id = sad.student_id
                JOIN
                    mst_class mc ON sad.student_class_id = mc.class_id
                JOIN
                    mst_section ms ON sad.student_section_id = ms.section_id
                JOIN
                    session ses  ON sad.session_id = ses.session_id
                WHERE
                    spd.parent_id @> ARRAY[?] 
                    AND spd.deleted is not true
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentDetails> studentDetails = null;
        try{
            studentDetails = jdbcTemplate.query(sql, new Object[]{parentId}, new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails sd = new StudentDetails();
                    sd.setStudentId(rs.getInt("student_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    //Handle the parent ids
                    Array parentIdArray = rs.getArray("parent_id");
                    if(parentIdArray != null) {
                        Integer[] parentIds = (Integer[]) parentIdArray.getArray();
                        sd.setParentId(Arrays.asList(parentIds));
                    }
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setFatherOccupation(rs.getString("father_occupation"));
                    sd.setMotherName(rs.getString("mother_name"));
                    sd.setMotherOccupation(rs.getString("mother_occupation"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setHeight(rs.getString("height"));
                    sd.setWeight(rs.getString("weight"));
                    sd.setAadharNumber(CipherUtils.decrypt(rs.getString("aadhar_number")));
                    sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    sd.setEmergencyPhoneNumber(CipherUtils.decrypt(rs.getString("emergency_phone_number")));
                    sd.setWhatsAppNumber(CipherUtils.decrypt(rs.getString("whatsapp_no")));
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setDobCirtificateNo(rs.getString("dob_cirtificate_no"));
                    sd.setIncomeAppNo(rs.getString("income_app_no"));
                    sd.setCasteAppNo(rs.getString("caste_app_no"));
                    sd.setDomicileAppNo(rs.getString("domicile_app_no"));
                    sd.setGovtStudentIdOnPortal(rs.getString("govt_student_id_on_portal"));
                    sd.setGovtFamilyIdOnPortal(rs.getString("govt_family_id_on_portal"));
                    sd.setBankName(rs.getString("bank_name"));
                    sd.setBranchName(rs.getString("branch_name"));
                    sd.setIfscCode(rs.getString("ifsc_code"));
                    sd.setAccountNumber(rs.getString("account_no"));
                    sd.setPanNo(rs.getString("pan_no"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setNationality(rs.getString("nationality"));
                    sd.setCategory(rs.getString("category"));
                    sd.setCaste(rs.getString("caste"));
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setStudentCountry(rs.getString("student_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setUpdatedBy(rs.getInt("updated_by"));
                    sd.setUpdatedDate(rs.getDate("updated_date"));
                    sd.setCreateDate(rs.getDate("create_date"));
                    sd.setValidityStartDate(rs.getDate("validity_start_date"));
                    sd.setValidityEndDate(rs.getDate("validity_end_date"));
                    // student academic details
                    sd.setApaarId(rs.getString("apaar_id"));
                    sd.setPenNo(rs.getString("pen_no"));
                    sd.setAdmissionNo(rs.getString("admission_no"));
                    sd.setAdmissionDate(rs.getDate("admission_date"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setRollNumber(rs.getString("roll_number"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setStudentClassId(rs.getInt("student_class_id"));
                    sd.setStudentSectionId(rs.getInt("student_section_id"));
                    sd.setStream(rs.getString("stream"));
                    sd.setEducationMedium(rs.getString("education_medium"));
                    sd.setReferredBy(rs.getString("referred_by"));
                    sd.setRteStudent(rs.getBoolean("is_rte_student"));
                    sd.setRteApplicationNo(rs.getString("rte_application_no"));
                    sd.setEnrolledSession(rs.getString("enrolled_session"));
                    sd.setEnrolledClass(rs.getString("enrolled_class"));
                    sd.setEnrolledYear(rs.getString("enrolled_year"));
                    sd.setTransferCirtiNo(rs.getString("transfer_cirti_no"));
                    sd.setDateOfIssue(rs.getDate("date_of_issue"));
                    sd.setScholarshipId(rs.getString("scholarship_id"));
                    sd.setScholarshipPassword(CipherUtils.decrypt(rs.getString("scholarship_password")));
                    sd.setLstSchoolName(rs.getString("lst_school_name"));
                    sd.setLstSchoolAddress(rs.getString("lst_school_addrs"));
                    sd.setLstAttendedClass(rs.getString("lst_attended_class"));
                    sd.setLstSclAffTo(rs.getString("lst_scl_aff_to"));
                    sd.setLstSession(rs.getString("lst_session"));
                    sd.setDropOut(rs.getBoolean("is_dropout"));
                    sd.setDropOutDate(rs.getDate("dropout_date"));
                    sd.setDropOutReason(rs.getString("dropout_reason"));
                    sd.setStudentAdmissionType(rs.getString("student_addmission_type"));
                    sd.setSessionStatus(rs.getString("session_status"));
                    sd.setSessionStatusComment(rs.getString("session_status_comment"));
                    // Mapping previous qualification details
                    String previousQualifications = rs.getString("previous_qualifications");
                    if(previousQualifications != null){
                        try{
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<PreviousQualificationDetails> qualifications = objectMapper.readValue(previousQualifications, new TypeReference<List<PreviousQualificationDetails>>() {});
                            sd.setPreviousQualificationDetails(qualifications);
                        } catch (JsonProcessingException e){
                            e.printStackTrace();
                            sd.setPreviousQualificationDetails(null);
                        }
                    } else{
                        sd.setPreviousQualificationDetails(null);
                    }
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setAcademicSession(rs.getString("academic_session"));
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }
/* i done changes here  */
    /*@Override
    public boolean isEligibleForTC(Long studentId, String schoolCode) {
        try {
            String sql = "SELECT COUNT(*) FROM student_academic_details sa JOIN student_personal_details sp ON sa.student_id = sp.student_id WHERE (sa.session_status IN ('inactive', 'graduate') )OR sa.validity_end_date <= CURRENT_DATE  AND sa.student_id = ?";
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
            System.out.println("count"+count);
            System.out.println(sql);
            return count != null && count > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }*/

    @Override
    public boolean isEligibleForTC(Long studentId, String schoolCode) {
        JdbcTemplate jdbcTemplate = null;
        try {
            // 1. Fixed SQL query with proper parentheses
            String sql = "SELECT COUNT(*) FROM student_academic_details sa "
                    + "JOIN student_personal_details sp ON sa.student_id = sp.student_id "
                    + "WHERE (sa.session_status IN ('inactive', 'graduate') "
                    + "OR sa.validity_end_date <= CURRENT_DATE) "
                    + "AND sa.student_id = ?";  // Added missing parenthesi
            jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
            int count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
            logger.debug("TC eligibility check count: {} for student: {}", count, studentId);

            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            logger.error("No student found with ID: {}", studentId);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error checking TC eligibility for student: {}", studentId, e);
            throw new ServiceException("Error checking TC eligibility", e);
        } finally {
            if(jdbcTemplate != null) {
                // 5. Ensure proper connection closing mechanism
                DatabaseUtil.closeDataSource(jdbcTemplate);
            }
        }
    }

    @Override
    public StudentDetails getStudentDetailsForTc(int studentId, int sessionId,String schoolCode) throws Exception {
        String sql = """
                SELECT
                    spd.id AS student_personal_id,
                    spd.student_id,
                    spd.school_id AS personal_school_id,
                    spd.uu_id AS personal_uu_id,
                    spd.first_name,
                    spd.last_name,
                    spd.blood_group,
                    spd.gender,
                    spd.height,
                    spd.weight,
                    spd.aadhar_number,
                    spd.phone_number,
                    spd.emergency_phone_number,
                    spd.whatsapp_no,
                    spd.email_address,
                    spd.dob,
                    spd.dob_cirtificate_no,
                    spd.income_app_no,
                    spd.caste_app_no,
                    spd.domicile_app_no,
                    spd.govt_student_id_on_portal,
                    spd.govt_family_id_on_portal,
                    spd.bank_name,
                    spd.branch_name,
                    spd.ifsc_code,
                    spd.account_no,
                    spd.pan_no,
                    spd.religion,
                    spd.nationality,
                    spd.category,
                    spd.caste,
                    spd.current_address,
                    spd.current_city,
                    spd.current_state,
                    spd.current_zipcode,
                    spd.permanent_address,
                    spd.permanent_city,
                    spd.permanent_state,
                    spd.permanent_zipcode,
                    spd.student_country,
                    spd.current_status,
                    spd.current_status_comment,
                    spd.updated_by AS personal_updated_by,
                    spd.updated_date AS personal_updated_date,
                    spd.create_date AS personal_create_date,
                    spd.validity_start_date AS personal_validity_start_date,
                    spd.validity_end_date AS personal_validity_end_date,
                    spd.student_photo,
                    sad.apaar_id,
                    sad.pen_no,
                    sad.admission_no,
                    sad.admission_date,
                    sad.registration_number,
                    sad.roll_number,
                    sad.session_id,
                    sad.student_class_id,
                    sad.student_section_id,
                    sad.stream,
                    sad.education_medium,
                    sad.referred_by,
                    sad.is_rte_student,
                    sad.rte_application_no,
                    sad.enrolled_session,
                    sad.enrolled_class,
                    sad.enrolled_year,
                    sad.transfer_cirti_no,
                    sad.date_of_issue,
                    sad.scholarship_id,
                    sad.scholarship_password,
                    sad.lst_school_name,
                    sad.lst_school_addrs,
                    sad.lst_attended_class,
                    sad.lst_scl_aff_to,
                    sad.lst_session,
                    sad.is_dropout,
                    sad.dropout_date,
                    sad.dropout_reason,
                    sad.student_addmission_type,
                    sad.session_status,
                    sad.session_status_comment,
                    sad.previous_qualifications::TEXT AS previous_qualifications,
                    mc.class_id AS class_id,
                    mc.class_name,
                    ms.section_id AS section_id,
                    ms.section_name,
                    ses.academic_session,
                    sd.school_name,
                    sd.school_address,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.school_zipcode,
                    sd.email_address AS school_email_address,
                    sd.phone_number AS school_phone_number,
                    COALESCE(
                        JSON_AGG(
                            JSON_BUILD_OBJECT(
                                'parentId', pd.parent_id,
                                'firstName', pd.first_name,
                                'lastName', pd.last_name,
                                'dob', pd.dob,
                                'phoneNumber', pd.phone_number,
                                'emergencyPhoneNumber', pd.emergency_phone_number,
                                'whatsappNumber', pd.whatsapp_no,
                                'email', pd.email_address,
                                'gender', pd.gender,
                                'parentType', pd.parent_type,
                                'qualification', pd.qualification,
                                'aadharNumber', pd.aadhar_number,
                                'companyName', pd.company_name,
                                'designation', pd.designation,
                                'companyAddress', pd.company_address,
                                'companyPhone', pd.company_phone,
                                'address', pd.address,
                                'city', pd.city,
                                'state', pd.state,
                                'zipcode', pd.zipcode
                            )
                        ) FILTER (WHERE pd.parent_id IS NOT NULL AND pd.deleted IS NOT TRUE), '[]'
                    ) AS parent_details
                FROM
                    student_personal_details AS spd
                INNER JOIN
                    student_academic_details AS sad ON spd.student_id = sad.student_id
                INNER JOIN
                    mst_class AS mc ON sad.student_class_id = mc.class_id
                INNER JOIN
                    mst_section AS ms ON sad.student_section_id = ms.section_id
                INNER JOIN
                    session AS ses ON sad.session_id = ses.session_id
                INNER JOIN
                    school_details AS sd ON spd.school_id = sd.school_id
                LEFT JOIN
                    parent_details AS pd ON pd.parent_id = ANY(spd.parent_id)
                WHERE
                    spd.student_id = ?
                    AND spd.school_id = 1
                    AND sad.school_id = 1
                    AND sad.session_id=?
                GROUP BY
                    spd.id, spd.student_id, spd.school_id, spd.uu_id,
                    spd.first_name, spd.last_name, spd.blood_group, spd.gender,
                    spd.height, spd.weight, spd.aadhar_number, spd.phone_number, spd.emergency_phone_number, spd.whatsapp_no,
                    spd.email_address, spd.dob, spd.dob_cirtificate_no, spd.income_app_no, spd.caste_app_no, spd.domicile_app_no,
                    spd.govt_student_id_on_portal, spd.govt_family_id_on_portal, spd.bank_name, spd.branch_name, spd.ifsc_code,
                    spd.account_no, spd.pan_no, spd.religion, spd.nationality, spd.category, spd.caste, spd.current_address,
                    spd.current_city, spd.current_state, spd.current_zipcode, spd.permanent_address, spd.permanent_city,
                    spd.permanent_state, spd.permanent_zipcode, spd.student_country, spd.current_status, spd.current_status_comment,
                    spd.updated_by, spd.updated_date, spd.create_date, spd.validity_start_date, spd.validity_end_date,
                    spd.student_photo, sad.apaar_id, sad.pen_no, sad.admission_no,
                    sad.admission_date, sad.registration_number, sad.roll_number, sad.session_id, sad.student_class_id,
                    sad.student_section_id, sad.stream, sad.education_medium, sad.referred_by, sad.is_rte_student, sad.rte_application_no,
                    sad.enrolled_session, sad.enrolled_class, sad.enrolled_year, sad.transfer_cirti_no, sad.date_of_issue,
                    sad.scholarship_id, sad.scholarship_password, sad.lst_school_name, sad.lst_school_addrs, sad.lst_attended_class,
                    sad.lst_scl_aff_to, sad.lst_session, sad.is_dropout, sad.dropout_date, sad.dropout_reason, sad.student_addmission_type,
                    sad.session_status, sad.session_status_comment, sad.previous_qualifications, mc.class_id, mc.class_name,
                    ms.section_id, ms.section_name, ses.academic_session, sd.school_name, sd.school_address, sd.school_city, sd.school_state,
                    sd.school_country, sd.school_zipcode, sd.email_address, sd.phone_number            
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        StudentDetails studentDetails = null;
        try{
            studentDetails =  jdbcTemplate.queryForObject(sql, new Object[]{studentId,sessionId}, new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails sd = new StudentDetails();
                    sd.setId(rs.getInt("student_personal_id"));
                    sd.setStudentId(rs.getInt("student_id"));
                    sd.setSchoolId(rs.getInt("personal_school_id"));
                    sd.setUuId(rs.getString("personal_uu_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setHeight(rs.getString("height"));
                    sd.setWeight(rs.getString("weight"));
                    sd.setAadharNumber(rs.getString("aadhar_number") != null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    sd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setWhatsAppNumber(rs.getString("whatsapp_no") != null ? CipherUtils.decrypt(rs.getString("whatsapp_no")) : null);
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setDobCirtificateNo(rs.getString("dob_cirtificate_no"));
                    sd.setIncomeAppNo(rs.getString("income_app_no"));
                    sd.setCasteAppNo(rs.getString("caste_app_no"));
                    sd.setDomicileAppNo(rs.getString("domicile_app_no"));
                    sd.setGovtStudentIdOnPortal(rs.getString("govt_student_id_on_portal"));
                    sd.setGovtFamilyIdOnPortal(rs.getString("govt_family_id_on_portal"));
                    sd.setBankName(rs.getString("bank_name"));
                    sd.setBranchName(rs.getString("branch_name"));
                    sd.setIfscCode(rs.getString("ifsc_code"));
                    sd.setAccountNumber(rs.getString("account_no"));
                    sd.setPanNo(rs.getString("pan_no"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setNationality(rs.getString("nationality"));
                    sd.setCategory(rs.getString("category"));
                    sd.setCaste(rs.getString("caste"));
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setStudentCountry(rs.getString("student_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setUpdatedBy(rs.getInt("personal_updated_by"));
                    sd.setUpdatedDate(rs.getDate("personal_updated_date"));
                    sd.setCreateDate(rs.getDate("personal_create_date"));
                    sd.setValidityStartDate(rs.getDate("personal_validity_start_date"));
                    sd.setValidityEndDate(rs.getDate("personal_validity_end_date"));
                    sd.setStudentPhoto(rs.getString("student_photo"));
                    // student academic details
                    sd.setApaarId(rs.getString("apaar_id"));
                    sd.setPenNo(rs.getString("pen_no"));
                    sd.setAdmissionNo(rs.getString("admission_no"));
                    sd.setAdmissionDate(rs.getDate("admission_date"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setRollNumber(rs.getString("roll_number"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setStudentClassId(rs.getInt("student_class_id"));
                    sd.setStudentSectionId(rs.getInt("student_section_id"));
                    sd.setStream(rs.getString("stream"));
                    sd.setEducationMedium(rs.getString("education_medium"));
                    sd.setReferredBy(rs.getString("referred_by"));
                    sd.setRteStudent(rs.getBoolean("is_rte_student"));
                    sd.setRteApplicationNo(rs.getString("rte_application_no"));
                    sd.setEnrolledSession(rs.getString("enrolled_session"));
                    sd.setEnrolledClass(rs.getString("enrolled_class"));
                    sd.setEnrolledYear(rs.getString("enrolled_year"));
                    sd.setTransferCirtiNo(rs.getString("transfer_cirti_no"));
                    sd.setDateOfIssue(rs.getDate("date_of_issue"));
                    sd.setScholarshipId(rs.getString("scholarship_id"));
                    sd.setScholarshipPassword(rs.getString("scholarship_password") != null ? CipherUtils.decrypt(rs.getString("scholarship_password")) : null);
                    sd.setLstSchoolName(rs.getString("lst_school_name"));
                    sd.setLstSchoolAddress(rs.getString("lst_school_addrs"));
                    sd.setLstAttendedClass(rs.getString("lst_attended_class"));
                    sd.setLstSclAffTo(rs.getString("lst_scl_aff_to"));
                    sd.setLstSession(rs.getString("lst_session"));
                    sd.setDropOut(rs.getBoolean("is_dropout"));
                    sd.setDropOutDate(rs.getDate("dropout_date"));
                    sd.setDropOutReason(rs.getString("dropout_reason"));
                    sd.setStudentAdmissionType(rs.getString("student_addmission_type"));
                    sd.setSessionStatus(rs.getString("session_status"));
                    sd.setSessionStatusComment(rs.getString("session_status_comment"));
                    // Mapping previous qualification details
                    String previousQualifications = rs.getString("previous_qualifications");
                    if (previousQualifications != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<PreviousQualificationDetails> qualifications = objectMapper.readValue(previousQualifications, new TypeReference<List<PreviousQualificationDetails>>() {});
                            sd.setPreviousQualificationDetails(qualifications);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sd.setPreviousQualificationDetails(null);
                        }
                    } else {
                        sd.setPreviousQualificationDetails(null);
                    }
                    sd.setClassId(rs.getInt("class_id"));
                    sd.setClassName(rs.getString("class_name"));
                    sd.setSectionId(rs.getInt("section_id"));
                    sd.setSectionName(rs.getString("section_name"));
                    sd.setAcademicSession(rs.getString("academic_session"));
                    sd.setSchoolName(rs.getString("school_name"));
                    sd.setSchoolAddress(rs.getString("school_address"));
                    sd.setSchoolCity(rs.getString("school_city"));
                    sd.setSchoolState(rs.getString("school_state"));
                    sd.setSchoolCountry(rs.getString("school_country"));
                    sd.setSchoolZipCode(rs.getInt("school_zipcode"));
                    sd.setSchoolEmailAddress(rs.getString("school_email_address"));
                    sd.setSchoolPhoneNumber(rs.getString("school_phone_number") != null ? CipherUtils.decrypt(rs.getString("school_phone_number")) : null);
                    // Mapping parent_details
                    String parentDetailsJson = rs.getString("parent_details");

                    if (parentDetailsJson != null) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ParentDetails> parentDetailsList = objectMapper.readValue(parentDetailsJson, new TypeReference<List<ParentDetails>>() {});

                            // Decrypt sensitive fields
                            for (ParentDetails parent : parentDetailsList) {
                                if (parent.getPhoneNumber() != null) {
                                    parent.setPhoneNumber(CipherUtils.decrypt(parent.getPhoneNumber()));
                                }
                                if (parent.getWhatsappNumber() != null) {
                                    parent.setWhatsappNumber(CipherUtils.decrypt(parent.getWhatsappNumber()));
                                }
                                if (parent.getAadharNumber() != null) {
                                    parent.setAadharNumber(CipherUtils.decrypt(parent.getAadharNumber()));
                                }
                                if (parent.getEmergencyPhoneNumber() != null) {
                                    parent.setEmergencyPhoneNumber(CipherUtils.decrypt(parent.getEmergencyPhoneNumber()));
                                }
                                if(parent.getCompanyPhone() != null) {
                                    parent.setCompanyPhone(CipherUtils.decrypt(parent.getCompanyPhone()));
                                }
                            }

                            sd.setParentDetails(parentDetailsList);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            sd.setParentDetails(null);
                        }
                    } else {
                        // If the column is null, set parentDetails to null
                        sd.setParentDetails(null);
                    }
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentDetails;
    }

    @Override
    public Map<Integer, StudentDetails> getStudentImagesBatch(String schoolCode, List<Integer> studentIds) {
        Map<Integer, StudentDetails> imageMap = new HashMap<>();

        for (Integer studentId : studentIds) {
            String fileName = studentId + ".png";
            String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                continue; // Skip missing files silently
            }

            try {
                byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                StudentDetails studentDetails = new StudentDetails();
                studentDetails.setStudentImage(base64Image);
                imageMap.put(studentId, studentDetails);

            } catch (IOException e) {
                // Log and continue processing other images
                logger.warn("Error processing image for student {}: {}", studentId, e.getMessage());
            }
        }

        return imageMap;
    }

    @Override
    public List<StudentDetails> getBirthday(String schoolCode) throws Exception {
        String sql= """
                WITH student_next_birthday AS (
                    SELECT
                        *,
                        CASE
                            WHEN (date_trunc('year', CURRENT_DATE) + (dob - date_trunc('year', dob))) >= CURRENT_DATE
                            THEN (date_trunc('year', CURRENT_DATE) + (dob - date_trunc('year', dob)))::DATE
                            ELSE (date_trunc('year', CURRENT_DATE) + (dob - date_trunc('year', dob)) + INTERVAL '1 year')::DATE
                        END AS next_birthday
                    FROM
                        student_personal_details
                    WHERE
                        deleted IS NOT TRUE
                )
                (
                    SELECT\s
                        spd.student_id,
                        spd.first_name,
                        spd.last_name,
                        mc.class_name AS class,
                        ms.section_name AS section,
                        spd.next_birthday AS date,
                        'today_birthday' AS event_type
                    FROM
                        student_next_birthday spd
                    JOIN
                        student_academic_details sad\s
                        ON spd.student_id = sad.student_id\s
                        AND spd.school_id = sad.school_id
                    JOIN
                        mst_class mc\s
                        ON sad.student_class_id = mc.class_id\s
                        AND sad.school_id = mc.school_id
                    JOIN
                        mst_section ms\s
                        ON sad.student_section_id = ms.section_id\s
                        AND sad.school_id = ms.school_id
                    WHERE
                        spd.next_birthday = CURRENT_DATE
                )
                UNION ALL
                (
                    SELECT\s
                      spd.student_id,
                        spd.first_name,
                        spd.last_name,
                        mc.class_name AS class,
                        ms.section_name AS section,
                        spd.next_birthday AS date,
                        'upcoming_birthday' AS event_type
                    FROM
                        student_next_birthday spd
                    JOIN
                        student_academic_details sad\s
                        ON spd.student_id = sad.student_id\s
                        AND spd.school_id = sad.school_id
                    JOIN
                        mst_class mc\s
                        ON sad.student_class_id = mc.class_id\s
                        AND sad.school_id = mc.school_id
                    JOIN
                        mst_section ms\s
                        ON sad.student_section_id = ms.section_id\s
                        AND sad.school_id = ms.school_id
                    WHERE
                        spd.next_birthday > CURRENT_DATE
                    ORDER BY
                        spd.next_birthday
                    LIMIT 5
                )
                ORDER BY\s
                    event_type DESC, \s
                    date;  \s
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentDetails> birthdays = null;
        try {
            birthdays = jdbcTemplate.query(sql, new RowMapper<StudentDetails>() {
                @Override
                public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentDetails birth = new StudentDetails();
                    birth.setStudentId(rs.getInt("student_id"));
                    birth.setFirstName(rs.getString("first_name"));
                    birth.setLastName(rs.getString("last_name"));
                    birth.setClassName(rs.getString("class"));
                    birth.setSectionName(rs.getString("section"));
                    birth.setDob(rs.getDate("date"));
                    birth.setEventType(rs.getString("event_type"));
                    return birth;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return birthdays;
    }
    @Override
    public List<StudentDetails> globalSearch(String firstName, String lastName, String FatherName, String AdmissionNumber, String phoneNumber, String rollNumber, int sessionId, String schoolCode) throws Exception {
        String sql = """
        SELECT
            spd.student_id,
            sad.session_id,
            mc.class_id,
            ms.section_id,
            spd.first_name,
            spd.last_name,
            spd.father_name,
            sad.admission_no,
            sad.roll_number,
            spd.phone_number,
            mc.class_name,
            ms.section_name
        FROM student_personal_details spd
        INNER JOIN student_academic_details sad 
            ON spd.student_id = sad.student_id
            AND spd.school_id = sad.school_id
        INNER JOIN mst_class mc 
            ON sad.student_class_id = mc.class_id
        INNER JOIN mst_section ms 
            ON sad.student_section_id = ms.section_id
        WHERE sad.session_id = ?
            AND spd.deleted IS NOT TRUE
            AND (
                (spd.first_name ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
                OR (spd.last_name ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
                OR (spd.father_name ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
                OR (sad.admission_no::TEXT ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
                OR (spd.phone_number::TEXT ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
                OR (sad.roll_number::TEXT ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
                OR (spd.first_name || ' ' || spd.last_name ILIKE '%' || ?::TEXT || '%' AND ?::TEXT IS NOT NULL)
            )
        """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(
                    sql,
                    new Object[] {
                            sessionId,
                            firstName, firstName,
                            lastName, lastName,
                            FatherName, FatherName,
                            AdmissionNumber, AdmissionNumber,
                            phoneNumber, phoneNumber,
                            rollNumber, rollNumber,
                            firstName, firstName  // Passed twice for the new combined name condition
                    },
                    new RowMapper<StudentDetails>() {
                        @Override
                        public StudentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                            StudentDetails details = new StudentDetails();
                            details.setStudentId(rs.getInt("student_id"));
                            details.setSessionId(rs.getInt("session_id"));
                            details.setStudentClassId(rs.getInt("class_id"));
                            details.setStudentSectionId(rs.getInt("section_id"));
                            details.setFirstName(rs.getString("first_name"));
                            details.setLastName(rs.getString("last_name"));
                            details.setFatherName(rs.getString("father_name"));
                            details.setAdmissionNo(rs.getString("admission_no"));
                            details.setRollNumber(rs.getString("roll_number"));
                            details.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                            details.setClassName(rs.getString("class_name"));
                            details.setSectionName(rs.getString("section_name"));
                            return details;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
