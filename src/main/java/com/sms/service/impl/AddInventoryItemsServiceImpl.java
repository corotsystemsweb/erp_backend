package com.sms.service.impl;

import com.sms.dao.AddInventoryItemsDao;
import com.sms.model.AddInventoryItemsDetails;
import com.sms.service.AddInventoryItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddInventoryItemsServiceImpl implements AddInventoryItemsService {
    @Autowired
    private AddInventoryItemsDao addInventoryItemsDao;

    @Override
    public AddInventoryItemsDetails addItems(AddInventoryItemsDetails addItemsDetails, String schoolCode) throws Exception {
        return addInventoryItemsDao.addItems(addItemsDetails, schoolCode);
    }

    @Override
    public AddInventoryItemsDetails getItemsById(int addItemsId, String schoolCode) throws Exception {
        return addInventoryItemsDao.getItemsById(addItemsId, schoolCode);
    }

    @Override
    public List<AddInventoryItemsDetails> getAllItems(String schoolCode) throws Exception {
        return addInventoryItemsDao.getAllItems(schoolCode);
    }

    @Override
    public AddInventoryItemsDetails updateItems(AddInventoryItemsDetails addItemsDetails, int addItemsId, String schoolCode) throws Exception {
        return addInventoryItemsDao.updateItems(addItemsDetails, addItemsId, schoolCode);
    }

    @Override
    public boolean softDeleteItems(int addItemsId, String schoolCode) throws Exception {
        return addInventoryItemsDao.softDeleteItems(addItemsId, schoolCode);
    }
}
