package com.sms.dao.impl;

import com.sms.dao.DriverDetailsDao;
import com.sms.model.DriverDetails;
import com.sms.model.StaffDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class DriverDetailsDaoImpl implements DriverDetailsDao {
    @Value("${driver.img.local.path}")
    private String FOLDER_PATH;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DriverDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*@Override
    public boolean addImage(MultipartFile file, int driverId) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileExtention = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
        String photoPath = FOLDER_PATH + driverId + "." + "png";
        file.transferTo(new File(photoPath));
        return true;
    }

    @Override
    public DriverDetails getImage(int driverId) throws Exception {
       String fileName = driverId + ".png";
       String imagePath = FOLDER_PATH + fileName;
       byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
       String base64Image = Base64.getEncoder().encodeToString(imageBytes);
       DriverDetails driverDetails = new DriverDetails();
       driverDetails.setDriverImage(base64Image);
       return driverDetails;
    }*/

    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int driverId) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") +1);
        String photoPath = FOLDER_PATH + schoolCode + File.separator + driverId + "." + "png";
        File directory = new File(FOLDER_PATH + schoolCode);
        if(!directory.exists()){
            directory.mkdirs();
        }
        file.transferTo(new File(photoPath));
        return true;
    }

    @Override
    public DriverDetails getImage(String schoolCode, int driverId) throws Exception {
        String fileName = driverId + ".png";
        String imagePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        File imageFile = new File(imagePath);
        if(!imageFile.exists()){
            throw new IOException("File not found: " + imagePath);
        }
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        DriverDetails driverDetails = new DriverDetails();
        driverDetails.setDriverImage(base64Image);
        return driverDetails;
    }

//    public int getDriverId(String schoolCode) throws Exception{
//        String sql = "select sd_id from staff_designation where designation ilike 'Driver'";
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        int driverId = 0;
//        Logger logger = LoggerFactory.getLogger(getClass());
//        try{
//            Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
//            if(result != null){
//                driverId = result;
//                System.out.println(driverId);
//            }else {
//                logger.warn("No driver id found");
//            }
//        }catch (EmptyResultDataAccessException e){
//            logger.warn("No driver designation found in staff_designation for school");
//        }catch (Exception e){
//            logger.warn("Error retrieving driver ID for school code");
//            throw e;
//        }finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//        return driverId;
//    }
public int getDriverId(String schoolCode) throws Exception {

    String sql = "SELECT sd_id FROM staff_designation WHERE designation ILIKE 'Driver'";
    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

    try {
        return jdbcTemplate.queryForObject(sql, Integer.class);
    } catch (EmptyResultDataAccessException e) {
        throw new RuntimeException("Driver designation not found in staff_designation table");
    } finally {
        DatabaseUtil.closeDataSource(jdbcTemplate);
    }
}

    @Override
