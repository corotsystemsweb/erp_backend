package com.sms.dao.impl;

import com.sms.dao.StaffDao;
import com.sms.exception.ImageSizeLimitExceededException;
import com.sms.model.HrPayroleDetails;
import com.sms.model.StaffDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Repository
public class StaffDaoImpl implements StaffDao {
    @Value("${staff.img.local.path}")
    private String FOLDER_PATH;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public StaffDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   /* @Override
    public boolean addImage(MultipartFile file, int staffId) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtention = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String photoPath = FOLDER_PATH + staffId + "." + "png";
        file.transferTo(new File(photoPath));
        return true;
    }
    @Override
    public StaffDetails getImage(int staffId) throws Exception {
        String fileName  = staffId + ".png";
        String imagePath = FOLDER_PATH + fileName;
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        StaffDetails staffDetails=new StaffDetails();
        staffDetails.setStaffImage(base64Image);
        return staffDetails;
    }*/

    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int staffId) throws Exception {
        // Check the file size (in bytes)
        long maxSize = 200 * 1024; // 200KB in bytes
        if (file.getSize() > maxSize) {
            throw new ImageSizeLimitExceededException("File size exceeds the maximum limit of 200KB");
        }
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") +1);
        String photoPath = FOLDER_PATH + schoolCode + File.separator + staffId + "." + "png";
        File directory = new File(FOLDER_PATH + schoolCode);
        if(!directory.exists()){
            directory.mkdirs();
        }
        file.transferTo(new File(photoPath));
        return true;
    }

    @Override
    public StaffDetails getImage(String schoolCode, int staffId) throws Exception {
        String fileName = staffId + ".png";
        String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        //check if the imagePath is exists
        File imageFile = new File(imagePath);
        if(!imageFile.exists()){
            throw new IOException("File not found: " + imagePath);
        }
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        StaffDetails sd = new StaffDetails();
        sd.setStaffImage(base64Image);
        return sd;
    }

    @Override
    public StaffDetails addStaffOneBYOne(StaffDetails staffDetails, String schoolCode) {
        // Encrypt sensitive information
        staffDetails.setAadharNumber(CipherUtils.encrypt(staffDetails.getAadharNumber()));
        staffDetails.setPhoneNumber(CipherUtils.encrypt(staffDetails.getPhoneNumber()));

        String sql = "insert into staff (school_id, session_id, first_name, last_name, registration_number, joining_date, department_id, designation_id, employment_type, father_name, blood_group, gender, aadhar_number, highest_qualification, pf_account_no, experience, experience_details, phone_number, email_address, dob, religion, emergency_phone_number, emergency_contact_name, current_address, current_zipcode, current_city, current_state, permanent_address, permanent_zipcode, permanent_city, permanent_state, staff_country, current_status, current_status_comment, staff_photo) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, 1);
                ps.setInt(2,staffDetails.getSessionId());
                ps.setString(3, staffDetails.getFirstName());
                ps.setString(4, staffDetails.getLastName());
                ps.setString(5, staffDetails.getRegistrationNumber());
                ps.setDate(6, new java.sql.Date(staffDetails.getJoiningDate().getTime()));
                ps.setInt(7, staffDetails.getDepartmentId());
                ps.setInt(8, staffDetails.getDesignationId());
                ps.setString(9, staffDetails.getEmploymentType());
                ps.setString(10, staffDetails.getFatherName());
                ps.setString(11, staffDetails.getBloodGroup());
                ps.setString(12, staffDetails.getGender());
                ps.setString(13, staffDetails.getAadharNumber());
                ps.setString(14, staffDetails.getHighestQualification());
                ps.setString(15, staffDetails.getPfAccountNo());
                ps.setString(16, staffDetails.getExperience());
                ps.setString(17, staffDetails.getExperienceDetails());
                ps.setString(18, staffDetails.getPhoneNumber());
                ps.setString(19, staffDetails.getEmailAddress());
                ps.setDate(20, staffDetails.getDob() != null ? new java.sql.Date(staffDetails.getDob().getTime()) : null);
                ps.setString(21, staffDetails.getReligion());
                ps.setString(22, CipherUtils.encrypt(staffDetails.getEmergencyPhoneNumber()));
                ps.setString(23, staffDetails.getEmergencyContactName());
                ps.setString(24, staffDetails.getCurrentAddress());
                ps.setInt(25, staffDetails.getCurrentZipCode());
                ps.setString(26, staffDetails.getCurrentCity());
                ps.setString(27, staffDetails.getCurrentState());
                ps.setString(28, staffDetails.getPermanentAddress());
                ps.setInt(29, staffDetails.getPermanentZipCode());
                ps.setString(30, staffDetails.getPermanentCity());
                ps.setString(31, staffDetails.getPermanentState());
                ps.setString(32, staffDetails.getStaffCountry());
                ps.setString(33, staffDetails.getCurrentStatus());
                ps.setString(34, staffDetails.getCurrentStatusComment());
                ps.setString(35, staffDetails.getStaffPhoto());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("staff_id")){
                int generatedId = ((Number)keys.get("staff_id")).intValue();
                staffDetails.setStaffId(generatedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or handle the exception as appropriate
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return staffDetails;
    }

    @Override
    public StaffDetails getStaffDetailsById(int staffId, String schoolCode) {
        String sql = """
                SELECT\s
                    s.staff_id,
                    s.session_id,
                    s.first_name,
                    s.last_name,
                    s.registration_number,
                    s.joining_date,
                    s.department_id,
                    dp.department AS department_name,
                    d.designation AS designation_name,
                    s.designation_id,
                    s.employment_type,
                    s.father_name,
                    s.blood_group,
                    s.gender,
                    s.aadhar_number,
                    s.highest_qualification,
                    s.pf_account_no,
                    s.experience,
                    s.experience_details,
                    s.phone_number,
                    s.email_address,
                    s.dob,
                    s.religion,
                    s.emergency_phone_number,
                    s.emergency_contact_name,
                    s.current_address,
                    s.current_zipcode,
                    s.current_city,
                    s.current_state,
                    s.permanent_address,
                    s.permanent_zipcode,
                    s.permanent_city,
                    s.permanent_state,
                    s.staff_country,
                    s.current_status,
                    s.current_status_comment,
                    s.staff_photo,
                    ss.ss_id,
                    ss.salary_amount
                FROM staff s
                LEFT JOIN staff_designation d\s
                       ON s.designation_id = d.sd_id
                LEFT JOIN staff_department dp\s
                       ON s.department_id = dp.stdp_id
                LEFT JOIN staff_salary ss\s
                       ON ss.staff_id = s.staff_id
                       AND COALESCE(ss.deleted,false) IS NOT TRUE
                       AND ss.ss_id = (
                            SELECT MAX(ss2.ss_id)
                            FROM staff_salary ss2
                            WHERE ss2.staff_id = s.staff_id
                            AND COALESCE(ss2.deleted,false) IS NOT TRUE
                       )
                WHERE s.deleted IS NOT TRUE
                AND s.staff_id = ?
                
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{staffId}, new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDetails sd = new StaffDetails();
                    sd.setStaffId(rs.getInt("staff_id"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setJoiningDate(rs.getDate("joining_date"));
                    sd.setDepartmentId(rs.getInt("department_id"));
                    sd.setDepartment(rs.getString("department_name"));
                    sd.setDesignation(rs.getString("designation_name"));
                    sd.setDesignationId(rs.getInt("designation_id"));
                    sd.setEmploymentType(rs.getString("employment_type"));
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    //sd.setAadharNumber(rs.getString("aadhar_number"));
                    sd.setAadharNumber(CipherUtils.decrypt(rs.getString("aadhar_number")));
                    //sd.setPhoneNumber(rs.getString("phone_number"));
                    sd.setHighestQualification(rs.getString("highest_qualification"));
                    sd.setPfAccountNo(rs.getString("pf_account_no"));
                    sd.setExperience(rs.getString("experience"));
                    sd.setExperienceDetails(rs.getString("experience_details"));
                    sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setReligion(rs.getString("religion"));
                    //sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number"));
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setEmergencyContactName(rs.getString("emergency_contact_name"));
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setStaffCountry(rs.getString("staff_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setStaffPhoto(rs.getString("staff_photo"));
                    //salary mapping
                    if(rs.getObject("ss_id") != null){
                        HrPayroleDetails salary = new HrPayroleDetails();
                        salary.setSsId(rs.getInt("ss_id"));
                        salary.setSalaryAmount(rs.getString("salary_amount"));
                        sd.setHrPayroleDetails(salary);
                    }
                    return sd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
//    @Override
//    public List<StaffDetails> getAllStaffDetails(String schoolCode) throws Exception {
//        String sql = "SELECT " +
//                "    s.staff_id, s.first_name, s.last_name, s.registration_number, s.joining_date, " +
//                "    s.department_id, dp.department AS department_name, " +
//                "    d.designation as designation_name, s.designation_id, s.employment_type, s.father_name, s.blood_group, " +
//                "    s.gender, s.aadhar_number, s.highest_qualification, s.pf_account_no, s.experience, s.experience_details, s.phone_number, s.email_address, " +
//                "    s.dob, s.religion, s.emergency_phone_number, s.emergency_contact_name, s.current_address, s.current_zipcode, " +
//                "    s.current_city, s.current_state, s.permanent_address, s.permanent_zipcode, " +
//                "    s.permanent_city, s.permanent_state, s.staff_country, s.current_status, " +
//                "    s.current_status_comment, s.staff_photo " +
//                "FROM " +
//                "    staff s " +
//                "LEFT JOIN " +
//                "staff_designation d ON s.designation_id = d.sd_id " +
//                "LEFT JOIN staff_department dp ON s.department_id = dp.stdp_id " +
//                "WHERE s.deleted is not true ORDER BY s.staff_id ASC";
//
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        List<StaffDetails> staffDetails = null;
//        try {
//            staffDetails = jdbcTemplate.query(sql, new RowMapper<StaffDetails>() {
//                @Override
//                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
//                    StaffDetails sd = new StaffDetails();
//                    sd.setStaffId(rs.getInt("staff_id"));
//                    sd.setFirstName(rs.getString("first_name"));
//                    sd.setLastName(rs.getString("last_name"));
//                    sd.setRegistrationNumber(rs.getString("registration_number"));
//                    sd.setJoiningDate(rs.getDate("joining_date"));
//                    sd.setDepartmentId(rs.getInt("department_id"));
//                    sd.setDepartment(rs.getString("department_name"));
//                    sd.setDesignation(rs.getString("designation_name"));
//                    sd.setDesignationId(rs.getInt("designation_id"));
//                    sd.setEmploymentType(rs.getString("employment_type"));
//                    sd.setFatherName(rs.getString("father_name"));
//                    sd.setBloodGroup(rs.getString("blood_group"));
//                    sd.setGender(rs.getString("gender"));
//                    //sd.setAadharNumber(rs.getString("aadhar_number"));
//                    sd.setAadharNumber(CipherUtils.decrypt(rs.getString("aadhar_number")));
//                    sd.setHighestQualification(rs.getString("highest_qualification"));
//                    sd.setPfAccountNo(rs.getString("pf_account_no"));
//                    sd.setExperience(rs.getString("experience"));
//                    sd.setExperienceDetails(rs.getString("experience_details"));
//                    //sd.setPhoneNumber(rs.getString("phone_number"));
//                    sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
//                    sd.setEmailAddress(rs.getString("email_address"));
//                    sd.setDob(rs.getDate("dob"));
//                    sd.setReligion(rs.getString("religion"));
//                    //sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number"));
//                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
//                    sd.setEmergencyContactName(rs.getString("emergency_contact_name"));
//                    sd.setCurrentAddress(rs.getString("current_address"));
//                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
//                    sd.setCurrentCity(rs.getString("current_city"));
//                    sd.setCurrentState(rs.getString("current_state"));
//                    sd.setPermanentAddress(rs.getString("permanent_address"));
//                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
//                    sd.setPermanentCity(rs.getString("permanent_city"));
//                    sd.setPermanentState(rs.getString("permanent_state"));
//                    sd.setStaffCountry(rs.getString("staff_country"));
//                    sd.setCurrentStatus(rs.getString("current_status"));
//                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
//                    sd.setStaffPhoto(rs.getString("staff_photo"));
//                    return sd;
//                }
//            });
//        } catch (Exception e){
//            e.printStackTrace();
//            return null;
//        } finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//        return staffDetails;
//    }
    @Override
    public List<StaffDetails> getAllStaffDetails(String type, String schoolCode) throws Exception{
        StringBuilder sql = new StringBuilder("""
                SELECT
                            s.staff_id,
                            s.session_id,
                            s.first_name,
                            s.last_name,
                            s.registration_number,
                            s.joining_date,
                            s.department_id,
                            dp.department AS department_name,
                            d.designation as designation_name,
                            s.designation_id,
                            s.employment_type,
                            s.father_name,
                            s.blood_group,
                            s.gender,
                            s.aadhar_number,
                            s.highest_qualification,
                            s.pf_account_no,
                            s.experience,
                            s.experience_details,
                            s.phone_number,
                            s.email_address,
                            s.dob,
                            s.religion,
                            s.emergency_phone_number,
                            s.emergency_contact_name,
                            s.current_address,
                            s.current_zipcode,
                            s.current_city,
                            s.current_state,
                            s.permanent_address,
                            s.permanent_zipcode,
                            s.permanent_city,
                            s.permanent_state,
                            s.staff_country,
                            s.current_status,
                            s.current_status_comment,
                            s.staff_photo,
                            ss.ss_id,
                            ss.salary_amount
                        FROM staff s
                        LEFT JOIN staff_designation d
                            ON s.designation_id = d.sd_id
                        LEFT JOIN staff_department dp
                            ON s.department_id = dp.stdp_id
                        LEFT JOIN staff_salary ss
                            ON ss.staff_id = s.staff_id
                            AND COALESCE(ss.deleted,false) IS NOT TRUE
                            AND ss.ss_id = (
                                SELECT MAX(ss2.ss_id)
                                FROM staff_salary ss2
                                WHERE ss2.staff_id = s.staff_id
                                AND COALESCE(ss2.deleted,false) IS NOT TRUE
                            )
                """);
        //DEFAULT
        if(type == null || type.isBlank()){
            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status ILIKE 'Active' ");
        }
        //PERMANENT
        else if (type.equalsIgnoreCase("Permanent")) {
            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status ILIKE 'Active' AND s.employment_type ILIKE 'Permanent' ");
        }
        //CONTRACT
        else if (type.equalsIgnoreCase("Contract")) {
            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status ILIKE 'Active' AND s.employment_type ILIKE 'Contract' ");
        }
        //NON TEACHING
        else if (type.equalsIgnoreCase("nonTeaching")) {
            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status ILIKE 'Active' AND d.designation NOT ILIKE '%Teacher%' ");
        }
        //DELETED
        else if(type.equalsIgnoreCase("deleted")){
            sql.append(" WHERE s.deleted IS TRUE ");
        }
        //LEAVED
        else if(type.equalsIgnoreCase("leaved")){

            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status NOT ILIKE 'Active' ");
        }

        // ---------------- NEW JOINERS ----------------
        else if(type.equalsIgnoreCase("newJoiners")){

            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status ILIKE 'Active' AND s.joining_date >= CURRENT_DATE - INTERVAL '6 months' ");
        }

        //FALLBACK
        else{
            sql.append(" WHERE s.deleted IS NOT TRUE AND s.current_status ILIKE 'Active' ");
        }

        sql.append(" ORDER BY s.staff_id ASC ");

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {

            return jdbcTemplate.query(sql.toString(), new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

                    StaffDetails sd = new StaffDetails();

                    sd.setStaffId(rs.getInt("staff_id"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setJoiningDate(rs.getDate("joining_date"));
                    sd.setDepartmentId(rs.getInt("department_id"));
                    sd.setDepartment(rs.getString("department_name"));
                    sd.setDesignationId(rs.getInt("designation_id"));
                    sd.setDesignation(rs.getString("designation_name"));
                    sd.setEmploymentType(rs.getString("employment_type"));
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setHighestQualification(rs.getString("highest_qualification"));
                    sd.setPfAccountNo(rs.getString("pf_account_no"));
                    sd.setExperience(rs.getString("experience"));
                    sd.setExperienceDetails(rs.getString("experience_details"));
                    sd.setAadharNumber(CipherUtils.decrypt(rs.getString("aadhar_number")));
                    sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setEmergencyContactName(rs.getString("emergency_contact_name"));
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setStaffCountry(rs.getString("staff_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setStaffPhoto(rs.getString("staff_photo"));
                    if(rs.getObject("ss_id") != null){
                        HrPayroleDetails salary = new HrPayroleDetails();
                        salary.setSsId(rs.getInt("ss_id"));
                        salary.setSalaryAmount(rs.getString("salary_amount"));
                        sd.setHrPayroleDetails(salary);
                    }

                    return sd;
                }
            });

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }
@Override
public StaffDetails updateStaffById(StaffDetails staffDetails, int staffId, String schoolCode) throws Exception {
    String sql = "update staff set first_name = ?, last_name = ?, registration_number = ?, joining_date = ?, department_id = ?, designation_id = ?, employment_type = ?, father_name = ?, blood_group = ?, gender = ?, aadhar_number = ?,highest_qualification=?, pf_account_no = ?, experience = ?, experience_details = ?, phone_number = ?, email_address = ?, dob = ?, religion = ?, emergency_phone_number = ?, emergency_contact_name = ?, current_address = ?, current_zipcode = ?, current_city = ?, current_state = ?, permanent_address = ?, permanent_zipcode = ?, permanent_city = ?, permanent_state = ?, staff_country = ?, current_status = ?, current_status_comment = ?, staff_photo = ? where staff_id = ?";
    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
    try{
    int rowAffected = jdbcTemplate.update(sql,
            staffDetails.getFirstName(),
            staffDetails.getLastName(),
            staffDetails.getRegistrationNumber(),
            staffDetails.getJoiningDate(),
            staffDetails.getDepartmentId(),
            staffDetails.getDesignationId(),
            staffDetails.getEmploymentType(),
            staffDetails.getFatherName(),
            staffDetails.getBloodGroup(),
            staffDetails.getGender(),
            staffDetails.getAadharNumber() != null ? CipherUtils.encrypt(staffDetails.getAadharNumber()) : null,
            staffDetails.getHighestQualification(),
            staffDetails.getPfAccountNo(),
            staffDetails.getExperience(),
            staffDetails.getExperienceDetails(),
            staffDetails.getPhoneNumber() != null ? CipherUtils.encrypt(staffDetails.getPhoneNumber()) : null,
            staffDetails.getEmailAddress(),
            staffDetails.getDob(),
            staffDetails.getReligion(),
            staffDetails.getEmergencyPhoneNumber() != null ? CipherUtils.encrypt(staffDetails.getEmergencyPhoneNumber()) : null,
            staffDetails.getEmergencyContactName(),
            staffDetails.getCurrentAddress(),
            staffDetails.getCurrentZipCode(),
            staffDetails.getCurrentCity(),
            staffDetails.getCurrentState(),
            staffDetails.getPermanentAddress(),
            staffDetails.getPermanentZipCode(),
            staffDetails.getPermanentCity(),
            staffDetails.getPermanentState(),
            staffDetails.getStaffCountry(),
            staffDetails.getCurrentStatus(),
            staffDetails.getCurrentStatusComment(),
            staffDetails.getStaffPhoto(),
            staffId
    );
    if (rowAffected > 0) {
        return staffDetails;
    } else {
        return null;
    }
} catch (Exception e){
        e.printStackTrace();
        return null;
    } finally {
        DatabaseUtil.closeDataSource(jdbcTemplate);
    }
    }

    @Override
    public boolean softDeleteStaff(int staffId,String schoolCode) throws Exception {
        String sql = "UPDATE staff SET deleted = TRUE where staff_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        int rowsAffected = jdbcTemplate.update(sql, staffId);
        if (rowsAffected > 0) {
            return true;
        } else {
            return false;
        }
    }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }
    @Override
    public int getTotalStaff(String schoolCode) throws Exception {
        String sql = "SELECT (SELECT COUNT(*) FROM staff WHERE deleted IS NOT TRUE) + (SELECT COUNT(*) FROM add_driver) AS total_staff";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }catch(Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }

    @Override
    public List<StaffDetails> totalTeacher(String schoolCode, String filter, String staffType) throws Exception {

        if (filter == null || filter.trim().isEmpty()) {
            filter = "all";
        }

        if (staffType == null || staffType.trim().isEmpty()) {
            staffType = "all";
        }

        String sql = """
        SELECT
            st.staff_id,
            st.school_id,
            st.session_id,
            st.first_name,
            st.last_name,
            st.registration_number,
            st.joining_date,
            st.designation_id,
            sd.designation,
            st.father_name,
            st.blood_group,
            st.gender,
            st.aadhar_number,
            st.highest_qualification,
            st.pf_account_no,
            st.experience_details,
            st.phone_number,
            st.email_address,
            st.dob,
            st.religion,
            st.emergency_phone_number,
            st.current_address,
            st.current_zipcode,
            st.current_city,
            st.current_state,
            st.permanent_address,
            st.permanent_zipcode,
            st.permanent_city,
            st.permanent_state,
            st.staff_country,
            st.current_status,
            st.current_status_comment,
            st.employment_type
        FROM staff st
        JOIN staff_designation sd ON sd.sd_id = st.designation_id
        WHERE st.deleted IS NULL

  
          AND (
                ? ILIKE 'all'
             OR (? ILIKE 'teaching' AND LOWER(sd.designation) LIKE '%teacher%')
             OR (? ILIKE 'non-teaching' AND LOWER(sd.designation) NOT LIKE '%teacher%')
          )

       
          AND (
                ? ILIKE 'all'
             OR (? ILIKE 'active' AND LOWER(st.current_status) = 'active')
             OR (? ILIKE 'inactive' AND LOWER(st.current_status) = 'inactive')
             OR (? ILIKE 'permanent' AND LOWER(st.employment_type) = 'permanent')
             OR (? ILIKE 'contract' AND LOWER(st.employment_type) = 'contract')
             OR (? ILIKE 'new joiners'
                 AND LOWER(st.current_status) = 'active'
                 AND st.joining_date >= CURRENT_DATE - INTERVAL '6 months')
          )

        ORDER BY st.staff_id ASC
        """;

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        return jdbcTemplate.query(
                sql,
                new Object[]{
                        staffType, staffType, staffType,   // staff type filter
                        filter, filter, filter, filter, filter, filter  // status filter
                },
                (rs, rowNum) -> {
                    StaffDetails sd = new StaffDetails();
                    sd.setStaffId(rs.getInt("staff_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSessionId(rs.getInt("session_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setStaffName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setJoiningDate(rs.getDate("joining_date"));
                    sd.setDesignationId(rs.getInt("designation_id"));
                    sd.setDesignation(rs.getString("designation"));
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setAadharNumber(rs.getString("aadhar_number") != null
                            ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    sd.setHighestQualification(rs.getString("highest_qualification"));
                    sd.setPfAccountNo(rs.getString("pf_account_no"));
                    sd.setExperienceDetails(rs.getString("experience_details"));
                    sd.setPhoneNumber(rs.getString("phone_number") != null
                            ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null
                            ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setStaffCountry(rs.getString("staff_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    sd.setEmploymentType(rs.getString("employment_type"));
                    return sd;
                }
        );
    }


    @Override
    public StaffDetails getStaffId(String staffName,String schoolCode) throws Exception {
        String sql = "SELECT staff_id FROM staff WHERE CONCAT(first_name, ' ', last_name) = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{staffName}, new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDetails sd = new StaffDetails();
                    sd.setStaffId(rs.getInt("staff_id"));
                    return sd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    /* below is updated */
    @Override
    public List<StaffDetails> getStaffByDesignation(int designationId, String schoolCode) throws Exception {
      //  String sql = "SELECT s.staff_id, CONCAT(s.first_name, ' ', s.last_name) AS staff_name FROM staff AS s  WHERE s.deleted IS NOT TRUE AND s.current_status = 'active' AND s.designation_id = ? ORDER BY s.staff_id ASC";
        String sql = "SELECT COALESCE(s.staff_id, ad.driver_id) AS id, CASE WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name) WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name) END AS name, sd.sd_id FROM staff_designation sd LEFT JOIN staff s ON sd.sd_id = s.designation_id LEFT JOIN add_driver ad ON sd.sd_id = ad.sd_id WHERE sd.sd_id = ?  AND (s.deleted IS NOT TRUE OR s.deleted IS NULL) ORDER BY id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffDetails> staffDetails = null;
        try {
            staffDetails = jdbcTemplate.query(sql, new Object[]{designationId}, new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDetails sd = new StaffDetails();
                    sd.setStaffId(rs.getInt("id"));
                    sd.setStaffName(rs.getString("name"));
                    return sd;
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffDetails;
    }

    @Override
    public List<StaffDetails> getSalaryByDesignation(int designationId, String schoolCode) throws Exception {
        //String sql = "SELECT s.staff_id, CONCAT(s.first_name, ' ', s.last_name) AS staff_name, ss.salary_amount FROM staff AS s INNER JOIN staff_salary AS ss ON s.staff_id = ss.staff_id WHERE s.deleted IS NOT TRUE AND s.current_status = 'active' and ss.deleted is not true AND s.designation_id = ? ORDER BY s.staff_id ASC";
        String sql = "SELECT \n" +
                "    sd.designation,\n" +
                "    CASE \n" +
                "        WHEN s.staff_id IS NOT NULL AND sd.designation ILIKE 'driver' THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        WHEN s.staff_id IS NOT NULL THEN CONCAT(s.first_name, ' ', s.last_name)\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN CONCAT(ad.first_name, ' ', ad.last_name)\n" +
                "        ELSE 'Unknown'\n" +
                "    END AS staff_name,\n" +
                "    CASE\n" +
                "        WHEN s.staff_id IS NOT NULL THEN s.staff_id\n" +
                "        WHEN ad.driver_id IS NOT NULL THEN ad.driver_id\n" +
                "        ELSE NULL\n" +
                "    END AS staff_id,\n" +
                "    ss.salary_amount\n" +
                "FROM \n" +
                "    staff_salary ss\n" +
                "LEFT JOIN \n" +
                "    staff_designation sd ON ss.designation_id = sd.sd_id\n" +
                "LEFT JOIN \n" +
                "    staff s ON ss.staff_id = s.staff_id AND sd.designation NOT ILIKE 'driver' AND COALESCE(s.deleted, false) IS NOT TRUE \n" +
                "LEFT JOIN \n" +
                "    add_driver ad ON ss.staff_id = ad.driver_id AND sd.designation ILIKE 'driver' \n" +
                "WHERE \n" +
                "    ss.designation_id = ?\n" +
                "    AND COALESCE(ss.deleted, false) IS NOT TRUE -- Exclude deleted records from staff_salary\n" +
                "ORDER BY \n" +
                "    ss.ss_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffDetails> staffDetails = null;
        try{
        staffDetails = jdbcTemplate.query(sql, new Object[]{designationId}, new RowMapper<StaffDetails>() {
            @Override
            public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                StaffDetails sd = new StaffDetails();
                sd.setStaffId(rs.getInt("staff_id"));
                sd.setStaffName(rs.getString("staff_name"));
                sd.setSalaryAmount(rs.getString("salary_amount"));
                return sd;
            }
        });
        return staffDetails;
    } catch(Exception e){
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public List<StaffDetails> getStaffDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "SELECT s.staff_id, s.joining_date, s.first_name, s.last_name, s.aadhar_number, d.designation FROM staff s JOIN staff_designation d ON s.designation_id = d.sd_id WHERE s.deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffDetails> staffDetails = null;
        try{
            staffDetails = jdbcTemplate.query(sql, new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDetails sd = new StaffDetails();
                    sd.setStaffId(rs.getInt("staff_id"));
                    sd.setJoiningDate(rs.getDate("joining_date"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setAadharNumber(CipherUtils.decrypt(rs.getString("aadhar_number")));
                    sd.setDesignation(rs.getString("designation"));
                    // Create a combined string of all fields
                    String combinedData = sd.getStaffId() + " " + sd.getJoiningDate() + " " + sd.getFirstName() + " " + sd.getLastName() + " " + sd.getAadharNumber() + " " + sd.getDesignation();

                    // Convert to lowercase for case-insensitive matching
                    combinedData = combinedData.toLowerCase();
                    // Check if the combined string contains the search text
                    if(combinedData.contains(searchText.toLowerCase())){
                        return sd;
                    } else{
                        return null;
                    }
                }
            }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffDetails;
    }

    @Override
    public List<StaffDetails> getAllStaffForIdCardGeneration(String schoolCode) throws Exception {
        String sql = """
                SELECT
                    s.school_id,
                    sd.school_code,
                    sd.school_name,
                    sd.school_building,
                    sd.school_address,
                    sd.email_address as school_email_address,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.phone_number as school_phone_number,
                    sd.bank_details as school_bank_details,
                    sd.branch_name as school_branch_name,
                    sd.account_number as school_account_number,
                    sd.ifsc_code as school_ifsc_code,
                    sd.alternate_phone_number as school_alternate_phone_number,
                    sd.school_zipcode,
                    s.staff_id,
                    s.first_name,
                    s.last_name,
                    s.registration_number,
                    s.joining_date,
                    d.designation AS designation_name,
                    s.designation_id,
                    s.department_id,
                    dep.department AS department_name,
                    s.father_name,
                    s.blood_group,
                    s.gender,
                    s.aadhar_number,
                    s.highest_qualification,
                    s.pf_account_no,
                    s.experience_details,
                    s.phone_number,
                    s.email_address,
                    s.dob,
                    s.religion,
                    s.emergency_phone_number,
                    s.current_address,
                    s.current_zipcode,
                    s.current_city,
                    s.current_state,
                    s.permanent_address,
                    s.permanent_zipcode,
                    s.permanent_city,
                    s.permanent_state,
                    s.staff_country,
                    s.current_status,
                    s.current_status_comment
                FROM
                    staff s
                LEFT JOIN
                    staff_designation d ON s.designation_id = d.sd_id
                LEFT JOIN
                    staff_department dep ON s.department_id = dep.stdp_id
                JOIN
                    school_details sd on s.school_id = sd.school_id
                WHERE
                    s.deleted IS NOT TRUE
                ORDER BY s.staff_id asc
                """;
        JdbcTemplate jdbcTemplate =  DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StaffDetails> staffDetailsList = null;
        try{
            staffDetailsList = jdbcTemplate.query(sql, new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDetails sd = new StaffDetails();
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSchoolCode(rs.getString("school_code"));
                    sd.setSchoolName(rs.getString("school_name"));
                    sd.setSchoolBuilding(rs.getString("school_building"));
                    sd.setSchoolAddress(rs.getString("school_address"));
                    sd.setSchoolEmailAddress(rs.getString("school_email_address"));
                    sd.setSchoolCity(rs.getString("school_city"));
                    sd.setSchoolState(rs.getString("school_state"));
                    sd.setSchoolCountry(rs.getString("school_country"));
                    sd.setSchoolPhoneNumber(rs.getString("school_phone_number") !=null ? CipherUtils.decrypt(rs.getString("school_phone_number")) : null);
                    sd.setSchoolBankDetails(rs.getString("school_bank_details"));
                    sd.setSchoolBranchName(rs.getString("school_branch_name"));
                    sd.setSchoolAccountNumber(rs.getString("school_account_number") != null ? CipherUtils.decrypt(rs.getString("school_account_number")) : null);
                    sd.setSchoolIfscCode(rs.getString("school_ifsc_code"));
                    sd.setSchoolAlternatePhoneNumber(rs.getString("school_alternate_phone_number") != null ? CipherUtils.decrypt(rs.getString("school_alternate_phone_number")) : null);
                    sd.setSchoolZipCode(rs.getString("school_zipcode"));
                    sd.setStaffId(rs.getInt("staff_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setJoiningDate(rs.getDate("joining_date"));
                    sd.setDesignation(rs.getString("designation_name"));
                    sd.setDesignationId(rs.getInt("designation_id"));
                    sd.setDepartmentId(rs.getInt("department_id"));
                    sd.setDepartment(rs.getString("department_name"));
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setAadharNumber(rs.getString("aadhar_number") != null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    sd.setHighestQualification(rs.getString("highest_qualification"));
                    sd.setPfAccountNo(rs.getString("pf_account_no"));
                    sd.setExperienceDetails(rs.getString("experience_details"));
                    //sd.setPhoneNumber(rs.getString("phone_number"));
                    sd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setStaffCountry(rs.getString("staff_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffDetailsList;
    }

    @Override
    public StaffDetails getStaffByIdForIdCardGeneration(int staffId, String schoolCode) throws Exception {
        String sql = """
                SELECT
                    s.school_id,
                    sd.school_code,
                    sd.school_name,
                    sd.school_building,
                    sd.school_address,
                    sd.email_address as school_email_address,
                    sd.school_city,
                    sd.school_state,
                    sd.school_country,
                    sd.phone_number as school_phone_number,
                    sd.bank_details as school_bank_details,
                    sd.branch_name as school_branch_name,
                    sd.account_number as school_account_number,
                    sd.ifsc_code as school_ifsc_code,
                    sd.alternate_phone_number as school_alternate_phone_number,
                    sd.school_zipcode,
                    s.staff_id,
                    s.first_name,
                    s.last_name,
                    s.registration_number,
                    s.joining_date,
                    d.designation AS designation_name,
                    s.designation_id,
                    s.father_name,
                    s.blood_group,
                    s.gender,
                    s.aadhar_number,
                    s.highest_qualification,
                    s.pf_account_no,
                    s.experience_details,
                    s.phone_number,
                    s.email_address,
                    s.dob,
                    s.religion,
                    s.emergency_phone_number,
                    s.current_address,
                    s.current_zipcode,
                    s.current_city,
                    s.current_state,
                    s.permanent_address,
                    s.permanent_zipcode,
                    s.permanent_city,
                    s.permanent_state,
                    s.staff_country,
                    s.current_status,
                    s.current_status_comment
                FROM
                    staff s
                LEFT JOIN
                    staff_designation d ON s.designation_id = d.sd_id
                JOIN
                    school_details sd on s.school_id = sd.school_id
                WHERE
                    s.staff_id = ?
                """;
        JdbcTemplate jdbcTemplate =  DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        StaffDetails staffDetailsList = null;
        try{
            staffDetailsList = jdbcTemplate.queryForObject(sql, new Object[]{staffId}, new RowMapper<StaffDetails>() {
                @Override
                public StaffDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StaffDetails sd = new StaffDetails();
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSchoolCode(rs.getString("school_code"));
                    sd.setSchoolName(rs.getString("school_name"));
                    sd.setSchoolBuilding(rs.getString("school_building"));
                    sd.setSchoolAddress(rs.getString("school_address"));
                    sd.setSchoolEmailAddress(rs.getString("school_email_address"));
                    sd.setSchoolCity(rs.getString("school_city"));
                    sd.setSchoolState(rs.getString("school_state"));
                    sd.setSchoolCountry(rs.getString("school_country"));
                    sd.setSchoolPhoneNumber(rs.getString("school_phone_number") !=null ? CipherUtils.decrypt(rs.getString("school_phone_number")) : null);
                    sd.setSchoolBankDetails(rs.getString("school_bank_details"));
                    sd.setSchoolBranchName(rs.getString("school_branch_name"));
                    sd.setSchoolAccountNumber(rs.getString("school_account_number") != null ? CipherUtils.decrypt(rs.getString("school_account_number")) : null);
                    sd.setSchoolIfscCode(rs.getString("school_ifsc_code"));
                    sd.setSchoolAlternatePhoneNumber(rs.getString("school_alternate_phone_number") != null ? CipherUtils.decrypt(rs.getString("school_alternate_phone_number")) : null);
                    sd.setSchoolZipCode(rs.getString("school_zipcode"));
                    sd.setStaffId(rs.getInt("staff_id"));
                    sd.setFirstName(rs.getString("first_name"));
                    sd.setLastName(rs.getString("last_name"));
                    sd.setRegistrationNumber(rs.getString("registration_number"));
                    sd.setJoiningDate(rs.getDate("joining_date"));
                    sd.setDesignation(rs.getString("designation_name"));
                    sd.setDesignationId(rs.getInt("designation_id"));
                    sd.setFatherName(rs.getString("father_name"));
                    sd.setBloodGroup(rs.getString("blood_group"));
                    sd.setGender(rs.getString("gender"));
                    sd.setAadharNumber(rs.getString("aadhar_number") != null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    sd.setHighestQualification(rs.getString("highest_qualification"));
                    sd.setPfAccountNo(rs.getString("pf_account_no"));
                    sd.setExperienceDetails(rs.getString("experience_details"));
                    sd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setDob(rs.getDate("dob"));
                    sd.setReligion(rs.getString("religion"));
                    sd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    sd.setCurrentAddress(rs.getString("current_address"));
                    sd.setCurrentZipCode(rs.getInt("current_zipcode"));
                    sd.setCurrentCity(rs.getString("current_city"));
                    sd.setCurrentState(rs.getString("current_state"));
                    sd.setPermanentAddress(rs.getString("permanent_address"));
                    sd.setPermanentZipCode(rs.getInt("permanent_zipcode"));
                    sd.setPermanentCity(rs.getString("permanent_city"));
                    sd.setPermanentState(rs.getString("permanent_state"));
                    sd.setStaffCountry(rs.getString("staff_country"));
                    sd.setCurrentStatus(rs.getString("current_status"));
                    sd.setCurrentStatusComment(rs.getString("current_status_comment"));
                    return sd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return staffDetailsList;
    }
}
