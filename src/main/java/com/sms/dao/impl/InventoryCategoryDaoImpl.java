package com.sms.dao.impl;

import com.sms.dao.InventoryCategoryDao;
import com.sms.model.BookCategroyDetails;
import com.sms.model.InventoryCategoryDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class InventoryCategoryDaoImpl implements InventoryCategoryDao {
    private final JdbcTemplate jdbcTemplate;
@Autowired
    public InventoryCategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public InventoryCategoryDetails addCategoryDetails(InventoryCategoryDetails inventoryCategoryDetails, String schoolCode) throws Exception {
        String sql="insert into inventory_category(school_id, session_id, category_details, category_description, updated_by, update_date_time) values(?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,inventoryCategoryDetails.getSchoolId());
                ps.setInt(2,inventoryCategoryDetails.getSessionId());
                ps.setString(3,inventoryCategoryDetails.getCategoryDetails());
                ps.setString(4,inventoryCategoryDetails.getCategoryDescription());
                ps.setInt(5,inventoryCategoryDetails.getUpdatedBy());
                ps.setTimestamp(6,inventoryCategoryDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("inventory_category_id")){
                int generatedId=((Number) keys.get("inventory_category_id")).intValue();
                inventoryCategoryDetails.setInventoryCategoryId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return inventoryCategoryDetails ;
    }

    @Override
    public InventoryCategoryDetails getCategoryDetailsById(int inventoryCategoryId, String schoolCode) throws Exception {
        String sql = "SELECT inventory_category_id,category_details,category_description FROM inventory_category WHERE inventory_category_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        InventoryCategoryDetails inventoryCategoryDetails=null;
        try{
            inventoryCategoryDetails = jdbcTemplate.queryForObject(sql, new Object[]{inventoryCategoryId}, new RowMapper<InventoryCategoryDetails>() {
                @Override
                public InventoryCategoryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    InventoryCategoryDetails icd = new InventoryCategoryDetails();
                    icd.setInventoryCategoryId(rs.getInt("inventory_category_id"));
                    icd.setCategoryDetails(rs.getString("category_details"));
                    icd.setCategoryDescription(rs.getString("category_description"));
                    return icd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return inventoryCategoryDetails;
    }

    @Override
    public List<InventoryCategoryDetails> getAllInventoryCategory(String schoolCode) throws Exception {
        String sql = "SELECT inventory_category_id,category_details,category_description FROM inventory_category";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<InventoryCategoryDetails> inventoryCategoryDetails=null;
        try{
            inventoryCategoryDetails = jdbcTemplate.query(sql,new RowMapper<InventoryCategoryDetails>() {
                @Override
                public InventoryCategoryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    InventoryCategoryDetails icd = new InventoryCategoryDetails();
                    icd.setInventoryCategoryId(rs.getInt("inventory_category_id"));
                    icd.setCategoryDetails(rs.getString("category_details"));
                    icd.setCategoryDescription(rs.getString("category_description"));
                    return icd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return inventoryCategoryDetails;
    }

    @Override
    public InventoryCategoryDetails updateInventoryCategoryById(InventoryCategoryDetails inventoryCategoryDetails, int inventoryCategoryId, String schoolCode) throws Exception {
        String sql = "UPDATE inventory_category SET category_details = ?, category_description = ?, updated_by = ?, update_date_time = ? WHERE inventory_category_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    inventoryCategoryDetails.getCategoryDetails(),
                    inventoryCategoryDetails.getCategoryDescription(),
                    inventoryCategoryDetails.getUpdatedBy(),
                    inventoryCategoryDetails.getUpdateDateTime(),
                    inventoryCategoryId);
            if (rowEffected > 0) {
                return inventoryCategoryDetails;
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
    public boolean deleteInventoryCategoryDetails(int inventoryCategoryId,String schoolCode) throws Exception {
        String sql = "delete from inventory_category where inventory_category_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{inventoryCategoryId});
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
}
