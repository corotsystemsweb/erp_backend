package com.sms.dao.impl;

import com.sms.dao.SchoolDao;
import com.sms.exception.ImageSizeLimitExceededException;
import com.sms.model.SchoolDetails;
import com.sms.model.StaffDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Repository
public class SchoolDaoImpl implements SchoolDao {
    @Value("${school.img.local.path}")
    private String FOLDER_PATH;
    private final JdbcTemplate jdbcTemplate;

    public SchoolDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int schoolId) throws Exception {
        // Check the file size (in bytes)
        long maxSize = 200 * 1024; // 200KB in bytes
        if (file.getSize() > maxSize) {
            throw new ImageSizeLimitExceededException("File size exceeds the maximum limit of 200KB");
        }
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") +1);
        String photoPath = FOLDER_PATH + schoolCode + File.separator + schoolId + "." + "png";
        File directory = new File(FOLDER_PATH + schoolCode);
        if(!directory.exists()){
            directory.mkdirs();
        }
        file.transferTo(new File(photoPath));
        return true;
    }

    @Override
    public SchoolDetails getImage(String schoolCode, int schoolId) throws Exception {
        String fileName = schoolId + ".png";
        String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        //check if the imagePath is exists
        File imageFile = new File(imagePath);
        if(!imageFile.exists()){
            throw new IOException("File not found: " + imagePath);
        }
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        SchoolDetails schoolDetails = new SchoolDetails();
        schoolDetails.setSchoolImageString(base64Image);
        return schoolDetails;
    }

    /*
    @method addSchoolDetails
    @param schoolDetails
    @throws Exception
    @description Adding school details in session table
    @developer Sukhendu Bhowmik
    */

    /*@Override
    public SchoolDetails addSchoolDetails(SchoolDetails schoolDetails, String schoolCode) throws Exception {
        // Encrypting phone number
        String encryptedPhoneNumber = CipherUtils.encrypt(schoolDetails.getPhoneNumber());
        schoolDetails.setPhoneNumber(encryptedPhoneNumber);
        // Encrypting account number
        String encryptedAccountNumber = CipherUtils.encrypt(schoolDetails.getAccountNumber());
        schoolDetails.setAccountNumber(encryptedAccountNumber);
        // Encrypting alternative phone number
        String encryptedAlternativePhoneNumber = CipherUtils.encrypt(schoolDetails.getAlternatePhoneNumber());
        schoolDetails.setAlternatePhoneNumber(encryptedAlternativePhoneNumber);
        String sql = "insert into school_details (school_code, school_name, school_building, school_address, email_address, school_city, school_state, school_country, phone_number, bank_details,branch_name,account_number, ifsc_code, alternate_phone_number, school_zipcode, school_photo) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, schoolDetails.getSchoolCode());
                ps.setString(2, schoolDetails.getSchoolName());
                ps.setString(3, schoolDetails.getSchoolBuilding());
                ps.setString(4, schoolDetails.getSchoolAddress());
                ps.setString(5, schoolDetails.getEmailAddress());
                ps.setString(6, schoolDetails.getSchoolCity());
                ps.setString(7, schoolDetails.getSchoolState());
                ps.setString(8, schoolDetails.getSchoolCountry());
                ps.setString(9, schoolDetails.getPhoneNumber());
                ps.setString(10,schoolDetails.getBankDetails());
                ps.setString(11,schoolDetails.getBranchName());
                ps.setString(12,schoolDetails.getAccountNumber());
                ps.setString(13,schoolDetails.getIfscCode());
                ps.setString(14,schoolDetails.getAlternatePhoneNumber());
                ps.setString(15,schoolDetails.getSchoolZipCode());
                ps.setString(16,schoolDetails.getSchoolPhoto());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("school_id")){
                int generatedKey = ((Number) keys.get("school_id")).intValue();
                schoolDetails.setSchoolId(generatedKey);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return schoolDetails;
    }*/
    @Override
    public SchoolDetails addSchoolDetails(SchoolDetails schoolDetails, String schoolCode) throws Exception {
        // Encrypting phone number
        String encryptedPhoneNumber = CipherUtils.encrypt(schoolDetails.getPhoneNumber());
        schoolDetails.setPhoneNumber(encryptedPhoneNumber);
        // Encrypting account number
        String encryptedAccountNumber = CipherUtils.encrypt(schoolDetails.getAccountNumber());
        schoolDetails.setAccountNumber(encryptedAccountNumber);
        // Encrypting alternative phone number
        String encryptedAlternativePhoneNumber = CipherUtils.encrypt(schoolDetails.getAlternatePhoneNumber());
        schoolDetails.setAlternatePhoneNumber(encryptedAlternativePhoneNumber);
        String sql = "insert into school_details (school_code, school_affiliation_no, school_name, school_building, school_address, email_address, school_city, school_state, school_country, phone_number, bank_details,branch_name,account_number, ifsc_code, alternate_phone_number, school_zipcode, school_photo) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, schoolDetails.getSchoolCode());
                ps.setString(2, schoolDetails.getSchoolAffiliationNo());
                ps.setString(3, schoolDetails.getSchoolName());
                ps.setString(4, schoolDetails.getSchoolBuilding());
                ps.setString(5, schoolDetails.getSchoolAddress());
                ps.setString(6, schoolDetails.getEmailAddress());
                ps.setString(7, schoolDetails.getSchoolCity());
                ps.setString(8, schoolDetails.getSchoolState());
                ps.setString(9, schoolDetails.getSchoolCountry());
                ps.setString(10, schoolDetails.getPhoneNumber());
                ps.setString(11,schoolDetails.getBankDetails());
                ps.setString(12,schoolDetails.getBranchName());
                ps.setString(13,schoolDetails.getAccountNumber());
                ps.setString(14,schoolDetails.getIfscCode());
                ps.setString(15,schoolDetails.getAlternatePhoneNumber());
                ps.setString(16,schoolDetails.getSchoolZipCode());
                ps.setString(17,schoolDetails.getSchoolPhoto());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("school_id")){
                int generatedKey = ((Number) keys.get("school_id")).intValue();
                schoolDetails.setSchoolId(generatedKey);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return schoolDetails;
    }

     /*
    @method getSchoolDetailsById
    @param schoolId
    @throws Exception
    @description getting session details by its id from session table
    @developer Sukhendu Bhowmik
    */
    /*@Override
    public SchoolDetails getSchoolDetailsById(int schoolId, String schoolCode) throws Exception {
        String sql = "select distinct school_id, school_code, school_name, school_building, school_address, email_address, school_city, school_state, school_country, phone_number, bank_details,branch_name,account_number, ifsc_code, alternate_phone_number, school_zipcode, school_photo from school_details where school_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SchoolDetails schoolDetails = null;
        schoolDetails = jdbcTemplate.queryForObject(sql, new Object[]{schoolId}, new RowMapper<SchoolDetails>() {
            @Override
            public SchoolDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                SchoolDetails sd = new SchoolDetails();
                sd.setSchoolId(rs.getInt("school_id"));
                sd.setSchoolCode(rs.getString("school_code"));
                sd.setSchoolName(rs.getString("school_name"));
                sd.setSchoolBuilding(rs.getString("school_building"));
                sd.setSchoolAddress(rs.getString("school_address"));
                sd.setEmailAddress(rs.getString("email_address"));
                sd.setSchoolCity(rs.getString("school_city"));
                sd.setSchoolState(rs.getString("school_state"));
                sd.setSchoolCountry(rs.getString("school_country"));
                //sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                String phoneNumber = rs.getString("phone_number");
                if (phoneNumber != null) {
                    sd.setPhoneNumber(CipherUtils.decrypt(phoneNumber));
                }
                sd.setBankDetails(rs.getString("bank_details"));
                sd.setBranchName(rs.getString("branch_name"));
                //sd.setAccountNumber(CipherUtils.decrypt(rs.getString("account_number")));
                String accountNumber = rs.getString("account_number");
                if (accountNumber != null) {
                    sd.setAccountNumber(CipherUtils.decrypt(accountNumber));
                }
                sd.setIfscCode(rs.getString("ifsc_code"));
                //sd.setAlternatePhoneNumber((CipherUtils.decrypt(rs.getString("alternate_phone_number"))));
                String alternatePhoneNumber = rs.getString("alternate_phone_number");
                if (alternatePhoneNumber != null) {
                    sd.setAlternatePhoneNumber(CipherUtils.decrypt(alternatePhoneNumber));
                }
                sd.setSchoolZipCode(rs.getString("school_zipcode"));
                sd.setSchoolPhoto(rs.getString("school_photo"));
                return sd;
            }
        });
        return schoolDetails;
    }*/
     @Override
     public SchoolDetails getSchoolDetailsById(int schoolId, String schoolCode) throws Exception {
         String sql = "select distinct school_id, school_code, school_affiliation_no, school_name, school_building, school_address, email_address, school_city, school_state, school_country, phone_number, bank_details,branch_name,account_number, ifsc_code, alternate_phone_number, school_zipcode, school_photo from school_details where school_id = ?";
         JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
         SchoolDetails schoolDetails = null;
         try{
             schoolDetails = jdbcTemplate.queryForObject(sql, new Object[]{schoolId}, new RowMapper<SchoolDetails>() {
                 @Override
                 public SchoolDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                     SchoolDetails sd = new SchoolDetails();
                     sd.setSchoolId(rs.getInt("school_id"));
                     sd.setSchoolCode(rs.getString("school_code"));
                     sd.setSchoolAffiliationNo(rs.getString("school_affiliation_no"));
                     sd.setSchoolName(rs.getString("school_name"));
                     sd.setSchoolBuilding(rs.getString("school_building"));
                     sd.setSchoolAddress(rs.getString("school_address"));
                     sd.setEmailAddress(rs.getString("email_address"));
                     sd.setSchoolCity(rs.getString("school_city"));
                     sd.setSchoolState(rs.getString("school_state"));
                     sd.setSchoolCountry(rs.getString("school_country"));
                     //sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                     String phoneNumber = rs.getString("phone_number");
                     if (phoneNumber != null) {
                         sd.setPhoneNumber(CipherUtils.decrypt(phoneNumber));
                     }
                     sd.setBankDetails(rs.getString("bank_details"));
                     sd.setBranchName(rs.getString("branch_name"));
                     //sd.setAccountNumber(CipherUtils.decrypt(rs.getString("account_number")));
                     String accountNumber = rs.getString("account_number");
                     if (accountNumber != null) {
                         sd.setAccountNumber(CipherUtils.decrypt(accountNumber));
                     }
                     sd.setIfscCode(rs.getString("ifsc_code"));
                     //sd.setAlternatePhoneNumber((CipherUtils.decrypt(rs.getString("alternate_phone_number"))));
                     String alternatePhoneNumber = rs.getString("alternate_phone_number");
                     if (alternatePhoneNumber != null) {
                         sd.setAlternatePhoneNumber(CipherUtils.decrypt(alternatePhoneNumber));
                     }
                     sd.setSchoolZipCode(rs.getString("school_zipcode"));
                     sd.setSchoolPhoto(rs.getString("school_photo"));
                     return sd;
                 }
             });
         }catch (Exception e){
             e.printStackTrace();
             return null;
         }finally {
             DatabaseUtil.closeDataSource(jdbcTemplate);
         }
         return schoolDetails;
     }

    /*
    @method getAllSchoolDetails
    @throws Exception
    @description getting all school details from session table
    @developer Sukhendu Bhowmik
    */
    /*@Override
    public List<SchoolDetails> getAllSchoolDetails(String schoolCode) throws Exception {
        String sql = "select distinct school_id, school_code, school_name, school_building, school_address, email_address, school_city, school_state, school_country, phone_number, bank_details,branch_name,account_number, ifsc_code, alternate_phone_number, school_zipcode, school_photo from school_details order by school_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SchoolDetails> schoolDetails = jdbcTemplate.query(sql, new RowMapper<SchoolDetails>() {
            @Override
            public SchoolDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                SchoolDetails sd = new SchoolDetails();
                sd.setSchoolId(rs.getInt("school_id"));
                sd.setSchoolCode(rs.getString("school_code"));
                sd.setSchoolName(rs.getString("school_name"));
                sd.setSchoolBuilding(rs.getString("school_building"));
                sd.setSchoolAddress(rs.getString("school_address"));
                sd.setEmailAddress(rs.getString("email_address"));
                sd.setSchoolCity(rs.getString("school_city"));
                sd.setSchoolState(rs.getString("school_state"));
                sd.setSchoolCountry(rs.getString("school_country"));
                //sd.setPhoneNumber(rs.getString("phone_number"));
                sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                sd.setBankDetails(rs.getString("bank_details"));
                sd.setBranchName(rs.getString("branch_name"));
                //sd.setAccountNumber(rs.getString("account_number"));
                sd.setAccountNumber(CipherUtils.decrypt(rs.getString("account_number")));
                sd.setIfscCode(rs.getString("ifsc_code"));
                //sd.setAlternatePhoneNumber(rs.getString("alternate_phone_number"));
                sd.setAlternatePhoneNumber((CipherUtils.decrypt(rs.getString("alternate_phone_number"))));
                sd.setSchoolZipCode(rs.getString("school_zipcode"));
                sd.setSchoolPhoto(rs.getString("school_photo"));
                return sd;
            }
        });
        return schoolDetails;
    }*/
    @Override
    public List<SchoolDetails> getAllSchoolDetails(String schoolCode) throws Exception {
        String sql = "select distinct school_id, school_code, school_affiliation_no, school_name, school_building, school_address, email_address, school_city, school_state, school_country, phone_number, bank_details,branch_name,account_number, ifsc_code, alternate_phone_number, school_zipcode, school_photo from school_details order by school_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SchoolDetails> schoolDetails = null;
        try{
             schoolDetails = jdbcTemplate.query(sql, new RowMapper<SchoolDetails>() {
                @Override
                public SchoolDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SchoolDetails sd = new SchoolDetails();
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSchoolCode(rs.getString("school_code"));
                    sd.setSchoolAffiliationNo(rs.getString("school_affiliation_no"));
                    sd.setSchoolName(rs.getString("school_name"));
                    sd.setSchoolBuilding(rs.getString("school_building"));
                    sd.setSchoolAddress(rs.getString("school_address"));
                    sd.setEmailAddress(rs.getString("email_address"));
                    sd.setSchoolCity(rs.getString("school_city"));
                    sd.setSchoolState(rs.getString("school_state"));
                    sd.setSchoolCountry(rs.getString("school_country"));
                    //sd.setPhoneNumber(rs.getString("phone_number"));
                    sd.setPhoneNumber(CipherUtils.decrypt(rs.getString("phone_number")));
                    sd.setBankDetails(rs.getString("bank_details"));
                    sd.setBranchName(rs.getString("branch_name"));
                    //sd.setAccountNumber(rs.getString("account_number"));
                    sd.setAccountNumber(rs.getString("account_number") != null ? CipherUtils.decrypt(rs.getString("account_number")) : null);
                    sd.setIfscCode(rs.getString("ifsc_code"));
                    //sd.setAlternatePhoneNumber(rs.getString("alternate_phone_number"));
                    sd.setAlternatePhoneNumber((CipherUtils.decrypt(rs.getString("alternate_phone_number"))));
                    sd.setSchoolZipCode(rs.getString("school_zipcode"));
                    sd.setSchoolPhoto(rs.getString("school_photo"));
                    return sd;
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return schoolDetails;
    }
    /*
    @method updateSchoolDetailsById
    @param schoolDetails, schoolId
    @throws Exception
    @description updating  school details by id
    @developer Sukhendu Bhowmik
    */
    /*@Override
    public SchoolDetails updateSchoolDetailsById(SchoolDetails schoolDetails, int schoolId, String schoolCode) throws Exception {
        // Encrypting phone number
        String encryptedPhoneNumber = CipherUtils.encrypt(schoolDetails.getPhoneNumber());
        schoolDetails.setPhoneNumber(encryptedPhoneNumber);
        // Encrypting account number
        String encryptedAccountNumber = CipherUtils.encrypt(schoolDetails.getAccountNumber());
        schoolDetails.setAccountNumber(encryptedAccountNumber);
        // Encrypting alternative phone number
        String encryptedAlternativePhoneNumber = CipherUtils.encrypt(schoolDetails.getAlternatePhoneNumber());
        schoolDetails.setAlternatePhoneNumber(encryptedAlternativePhoneNumber);
        String sql = "update school_details set school_code = ?, school_name = ?, school_building = ?, school_address = ?, email_address = ?, school_city = ?, school_state = ?, school_country = ?, phone_number = ?, bank_details = ?, branch_name = ?,account_number = ?, ifsc_code = ?, alternate_phone_number = ?, school_zipcode = ?, school_photo = ? where school_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int rewAffected = jdbcTemplate.update(sql,
                schoolDetails.getSchoolCode(),
                schoolDetails.getSchoolName(),
                schoolDetails.getSchoolBuilding(),
                schoolDetails.getSchoolAddress(),
                schoolDetails.getEmailAddress(),
                schoolDetails.getSchoolCity(),
                schoolDetails.getSchoolState(),
                schoolDetails.getSchoolCountry(),
                schoolDetails.getPhoneNumber(),
                schoolDetails.getBankDetails(),
                schoolDetails.getBranchName(),
                schoolDetails.getAccountNumber(),
                schoolDetails.getIfscCode(),
                schoolDetails.getAlternatePhoneNumber(),
                schoolDetails.getSchoolZipCode(),
                schoolDetails.getSchoolPhoto(),
                schoolDetails.getSchoolId());
        if(rewAffected > 0){
            return schoolDetails;
        }
        return null;
    }*/
    public SchoolDetails updateSchoolDetailsById(SchoolDetails schoolDetails, int schoolId, String schoolCode) throws Exception {
        // Encrypting phone number
        String encryptedPhoneNumber = CipherUtils.encrypt(schoolDetails.getPhoneNumber());
        schoolDetails.setPhoneNumber(encryptedPhoneNumber);
        // Encrypting account number
        String encryptedAccountNumber = CipherUtils.encrypt(schoolDetails.getAccountNumber());
        schoolDetails.setAccountNumber(encryptedAccountNumber);
        // Encrypting alternative phone number
        String encryptedAlternativePhoneNumber = CipherUtils.encrypt(schoolDetails.getAlternatePhoneNumber());
        schoolDetails.setAlternatePhoneNumber(encryptedAlternativePhoneNumber);

        String sql =
                "UPDATE school_details SET " +
                        "school_code = ?, school_affiliation_no = ?, school_name = ?, " +
                        "school_building = ?, school_address = ?, email_address = ?, " +
                        "school_city = ?, school_state = ?, school_country = ?, " +
                        "phone_number = ?, bank_details = ?, branch_name = ?, " +
                        "account_number = ?, ifsc_code = ?, alternate_phone_number = ?, " +
                        "school_zipcode = ?, school_photo = ? " +
                        "WHERE school_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rewAffected = jdbcTemplate.update(sql,
                    schoolDetails.getSchoolCode(),
                    schoolDetails.getSchoolAffiliationNo(),
                    schoolDetails.getSchoolName(),
                    schoolDetails.getSchoolBuilding(),
                    schoolDetails.getSchoolAddress(),
                    schoolDetails.getEmailAddress(),
                    schoolDetails.getSchoolCity(),
                    schoolDetails.getSchoolState(),
                    schoolDetails.getSchoolCountry(),
                    schoolDetails.getPhoneNumber(),
                    schoolDetails.getBankDetails(),
                    schoolDetails.getBranchName(),
                    schoolDetails.getAccountNumber(),
                    schoolDetails.getIfscCode(),
                    schoolDetails.getAlternatePhoneNumber(),
                    schoolDetails.getSchoolZipCode(),
                    schoolDetails.getSchoolPhoto(),
                    schoolDetails.getSchoolId());
            if(rewAffected > 0){
                return schoolDetails;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return null;
    }

    /*
    @method deleteSchoolDetailsById
    @param schoolId
    @throws Exception
    @description deleting  school details by id
    @developer Sukhendu Bhowmik
    */
    /*@Override
    public boolean deleteSchoolDetailsById(int schoolId, String schoolCode) throws Exception {
        String sql = "delete from school_details where school_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int rowAffected = jdbcTemplate.update(sql, new Object[]{schoolId});
        if(rowAffected > 0 ){
            return true;
        }else{
            return false;
        }
    }*/
    public boolean deleteSchoolDetailsById(int schoolId, String schoolCode) throws Exception {
        String sql = "delete from school_details where school_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{schoolId});
            if(rowAffected > 0 ){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }
}