public DriverDetails addDriver(DriverDetails driverDetails, String schoolCode) throws Exception {

    // Encrypt contact number
    String encryptedContactNumber = CipherUtils.encrypt(driverDetails.getContactNumber());
    driverDetails.setContactNumber(encryptedContactNumber);

    // Encrypt license number
    String encryptedLicense = CipherUtils.encrypt(driverDetails.getLicenseNumber());
    driverDetails.setLicenseNumber(encryptedLicense);

    int designationId = getDriverId(schoolCode);
        if (designationId <= 0) {
            throw new RuntimeException("Driver designation not found. Insert aborted.");
        }

    String sql = "INSERT INTO add_driver (" +
            "school_id, session_id, sd_id, first_name, last_name, dob, " +
            "contact_number, license_number, aadhar_number, joining_date, " +
            "employment_type, experience, address, city, state, zip_code, country, " +
            "current_status, current_status_comment, updated_by, update_date_time" +
            ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

    try {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, driverDetails.getSchoolId());
            ps.setInt(2, driverDetails.getSessionId());
            //ps.setInt(3, driverDetails.getSdId()); // sd_id
            ps.setInt(3, designationId);
            ps.setString(4, driverDetails.getFirstName());
            ps.setString(5, driverDetails.getLastName());
            ps.setDate(6, new java.sql.Date(driverDetails.getDob().getTime()));
            ps.setString(7, driverDetails.getContactNumber());
            ps.setString(8, driverDetails.getLicenseNumber());
            ps.setString(9, driverDetails.getAadharNumber());
            ps.setDate(10, new java.sql.Date(driverDetails.getJoiningDate().getTime()));
            ps.setString(11, driverDetails.getEmploymentType());
            ps.setString(12, driverDetails.getExperience());
            ps.setString(13, driverDetails.getAddress());
            ps.setString(14, driverDetails.getCity());
            ps.setString(15, driverDetails.getState());
            ps.setInt(16, driverDetails.getZipCode());
            ps.setString(17, driverDetails.getCountry());
            ps.setString(18, driverDetails.getCurrentStatus());
            ps.setString(19, driverDetails.getCurrentStatusComment());
            ps.setInt(20, driverDetails.getUpdatedBy());
            ps.setTimestamp(21, new java.sql.Timestamp(driverDetails.getUpdateDateTime().getTime()));

            return ps;

        }, keyHolder);


        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("driver_id")) {
            driverDetails.setDriverId(((Number) keys.get("driver_id")).intValue());
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        DatabaseUtil.closeDataSource(jdbcTemplate);
    }

    return driverDetails;
}



    @Override
    public DriverDetails getDriverById(int driverId, String schoolCode) throws Exception {
        String sql = "SELECT driver_id, school_id,session_id, sd_id, first_name, last_name, dob, contact_number, license_number, aadhar_number, joining_date, employment_type,experience, address, city, state, zip_code, country, current_status, current_status_comment from add_driver where driver_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{driverId}, new RowMapper<DriverDetails>() {
                @Override
                public DriverDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DriverDetails dd = new DriverDetails();
                    dd.setDriverId(rs.getInt("driver_id"));
                    dd.setSchoolId(rs.getInt("school_id"));
                    dd.setSessionId(rs.getInt("session_id"));
                    dd.setSdId(rs.getInt("sd_id"));
                    dd.setFirstName(rs.getString("first_name"));
                    dd.setLastName(rs.getString("last_name"));
                    dd.setDob(rs.getDate("dob"));

                    // Decrypt sensitive fields
                    dd.setContactNumber(CipherUtils.decrypt(rs.getString("contact_number")));
                    dd.setLicenseNumber(CipherUtils.decrypt(rs.getString("license_number")));

                    dd.setAadharNumber(rs.getString("aadhar_number"));
                    dd.setJoiningDate(rs.getDate("joining_date"));
                    dd.setEmploymentType(rs.getString("employment_type"));
                    dd.setExperience(rs.getString("experience"));
                    dd.setAddress(rs.getString("address"));
                    dd.setCity(rs.getString("city"));
                    dd.setState(rs.getString("state"));
                    dd.setZipCode(rs.getInt("zip_code"));
                    dd.setCountry(rs.getString("country"));
                    dd.setCurrentStatus(rs.getString("current_status"));
                    dd.setCurrentStatusComment(rs.getString("current_status_comment"));

                    return dd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


   @Override
   public List<DriverDetails> getAllDriver(String schoolCode) throws Exception {
       String sql = "SELECT driver_id, school_id,session_id, sd_id, first_name, last_name, dob, contact_number, license_number, address, city, state, zip_code, country from add_driver order by driver_id asc";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<DriverDetails> driverDetails = null;
       try{
           driverDetails = jdbcTemplate.query(sql, new RowMapper<DriverDetails>() {
               @Override
               public DriverDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   DriverDetails dd = new DriverDetails();
                   dd.setDriverId(rs.getInt("driver_id"));
                   dd.setSchoolId(rs.getInt("school_id"));
                   dd.setSessionId(rs.getInt("session_id"));
                   dd.setSdId(rs.getInt("sd_id"));
                   dd.setFirstName(rs.getString("first_name"));
                   dd.setLastName(rs.getString("last_name"));
                   dd.setDob(rs.getDate("dob"));
                   //decrypting contact number
                   dd.setContactNumber(CipherUtils.decrypt(rs.getString("contact_number")));
                   //decrypting license number
                   dd.setLicenseNumber(CipherUtils.decrypt(rs.getString("license_number")));
                   dd.setAddress(rs.getString("address"));
                   dd.setCity(rs.getString("city"));
                   dd.setState(rs.getString("state"));
                   dd.setZipCode(rs.getInt("zip_code"));
                   dd.setCountry(rs.getString("country"));
                   return dd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return driverDetails;
   }
   @Override
   public DriverDetails updateDriverDetails(DriverDetails driverDetails, int driverId, String schoolCode) throws Exception {
       //Encoding contact number
       String encryptedContactNumber = CipherUtils.encrypt(driverDetails.getContactNumber());
       driverDetails.setContactNumber(encryptedContactNumber);
       //Encoding License number
       String encryptedLicense = CipherUtils.encrypt(driverDetails.getLicenseNumber());
       driverDetails.setLicenseNumber(encryptedLicense);
       String sql = "update add_driver set first_name = ?, last_name = ?, dob = ?, contact_number = ?, license_number = ?, address = ?, city = ?, state = ?, zip_code = ?, country = ? where driver_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql,
                   driverDetails.getFirstName(),
                   driverDetails.getLastName(),
                   driverDetails.getDob(),
                   driverDetails.getContactNumber(),
                   driverDetails.getLicenseNumber(),
                   driverDetails.getAddress(),
                   driverDetails.getCity(),
                   driverDetails.getState(),
                   driverDetails.getZipCode(),
                   driverDetails.getCity(),
                   driverId
           );
           if (rowAffected > 0) {
               driverDetails.setDriverId(driverId);
               return driverDetails;
           } else {
               return null;
           }
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

   @Override
   public boolean deleteDriver(int driverId, String schoolCode) throws Exception {
       String sql = "delete from add_driver where driver_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql, new Object[]{driverId});
           if(rowAffected > 0){
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
    @Override
    public List<DriverDetails> getDriverDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "select driver_id, first_name, last_name, dob, contact_number from add_driver";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<DriverDetails> driverDetails = null;

        try {
            driverDetails = jdbcTemplate.query(sql, new RowMapper<DriverDetails>() {
                @Override
                public DriverDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DriverDetails dd = new DriverDetails();
                    dd.setDriverId(rs.getInt("driver_id"));
                    dd.setFirstName(rs.getString("first_name"));
                    dd.setLastName(rs.getString("last_name"));
                    dd.setDob(rs.getDate("dob"));
                    dd.setContactNumber(CipherUtils.decrypt(rs.getString("contact_number")));

                    // Create a combined string of all fields
                    String combinedData = dd.getDriverId() + " " + dd.getFirstName() + " " + dd.getLastName() + " " + dd.getDob() + " " + dd.getContactNumber();

                    // Convert to lowercase for case-insensitive matching
                    combinedData = combinedData.toLowerCase();

                    // Check if the combined string contains the search text
                    if (combinedData.contains(searchText.toLowerCase())) {
                        return dd;
                    } else {
                        return null;
                    }
                }
            }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return driverDetails;
    }


}
