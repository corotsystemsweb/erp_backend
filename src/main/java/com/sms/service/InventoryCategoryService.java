package com.sms.service;

import com.sms.model.InventoryCategoryDetails;

import java.util.List;

public interface InventoryCategoryService {
    public InventoryCategoryDetails addCategoryDetails(InventoryCategoryDetails inventoryCategoryDetails, String schoolCode) throws Exception;
    public InventoryCategoryDetails getCategoryDetailsById(int inventoryCategoryId, String schoolCode) throws Exception;
    public List<InventoryCategoryDetails> getAllInventoryCategory(String schoolCode) throws  Exception;
    public InventoryCategoryDetails updateInventoryCategoryById(InventoryCategoryDetails inventoryCategoryDetails,int inventoryCategoryId,String schoolCode) throws Exception;
    public boolean deleteInventoryCategoryDetails(int inventoryCategoryId, String schoolCode) throws Exception;
}
