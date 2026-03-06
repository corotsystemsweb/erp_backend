package com.sms.dao.impl;

import com.sms.dao.AddInventoryItemsDao;
import com.sms.model.AddInventoryItemsDetails;
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
public class AddInventoryItemsDaoImpl implements AddInventoryItemsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddInventoryItemsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AddInventoryItemsDetails addItems(AddInventoryItemsDetails addItemsDetails, String schoolCode) throws Exception {
        String sql = "insert into add_inventory_items(school_id, session_id, supplier_id, inventory_category_id, item_name, description, publish, cost_price, sale_price, stock, updated_by, update_date_time) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,addItemsDetails.getSchoolId());
                ps.setInt(2, addItemsDetails.getSessionId());
                ps.setInt(3, addItemsDetails.getSupplierId());
                ps.setInt(4, addItemsDetails.getInventoryCategoryId());
                ps.setString(5, addItemsDetails.getItemName());
                ps.setString(6, addItemsDetails.getDescription());
                ps.setString(7, addItemsDetails.getPublish());
                ps.setDouble(8, addItemsDetails.getCostPrice());
                ps.setDouble(9, addItemsDetails.getSalePrice());
                ps.setInt(10, addItemsDetails.getStock());
                ps.setInt(11, addItemsDetails.getUpdated_by());
                ps.setTimestamp(12, addItemsDetails.getUpdateDateTime());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("add_inventory_items_id")){
                int generatedId = ((Number)keys.get("add_inventory_items_id")).intValue();
                addItemsDetails.setAddInventoryItemsId(generatedId);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addItemsDetails;
    }

    @Override
    public AddInventoryItemsDetails getItemsById(int addItemsId, String schoolCode) throws Exception {
        String sql = "SELECT  aii.add_inventory_items_id,ads.supplier_id, ads.supplier_name, ads.mobile_number,  ads.email,  ic.inventory_category_id, ic.category_details, ic.category_description,  aii.item_name, aii.description, aii.publish,aii.cost_price, aii.sale_price,  aii.stock FROM add_inventory_items aii JOIN add_supplier ads ON aii.supplier_id = ads.supplier_id JOIN  inventory_category ic ON aii.inventory_category_id = ic.inventory_category_id WHERE aii.add_inventory_items_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        AddInventoryItemsDetails addInventoryItemsDetails = null;
        try{
            addInventoryItemsDetails = jdbcTemplate.queryForObject(sql, new Object[]{addItemsId}, new RowMapper<AddInventoryItemsDetails>() {
                @Override
                public AddInventoryItemsDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddInventoryItemsDetails aiid = new AddInventoryItemsDetails();
                    aiid.setAddInventoryItemsId(rs.getInt("add_inventory_items_id"));
                    aiid.setSupplierId(rs.getInt("supplier_id"));
                    aiid.setSupplierName(rs.getString("supplier_name"));
                    aiid.setMobileNumber(rs.getString("mobile_number"));
                    aiid.setEmail(rs.getString("email"));
                    aiid.setInventoryCategoryId(rs.getInt("inventory_category_id"));
                    aiid.setCategoryDetails(rs.getString("category_details"));
                    aiid.setCategoryDescription(rs.getString("category_description"));
                    aiid.setItemName(rs.getString("item_name"));
                    aiid.setDescription(rs.getString("description"));
                    aiid.setPublish(rs.getString("publish"));
                    aiid.setCostPrice(rs.getDouble("cost_price"));
                    aiid.setSalePrice(rs.getDouble("sale_price"));
                    aiid.setStock(rs.getInt("stock"));
                    return aiid;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addInventoryItemsDetails;
    }

    @Override
    public List<AddInventoryItemsDetails> getAllItems(String schoolCode) throws Exception {
        String sql = "SELECT  aii.add_inventory_items_id,ads.supplier_id, ads.supplier_name, ads.mobile_number,  ads.email,  ic.inventory_category_id, ic.category_details, ic.category_description,  aii.item_name, aii.description, aii.publish,aii.cost_price, aii.sale_price,  aii.stock FROM add_inventory_items aii JOIN add_supplier ads ON aii.supplier_id = ads.supplier_id JOIN  inventory_category ic ON aii.inventory_category_id = ic.inventory_category_id where aii.deleted is not true order by aii.add_inventory_items_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddInventoryItemsDetails> addInventoryItemsDetails = null;
        try{
            addInventoryItemsDetails = jdbcTemplate.query(sql, new RowMapper<AddInventoryItemsDetails>() {
                @Override
                public AddInventoryItemsDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddInventoryItemsDetails aiid = new AddInventoryItemsDetails();
                    aiid.setAddInventoryItemsId(rs.getInt("add_inventory_items_id"));
                    aiid.setSupplierId(rs.getInt("supplier_id"));
                    aiid.setSupplierName(rs.getString("supplier_name"));
                    aiid.setMobileNumber(rs.getString("mobile_number"));
                    aiid.setEmail(rs.getString("email"));
                    aiid.setInventoryCategoryId(rs.getInt("inventory_category_id"));
                    aiid.setCategoryDetails(rs.getString("category_details"));
                    aiid.setCategoryDescription(rs.getString("category_description"));
                    aiid.setItemName(rs.getString("item_name"));
                    aiid.setDescription(rs.getString("description"));
                    aiid.setPublish(rs.getString("publish"));
                    aiid.setCostPrice(rs.getDouble("cost_price"));
                    aiid.setSalePrice(rs.getDouble("sale_price"));
                    aiid.setStock(rs.getInt("stock"));
                    return aiid;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addInventoryItemsDetails;
    }

    @Override
    public AddInventoryItemsDetails updateItems(AddInventoryItemsDetails addItemsDetails, int addItemsId, String schoolCode) throws Exception {
        String sql = "UPDATE add_inventory_items SET supplier_id = ?, inventory_category_id = ?, item_name = ?, description = ?, publish = ?, cost_price = ?, sale_price = ?, stock = ?, updated_by = ?, update_date_time = ? where add_inventory_items_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql,
                    addItemsDetails.getSupplierId(),
                    addItemsDetails.getInventoryCategoryId(),
                    addItemsDetails.getItemName(),
                    addItemsDetails.getDescription(),
                    addItemsDetails.getPublish(),
                    addItemsDetails.getCostPrice(),
                    addItemsDetails.getSalePrice(),
                    addItemsDetails.getStock(),
                    addItemsDetails.getUpdated_by(),
                    addItemsDetails.getUpdateDateTime(),
                    addItemsId
            );
            if(rowAffected > 0){
                return addItemsDetails;
            }else {
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
    public boolean softDeleteItems(int addItemsId, String schoolCode) throws Exception {
        String sql = "UPDATE add_inventory_items SET deleted = TRUE WHERE add_inventory_items_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, addItemsId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
