package com.sms.service.impl;

import com.sms.dao.InventoryCategoryDao;
import com.sms.model.InventoryCategoryDetails;
import com.sms.service.InventoryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryCategoryServiceImpl implements InventoryCategoryService {
    @Autowired
    private InventoryCategoryDao inventoryCategoryDao;
    @Override
    public InventoryCategoryDetails addCategoryDetails(InventoryCategoryDetails inventoryCategoryDetails, String schoolCode) throws Exception {
        return inventoryCategoryDao.addCategoryDetails(inventoryCategoryDetails, schoolCode);
    }
    @Override
    public InventoryCategoryDetails getCategoryDetailsById(int inventoryCategoryId, String schoolCode) throws Exception {
        return inventoryCategoryDao.getCategoryDetailsById(inventoryCategoryId,schoolCode);
    }

    @Override
    public List<InventoryCategoryDetails> getAllInventoryCategory(String schoolCode) throws Exception {
        return inventoryCategoryDao.getAllInventoryCategory(schoolCode);
    }

    @Override
    public InventoryCategoryDetails updateInventoryCategoryById(InventoryCategoryDetails inventoryCategoryDetails, int inventoryCategoryId, String schoolCode) throws Exception {
        return inventoryCategoryDao.updateInventoryCategoryById(inventoryCategoryDetails,inventoryCategoryId,schoolCode);
    }

    @Override
    public boolean deleteInventoryCategoryDetails(int inventoryCategoryId, String schoolCode) throws Exception {
        return inventoryCategoryDao.deleteInventoryCategoryDetails(inventoryCategoryId,schoolCode);
    }
}
