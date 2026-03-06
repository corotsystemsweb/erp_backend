package com.sms.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.StudentIdCardDao;
import com.sms.model.ParentDetails;
import com.sms.model.PreviousQualificationDetails;
import com.sms.model.StudentDetails;
import com.sms.model.StudentIdCardDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Repository
public class StudentIdCardDaoImpl implements StudentIdCardDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentIdCardDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Value("${student.img.local.path}")
    private String FOLDER_PATH;
    @Override
    public List<StudentIdCardDetails> getAllStudentImage(String schoolCode, int studentId) throws Exception {
        String fileName = studentId + ".png";
        String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        File imageFile = new File(imagePath);
        if(!imageFile.exists()){
            //throw new IOException("File not found: " + imagePath);
            return null;
        }
        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String base64Images = Base64.getEncoder().encodeToString(imageBytes);

            StudentIdCardDetails studentIdCardDetails = new StudentIdCardDetails();
            studentIdCardDetails.setStudentImageUrl(base64Images);
            //get list of students url
            List<StudentIdCardDetails> studentIdCardDetailsList = new ArrayList<>();
            studentIdCardDetailsList.add(studentIdCardDetails);
            return studentIdCardDetailsList;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<StudentIdCardDetails> searchByClassSectionAndSession(int studentClassId, int studentSectionId, int sessionId, String schoolCode) throws Exception {
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
                                     sad.student_class_id = ?
                                     AND sad.student_section_id = ?
                                     AND sad.session_id = ?
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
                                    sad.session_status, sad.session_status_comment, sad.previous_qualifications, mc.class_id, mc.class_name,
                                    ms.section_id, ms.section_name, ses.academic_session, sd.school_name, sd.school_address, sd.school_city, sd.school_state,
                                    sd.school_country, sd.school_zipcode, sd.email_address, sd.phone_number
                                ORDER BY mc.class_id ASC, ms.section_id ASC   
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentIdCardDetails> studentIdCardDetails = null;
        try{
            studentIdCardDetails =  jdbcTemplate.query(sql, new Object[]{studentClassId, studentSectionId, sessionId}, new RowMapper<StudentIdCardDetails>() {
                @Override
                public StudentIdCardDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentIdCardDetails sd = new StudentIdCardDetails();
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
        return studentIdCardDetails;
    }
}
