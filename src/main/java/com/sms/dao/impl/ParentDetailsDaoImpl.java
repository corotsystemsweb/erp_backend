package com.sms.dao.impl;

import com.sms.dao.ParentDetailsDao;
import com.sms.model.ParentDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
public class ParentDetailsDaoImpl implements ParentDetailsDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ParentDetailsDaoImpl.class);

    public ParentDetails findExistingParent(String encryptedAadhar, String schoolCode) {
        String sql = "SELECT parent_id FROM parent_details WHERE deleted IS NOT TRUE AND aadhar_number = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{encryptedAadhar},
                    (rs, rowNum) -> {
                        ParentDetails pd = new ParentDetails();
                        pd.setParentId(rs.getInt("parent_id"));
                        return pd;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ParentDetails> addBulkParentDetails(List<ParentDetails> parentDetailsList, String schoolCode) throws Exception {
        if (parentDetailsList == null || parentDetailsList.isEmpty()) {
            throw new IllegalArgumentException("No Parent details are provided for insertion");
        }
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            for (ParentDetails parentDetails : parentDetailsList) {
                //Encrypt Aadhaar once
                String encryptedAadhar = CipherUtils.encrypt(parentDetails.getAadharNumber());
                parentDetails.setAadharNumber(encryptedAadhar);

                // Check if parent already exists (duplicate Aadhaar)
                String duplicateSql = "SELECT parent_id FROM parent_details WHERE deleted IS NOT TRUE AND aadhar_number = ?";
                Integer existingParentId = null;
                try {
                    existingParentId = jdbcTemplate.queryForObject(duplicateSql, new Object[]{encryptedAadhar},Integer.class);
                } catch (EmptyResultDataAccessException ignored) {}

                if (existingParentId != null) {
                    // Parent already exists → Assign ID and skip insert
                    parentDetails.setParentId(existingParentId);
                    continue;
                }
                // Insert NEW parent
                String insertSql =
                        "INSERT INTO parent_details (uuid, school_id, first_name, last_name, dob, phone_number, emergency_phone_number, " +
                                "whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number, company_name, designation, " +
                                "company_address, company_phone, address, city, state, zipcode, updated_by, updated_date, create_date) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE)";
                jdbcTemplate.update(insertSql,
                        parentDetails.getUuid(),
                        parentDetails.getSchoolId(),
                        parentDetails.getFirstName(),
                        parentDetails.getLastName(),
                        parentDetails.getDob(),
                        parentDetails.getPhoneNumber() != null ? CipherUtils.encrypt(parentDetails.getPhoneNumber()) : null,
                        parentDetails.getEmergencyPhoneNumber() != null ? CipherUtils.encrypt(parentDetails.getEmergencyPhoneNumber()) : null,
                        parentDetails.getWhatsappNumber() != null ? CipherUtils.encrypt(parentDetails.getWhatsappNumber()) : null,
                        parentDetails.getEmail(),
                        parentDetails.getGender(),
                        parentDetails.getParentType(),
                        parentDetails.getQualification(),
                        encryptedAadhar,
                        parentDetails.getCompanyName(),
                        parentDetails.getDesignation(),
                        parentDetails.getCompanyAddress(),
                        parentDetails.getCompanyPhone() != null ? CipherUtils.encrypt(parentDetails.getCompanyPhone()) : null,
                        parentDetails.getAddress(),
                        parentDetails.getCity(),
                        parentDetails.getState(),
                        parentDetails.getZipcode(),
                        parentDetails.getUpdatedBy()
                );
            }

            //Return all parents with this UUID (after inserts + skips)
            String uuid = parentDetailsList.get(0).getUuid();
            return getParentsByUuid(uuid, schoolCode);

        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


//    @Override
//    public List<ParentDetails> addBulkParentDetails(List<ParentDetails> parentDetailsList, String schoolCode) throws Exception {
//        if(parentDetailsList == null || parentDetailsList.isEmpty()){
//            throw new IllegalArgumentException("No Parent details are provided for insertion");
//        }
//        String sql = "INSERT INTO parent_details (uuid, school_id, first_name, last_name,  dob, phone_number, emergency_phone_number, whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number, company_name, designation, company_address, company_phone, address, city, state, zipcode, updated_by, updated_date, create_date) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
//        try{
//            int[] batchResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//                @Override
//                public void setValues(PreparedStatement ps, int i) throws SQLException {
//                    ParentDetails parentDetails = parentDetailsList.get(i);
//                    ps.setString(1, parentDetails.getUuid());
//                    ps.setInt(2, parentDetails.getSchoolId());
//                    ps.setString(3, parentDetails.getFirstName());
//                    ps.setString(4, parentDetails.getLastName());
//                    ps.setDate(5, parentDetails.getDob() != null ? new java.sql.Date(parentDetails.getDob().getTime()) : null);
//                    ps.setString(6, CipherUtils.encrypt(parentDetails.getPhoneNumber()));
//                    ps.setString(7, CipherUtils.encrypt(parentDetails.getEmergencyPhoneNumber()));
//                    ps.setString(8, CipherUtils.encrypt(parentDetails.getWhatsappNumber()));
//                    ps.setString(9, parentDetails.getEmail());
//                    ps.setString(10, parentDetails.getGender());
//                    ps.setString(11, parentDetails.getParentType());
//                    ps.setString(12, parentDetails.getQualification());
//                    ps.setString(13, CipherUtils.encrypt(parentDetails.getAadharNumber()));
//                    ps.setString(14, parentDetails.getCompanyName());
//                    ps.setString(15, parentDetails.getDesignation());
//                    ps.setString(  16, parentDetails.getCompanyAddress());
//                    ps.setString(17, CipherUtils.encrypt(parentDetails.getCompanyPhone()));
//                    ps.setString(18, parentDetails.getAddress());
//                    ps.setString(19, parentDetails.getCity());
//                    ps.setString(20, parentDetails.getState());
//                    ps.setInt(21, parentDetails.getZipcode());
//                    ps.setInt(22, parentDetails.getUpdatedBy());
//                    ps.setDate(23, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
//                    ps.setDate(24, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
//                }
//
//                @Override
//                public int getBatchSize() {
//                    return parentDetailsList.size();
//                }
//            });
//            // Check if all records are inserted successfully
//            if(!Arrays.stream(batchResult).allMatch(result -> result >= 0)){
//                throw new RuntimeException("Some parent records failed to insert");
//            }
//            return parentDetailsList;
//
//        }catch (DataAccessException e){
//            logger.error("Database error while adding parent details for the schoolCode: {}", schoolCode);
//            throw e;
//        }finally {
//            DatabaseUtil.closeDataSource(jdbcTemplate);
//        }
//    }

    @Override
    public List<ParentDetails> getAllParentDetails(String schoolCode) throws Exception {
        String sql = "select parent_id, uuid, school_id, first_name, last_name, dob, phone_number, emergency_phone_number, whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number, company_name, designation, company_address, company_phone, address, city, state, zipcode, updated_by, updated_date, create_date from parent_details where deleted is not true order by parent_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ParentDetails> parentDetails = null;
        try{
            parentDetails = jdbcTemplate.query(sql, new RowMapper<ParentDetails>() {
                @Override
                public ParentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ParentDetails pd = new ParentDetails();
                    pd.setParentId(rs.getInt("parent_id"));
                    pd.setUuid(rs.getString("uuid"));
                    pd.setSchoolId(rs.getInt("school_id"));
                    pd.setFirstName(rs.getString("first_name"));
                    pd.setLastName(rs.getString("last_name"));
                    pd.setDob(rs.getDate("dob"));
                    pd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    pd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    pd.setWhatsappNumber(rs.getString("whatsapp_no") != null ? CipherUtils.decrypt(rs.getString("whatsapp_no")) : null);
                    pd.setEmail(rs.getString("email_address"));
                    pd.setGender(rs.getString("gender"));
                    pd.setParentType(rs.getString("parent_type"));
                    pd.setQualification(rs.getString("qualification"));
                    pd.setAadharNumber(rs.getString("aadhar_number") !=null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    pd.setCompanyName(rs.getString("company_name"));
                    pd.setDesignation(rs.getString("designation"));
                    pd.setCompanyAddress(rs.getString("company_address"));
                    pd.setCompanyPhone(rs.getString("company_phone") != null ? CipherUtils.decrypt(rs.getString("company_phone")) : null);
                    pd.setAddress(rs.getString("address"));
                    pd.setCity(rs.getString("city"));
                    pd.setState(rs.getString("state"));
                    pd.setZipcode(rs.getInt("zipcode"));
                    pd.setUpdatedBy(rs.getInt("updated_by"));
                    pd.setUpdatedDate(rs.getDate("updated_date"));
                    pd.setCreateDate(rs.getDate("create_date"));
                    return pd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return parentDetails;
    }

    @Override
    public ParentDetails getParentDetailsById(int parentId, String schoolCode) {
        String sql = "select parent_id, uuid, school_id, first_name, last_name, dob, phone_number, emergency_phone_number, whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number, company_name, designation, company_address, company_phone, address, city, state, zipcode, updated_by, updated_date, create_date from parent_details where deleted is not true and parent_id = ? ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ParentDetails parentDetails = null;
        try{
            parentDetails = jdbcTemplate.queryForObject(sql, new Object[]{parentId}, new RowMapper<ParentDetails>() {
                @Override
                public ParentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ParentDetails pd = new ParentDetails();
                    pd.setParentId(rs.getInt("parent_id"));
                    pd.setUuid(rs.getString("uuid"));
                    pd.setSchoolId(rs.getInt("school_id"));
                    pd.setFirstName(rs.getString("first_name"));
                    pd.setLastName(rs.getString("last_name"));
                    pd.setDob(rs.getDate("dob"));
                    pd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    pd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    pd.setWhatsappNumber(rs.getString("whatsapp_no") != null ? CipherUtils.decrypt(rs.getString("whatsapp_no")) : null);
                    pd.setEmail(rs.getString("email_address"));
                    pd.setGender(rs.getString("gender"));
                    pd.setParentType(rs.getString("parent_type"));
                    pd.setQualification(rs.getString("qualification"));
                    pd.setAadharNumber(rs.getString("aadhar_number") != null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    pd.setCompanyName(rs.getString("company_name"));
                    pd.setDesignation(rs.getString("designation"));
                    pd.setCompanyAddress(rs.getString("company_address"));
                    pd.setCompanyPhone(rs.getString("company_phone") != null ? CipherUtils.decrypt(rs.getString("company_phone")) : null);
                    pd.setAddress(rs.getString("address"));
                    pd.setCity(rs.getString("city"));
                    pd.setState(rs.getString("state"));
                    pd.setZipcode(rs.getInt("zipcode"));
                    pd.setUpdatedBy(rs.getInt("updated_by"));
                    pd.setUpdatedDate(rs.getDate("updated_date"));
                    pd.setCreateDate(rs.getDate("create_date"));
                    return pd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return parentDetails;
    }

    @Override
    public List<ParentDetails> updateBulkParentDetailsById(List<ParentDetails> parentDetailsList, String schoolCode) throws Exception {
        if(parentDetailsList == null || parentDetailsList.isEmpty()){
            logger.warn("Parent details list is empty or null for the schoolCode: {}", schoolCode);
            throw new IllegalArgumentException("No records are found to update");
        }
        String sql = "update parent_details set first_name = ?, last_name = ?, dob = ?, phone_number = ?, emergency_phone_number = ?, whatsapp_no = ?, email_address = ?, gender = ?, parent_type = ?, qualification = ?, aadhar_number = ?, company_name = ?, designation = ?, company_address = ?, company_phone = ?, address = ?, city = ?, state = ?, zipcode = ?, updated_by = ?, updated_date = CURRENT_DATE where parent_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int batchResult[] = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ParentDetails parentDetails = parentDetailsList.get(i);
                    ps.setString(1, parentDetails.getFirstName());
                    ps.setString(2, parentDetails.getLastName());
                    ps.setDate(3, parentDetails.getDob() != null ? new java.sql.Date(parentDetails.getDob().getTime()) : null);
                    ps.setString(4, parentDetails.getPhoneNumber() != null ? CipherUtils.encrypt(parentDetails.getPhoneNumber()) : null);
                    ps.setString(5, parentDetails.getEmergencyPhoneNumber() != null ? CipherUtils.encrypt(parentDetails.getEmergencyPhoneNumber()) : null);
                    ps.setString(6, parentDetails.getWhatsappNumber() !=null ? CipherUtils.encrypt(parentDetails.getWhatsappNumber()) : null);
                    ps.setString(7, parentDetails.getEmail());
                    ps.setString(8, parentDetails.getGender());
                    ps.setString(9, parentDetails.getParentType());
                    ps.setString(10, parentDetails.getQualification());
                    ps.setString(11, parentDetails.getAadharNumber() != null ? CipherUtils.encrypt(parentDetails.getAadharNumber()) : null);
                    ps.setString(12, parentDetails.getCompanyName());
                    ps.setString(13, parentDetails.getDesignation());
                    ps.setString(  14, parentDetails.getCompanyAddress());
                    ps.setString(15, parentDetails.getCompanyPhone() != null ? CipherUtils.encrypt(parentDetails.getCompanyPhone()) : null);
                    ps.setString(16, parentDetails.getAddress());
                    ps.setString(17, parentDetails.getCity());
                    ps.setString(18, parentDetails.getState());
                    ps.setInt(19, parentDetails.getZipcode());
                    ps.setInt(20, parentDetails.getUpdatedBy());
                    ps.setInt(21, parentDetails.getParentId());
                }

                @Override
                public int getBatchSize() {
                    return parentDetailsList.size();
                }
            });
            // Check if all records are updated successfully
            if(!Arrays.stream(batchResult).allMatch(result -> result >= 0)){
                throw new RuntimeException("Error occurred while updating records");
            }
            return parentDetailsList;
        }catch (DataAccessException e){
            logger.error("Database error while updating parent details for schoolCode: {}", schoolCode, e);
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean softDeleteBulkParentDetails(List<Integer> parentIds, String schoolCode) throws Exception {
        if(parentIds == null || parentIds.isEmpty()){
            logger.warn("Parent ids are not found for the schoolCode: {}", schoolCode);
            throw new IllegalArgumentException("No records are found to update");
        }
        String sql = "UPDATE parent_details SET deleted = TRUE WHERE parent_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, parentIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return parentIds.size();
                }
            });
            // Check if all records are deleted successfully
            boolean success = Arrays.stream(batchUpdate).allMatch(result -> result == 1);
            if (success) {
                logger.info("Successfully deleted {} parent details for schoolCode: {}", parentIds.size(), schoolCode);
            } else {
                logger.warn("Some parent_ids were not deleted for schoolCode: {}", schoolCode);
            }
            return success;
        }catch (DataAccessException e){
            logger.error("Database error while deleting the parent for parentIds: {} for schoolCode: {}", parentIds, schoolCode, e);
            throw new RuntimeException("Bulk delete failed: " + e.getMessage(), e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<ParentDetails> getParentsByUuid(String uuid, String schoolCode) throws Exception {
        String sql = "select parent_id, uuid, school_id, first_name, last_name, dob, phone_number, emergency_phone_number, whatsapp_no, email_address, gender, parent_type, qualification, aadhar_number, company_name, designation, company_address, company_phone, address, city, state, zipcode, updated_by, updated_date, create_date from parent_details where deleted is not true and uuid = ? order by parent_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ParentDetails> parentDetails = null;
        try{
            parentDetails = jdbcTemplate.query(sql, new Object[]{uuid}, new RowMapper<ParentDetails>() {
                @Override
                public ParentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ParentDetails pd = new ParentDetails();
                    pd.setParentId(rs.getInt("parent_id"));
                    pd.setUuid(rs.getString("uuid"));
                    pd.setSchoolId(rs.getInt("school_id"));
                    pd.setFirstName(rs.getString("first_name"));
                    pd.setLastName(rs.getString("last_name"));
                    pd.setDob(rs.getDate("dob"));
                    pd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                    pd.setEmergencyPhoneNumber(rs.getString("emergency_phone_number") != null ? CipherUtils.decrypt(rs.getString("emergency_phone_number")) : null);
                    pd.setWhatsappNumber(rs.getString("whatsapp_no") != null ? CipherUtils.decrypt(rs.getString("whatsapp_no")) : null);
                    pd.setEmail(rs.getString("email_address"));
                    pd.setGender(rs.getString("gender"));
                    pd.setParentType(rs.getString("parent_type"));
                    pd.setQualification(rs.getString("qualification"));
                    pd.setAadharNumber(rs.getString("aadhar_number") !=null ? CipherUtils.decrypt(rs.getString("aadhar_number")) : null);
                    pd.setCompanyName(rs.getString("company_name"));
                    pd.setDesignation(rs.getString("designation"));
                    pd.setCompanyAddress(rs.getString("company_address"));
                    pd.setCompanyPhone(rs.getString("company_phone") != null ? CipherUtils.decrypt(rs.getString("company_phone")) : null);
                    pd.setAddress(rs.getString("address"));
                    pd.setCity(rs.getString("city"));
                    pd.setState(rs.getString("state"));
                    pd.setZipcode(rs.getInt("zipcode"));
                    pd.setUpdatedBy(rs.getInt("updated_by"));
                    pd.setUpdatedDate(rs.getDate("updated_date"));
                    pd.setCreateDate(rs.getDate("create_date"));
                    return pd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return parentDetails;
    }
}