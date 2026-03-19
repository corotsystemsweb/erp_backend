package com.sms.dao.impl;

import com.sms.dao.BankDetailsDao;
import com.sms.model.BankDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class BankDetailsDaoImpl implements BankDetailsDao {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public BankDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BankDetails addBankDetails(BankDetails bankDetails, String schoolCode) throws Exception {

        String sql = "insert into add_bank_details (school_id,staff_id, staff_name, phone_number, bank_name, account_number,ifsc_code, permanent_account_number, branch_name, bank_address, updated_by, update_date_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,bankDetails.getSchoolId());
                ps.setInt(2,bankDetails.getStaffId());
                ps.setString(3, bankDetails.getStaffName());
                ps.setString(4, bankDetails.getPhoneNumber());
                ps.setString(5, bankDetails.getBankName());
                ps.setString(6, bankDetails.getAccountNumber());
                ps.setString(7, bankDetails.getIfscCode());
                ps.setString(8, bankDetails.getPermanentAccountNumber());
                ps.setString(9, bankDetails.getBranchName());
               ps.setString(10, bankDetails.getBankAddress());
               ps.setInt(11,bankDetails.getUpdatedBy());
               ps.setDate(12, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("bd_id")){
                int generatedKey = ((Number) keys.get("bd_id")).intValue();
                bankDetails.setBdId(generatedKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bankDetails;
    }

    @Override
    public List<BankDetails> getAllStaffBankDetails(String schoolCode) throws Exception {
        String sql="Select bd_id,school_id,staff_id,staff_name,phone_number,bank_name,account_number,ifsc_code,permanent_account_number,branch_name,bank_address from add_bank_details WHERE deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<BankDetails>bankDetails=null;
        try {
            bankDetails = jdbcTemplate.query(sql, new RowMapper<BankDetails>() {
                @Override
                public BankDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BankDetails bd = new BankDetails();
                    bd.setBdId(rs.getInt("bd_id"));
                    bd.setSchoolId(rs.getInt("school_id"));
                    bd.setStaffId(rs.getInt("staff_id"));
                    bd.setStaffName(rs.getString("staff_name"));
                    bd.setPhoneNumber(rs.getString("phone_number"));
                    bd.setBankName(rs.getString("bank_name"));
                    bd.setAccountNumber(rs.getString("account_number"));
                    bd.setIfscCode(rs.getString("ifsc_code"));
                    bd.setPermanentAccountNumber(rs.getString("permanent_account_number"));
                    bd.setBranchName(rs.getString("branch_name"));
                    bd.setBankAddress(rs.getString("bank_address"));
                    return bd;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bankDetails;
    }

    @Override
    public BankDetails getBankDetailsById(int bdId, String schoolCode) throws Exception {
        String sql = "Select bd_id,school_id,staff_id,staff_name,phone_number,bank_name,account_number,ifsc_code,permanent_account_number,branch_name,bank_address from add_bank_details where bd_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        BankDetails bankDetails = null;
        try {
            bankDetails = jdbcTemplate.queryForObject(sql, new Object[]{bdId}, new RowMapper<BankDetails>() {
                @Override
                public BankDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BankDetails bd = new BankDetails();
                    bd.setBdId(rs.getInt("bd_id"));
                    bd.setSchoolId(rs.getInt("school_id"));
                    bd.setStaffId(rs.getInt("staff_id"));
                    bd.setStaffName(rs.getString("staff_name"));
                    bd.setPhoneNumber(rs.getString("phone_number"));
                    bd.setBankName(rs.getString("bank_name"));
                    bd.setAccountNumber(rs.getString("account_number"));
                    bd.setIfscCode(rs.getString("ifsc_code"));
                    bd.setPermanentAccountNumber(rs.getString("permanent_account_number"));
                    bd.setBranchName(rs.getString("branch_name"));
                    bd.setBankAddress(rs.getString("bank_address"));
                    return bd;
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bankDetails;
    }



    @Override
    public BankDetails updateByEmpId(BankDetails bankDetails, int bdId, String schoolCode) throws Exception {
        String sql = "UPDATE add_bank_details SET staff_id=?, staff_name=?, phone_number=?, bank_name=?, account_number=?, ifsc_code=?, permanent_account_number=?, branch_name=?, bank_address=?, updated_by=?, update_date_time=? WHERE bd_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            String encryptedPhone = bankDetails.getPhoneNumber() != null ? CipherUtils.encrypt(bankDetails.getPhoneNumber()) : null;
            int rowAffected = jdbcTemplate.update(sql,
                bankDetails.getStaffId(),
                bankDetails.getStaffName(),
                    encryptedPhone,
                bankDetails.getBankName(),
                bankDetails.getAccountNumber(),
                bankDetails.getIfscCode(),
                bankDetails.getPermanentAccountNumber(),
                bankDetails.getBranchName(),
                bankDetails.getBankAddress(),
                bankDetails.getUpdatedBy(),
                bankDetails.getUpdateDateTime(),
                bdId);
        if (rowAffected > 0) {
            return bankDetails;
        } else {
            return null;
        }
    } catch(Exception e){
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }

    @Override
    public boolean softDeleteBankDetails(int bdId, String schoolCode) throws Exception {
        String sql = "UPDATE add_bank_details SET deleted = TRUE WHERE bd_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        int update = jdbcTemplate.update(sql, bdId);
        return update > 0;
    } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }

    @Override
    public List<BankDetails> getBankDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "Select staff_id,staff_name,bank_name,account_number,branch_name,ifsc_code,permanent_account_number from add_bank_details WHERE CONCAT_WS(' ', staff_id,staff_name,bank_name,account_number,branch_name,ifsc_code,permanent_account_number) ILIKE ? AND deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<BankDetails> bankDetails = null;
        try{
            bankDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<BankDetails>() {
                @Override
                public BankDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BankDetails bd = new BankDetails();
                    bd.setStaffId(rs.getInt("staff_id"));
                    bd.setStaffName(rs.getString("staff_name"));
                    bd.setBankName(rs.getString("bank_name"));
                    bd.setAccountNumber(rs.getString("account_number"));
                    bd.setBranchName(rs.getString("branch_name"));
                    bd.setIfscCode(rs.getString("ifsc_code"));
                    bd.setPermanentAccountNumber(rs.getString("permanent_account_number"));
                    return bd;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bankDetails;
    }


   /* @Override
    public boolean deleteByEmpId(int employeeId) throws Exception {

        String sql = "DELETE from add_bank_details where employeeId= ?";
        int rowAffected = jdbcTemplate.update(sql, new Object[]{employeeId});
        if(rowAffected > 0){
            return true;
        }else{
            return false;
        }
    }*/
   @Override
   public BankDetails getBankDetailsByStaffId(int staffId, String schoolCode) throws Exception {

       String sql = """
        SELECT bd_id, school_id, staff_id, staff_name, phone_number, bank_name, account_number, ifsc_code,
        permanent_account_number, branch_name, bank_address
        FROM add_bank_details
        WHERE staff_id = ?
        AND deleted IS NOT TRUE
    """;

       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

       try {
           return jdbcTemplate.queryForObject(sql, new Object[]{staffId}, (rs, rowNum) -> {
                       BankDetails bd = new BankDetails();
                       bd.setBdId(rs.getInt("bd_id"));
                       bd.setSchoolId(rs.getInt("school_id"));
                       bd.setStaffId(rs.getInt("staff_id"));
                       bd.setStaffName(rs.getString("staff_name"));
                       bd.setPhoneNumber(rs.getString("phone_number") != null ? CipherUtils.decrypt(rs.getString("phone_number")) : null);
                       bd.setBankName(rs.getString("bank_name"));
                       bd.setAccountNumber(rs.getString("account_number"));
                       bd.setIfscCode(rs.getString("ifsc_code"));
                       bd.setPermanentAccountNumber(rs.getString("permanent_account_number"));
                       bd.setBranchName(rs.getString("branch_name"));
                       bd.setBankAddress(rs.getString("bank_address"));
                       return bd;
                   });

       } catch (Exception e) {
           return null;
       } finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

}
