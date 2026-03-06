package com.sms.dao.impl;
import com.sms.dao.AddBookDao;
import com.sms.dao.AddSupplierDao;
import com.sms.model.AddBookDetails;
import com.sms.model.AddSupplierDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class AddSupplierDaoImpl implements AddSupplierDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AddSupplierDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AddSupplierDetails addSupplierDetails(AddSupplierDetails addSupplierDetails, String schoolCode) throws Exception {
        String sql="insert into add_supplier(school_id, session_id, supplier_name, mobile_number, email, address, gstin, pan,city, state, pin_code, website, bank_name, branch_name, account_number, ifsc_code,updated_by, update_date_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,addSupplierDetails.getSchoolId());
                ps.setInt(2,addSupplierDetails.getSessionId());
                ps.setString(3,addSupplierDetails.getSupplierName());
                ps.setString(4,addSupplierDetails.getMobileNumber());
                ps.setString(5,addSupplierDetails.getEmail());
                ps.setString(6,addSupplierDetails.getAddress());
                ps.setString(7,addSupplierDetails.getGstin());
                ps.setString(8,addSupplierDetails.getPan());
                ps.setString(9,addSupplierDetails.getCity());
                ps.setString(10,addSupplierDetails.getState());
                ps.setString(11,addSupplierDetails.getPinCode());
                ps.setString(12,addSupplierDetails.getWebsite());
                ps.setString(13,addSupplierDetails.getBankName());
                ps.setString(14,addSupplierDetails.getBranchName());
                ps.setString(15,addSupplierDetails.getAccountNumber());
                ps.setString(16,addSupplierDetails.getIfscCode());
                ps.setInt(17,addSupplierDetails.getUpdatedBy());
                ps.setTimestamp(18,addSupplierDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("supplier_id")){
                int generatedId=((Number) keys.get("supplier_id")).intValue();
                addSupplierDetails.setSupplierId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addSupplierDetails;
    }

    @Override
    public AddSupplierDetails getSupplierDetailsById(int supplierId, String schoolCode) throws Exception {
        String sql = "SELECT supplier_id,supplier_name, mobile_number, email, gstin, pan, address, website from add_supplier where supplier_id=? AND deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        AddSupplierDetails addSupplierDetails=null;
        try{
            addSupplierDetails = jdbcTemplate.queryForObject(sql, new Object[]{supplierId}, new RowMapper<AddSupplierDetails>() {
                @Override
                public AddSupplierDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddSupplierDetails as = new AddSupplierDetails();
                    as.setSupplierId(rs.getInt("supplier_id"));
                    as.setSupplierName(rs.getString("supplier_name"));
                    as.setMobileNumber(rs.getString("mobile_number"));
                    as.setEmail(rs.getString("email"));
                    as.setGstin(rs.getString("gstin"));
                    as.setPan(rs.getString("pan"));
                    as.setAddress(rs.getString("address"));
                    as.setWebsite(rs.getString("website"));
                    return as;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addSupplierDetails;
    }

    @Override
    public List<AddSupplierDetails> getSupplierDetails(String schoolCode) throws Exception {
        String sql = "SELECT supplier_id,supplier_name, mobile_number, email, gstin, pan, address, account_number,website from add_supplier WHERE deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddSupplierDetails> addSupplierDetails=null;
        try{
            addSupplierDetails = jdbcTemplate.query(sql, new RowMapper<AddSupplierDetails>() {
                @Override
                public AddSupplierDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddSupplierDetails as = new AddSupplierDetails();
                    as.setSupplierId(rs.getInt("supplier_id"));
                    as.setSupplierName(rs.getString("supplier_name"));
                    as.setMobileNumber(rs.getString("mobile_number"));
                    as.setEmail(rs.getString("email"));
                    as.setGstin(rs.getString("gstin"));
                    as.setPan(rs.getString("pan"));
                    as.setAddress(rs.getString("address"));
                    as.setAccountNumber(rs.getString("account_number"));
                    as.setWebsite(rs.getString("website"));
                    return as;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return addSupplierDetails;
    }

    @Override
    public AddSupplierDetails updateSupplierDetails(AddSupplierDetails addSupplierDetails, int supplierId, String schoolCode) throws Exception {
        String sql = "UPDATE add_supplier SET supplier_name=?,mobile_number = ?, email = ?, address = ?, gstin = ?, pan=?, city = ?, state=? , pin_code=? , website=?, bank_name=?, branch_name=?, account_number=?, ifsc_code=?, updated_by = ?, update_date_time = ? WHERE supplier_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    addSupplierDetails.getSupplierName(),
                    addSupplierDetails.getMobileNumber(),
                    addSupplierDetails.getEmail(),
                    addSupplierDetails.getAddress(),
                    addSupplierDetails.getGstin(),
                    addSupplierDetails.getPan(),
                    addSupplierDetails.getCity(),
                    addSupplierDetails.getState(),
                    addSupplierDetails.getPinCode(),
                    addSupplierDetails.getWebsite(),
                    addSupplierDetails.getBankName(),
                    addSupplierDetails.getBranchName(),
                    addSupplierDetails.getAccountNumber(),
                    addSupplierDetails.getIfscCode(),
                    addSupplierDetails.getUpdatedBy(),
                    addSupplierDetails.getUpdateDateTime(),
                    supplierId);
            if (rowEffected > 0) {
                return addSupplierDetails;
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
    public boolean softDeleteSupplierDetails(int supplierId, String schoolCode) throws Exception {
        String sql = "UPDATE add_supplier SET deleted = TRUE WHERE supplier_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql,supplierId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
